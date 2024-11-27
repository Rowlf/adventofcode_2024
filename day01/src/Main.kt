// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 1

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
a
bb
ccc
"""
    val example1Lines = linesOf(data = example1)
    // val inputLines = linesOf(day = day)

    val inputData = example1Lines
    // val inputData = inputLines
    inputData.print(indent = 2, description = "input lines:", take = 2)
}
