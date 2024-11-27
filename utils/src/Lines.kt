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
fun <T> List<T>.print(indent: Int = 0, description: String = "", take: Int = this.size) {
    if (description.isNotBlank()) { println(description) }
    val prefix = " ".repeat(indent)
    this.take(take).forEach { row ->
        println("$prefix$row")
    }
}
