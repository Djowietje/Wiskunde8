package nl.joey.universe

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import nl.joey.universe.Simulate.{dateTime, stars, planets}
import nl.joey.universe.entity._
import nl.joey.universe.repository.{Keplar, Stars}
import nl.joey.universe.util.Utils
import processing.core.{PApplet, PConstants}
import processing.event.{KeyEvent, MouseEvent}

class Simulate extends PApplet {

  implicit val window: PApplet = this

  var lastUpdate: Long = System.currentTimeMillis()
  var zoom: Float = 1
  var visualDistanceModifier: Float = 100f

  override def settings() {
    size(1024, 768, PConstants.P3D)
  }

  override def draw(): Unit = {
    /** Update with 60 FPS */
    if (System.currentTimeMillis() - lastUpdate < 1000 / 60) {
      Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate))
    }

    background(0)
    textSize(16)
    text(s"Current FPS: ${1000 / (System.currentTimeMillis() - lastUpdate)}", 20, 20)
    text(s"Current date: ${dateTime} Julian Day: ${Utils.ConvertToJulianDayNumber(dateTime)}", 20, 40)

    /** Make 0,0 the center of the screen before drawing planets (for now) */
    translate(width / 2, height / 2)
    scale(zoom)
    planets.foreach(_.drawPlanet(zoom, visualDistanceModifier))
    stars.foreach(_.drawStar(zoom, visualDistanceModifier))

    lastUpdate = System.currentTimeMillis()

  }

  override def keyPressed(event: KeyEvent): Unit = {
    event.getKey match {
      case 'c' => {
        planets.foreach(_.showCoordinates = true)
        stars.foreach(_.showCoordinates = true)
      }
      case 'f' => earthMode(true)
      case _ =>
    }
  }

  override def keyReleased(event: KeyEvent): Unit = {
    event.getKey match {
      case 'c' => {
        planets.foreach(_.showCoordinates = false)
        stars.foreach(_.showCoordinates = false)

      }
      case 'f' => earthMode(false)
      case _ =>
    }
  }

  override def mouseWheel(event: MouseEvent): Unit = {
    event.getCount match {
      case -1 => zoom = zoom * 1.1f
      case 1 => zoom = zoom * 0.9f
    }
  }

  def earthMode(activate: Boolean): Unit = {
    if(activate){
      val coords = planets.find(_.planetData.name=="Earth").get.r
      camera((coords.x * 100).toFloat, (coords.y* - 100).toFloat, (coords.z * 100).toFloat,0f,0f,0f,0f,0f,100f)
      printCamera()
    }
  }
}

object Simulate extends App {

  var planets: Seq[Planet] = Seq.empty
  var stars: Seq[Star] = Seq.empty
  var dateTime: LocalDateTime = _

  override def main(args: Array[String]): Unit = {
    setup()
    PApplet.main("nl.joey.universe.Simulate")
    var lastUpdate = System.currentTimeMillis()

    /** Update with 60 FPS */
    while (true) {
      if (System.currentTimeMillis() - lastUpdate < 1000 / 60) {
        Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate))
        dateTime = LocalDateTime.from(dateTime).plus(1, ChronoUnit.HOURS)
      } else {
        planets.foreach(_.update(dateTime))
        lastUpdate = System.currentTimeMillis()
      }
    }
  }

  def setup(): Unit = {
    planets = Keplar.PLANETDATAS.map(data => new Planet(data))
    stars = Stars.BrightestStars.map(data => new Star(data))
    planets.filter(_.planetData.name != "Sun").foreach(planet => planet.orbit = planet.calculateOrbit())
    dateTime = LocalDateTime.parse("05/05/2015:00:00", DateTimeFormatter.ofPattern("MM/dd/yyyy:HH:mm"))
  }


}
