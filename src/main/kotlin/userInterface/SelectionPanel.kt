package kjpaint.userInterface

import kjpaint.base.*
import java.awt.*
import javax.swing.*

class SelectionPanel(private val paintingPanel: MainPanel, private val jar : Boolean = false) : JPanel(GridLayout(5, 1, 10, 10)) {
    init {
        preferredSize = Dimension(120, height)
        val menu = createShapesMenu()
        add(JButton(getImageIcon("brush.png")).apply {
            isFocusable = false
            addActionListener {
                ProgramModel.shapeType = ShapeType.NONE
            }
        })
        add(JButton(getImageIcon("bucket.png")).apply {
            isFocusable = false
            addActionListener {
                ProgramModel.shapeType = ShapeType.BUCKET
            }
        })
        add(JButton("Shapes").apply {
            isFocusable = false
            addActionListener {
                menu.isVisible = true
            }
        })
        add(createOutlineColorChooser())
        add(createFillColorChooser())
    }

    private fun createOutlineColorChooser(): JButton {
        return JButton("Outline\nColor".toHtml()).apply {
            isFocusable = false
            addActionListener {
                val chosenColor: Color? = JColorChooser.showDialog(
                    JFrame(),
                    "Outline Color",
                    paintingPanel.outlineColor
                )
                if (chosenColor != null) paintingPanel.outlineColor = chosenColor
            }
        }
    }

    private fun createFillColorChooser(): JButton {
        return JButton("Fill\nColor".toHtml()).apply {
            isFocusable = false
            addActionListener {
                val chosenColor: Color? = JColorChooser.showDialog(
                    JFrame(),
                    "Fill Color",
                    paintingPanel.fillColor
                )
                if (chosenColor != null) paintingPanel.fillColor = chosenColor
            }
        }
    }

    private fun createShapesMenu(): JDialog {
        val dialog = JDialog(JFrame(), "Choose shape")
        return dialog.apply {
            setLocationRelativeTo(null)
            setSize(360, 160)
            layout = GridLayout(1, 3)
            contentPane.add(JButton(getImageIcon("rect.png")).apply {
                addActionListener {
                    isFocusable = false
                    ProgramModel.shapeType = ShapeType.RECTANGLE
                    dialog.isVisible = false
                }
            })
            contentPane.add(JButton(getImageIcon("oval.png")).apply {
                addActionListener {
                    isFocusable = false
                    ProgramModel.shapeType = ShapeType.OVAL
                    dialog.isVisible = false
                }
            })
            contentPane.add(JButton(getImageIcon("line.png")).apply {
                isFocusable = false
                addActionListener {
                    ProgramModel.shapeType = ShapeType.LINE
                    dialog.isVisible = false
                }
            })
        }
    }

    private fun getImageIcon(path : String): ImageIcon {
        return if (!jar) {
            ImageIcon("${ICONS_PATH}/${path}")
        } else {
            ImageIcon(Toolkit.getDefaultToolkit().getImage(this::class.java.getResource("${JAR_RESOURCES}/icons/${path}")))
        }
    }
}