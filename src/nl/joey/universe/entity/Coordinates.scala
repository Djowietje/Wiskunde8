package nl.joey.universe.entity

case class Coordinates(var x: Float, var y: Float, var z: Float){
  override def toString: String = {
    s"[X: $x, Y: $y, Z: $z]"
  }


}

object Coordinates{
  def empty(): Coordinates = {
    Coordinates(0f,0f,0f)
  }
}
