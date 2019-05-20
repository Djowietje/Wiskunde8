package nl.joey.universe.entity

case class Coordinates(var x: Double, var y: Double, var z: Double){
  override def toString: String = {
    s"[X: $x, Y: $y, Z: $z]"
  }
}

object Coordinates{
  def empty(): Coordinates = {
    Coordinates(0,0,0)
  }
}
