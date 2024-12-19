// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 19

fun main() {
    println("Day $day\n-----\n")
    fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
"""
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
""",
    )

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 3)

    val blocks = inputData.toBlocks()
    val towels = blocks[0][0].split(",").map { it.trim() }.toSet()
    val stripes = blocks[1].map { it.trim() }

    println("towels: $towels")
    stripes.print(indent = 2, description = "stripes:", take = 2)

    fun countArrangements(stripe: String, memoization: MutableMap<String,Long> = mutableMapOf()): Long {
        if (stripe.isEmpty()) return 1L
        memoization[stripe]?.let { return it }

        return towels.sumOf {
            if (stripe.startsWith(it)) countArrangements(stripe.substring(it.length),memoization) else 0L
        }.apply { memoization[stripe] = this }
    }

    // part 1: solutions: 6 / 283

    timeResult { // [M3 84.323959ms]
        stripes.count { countArrangements(it) > 0 }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (designs)") }

    // part 2: solutions: 16 / 615388132411142

    timeResult { // [M3 94.447ms]
        stripes.sumOf { countArrangements(it) }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (arrangements)") }
}
