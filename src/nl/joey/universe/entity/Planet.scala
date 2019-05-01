package nl.joey.universe.entity

import java.time.{LocalDate, LocalDateTime}

import nl.joey.universe.repository.PlanetData
import nl.joey.universe.service.PlanetService
import nl.joey.universe.util.Utils
import processing.core.PApplet

trait Planet{

  private var lastUpdate = System.currentTimeMillis()

  var sizeScale: Double
  var r: Coordinates = Coordinates.empty()
  var recl: Coordinates = Coordinates.empty()
  var req: Coordinates = Coordinates.empty()

  val planetData: PlanetData

  var formulaVars: FormulaVariables = FormulaVariables.empty()

  var localDateTime: LocalDateTime = LocalDateTime.now()

  private var rotationEnabled: Boolean = false

  def update(date: LocalDateTime): Unit = {
    localDateTime = date
    val (julianDay, julianTime) = Utils.ConvertToJulianDayNumber(localDateTime)
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

  def drawText()(implicit window: PApplet): Unit = {
    window.text(s"${planetData.name} = $r", 20,planetData.textY)
  }

  def drawPlanet(scale: Float)(implicit window: PApplet): Unit = {
    window.noFill()
    window.stroke(255)
//    window.rotateX(recl.x)
//    window.rotateY(recl.y)
//    window.rotateZ(recl.z)
    window.translate(r.x*100, r.y*100, 0)
    window.scale(1/scale)
    window.text(planetData.name,2*scale,2*scale)
    window.scale(scale)
    window.sphere(sizeScale.toInt)
    window.translate(r.x * -100, r.y * -100, 0)
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


}
