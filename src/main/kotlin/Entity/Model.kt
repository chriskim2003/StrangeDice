package Entity

class Model() {

    private var id:Int = 0
    private var vertexCount:Int = 0
    private var texture: Texture? = null

    constructor(id:Int, vertexCount:Int) : this() {
        this.id = id
        this.vertexCount = vertexCount
    }

    constructor(id:Int, vertexCount:Int, texture:Texture) : this() {
        this.texture = texture;
        this.id = id
        this.vertexCount = vertexCount
    }

    constructor(model:Model, texture: Texture) : this() {
        this.id = model.getId()
        this.vertexCount = model.getVertexCount()
        this.texture = texture
    }

    fun getId(): Int {
        return id
    }

    fun getVertexCount(): Int {
        return vertexCount
    }

    fun getTexture():Texture {
        return texture!!
    }

    fun setTexture(texture:Texture) {
        this.texture = texture
    }

}