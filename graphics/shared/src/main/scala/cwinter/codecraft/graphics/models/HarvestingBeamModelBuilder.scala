package cwinter.codecraft.graphics.models

import cwinter.codecraft.graphics.engine.RenderStack
import cwinter.codecraft.graphics.model._
import cwinter.codecraft.graphics.primitives.PartialPolygon
import cwinter.codecraft.graphics.worldstate._
import cwinter.codecraft.util.maths.{ColorRGBA, VertexXY}
import cwinter.codecraft.util.modules.ModulePosition


private[graphics] case class HarvestingBeamModelBuilder(
  signature: HarvestingBeamsDescriptor
)(implicit rs: RenderStack) extends CompositeModelBuilder[HarvestingBeamsDescriptor, Unit] {
  import signature._
  val radius = 8
  val outlineWidth = 1


  override protected def buildSubcomponents: (Seq[ModelBuilder[_, Unit]], Seq[ModelBuilder[_, Unit]]) = {
    val beams =
      for {
        moduleIndex <- moduleIndices
        modulePosition = ModulePosition(droneSize, moduleIndex)
      } yield buildBeamModel(modulePosition)

    (beams, Seq.empty)
  }

  private def buildBeamModel(position: VertexXY): ModelBuilder[_, Unit] = {
    val displacement = position.toVector2 - mineralDisplacement
    val angle =
      if (displacement.x == 0 && displacement.y == 0) 0
      else displacement.orientation.toFloat
    val radius = displacement.length
    val width = 20
    val alpha = math.Pi - 2 * math.atan2(radius, width / 2)

    val n = 5
    val n2 = n - 1
    val midpoint = (n2 - 1) / 2f
    val range = (n2 + 1) / 2f
    PartialPolygon(
      rs.TranslucentAdditive,
      n,
      Seq.fill(n)(ColorRGBA(0.5f, 1f, 0.5f, 0.7f)),
      ColorRGBA(0, 0, 0, 0) +: Seq.tabulate(n2)(i => {
        val color = 1 - Math.abs(i - midpoint) / range
        ColorRGBA(color / 2, color, color / 2, 0)
      }).flatMap(x => Seq(x, x)) :+ ColorRGBA(0, 0, 0, 0),
      radius.toFloat,
      position,
      0,
      angle,
      fraction = (alpha / (2 * math.Pi)).toFloat
    )
  }
}

