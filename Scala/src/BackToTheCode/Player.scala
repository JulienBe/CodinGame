import math._
import scala.util._

object Player extends App {
  val opponentcount = readInt
  val myPod = new Pod(0, 0, 0)
  val enemies = new Array[Pod](opponentcount)
  for (i <- 0 until opponentcount)
    enemies(i) = new Pod
  val map: Map = new Map

  while(true) {
    val gameround = readInt

    myPod.update(for(i <- readLine split " ") yield i.toInt)

    for(i <- 0 until opponentcount)
      enemies(0).update(for(i <- readLine split " ") yield i.toInt)

    for(i <- 0 until 20)
      map.update(readLine, i)

     println("17 10") // action: "x y" to move or "BACK rounds" to go back in time
  }
}
case class Pod(var x: Int = 0, var y: Int = 0, var backInTimeLeft: Int = 0) {
  def update(ints: Array[Int]) = {
    x = ints(0)
    y = ints(1)
    backInTimeLeft = ints(2)
  }
}
// One line of the map ('.' = free, '0' = you, otherwise the id of the opponent)
case class Map(val rep: Array[String] = new Array[String](20)) {

  def update(readLine: String, line: Int) = {
    rep(line) = readLine
  }

}