// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 4

fun main() {
    println("Day $day\n-----\n")

    val example1 = """
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
"""

    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day)
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val field = inputData.toField() // make it a 2D field

    // part 1: solutions: 18 / 2406

    val xmas = listOf('X','M','A','S')
    val xmasSize = xmas.size
    timeResult { // [M3 28.228709ms]
        field.positions().sumOf { pos ->
            // from pos look in all directions
            Field.LineDirection.entries.count { dir ->
                field[field.linePositions(start = pos, direction = dir, skipStart = false).take(xmasSize)]
                    .contentEquals(xmas)
            }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (word search)") }

    // part 2: solutions: 9 / 1807

    val mas = listOf('M','S')
    val sam = listOf('S','M')
    val midElement = 'A'
    val extent = mas.size / 2
    timeResult { // [M3 10.471666ms]
        field.positions().sumOf { pos ->
            // from pos consider only diagonal lines matching MAS in all variations
            if (field[pos] != midElement) 0
            else {
                // materialize sequences as we need them twice
                val uldr = (field[field.linePositions(start = pos, direction = Field.LineDirection.UPLEFT, skipStart = true).take(extent)
                                  + field.linePositions(start = pos, direction = Field.LineDirection.DOWNRIGHT, skipStart = true).take(extent)])
                    .toList()
                val dlur = (field[field.linePositions(start = pos, direction = Field.LineDirection.DOWNLEFT, skipStart = true).take(extent)
                                  + field.linePositions(start = pos, direction = Field.LineDirection.UPRIGHT, skipStart = true).take(extent)])
                    .toList()
                if ( (uldr==mas || uldr==sam) && (dlur==mas || dlur==sam) ) 1 else 0.toInt()
            }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (xmas search)") }
}
