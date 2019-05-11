package nl.joey.universe.entity

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime}

import nl.joey.universe.repository.PlanetData
import nl.joey.universe.service.PlanetService
import nl.joey.universe.util.Utils._
import processing.core.PApplet

trait Planet{

  private var lastUpdate = System.currentTimeMillis()

  var sizeScale: Double
  var r: Coordinates = Coordinates.empty()
  var recl: Coordinates = Coordinates.empty()
  var req: Coordinates = Coordinates.empty()

  val planetData: PlanetData

  var formulaVars: FormulaVariables = FormulaVariables.empty()
  var showCoordinates: Boolean = false

  var localDateTime: LocalDateTime = LocalDateTime.now()

  private var rotationEnabled: Boolean = false

  def update(date: LocalDateTime): Unit = {
    localDateTime = date
    val (julianDay, julianTime) = ConvertToJulianDayNumber(localDateTime)
    updateFormulaVars(julianDay, julianTime)
    updateCoordinates()
//    if(rotationEnabled) updateRotation(timePassedSinceLastUpdate)
  }

  private def updateFormulaVars(julianDay: Float, julianTime: Float): Unit = {
    formulaVars = PlanetService.updateVariables(julianDay, julianTime, formulaVars, planetData)
  }

  private def updateCoordinates():Unit = {
    r = PlanetService.calculateCoordinates(formulaVars)
//    r = PlanetService.calculateRCoordinates(formulaVars)
//    recl = PlanetService.calculateReclCoordinates(formulaVars, r)
//    req = PlanetService.calculateReqCoordinates(recl)
  }

  def drawPlanet(scale: Float)(implicit window: PApplet): Unit = {
    window.noFill()
    window.stroke(255)
//    window.rotateX(recl.x)
//    window.rotateY(recl.y)
//    window.rotateZ(recl.z)
    window.translate(r.x*100, r.y* -100, 0)
    window.scale(1/scale)
    window.text(s"${planetData.name}",1,1)
    if(showCoordinates) {
      window.scale(0.5f)
      window.text(s"X:${r.x}\nY:${r.y}\nZ:${r.z}", 140, 1)
      window.stroke(80)
      window.line(0,0,0,100,100,100)
      window.scale(2f)
    }
    window.scale(scale)
    window.sphere(sizeScale.toInt)
    window.translate(r.x * -100, r.y * 100, 0)
  }

  def enableRotation(): Unit = {
    rotationEnabled = true
  }

  def disableRotation(): Unit = {
    rotationEnabled = false
  }

  override def toString: String = {
    s"Planet ${super.getClass.getCanonicalName}'s location is $r"
  }

  def calculateOrbit(): List[(Coordinates, Coordinates)] = {
    var result = List.empty[(Coordinates, Coordinates)]
    var dateTime = localDateTime
    update(dateTime)
    val startCoordinates = r
    var deltaCoordinates = 0f
    var prevCoordinates = r
    var orbitComplete = false
    var iterations = 0
    while(!orbitComplete) {
      iterations+=1
      update(dateTime)
      val currentCoords = r
//      window.translate(r.x*100, r.y* -100, 0)
      result :+ prevCoordinates -> currentCoords
//      window.translate(r.x * -100, r.y * 100, 0)

      //update vars for next iteration
      prevCoordinates = currentCoords
      dateTime = dateTime.plus(1,ChronoUnit.DAYS)
      val newDeltaCoordinates = calculateDistance(startCoordinates, currentCoords)
      if(newDeltaCoordinates<deltaCoordinates) orbitComplete = true

      deltaCoordinates = newDeltaCoordinates

    }
    println(s"Done calculating orbit of ${planetData.name}. Took $iterations iterations.")
    result
  }

  def drawOrbit(orbit: List[(Coordinates, Coordinates)])(implicit window: PApplet): Unit ={
    for((coord1, coord2) <- orbit){
      window.color(60,60,60)
      window.line(coord1.x*100,coord1.y*100,coord1.z*100,coord2.x*100,coord2.y*100,coord2.z*100)
    }
    println(s"Done drawing orbit of ${planetData.name}.")
  }


}
