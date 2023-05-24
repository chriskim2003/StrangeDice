package Util

import Camera
import Engine
import Entity.Entity
import Entity.Model
import Shader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Render {

    private var window: Engine? = null
    private var shader:Shader? = null

    fun init() {
        window = Engine.getInstance()!!
        shader = Shader()
        shader!!.createVertexShader(Utils.loadResource("/shaders/vertex.vs"))
        shader!!.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shader!!.link()
        shader!!.createUniform("textureSampler")

        shader!!.createUniform("transformationMatrix")
        shader!!.createUniform("projectionMatrix")
        shader!!.createUniform("viewMatrix")
    }

    fun render(entity: Entity, camera: Camera) {
        clear()
        shader!!.bind()
        shader!!.setUniform("textureSampler", 0)
        shader!!.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity))
        shader!!.setUniform("projectionMatrix", window!!.updateProjectionMatrix()!!)
        shader!!.setUniform("viewMatrix", Transformation.getViewMatrix(camera))
        GL30.glBindVertexArray(entity.getModel().getId())
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId())
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0)
        System.out.printf("Print Triangle %d\n", entity.getModel().getId())
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
        shader!!.unbind()
    }

    private fun clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

    fun cleanup() {
        shader!!.cleanup()
    }
}