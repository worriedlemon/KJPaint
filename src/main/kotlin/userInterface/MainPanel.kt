package kjpaint.userInterface

import kjpaint.base.*
import kjpaint.base.Rectangle
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JPanel

class MainPanel(private val mainWindow: MainWindow) : JPanel() {
    // Auxiliary variables for graphics
    private lateinit var graphics2D: Graphics2D
    private var image: Image? = null

    // Sizes a.k.a. thickness values
    var brushThickness: Int = 3
    var eraserThickness: Int = 5
    var outlineThickness: Int = 1

    // Primary (for brush and borders) and secondary (for filler and eraser) colors
    var outlineColor: Color = Color.BLACK
        set(value) {
            field = value
            graphics2D.paint = value
        }

    var fillColor: Color = Color.WHITE
        set(value) {
            field = value
            graphics2D.background = value
        }

    var shapesFill = false

    init {
        cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
    }

    // Adding mouse listeners
    fun initializeMouseListeners() {
        addMouseListener(mainWindow.mouseListener)
        addMouseListener(FocusRequestByMouseClickListener(mainWindow))
        addMouseMotionListener(mainWindow.mouseListener)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (image == null) {
            image = createImage(size.width, size.height)
            graphics2D = (image!!.graphics as Graphics2D).apply {

                // Using following for antialiasing:
                // setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                background = Color.WHITE
                clearRect(0, 0, size.width, size.height)
            }
            outlineColor = Color.BLACK
            fillColor = Color.WHITE
        }
        g?.drawImage(image, 0, 0, null)
    }

    // Function, which changes the size values
    fun changeThicknessValue(id: Int, value: Int) {
        when (id) {
            0 -> brushThickness = value
            1 -> eraserThickness = value
            2 -> {
                outlineThickness = value
                graphics2D.stroke = BasicStroke(outlineThickness.toFloat())
            }
            else -> throw IllegalArgumentException()
        }
    }

    // Function, which fills the closed contour (or the whole field if there are none)
    fun fill(x: Int, y: Int) {
        graphics2D.paint = fillColor
        val lastState = saveState(true)!!
        val colorToFill = lastState.getRGB(x, y)
        val aux = Array(size.width) { Array(size.height) { colorToFill } }
        val points = mutableListOf<Point>()
        points.add(Point(x, y))
        graphics2D.fillRect(x, y, 1, 1)
        aux[x][y] = 0
        while (points.isNotEmpty()) {
            val point = points.removeFirst()
            val x1 = point.x
            val y1 = point.y
            if (x1 + 1 in 0 until size.width - 1 &&
                y1 in 0 until size.height - 1 &&
                lastState.getRGB(x1 + 1, y1) == colorToFill &&
                aux[x1 + 1][y1] != 0
            ) {
                points.add(Point(x1 + 1, y1))
                graphics2D.fillRect(x1 + 1, y1, 1, 1)
                aux[x1 + 1][y1] = 0
            }
            if (x1 - 1 in 0 until size.width - 1 &&
                y1 in 0 until size.height - 1 &&
                lastState.getRGB(x1 - 1, y1) == colorToFill &&
                aux[x1 - 1][y1] != 0
            ) {
                points.add(Point(x1 - 1, y1))
                graphics2D.fillRect(x1 - 1, y1, 1, 1)
                aux[x1 + 1][y1] = 0
            }
            if (x1 in 0 until size.width - 1 &&
                y1 + 1 in 0 until size.height - 1 &&
                lastState.getRGB(x1, y1 + 1) == colorToFill &&
                aux[x1][y1 + 1] != 0
            ) {
                points.add(Point(x1, y1 + 1))
                graphics2D.fillRect(x1, y1 + 1, 1, 1)
                aux[x1][y1 + 1] = 0
            }
            if (x1 in 0 until size.width - 1 &&
                y1 - 1 in 0 until size.height - 1 &&
                lastState.getRGB(x1, y1 - 1) == colorToFill &&
                aux[x1][y1 - 1] != 0
            ) {
                points.add(Point(x1, y1 - 1))
                graphics2D.fillRect(x1, y1 - 1, 1, 1)
                aux[x1][y1 - 1] = 0
            }
        }
        graphics2D.paint = outlineColor
        repaint()
    }

    // Function for drawing shapes (rectangles, ovals, lines)
    fun drawShape(shape: Shape2D) {
        when (shape) {
            is Oval -> {
                var w = shape.width
                var h = shape.height
                val x = if (w < 0) {
                    w = -w
                    shape.x - w
                } else shape.x
                val y = if (h < 0) {
                    h = -h
                    shape.y - h
                } else shape.y

                graphics2D.drawOval(x, y, w, h)
                if (shapesFill) {
                    graphics2D.color = fillColor
                    graphics2D.fillOval(x, y, w, h)
                    graphics2D.color = outlineColor
                }
            }
            else -> {
                graphics2D.drawPolygon(shape as Polygon)
                if (shapesFill && shape is Rectangle) {
                    graphics2D.color = fillColor
                    graphics2D.fillPolygon(
                        if (outlineThickness % 2 == 1) shape.correctFiller()
                        else shape
                    )
                    graphics2D.color = outlineColor
                }
            }
        }
        repaint()
    }

    // Function for erasing (right mouse button)
    fun erase(x1: Int, y1: Int, x2: Int, y2: Int) {
        graphics2D.stroke = BasicStroke(eraserThickness.toFloat())
        graphics2D.paint = fillColor
        if (Point(x1, y1).distance(x2.toDouble(), y2.toDouble()) < eraserThickness - 1) {
            graphics2D.clearRect(
                x1 - eraserThickness / 2,
                y1 - eraserThickness / 2,
                eraserThickness,
                eraserThickness
            )
        } else {
            graphics2D.drawPolygon(Line(x1, y1, x2, y2))
        }
        graphics2D.paint = outlineColor
        graphics2D.stroke = BasicStroke(outlineThickness.toFloat())
        repaint()
    }

    // Function for drawing with brush (using no shape)
    fun draw(x1: Int, y1: Int, x2: Int, y2: Int) {
        graphics2D.stroke = BasicStroke(brushThickness.toFloat())
        if (Point(x1, y1).distance(x2.toDouble(), y2.toDouble()) < brushThickness - 1) {
            graphics2D.fillOval(
                x1 - brushThickness / 2,
                y1 - brushThickness / 2,
                brushThickness,
                brushThickness
            )
        } else {
            graphics2D.drawPolygon(Line(x1, y1, x2, y2))
        }
        graphics2D.stroke = BasicStroke(outlineThickness.toFloat())
        repaint()
    }

    // Function for saving image in buffer
    fun saveState(final: Boolean): BufferedImage? {
        val image = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
        val gr2d: Graphics2D = image.createGraphics()
        paintAll(gr2d)
        return if (final) image else {
            ProgramModel.undoLayers.add(image)
            null
        }
    }

    // Function for loading the previous state
    fun loadState(withRemoval: Boolean = true) {
        ProgramModel.redoLayers.add(saveState(true)!!)

        val image: BufferedImage? = try {
            ProgramModel.undoLayers.last()
        } catch (e: NoSuchElementException) {
            null
        }

        if (image != null) {
            graphics2D.drawImage(image, null, 0, 0)
            if (withRemoval) {
                ProgramModel.undoLayers.removeLast()
            }
            repaint()
        }
    }

    // Function for reloading from the "undo" state
    fun reloadState() {
        val image: BufferedImage? = try {
            ProgramModel.redoLayers.removeLast()
        } catch (e: NoSuchElementException) {
            null
        }

        if (image != null) {
            ProgramModel.undoLayers.add(saveState(true)!!)
            graphics2D.drawImage(image, null, 0, 0)
            repaint()
        }
    }
}