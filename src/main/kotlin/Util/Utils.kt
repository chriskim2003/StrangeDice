package Util

import org.lwjgl.system.MemoryUtil
import java.io.InputStream
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.charset.StandardCharsets
import java.util.*

class Utils {

    companion object {
        fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
            val buffer: FloatBuffer = MemoryUtil.memAllocFloat(data.size)
            buffer.put(data).flip()
            return buffer
        }

        fun storeDataInIntBuffer(data: IntArray): IntBuffer {
            val buffer: IntBuffer = MemoryUtil.memAllocInt(data.size)
            buffer.put(data).flip()
            return buffer
        }


        fun loadResource(fileName: String): String {
            var result: String? = null
            val ins: InputStream = Utils::class.java.getResourceAsStream(fileName)!!
            val scanner = Scanner(ins, StandardCharsets.UTF_8.name())
            try {
                result = scanner.useDelimiter("\\A").next()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return result!!
        }

        fun readAllLines(fileName: String): List<String> {
            var list: List<String> = ArrayList<String>()
            //try(BufferedReader br = BufferedReader(InputStreamReader(Class.forName(Utils.class.getName))))
            return list
        }
    }
}