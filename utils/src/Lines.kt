// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import java.io.File

typealias Lines = List<String>

/**
 * Constructs a File object based on the given day and file name.
 *
 * @param day The day number, which is used to use a subdirectory in the format "dayXX".
 * @param fileName The name of the data file within the subdirectory.
 * @return A File object representing the specified file in the "dayXX/data" directory.
 */
private fun fileOf(day: Int, fileName: String) = File("./day${String.format("%02d", day)}/data/$fileName")

/**
 * Reads input lines for a given day from a specified data string or from a default input file.
 *
 * @param day The day number, used to locate the default input file.
 * @param data Optional string containing the data. If provided, this data will be split into lines.
 * If not provided, the method will read lines from the default input file for the given day.
 * Finally, all leading and trailing blank lines are dropped.
 */
fun linesOf(day: Int = -1, data: String? = null): Lines {
    require(day > 0 || data != null)
    return (data?.lines() ?: fileOf(
        day = day,
        fileName = "input.txt"
    ).readLines())  // use readText for the original textfile
        .dropWhile { it.isBlank() }
        .dropLastWhile { it.isBlank() }
}

/**
 * Prints the elements of the list with optional indentation and description.
 *
 * @param indent The number of spaces to indent each element. Default is 0.
 * @param description A description to print before the list elements. Default is an empty string.
 * @param take The number of elements from the list to print. Default is the size of the list.
 */
fun <T> List<T>.print(indent: Int = 0, description: String = "", take: Int = this.size, skipEndl: Boolean = false) {
    if (description.isNotBlank()) { println(description) }
    val prefix = " ".repeat(indent)
    this.take(take).forEach { row ->
        println("$prefix$row")
    }
    if (!skipEndl) println()
}

/**
 * Performs a binary search on a sorted list to find the index of a target element.
 * The list should be sorted in ascending order for the binary search to function correctly.
 *
 * @param target The element to search for in the list.
 * @return The index of the target element if it is present in the list; otherwise, returns -1.
 */
fun <T : Comparable<T>> List<T>.binarySearch(target: T): Int {
    var left = 0
    var right = size - 1

    while (left <= right) {
        val mid = left + (right - left) / 2
        when {
            this[mid] == target -> return mid
            this[mid] < target -> left = mid + 1
            else -> right = mid - 1
        }
    }
    return -1
}

/**
 * Counts the number of consecutive occurrences of a specified value in the list, starting at a given index and moving
 * in specified step increments. The search continues as long as the indices remain within the list bounds and the list
 * elements match the specified value.
 * A predicate: (T) -> Boolean would be more general, but needs a lot more execution time.
 * This function was needed in day 1.
 *
 * @param startIndex The index from which to start counting.
 * @param step The number of positions to move after each match. A positive step moves forward, a negative step moves backward.
 * @param value The value to be counted if it matches the elements of the list.
 * @return The count of consecutive elements matching the specified value.
 */
fun <T> List<T>.countWhile(startIndex: Int, step: Int, value: T): Int {
    var count = 0
    var index = startIndex

    while (index in indices && this[index]==value) {
        count++
        index += step
    }
    return count
}
