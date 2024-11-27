// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

/**
 * A simple point class.
 */
data class Position(val row: Int, val col: Int)

/**
 * Represents a 2D field of elements of type T. It supports basic
 * operations such as adding rows, accessing and modifying elements,
 * and printing the field.
 * Moreover, there is an iterator running the row or col or the neighborhood.
 * Note that it is assumed that all rows are of same length.
 *
 * @param T The type of elements stored in the field.
 */
class Field<T> {
    private val _data: MutableList<MutableList<T>> = mutableListOf()

    val rows: Int
        get() = _data.size
    val cols: Int
        get() = _data[0].size

    fun add(row: List<T>) { _data.add(row.toMutableList()) }

    operator fun get(row: Int, col: Int): T = _data[row][col]
    operator fun get(pos: Position): T = get(pos.row, pos.col)
    operator fun set(row: Int, col: Int, value: T) { _data[row][col] = value }
    operator fun set(pos: Position, value: T) { set(pos.row, pos.col, value) }

    fun print(indent: Int = 0, description: String = "") {
        if (description.isNotBlank()) { println(description) }
        val prefix = " ".repeat(indent)
        _data.forEach { row ->
            println(row.joinToString(" ", prefix = prefix))
        }
    }

    enum class Direction { LEFT, RIGHT, UP, DOWN, ROUND }

    fun positions(start: Position, direction: Direction, skipStart: Boolean = false)
    = positions(start.row, start.col, direction, skipStart)

    fun positions(startRow: Int, startCol: Int, direction: Direction, skipStart: Boolean = false)
    = sequence {
        require(startRow in 0 ..< rows && startCol in 0 ..< cols) {
            "start ($startRow,$startCol) must be within [0..${rows - 1},0..${cols - 1}]"
        }
        val offset = if (skipStart) 1 else 0
        when (direction) {
            Direction.LEFT -> for (col in startCol-offset downTo 0) yield(Position(startRow, col))
            Direction.RIGHT -> for (col in startCol+offset until cols) yield(Position(startRow, col))
            Direction.UP -> for (row in startRow-offset downTo 0) yield(Position(row, startCol))
            Direction.DOWN -> for (row in startRow+offset until rows) yield(Position(row, startCol))
            Direction.ROUND -> yieldAll(generateRoundIndices(startRow, startCol, skipStart))
        }
    }

    private fun generateRoundIndices(startRow: Int, startCol: Int, skipStart: Boolean)
    = sequence { // or: (-1..1).asSequence().flatMap { row -> (-1..1).asSequence().map { col -> ...
        (-1..1).forEach { rowOffset ->
            (-1..1).forEach { colOffset ->
                if (!skipStart || rowOffset != 0 || colOffset != 0)
                    yield(Position(startRow+rowOffset, startCol+colOffset))
            }
        }
    }.filter { (row,col) -> (row in 0 ..< rows) && (col in 0 ..< cols) }

    fun elements(start: Position, direction: Direction, skipStart: Boolean = false)
    = elements(start.row, start.col, direction, skipStart)

    fun elements(startRow: Int, startCol: Int, direction: Direction, skipStart: Boolean = false)
    = positions(startRow, startCol, direction, skipStart)
        .map { (row, col) -> _data[row][col] }
}

/**
 * Converts a list of strings (Lines) into a Field of characters.
 *
 * This method iterates over each string in the Lines and converts
 * each string into a list of characters. Each list of characters is
 * then added as a row to a newly created Field instance of Char.
 *
 * @return A Field<Char> object containing the characters from the Lines.
 */
fun Lines.toField() = Field<Char>().apply { this@toField.forEach { add(it.toList()) } }
