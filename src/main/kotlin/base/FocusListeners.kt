package kjpaint.base

import kjpaint.userInterface.MainWindow
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

/* If window unfocuses because of other window or window component
 * interruption of keyboard listening tend to happen. This listeners
 * request for this focus back to main window
 */

class MainWindowFocusListener(private val window: MainWindow) : WindowFocusListener {
    override fun windowGainedFocus(e: WindowEvent?) {
        window.requestFocus()
    }

    override fun windowLostFocus(e: WindowEvent?) {}

}

class FocusRequestByMouseClickListener(
    private val window: MainWindow,
) : MouseAdapter() {
    override fun mousePressed(e: MouseEvent?) {
        window.requestFocus()
    }
}
