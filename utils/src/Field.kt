// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

/**
 * A simple point class.
 */
data class Position(val row: Int, val col: Int) {
    operator fun times(scale: Int): Position {
        return Position(row * scale, col * scale)
    }

    operator fun plus(shift: Position): Position {
        return Position(row + shift.row, col + shift.col)
    }

    operator fun minus(rhs: Position): Position {
        return Position(row - rhs.row, col - rhs.col)
    }

    operator fun unaryMinus() = Position(-row, -col)
}

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
    operator fun get(seq: Sequence<Position>): Sequence<T> = seq.map(this::get)
    operator fun set(row: Int, col: Int, value: T) { _data[row][col] = value }
    operator fun set(pos: Position, value: T) { set(pos.row, pos.col, value) }

    fun print(indent: Int = 0, description: String = "") {
        if (description.isNotBlank()) { println(description) }
        val prefix = " ".repeat(indent)
        _data.forEach { row ->
            println(row.joinToString(" ", prefix = prefix))
        }
    }

    enum class LineDirection(val rowOffset: Int, val colOffset: Int) {
        UP(-1, 0),
        DOWN(+1, 0),
        LEFT(0, -1),
        RIGHT(0, +1),
        UPLEFT(-1, -1),
        DOWNLEFT(+1, -1),
        UPRIGHT(-1, +1),
        DOWNRIGHT(+1, +1);

        fun asPosition() = Position(rowOffset, colOffset)
        fun clockWise() = when (this) {
            UP -> RIGHT
            DOWN -> LEFT
            LEFT -> UP
            RIGHT -> DOWN
            UPLEFT -> UPRIGHT
            UPRIGHT -> DOWNRIGHT
            DOWNRIGHT -> DOWNLEFT
            DOWNLEFT -> UPLEFT
        }
    }

    fun positions() = sequence {
        for (row in 0 ..< rows) {
            for (col in 0 ..< cols) {
                yield(Position(row, col))
            }
        }
    }

    fun isValid(pos: Position) = (pos.row in 0..<rows && pos.col in 0..<cols)
    fun isValidRow(row: Int) = (row in 0..<rows)
    fun isValidCol(col: Int) = (col in 0..<cols)

    fun linePositions(start: Position, direction: LineDirection, skipStart: Boolean = false)
    = sequence {
        require(start.row in 0 ..< rows && start.col in 0 ..< cols) {
            "start ($start) must be within [0..${rows - 1},0..${cols - 1}]"
        }
        var row = start.row + if (skipStart) direction.rowOffset else 0
        var col = start.col + if (skipStart) direction.colOffset else 0
        var cnt = 0
        while (row in 0 ..< rows && col in 0 ..< cols) {
            yield(Position(row, col))
            row += direction.rowOffset
            col += direction.colOffset
            cnt++
        }
    }

    fun squarePositions(start: Position, skipStart: Boolean = false, extent: Int = 1)
    = sequence { // or such as: (-1..1).asSequence().flatMap { row -> (-1..1).asSequence().map { col -> ...
        (-extent..extent).forEach { rowOffset ->
            (-extent..extent).forEach { colOffset ->
                if (!skipStart || rowOffset != 0 || colOffset != 0)
                    yield(Position(start.row+rowOffset, start.col+colOffset))
            }
        }
    }.filter { (row,col) -> (row in 0 ..< rows) && (col in 0 ..< cols) }

    fun nextCrossWayPositions(start: Position, past: Position? = null)
    = sequence {
        yield(start + LineDirection.RIGHT.asPosition())
        yield(start + LineDirection.DOWN.asPosition())
        yield(start + LineDirection.LEFT.asPosition())
        yield(start + LineDirection.UP.asPosition())
    }.filter { isValid(it) && (past==null || it != past) }

    inline fun <R> temporarilyReplace(pos: Position, value: T, block: Field<T>.() -> R): R {
        val original = this[pos]
        this[pos] = value
        return try {
            block()
        } finally {
            this[pos] = original
        }
    }
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

fun <R> Lines.toField(block: (Char) -> R) = Field<R>().apply { this@toField.forEach { add(it.map { c -> block(c) }) } }

/**
 * Compares the contents of the sequence with another iterable to determine if they are equal.
 *
 * @param other The other iterable to compare against.
 * @return `true` if both the sequence and the iterable contain the same elements in the same order, `false` otherwise.
 */
fun <T> Sequence<T>.contentEquals(other: Iterable<T>): Boolean {
    val lhs = this.iterator()
    val rhs = other.iterator()

    // or with zip, but we need to check the end, so this is the shortest (imho)
    while (lhs.hasNext() && rhs.hasNext()) {
        if (lhs.next() != rhs.next()) return false
    }
    return !lhs.hasNext() && !rhs.hasNext()
}
