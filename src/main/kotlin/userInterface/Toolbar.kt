package kjpaint.userInterface

import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class Toolbar(paintingPanel: MainPanel) : JPanel(GridLayout(1, 0, 5, 0)) {
    init {
        preferredSize = Dimension(width, 40)
        add(JLabel("Brush size:", JLabel.CENTER))
        add(JSlider(0, 1, 10, paintingPanel.brushThickness).apply {
            isFocusable = false
            addChangeListener {
                paintingPanel.changeThicknessValue(0, value)
            }
        })
        add(JLabel("Eraser size:", JLabel.CENTER))
        add(JSlider(0, 1, 10, paintingPanel.eraserThickness).apply {
            isFocusable = false
            addChangeListener {
                paintingPanel.changeThicknessValue(1, value)
            }
        })
        add(JLabel("Outline size:", JLabel.CENTER))
        add(JSlider(0, 1, 10, paintingPanel.outlineThickness).apply {
            isFocusable = false
            addChangeListener {
                paintingPanel.changeThicknessValue(2, value)
            }
        })
        add(JCheckBox("Shape fill", false).apply {
            isFocusable = false
            addChangeListener {
                paintingPanel.shapesFill = !paintingPanel.shapesFill
            }
        })
    }
}