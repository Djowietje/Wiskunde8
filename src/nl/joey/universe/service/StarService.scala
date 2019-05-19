package nl.joey.universe.service

import java.nio.DoubleBuffer

import nl.joey.universe.entity.{Coordinates, StarData}

object StarService {

  def convertParsecToAU(parsec:Double): Double = {
    //1 parsec = 206.265 AE
    parsec * 206264.80621425
  }

  def convertLightYearToAU(lightyear: Double): Double = {
    lightyear * 63241.077084266
  }

  def convertLightYearToParsec(lightyear: Double): Double = {
    lightyear * 0.30660139383437
  }

  def calculateCoordinatesInParsec(starData: StarData): Coordinates = {
    val x = starData.getDistanceInParsec * Math.cos(starData.getDeclinationInRad) * Math.cos(starData.getRAinRad)
    val y = starData.getDistanceInParsec * Math.cos(starData.getDeclinationInRad) * Math.sin(starData.getRAinRad)
    val z = starData.getDistanceInParsec * Math.sin(starData.getDeclinationInRad)
    Coordinates(x,y,z)
  }

  def calculateCoordinatesInAU(starData: StarData): Coordinates = {
    val parsecCords = calculateCoordinatesInParsec(starData)
    Coordinates(convertParsecToAU(parsecCords.x), convertParsecToAU(parsecCords.y), convertParsecToAU(parsecCords.z))
  }

}
