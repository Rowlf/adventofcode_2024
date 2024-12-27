// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import java.util.PriorityQueue

class Graph<T>() {
    companion object

    val connections = mutableMapOf<T, MutableMap<T,Long>>()

    val nodes get(): Set<T> = connections.keys
    fun contains(node: T): Boolean = connections.containsKey(node)

    fun neighbors(node: T): Map<T,Long> = connections[node]!!

    fun add(node: T) {
        connections.putIfAbsent(node, mutableMapOf())
    }

    fun remove(node: T) {
        connections.remove(node)?.forEach { neighbor -> connections[neighbor.key]?.remove(node) }
    }

    fun add(from: T, to: T, weight: Long = 1) {
        add(from)
        add(to)
        connections[from]?.put(to, weight)
        connections[to]?.put(from, weight)
    }

    fun remove(from: T, to: T) {
        connections[from]?.remove(to)
        connections[to]?.remove(from)
    }
}

fun <T> Graph.Companion.ofNodes(vararg nodes: T) = Graph<T>().apply {
    nodes.forEach { add(it) }
}

fun <T> Graph.Companion.ofUnweightedMap(map: Map<T,Set<T>>) = Graph<T>().apply {
    connections.clear()
    map.forEach { (node, edges) ->
        add(node)
        edges.forEach { neighbor -> add(node, neighbor) }
    }
}

fun <T> Graph.Companion.ofWeightedMap(map: Map<T,Map<T, Long>>) = Graph<T>().apply {
    connections.clear()
    map.forEach { (node, edges) ->
        add(node)
        edges.forEach { (neighbor, weight) -> add(node, neighbor, weight) }
    }
}

fun <T> Map<T,Set<T>>.toUnweightedGraph() = Graph.ofUnweightedMap(this)
fun <T> Map<T,Map<T, Long>>.toWeightedGraph() = Graph.ofWeightedMap(this)

data class ShortestPaths<T>(val start:T, val distances: Map<T, Long>, val predecessors: Map<T,T?>)

fun <T> Graph<T>.findShortestPaths(start: T, cutoff: Long = Long.MAX_VALUE): ShortestPaths<T>
= findShortestPathsDijkstra(start, cutoff)

// Dijkstra
fun <T> Graph<T>.findShortestPathsDijkstra(start: T, cutoff: Long = Long.MAX_VALUE): ShortestPaths<T> {
    val distances = mutableMapOf<T, Long>()
    val predecessors = mutableMapOf<T,T?>()
    val priorityQueue = PriorityQueue<Pair<T, Long>>(compareBy { it.second })

    distances[start] = 0L
    priorityQueue.add(Pair(start, 0L))

    while (priorityQueue.isNotEmpty()) {
        val (current, distance) = priorityQueue.poll()

        if (distance > cutoff) continue
        if (distance > distances.getOrDefault(current,Long.MAX_VALUE)) continue

        connections[current]!!.forEach { (neighbor, weight) ->
            val newDistance = distance + weight
            if (newDistance <= cutoff && newDistance < distances.getOrDefault(neighbor,Long.MAX_VALUE)) {
                distances[neighbor] = newDistance
                priorityQueue.add(Pair(neighbor, newDistance))
                predecessors[neighbor] = current
            }
        }
    }
    return ShortestPaths(start, distances, predecessors)
}

fun <T> Graph<T>.extractPath(shortestPaths: ShortestPaths<T>, target: T): List<T> {
    val path = mutableListOf<T>()
    var current: T? = target
    while (current != null) {
        path.add(current)
        current = shortestPaths.predecessors[current]
    }
    return if (path.lastOrNull() != shortestPaths.start) emptyList() else path.reversed()
}

fun <T> Graph<T>.isConnected(start: T, target:T)
= isConnectedBFS(start, target)

// Breadth-First Search BFS
fun <T> Graph<T>.isConnectedBFS(start: T, target:T): Boolean {
    val visited = mutableSetOf<T>()
    val queue = ArrayDeque<T>()
    queue.add(start)

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        if (node == target) return true
        if (node !in visited) {
            visited.add(node)
            neighbors(node).keys.forEach { n -> queue.add(n) }
        }
    }
    return false
}

fun <T> Graph<T>.isConnectedDFS(start: T, target: T, visited: MutableSet<T> = mutableSetOf()): Boolean {
    if (start == target) return true
    if (start in visited) return false

    visited.add(start)
    return neighbors(start).keys.any { isConnectedDFS(it, target, visited) }
}

fun <T> Graph<T>.isConnectedDFSIterative(start: T, target: T): Boolean {
    val visited = mutableSetOf<T>()
    val stack = ArrayDeque<T>()
    stack.addFirst(start)

    while (stack.isNotEmpty()) {
        val node = stack.removeFirst()
        if (node == target) return true
        if (node !in visited) {
            visited.add(node)
            neighbors(node).keys.reversed().forEach { n -> stack.addFirst(n) }
        }
    }
    return false
}

fun <T> Graph<T>.minimalSteps(start: T, target:T): Int {
    val visited = mutableSetOf<T>()
    val queue = ArrayDeque<Pair<T,Int>>()
    queue.add(start to 0)

    while (queue.isNotEmpty()) {
        val (node,len) = queue.removeFirst()
        if (node == target) return len
        if (node !in visited) {
            visited.add(node)
            neighbors(node).keys.forEach { n -> queue.add(n to len+1) }
        }
    }
    return -1
}

fun <T> Graph<T>.findAllShortestPaths(start: T, target: T): List<List<T>> {
    val shortestPaths = mutableListOf<List<T>>()
    val queue = ArrayDeque<List<T>>()
    val distances = mutableMapOf<T, Long>()

    queue.add(listOf(start))
    distances[start] = 0

    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()
        val current = path.last()

        if (current == target) {
            shortestPaths.add(path)
            continue
        }

        neighbors(current).forEach { (neighbor, weight) ->
            val newDistance = distances[current]!! + weight

            if (neighbor !in distances || newDistance == distances[neighbor]) {
                distances[neighbor] = newDistance
                queue.add(path + neighbor)
            }
        }
    }

    return shortestPaths
}
