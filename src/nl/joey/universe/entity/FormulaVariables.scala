package nl.joey.universe.entity

case class FormulaVariables(var a: Double,
                            var e: Double,
                            var I: Double,
                            var L: Double,
                            var w: Double,
                            var o: Double,
                            var W: Double,
                            var M: Double,
                            var EccAnomaly: Double,
                            var n: Double){
  override def toString: String = {
    s"a:$a, e:$e, I:$I, L:$L, w:$w, o:$o, W:$W, M:$M, E:$EccAnomaly, n:$n"
  }
}
object FormulaVariables {
  def empty(): FormulaVariables = {
    FormulaVariables(0,0,0,0,0,0,0,0,0,0)
  }
}
