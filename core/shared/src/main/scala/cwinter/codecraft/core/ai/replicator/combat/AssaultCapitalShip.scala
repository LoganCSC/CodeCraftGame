package cwinter.codecraft.core.ai.replicator.combat

import cwinter.codecraft.core.ai.shared.Mission
import cwinter.codecraft.core.api.Drone


class AssaultCapitalShip(enemy: Drone) extends Mission[ReplicatorCommand] {
  var minRequired = computeMinRequired
  def maxRequired = minRequired * 2
  val priority = 10
  var maxDist2: Option[Double] = None

  def locationPreference = Some(enemy.lastKnownPosition)

  private var searchRadius = 0.0

  def missionInstructions: ReplicatorCommand =
    if (enemy.isVisible || searchRadius == 0) Attack(maxDist2.getOrElse(0), enemy, this)
    else Search(enemy.lastKnownPosition, searchRadius)

  def notFound(): Unit = {
    searchRadius = 750
  }

  override def update(): Unit = {
    if (!enemy.isVisible && nAssigned > 0 && searchRadius > 0) searchRadius += 1
    else if (enemy.isVisible) searchRadius = 0

    maxDist2 =
      if (nAssigned == 0) None
      else {
        val sortedByDist =
          assigned.toSeq.sortBy(d => (enemy.lastKnownPosition - d.position).lengthSquared)
        val straggler = sortedByDist(math.min(nAssigned - 1, minRequired))
        Some((enemy.lastKnownPosition - straggler.position).length)
      }
  }

  def computeMinRequired: Int = {
    val hitpoints = if (enemy.isVisible) enemy.hitpoints else enemy.spec.maxHitpoints
    val strength = math.sqrt(enemy.spec.missileBatteries * hitpoints)
    math.ceil(strength / 2).toInt
  }

  def hasExpired = enemy.isDead
}
