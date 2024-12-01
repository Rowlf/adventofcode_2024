// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import kotlin.math.abs
import kotlin.time.measureTime

const val day = 1

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
3   4
4   3
2   5
1   3
3   9
3   3
"""
    val inputData = linesOf(data = example1)
    //val inputData = linesOf(day = day)

    inputData.print(indent = 2, description = "input lines:", take = 6)
    println()

    val lineRegex = """(\d+)\s+(\d+)""".toRegex()
    // keep both lists at one place, no need for an extra class
    val sortedData = Pair(mutableListOf<Int>(), mutableListOf<Int>()).apply {
        for (line in inputData) {
            lineRegex.matchEntire(line)!!.destructured.let { (n1, n2) ->
                first.add(n1.toInt())
                second.add(n2.toInt())
            }
        }
        first.sort()
        second.sort()
    }

    // Both parts are straightforward, so this is my shortest and most Kotlin-idiomatic solution with reasonable effort.
    // Three comments: a collection modelling a sorted list may be tempting, but it does not make sense (imho),
    // as all adds=inserts have to look for the right place to insert, but this is far more overhead than sorting the list
    // at the end once (in this situation here).
    // Second, technically the second part does not need to work on the sorted lists, but if you really want to,
    // you could simplify the counting by a) doing a binary search to find the first occurrence of a value
    // in the second list, and then b) simply counting down and up as long as the elements are equal
    // -> see example version 2.
    // Third, another idea utilizing maps with grouping and counting before. This is also a very fast solution
    // for large data sets.

    // part 1: solutions: 11 / 2057374

    val duration1 = measureTime {
        val totalDistance = sortedData.run {
            first.zip(second).sumOf { (n1, n2) -> abs(n1 - n2) }
        }
        println("part 1: total distance: $totalDistance")
    }
    println("        duration: $duration1\n")

    // part 2: solutions: 31 / 23177084

    val duration2 = measureTime {
        val similarityScore = sortedData.run {
            first.sumOf { n1 -> n1 * second.count { n2 -> n1 == n2 } }
        }
        println("part 2: similarity score: $similarityScore")
    }
    println("        duration: $duration2\n")

    // exploit the sorted structure...
    val duration2a = measureTime {
        val similarityScore = sortedData.run {
            first.sumOf { n1 -> n1 * second.run { binarySearch(n1).let { index ->
                if (index >= 0) 1 + countWhile(index - 1, -1, n1) + countWhile(index + 1, 1, n1) else 0
            } } }
        }
        println("part 2: alternative similarity score: $similarityScore")
    }
    println("        duration: $duration2a (${"%.2f".format(duration2 / duration2a)}x faster)\n")

    // count before...
    val duration2b = measureTime {
        val similarityScore = sortedData.run {
            val countMap = second.groupingBy { it }.eachCount()
            first.sumOf { n1 -> n1 * (countMap[n1] ?: 0) }
        }
        println("part 2: alternative similarity score: $similarityScore")
    }
    println("        duration: $duration2b (${"%.2f".format(duration2 / duration2b)}x faster)")
}
