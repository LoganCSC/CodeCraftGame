package cwinter.codecraft.graphics.model

import cwinter.codecraft.graphics.materials.Material
import cwinter.codecraft.util.maths.Vertex
import cwinter.codecraft.util.maths.matrices.Matrix4x4


private[graphics] class StaticModel[TPosition <: Vertex, TColor <: Vertex, TParams](
  val vbo: VBO,
  val material: Material[TPosition, TColor, TParams]
) extends Model[TParams] {
  private[this] var activeVertexCount = vbo.size

  def update(params: TParams): Unit = { material.params = params }

  def draw(modelview: Matrix4x4, material: GenericMaterial): Unit =
    if (material == this.material)
      material.draw(vbo.withSize(activeVertexCount), modelview)

  def hasMaterial(material: GenericMaterial): Boolean =
    this.material == material

  def setVertexCount(n: Int): Unit = {
    assert(n % 3 == 0)
    assert(n >= 0)
    assert(n <= vertexCount)
    activeVertexCount = n
  }
  def vertexCount = vbo.size


  def prettyPrintTree(depth: Int): String =
    prettyPrintNode(depth, s"Static[${material.getClass.getSimpleName}]($vertexCount)")
}

