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

    // part 1: solutions: 18 / 2406

    val xmas = listOf('X','M','A','S')
    val xmasSize = xmas.size
    val grid = inputData.toGrid()   // make it a 2D field

    timeResult { // [M3 28.228709ms]
        grid.positions.sumOf { pos ->
            RCDir.AllCardinals.count { dir -> pos.walk(dir).toGrid(grid).take(xmasSize).contentEquals(xmas) }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (word search)") }

    // part 2: solutions: 9 / 1807

    val mas = listOf('M','S')
    val sam = listOf('S','M')
    val midElement = 'A'
    val extent = mas.size / 2
    timeResult { // [M3 10.471666ms]
        grid.positions.sumOf { pos ->
            // from pos consider only diagonal lines matching MAS in all variations
            if (grid[pos] != midElement) 0
            else {
                val uldr = (pos.visit(RCDir.UpLeft).slice(1..extent).toGrid(grid) +
                        pos.visit(RCDir.DownRight).slice(1..extent).toGrid(grid)).toList()
                val dlur = (pos.visit(RCDir.DownLeft).slice(1..extent).toGrid(grid) +
                        pos.visit(RCDir.UpRight).slice(1..extent).toGrid(grid)).toList()
                if ( (uldr==mas || uldr==sam) && (dlur==mas || dlur==sam) ) 1 else 0.toInt()
            }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (xmas search)") }
}
