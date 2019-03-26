package nl.joey.universe.entity

import nl.joey.universe.repository.{Keplar, PlanetData}

object Mercury extends Planet{
  override var sizeScale: Double = 1
  override val planetData: PlanetData = Keplar.PLANETDATAS.find(_.name=="Mercury").getOrElse(throw new IllegalArgumentException("Cannot find data for Mercury"))

}
