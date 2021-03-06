package cwinter.codecraft.core

import cwinter.codecraft.core.api.{Player, TheGameMaster, BluePlayer, OrangePlayer}
import cwinter.codecraft.core.game.{MultiplayerClientConfig, AuthoritativeServerConfig, DroneWorldSimulator}
import cwinter.codecraft.core.multiplayer.{LocalServerConnection, LocalClientConnection, LocalConnection}
import cwinter.codecraft.core.replay.DummyDroneController

private[codecraft] object IntraRuntimeMultiplayerTest {
  def main(args: Array[String]): Unit = {
    val clientPlayers = Set[Player](BluePlayer)
    val serverPlayers = Set[Player](OrangePlayer)
    val connection = new LocalConnection(Set(0))
    val clientConnection0 = new LocalClientConnection(0, connection, clientPlayers)
    val serverConnection = new LocalServerConnection(0, connection)
    val server = new DroneWorldSimulator(
      TheGameMaster.defaultMap.createGameConfig(
        droneControllers = Seq(new DummyDroneController, TheGameMaster.level1AI()),
        tickPeriod = 10
      ),
      t => Seq.empty,
      AuthoritativeServerConfig(serverPlayers, clientPlayers, Set(clientConnection0), s => (), s => ())
    )
    val client = new DroneWorldSimulator(
      TheGameMaster.defaultMap.createGameConfig(
        droneControllers = Seq(TheGameMaster.level2AI(), new DummyDroneController),
        tickPeriod = 10
      ),
      t => Seq.empty,
      MultiplayerClientConfig(clientPlayers, serverPlayers, serverConnection)
    )
    val displayClient = true
    if (displayClient) {
      server.run()
      TheGameMaster.run(client)
    } else {
      client.run()
      TheGameMaster.run(server)
    }
  }
}
