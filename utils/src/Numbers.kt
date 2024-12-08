// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

/**
 * Extracts integers from the receiver string using a specified delimiter. The input string
 * is first trimmed of any leading or trailing whitespace. It is then split into parts based
 * on the provided delimiter. Finally, each part is trimmed individually and converted to
 * an integer if possible. The method returns a list of successfully converted integers.
 *
 * @param delimiter Character used to split the input string into parts.
 * @return A list of integers extracted from the input string.
 */
fun String.extractIntegers(delimiter: Char) = this.trim().split(delimiter).mapNotNull { it.trim().toIntOrNull() }

fun String.extractLongs(delimiter: Char) = this.trim().split(delimiter).mapNotNull { it.trim().toLongOrNull() }
