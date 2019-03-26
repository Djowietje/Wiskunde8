package nl.joey.universe

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoUnit, TemporalAmount}
import java.util.{Calendar, Date}

import nl.joey.universe.entity._
import nl.joey.universe.repository.Keplar
import processing.core.{PApplet, PConstants}
import processing.event.{KeyEvent, MouseEvent}

class Simulate extends PApplet {



  var lastUpdate: Long = System.currentTimeMillis()
  var zoom: Float = 1

  override def settings() {
    size(1024, 768, PConstants.P3D)
  }

  override def draw(): Unit = {
    /** Update with 60 FPS */
    implicit val window: PApplet = this

    if(System.currentTimeMillis() - lastUpdate < 1000 / 60 ) {
      Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate) )
    }

    background(0)
    textSize(16)
    text(s"Current FPS: ${1000 / (System.currentTimeMillis()-lastUpdate)}", 20, 20)
    Keplar.PLANETS.foreach(_.drawText)

    /** Make 0,0 the center of the screen before drawing planets (for now) */
    translate(width/2, height/2)
    scale(zoom)
    Keplar.PLANETS.foreach(_.drawPlanet)

    lastUpdate = System.currentTimeMillis()
  }

  override def keyPressed(event: KeyEvent): Unit = {
    event.getKey match {
      case 'r' => Earth.enableRotation()
    }
  }

  override def keyReleased(event: KeyEvent): Unit = {
    event.getKey match {
      case 'r' => Earth.disableRotation()
    }
  }

  override def mouseWheel(event: MouseEvent): Unit = {
    event.getCount match {
      case 1 => zoom = zoom * 1.1f
      case -1 => zoom = zoom * 0.9f
    }
  }

}

object Simulate extends App {

  override def main(args: Array[String]): Unit = {
    PApplet.main("nl.joey.universe.Simulate")
    var lastUpdate = System.currentTimeMillis()
    var date = LocalDate.parse("01/01/2000", DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    /** Update with 60 FPS */
    while(true){
      if(System.currentTimeMillis() - lastUpdate < 1000 / 60 ) {
        Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate) )
      }
      date = LocalDate.from(date).plus(1, ChronoUnit.DAYS)
      Keplar.PLANETS.foreach(_.update(date))
      lastUpdate = System.currentTimeMillis()
    }
  }

}
