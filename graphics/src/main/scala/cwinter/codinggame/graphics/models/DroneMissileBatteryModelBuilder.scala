package cwinter.codinggame.graphics.models

import cwinter.codinggame.graphics.engine.RenderStack
import cwinter.codinggame.graphics.model.{Model, ModelBuilder, SquarePrimitive, StaticCompositeModel}
import cwinter.codinggame.graphics.models.DroneColors._
import cwinter.codinggame.util.maths.VertexXY
import cwinter.codinggame.worldstate.Player


case class DroneMissileBatteryModelBuilder(player: Player, position: VertexXY, n: Int)(implicit rs: RenderStack)
  extends ModelBuilder[DroneMissileBatteryModelBuilder, Unit] {
  override def signature: DroneMissileBatteryModelBuilder = this

  override protected def buildModel: Model[Unit] = {
    /*
    val positions = Seq(
          VertexXY(1, 1), VertexXY(0, 1), VertexXY(-1, 1),
          VertexXY(1, 0), VertexXY(0, 0), VertexXY(-1, 0),
          VertexXY(1, -1), VertexXY(0, -1), VertexXY(-1, -1))
          */
    val positions = Seq(VertexXY(1, 1), VertexXY(1, -1), VertexXY(-1, -1), VertexXY(-1, 1))

    val components =
      for {
        pos <- positions
        offset = pos * 3
        segment <- buildSegment(offset + position)
      } yield segment

    new StaticCompositeModel(components)
  }


  def buildSegment(midpoint: VertexXY): Seq[Model[Unit]] = {
    val background =
      SquarePrimitive(
        rs.MaterialXYRGB,
        midpoint.x,
        midpoint.y,
        2.5fd,
        player.color,
        1
      ).getModel

    val element =
      SquarePrimitive(
        rs.BloomShader,
        midpoint.x,
        midpoint.y,
        1.5f,
        White,
        2
      ).getModel

    Seq(background, element)
  }
}
