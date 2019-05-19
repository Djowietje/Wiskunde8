package nl.joey.universe.service

import nl.joey.universe.entity.{Coordinates, FormulaVariables, PlanetData}
import nl.joey.universe.util.Utils.toRadians

object PlanetService {

  def updateVariables(julianDayNumber: Double, julianTime: Double, fv: FormulaVariables, pd: PlanetData): FormulaVariables = {
//    println(s"julianDay: ${julianDayNumber - 2451545} T: ${(julianDayNumber - 2451545) / 36525}")

    fv.a = pd.a0 + (((julianDayNumber - 2451545) / 36525) * pd.at) + (julianTime/36525*pd.at)// Semi-Major Axis, a
    fv.e = pd.e0 + (((julianDayNumber - 2451545) / 36525) * pd.et) + (julianTime/36525*pd.et)// Eccentricity, e

    fv.I = (pd.I0 + (((julianDayNumber - 2451545) / 36525) * pd.It) + (julianTime/36525*pd.It))%360 // Inclination, I
    fv.L = pd.L0 + (((julianDayNumber - 2451545) / 36525) * pd.Lt) + (julianTime/36525*pd.Lt) // Mean Longtitude, L
    fv.w = pd.w0 + (((julianDayNumber - 2451545) / 36525) * pd.wt) + (julianTime/36525*pd.wt) // Longtitude of Perihelion, w
    fv.o = pd.o0 + (((julianDayNumber - 2451545) / 36525) * pd.ot) + (julianTime/36525*pd.ot) // Longtitude of the ascending node, o (W)

    while(fv.L< 0) fv.L += 360
    while(fv.w< 0) fv.w += 360
    while(fv.o< 0) fv.o += 360

    while(fv.L > 360) fv.L -= 360
    while(fv.w > 360) fv.w -= 360
    while(fv.o > 360) fv.o -= 360

    //2. Compute arugment of perihelion: W, and the mean anomaly, M
    fv.W = fv.w - fv.o

    val T = (julianDayNumber - 2451545) / 36525
    if(pd.f.nonEmpty)
      fv.M = fv.L - fv.w + (pd.b.get * Math.pow(T, 2)) + (pd.c.get* Math.cos(pd.f.get * T)) + (pd.s.get * Math.sin(pd.f.get * T))
    else fv.M= fv.L - fv.w

    while(fv.M > 360) fv.M-=360
    while(fv.M < 0) fv.M += 360

    fv.EccAnomaly = CalculateEccentricAnomaly(fv.e, fv.M, 7)
    val trueAnomaly = Math.sqrt((1+fv.e) / (1-fv.e)) * Math.tan(toRadians(fv.EccAnomaly)/2)

    val K = Math.PI/180.0

    if(trueAnomaly<0){
      fv.n = 2 * (Math.atan(trueAnomaly)/K+180)
    }
    else{
      fv.n = 2 * (Math.atan(trueAnomaly)/K)
    }
    fv
  }

  def CalculateEccentricAnomaly(e: Double, M: Double, precision: Double): Double = {
    var i = 0.0
    val precisionLimit = Math.pow(10, -precision)
    var E = 0.0
    var F = 0.0
    val maxIterations = 1000
    E = M + Math.sin(M)
    F = E - e * Math.sin(E) - M
    while ( {
      (Math.abs(F) > precisionLimit) && (i < maxIterations)
    }) {
      F = E - e * Math.sin(E) - M
      E = E - F / (1.0f - (e * Math.cos(E)))
      i = i + 1
    }
    Math.round(E * Math.pow(10, precision)) / Math.pow(10, precision)
  }

  def calculateCoordinates(fv:FormulaVariables): Coordinates ={
    val r = fv.a * (1 - (fv.e * Math.cos(toRadians(fv.EccAnomaly))))
    val x = r * (Math.cos(toRadians(fv.o)) * Math.cos(toRadians(fv.n + fv.w - fv.o)) - Math.sin(toRadians(fv.o)) * Math.sin(toRadians(fv.n + fv.w - fv.o)) * Math.cos(toRadians(fv.I)))
    val y = r * (Math.sin(toRadians(fv.o)) * Math.cos(toRadians(fv.n + fv.w - fv.o)) + Math.cos(toRadians(fv.o)) * Math.sin(toRadians(fv.n + fv.w - fv.o)) * Math.cos(toRadians(fv.I)))
    val z = r * (Math.sin(toRadians(fv.n + fv.w - fv.o)) * Math.sin(toRadians(fv.I)))
    Coordinates(x,y,z)
  }

  def calculateRCoordinates(fv: FormulaVariables): Coordinates = {
    Coordinates(
      x = fv.a * (Math.cos(fv.EccAnomaly) - fv.e),
      y = fv.a * Math.sqrt(1.0f - (fv.e * fv.e)) * Math.sin(fv.EccAnomaly),
      z = 0f )
  }

  def calculateReclCoordinates(fv: FormulaVariables, r: Coordinates): Coordinates = {
    Coordinates(
      x = (Math.cos(fv.W) * Math.cos(fv.o)) -
        (Math.sin(fv.W) * Math.sin(fv.o) * Math.cos(fv.I)) * r.x +
        ((-Math.sin(fv.W) * Math.cos(fv.o)) - (Math.cos(fv.W) * Math.sin(fv.o) * Math.cos(fv.I))) * r.y,
      y = (Math.cos(fv.W) * Math.sin(fv.o)) +
        (Math.sin(fv.W) * Math.cos(fv.o) * Math.cos(fv.I)) * r.x +
        ((-Math.sin(fv.W) * Math.sin(fv.o)) + (Math.cos(fv.W) * Math.cos(fv.o) * Math.cos(fv.I))) * r.y,
      z = (Math.sin(fv.W) * Math.sin(fv.I)) * r.x + (Math.cos(fv.W) * Math.sin(fv.I)) * r.y
    )
  }

  def calculateReqCoordinates(recl: Coordinates): Coordinates = {
    val Ecust = 23.43928f

    Coordinates(
      x = recl.x,
      y = recl.x + Math.cos(Ecust) * recl.y - Math.sin(Ecust) * recl.z,
      z = recl.x + Math.sin(Ecust) * recl.y + Math.cos(Ecust) * recl.z
    )
  }
}
