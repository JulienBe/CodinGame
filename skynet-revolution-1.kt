import java.util.*
import java.io.*
import java.math.*

fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val N = input.nextInt() // the total number of nodes in the level, including the gateways
    val L = input.nextInt() // the number of links
    val E = input.nextInt() // the number of exit gateways

    for (i in 0 until L) {
        val N1 = input.nextInt() // N1 and N2 defines a link between these nodes
        val N2 = input.nextInt()
    }
    for (i in 0 until E) {
        val EI = input.nextInt() // the index of a gateway node
    }

    // game loop
    while (true) {
        val SI = input.nextInt() // The index of the node on which the Skynet agent is positioned this turn

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");


        // Example: 0 1 are the indices of the nodes you wish to sever the link between
        println("0 1")
    }
}
