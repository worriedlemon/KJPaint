package kjpaint.base

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

const val ICONS_PATH = "src/main/resources/icons"
const val OUTPUT_PATH = "."
const val DOCUMENTS_PATH = "src/main/resources/doc"
const val JAR_RESOURCES = "/resources"

enum class ShapeType {
    NONE, BUCKET, RECTANGLE, OVAL, LINE
}

fun String.toHtml(): String {
    return "<html><center>${this.replace("\n", "<br>")}</center></html>"
}

object ProgramModel {
    // Buffers
    val undoLayers: MutableList<BufferedImage> = mutableListOf()
    val redoLayers: MutableList<BufferedImage> = mutableListOf()

    // Chosen shape
    var shapeType = ShapeType.NONE

    fun saveImage(
        fullPath: String,
        filename: String,
        format: String,
        image: BufferedImage
    ) {
        try {
            ImageIO.write(image, format, File("$fullPath/$filename.$format".replace("//", "/")))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}