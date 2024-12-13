// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 13

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279
""",
)

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val regex = """X[+=](-?\d+), Y[+=](-?\d+)""".toRegex()
    fun Regex.findCoefficients(line:String) = this.find(line)!!.destructured.let { (x, y) -> arrayOf(x.toLong(), y.toLong()) }

    // This is actually linear algebra. But that's no reason not to include careless mistakes.
    // For example, in part 2 the large number should be added. No one, almost no one, multiplies it...

    class ClawMachine(val v1: Array<Long>, val v2: Array<Long>, val z: Array<Long>) {
        constructor(lines: Lines, offset: Long = 0L) : this(
            v1 = regex.findCoefficients(lines[0]),
            v2 = regex.findCoefficients(lines[1]),
            z = regex.findCoefficients(lines[2]).let { arrayOf(it[0]+offset,it[1]+offset) }
        )
        fun solve(): Triple<Boolean,Long,Long> {
            val det = (v1[0] * v2[1] - v2[0] * v1[1]).also { if (it==0L) return Triple(false, 0L, 0L) }

            val x = (v2[1] * z[0] - v2[0] * z[1]).toDouble() / det
            val y = (-v1[1] * z[0] + v1[0] * z[1]).toDouble() / det
            val xl = x.toLong()
            val yl = y.toLong()
            val eps = 1e-10
            return Triple(0 < x && 0 < y
                    && kotlin.math.abs(x - xl) < eps && kotlin.math.abs(y - yl) < eps,
                xl,yl)
        }
    }

    val blocks = inputData.toBlocks()

    // part 1: solution: 480 / 28753

    timeResult { // [M3 4.720500ms]
        blocks.sumOf { block ->
            ClawMachine(block).solve().let { (solvable, A, B) -> if (solvable) A*3+B*1 else 0L }
        }
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (claw machine)") }

    // part 2: solution: 875318608908 / 102718967795500

    timeResult { // [M3 578.209us]
        blocks.sumOf { block ->
            ClawMachine(block, offset = 10000000000000L).solve().let { (solvable, A, B) -> if (solvable) A*3+B*1 else 0L }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (with conversion error)") }
}
