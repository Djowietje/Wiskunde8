package nl.joey.universe.entity

import java.time.LocalDate
import java.util.Calendar

import nl.joey.universe.repository.PlanetData
import processing.core.PApplet

trait Planet{

  private var lastUpdate = System.currentTimeMillis()

  var sizeScale: Double
  var r: Coordinates = Coordinates.empty()
  var recl: Coordinates = Coordinates.empty()
  var req: Coordinates = Coordinates.empty()

  var a: Float = _
  var e: Float = _
  var I: Float = _
  var L: Float = _
  var w: Float = _
  var o: Float = _
  var W: Float = _
  var M: Float = _
  var E: Float = _

  var localDate: LocalDate = LocalDate.now()

  private var rotationEnabled: Boolean = false

  val planetData: PlanetData


  def update(date: LocalDate): Unit = {
    val timePassedSinceLastUpdate = System.currentTimeMillis() - lastUpdate
    localDate = date
    updateWithFormula(date.toEpochDay, timePassedSinceLastUpdate)
//    if(rotationEnabled) updateRotation(timePassedSinceLastUpdate)
    lastUpdate = System.currentTimeMillis()
  }

  private def updateWithFormula(julianDayNumber: Long, deltaTime: Float): Unit = {
    val T: Float = (julianDayNumber - 2451545) / 36525

    a = planetData.a0 + (T * planetData.at * deltaTime) // Semi-Major Axis, a
    e = planetData.e0 + (T * planetData.et * deltaTime) // Eccentricity, e
    I = planetData.I0 + (T * planetData.It * deltaTime) // Inclination, I
    L = planetData.L0 + (T * planetData.Lt * deltaTime) // Mean Longtitude, L
    w = planetData.w0 + (T * planetData.wt * deltaTime) // Longtitude of Perihelion, w
    o = planetData.o0 + (T * planetData.ot * deltaTime) // Longtitude of the ascending node, o

    //2. Compute arugment of perihelion: W, and the mean anomaly, M
    W = w - o

    if(planetData.f.nonEmpty)
      M = L - w + (planetData.b.get * Math.pow(T, 2).toFloat) + (planetData.c.get* Math.cos(planetData.f.get * T).toFloat) + (planetData.s.get * Math.sin(planetData.f.get * T).toFloat) % 180
    else M= L - w % 180

    E = CalculateEccentricAnomaly(e, M, 6)

    updateCoordinates()
  }

  def CalculateEccentricAnomaly(ec: Float, am: Float, dp: Float): Float = {
    var i = 0
    val delta = Math.pow(10, -dp).asInstanceOf[Float]
    var E = .0
    var F = .0
    val maxIterations = 1000
    E = am + Math.sin(am).asInstanceOf[Float]
    F = E - ec * Math.sin(E).asInstanceOf[Float] - am
    while ( {
      (Math.abs(F) > delta) && (i < maxIterations)
    }) {
      F = E - ec * Math.sin(E).asInstanceOf[Float] - am
      E = E - F / (1.0f - (ec * Math.cos(E).asInstanceOf[Float]))
      i = i + 1
    }
    Math.round(E * Math.pow(10, dp).asInstanceOf[Float]).asInstanceOf[Float] / Math.pow(10, dp).asInstanceOf[Float]
  }

  def updateCoordinates(): Unit = {
    r = Coordinates(
      x = a * (Math.cos(E) - e).toFloat,
      y = a * Math.sqrt(1.0f - e * e).toFloat * Math.sin(E).toFloat,
      z = 0f )
    recl = Coordinates(
      x = ((Math.cos(W) * Math.cos(o)) - (Math.sin(W) * Math.sin(o) * Math.cos(I)) * r.x + ((-Math.sin(W) * Math.cos(o)) - (Math.cos(W) * Math.sin(o) * Math.cos(I))) * r.y).toFloat,
      y = ((Math.cos(W) * Math.sin(o)) + (Math.sin(W) * Math.cos(o) * Math.cos(I)) * r.x + ((-Math.sin(W) * Math.sin(o)) + (Math.cos(W) * Math.cos(o) * Math.cos(I))) * r.y).toFloat,
      z = ((Math.sin(W) * Math.sin(I)) * r.x + (Math.cos(W) * Math.sin(I)) * r.y).toFloat
    )

    val Ecust = 23.43928f
    req = Coordinates(
      x = recl.x,
      y = recl.x + Math.cos(Ecust).toFloat * recl.y - Math.sin(Ecust).toFloat * recl.z,
      z = recl.x + Math.sin(Ecust).toFloat * recl.y + Math.cos(Ecust).toFloat * recl.z
    )
  }

  def drawText()(implicit window: PApplet): Unit = {
    window.text(s"${planetData.name} = $r", 20,planetData.textY)
  }

  def drawPlanet()(implicit window: PApplet): Unit = {
    window.noFill()
    window.stroke(255)
    window.translate(r.x, r.y, r.z)
//    window.rotateX(recl.x)
//    window.rotateY(recl.y)
//    window.rotateZ(recl.z)
    window.sphere(sizeScale.toInt*100)
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
