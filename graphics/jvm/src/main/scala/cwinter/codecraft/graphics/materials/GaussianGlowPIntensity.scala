package cwinter.codecraft.graphics.materials

import com.jogamp.opengl.GL._
import com.jogamp.opengl.GL4

import cwinter.codecraft.util.maths.matrices.Matrix4x4
import cwinter.codecraft.graphics.model.{VBO, JVMVBO$}
import cwinter.codecraft.util.maths.{ColorRGBA, VertexXYZ}

private[graphics] class GaussianGlowPIntensity(implicit gl: GL4)
  extends JVMMaterial[VertexXYZ, ColorRGBA, Intensity](
    gl = gl,
    vsPath = "xyz_rgba_vs.glsl",
    fsPath = "rgba_gaussian_pint_fs.glsl",
    "vertexPos",
    Some("vertexCol"),
    GL_BLEND
  ) {
  import gl._
  val uniformIntensity = glGetUniformLocation(programID, "intensity")


  override def beforeDraw(projection: Matrix4x4): Unit = {
    super.beforeDraw(projection)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE)
  }

  override def draw(vbo: VBO, modelview: Matrix4x4): Unit = {
    glUniform1f(uniformIntensity, params.intensity)
    super.draw(vbo, modelview)
  }
}

