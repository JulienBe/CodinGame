import java.util.*
import java.io.*
import java.math.*
import kotlin.coroutines.*

/**
 * In this game your are a program driving the legendary tron light cycle and fighting against other programs on the game grid.
 *
 * The light cycle moves in straight lines and only turn in 90° angles while leaving a solid light ribbon in its wake.
 * Each cycle and associated ribbon features a different color.
 * Should a light cycle stop, hit a light ribbon or goes off the game grid it will be instantly deactivated.
 *
 * The last cycle in play wins the game.
 *
 * Your goal is to be the best program:
 *  once sent to the arena, programs will compete against each-others in battles gathering 2 to 4 cycles.
 *  The more battles you win, the better your rank will be.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {
        val N = input.nextInt() // total number of players (2 to 4).
        val P = input.nextInt() // your player number (0 to 3).
        for (i in 0 until N) {
            val X0 = input.nextInt() // starting X coordinate of lightcycle (or -1)
            val Y0 = input.nextInt() // starting Y coordinate of lightcycle (or -1)
            val X1 = input.nextInt() // starting X coordinate of lightcycle (can be the same as X0 if you play before this player)
            val Y1 = input.nextInt() // starting Y coordinate of lightcycle (can be the same as Y0 if you play before this player)
        }

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");

        println("LEFT") // A single line with UP, DOWN, LEFT or RIGHT
    }
}