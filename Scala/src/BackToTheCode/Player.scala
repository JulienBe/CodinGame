import math._
import scala.util._

object Player extends App {
  val opponentcount = readInt // Opponent count

  // game loop
  while(true) {
    val gameround = readInt
    // x: Your x position
    // y: Your y position
    // backintimeleft: Remaining back in time
    val Array(x, y, backintimeleft) = for(i <- readLine split " ") yield i.toInt
    for(i <- 0 until opponentcount) {
      // opponentx: X position of the opponent
      // opponenty: Y position of the opponent
      // opponentbackintimeleft: Remaining back in time of the opponent
      val Array(opponentx, opponenty, opponentbackintimeleft) = for(i <- readLine split " ") yield i.toInt
    }
    for(i <- 0 until 20) {
      val line = readLine // One line of the map ('.' = free, '0' = you, otherwise the id of the opponent)
    }

    // Write an action using println
    // To debug: Console.err.println("Debug messages...")

    println("17 10") // action: "x y" to move or "BACK rounds" to go back in time
  }
}