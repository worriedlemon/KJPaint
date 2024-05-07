package kjpaint.base

import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.system.exitProcess

// Window Listener which prevents application from unexpected exit
class ClosingWindowListener(private val window: JFrame) : WindowListener {
    override fun windowOpened(e: WindowEvent?) {
        println("Window opened")
    }

    override fun windowClosing(e: WindowEvent?) {
        val dialog = JOptionPane.showConfirmDialog(
            window,
            "Are you sure?",
            "Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        )

        if (dialog == JOptionPane.YES_OPTION) {
            windowClosed(e)
            exitProcess(0)
        }
    }

    override fun windowClosed(e: WindowEvent?) {
        window.dispose()
        println("Window closed")
    }

    override fun windowIconified(e: WindowEvent?) {}

    override fun windowDeiconified(e: WindowEvent?) {}

    override fun windowActivated(e: WindowEvent?) {}

    override fun windowDeactivated(e: WindowEvent?) {}

}