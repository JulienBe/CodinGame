package Scala.src

/**
  * The city of Montpellier has equipped its streets with defibrillators to help save victims of cardiac arrests.
  * The data corresponding to the position of all defibrillators is available online.
  *
  * Based on the data we provide in the tests, write a program that will allow users to find the defibrillator nearest to their location using their mobile phone.
  *
  * The input data you require for your program is provided in text format.
  * This data is comprised of lines, each of which represents a defibrillator. Each defibrillator is represented by the following fields:
  *   A number identifying the defibrillator
  *   Name
  *   Address
  *   Contact Phone number
  *   Longitude (degrees)
  *   Latitude (degrees)
  *   These fields are separated by a semicolon (;).
  * Beware: the decimal numbers use the comma (,) as decimal separator. Remember to turn the comma (,) into dot (.) if necessary in order to use the data in your program.
  */
object Solution extends App {

  val lon = convertToDouble(readLine)
  val lat = convertToDouble(readLine)
  val numberOfDefib = readInt
  var answer = Defibrillator(-1, "no one has as many friends as the man with many cheeses", 9999999)

  for(i <- 0 until numberOfDefib) {
    val defib = readLine.split(";")
    // 0 : id, 4 : longitude, 5 : latitude
    val currentLongitude = convertToDouble(defib(4))
    val currentLatitude = convertToDouble(defib(5))
    val currentDefib = Defibrillator(i, defib(1), computeDistance(lon, lat, currentLongitude, currentLatitude))

    if (currentDefib.distance < answer.distance)
      answer = currentDefib
  }

  println(answer.name)

  def computeDistance(longitude1: Double, latitude1: Double, longitude2: Double, latitude2: Double): Double = {
    val x = (longitude2 - longitude1) * Math.cos((latitude1 + latitude2) / 2)
    val y = latitude2 - latitude1
    Math.sqrt((x*x) + (y*y)) * 6371
  }

  def convertToDouble(s: String): Double = s.replaceAll(",", ".").toDouble
}

case class Defibrillator(id: Int, name: String, distance: Double)