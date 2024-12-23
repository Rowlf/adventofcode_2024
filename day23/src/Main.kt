// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

const val day = 23

fun main() {
    println("Day $day\n-----\n")
    // fetchAoCInputIfNeeded(day)

    val examples = listOf(
// 1: ..
        """
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
""",
    )

    val example = 0
    val inputData = if (example==0) linesOf(day = day) else linesOf(data = examples[example-1])

    inputData.print(indent = 2, description = "input lines:", take = 2)

    var connections = inputData.map { line -> line.split("-").let { it[0] to it[1] } }
    val network = mutableMapOf<String, MutableSet<String>>().apply {
        connections.forEach { (a, b) ->
            getOrPut(a) { mutableSetOf() }.add(b)
            getOrPut(b) { mutableSetOf() }.add(a)
        }
    }

    // part 1: solutions: 7 / 1302

    timeResult { // [M3 32.112750ms]
        val triangles = mutableSetOf<List<String>>()
        network.keys.forEach { k1 ->
            (network[k1]!! - k1).forEach { k2 ->
                (network[k2]!! - k1 - k2).filter{k1 in network[it]!!}.forEach { k3 ->
                    listOf(k1, k2, k3).run {
                        if (any { it.startsWith("t") }) triangles.add(sorted())
                    }
                }
            }
        }
        triangles.size
    }.let { (dt,result) -> println("[part 1] result: $result, dt: $dt (connected triangles)") }

    // part 2: solutions: [co, de, ka, ta] / [cb, df, fo, ho, kk, nw, ox, pq, rt, sf, tq, wi, xz]

    // task is to find cliques, see Bron+Kerbosch
    fun findLargestClique(current: Set<String>, candidates: Set<String>, excluded: Set<String>): Set<String> {
        if (candidates.isEmpty() && excluded.isEmpty()) { return current }

        val pivot = (candidates + excluded).first()
        var largest = emptySet<String>()
        (candidates - network[pivot]!!).forEach { node -> // non Neighbors
            val neighbors = network[node]!!
            val candidateClique = findLargestClique(current + node,
                candidates.intersect(neighbors), excluded.intersect(neighbors))
            if (candidateClique.size > largest.size) {
                largest = candidateClique
            }
        }
        return largest
    }

    timeResult { // [M3 26.444750ms]
        findLargestClique(mutableSetOf(),network.keys.toMutableSet(),mutableSetOf())
            .sorted()
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (largest clique)") }
}
