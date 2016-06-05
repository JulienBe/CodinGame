package Scala

/**
  * Casablanca’s hippodrome is organizing a new type of horse racing: duals.
  * During a dual, only two horses will participate in the race.
  * In order for the race to be interesting, it is necessary to try to select two horses with similar strength.
  *
  * Write a program which, using a given number of strengths, identifies the two closest strengths and shows their difference with an integer (≥ 0).
  */
object Solution extends App {
  var n = readInt
  var delta = 999999
  val horses = for (i <- 0 until n) yield readInt()
  horses.sortWith(_ < _)
  var previous = -1
  for (current <- horses.sortWith(_ < _)) {
    if (current - previous < delta && previous != -1)
      delta = current - previous
    previous = current
  }
  println(delta)
}