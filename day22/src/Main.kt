// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 22

fun main() {
    println("Day $day\n-----\n")
    fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
1
10
100
2024
""",
    )

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val initialValues = inputData.map { it.toLong() }

    fun step(n: Long): Long {                   // note: prune 16777216L = 0x1000000
        val n1 = n.xor(n shl 6) and 0xffffff    // prune mix n, n * 64
        val n2 = n1.xor(n1 shr 5) and 0xffffff  // prune mix n / 32
        return n2.xor(n2 shl 11) and 0xffffff   // prune mix n * 2048
    }

    // part 1: solutions: 37327623 / 16619522798

    timeResult { // [M3 9.369708ms]
        initialValues.sumOf { initialValue ->
            (1..2000).fold(initialValue) { n, _ -> step(n) }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (secret values)") }

    // part 2: solutions: 24 / 1854
    // heavy task description...

    timeResult { // [M3 10.931793583s]
        val allSeqMappings = initialValues.map { initialValue ->
            // as before, but as list of numbers and diffs
            val numbers = (1..2000).runningFold(initialValue to 0L) { (n, _), _ ->
                step(n).let { it to (it % 10) - (n % 10) }
            }.drop(1)
            // slice and look for first occurrence and save the bananas
            mutableMapOf<List<Long>, Long>().apply {
                numbers.windowed(4).forEach { window ->
                    val seq = window.map { it.second }
                    putIfAbsent(seq, window.last().first % 10)
                }
            }
        }

        // test all sequences and search the max bananas
        mutableSetOf<List<Long>>().apply {
            allSeqMappings.forEach { addAll(it.keys) }
        }.maxOf { seq ->
            allSeqMappings.sumOf { it[seq] ?: 0L }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (bananas)") }
}
