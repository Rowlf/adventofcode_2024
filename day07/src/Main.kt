// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 7

enum class Operators(val operators: String) {
    Part1("+*"),
    Part2("+*|");

    fun calc(step: Char, r: Long, num: Long) = when (step) {
        '+' -> num + r
        '*' -> num * r
        '|' -> "$r$num".toLong()
        else -> 0
    }

    fun combine(length: Int) = generateCombinations(this.operators, length)

    companion object {
        private val opsCache = Operators.entries.map { operators ->
            mutableMapOf<Int,List<String>>()
        }
    }

    private fun generateCombinations(ops: String, length: Int): List<String> = when {
        opsCache[this.ordinal].containsKey(length) -> opsCache[this.ordinal][length]!!
        length == 0 -> listOf("")
        else -> generateCombinations(ops, length - 1).flatMap { ops.map { c -> "$it$c" } }
            .apply { opsCache[this@Operators.ordinal][length] = this }
    }
}

data class Equation(val result: Long, val numbers: List<Long>)

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
"""

    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }
    inputData.print(indent = 2, description = "input lines:", take = 2)

    val equations = inputData.map { line ->
        line.split(":").let { (result, numbers) -> Equation(result.toLong(), numbers.extractLongs(' ')) }
    }

    fun calibrate(operators: Operators): Long = equations.sumOf { equation ->
        val ops = operators.combine(equation.numbers.size - 1)
        if (ops.any { op -> equation.numbers.reduceIndexed { index, r, num ->
                if (index == 0) num else operators.calc(op[index - 1], r, num) } == equation.result })
            equation.result
        else
            0L
    }

    // part 1: solutions: 3749 / 2654749936343

    timeResult { // [M3 32.093709ms]
        calibrate(Operators.Part1)
    }.let { (dt, result) -> println("[part 1] result: $result, dt: $dt (calibration result)") }

    // part 2: solutions: 11387 / 124060392153684

    timeResult { // [M3 1.258522167s]
        calibrate(Operators.Part2)
    }.let { (dt, result) -> println("[part 1] result: $result, dt: $dt (calibration result with concatenation)") }
}
