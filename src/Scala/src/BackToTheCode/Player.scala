import scala.collection.mutable.ListBuffer
import scala.util._

case class Pod(var pos: Cell, var backInTimeLeft: Int = 0) {
  var previous = pos

  def update(ints: Array[Int]) = {
    previous = pos
    if (Map.isValid(ints(0), ints(1)))
      pos = Map.getCell(ints(0), ints(1))
    backInTimeLeft = ints(2)
  }
}

case class Cell(var status: Int, val x: Int, val y: Int, var attraction: Float) {
  def computeValue(from: Cell) = {
    attraction = 0
    if (isFree) {
      attraction += 20
      attraction += Map.numberInThisDirUntil(this, from, Map.isntFree)
      Console.err.println(x +"," + y + " : value around straight : " + Map.valueAroundStraight(this))
      attraction += Map.valueAroundStraight(this)
    }
  }
  def isFree = status == Status.free
  def isMine = status == Status.mine
}

trait Behavior {
  def act(me: Pod): Behavior
  def speak(): String
}

class Random extends Behavior {
  def act(me: Pod) = {
    val destination = Map.target(me)
    Player.output(destination, speak)
    new Straight(Player.gameround)
  }
  def speak() = "Rand"
}

class Turn extends Behavior {
  def act(me: Pod): Behavior = {
    val destination = Map.getTurn(me).filter(_.isFree)
    Console.err.println(destination)
    for (c <- destination) {
      Player.output(c, speak)
      return new Straight(Player.gameround)
    }
    return new Random().act(me)
  }
  def speak() = "Turn !"
}

class Straight(val startTurn: Int) extends Behavior {

  def act(me: Pod): Behavior = {
    Map.getStraight(me) match {
      case Some(c: Cell)  => mightChange(me, c)
      case _              => new Turn().act(me)
    }
  }
  def mightChange(me: Pod, target: Cell): Behavior = {
    if (shouldNotTurn(me, target)) {
      Player.output(target, speak)
      this
    } else {
      new Turn().act(me)
    }
  }
  def shouldNotTurn(me: Pod, target: Cell) = {
    val distance = Map.numberInThisDirUntil(target, me.pos, Map.isPossessedByEvil)
    target.isFree && (Player.gameround - startTurn) < (10 - Player.opponentcount) + distance
  }

  def speak() = "Straight on !"
}

object Player extends App {
  val random = Random
  val opponentcount = readInt
  val myPod = new Pod(Map.getCell(0, 0), 0)
  val enemies = new Array[Pod](opponentcount)
  var gameround = 1

  var behavior: Behavior = new Straight(gameround)

  for (i <- 0 until opponentcount)
    enemies(i) = new Pod(Map.getCell(0, 0), 0)

  while(true) {
    gameround = readInt

    myPod.update(for(i <- readLine split " ") yield i.toInt)

    for(i <- 0 until opponentcount)
      enemies(0).update(for(i <- readLine split " ") yield i.toInt)

    for(i <- 0 until 20)
      Map.update(readLine, i)

    behavior = behavior.act(myPod)
  }

  def output(cell: Cell, speak: String) = println(cell.x + " " + cell.y + " " + speak)
}

object Status {
  val free = '.'.toInt
  val mine = '0'.toInt
  val p1 = '1'.toInt
  val p2 = '2'.toInt
  val p3 = '3'.toInt
}

/**
 * Bon, on va considerer que si y++ on va vers le haut
 */
// One line of the map ('.' = free, '0' = you, otherwise the id of the opponent)
object Map {
  val rep: Array[Array[Cell]] = makeGrid(Array.ofDim[Cell](35, 20))
  val left: Int = 0
  val right: Int = 1
  val up: Int = 2
  val down: Int = 3
  val ON_EN_A_GROS = 4

  def makeGrid(cells: Array[Array[Cell]]) = {
    for (x <- 0 until cells.length)
      for (y <- 0 until cells(0).length)
        cells(x)(y) = new Cell(Status.free, x, y, 0)
    cells
  }

  def numberOfSurrounding(c: Cell, id: Int) = {
    var rep = 0
    if (hasLeft(c) && getLeftBorder(c) == id)    rep += 1
    if (hasRight(c) && getRightBorder(c) == id)  rep += 1
    if (hasBottom(c) && getUpBorder(c) == id)    rep += 1
    if (hasTop(c) && getDownBorder(c) == id)     rep += 1
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
    values = orderList(values, pod.pos)
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

  def getDir(to: Cell, from: Cell): Int = {
    if (to.x < from.x)  return left
    if (to.x > from.x)  return right
    if (to.y < from.y)  return down
    if (to.y > from.y)  return up
    ON_EN_A_GROS
  }

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

  def orderList(list: ListBuffer[Cell], from: Cell) = {
    list.foreach(_.computeValue(from))
    list.sortBy(- _.attraction)
  }

  def update(line: String, lineIndex: Int) = {
    for (i <- 0 until line.length)
      rep(i)(lineIndex).status = line.charAt(i)
  }

  def valueAroundStraight(to: Cell) = {
    val left = getHorizontalList(to, -1,  to.x - 1, 0, isntFree)
    val right = getHorizontalList(to,  1,  to.x + 1, rep.length - 1, isntFree)
    val up = getVerticalList(  to,  1,  to.y + 1, rep(0).length - 1, isntFree)
    val down = getVerticalList(  to, -1,  to.y - 1, 0, isntFree)

    val leftSum = left.map(getNumberOfBordersMine(_)).sum
    val rightSum = right.map(getNumberOfBordersMine(_)).sum
    val upSum = up.map(getNumberOfBordersMine(_)).sum
    val downSum = down.map(getNumberOfBordersMine(_)).sum

    leftSum + rightSum + upSum + downSum
  }

  def getNumberOfBordersMine(cell: Cell) = {
    var rep = 0
    if (getLeftBorder(cell) == Status.mine)   rep += 1
    if (getRightBorder(cell) == Status.mine)  rep += 1
    if (getUpBorder(cell) == Status.mine)     rep += 1
    if (getDownBorder(cell) == Status.mine)   rep += 1
    rep
  }
  def getLeftBorder(cell: Cell) = getHorizontalBorderStatus(cell, -1, cell.x - 1, 0)
  def getRightBorder(cell: Cell) = getHorizontalBorderStatus(cell, 1, cell.x + 1, rep.length - 1)
  def getUpBorder(cell: Cell) = getVerticalBorderStatus(cell, -1, cell.y + 1, 0)
  def getDownBorder(cell: Cell) = getVerticalBorderStatus(cell, 1, cell.y - 1, rep(0).length - 1)

  def numberInThisDirUntil(to: Cell, from: Cell, shouldStop: (Cell) => Boolean): Int = {
    val dir: Int = getDir(to, from)
    dir match {
      case 0 => Console.err.println("left"); return Math.abs(from.x - getHorizontalFreeBorder(from, -1,  from.x - 1, 0, shouldStop, false).getOrElse(from).x)
      case 1 => Console.err.println("right"); return Math.abs(from.x - getHorizontalFreeBorder(from,  1,  from.x + 1, rep.length - 1, shouldStop, false).getOrElse(from).x)
      case 2 => Console.err.println("up"); return Math.abs(from.y - getVerticalFreeBorder(  from,  1,  from.y + 1, rep(0).length - 1, shouldStop, false).getOrElse(from).y)
      case 3 => Console.err.println("down"); return Math.abs(from.y - getVerticalFreeBorder(  from, -1,  from.y - 1, 0, shouldStop, false).getOrElse(from).y)
    }
  }

  def getVerticalList(cell: Cell, inc: Int, init: Int, end: Int, shouldStop: (Cell) => Boolean): List[Cell] = {
    var list = ListBuffer[Cell]()
    for (i <- init to end by inc) {
      list += rep(cell.x)(i)
      if (shouldStop(rep(cell.x)(i)))
        return list.toList
    }
    list.toList
  }
  def getHorizontalList(cell: Cell, inc: Int, init: Int, end: Int, shouldStop: (Cell) => Boolean): List[Cell] = {
    var list = ListBuffer[Cell]()
    for (i <- init to end by inc) {
      list += rep(i)(cell.y)
      if (shouldStop(rep(i)(cell.y)))
        return list.toList
    }
    list.toList
  }

  // ugly stuff
  def getHorizontalFreeBorder(cell: Cell, inc: Int, init: Int, end: Int, shouldStop: (Cell) => Boolean, useNone: Boolean): Option[Cell] = {
    for (i <- init to end by inc)
      if (isValid(i, cell.y) && shouldStop(rep(i)(cell.y)))
        return Some(rep(i)(cell.y))
    if (useNone)  None
    else          Some(rep(end)(cell.y))
  }
  def getVerticalFreeBorder(cell: Cell, inc: Int, init: Int, end: Int, shouldStop: (Cell) => Boolean, useNone: Boolean): Option[Cell] = {
    for (i <- init to end by inc)
      if (isValid(cell.x, i) && shouldStop(rep(cell.x)(i)))
        return Some(rep(cell.x)(i))
    if (useNone)  None
    else          Some(rep(cell.x)(end))
  }

  def isFree(c: Cell) = c.isFree
  def isntFree(c: Cell) = !c.isFree
  def isPossessedByEvil(c: Cell) = c.isFree || c.isMine

  def getHorizontalBorderStatus(cell: Cell, inc: Int, init: Int, end: Int): Int = getHorizontalFreeBorder(cell, inc, init, end, isntFree, true).getOrElse(Cell(Status.free, 0, 0, 0)).status
  def getVerticalBorderStatus(cell: Cell, inc: Int, init: Int, end: Int): Int =   getVerticalFreeBorder(cell, inc, init, end, isntFree, true).getOrElse(Cell(Status.free, 0, 0, 0)).status

}
