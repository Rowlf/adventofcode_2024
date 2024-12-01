// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

// template

const val day = 99

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
a
bb
ccc
"""
    val inputData = linesOf(data = example1)
    // val inputData = linesOf(day = day)

    inputData.print(indent = 2, description = "input lines:", take = 2)
}
