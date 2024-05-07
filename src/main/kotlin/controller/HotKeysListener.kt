package kjpaint.controller

import kjpaint.userInterface.MainPanel
import kjpaint.userInterface.MenuPanel
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

/* Hot keys for program:
 *
 * Ctrl + S -- save image
 * Ctrl + Z -- undo
 * Ctrl + Y -- redo
 * Shift    -- is used for drawing "right" shapes
 */

class HotKeysListener(
    private val menuPanel: MenuPanel,
    private val paintingPanel: MainPanel
) : KeyListener {
    var controlKey = false
    var shiftKey = false

    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent?) {
        when (e!!.keyCode) {
            KeyEvent.VK_CONTROL -> controlKey = true
            KeyEvent.VK_SHIFT -> shiftKey = true
            KeyEvent.VK_S -> if (controlKey) {
                menuPanel.saveDialog.isVisible = true
                controlKey = false
            }
            KeyEvent.VK_Z -> if (controlKey) paintingPanel.loadState()
            KeyEvent.VK_Y -> if (controlKey) paintingPanel.reloadState()
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        when (e!!.keyCode) {
            KeyEvent.VK_CONTROL -> controlKey = false
            KeyEvent.VK_SHIFT -> shiftKey = false
        }
    }

}