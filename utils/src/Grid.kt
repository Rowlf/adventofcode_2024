// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import kotlin.math.abs
import kotlin.sequences.map

interface AStep {
    val asPos: RCPos
    val asDir: RCDir
}

data class RCPos(val row: Int, val col: Int): AStep {
    override fun toString() = "($row|$col)"
    override val asPos: RCPos get() = this
    override val asDir: RCDir get() = RCDir(0, 0)
}

data class RCDir(val dRow: Int, val dCol: Int): AStep {
    override fun toString() = "($dRow:$dCol)"

    override val asPos: RCPos get() = RCPos(dRow, dCol)
    override val asDir: RCDir get() = this

    val norm1: Int get() = abs(dRow) + abs(dCol)

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

        fun Square(extend: Int) = sequence {
            for (dr in -extend..extend) {
                for (dc in -extend..extend) {
                    yield(RCDir(dr, dc))
                }
            }
        }
    }
}

data class RCStep(val pos: RCPos, val dir: RCDir): AStep {
    override fun toString() = "(${pos.row}|${pos.col} ${dir.dRow}:${dir.dCol})"
    override val asPos: RCPos get() = pos
    override val asDir: RCDir get() = dir
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

fun RCPos.visit(step: RCDir) = sequence {
    var pos = this@visit
    while (true) { yield(pos); pos += step }
}

fun RCPos.visit(seq: Sequence<RCDir>)
= sequence { yieldAll(seq.map { this@visit + it }) }

fun RCPos.walk(step: RCDir) = sequence {
    var pos = this@walk
    while (true) { yield(RCStep(pos=pos, dir=step)); pos += step }
}

fun RCPos.walk(seq: Sequence<RCDir>)
= sequence { yieldAll(seq.map { RCStep(pos=this@walk + it, dir=it) }) }

//

data class RCValue<T>(val pos: RCPos, val dir:RCDir, val value: T): AStep {
    override fun toString() = "(${pos.row}|${pos.col} ${dir.dRow}:${dir.dCol} '$value')"
    override val asPos: RCPos get() = pos
    override val asDir: RCDir get() = dir
}

fun <T,S:AStep> Sequence<S>.takeWhileInGrid(grid: RCGrid<T>) = this.takeWhile { it.asPos in grid }

fun <T,S:AStep> Sequence<S>.withGrid(grid: RCGrid<T>) = this.takeWhile { it.asPos in grid }.map { RCValue(it.asPos, it.asDir, grid[it.asPos]) }

fun <T,S:AStep> Sequence<S>.toGrid(grid: RCGrid<T>) = this.takeWhile { it.asPos in grid }.map { grid[it.asPos] }

fun <T> Sequence<T>.slice(range: IntRange): Sequence<T> {
    //require(range.first >= 0) { "Range start must be non-negative" }
    return drop(range.first).take(range.last - range.first + 1)
}

// ---

class RCGrid<T>() {
    val data: MutableList<MutableList<T>> = mutableListOf()

    val rows: Int get() = data.size
    val cols: Int get() = data[0].size

    constructor(rows: Int, cols: Int, block: (pos: RCPos) -> T) : this() {
        reset(rows, cols, block)
    }

    fun reset(rows: Int, cols: Int, block: (pos: RCPos) -> T) {
        data.clear()
        data.addAll(MutableList(rows) { row -> MutableList(cols) { col -> block(RCPos(row,col)) } })
    }

    fun add(row: MutableList<T>) { data.add(row) }
    fun addAll(rows: Iterable<MutableList<T>>) { data.addAll(rows) }

    operator fun get(row: Int, col: Int): T = data[row][col]
    operator fun get(pos: RCPos): T = get(pos.row, pos.col)
    //operator fun get(seq: Sequence<RCPos>): Sequence<T> = seq.map(this::get)
    operator fun set(row: Int, col: Int, value: T) { data[row][col] = value }
    operator fun set(pos: RCPos, value: T) { set(pos.row, pos.col, value) }

    //operator fun get(step: RCStep): T = get(step.pos.row, step.pos.col)

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
        data.forEach { row -> println(row.joinToString(separator, prefix = prefix)) }
    }
}

fun Lines.toGrid() = RCGrid<Char>().apply {
    this.addAll(this@toGrid.map { it.toMutableList() })
}

fun <T> RCGrid<T>.toGraph(predicate: (RCPos) -> Boolean) = Graph<RCPos>().also { graph ->
    this.positions.forEach { pos ->
        if (predicate(pos)) {
            graph.add(pos)
            for (neighbor in pos.walk(RCDir.Cardinals).map { it.pos }.filter { it in this }) {
                if (predicate(neighbor))
                    graph.add(pos, neighbor)
            }
        }
    }
}

fun RCGrid<Char>.toGraph(pattern: String = ".") = this.toGraph { this[it] in pattern }
