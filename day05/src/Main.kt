// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 5

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
"""

    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val (ruleLines, updateLines) = inputData.toBlocks().splitHeaderBlock(n = 1).run { first[0] to second[0] }

    val rules = ruleLines
        .map { line -> line.extractIntegers('|').let { (a, b) -> a to b } }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.toSet() }
    val updates = updateLines.map { line -> line.extractIntegers(',') }

    fun doesBreak(n: Int, m: Int) = rules[m]?.contains(n)==true // or: n in rules[m]!!
    fun List<Int>.isCorrect(n:Int, from:Int = 0) = subList(from,size).all { m -> !doesBreak(n, m) }
    fun List<Int>.isCorrect() = withIndex().all { (index, n) -> isCorrect(n, from = index+1) }

    // part 1: solutions: 143 / 6051

    val wrongUpdates = mutableListOf<List<Int>>()
    timeResult { // [M3 2.074584ms]
        updates.sumOf { update ->
            if (update.isCorrect()) { update[update.size/2] }
            else { wrongUpdates.add(update); 0 }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (...)") }

    // part 2: solutions: 123 / 5093

    timeResult { // [M3 6.413750ms]
        wrongUpdates.sumOf { update ->
            buildList {
                val pages = update.toMutableList()
                // assuming there _is_ a solution...
                while (pages.isNotEmpty()) {
                    pages.removeFirst().let { n-> (if (pages.isCorrect(n)) this else pages).add(n) }
                }
            }.run { this[this.size/2] } // btw. we could have stopped at the mid-element
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (...)") }
}
