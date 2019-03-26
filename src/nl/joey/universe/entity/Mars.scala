package nl.joey.universe.entity

import nl.joey.universe.repository.{Keplar, PlanetData}

object Mars extends Planet{
  override var sizeScale: Double = 100
  override val planetData: PlanetData = Keplar.PLANETDATAS.find(_.name=="Mars").getOrElse(throw new IllegalArgumentException("Cannot find data for Mars"))

}
