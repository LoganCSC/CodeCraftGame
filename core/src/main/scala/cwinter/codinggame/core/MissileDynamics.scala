package cwinter.codinggame.core

import cwinter.codinggame.core.drone.DroneDynamics
import cwinter.codinggame.util.maths.{Rectangle, Vector2}


class MissileDynamics(
  val speed: Double,
  val target: ConstantVelocityDynamics,
  initialPosition: Vector2,
  initialTime: Double
) extends ConstantVelocityDynamics(1, initialPosition, initialTime) {
  var hasHit = false

  override def handleObjectCollision(other: ConstantVelocityDynamics): Unit = {
    this.remove()

    hasHit = true
    other match {
      case otherMissile: MissileDynamics => otherMissile.remove()
      case otherDrone: DroneDynamics => otherDrone.drone.missileHit(pos)
    }
  }

  override def handleWallCollision(areaBounds: Rectangle): Unit = {
    this.remove()
    // just die on collision (or maybe bounce?)
  }

  override def update(): Unit = {
    val targetDirection = target.pos - pos
    if (!target.removed && targetDirection.size >= 0.0001) {
      velocity = speed * targetDirection.normalized
    }
  }
}
