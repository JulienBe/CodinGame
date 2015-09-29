import scala.collection.mutable.ListBuffer
import scala.util._

case class Pod(var pos: Cell, var backInTimeLeft: Int = 0) {

  var previous = pos

  def update(ints: Array[Int]) = {
    previous = pos
    pos = Map.getCell(ints(0), ints(1))
    backInTimeLeft = ints(2)
  }
}

case class Cell(var status: Int, val x: Int, val y: Int, var attraction: Float) {
  def computeValue = {
    attraction = -Random.nextFloat
    if (isFree) {
      attraction += 20
      attraction += Map.numberOfSurrounding(this, Status.mine)
    }
  }
  def isFree = status == Status.free
  def isMine = status == Status.mine
}

trait Behavior {
  def act(me: Pod): Behavior
}

class Random extends Behavior {
  def act(me: Pod) = {
    val destination = Map.target(me)
    println(destination.x + " " + destination.y) // action: "x y" to move or "BACK rounds" to go back in time
    new Straight
  }
}
class Turn extends Behavior {

  def act(me: Pod): Behavior = {
    val destination = Map.getTurn(me)
    for (c <- destination)
      if (c.attraction > 0) {
        println(c.x + " " + c.y)
        return new Straight
      }
    return new Random().act(me)
  }
}
class Straight extends Behavior {

  var turns = 0

  def act(me: Pod): Behavior = {
    turns += 1
    val destination = Map.getStraight(me)
    destination match {
      case Some(c: Cell)  => mightChange(me, c)
      case _              => new Random().act(me)
    }
  }

  def mightChange(me: Pod, target: Cell): Behavior = {
    if (target.isFree && turns < 10 - Player.opponentcount * 2) {
      println(target.x + " " + target.y)
      this
    } else {
      new Turn().act(me)
    }
  }
}

object Player extends App {
  val random = Random
  val opponentcount = readInt
  val myPod = new Pod(Map.getCell(0, 0), 0)
  val enemies = new Array[Pod](opponentcount)

  var behavior: Behavior = new Straight

  for (i <- 0 until opponentcount)
    enemies(i) = new Pod(Map.getCell(0, 0), 0)

  while(true) {
    val gameround = readInt

    myPod.update(for(i <- readLine split " ") yield i.toInt)

    for(i <- 0 until opponentcount)
      enemies(0).update(for(i <- readLine split " ") yield i.toInt)

    for(i <- 0 until 20)
      Map.update(readLine, i)

    behavior = behavior.act(myPod)
  }
}

object Status {
  val free = '.'.toInt
  val mine = '0'.toInt
  val p1 = '1'.toInt
  val p2 = '2'.toInt
  val p3 = '3'.toInt
}
// One line of the map ('.' = free, '0' = you, otherwise the id of the opponent)
object Map {
  val rep: Array[Array[Cell]] = makeGrid(Array.ofDim[Cell](35, 20))

  def makeGrid(cells: Array[Array[Cell]]) = {
    for (x <- 0 until cells.length)
      for (y <- 0 until cells(0).length)
        cells(x)(y) = new Cell(Status.free, x, y, 0)
    cells
  }

  def numberOfSurrounding(c: Cell, id: Int) = {
    var rep = 0
    if (hasLeft(c) && getLeftBorder(c) == id)    rep += 1
    if (hasRight(c) && getRightBorder(c) == id)   rep += 1
    if (hasBottom(c) && getUpBorder(c) == id)      rep += 1
    if (hasTop(c) && getDownBorder(c) == id)    rep += 1
    rep
  }

  def getCell(x: Int, y: Int) = rep(x)(y)
  def isMine(x: Int, y: Int) = isValid(x, y) && getCell(x, y).isMine
  def isValid(x: Int, y: Int) = x >= 0 && y >= 0 && x < rep.length && y < rep(0).length

  def getTurn(pod: Pod): List[Cell] = {
    var values = ListBuffer[Cell]()

    // that's ugly
    val targetX = pod.pos.x + (pod.pos.y - pod.previous.y)
    val targetY = pod.pos.y + (pod.pos.x - pod.previous.x)
    if (isValid(targetX, targetY))  values += getCell(targetX, targetY)
    // and uglier
    val targetX2 = pod.pos.x - (pod.pos.y - pod.previous.y)
    val targetY2 = pod.pos.y - (pod.pos.x - pod.previous.x)
    if (isValid(targetX2, targetY2))  values += getCell(targetX2, targetY2)
    values = orderList(values)
    values.toList
  }

  def getStraight(pod: Pod): Option[Cell] = {
    val targetX = pod.pos.x + (pod.pos.x - pod.previous.x)
    val targetY = pod.pos.y + (pod.pos.y - pod.previous.y)

    if (isValid(targetX, targetY))    Some(getCell(targetX, targetY))
    else                              None
  }

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

  def target(pod: Pod) = {
    var cells = List[Cell]()
    var range = 1
    val current = pod.pos
    do {
      cells = getCellsOnRange(current, range).filter(_.isFree).filter(_ != pod.previous)
      range += 1
    } while (cells.length == 0)
    cells(0)
  }
  def getCellsOnRange(center: Cell, range: Int) = {
    var list = ListBuffer[Cell]()
    for (x <- center.x - range to center.x + range) {
      if (isValid(x, center.y + range)) list += getCell(x, center.y + range)
      if (isValid(x, center.y - range)) list += getCell(x, center.y - range)
    }
    for (y <- center.y - range + 1 until center.y + range) {
      if (isValid(center.x + range, y)) list += getCell(center.x + range, y)
      if (isValid(center.x - range, y)) list += getCell(center.x - range, y)
    }
    list.toList
  }
  def borders(id: Int, count: Int, cell: Cell) = numberOfSurrounding(cell, id) >= count

  def orderList(list: ListBuffer[Cell]) = {
    list.foreach(_.computeValue)
    list.sortBy(- _.attraction)
  }

  def update(line: String, lineIndex: Int) = {
    for (i <- 0 until line.length)
      rep(i)(lineIndex).status = line.charAt(i)
  }

  def getLeftBorder(cell: Cell) = getHorizontalBorder(cell, -1, cell.x - 1, 0)
  def getRightBorder(cell: Cell) = getHorizontalBorder(cell, 1, cell.x + 1, rep.length - 1)
  def getUpBorder(cell: Cell) = getVerticalBorder(cell, -1, cell.y + 1, 0)
  def getDownBorder(cell: Cell) = getVerticalBorder(cell, 1, cell.y - 1, rep(0).length - 1)

  def getHorizontalBorder(cell: Cell, inc: Int, init: Int, end: Int): Int = {
    for (i <- init to end by inc)
      if (!rep(i)(cell.y).isFree)
        return rep(i)(cell.y).status
    return Status.free
  }

  def getVerticalBorder(cell: Cell, inc: Int, init: Int, end: Int): Int = {
    for (i <- init to end by inc)
      if (!rep(cell.x)(i).isFree)
        return rep(cell.x)(i).status
    return Status.free
  }
}