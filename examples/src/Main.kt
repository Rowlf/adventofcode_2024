// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

// enum classes are global, needed for case 2
enum class Color { blue, red, green }

fun main() {
    // different ways handling input data (input.txt or as multiline string example)
    val case = 4
    when (case) {
        1 -> {
            // simple text per line
            val data = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
"""
            // val inputLines = linesOf(day = 0)    // read from dayNN/data/input.txt
            val inputLines = linesOf(data = data)   // read from multiline string
            inputLines.print(indent = 2, description = "case $case, input lines:")  // , take = 3

            // find file...
            // val currentDirectory = System.getProperty("user.dir")
            // println("Current runtime directory: $currentDirectory")
        }
        2 -> {
            // leading "Game No" text with variable number of "No Color" pairs
            val data = """
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
"""
            val inputLines = linesOf(data = data)
            inputLines.print(indent = 2, description = "case $case, input lines:")

            data class ColorMappings(val colors:Map<Color,Int>)
            data class Game(val no: Int, val pairs: List<ColorMappings>)

            fun gameOf(line: String): Game {
                val gameRegex = """Game (\d+): (.+)""".toRegex()
                val pairRegex = """(\d+) (\w+)""".toRegex()

                // assume, line format is correct
                // split line in game no and rest
                gameRegex.matchEntire(line)!!.destructured.let { (gameNo, tail) ->
                    val groups = tail.split(';').map { group ->
                        group.split(',').associate { pair ->
                            pairRegex.matchEntire(pair.trim())!!.destructured.let { (num, colorString) ->
                                val color = Color.entries.find { it.name.equals(colorString, ignoreCase = true) }!!
                                color to num.toInt()
                            }
                        }.let { ColorMappings(it) }
                    }
                    return Game(gameNo.toInt(), groups)
                }
            }

            val games = inputLines.map { gameOf(it) }
            games.print(indent = 2, description = "games:")
        }
        3 -> {
            // leading "Card No" text with two blocks of ints
            val data = """
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
"""
            val inputLines = linesOf(data = data)
            inputLines.print(indent = 2, description = "case $case, input lines:")

            data class Card(val no: Int, val numbers1: List<Int>, val numbers2: List<Int>)

            fun cardOf(line: String): Card {
                val cardRegex = """Card (\d+): (.+) \| (.+)""".toRegex()

                cardRegex.matchEntire(line)!!.destructured.let { (cardNo, part1, part2) ->
                    val numbers1 = part1.split(' ').mapNotNull { it.trim().toIntOrNull() } // takeIf { }?.toIntOrNull() }
                    val numbers2 = part2.split(' ').mapNotNull { it.trim().toIntOrNull() }
                    return Card(cardNo.toInt(), numbers1, numbers2)
                }
            }

            val cards = inputLines.map { cardOf(it) }
            cards.print(indent = 2, description = "cards:")
        }
        4 -> {
            // a 2d-array
            val data = """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
"""
            val inputLines = linesOf(data = data)
            inputLines.print(indent = 2, description = "case $case, input lines:")

            val field = inputLines.toField()
            field.print(indent = 2, description = "field:")

            println("elements right, start (2,3), skip=true:")
            println(field[field.linePositions(start = Position(2,3), direction = Field.LineDirection.RIGHT, skipStart = true)].joinToString("|", prefix = "  "))

            println("positions left, start (4,3):")
            println(field[field.linePositions(start = Position(4,3), direction = Field.LineDirection.LEFT)].joinToString("|", prefix = "  "))

            println("positions left, start (4,3), only 3 elements:")
            println(field[field.linePositions(start = Position(4,3), direction = Field.LineDirection.LEFT).take(3)].joinToString("|", prefix = "  "))

            println("elements around start (0,2), skip=true:")
            println(field[field.squarePositions(start = Position(0,2), skipStart = true, extent = 1)].joinToString("|", prefix = "  "))

            // use field.positions(...).iterator() if you need hasNext/next:
            //      while (iterator.hasNext()) {
            //          ... iterator.next()
        }
        5 -> {
            // lines are organized in blocks with a separate header block
            val data = """
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4
"""
            val inputLines = linesOf(data = data)
            inputLines.print(indent = 2, description = "case $case, input lines:")

            val (header, body) = inputLines.toBlocks().splitHeaderBlock(n = 1)
            header.print(indent = 2, description = "header:")
            body.print(indent = 2, description = "body:")
        }
    }

}
