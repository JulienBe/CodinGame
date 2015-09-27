import scala.collection.mutable.ListBuffer
import scala.util._

object Player extends App {
  val random = Random
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

    val destination = map.target(map.rep(myPod.x)(myPod.y))
    Console.err.println("Goto : " + destination.x + "," + destination.y)
    println(destination.x + " " + destination.y) // action: "x y" to move or "BACK rounds" to go back in time
  }
}

object Status {
  val free = '.'.toInt
  val mine = '0'.toInt
  val p1 = '1'.toInt
  val p2 = '2'.toInt
  val p3 = '3'.toInt
}

object Builder {
  def makeGrid(cells: Array[Array[Cell]]) = {
    for (x <- 0 until cells.length)
      for (y <- 0 until cells(0).length)
        cells(x)(y) = new Cell(Status.free, x, y, 0)
    cells
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
case class Map(val rep: Array[Array[Cell]] = Builder.makeGrid(Array.ofDim[Cell](35, 20))) {

  def getCanditates(c: Cell) = {
    var candidates = ListBuffer[Cell]()
    if (hasLeft(c))   candidates += rep(c.x-1)(c.y)
    if (hasRight(c))  candidates += rep(c.x+1)(c.y)
    if (hasTop(c))    candidates += rep(c.x)(c.y-1)
    if (hasBottom(c)) candidates += rep(c.x)(c.y+1)
    candidates
  }

  def hasLeft(c: Cell)    = c.x > 0
  def hasRight(c: Cell)   = c.x < 35 - 1
  def hasTop(c: Cell)     = c.y > 0
  def hasBottom(c: Cell)  = c.y < 20 - 1

  def target(current: Cell) = {
    val candidates = getCanditates(current)
    candidates.foreach(c => c.computeValue)
    val sorted = candidates.sortBy(- _.attraction)
    sorted(0)
  }

  def update(line: String, lineIndex: Int) = {
    for (i <- 0 until line.length)
      rep(i)(lineIndex).status = line.charAt(i)
  }

}

case class Cell(var status: Int, val x: Int, val y: Int, var attraction: Float) {

  def computeValue = {
    attraction = Random.nextFloat
    if (status == Status.free)
      attraction += 1
  }
}
