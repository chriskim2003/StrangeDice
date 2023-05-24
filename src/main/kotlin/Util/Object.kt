package Util

import Entity.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Object {

    private var arr = ArrayList<Int>();
    private var buff = ArrayList<Int>();
    private var textures = ArrayList<Int>();

    fun loadModel(vertices:FloatArray, textureCoords:FloatArray, indices:IntArray) : Model {
        var id:Int = createVAO()
        storeIndicesBuffer(indices)
        storeDataInAttribList(0, 3, vertices)
        storeDataInAttribList(1, 2, textureCoords)
        unbind()
        return Model(id, indices.size)
    }

    fun loadTexture(filename:String): Int {
        var width:Int? = null
        var height:Int? = null
        var buffer:ByteBuffer? = null
        var stack:MemoryStack = MemoryStack.stackPush()
        try {
            var w:IntBuffer = stack.mallocInt(1)
            var h:IntBuffer = stack.mallocInt(1)
            var c:IntBuffer = stack.mallocInt(1)

            buffer = STBImage.stbi_load(filename, w, h, c, 4)!!

            width = w.get()
            height = h.get()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
        }

        var id:Int = GL11.glGenTextures()
        textures.add(id)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width!!, height!!, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer!!)
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        STBImage.stbi_image_free(buffer)
        return id
    }

    private fun createVAO(): Int {
        var id:Int = GL30.glGenVertexArrays()
        arr.add(id)
        GL30.glBindVertexArray(id)
        return id
    }

    private fun storeIndicesBuffer(indices:IntArray) {
        var vbo:Int = GL15.glGenBuffers()
        buff.add(vbo)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo)
        var buffer:IntBuffer = Utils.storeDataInIntBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    private fun storeDataInAttribList(attribNo:Int, vertexCount:Int, data:FloatArray) {
        var vbo:Int = GL15.glGenBuffers()
        buff.add(vbo)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        var buffer:FloatBuffer = Utils.storeDataInFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun unbind() {
        GL30.glBindVertexArray(0)
    }

    fun cleanup() {
        for(vao in arr) {
            GL30.glDeleteVertexArrays(vao)
        }
        for(vbo in buff) {
            GL30.glDeleteBuffers(vbo)
        }
        for(texture in textures) {
            GL11.glDeleteTextures(texture)
        }
    }

}