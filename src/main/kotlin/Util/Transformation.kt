package Util

import Camera
import Entity.Entity
import org.joml.Matrix4f
import org.joml.Vector3f

class Transformation {

    companion object {
        fun createTransformationMatrix(entity: Entity): Matrix4f {
            var matrix = Matrix4f()
            matrix.identity().translate(entity.getPos())
                .rotateX(Math.toRadians(entity.getRotation().x.toDouble()).toFloat())
                .rotateY(Math.toRadians(entity.getRotation().y.toDouble()).toFloat())
                .rotateZ(Math.toRadians(entity.getRotation().z.toDouble()).toFloat())
                .scale(entity.getScale())
            return matrix
        }

        fun getViewMatrix(camera:Camera): Matrix4f {
            var pos = camera.position
            var rot = camera.rotation
            var matrix = Matrix4f()

            matrix.identity()
            matrix.rotate(Math.toRadians(rot.x.toDouble()).toFloat(), Vector3f(1f, 0f, 0f))
                .rotate(Math.toRadians(rot.y.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))
                .rotate(Math.toRadians(rot.z.toDouble()).toFloat(), Vector3f(0f, 0f, 1f))
            matrix.translate(-pos.x, -pos.y, -pos.z)
            return matrix
        }
    }

}
