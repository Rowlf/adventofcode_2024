// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import kotlin.math.abs
import kotlin.time.measureTime

const val day = 2

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
"""
    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 6)

    val reports = inputData.map { line -> line.split(' ').map(String::toInt) }
    reports.print(indent = 2, description = "reports:", take = 6)

    /**
     * Checks if a list of integers is either strictly increasing or strictly decreasing
     * based on the provided direction, while ensuring the difference between
     * consecutive elements does not exceed a threshold (here 3 is fixes).
     */
    fun checkReportOneDirection(line: List<Int>, incOrDec: Boolean)
    = line.zipWithNext().all { (prev, current) -> abs(current - prev).let { dist ->
        (incOrDec && prev < current || !incOrDec && prev > current) && dist <= 3
    } }

    /** checks in both directions */
    fun checkReport(line: List<Int>)
    = checkReportOneDirection(line,incOrDec = true) || checkReportOneDirection(line,incOrDec = false)

    /** checks with one possible exception */
    fun checkTolerantReport(line: List<Int>)
    = checkReport(line)
        || line.indices.any { i -> checkReport(line.toMutableList().apply { removeAt(i) }) }
    // here, technically cloning can be avoided if MutableList are used from the beginning,
    // but due to the insert and remove operations there is no benefit

    // part 1: solutions: 2 / 299

    val duration1 = measureTime {
        val safeReports = reports.count { line -> checkReport(line) }
        println("part 1: safe reports: $safeReports")
    }
    println("        duration: $duration1\n")

    // part 2: solutions: 4 / 364

    val duration2 = measureTime {
        val safeReports = reports.count { line -> checkTolerantReport(line) }
        println("part 2: tolerant safe reports: $safeReports")
    }
    println("        duration: $duration2")
}
