package nl.joey.universe.entity

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime}

import nl.joey.universe.service.PlanetService
import nl.joey.universe.util.Utils._
import processing.core.PApplet

class Planet(val planetData: PlanetData) {

  private var lastUpdate = System.currentTimeMillis()

  var sizeScale: Double = 4.0
  var r: Coordinates = Coordinates.empty()
  var recl: Coordinates = Coordinates.empty()
  var req: Coordinates = Coordinates.empty()
  var formulaVars: FormulaVariables = FormulaVariables.empty()
  var orbit: List[(Coordinates, Coordinates)] = List.empty

  var orbitCalculated: Boolean = false
  var showCoordinates: Boolean = false
  var rotationEnabled: Boolean = false

  var localDateTime: LocalDateTime = LocalDateTime.now()


  def update(date: LocalDateTime): Unit = {
    localDateTime = date
    val (julianDay, julianTime) = ConvertToJulianDayNumber(localDateTime)
    updateFormulaVars(julianDay, julianTime)
    updateCoordinates()
  }

  private def updateFormulaVars(julianDay: Double, julianTime: Double): Unit = {
    formulaVars = PlanetService.updateVariables(julianDay, julianTime, formulaVars, planetData)
  }

  private def updateCoordinates():Unit = {
    r = PlanetService.calculateCoordinates(formulaVars)
    recl = PlanetService.calculateReclCoordinates(formulaVars, r)
//    req = PlanetService.calculateReqCoordinates(recl)
  }

  def drawPlanet(scale: Float, visualDistanceModifier: Float)(implicit window: PApplet): Unit = {
    window.noFill()
    window.stroke(255)

    window.translate((r.x*visualDistanceModifier).toFloat, (r.y* -visualDistanceModifier).toFloat, (r.z * visualDistanceModifier).toFloat)
    window.scale(1/scale)
    window.text(s"${planetData.name}",1,1)
    if(showCoordinates) {
      window.scale(0.5f)
      window.text(s"X:${r.x}\nY:${r.y}\nZ:${r.z}", 140, 1)
      window.scale(2f)
    }
    window.scale(scale)
    window.sphere(sizeScale.toInt)
    window.translate((r.x * -visualDistanceModifier).toFloat, (r.y * visualDistanceModifier).toFloat, (r.z * -visualDistanceModifier).toFloat)
    if(this.planetData.name != "Sun" ) drawOrbit(orbit, visualDistanceModifier)
  }

  def calculateOrbit(): List[(Coordinates, Coordinates)] = {
    var result = List.empty[(Coordinates, Coordinates)]
    var dateTime = localDateTime
    update(dateTime)
    val startCoordinates = r
    var deltaCoordinates = 0.0
    var prevCoordinates = r
    var orbitComplete = false
    var halfwaypoint = false
    var iterations = 0
    while(!orbitComplete) {
      iterations+=1
      update(dateTime)
      val currentCoords = r
      result = result :+ (prevCoordinates -> currentCoords)

      //update vars for next iteration
      prevCoordinates = currentCoords
      dateTime = dateTime.plus(5,ChronoUnit.DAYS)
      val newDeltaCoordinates = calculateDistance(startCoordinates, currentCoords)
      if(newDeltaCoordinates<deltaCoordinates) halfwaypoint = true
      if(halfwaypoint && newDeltaCoordinates>deltaCoordinates) orbitComplete = true

      deltaCoordinates = newDeltaCoordinates

    }
    println(s"Done calculating orbit of ${planetData.name}. Took $iterations iterations. Returning a list of ${result.size}.")
    orbitCalculated = true
    result
  }

  def drawOrbit(orbit: List[(Coordinates, Coordinates)], visualDistanceModifier: Float)(implicit window: PApplet): Unit ={
    for((coord1, coord2) <- orbit){
      window.stroke(60)
      window.line(
        (coord1.x*visualDistanceModifier).toFloat,
        (coord1.y*visualDistanceModifier * -1).toFloat,
        (coord1.z*visualDistanceModifier).toFloat,
        (coord2.x*visualDistanceModifier).toFloat,
        (coord2.y*visualDistanceModifier * -1).toFloat,
        (coord2.z*visualDistanceModifier).toFloat)
    }
  }

  override def toString: String = {
    s"Planet ${super.getClass.getCanonicalName}'s location is $r"
  }

}
