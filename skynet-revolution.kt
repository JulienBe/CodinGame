import java.util.*

fun main(args : Array<String>) {
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
        val exitPaths = findExitPaths(nodes[SI], adjacencyList, exits)

        exitPaths.forEach {
            System.err.println("${it.goal} : $it")
        }
        val selectedPath = exitPaths.first().nodes
        cut(selectedPath.elementAt(selectedPath.size - 1), selectedPath.elementAt(selectedPath.size - 2), adjacencyList)
    }
}

data class SinglePath(val origin: Node, val goal: Node, val nodes: Set<Node> = setOf())

fun findExitPaths(origin: Node, adjacencyList: Array<MutableSet<Node>>, exits: Set<Node>): List<SinglePath> {
    val candidates = exits.map { exit ->
        travelPathToExit(origin, Array(adjacencyList.size) { false }, exit, adjacencyList)
    }.filter { it.nodes.isNotEmpty() }               // no path to go there

    val urgentBlockItNOOOOOOOOOW = candidates.filter { it.nodes.size == 2 }
    if (urgentBlockItNOOOOOOOOOW.isNotEmpty()) {
        System.err.println("URGENT")
        return urgentBlockItNOOOOOOOOOW
    }

    val thoseDoubleExitsAreAnnoying = candidates
            .filter { path ->
                path.nodes.any { node ->
                    adjacencyList[node.index].count { it.exit } >= 2 }
            }
    // That's used to get the closest with double exit. But apparently skynet would just target the closest exit ?
            /*.sortedWith(compareBy(
                { path ->
                    path.nodes.indexOfFirst { node ->
                        adjacencyList[node.index].count { it.exit } >= 2
                    }
                },
                    {it.nodes.size}
            ))*/

    if (thoseDoubleExitsAreAnnoying.isNotEmpty()) {
        System.err.println("ANNOYING")
        return thoseDoubleExitsAreAnnoying.sortedBy { path ->
            path.nodes.indexOfFirst { node ->
                adjacencyList[node.index].any { it.exit }
            }
        }
    }

    return candidates.sortedBy { it.nodes.size }
}

fun travelPathToExit(origin: Node, visited: Array<Boolean>, goal: Node, adjacencyList: Array<MutableSet<Node>>): SinglePath {
    var depth = 0
    var pathToCandidates: List<Pair<Set<Node>, Set<Node>>> = listOf ( setOf(origin) to adjacencyList[origin.index] )
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
