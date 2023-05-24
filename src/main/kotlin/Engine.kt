
import Entity.Entity
import Entity.Texture
import Util.Object
import Util.Render
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15.*
import org.lwjgl.system.MemoryUtil.NULL


class Engine {

    init{
        instance = this
    }

    companion object {
        private var instance:Engine? = null
        fun getInstance():Engine? {
            return instance
        }
        val WINDOW_SIZE = Pair(800, 600)
    }

    private var errorCallback : GLFWErrorCallback? = null
    private var keyCallback : GLFWKeyCallback? = null

    private var window : Long? = null

    private var renderer:Render? = null
    private var loader:Object? = null

    private var entity: Entity? = null

    private var camera: Camera? = null
    private var cameraInk: Vector3f = Vector3f(0f, 1.2f, 0f)

    private fun init() {
        errorCallback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
    
        if(!glfwInit()) {
            throw IllegalStateException("GLFW 로드할 수 없음")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        window = glfwCreateWindow(WINDOW_SIZE.first, WINDOW_SIZE.second, "테스트", NULL, NULL)
        if(window==NULL) {
            throw java.lang.RuntimeException("GLFW window 생성 실패")
        }

        keyCallback = glfwSetKeyCallback(window!!, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, false)
                }
            }
        })

        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        if (vidmode != null) {
            glfwSetWindowPos(window!!, (vidmode.width() - WINDOW_SIZE.first)/2, (vidmode.height() - WINDOW_SIZE.second)/2)
        };

        renderer = Render()
        loader = Object()

        camera = Camera()

        glfwMakeContextCurrent(window!!)
        glfwSwapInterval(1)

        glfwShowWindow(window!!)

    }

    private fun setup() {
        renderer!!.init()

        val vertices = floatArrayOf(
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f
        )
        val textureCoords = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.0f,
            0.5f, 0.5f,
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f
        )
        val indices = intArrayOf(
            0, 1, 3, 3, 1, 2,
            8, 10, 11, 9, 8, 11,
            12, 13, 7, 5, 12, 7,
            14, 15, 6, 4, 14, 6,
            16, 18, 19, 17, 16, 19,
            4, 6, 7, 5, 4, 7
        )

        var model = loader!!.loadModel(vertices, textureCoords, indices)
        model!!.setTexture(Texture(loader!!.loadTexture("textures/grassblock.png")))
        entity = Entity(model, Vector3f(0F, 0F, -5F), Vector3f(0F, 0F, 0F), 1F)
    }

    fun input() {

    }

    private var inited = false
    private fun loop() {
        GL.createCapabilities()

        while (!glfwWindowShouldClose(window!!)) {
            GL11.glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)


            if(!inited) {
                GL11.glClearColor(0f, 0f, 0f, 0f)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glEnable(GL11.GL_STENCIL_TEST)
                setup()
                System.out.println("INIT!")
                inited = true;
            }

            camera?.movePosition(cameraInk.x, cameraInk.y, cameraInk.z)
            entity!!.incRotation(2f, 2f, 0f)

            renderer!!.render(entity!!, camera!!)

            glfwSwapBuffers(window!!)
            glfwPollEvents()

            glfwSwapInterval(1)
        }
    }

    private val projectionMatrix:Matrix4f = Matrix4f()
    fun updateProjectionMatrix(): Matrix4f? {
        var aspectRatio = (WINDOW_SIZE.first / WINDOW_SIZE.second).toFloat()
        return projectionMatrix.setPerspective(Math.toRadians(60.0).toFloat(), aspectRatio, 0.01f, 1000f)
    }

    fun run() {
        try {
            init()
            loop()
            glfwDestroyWindow(window!!)
            keyCallback?.free()
        } finally {
            glfwTerminate()
            errorCallback?.free()
        }
    }

    fun getWindow(): Long? {
        return window
    }
}