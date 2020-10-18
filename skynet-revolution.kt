import java.util.*

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val N = input.nextInt() // the total number of nodes in the level, including the gateways
    val L = input.nextInt() // the number of links
    val E = input.nextInt() // the number of exit gateways

    val adjacencyList = Array(N) {
        mutableSetOf<Node>()
    }
    val nodes = Array(N) { Node(it) }

    for (i in 0 until L) {
        val N1 = input.nextInt() // N1 and N2 defines a link between these nodes
        val N2 = input.nextInt()
        adjacencyList[N1].add(nodes[N2])
        adjacencyList[N2].add(nodes[N1])
    }

    for (i in 0 until E)
        nodes[input.nextInt()].exit = true
    val exits: Set<Node> = nodes.filter { it.exit }.toHashSet()

    while (true) {
        val SI = input.nextInt() // The index of the node on which the Skynet agent is positioned this turn
        val selectedPath = findExitPaths(nodes[SI], adjacencyList, exits)
        cut(selectedPath.nodes.elementAt(selectedPath.nodes.size - 1), selectedPath.nodes.elementAt(selectedPath.nodes.size - 2), adjacencyList)
    }
}

data class SinglePath(val origin: Node, val goal: Node, val nodes: Set<Node> = setOf())

fun findExitPaths(origin: Node, adjacencyList: Array<MutableSet<Node>>, exits: Set<Node>): SinglePath {
    val candidates = exits.map { exit ->
        travelPathToExit(origin, Array(adjacencyList.size) { false }, exit, adjacencyList)
    }.filter { it.nodes.isNotEmpty() }               // no path to go there

    val urgentBlockItNOOOOOOOOOW = candidates.filter { it.nodes.size == 2 }
    if (urgentBlockItNOOOOOOOOOW.isNotEmpty()) {
        return urgentBlockItNOOOOOOOOOW.first()
    }

    val thoseDoubleExitsAreAnnoying = candidates
            .filter { path ->
                path.nodes.any { node ->
                    adjacencyList[node.index].count { it.exit } >= 2
                }
            }

    if (thoseDoubleExitsAreAnnoying.isNotEmpty()) {
        val doubleCandidate = thoseDoubleExitsAreAnnoying.sortedWith(compareBy(
                { path -> // get the closest double exit
                    path.nodes.indexOfFirst { node ->
                        adjacencyList[node.index].any { it.exit }
                    }
                }, { path -> // get shortest path if they are equal
                    path.nodes.size
                }
        )).first()
        return doubleCandidate
    }

    return candidates.minBy { it.nodes.size }!!
}

fun travelPathToExit(origin: Node, visited: Array<Boolean>, goal: Node, adjacencyList: Array<MutableSet<Node>>): SinglePath {
    var depth = 0
    var pathToCandidates: List<Pair<Set<Node>, Set<Node>>> = listOf(setOf(origin) to adjacencyList[origin.index])
    while (depth <= 10 && pathToCandidates.isNotEmpty()) {
        // every current origin
        pathToCandidates.forEach { ptc ->
            // has the goal adjacent
            if (ptc.second.any { it == goal })
                return SinglePath(origin, goal, ptc.first.plus(goal))
            // well at least it's been tried
            else {
                ptc.first.forEach {
                    visited[it.index] = true
                }
            }
        }
        // override for the next cycle
        pathToCandidates = pathToCandidates.flatMap { ptc ->
            ptc.second.map { neighbor ->
                val toVisitFromHere = adjacencyList[neighbor.index].filter { !visited[neighbor.index] }.toMutableSet()
                Pair(ptc.first.plus(neighbor), toVisitFromHere)
            }
        }
        depth++
    }
    return SinglePath(origin, goal)
}

fun cut(a: Node, b: Node, adjacencyList: Array<MutableSet<Node>>) {
    adjacencyList[a.index].remove(b)
    adjacencyList[b.index].remove(a)
    println("$a $b")
}

data class Node(val index: Int, var exit: Boolean = false, var dstFromExit: Int = Int.MAX_VALUE) {

    override fun toString(): String {
        return "$index"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return index
    }
}
