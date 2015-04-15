package cwinter.codinggame.physics

import cwinter.codinggame.maths.{Vector2, Rng}
import robowars.worldstate.{Circle, WorldObject}


class MovingObject[TDynamics](
  val radius: Double,
  val objectDynamics: DynamicObject[TDynamics]
) {
  val id = UID()

  def state: WorldObject = Circle(id, objectDynamics.pos.x.toFloat, objectDynamics.pos.y.toFloat, radius.toFloat)

  override def toString: String = id.toString
}


object MovingObject {
  def apply(): MovingObject[ConstantVelocityObject] =
    MovingObject(Rng.vector2(-500, 500, -500, 500))

  def apply(position: Vector2) = {
    val weight = Rng.float(500, 1500)
    val radius = math.sqrt(weight)
    new MovingObject(radius, new ConstantVelocityObject(position, Rng.vector2(200), weight, radius))
  }
}


object UID {
  private[this] var _count = -1
  def apply(): Int = {
    _count += 1
    _count
  }
}
