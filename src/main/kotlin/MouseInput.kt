import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorEnterCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback

class MouseInput {

    val previousPos:Vector2d = Vector2d(-1.0, -1.0)
    val currentPos:Vector2d = Vector2d(0.0, 0.0)
    val displVec:Vector2f = Vector2f()

    var inWindow = false
    var leftButtonPress = false
    var rightButtonPress = false

    fun init() {
        GLFW.glfwSetCursorPosCallback(Engine.getInstance()!!.getWindow()!!, object : GLFWCursorPosCallback() {
            override fun invoke(window: Long, xpos: Double, ypos: Double) {
                currentPos.x = xpos
                currentPos.y = ypos
            }
        })

        GLFW.glfwSetCursorEnterCallback(Engine.getInstance()!!.getWindow()!!, object  : GLFWCursorEnterCallback() {
            override fun invoke(window: Long, entered: Boolean) {
                inWindow = entered
            }
        })

        GLFW.glfwSetMouseButtonCallback(Engine.getInstance()!!.getWindow()!!, object  : GLFWMouseButtonCallback() {
            override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
                leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS
                rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS
            }
        })
    }

    fun input() {
        displVec.x = 0f
        displVec.y = 0f

        if(previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            var x = currentPos.x - previousPos.x
            var y = currentPos.y - previousPos.y
            var rotateX = x != 0.0
            var rotateY = y != 0.0
            if(rotateX) displVec.y = x.toFloat()
            if(rotateY) displVec.x = y.toFloat()
        }

        previousPos.x = currentPos.x
        previousPos.y = currentPos.y
    }
}