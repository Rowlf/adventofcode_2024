// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 21

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
029A
980A
179A
456A
379A
""",
    )

    val example = 0
    val inputData = if (example == 0) linesOf(day = day) else linesOf(data = examples[example - 1])

    inputData.print(indent = 2, description = "input lines:", take = 2)
    val codes = inputData.map { it to it.substringBeforeLast('A').toLong() }

    val moveMap = mapOf(
        RCDir.Up.asPos to '^',
        RCDir.Down.asPos to 'v',
        RCDir.Left.asPos to '<',
        RCDir.Right.asPos to '>',
    )

    val numPad = object {
        val layout = """
            789
            456
            123
            #0A
        """.trimIndent()
        val lines = linesOf(data = layout)
        val chars = lines.joinToString("").replace("#", "")
        val grid = lines.toGrid()
        val pos = grid.positions.filter { grid[it] in chars }.associateBy { grid[it] }
        val graph = grid.toGraph(chars)
        val paths = chars.associateWith { c1 ->
            chars.associateWith { c2 ->
                graph.findAllShortestPaths(pos[c1]!!, pos[c2]!!).map { list ->
                    list.zipWithNext { a, b -> moveMap[(b - a)]!! }.joinToString("")
                }
            }
        }
    }

    val dirPad = object {
        val layout = """
            #^A
            <v>
        """.trimIndent()
        val lines = linesOf(data = layout)
        val chars = lines.joinToString("").replace("#", "")
        val grid = lines.toGrid()
        val pos = grid.positions.filter { grid[it] in chars }.associateBy { grid[it] }
        val graph = grid.toGraph(chars)
        val paths = chars.associateWith { c1 ->
            chars.associateWith { c2 ->
                graph.findAllShortestPaths(pos[c1]!!, pos[c2]!!).map { list ->
                    list.zipWithNext { a, b -> moveMap[(b - a)]!! }.joinToString("")
                }
            }
        }
    }

    val allCodeLengths: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

    fun calcLength(code: String, level: Int, pad: Map<Char, Map<Char, List<String>>>): Long
    = allCodeLengths.getOrPut(code to level) {
        if (level == 0) code.length.toLong() else
            "A$code".zipWithNext().sumOf { (start, end) ->
                pad[start]!![end]!!.minOf { path -> calcLength("${path}A", level - 1, dirPad.paths) }
            }
        }

    // part 1: solutions: 126384 / 152942

    timeResult { // [M3 529.209us]
        codes.sumOf { (code, num) -> calcLength(code, 2 + 1, numPad.paths) * num }
    }.let { (dt, result) -> println("[part 1] result: $result, dt: $dt (...)") }

    // part 2: solutions: . / 189235298434780

    timeResult { // [M3 1.351166ms]
        codes.sumOf { (code, num) -> calcLength(code, 25 + 1, numPad.paths) * num }
    }.let { (dt, result) -> println("[part 2] result: $result, dt: $dt (...)") }
}
