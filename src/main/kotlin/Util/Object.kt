package Util

import Entity.Model
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.channels.Channels

class Object {

    private var arr = ArrayList<Int>();
    private var buff = ArrayList<Int>();
    private var textures = ArrayList<Int>();

    fun loadOBJModel(fileName:String): Model {
        var lines = Utils.readAllLines(fileName)

        var vertices = ArrayList<Vector3f>()
        var normals = ArrayList<Vector3f>()
        var textures = ArrayList<Vector3f>()
        var faces = ArrayList<Vector3i>()

        for(line in lines) {
            var tokens = line.split("\\s+".toRegex()).map { word ->
                word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
            }

            when(tokens[0]) {
                "v" -> {
                    var verticesVec = Vector3f(
                        tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()
                    )
                    vertices.add(verticesVec)
                    continue
                }
                "vt" -> {
                    var textureVec = Vector3f(
                        tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()
                    )
                    textures.add(textureVec)
                    continue
                }
                "vn" -> {
                    var normalsVec = Vector3f(
                        tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()
                    )
                    normals.add(normalsVec)
                    continue
                }
                "f" -> {
                    for (i in 1 until tokens.size) {
                        if(tokens[i].isNullOrEmpty()) continue
                        processFace(tokens[i], faces)
                    }
                    continue
                }
                else -> continue
            }
        }
        var indices = ArrayList<Int>()
        var verticesArr = FloatArray(vertices.size * 3)
        var i = 0
        for(pos in vertices) {
            verticesArr[i*3] = pos.x
            verticesArr[i*3 + 1] = pos.y
            verticesArr[i*3 + 2] = pos.z
            i++
        }

        var texCoordArr = FloatArray(vertices.size * 3)
        var normalArr = FloatArray(vertices.size * 3)

        for(face in faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalArr)
        }

        var indicesArr = indices.map { v:Int -> v }.toIntArray()
        return loadModel(verticesArr, texCoordArr, indicesArr)
    }

    companion object {

        fun processVertex(pos:Int, texCoord:Int, normal:Int, texCoordList:ArrayList<Vector3f>, normalList:ArrayList<Vector3f>
        , indicesList:ArrayList<Int>, texCoordArr:FloatArray, normalArr:FloatArray) {

            indicesList.add(pos)

            if(texCoord != -1) {
                var texCoordVec = texCoordList[texCoord]
                texCoordArr[pos * 3] = texCoordVec.x
                texCoordArr[pos * 3 + 1] = 1 - texCoordVec.y
                texCoordArr[pos * 3 + 2] = texCoordVec.z
            }

            if(normal != -1) {
                var normalVec = normalList[normal]
                normalArr[pos * 3] = normalVec.x
                normalArr[pos * 3 + 1] = normalVec.y
                normalArr[pos * 3 + 2] = normalVec.z
            }
        }
        fun processFace(token:String, faces:ArrayList<Vector3i>) {
            var lineToken = token.split("/")
            var length = lineToken.size
            var pos = -1
            var coords = -1
            var normal = -1
            if(lineToken[0].isNullOrEmpty()) println(token)
            pos = (lineToken[0].toInt()) - 1
            if (length > 1) {
                var textCoord = lineToken[1]
                coords = if (textCoord.length>0) {
                    textCoord.toInt() - 1
                } else {
                    -1
                }
                if (length > 2) {
                    normal = lineToken[2].toInt() - 1
                }
            }
            var facesVec = Vector3i(pos, coords, normal)
            faces.add(facesVec)
        }
    }

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

            var source: InputStream = this.javaClass.getResourceAsStream(filename)
            var buff = BufferUtils.createByteBuffer(1024*1024)
            var rbc = Channels.newChannel(source)
            while(true) {
                var bytes = rbc.read(buff)
                println(bytes)
                if(bytes == -1) {
                    break;
                }
                if(buff.remaining() == 0) {
                    buff = BufferUtils.createByteBuffer(buff.capacity()*2).put(buff.flip())
                }
            }
            buff.flip()
            buffer = STBImage.stbi_load_from_memory(buff, w, h, c, 4)!!

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