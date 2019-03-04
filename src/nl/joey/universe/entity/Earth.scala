package nl.joey.universe.entity

object Earth extends Planet{
  override var sizeScale: Double = 1
  override var position: BigTriple = BigTriple(0,0,0)
  override val velocity: BigTriple = BigTriple(1,2,3)
  override val rotation: BigTriple = BigTriple(5,2,3)
  override val rotationalVelocity: BigTriple = BigTriple(10,10,10)
}
