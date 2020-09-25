import java.util.*
import java.io.*
import java.math.*

fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val N = input.nextInt() // the total number of nodes in the level, including the gateways
    val L = input.nextInt() // the number of links
    val E = input.nextInt() // the number of exit gateways

    val nodes = Array<Node>(N) { Node(it) }
    for (i in 0 until L) {
        val N1 = input.nextInt() // N1 and N2 defines a link between these nodes
        val N2 = input.nextInt()
        nodes[N1].connections.add(N2)
        nodes[N2].connections.add(N1)
    }
    for (i in 0 until E) {
        val EI = input.nextInt() // the index of a gateway node
        nodes[EI].exit = true
    }

    // game loop
    while (true) {
        val SI = input.nextInt() // The index of the node on which the Skynet agent is positioned this turn

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");

        val nodeA =
            nodes.firstOrNull { it.exit && it.connections.isNotEmpty() } ?:
            nodes.first { it.connections.isNotEmpty() }
        val nodeB = nodes[nodes[nodeA.index].connections[0]]
        cut(nodeA, nodeB)
    }

}
fun cut(a: Node, b: Node) {
    a.connections.remove(b.index)
    b.connections.remove(a.index)
    println("$a $b")
}

data class Node(val index: Int, var exit: Boolean = false, val connections: MutableList<Int> = mutableListOf<Int>()) {
    override fun toString(): String {
        return "$index"
    }
}
