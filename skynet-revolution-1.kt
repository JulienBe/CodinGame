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

    for (i in 0 until E) {
        val exit = input.nextInt()
        nodes[exit].exit = true
    }
    val exits: Set<Node> = nodes.filter { it.exit }.toHashSet()


    while (true) {
        val SI = input.nextInt() // The index of the node on which the Skynet agent is positioned this turn
        val exitPaths = findExitPaths(nodes[SI], adjacencyList, exits)

        exitPaths.forEach {
            System.err.println(it)
        }

        cut(exitPaths.first().nodes.elementAt(0), exitPaths.first().nodes.elementAt(1), adjacencyList)
    }
}

data class Path(val origin: Node, val goal: Node, val nodes: MutableSet<Node> = mutableSetOf()) {
    init {
        nodes.add(origin)
    }
}

fun findExitPaths(origin: Node, adjacencyList: Array<MutableSet<Node>>, exits: Set<Node>): List<Path> {
    System.err.println("Search exits for ${origin.index}")
    return exits.map { exit ->
        travelPathToExit(Path(origin, exit), origin, 0, Array(adjacencyList.size) { false }, exit, adjacencyList)
    }.sortedBy { it.nodes.size }
}

fun travelPathToExit(path: Path, origin: Node, currentDepth: Int, visited: Array<Boolean>, goal: Node, adjacencyList: Array<MutableSet<Node>>): Path {
    if (currentDepth > 10)
        return path

    visited[origin.index] = true
    path.nodes.add(origin)
    val neighbors = adjacencyList[origin.index]

    if (neighbors.any { it == goal }) {
        path.nodes.add(goal)
        return path
    }
    val toVisitNext = neighbors.filter { !visited[it.index] }
    neighbors.forEach {
        visited[it.index] = true
    }
    val paths = toVisitNext.map {
        travelPathToExit(path, it, currentDepth + 1, visited, goal, adjacencyList)
    }
    return if (paths.isEmpty())
        path
    else
        paths.first()
}

fun cut(a: Node, b: Node, adjacencyList: Array<MutableSet<Node>>) {
    println("$a $b")
    adjacencyList[a.index].remove(b)
    adjacencyList[b.index].remove(a)
}

data class Node(val index: Int, var exit: Boolean = false, var dstFromExit: Int = Int.MAX_VALUE) {
    override fun toString(): String {
        return "$index"
    }
}
