// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

data class RCPos(val row: Int, val col: Int) {
    override fun toString() = "($row|$col)"
}

data class RCDir(val dRow: Int, val dCol: Int) {
    override fun toString() = "($dRow:$dCol)"

    val asPos: RCPos get() = RCPos(dRow, dCol)

    companion object {
        val Origin = RCDir(0, 0)
        // directions (dir)
        val Up = RCDir(-1, 0)
        val Down = RCDir(+1, 0)
        val Left = RCDir(0, -1)
        val Right = RCDir(0, +1)
        val UpLeft = RCDir(-1, -1)
        val DownLeft = RCDir(+1, -1)
        val UpRight = RCDir(-1, +1)
        val DownRight = RCDir(+1, +1)

        val Cardinals = sequenceOf(Right, Down, Left, Up)
        val InterCardinals = sequenceOf(DownRight, DownLeft, UpLeft, UpRight)
        val AllCardinals = sequenceOf(Right, DownRight, Down, DownLeft, Left, UpLeft, Up, UpRight)
    }

}

data class RCStep(val pos: RCPos, val dir: RCDir) {
    override fun toString() = "(${pos.row}|${pos.col} ${dir.dRow}:${dir.dCol})"
}

// ---

operator fun RCPos.times(other: Int) = RCPos(row*other, col*other)
operator fun RCPos.times(other: Long) = this * other.toInt()
operator fun Int.times(other: RCPos) = other * this
operator fun Long.times(other: RCPos) = other * this

operator fun RCPos.plus(other: RCPos) = RCPos(row+other.row, col+other.col)
operator fun RCPos.unaryMinus() = -1 * this // RCPos(-row, -col)
operator fun RCPos.minus(other: RCPos) = this + (-other) //RCPos(row-other.row, col-other.col)

operator fun RCPos.plus(other: RCDir) = RCPos(row+other.dRow, col+other.dCol)
operator fun RCDir.plus(other: RCPos) = other + this

operator fun RCDir.times(other: Int) = RCPos(dRow*other, dCol*other)
operator fun RCDir.times(other: Long) = this * other.toInt()
operator fun Int.times(other: RCDir) = other * this
operator fun Long.times(other: RCDir) = other * this

operator fun RCDir.plus(other: RCDir) = RCPos(dRow+other.dRow, dCol+other.dCol)
operator fun RCDir.unaryMinus() = -1 * this

fun RCPos.walk(step: Sequence<RCDir>) = walk(step.asIterable())

fun RCPos.walk(step: Iterable<RCDir>) = sequence<RCStep> {
    val it = step.iterator()
    while (it.hasNext()) {
        val step = it.next()
        yield(RCStep(pos=this@walk + step,dir=step))
    }
}

// ---

class RCGrid<T>() {
    val cells: MutableList<MutableList<T>> = mutableListOf()

    val rows: Int get() = cells.size
    val cols: Int get() = cells[0].size

    constructor(rows: Int, cols: Int, block: (pos: RCPos) -> T) : this() {
        reset(rows, cols, block)
    }

    fun reset(rows: Int, cols: Int, block: (pos: RCPos) -> T) {
        cells.clear()
        cells.addAll(MutableList(rows) { row -> MutableList(cols) { col -> block(RCPos(row,col)) } })
    }

    fun add(row: MutableList<T>) { cells.add(row) }
    fun addAll(rows: Iterable<MutableList<T>>) { cells.addAll(rows) }

    operator fun get(row: Int, col: Int): T = cells[row][col]
    operator fun get(pos: RCPos): T = get(pos.row, pos.col)
    operator fun get(seq: Sequence<RCPos>): Sequence<T> = seq.map(this::get)
    operator fun set(row: Int, col: Int, value: T) { cells[row][col] = value }
    operator fun set(pos: RCPos, value: T) { set(pos.row, pos.col, value) }

    fun isValid(row: Int, col: Int) = (row in 0..<rows && col in 0..<cols)
    fun isValid(pos: RCPos) = isValid(pos.row, pos.col)
    fun isValidRow(row: Int) = (row in 0..<rows)
    fun isValidCol(col: Int) = (col in 0..<cols)

    operator fun contains(index: RCPos) = isValid(index)

    val positions get() = (0 until rows).asSequence().flatMap { row ->
        (0 until cols).asSequence().map { col -> RCPos(row, col) }
    }

    fun print(indent: Int = 0, description: String = "", separator: String =" ") {
        if (description.isNotBlank()) { println(description) }
        val prefix = " ".repeat(indent)
        cells.forEach { row -> println(row.joinToString(separator, prefix = prefix)) }
    }
}

fun Lines.toGrid() = RCGrid<Char>().apply {
    this.addAll(this@toGrid.map { it.toMutableList() })
}

fun <T> RCGrid<T>.toGraph() = Graph<RCPos>().also { graph ->
    this.positions.forEach { pos ->
        if (this[pos] == '.') {
            graph.add(pos)
            for (neighbor in pos.walk(RCDir.Cardinals).map { it.pos }.filter { it in this }) {
                if (this[neighbor] == '.')
                    graph.add(pos, neighbor)
            }
        }
    }
}
