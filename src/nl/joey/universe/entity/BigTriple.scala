package nl.joey.universe.entity

case class BigTriple(var x: BigDecimal, var y: BigDecimal, var z: BigDecimal){
  override def toString: String = {
    s"[X: $x, Y: $y, Z: $z]"
  }
}
