// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import Field.LineDirection
import kotlin.collections.ArrayDeque

const val day = 16

typealias Path = MutableList<Position>
data class DirPosition(val pos: Position, val dir: LineDirection)
data class ScoredPath(val path: Path, val score: Int)
data class Node(val dirPos: DirPosition, val scoredPath: ScoredPath)
fun nodeOf(pos: Position, dir: LineDirection, path: Path, score: Int)
    = Node(DirPosition(pos,dir),ScoredPath(path,score))

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: 7036 / 45
"""
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
""",
// 2: 11048 / 64
"""
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################            
""",
// 3: 2007 / 8 (debug example)
"""
########
#.....E#
#.#.##.#
#S#....#
########
"""
)

    val example = 0
    val inputData = if (example == 0) linesOf(day = day) else linesOf(data = examples[example - 1])

    inputData.print(indent = 2, description = "input lines:", take = 2)
    val maze = inputData.toField()

    maze.print(description = "original:", separator = "")

    var startPos = maze.positions().find { maze[it] == 'S' }!!
    var endPos = maze.positions().find { maze[it] == 'E' }!!
    println("\nstartPos: $startPos, endPos: $endPos\n")

    maze[startPos] = '.'
    maze[endPos] = '.'

    fun findAllRoutes(): List<ScoredPath> = mutableListOf<ScoredPath>().apply {
        val visited = mutableMapOf<DirPosition, Int>()
        val queue = ArrayDeque<Node>()

        queue.add(nodeOf(startPos,LineDirection.RIGHT,mutableListOf(startPos),0))
        while (queue.isNotEmpty()) {
            val (reindeer, current) = queue.removeFirst()
            when {
                reindeer.pos == endPos -> { this.add(current); continue }
                reindeer in visited && visited[reindeer]!! < current.score -> continue
                else -> {
                    visited[reindeer] = current.score
                    for (dir in LineDirection.Cross) {
                        if (reindeer.dir.isOpposite(dir)) continue
                        val nextPos = reindeer.pos+dir

                        if (maze[nextPos]=='.' && (nextPos !in current.path)) { // no border checks
                            queue.add(if (dir==reindeer.dir)
                                nodeOf(nextPos,dir,current.path.toMutableList().also { it.add(nextPos) },current.score+1)
                            else
                                nodeOf(reindeer.pos,dir,current.path.toMutableList(),current.score+1000) // just turn
                            )
                        }
                    }
                }
            }
        }
    }

    var minScore = 0

    // part 1: solutions: - / 83432

    timeResult { // [M3 233.724084ms]
        findAllRoutes().minBy { it.score }.score.apply { minScore = this }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (Reindeer maze)") }

    // part 2: solutions: - / 467

    timeResult { // [M3 167.095667ms]
        findAllRoutes().filter { it.score == minScore }
            .flatMap { it.path }.toSet()
            .size
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (best seats)") }
}
