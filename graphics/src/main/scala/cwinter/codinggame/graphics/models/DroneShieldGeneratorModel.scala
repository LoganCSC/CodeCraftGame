package cwinter.codinggame.graphics.models

import cwinter.codinggame.graphics.engine.RenderStack
import cwinter.codinggame.graphics.model._
import cwinter.codinggame.graphics.models.DroneColors._
import cwinter.codinggame.util.maths.Geometry._
import cwinter.codinggame.util.maths.{Geometry, VertexXY}
import cwinter.codinggame.worldstate.Player


case class DroneShieldGeneratorModel(position: VertexXY, player: Player)(implicit rs: RenderStack)
  extends ModelBuilder[DroneShieldGeneratorModel, Unit] {
  def signature = this


  protected def buildModel: Model[Unit] = {
    val radius = 3
    val gridposRadius = 2 * inradius(radius, 6)
    val gridpoints = VertexXY(0, 0) +: polygonVertices(6, radius = gridposRadius)
    val hexgrid =
      for (pos <- gridpoints)
      yield
        PolygonRing(
          material = rs.MaterialXYRGB,
          n = 6,
          colorInside = White,
          colorOutside = White,
          innerRadius = radius - 0.5f,
          outerRadius = radius,
          position = pos + position,
          zPos = 1
        ).getModel

    val filling =
      for (pos <- gridpoints)
      yield
        Polygon(
          material = rs.MaterialXYRGB,
          n = 6,
          colorMidpoint = player.color,
          colorOutside = player.color,
          radius = radius - 0.5f,
          position = pos + position,
          zPos = 1
        ).getModel

    new StaticCompositeModel(hexgrid ++ filling)
  }
}
