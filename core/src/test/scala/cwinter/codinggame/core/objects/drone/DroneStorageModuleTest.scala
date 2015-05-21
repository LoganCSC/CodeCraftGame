package cwinter.codinggame.core.objects.drone

import cwinter.codinggame.core.MineralCrystalHarvested
import cwinter.codinggame.core.api.DroneSpec
import cwinter.codinggame.core.objects.MineralCrystal
import cwinter.codinggame.util.maths.Vector2
import cwinter.codinggame.worldstate.BluePlayer
import org.scalatest.FlatSpec

class DroneStorageModuleTest extends FlatSpec {
  val mockDrone1 = new Drone(DroneSpec(5, storageModules = 4), null, BluePlayer, Vector2.NullVector, 0)
  val mockDrone2 = new Drone(DroneSpec(4, storageModules = 2), null, BluePlayer, Vector2.NullVector, 0)
  val mineralCrystal = new MineralCrystal(2, Vector2.NullVector)
  val storageModule1 = new DroneStorageModule((0 to 3).toSeq, mockDrone1)
  val storageModule2 = new DroneStorageModule((0 to 2).toSeq, mockDrone2)

  "A drone storage module" should "harvest a mineral in less than 1000 timesteps" in {
    storageModule2.harvestMineral(mineralCrystal)

    val (events, _, _) = DroneModuleTestHelper.multipleUpdates(storageModule2, 1000)
    assert(events.contains(MineralCrystalHarvested(mineralCrystal)))
    assert(storageModule2.storedMinerals.contains(mineralCrystal))
  }


  it should "be able to deposit its mineral crystals in another storage module" in {
    storageModule2.depositMinerals(Some(storageModule1))

    DroneModuleTestHelper.multipleUpdates(storageModule2, 1000)

    assert(!storageModule2.storedMinerals.contains(mineralCrystal))
    assert(storageModule1.storedMinerals.contains(mineralCrystal))
  }
}