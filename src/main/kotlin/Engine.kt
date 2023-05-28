
import Entity.Entity
import Entity.Texture
import Entity.UseShape
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

    private var entities = ArrayList<Entity>()

    private var mouse: MouseInput? = null
    private val CAMERA_MOVE_SPEED = 0.05f

    private var camera: Camera? = null
    private var cameraInk: Vector3f = Vector3f(0f, 0f, 0f)

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
                    glfwSetWindowShouldClose(window, true)
                }
            }
        })

        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        if (vidmode != null) {
            glfwSetWindowPos(window!!, (vidmode.width() - WINDOW_SIZE.first)/2, (vidmode.height() - WINDOW_SIZE.second)/2)
        };

        renderer = Render()
        loader = Object()

        mouse = MouseInput()

        camera = Camera()

        glfwMakeContextCurrent(window!!)
        glfwSwapInterval(1)

        glfwShowWindow(window!!)

    }

    private fun setup() {
        mouse!!.init()
        renderer!!.init()



        var couch = loader!!.loadOBJModel("/models/couch.obj")
        couch!!.setTexture(Texture(loader!!.loadTexture("/marble.png")))
        entities.add(Entity(couch, Vector3f(0F, -1F, -2F), Vector3f(0F, 0F, 0F), 0.001F, UseShape.SQUARE))

        var bunny = loader!!.loadOBJModel("/models/bunny.obj")
        bunny!!.setTexture(Texture(loader!!.loadTexture("/grassblock.png")))
        entities.add(Entity(bunny, Vector3f(0F, -0.5F, -2F), Vector3f(0F, 0F, 0F), 3F, UseShape.TRIANGLE))

        var airboat = loader!!.loadOBJModel("/models/airboat.obj")
        airboat!!.setTexture(Texture(loader!!.loadTexture("/marble.png")))
        entities.add(Entity(airboat, Vector3f(0f, 2f, -3f), Vector3f(0f, 0f, 0f), 0.1f, UseShape.SQUARE))
    }

    fun input() {
        mouse!!.input()

        cameraInk.set(0f, 0f, 0f)

        if(glfwGetKey(window!!, GLFW_KEY_A) == GLFW_PRESS) {
            cameraInk.x = -1f
        }

        if(glfwGetKey(window!!, GLFW_KEY_D) == GLFW_PRESS) {
            cameraInk.x = 1f
        }

        if(glfwGetKey(window!!, GLFW_KEY_W) == GLFW_PRESS) {
            cameraInk.z = -1f
        }

        if(glfwGetKey(window!!, GLFW_KEY_S) == GLFW_PRESS) {
            cameraInk.z = 1f
        }
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

            input()

            if(mouse!!.rightButtonPress) {
                var rotvec = mouse!!.displVec
                camera!!.moveRotation(rotvec.x * 0.2f, rotvec.y * 0.2f, 0f)
            }
            camera?.movePosition(cameraInk.x * CAMERA_MOVE_SPEED, cameraInk.y * CAMERA_MOVE_SPEED, cameraInk.z * CAMERA_MOVE_SPEED)

            for(entity in entities) {
                renderer!!.processEntity(entity)
            }

            renderer!!.render(camera!!)

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