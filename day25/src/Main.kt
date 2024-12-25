// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 25

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####
""",
    )

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val blocks = inputData.toBlocks()

    val lockIndicator = blocks[0][0]
    val pins = lockIndicator.length
    val spaces = blocks[0].size

    val (locks,keys) = blocks.map { it.toField() }.mapIndexed { i, f ->
        val occupied = (0..<pins).map { pin -> (0..<spaces).count { f[it,pin] == '#' } - 1 }
        if (blocks[i][0]==lockIndicator) occupied to null else null to occupied
    }.unzip().let { (x,y) -> x.filterNotNull() to y.filterNotNull() } // instead if lock.add / keys.add... better?

    // part 1: solutions: 3 / 3201

    timeResult { // [M3 14.417125ms]
        locks.sumOf { lock ->
            keys.count { key ->
                lock.zip(key).all { (l, k) -> l + k < spaces - 1 }
            }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (locks and keys)") }

    // part 2: solutions: . / . chronicles delivered
}
