package nl.joey.universe.entity

import nl.joey.universe.service.StarService
import processing.core.PApplet

class Star(starData: StarData) {
  val coords = StarService.calculateCoordinatesInAU(starData)
  var showCoordinates: Boolean = false

  def drawStar(scale: Float, visualDistanceModifier: Float)(implicit window: PApplet): Unit = {
    window.noFill()
    window.stroke(255)

    window.translate((coords.x*visualDistanceModifier).toFloat, (coords.y* -visualDistanceModifier).toFloat, (coords.z * visualDistanceModifier).toFloat)
    window.scale(1/scale)
    window.text(s"${starData.name}",1,1)
    if(showCoordinates) {
      window.text(s"X:${coords.x}\nY:${coords.y}\nZ:${coords.z}", 140, 1)
    }
    window.sphere(1)
    window.scale(scale)
    window.translate((coords.x * -visualDistanceModifier).toFloat, (coords.y * visualDistanceModifier).toFloat, (coords.z * -visualDistanceModifier).toFloat)
  }
}
