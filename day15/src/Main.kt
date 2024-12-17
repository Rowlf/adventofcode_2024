// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 15

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
""",
        """
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
""".trimIndent(),
        """
#######
#...#.#
#.....#
#..OO@#
#..O..#
#.....#
#######

<vv<<^^<<^^
""".trimIndent()
    )

    val example = 0
    val inputData = if (example == 0) linesOf(day = day) else linesOf(data = examples[example - 1])

    val (warehouseLoaded, moves) = inputData.toBlocks().let { it[0].toField() to it[1].joinToString(separator = "") }
    warehouseLoaded.print(indent = 2, description = "loaded warehouse:", separator = "")
    println("moves: $moves\n")

    fun Field<Char>.print(robot: Position, description: String = "") {
        val c = this[robot]
        this[robot] = '@'
        this.print(indent = 2, description = description, separator = "")
        this[robot] = c
    }

    fun sumGpsPositions(isExtended: Boolean): Long {
        val warehouseStart = if (!isExtended) warehouseLoaded.copy() else Field<Char>().apply {
            val repl = mapOf('#' to "##", '.' to "..", 'O' to "[]", '@' to "@.")
            this.addAll((0..<warehouseLoaded.rows).map { row ->
                (0..<warehouseLoaded.cols).flatMap { col ->
                    repl[warehouseLoaded[row, col]].let { listOf(it!![0], it[1]) }
                }
            })
        }
        var startPos = warehouseStart.positions().find { warehouseStart[it] == '@' }!!
        warehouseStart[startPos] = '.'

        // warehouseStart.print(startPos)
        // println("start position: $startPos\n")

        var ware = warehouseStart.copy()
        var pos = startPos

        fun affectedSimpleBoxes(from: Position, dir: Field.LineDirection) =
            mutableListOf<MutableSet<Position>>().apply {
                var pos = from
                while (ware[pos] == 'O') {
                    add(mutableSetOf(pos)); pos += dir
                }
                if (ware[pos] == '#') {
                    clear()
                }
            }

        fun affectedHorizontalDoubleBoxes(from: Position, dir: Field.LineDirection) =
            mutableListOf<MutableSet<Position>>().apply {
                var pos = from
                while (ware[pos] == '[' || ware[pos] == ']') {  // @[] or []@
                    add(mutableSetOf(pos)); pos += dir
                    add(mutableSetOf(pos)); pos += dir
                }
                if (ware[pos] == '#') {
                    clear()
                }
            }

        fun affectedVerticalDoubleBoxes(from: Position, dir: Field.LineDirection) =
            mutableListOf<MutableSet<Position>>().apply {
                var pos = from
                var c = ware[pos]
                // starter
                if (c == ']') {
                    add(mutableSetOf(pos + Field.LineDirection.LEFT, pos))
                } else {
                    add(mutableSetOf(pos, pos + Field.LineDirection.RIGHT))
                }

                // collect affected boxes
                while (true) {
                    val lastBoxes = this.last()
                    val newBoxes = mutableSetOf<Position>()
                    for (box in lastBoxes) {
                        val nextPos = box + dir
                        val nc = ware[nextPos]
                        if (nc == '[' || nc == ']') {
                            newBoxes.add(nextPos)
                            newBoxes.add(box + dir + (if (nc == '[') Field.LineDirection.RIGHT else Field.LineDirection.LEFT))
                        }
                    }
                    if (newBoxes.isEmpty()) break
                    else this.add(newBoxes)
                }
            }

        moves.forEach { move ->
            val dir = move.toCrossDirection()
            val c = ware[pos + dir]
            when (c) {
                '#' -> {}
                '.' -> pos += dir
                'O', '[', ']' -> {
                    var affected = when {
                        c == 'O' -> affectedSimpleBoxes(pos + dir, dir)
                        dir.isHorizontal -> affectedHorizontalDoubleBoxes(pos + dir, dir)
                        dir.isVertical -> affectedVerticalDoubleBoxes(pos + dir, dir)
                        else -> mutableListOf()
                    }

                    if (affected.isNotEmpty()) {
                        var fieldCopy = ware.copy()

                        // copy fields if free
                        var allFree = true
                        while (affected.isNotEmpty() && allFree) {
                            val last = affected.removeLast()
                            if (!last.all { ware[it + dir] == '.' })
                                allFree = false
                            else
                                last.forEach { ware[it + dir] = ware[it]; ware[it] = '.' }
                        }
                        if (!allFree)
                            ware = fieldCopy
                        else
                            pos += dir
                    }
                }
            }
        }

        return ware.positions().sumOf { pos ->
            if (ware[pos] == 'O' || ware[pos] == '[') pos.row * 100L + pos.col * 1L else 0L
        }
    }

    // part 1: solutions: - / 1415498

    timeResult { // [M3 20.646875ms]
        sumGpsPositions(isExtended = false)
    }.let { (dt, result) -> println("[part 1] result: $result, dt: $dt (boxes)") }

    // part 2: solutions: - / 1432898

    timeResult { // [M3 18.621625ms]
        sumGpsPositions(isExtended = true)
    }.let { (dt, result) -> println("[part 2] result: $result, dt: $dt (larger boxes)") }
}
