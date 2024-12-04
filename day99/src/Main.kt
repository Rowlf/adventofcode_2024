// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 99

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
a
bb
ccc
"""

    val example = 1
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 2)

    timeResult { // [M3 ]
        23
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (...)") }
}
