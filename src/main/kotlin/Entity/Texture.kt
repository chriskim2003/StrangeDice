package Entity

class Texture(id:Int) {

    var id:Int = 0
    init {
        this.id = id
    }

    @JvmName("getId1")
    fun getId() : Int {
        return id
    }
}