// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 99

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
"""
a
bb
ccc
""",
)

    val example = 1
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    // part 1: solutions: 1 / 0

    timeResult { // [M3 ]
        23
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (...)") }
}
