import java.util.*

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
    val grid = Grid(20, 30)
    val input = Scanner(System.`in`)

    // game loop
    while (true) {
        val N = input.nextInt() // total number of players (2 to 4).
        val myId = input.nextInt()
        val players: Array<Player> = Array(N) {
            val segmentOriginalX = input.nextInt() // starting X coordinate of lightcycle (or -1)
            val segmentOriginalY = input.nextInt() // starting Y coordinate of lightcycle (or -1)
            val currentX = input.nextInt()
            val currentY = input.nextInt()
            Player(it, Pos(currentX, currentY), Direction.guess(segmentOriginalX, segmentOriginalY, currentX, currentY))
        }
        players[myId].direction = computeNewDir(grid, players, myId)
        println(players[myId].direction)
    }
}

fun computeNewDir(grid: Grid, players: Array<Player>, id: Int): Direction {
    // Just avoid the borders
    val me = players[id]
    val dirsToTry = me.direction.andAdjacent()
    val newDir: Direction? = dirsToTry.firstOrNull {
        grid.isValid(me.pos + it)
    }
    return newDir ?: me.direction
}

data class Grid(val width: Int, val height: Int) {
    fun isValid(pos: Pos) = pos.x in 0 until width && pos.y in 0 until height
    val grid = Array(width) { Array(height) { -1 } }
}

data class Pos(val x: Int, val y: Int) {
    operator fun plus(direction: Direction): Pos = Pos(x + direction.x, y + direction.y)
}

enum class Direction(val x: Int, val y: Int) { UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);
    fun andAdjacent(): Array<Direction> {
        return when (this) {
            UP, DOWN -> arrayOf(this, LEFT, RIGHT)
            LEFT, RIGHT -> arrayOf(this, UP, DOWN)
        }
    }

    companion object {
        fun guess(segmentOriginalX: Int, segmentOriginalY: Int, currentX: Int, currentY: Int): Direction {
            val diffX = (segmentOriginalX - currentX).coerceIn(-1, 1)
            val diffY = (segmentOriginalY - currentY).coerceIn(-1, 1)
            if (diffX == 0 && diffY == 0)
                return values().random()
            return values().first { it.x == diffX && it.y == diffY }
        }
    }
}

data class Player(val id: Int, val pos: Pos, var direction: Direction) {
    fun nextPos() = pos + direction
}