package nl.joey.universe

import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoUnit, TemporalAmount}

import nl.joey.universe.Simulate.planets
import nl.joey.universe.entity._
import nl.joey.universe.repository.{Keplar, PlanetData}
import nl.joey.universe.util.Utils
import processing.core.{PApplet, PConstants}
import processing.event.{KeyEvent, MouseEvent}

class Simulate extends PApplet {


  //CHECK http://www.planetaryorbits.com/tutorial-javascript-orbit-simulation.html
  implicit val window: PApplet = this

  var lastUpdate: Long = System.currentTimeMillis()
  var zoom: Float = 1
  override def settings() {
    size(1024, 768, PConstants.P3D)
  }

  override def draw(): Unit = {
    if(!Simulate.drawnOrbits){
      //draw orbits once.
      planets.filter(_.planetData.name != "Sun").foreach{planet =>
        planet.drawOrbit(planet.calculateOrbit())
      }
      Simulate.drawnOrbits = true
      println("Set DrawnOrbits to True.")
    } else {
      /** Update with 60 FPS */

      if (System.currentTimeMillis() - lastUpdate < 1000 / 60) {
        Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate))
      }
      background(0)
      textSize(16)
      text(s"Current FPS: ${1000 / (System.currentTimeMillis() - lastUpdate)}", 20, 20)
      text(s"Current date: ${Simulate.dateTime} Julian Day: ${Utils.ConvertToJulianDayNumber(Simulate.dateTime)}", 20, 40)

      /** Make 0,0 the center of the screen before drawing planets (for now) */
      translate(width / 2, height / 2)
      scale(zoom)
      planets.foreach(_.drawPlanet(zoom))
      //    printCamera()

      lastUpdate = System.currentTimeMillis()
    }
  }

  override def keyPressed(event: KeyEvent): Unit = {
    event.getKey match {
      case 'c' => planets.foreach(_.showCoordinates=true)
      case _ =>
    }
  }

  override def keyReleased(event: KeyEvent): Unit = {
    event.getKey match {
      case 'c' => planets.foreach(_.showCoordinates=false)
      case _ =>
    }
  }

  override def mouseWheel(event: MouseEvent): Unit = {
    event.getCount match {
      case -1 => zoom = zoom * 1.1f
      case 1 => zoom = zoom * 0.9f
    }
  }
}

object Simulate extends App {

  var planets: Seq[Planet] = Seq.empty
  var dateTime: LocalDateTime = _
  var drawnOrbits: Boolean = false

  override def main(args: Array[String]): Unit = {
    setup()
    PApplet.main("nl.joey.universe.Simulate")
    var lastUpdate = System.currentTimeMillis()

    /** Update with 60 FPS */
    while(true) {
      if(Simulate.drawnOrbits) {
        if (System.currentTimeMillis() - lastUpdate < 1000 / 60) {
          Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate))
          dateTime = LocalDateTime.from(dateTime).plus(1, ChronoUnit.HOURS)
        } else {
          planets.foreach(_.update(dateTime))
          lastUpdate = System.currentTimeMillis()

        }
      } else Thread.sleep(1000)
    }
  }

  def setup(): Unit = {
    planets = Keplar.PLANETDATAS.map(data => new Planet {
      override var sizeScale: Double = 1
      override val planetData: PlanetData = data
    })

    dateTime = LocalDateTime.parse("05/05/2015:00:00", DateTimeFormatter.ofPattern("MM/dd/yyyy:HH:mm"))
  }


}
