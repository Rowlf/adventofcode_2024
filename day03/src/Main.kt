// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 3

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
"""
    val example2 = """
xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
"""
    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        2 -> linesOf(data = example2)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 2)

    fun String.extractProduct() = this.extractIntegers(',').let { (n1,n2) -> n1 * n2 }

    // part 1: solutions: 161 / 166630675

    timeResult { // [M3 7.466041ms]
        val regex = Regex("mul\\((\\d+,\\d+)\\)")
        inputData.sumOf { line ->
            regex.findAll(line).sumOf { it.groupValues[1].extractProduct() }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (corrupted memory)") }

    // part 2: solutions: 48 / 93465710

    timeResult { // [M3 1.264500ms]
        var switch = true
        val regex = Regex("mul\\((\\d+,\\d+)\\)|do\\(\\)|don't\\(\\)")
        inputData.sumOf { line ->
            regex.findAll(line).sumOf { when (it.value) {
                "do()" -> { switch = true; 0 }
                "don't()" -> { switch = false; 0 }
                else -> if (switch) it.groupValues[1].extractProduct() else 0
            } }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (corrupted memory switched)") }
}
