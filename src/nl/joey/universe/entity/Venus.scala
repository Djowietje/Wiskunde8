package nl.joey.universe.entity

import nl.joey.universe.repository.{Keplar, PlanetData}

object Venus extends Planet{
  override var sizeScale: Double = 1
  override val planetData: PlanetData = Keplar.PLANETDATAS.find(_.name=="Venus").getOrElse(throw new IllegalArgumentException("Cannot find data for Venus"))

}
