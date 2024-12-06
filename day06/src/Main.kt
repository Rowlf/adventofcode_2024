// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import Field.LineDirection

const val day = 6

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
"""

    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 2)
    val theLab = inputData.toField()

    var (guard, facing) = mapOf( // we do not know the orientation (it is always UP)
        '^' to LineDirection.UP, 'v' to LineDirection.DOWN,
        '>' to LineDirection.LEFT, '<' to LineDirection.RIGHT
    ).let { orientations ->
        theLab.positions().first { pos -> theLab[pos] in orientations.keys }.let { pos ->
            pos to orientations[theLab[pos]]!! }
    }

    // remember the way but also check for spaces been before, but only in the same direction, so crossing is allowed
    fun MutableMap<Position,LineDirection>.fillAndCheckWay(positions: Iterable<Position>, dir:LineDirection): Boolean {
        positions.forEach { pos ->
            if (this[pos] == dir) return true
            this[pos] = dir
        }
        return false
    }

    // patrol the lab (all these seek and check feels a little quirky...)
    fun Field<Char>.patrol(): Pair<MutableMap<Position,LineDirection>, Boolean> {
        val visited = mutableMapOf<Position,LineDirection>(guard to facing)

        var current = guard
        var direction = facing
        var stuck = false
        while (true) {
            var way = this.linePositions(start = current, direction = direction, skipStart = true).toList()
            var (spaces, blocked) = way.indexOfFirst { pos -> this[pos]=='#' }
                .let { index -> if (index>=0) Pair(index,true) else Pair(way.size,false)}
            stuck = visited.fillAndCheckWay(way.take(spaces), direction)

            if (!blocked || stuck) break;

            current += direction.asPosition() * spaces
            direction = direction.clockWise()
        }
        return Pair(visited, stuck)
    }

    var guardsWay = mutableMapOf<Position,LineDirection>()

    // part 1: solutions: 41 / 4883

    timeResult { // [M3 5.973625ms]
        theLab.patrol().let { (visited, _) -> guardsWay.putAll(visited); visited.size }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (...)") }

    guardsWay.remove(guard) // skip starting postion

    // part 2: solutions: 6 / 1655

    timeResult { // [M3 838.868625ms]
        guardsWay.keys.count { pos ->
            theLab.temporarilyReplace(pos,'#') { patrol() }.let { (_, stuck) -> stuck }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (...)") }
}
