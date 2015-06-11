package cwinter.codecraft.core.errors

import cwinter.codecraft.graphics.engine.Debug
import cwinter.codecraft.util.maths.{ColorRGBA, Vector2}

private[core] class ErrorMessageObject(
  val message: String,
  val errorLevel: ErrorLevel,
  var position: Vector2,
  val lifetime: Int = 90
) {
  private[this] var age = 0

  def update(): Unit = {
    val color = ColorRGBA(errorLevel.color, 1 - (age.toFloat / lifetime))
    Debug.drawText(message, position.x.toFloat, position.y.toFloat, color)
    position += Vector2(0, 1)
    age += 1
  }

  def hasFaded: Boolean = age >= lifetime
}
