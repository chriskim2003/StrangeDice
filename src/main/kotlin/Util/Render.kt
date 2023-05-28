package Util

import Camera
import Engine
import Entity.Entity
import Entity.Model
import Entity.UseShape
import Shader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Render {

    private var window: Engine? = null
    private var shader:Shader? = null

    private var entities = HashMap<Model, ArrayList<Entity>>()

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

    fun bind(model: Model) {
        GL30.glBindVertexArray(model.getId())
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)

        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId())
    }

    fun unbind() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    fun prepare(entity: Entity, camera: Camera) {
        shader!!.setUniform("textureSampler", 0)
        shader!!.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity))
        shader!!.setUniform("viewMatrix", Transformation.getViewMatrix(camera))
    }

    fun render(camera: Camera) {
        clear()
        shader!!.bind()

        shader!!.setUniform("projectionMatrix", window!!.updateProjectionMatrix()!!)

        for(model in entities.keys) {
            bind(model)
            var entityList = entities[model]
            if (entityList != null) {
                for(entity in entityList) {
                    prepare(entity, camera)
                    if(entity.getShape() == UseShape.TRIANGLE)
                        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0)
                    else if(entity.getShape() == UseShape.SQUARE)
                        GL11.glDrawElements(GL11.GL_QUADS, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0)
                }
            }
            unbind()
        }
        entities.clear()

        shader!!.unbind()
    }

    fun processEntity(entity:Entity) {
        var entityList = entities.get(entity.getModel())
        if(entityList != null) {
            entityList.add(entity)
        } else {
            var newEntityList = ArrayList<Entity>()
            newEntityList.add(entity)
            entities.put(entity.getModel(), newEntityList)
        }
    }

    private fun clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

    fun cleanup() {
        shader!!.cleanup()
    }
}