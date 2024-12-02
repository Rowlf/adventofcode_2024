// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

/**
 * Extracts integers from a given input string using specified delimiters. The input string
 * is first trimmed of any leading or trailing whitespace. It is then split into parts based
 * on the provided delimiters. Finally, each part is trimmed individually and converted to
 * an integer if possible. The method returns a list of successfully converted integers.
 *
 * @param line The input string from which to extract integers.
 * @param delimiters Characters used to split the input string into parts. If no delimiters
 * are provided, the default behavior uses whitespace as a delimiter.
 * @return A list of integers extracted from the input string.
 */
fun extractIntegers(line: String, vararg delimiters: Char) = line.trim().split(*delimiters).mapNotNull { it.trim().toIntOrNull() }

/**
 * Extracts integers from a given input string using whitespace as the default delimiter.
 *
 * @param line The input string from which to extract integers.
 * @return A list of integers extracted from the input string, using whitespace as the delimiter.
 */
fun extractIntegers(line: String) = extractIntegers(line, ' ')
