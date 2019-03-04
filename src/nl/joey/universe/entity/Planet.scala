package nl.joey.universe.entity

import processing.core.PApplet

trait Planet{

  private var lastUpdate = System.currentTimeMillis()

  var sizeScale: Double
  var position: BigTriple
  val rotation: BigTriple
  val velocity: BigTriple
  val rotationalVelocity: BigTriple
  private var rotationEnabled: Boolean = false

  def update(): Unit = {
    val timePassedSinceLastUpdate = System.currentTimeMillis() - lastUpdate
    updateLocation(timePassedSinceLastUpdate)
    if(rotationEnabled) updateRotation(timePassedSinceLastUpdate)
    lastUpdate = System.currentTimeMillis()
  }

  private def updateLocation(timePassedSinceLastUpdate: Long): Unit = {
    position.x = position.x + (velocity.x/1000 * timePassedSinceLastUpdate)
    position.y = position.y + (velocity.y/1000 * timePassedSinceLastUpdate)
    position.z = position.z + (velocity.z/1000 * timePassedSinceLastUpdate)
  }

  private def updateRotation(timePassedSinceLastUpdate: Long): Unit = {
    rotation.x = rotation.x + (rotationalVelocity.x/1000 * timePassedSinceLastUpdate)
    rotation.y = rotation.y + (rotationalVelocity.y/1000 * timePassedSinceLastUpdate)
    rotation.z = rotation.z + (rotationalVelocity.z/1000 * timePassedSinceLastUpdate)
  }

  def drawPlanet()(implicit window: PApplet): Unit = {
    window.noFill()
    window.stroke(255)
    window.translate(position.x.toFloat, position.y.toFloat, position.z.toFloat)
    window.rotateX(rotation.x.toFloat)
    window.rotateY(rotation.y.toFloat)
    window.rotateZ(rotation.z.toFloat)
    window.sphere(sizeScale.toInt*100)
  }

  def enableRotation(): Unit = {
    rotationEnabled = true
  }

  def disableRotation(): Unit = {
    rotationEnabled = false
  }

  override def toString: String = {
    s"Planet ${super.getClass.getCanonicalName}'s location is $position"
  }

}
