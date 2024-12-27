// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 18

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
""",
    )

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])
    val dims = if (example==0) Position(71,71) else Position(7,7)
    val down = if (example==0) 1024 else 12

    inputData.print(indent = 2, description = "input lines:", take = 2)

    val falling = inputData.map { it.split(',').map { it.toInt() } }

    fun minimalSteps(simDown: Int): Int
    = RCGrid<Char>(rows = dims.row, cols = dims.col) { pos -> '.' }.run {
        falling.take(simDown).forEach { (x,y) -> this[y,x] = '#' }
        toGraph().minimalSteps(RCPos(0,0), RCPos(dims.row-1,dims.col-1))
    }

    // part 1: solutions: 22 / 226

    timeResult { // [M3 37.279542ms]
        minimalSteps(down)
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (minimum number of steps)") }

    // part 2: solutions: 6,1 / 60,46

    timeResult { // [M3 28.305125ms]
        var (left,right) = down+1 to falling.size-1     // bin search
        var res = right
        while (left <= right) {
            val mid = (right + left) / 2
            val steps = minimalSteps(mid)
            if (steps==-1) { res=mid; right = mid-1 } else { left = mid+1 }
        }
        falling[res-1].let { (x,y) -> "${x},${y}" }
    }.let { (dt,result) -> println("[part 2] result: '$result', dt: $dt (coordinates of the first byte)") }
}
