package nl.joey.universe.entity

case class FormulaVariables(var a: Float,
                            var e: Float,
                            var I: Float,
                            var L: Float,
                            var w: Float,
                            var o: Float,
                            var W: Float,
                            var M: Float,
                            var EccAnomaly: Float,
                            var n: Float){
  override def toString: String = {
    s"a:$a, e:$e, I:$I, L:$L, w:$w, o:$o, W:$W, M:$M, E:$EccAnomaly, n:$n"
  }
}
object FormulaVariables {
  def empty(): FormulaVariables = {
    FormulaVariables(0,0,0,0,0,0,0,0,0,0)
  }
}
