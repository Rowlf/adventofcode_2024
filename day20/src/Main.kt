// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 20

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
""",
)

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val grid = inputData.toGrid()
    val startPos = grid.positions.find { grid[it] == 'S' }!!.also { grid[it] = '.' }
    val endPos = grid.positions.find { grid[it] == 'E' }!!.also { grid[it] = '.' }

    val graph = grid.toGraph()
    val startPaths = graph.findShortestPaths(startPos)  // reachable from start
    val endPaths = graph.findShortestPaths(endPos)      // end reachable from here
    val fairDist = graph.minimalSteps(startPos,endPos)  // Benchmark if played fair

    fun cheats(cutOff: Int, worthIt: Int): Int {
        val total = mutableMapOf<Int,Int>()
        grid.positions.filter { it in startPaths.distances }.forEach { pos ->
            pos.walk(RCDir.Square(cutOff))
                .filter { it.dir.norm1 <= cutOff && it.pos in endPaths.distances } // implicitly: it.pos in grid
                .forEach { (cheatPos,dir) ->
                    val cheated = fairDist - (startPaths.distances[pos]!!.toInt() + dir.norm1 + endPaths.distances[cheatPos]!!.toInt())
                    if (cheated >= worthIt) {
                        total[cheated] = total.getOrDefault(cheated, 0).toInt() + 1
                    }
                }
        }
        return total.values.sumOf { it }
    }

    // part 1: solutions: 44 / 1346

    timeResult { // [M3 40.250916ms]
        cheats(2,if (example==0) 100 else 0)
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (...)") }

    // part 2: solutions: 285 / 985482

    timeResult { // [M3 784.168ms]
        cheats(20,if (example==0) 100 else 50)
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (...)") }
}
