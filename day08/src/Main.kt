// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 8

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
"""

    val example = 1
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 2)
    val city = inputData.toField()

    val antennas = city.positions().filter { city[it] != '.' }.groupBy { city[it] }

    fun createAntinodes(withHarmonics: Boolean) = antennas.values.flatMapTo(mutableSetOf<Position>()) { positions ->
        positions.flatMapIndexed { i, pos1 -> positions.drop(i+1).flatMap { pos2 -> (pos1-pos2).let { offset ->
                (generateSequence(1) { if (withHarmonics) it + 1 else null }
                    .map { cnt -> pos1 + offset * cnt }.takeWhile { city.isValid(it) }
                +
                generateSequence(1) { if (withHarmonics) it + 1 else null }
                    .map { cnt -> pos2 - offset * cnt }.takeWhile { city.isValid(it) })
            } }
        } + if (withHarmonics) positions else listOf()
    }

    // part 1: solutions: 14 / 291

    timeResult { // [M3 12.334959ms]
        createAntinodes(withHarmonics = false).size
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (antinode locations)") }

    // part 2: solutions: 34 / 1015

    timeResult { // [M3 1.616250ms]
        createAntinodes(withHarmonics = true).size
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (with resonant harmonics)") }
}
