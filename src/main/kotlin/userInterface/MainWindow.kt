package kjpaint.userInterface

import kjpaint.controller.*
import kjpaint.base.ClosingWindowListener
import kjpaint.base.FocusRequestByMouseClickListener
import kjpaint.base.MainWindowFocusListener
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel

class MainWindow(jar : Boolean = false) : JFrame("KJPaint") {
    private val paintingPanel = MainPanel(this)
    private val menuPanel = MenuPanel(paintingPanel, jar)
    private val keyListener = HotKeysListener(menuPanel, paintingPanel)
    val mouseListener = MainPanelMouseListener(paintingPanel, keyListener)

    init {
        // Setting up
        val mainContainer = JPanel().apply {
            border = BorderFactory.createEmptyBorder(0, 5, 5, 5)
            layout = BorderLayout()
        }
        isFocusable = true
        setSize(1280, 720)
        setLocationRelativeTo(null)
        rootPane.contentPane.add(mainContainer)
        defaultCloseOperation = DO_NOTHING_ON_CLOSE

        // Initializing listeners
        addWindowListener(ClosingWindowListener(this))
        addWindowFocusListener(MainWindowFocusListener(this))
        addMouseListener(FocusRequestByMouseClickListener(this))
        addKeyListener(keyListener)
        paintingPanel.initializeMouseListeners()

        // Adding panels
        jMenuBar = menuPanel
        mainContainer.apply {
            add(paintingPanel, BorderLayout.CENTER)
            add(Toolbar(paintingPanel), BorderLayout.NORTH)
            add(SelectionPanel(paintingPanel, jar), BorderLayout.WEST)
        }

        // Displaying
        isVisible = true
    }
}