package nl.joey.universe.util

import java.time.LocalDateTime

object Utils {

  def ConvertToJulianDayNumber(date: LocalDateTime): (Float, Float) = {

        val a: Float = (14 - date.getMonthValue) / 12
        val y: Float = date.getYear + 4800 - a
        val m: Float = date.getMonthValue + (12 * a) - 3

        date.getDayOfMonth + (((153 * m) + 2) / 5) + (365 * y) + (y / 4) - (y / 100) + (y / 400) - 32045 ->
        (1f/24*date.getHour + 1f/24/60*date.getMinute + 1f/24/60/60*date.getSecond)
//    val month = (date.getMonthValue-14)/12
//    val year = date.getYear + 4800
//
//    (1461*(year+month)/4 + 367*(date.getMonthValue-2-12*month)/12 - (3*((year+month+100)/100))/4 + date.getDayOfMonth - 32075, 1f/24*date.getHour + 1f/24/60*date.getMinute + 1f/24/60/60*date.getSecond)
  }

  def toRadians(deg:Float): Float = {
    (deg * (Math.PI/180)).toFloat
  }
}
