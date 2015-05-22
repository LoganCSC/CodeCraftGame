package cwinter.codinggame.util.maths

import scala.math._

object Functions {
  def gaussian(x: Double): Double =
    sqrt(2 * Pi) * exp(-x * x)
}
