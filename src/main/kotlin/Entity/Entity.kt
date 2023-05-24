package Entity

import org.joml.Vector3f

class Entity(model:Model, pos:Vector3f, rotation:Vector3f, scale:Float) {

    private var model: Model
    private var pos: Vector3f
    private var rotation: Vector3f
    private var scale: Float

    init {
        this.model = model
        this.pos = pos
        this.rotation = rotation
        this.scale = scale
    }

    fun incPos(x:Float, y:Float, z:Float) {
        pos.x += x
        pos.y += y
        pos.z += z
    }

    fun incRotation(x:Float, y:Float, z:Float) {
        rotation.x += x
        rotation.y += y
        rotation.z += z
    }

    fun setPos(x:Float, y:Float, z:Float) {
        pos.x += x
        pos.y += y
        pos.z += z
    }

    fun setRotation(x:Float, y:Float, z:Float) {
        rotation.x += x
        rotation.y += y
        rotation.z += z
    }

    fun getModel():Model {
        return model
    }

    fun getPos():Vector3f {
        return pos
    }

    fun getRotation():Vector3f {
        return rotation
    }

    fun getScale():Float {
        return scale
    }
}