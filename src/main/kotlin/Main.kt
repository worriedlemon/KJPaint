package kjpaint

import kjpaint.userInterface.MainWindow
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        MainWindow(true)
    }
}