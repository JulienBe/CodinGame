import java.util.*

/**
 * In this game you are a program driving the legendary tron light cycle and fighting against other programs on the game grid.
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
    val grid = Grid(30, 20)
    val input = Scanner(System.`in`)
    val playerHistory = mutableListOf<Array<Player>>()
    var turn = 0
    // game loop
    while (true) {
        val N = input.nextInt() // total number of players (2 to 4).
        val myId = input.nextInt()
        val players: Array<Player> = Array(N) {
            val start = Pos(input.nextInt(), input.nextInt())
            val current = Pos(input.nextInt(), input.nextInt())
            val previous = playerHistory
                .getOrElse(turn - 1) { arrayOf() }
                .getOrElse(it) { Player(myId, current, Direction.NONE) }.pos
            if (current != Pos.DEAD)
                grid.update(current, it)
            else if (previous != Pos.DEAD)
                grid.playerDied(it)
            Player(it, current, Direction.guess(current, previous))
        }
        playerHistory.add(players)
        players[myId].direction = computeNewDir(grid, players, myId)
        println(players[myId].direction)
        turn++
    }
}

fun computeNewDir(grid: Grid, players: Array<Player>, id: Int): Direction {
    // Just avoid the borders
    val me = players[id]
    val dirsToTry: Array<Direction> = me.direction.andAdjacent()
    val otherPlayers = players
        .filter { it.id != id }
        .map { grid.computeDistances(it.pos) }

    dirsToTry.sortBy { -grid.closestCellsValue(it, me.pos, otherPlayers) }
    val newDir: Direction? = dirsToTry.firstOrNull {
        grid.free(me.pos + it)
    }
    return newDir ?: me.direction
}

data class Player(val id: Int, val pos: Pos, var direction: Direction)
data class Cell(var playerId: Int)

data class DistanceMap(val width: Int, val height: Int, val distances: Array<Array<Int>> = Array(height) { Array(width) { Int.MAX_VALUE } }) {
    fun get(pos: Pos) = distances[pos.y][pos.x]
    fun set(pos: Pos, value: Int) {
        distances[pos.y][pos.x] = value
    }

    fun distance(index: Int): Int {
        val x = index % width
        val y = index / width
        return distances[y][x]
    }
}

data class Grid(val width: Int, val height: Int) {

    private val grid = Array(height) { Array(width) { Cell(NO_PLAYER) } }

    private fun withinBound(pos: Pos) = pos.x in 0 until width && pos.y in 0 until height

    fun free(pos: Pos) = withinBound(pos) && grid[pos.y][pos.x].playerId == NO_PLAYER

    fun update(current: Pos, id: Int) {
        grid[current.y][current.x].playerId = id
    }
    fun get(pos: Pos) = grid[pos.y][pos.x]

    fun computeDistances(playerPos: Pos): DistanceMap {
        val distances = DistanceMap(width, height)

        val visitedCells = mutableSetOf<Cell>()
        val posToMeasure = Direction.moves
            .map { playerPos + it }
            .filter { free(it) }
            .associateWith { 1 }.toMutableMap()
        while (posToMeasure.isNotEmpty()) {
            val toEval = posToMeasure.entries.first()
            val cell = get(toEval.key)
            // Did I already found a better path to it ?
            if (visitedCells.contains(cell) && distances.get(toEval.key) <= toEval.value) {
                posToMeasure.remove(toEval.key)
                continue
            }
            distances.set(toEval.key, toEval.value)
            visitedCells.add(cell)
            posToMeasure.remove(toEval.key)

            Direction.moves
                .map { toEval.key + it }
                .filter { free(it) }
                .forEach { posToMeasure[it] = toEval.value + 1 }
        }
        return distances
    }

    /**
     * Compute the number of cells that the player would be closest to then other players
     */
    fun closestCellsValue(dir: Direction, pos: Pos, otherPlayers: List<DistanceMap>): Float {
        val myDistanceMap = computeDistances(pos + dir)
        return myDistanceMap.distances
            .flatten()
            .filterIndexed { index, dst ->
                otherPlayers.all { it.distance(index) > dst }
            }.count().toFloat()
    }

    fun playerDied(id: Int) {
        grid.forEach { row ->
            row.forEach { cell ->
                if (cell.playerId == id)
                    cell.playerId = NO_PLAYER
            }
        }
    }
    companion object {
        const val NO_PLAYER = -1
    }
}

data class Pos(val x: Int, val y: Int) {
    operator fun plus(direction: Direction): Pos = Pos(x + direction.x, y + direction.y)
    companion object {
        val DEAD = Pos(-1, -1)
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), NONE(0, 0);

    fun andAdjacent(): Array<Direction> {
        return when (this) {
            UP, DOWN -> arrayOf(this, LEFT, RIGHT)
            LEFT, RIGHT -> arrayOf(this, UP, DOWN)
            NONE -> arrayOf(UP, DOWN, LEFT, RIGHT)
        }
    }

    companion object {
        val moves = arrayOf(UP, DOWN, LEFT, RIGHT)
        fun guess(current: Pos, previous: Pos): Direction {
            val diffX = (current.x - previous.x).coerceIn(-1, 1)
            val diffY = (current.y - previous.y).coerceIn(-1, 1)
            return values().firstOrNull { it.x == diffX && it.y == diffY } ?: NONE
        }
    }
}