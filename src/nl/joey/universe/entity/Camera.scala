package nl.joey.universe.entity

case class Camera (eyePos: Coordinates, lookAt: Coordinates, up: Coordinates){
  def this(ex: Double,ey: Double,ez: Double,lx: Double,ly: Double,lz: Double,ux: Double,uy: Double,uz: Double) =
    this(Coordinates(ex,ey,ez), Coordinates(lx,ly,lz),Coordinates(ux,uy,uz))
}

object Camera{
  def getDefault: Camera = {
    Camera(
      Coordinates(0 ,500,500),
      Coordinates(0,0,0),
      Coordinates(0,0,-1)
    )
  }
}