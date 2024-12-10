// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 10

fun main() {
    println("Day $day\n-----\n")
    fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: trailheads 2
"""
...0...
...1...
...2...
6543456
7.....7
8.....8
9.....9
""",
// 2: trailheads 4
"""
..90..9
...1.98
...2..7
6543456
765.987
876....
987....
""",
// 3: trailheads 2
"""
10..9..
2...8..
3...7..
4567654
...8..3
...9..2
.....01
""",
// 4: trailheads 36, rating 81
"""
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
""",
// 5: rating 81
"""
.....0.
..4321.
..5..2.
..6543.
..7..4.
..8765.
..9....
""",
// 6: rating 13
"""
..90..9
...1.98
...2..7
6543456
765.987
876....
987....
""",
// 7: rating 227
"""
012345
123456
234567
345678
4.6789
56789.
""",
// 8: rating 81
"""
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
""",
)

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val guide = inputData.toField { c -> c.digitToIntOrNull() ?: -1 }

    fun followTrail(start: Position, expected: Int, highest: MutableMap<Position,Int>, past:Position? = null) {
        guide[start].let { c -> when {
            c==9 && expected==9 -> highest[start] = highest.getOrDefault(start, 0) + 1
            c==expected /* && c!=-1 */ -> guide.nextCrossWayPositions(start,past).forEach {
                followTrail(it,expected+1,highest,start)
            }
        } }
    }

    // part 1: solutions: 550
    // part 2: solutions: 1255

    timeResult { // [M3 9.740667ms]
        val trailheads = guide.positions().filter { guide[it] == 0 }
        trailheads.fold(0 to 0) { acc, start ->
            mutableMapOf<Position,Int>().let { highest ->
                followTrail(start,0,highest)
                acc.first + highest.size to acc.second + highest.values.sum()
            }
        }
    }.let { (dt,result) -> println("[part 1|2] results: ${result.first}|${result.second}, dt: $dt (hiking)") }
}
