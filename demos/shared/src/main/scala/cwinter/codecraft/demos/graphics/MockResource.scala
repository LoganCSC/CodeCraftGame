package cwinter.codecraft.demos.graphics

import cwinter.codecraft.graphics.worldstate.{PositionDescriptor, ModelDescriptor, MineralDescriptor, WorldObjectDescriptor}


private[graphics] class MockResource(
  val xPos: Float,
  val yPos: Float,
  val orientation: Float,
  val size: Int
) extends MockObject {

  override def update(): Unit = ()

  override def state(): ModelDescriptor[_] =
    ModelDescriptor(
      PositionDescriptor(xPos, yPos, orientation),
      MineralDescriptor(size)
    )

  def dead = false
}
