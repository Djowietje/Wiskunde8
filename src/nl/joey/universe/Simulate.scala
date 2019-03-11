package nl.joey.universe

import nl.joey.universe.entity.Earth
import processing.core.{PApplet, PConstants}
import processing.event.KeyEvent

class Simulate extends PApplet {

  var lastUpdate: Long = System.currentTimeMillis()

  override def settings() {
    size(1024, 768, PConstants.P3D)
  }

  override def draw(): Unit = {
    /** Update with 60 FPS */
    if(System.currentTimeMillis() - lastUpdate < 1000 / 60 ) {
      Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate) )
    }

    background(0)
    textSize(16)
    text(s"Current FPS: ${1000 / (System.currentTimeMillis()-lastUpdate)}", 20, 20)

    /** Make 0,0 the center of the screen before drawing planets (for now) */
    translate(width/2, height/2)

    Earth.drawPlanet()(this)

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

}

object Simulate extends App {

  override def main(args: Array[String]): Unit = {
    PApplet.main("nl.joey.universe.Simulate")
    var lastUpdate = System.currentTimeMillis()
    /** Update with 60 FPS */
    while(true){
      if(System.currentTimeMillis() - lastUpdate < 1000 / 60 ) {
        Thread.sleep(1000 / 60 - (System.currentTimeMillis() - lastUpdate) )
      }
      Earth.update()
      lastUpdate = System.currentTimeMillis()
    }
  }

}
