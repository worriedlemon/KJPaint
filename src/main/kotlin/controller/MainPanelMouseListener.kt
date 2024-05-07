package kjpaint.controller

import kjpaint.base.ProgramModel
import kjpaint.base.*
import kjpaint.userInterface.MainPanel
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import kotlin.math.abs

class MainPanelMouseListener(
    private val paintingPanel: MainPanel,
    private val keyListener: HotKeysListener
) : MouseAdapter() {
    // Instant and buffer coordinates
    private var x1: Int = 0
    private var y1: Int = 0
    private var x2: Int = 0
    private var y2: Int = 0

    private var button: Int = 0

    override fun mousePressed(e: MouseEvent?) {
        if (e == null || e.button == 2) return

        // Saving previous state
        paintingPanel.saveState(false)

        // Clearing the buffer of redo layers
        ProgramModel.redoLayers.clear()

        x1 = e.x
        y1 = e.y

        // Getting pressed button (1 is LB, 3 is RB, 2 is Mouse Wheel)
        button = e.button
        if (button == 1) {
            if (ProgramModel.shapeType == ShapeType.NONE) {
                paintingPanel.draw(x1, y1, e.x, e.y)
            } else if (ProgramModel.shapeType == ShapeType.BUCKET) {
                paintingPanel.fill(e.x, e.y)
            }
        } else if (button == 3) {
            paintingPanel.erase(x1, y1, e.x, e.y)
        }
    }

    // Resetting mouse button
    override fun mouseReleased(e: MouseEvent?) {
        if (e == null) return
        button = 0
    }

    // Initializing drawing, when mouse is being moved with button pressed
    override fun mouseDragged(e: MouseEvent) {
        if (button == 1) {
            if (ProgramModel.shapeType == ShapeType.NONE) {
                paintingPanel.draw(x1, y1, e.x, e.y)
                x1 = e.x
                y1 = e.y
            } else {
                paintingPanel.loadState(false)

                x2 = e.x
                y2 = e.y

                // Set the coordinates of the right figures (square, circle, angled line with a step of 45 degrees)
                if (keyListener.shiftKey) {
                    val dx = abs(x2 - x1)
                    val dy = abs(y2 - y1)
                    if (ProgramModel.shapeType == ShapeType.LINE) {
                        if (dy.toDouble() / dx.toDouble() < 0.5) y2 = y1
                        else if (dy.toDouble() / dx.toDouble() > 2.0) x2 = x1
                        else {
                            if (dx > dy) x2 = x1 + if (x2 - x1 > 0) dy else -dy
                            else y2 = y1 + if (y2 - y1 > 0) dx else -dx
                        }
                    } else {
                        if (dx > dy) x2 = x1 + if (x2 - x1 > 0) dy else -dy
                        else y2 = y1 + if (y2 - y1 > 0) dx else -dx
                    }
                }

                // Drawing shapes
                paintingPanel.drawShape(
                    when (ProgramModel.shapeType) {
                        ShapeType.RECTANGLE -> Rectangle(x1, y1, x2, y2)
                        ShapeType.OVAL -> Oval(x1, y1, x2, y2)
                        ShapeType.LINE -> Line(x1, y1, x2, y2)
                        ShapeType.BUCKET -> return
                        else -> throw IllegalStateException()
                    }
                )
            }
        } else if (button == 3) {
            paintingPanel.erase(x1, y1, e.x, e.y)
            x1 = e.x
            y1 = e.y
        }
    }
}