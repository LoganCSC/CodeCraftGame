package cwinter.codinggame.testai

import cwinter.codinggame.core.{MineralCrystal, DroneController, TheGameMaster}
import cwinter.codinggame.maths.{Rng, Vector2}

object Main {
  def main(args: Array[String]): Unit = {
    TheGameMaster.startGame(new Mothership)
  }
}


class Mothership extends DroneController {
  // abstract methods for event handling
  override def onSpawn(): Unit = {
    moveInDirection(Vector2(0, 1))
  }

  override def onTick(): Unit = {
    if (Rng.bernoulli(0.001)) {
      moveInDirection(Vector2(Rng.double(0, 100)))
    }
  }

  override def onMineralEntersVision(mineralCrystal: MineralCrystal): Unit = {
    if (mineralCrystal.size == 1) {
      moveToPosition(mineralCrystal.position)
      this.mineralCrystal = mineralCrystal
    } else {
      moveInDirection(mineralCrystal.position - position)
    }
  }

  var mineralCrystal: MineralCrystal = null
  override def onArrival(): Unit = {
    harvestMineral(mineralCrystal)
  }
}