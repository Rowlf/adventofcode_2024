// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import java.util.LinkedList

const val day = 24

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
x00: 1
x01: 1
x02: 1
y00: 0
y01: 1
y02: 0

x00 AND y00 -> z00
x01 XOR y01 -> z01
x02 OR y02 -> z02
""",
        // 2: ..
        """
x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj
"""
    )

    val example = 0
    val inputData = if (example == 0) linesOf(day = day) else linesOf(data = examples[example - 1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    data class Operation(val reg: String, var result: Int,
                         val op1: String = "", val op: String = "", val op2: String = "")

    val blocks = inputData.toBlocks()
    val nodes = mutableMapOf<String, Operation>()

    blocks[0].forEach { line ->
        // e.g. y04: 1
        val (reg, result) = line.split(": ").let { it[0] to it[1].toInt() }
        nodes[reg] = Operation(reg, result)
    }
    blocks[1].forEach { line ->
        // e.g. tnw OR pbm -> gnj
        val (reg, ops) = line.split(" -> ").let { it[1] to it[0].split(" ") }
        nodes[reg] = Operation(reg, -1, ops[0], ops[1], ops[2])
    }

    fun String.reg(i:Int) = "$this${String.format("%02d",i)}"

    val z00 = "z".reg(0)
    val z01 = "z".reg(1)
    var znn = ""

    // part 1: solutions: 4 / 47666458872582

    timeResult { // [M3 2.085916ms]
        val unknownRegisters = nodes.filter { it.value.result == -1 }
            .map { it.key }.toCollection(LinkedList())
        while (unknownRegisters.isNotEmpty()) {
            val node = nodes[unknownRegisters.poll()]!!
            val node1 = nodes[node.op1]!!
            val node2 = nodes[node.op2]!!
            when {
                node1.result < 0 || node2.result < 0 -> unknownRegisters.add(node.reg)
                node.op == "AND" -> node.result = node1.result and node2.result
                node.op == "OR" -> node.result = node1.result or node2.result
                node.op == "XOR" -> node.result = node1.result xor node2.result
            }
        }

        var idx = 0
        var result = ""
        while ("z".reg(idx) in nodes) {
            result = nodes["z".reg(idx)]!!.result.toString() + result
            ++idx
        }
        znn = "z".reg(idx-1)

        result.toLong(2)
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (collect bits)") }

    /*
        Now it gets interesting. A few people solved it by hand, more or less, so did I.
        First, check the add operation itself:

        x: 0 1 0 1 1 0 0 0 1 1 0 0 1 1 0 0 1 0 0 1 1 0 0 1 1 0 0 1 0 0 0 0 1 1 1 1 1 0 1 1 0 0 1 1 0 1
        y: 0 1 0 1 0 1 0 0 1 0 0 1 1 0 1 1 0 0 1 1 1 1 1 1 1 1 0 1 0 0 1 0 0 0 1 0 0 0 0 0 0 1 1 0 0 1

        z: 1 0 1 0 1 1 0 1 0 1 1 0 1 0 0 0 1 1 0 1 1 0 0 1 0 1 1 0 0 1 0 1 0 0 1 0 1 1 0 0 0 0 0 1 1 0
        suspicious:                33323130                          1615      1110  8 7 6 5

        In the equations one can find the starter:
             z5 wrong (sgt OR bhb -> z05)
            z15 wrong (y15 AND x15 -> z15)
            z30 wrong (kgr AND vrg -> z30)

        Second, check your input. The device models a Carry Ripple Adder, so there is a data flow
        like this:

        i:  carry bit (c_i)     base bit (b_i)     |    or bit (o_i)    |   and bit (a_i)    |  out_i

        2:  x1 AND y1 -> c2     x2 XOR y2 -> b2         a1 OR c2 -> o2      b2 AND o2 -> a2     o2 XOR b2 -> z2
        3:  x2 AND y2 -> c3     x3 XOR y3 -> b3         a2 OR c3 -> o3      b3 AND o3 -> a3     o3 XOR b3 -> z3

        My input...

        0:                                                                                      x00 XOR y00 -> z00      ok
        1:  y00 AND x00 -> rpj  y01 XOR x01 -> nsc                          rpj AND nsc -> gpt  rpj XOR nsc -> z01      ok
        2:  y01 AND x01 -> bkg  y02 XOR x02 -> mdw      gpt OR bkg -> gsh   mdw AND gsh -> vbm  gsh XOR mdw -> z02      ok

        and so on...

        4:  x03 AND y03 -> vvj  y04 XOR x04 -> bjc      vvj OR mkv -> tkj   bjc AND tkj -> rkg  bjc XOR tkj -> z04      ok
                                                        a4     c5     o5    o5      b5     a5
        5:  x04 AND y04 -> kff  y05 XOR x05 -> tvp      rkg OR kff -> ggh   ggh AND tvp -> bhb  sgt  OR bhb -> z05      z05 wrong
                                                                                                o5      b5
                                                                                  and there is: ggh XOR tvp -> jst
                                                        c6     a5     ???   b6      o6     a6   b6      o6              jst <-> z05
        6:  y05 AND x05 -> sgt  y06 XOR x06 -> vjh      sgt OR bhb -> z05   vjh AND jst -> vvg  vjh XOR jst -> z06

        investigated the same way...

                                                        a9     c10    o10   o9      ???    a10  ???     o10
        10: y09 AND x09 -> hrq  y10 XOR x10 -> gdf      mcc OR hrq -> tdw   tdw AND mcm -> pqq  mcm XOR tdw -> z10
                                                        c11    a10    o11   o11     b11         o11     b11             mcm <-> gdf
        11: x10 AND y10 -> mcm  x11 XOR y11 -> jsd      gdf OR pqq -> gvj   gvj AND jsd -> gpd  gvj XOR jsd -> z11

        15: y14 AND x14 -> hdb  x15 XOR y15 -> dvj      hdb OR rkf -> vhr   vhr AND dvj -> ckf  y15 AND x15 -> z15      z15 <-> dnt
        16: y15 AND x15 -> z15  x16 XOR y16 -> scq      dnt OR ckf -> jpj   jpj AND scq -> swj  jpj XOR scq -> z16
                                                                                   a29
        29: x28 AND y28 -> mtc  y29 XOR x29 -> fkb      dwd OR mtc -> fhn   fhn AND fkb -> nqs  fhn XOR fkb -> z29      z30 <-> gwc
        30: x29 AND y29 -> cfp  x30 XOR y30 -> vrg      cfp OR nqs -> kgr                       kgr AND vrg -> z30

        and finally...

        45: x44 AND y44 -> nng  (no x45,y45)                                fnc AND vrw -> mgq  mgq  OR nng -> z45      ok, highest bit

        Sorted: dnt, gdf, gwc, jst, mcm, z05, z15, z30

        From this scheme we can state some rules (not sure if they are complete and work for every input):
          - z00, z01 and z45 follow special rules (they are correct in my input data, so skip them)
          - XOR is a valid op only for the base bit, involving x and y, and for the result, determine z
          - z is always the result of XOR
          - a base bit is only part of XOR or AND, never OR
          - the result of AND always goes into OR

        With this we can filter the operations and collect any violation.
     */

    // part 2: solutions: - / 'dnt,gdf,gwc,jst,mcm,z05,z15,z30'

    timeResult { // [M3 4.600875ms]
        val z01Node = nodes[z01]!!
        val violated = mutableSetOf<String>()
        nodes.filter { it.key.let {
                (!it.startsWith("x") && !it.startsWith("y"))        // skip x and y
                        && (it!=z00)                                // skip z00
                        && (it!=z01Node.op1 && it!=z01Node.op2)     // skip ops of z01
                        && (it!=znn)                                // and skip last z
            } }
            .forEach { (reg,op) ->
                if ( (op.op=="XOR" && listOf(reg[0], op.op1[0], op.op2[0]).none { it in "xyz" })
                    || (op.op=="XOR" && nodes.values.find { (it.op1==reg || it.op2==reg) && it.op=="OR" }!=null)
                    || (op.op!="XOR" && reg.startsWith("z"))
                    || (op.op=="AND" && nodes.values.find { (it.op1==reg || it.op2==reg) && it.op!="OR" }!=null))
                    violated.add(reg)
        }
        violated.sorted().joinToString(",")
    }.let { (dt,result) -> println("[part 2] result: '$result', dt: $dt (Carry Ripple Adder)") }
}
