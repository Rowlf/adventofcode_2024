import kotlin.math.log10
import kotlin.math.pow

// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 11

fun main() {
    println("Day $day\n-----\n")
    fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ...
        """
0 1 10 99 999
""",
// 2: part 1: 55312
        """
125 17
""",
    )

    val example = 0
    val inputData = if (example == 0) linesOf(day = day) else linesOf(data = examples[example - 1])

    inputData.print(indent = 2, description = "input lines:", take = 1)
    val stonesOrig = inputData[0].extractLongs(' ')

    // the main idea is to count occurrences
    fun countStones(n: Int): Long {
        val stones = listOf(CounterMap<Long>(stonesOrig), CounterMap<Long>())
        var current = 0 // ping-pong

        repeat(n) {
            val nextStones = stones[1 - current]
            nextStones.clear()

            for ((id, count) in stones[current]) {
                if (id == 0L) {
                    nextStones[1L] += count
                } else {
                    // it feels a little bit better... and, id is not 0 here!
                    val numDigits = log10(id.toDouble()).toInt() + 1
                    if (numDigits % 2 == 0) {
                        val divisor = 10.0.pow(numDigits / 2).toLong()
                        nextStones[id / divisor] += count
                        nextStones[id % divisor] += count
                    } else {
                        nextStones[id * 2024L] += count
                    }
                    /* val str = id.toString()
                    val len = str.length
                    if (len % 2 == 0) {
                        nextStones[str.substring(0, len / 2).toLong()] += count  // or take()
                        nextStones[str.substring(len / 2).toLong()] += count     // or drop()
                    } else {
                        nextStones[id * 2024L] += count
                    } */
                }
            }
            current = 1 - current
        }
        return stones[current].values.sum()
    }

    // part 1: solution: 193607

    timeResult { // [M3 2.876ms]
        countStones(25)
    }.let { (dt, result) -> println("[part 1] result: $result, dt: $dt (stones 25)") }

    // part 2: solution: 229557103025807

    timeResult { // [M3 29.627292ms]
        countStones(75)
    }.let { (dt, result) -> println("[part 2] result: $result, dt: $dt (stones 75)") }
}
