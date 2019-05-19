package nl.joey.universe.entity

import nl.joey.universe.service.StarService
import nl.joey.universe.util.Utils

case class StarData ( name: String,                     //Star Name
                      rightAscensionHours: Double,      //RA_hr
                      rightAscensionMinutes: Double,    //RA_min
                      declination: Double,              //Declination (d) in degrees
                      distance: Double                  //Dist in Lightyears
                    ){

  def getRAinDeg: Double = {
    (rightAscensionHours + rightAscensionMinutes/60) * 15
  }

  def getRAinRad: Double = {
    Utils.toRadians(getRAinDeg)
  }

  def getDistanceInParsec: Double = {
    StarService.convertLightYearToParsec(distance)
  }

  def getDeclinationInRad: Double = {
    Utils.toRadians(declination)
  }
}
