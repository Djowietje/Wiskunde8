package nl.joey.universe.entity

import nl.joey.universe.repository.{Keplar, PlanetData}

object Jupiter extends Planet{
  override var sizeScale: Double = 1
  override val planetData: PlanetData = Keplar.PLANETDATAS.find(_.name=="Jupiter").getOrElse(throw new IllegalArgumentException("Cannot find data for Jupiter"))

}
