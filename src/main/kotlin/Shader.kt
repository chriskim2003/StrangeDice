import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack

class Shader {

    private var vertexShaderID : Int
    private var fragmentShaderID : Int

    private var programID : Int

    private var uniforms:MutableMap<String, Int>;

    init {
        vertexShaderID = 0
        fragmentShaderID = 0

        uniforms = HashMap<String, Int>()

        programID = GL20.glCreateProgram();
        if(programID == 0) throw java.lang.Exception("쉐이더 만들기 실패")
    }

    fun createUniform(uniformName:String) {
        var uniformLocation:Int = GL20.glGetUniformLocation(programID, uniformName)
        if(uniformLocation<0) throw Exception("uniform 찾을 수 없음 $uniformName")
        uniforms.put(uniformName, uniformLocation)
    }

    fun setUniform(uniformName: String, value:Matrix4f) {
        var stack:MemoryStack = MemoryStack.stackPush()
        try {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName)!!, false, value.get(stack.mallocFloat(16)))
        } catch(e:java.lang.Exception) {
            e.printStackTrace()
        }
        stack.close()
    }

    fun setUniform(uniformName: String, value: Vector3f) {
        GL20.glUniform3f(uniforms.get(uniformName)!!, value.x, value.y, value.z)
    }

    fun setUniform(uniformName:String, value:Boolean) {
        var res:Float = 0F
        if(value) res = 1F
        GL20.glUniform1f(uniforms.get(uniformName)!!, res)
    }

    fun setUniform(uniformName: String, value:Float) {
        GL20.glUniform1f(uniforms.get(uniformName)!!, value)
    }

    fun setUniform(uniformName: String, value:Int) {
        GL20.glUniform1i(uniforms.get(uniformName)!!, value)
    }

    fun createVertexShader(shaderCode:String) {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER)
    }

    fun createFragmentShader(shaderCode:String) {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER)
    }

    fun createShader(shaderCode:String, shaderType:Int) : Int {
        var shaderID:Int = GL20.glCreateShader(shaderType)
        if(shaderID == 0) throw java.lang.Exception("쉐이더 만들기 실패. : $shaderType")

        GL20.glShaderSource(shaderID, shaderCode)
        GL20.glCompileShader(shaderID)

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) throw Exception("쉐이더 컴파일 실패 : $shaderType Info " + GL20.glGetShaderInfoLog(shaderID, 1024))

        GL20.glAttachShader(programID, shaderID)

        return shaderType
    }

    fun link() {
        GL20.glLinkProgram(programID)
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) throw java.lang.Exception("쉐이더 링크 실패 Info " + GL20.glGetProgramInfoLog(programID, 1024))

        if(vertexShaderID != 0) GL20.glDetachShader(programID, vertexShaderID!!)

        if(fragmentShaderID != 0) GL20.glDetachShader(programID, fragmentShaderID!!)

        GL20.glValidateProgram(programID)
        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) throw java.lang.Exception("쉐이더 코드 증명 실패 " + GL20.glGetProgramInfoLog(programID, 1024))
    }

    fun bind() {
        GL20.glUseProgram(programID)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if(programID != 0) GL20.glDeleteProgram(programID)
    }
}