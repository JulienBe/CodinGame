package Scala

/**
 * The program:
 * The game is played on a rectangular grid with a given size.
 *  Some cells contain power nodes. The rest of the cells are empty.
 *
 * The goal is to find the horizontal and vertical neighbors of each node.
 *
 * To do this, you must find each (x1,y1) coordinates containing a node,
 *  and display the (x2,y2) coordinates of the next node to the right,
 *  and the (x3,y3) coordinates of the next node to the bottom within the grid.
 *
 * If a neighbor does not exist, you must output the coordinates -1 -1 instead of (x2,y2) and/or (x3,y3).
 *
 * Don't let the machines win. You are humanity's last hope...
 **/

/**
  * The program:
  * The game is played on a rectangular grid with a given size.
  *  Some cells contain power nodes. The rest of the cells are empty.
  *
  * The goal is to find the horizontal and vertical neighbors of each node.
  *
  * To do this, you must find each (x1,y1) coordinates containing a node,
  *  and display the (x2,y2) coordinates of the next node to the right,
  *  and the (x3,y3) coordinates of the next node to the bottom within the grid.
  *
  * If a neighbor does not exist, you must output the coordinates -1 -1 instead of (x2,y2) and/or (x3,y3).
  *
  * Don't let the machines win. You are humanity's last hope...
  **/
object Player extends App {
  // TODO : VERY ugly and naive
  val width = readInt
  val height = readInt
  val grid = Array.fill(height){Array.fill[Cell](width)(Cell(true))}
  val answer = StringBuilder.newBuilder
  initGrid

  for (y <- 0 until grid.length) {
    for (x <- 0 until grid(y).length) {
      answer ++= (x + " " + y + " ")
      Console.err.println(x+1 + "is on grid " + isOnGrid(x+1, width))
      // Console.err.println(y + ", " + x+1 + " is empty ? " + grid(y)(x+1).empty)
      if (isOnGrid(x+1, width) && !grid(y)(x+1).empty)  answer ++= ((x+1) + " " + y + " ")
      else answer ++= "-1 -1 "
      if (isOnGrid(y+1, height) && !grid(y+1)(x).empty) answer ++= (x + " " + (y+1) + " ")
      else answer ++= "-1 -1 "
      answer ++= "\n"
    }
  }

  def searchFor(y: Int, x: Int) = {

    var foundRight = false
    var foundBottom = false
    while (y < grid.length) {
      while (x <  grid(y).length) {
        x += 1
      }
    }
  }

  for (y <- 0 until grid.length) {
    for (x <- 0 until grid(y).length) {
      searchFor(y, x)
    }
  }

  println(answer.toString)

  def isOnGrid(i: Int, limit: Int) = i >= 0 && i < limit

  def initGrid: Unit = {
    var y = 0
    for (i <- 0 until height) {
      var x = 0
      for (c <- readLine) {
        if (c == '.') grid(y)(x).empty = true
        else grid(y)(x).empty = false
        x += 1
      }
      y += 1
    }
  }
}

case class Cell(var empty: Boolean)

