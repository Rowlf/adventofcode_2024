// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

typealias Blocks = List<Lines>

/**
 * Splits a list of lines into blocks based on a custom block separator condition.
 *
 * @param isNewBlock A lambda function that determines whether a line signifies the start of a new block.
 * Defaults to checking if a line is blank.
 * @return A list of blocks, where each block is a list of lines.
 */
fun Lines.toBlocks(isNewBlock: (String) -> Boolean = { it.isBlank() }): Blocks
= mutableListOf<Lines>().also { blocks ->
    var currentBlock = mutableListOf<String>()
    this.forEach { line ->
        if (isNewBlock(line)) {
            if (currentBlock.isNotEmpty()) {
                blocks.add(currentBlock)
                currentBlock = mutableListOf()
            }
        } else {
            currentBlock.add(line)
        }
    }
    if (currentBlock.isNotEmpty()) {
        blocks.add(currentBlock)
    }
}

fun Blocks.splitHeaderBlock(n: Int = 1): Pair<Blocks, Blocks> = Pair(this.take(n), this.drop(n))
