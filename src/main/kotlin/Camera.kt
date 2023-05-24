import org.joml.Vector3f

class Camera {

    var position:Vector3f
    var rotation:Vector3f

    constructor() {
        position = Vector3f(0f, 0f, 0f)
        rotation = Vector3f(0f, 0f, 0f)
    }

    constructor(position:Vector3f, rotation:Vector3f) : this() {
        this.position = position
        this.rotation = rotation
    }

    fun movePosition(x:Float, y:Float, z:Float) {
        if(z!=0f) {
            position.x += (Math.sin(Math.toRadians(rotation.y.toDouble()-90)) * -1f * z).toFloat()
            position.z += (Math.sin(Math.toRadians(rotation.y.toDouble()-90)) * z).toFloat()
        }
        if(x!=0f) {
            position.x += (Math.sin(Math.toRadians(rotation.y.toDouble()-90)) * -1f * x).toFloat()
            position.z += (Math.sin(Math.toRadians(rotation.y.toDouble()-90)) * x).toFloat()
        }
    }

    fun setPosition(x:Float, y:Float, z:Float) {
        this.position.x = x
        this.position.y = y
        this.position.z = z
    }

    fun setRotation(x:Float, y:Float, z:Float) {
        this.rotation.x = x
        this.rotation.y = y
        this.rotation.z = z
    }

    fun moveRotation(x:Float, y:Float, z:Float) {
        this.rotation.x += x
        this.rotation.y += y
        this.rotation.z += z
    }

}