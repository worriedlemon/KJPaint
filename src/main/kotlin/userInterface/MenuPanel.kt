package kjpaint.userInterface

import kjpaint.base.DOCUMENTS_PATH
import kjpaint.base.JAR_RESOURCES
import kjpaint.base.OUTPUT_PATH
import kjpaint.base.ProgramModel
import java.awt.*
import java.io.FileInputStream
import javax.swing.*
import javax.swing.JOptionPane.*
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import kotlin.system.exitProcess

class MenuPanel(private val paintingPanel: MainPanel, private val jar : Boolean = false) : JMenuBar() {
    val saveDialog = createSaveDialogWindow()
    private val faq = createHelp()
    private val about = createAbout()

    init {
        add(JMenu("File").apply {
            add(JMenuItem("Save").apply {
                addActionListener {
                    saveDialog.isVisible = true
                }
                toolTipText = "Ctrl + S"
            })
            addSeparator()
            add(JMenuItem("Exit").apply {
                addActionListener {
                    val dialog = showConfirmDialog(
                        this,
                        "Are you sure?",
                        "Exit",
                        YES_NO_OPTION,
                        QUESTION_MESSAGE
                    )
                    if (dialog == YES_OPTION) exitProcess(0)
                }
            })
        })
        add(JMenu("Edit").apply {
            add(JMenuItem("Undo").apply {
                addActionListener {
                    paintingPanel.loadState(true)
                }
                toolTipText = "Ctrl + Z"
            })
            add(JMenuItem("Redo").apply {
                addActionListener {
                    paintingPanel.reloadState()
                }
                toolTipText = "Ctrl + Y"
            })
        })
        add(JMenu("Help").apply {
            add(JMenuItem("FAQ").apply {
                addActionListener {
                    faq.isVisible = true
                }
            })
            addSeparator()
            add(JMenuItem("About").apply {
                addActionListener {
                    about.isVisible = true
                }
            })
        })
    }

    // Save image to file dialog
    private fun createSaveDialogWindow(): JDialog {
        val dialog = JDialog(JFrame(), "Saving to file")

        val filenameField = JTextField("output")
        val filetypeChooser = JComboBox<String>().apply {
            addItem("png")
            addItem("bmp")
            addItem("jpeg")
        }
        val chooser = JFileChooser(OUTPUT_PATH).apply {
            dialogTitle = "Choose folder"
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            approveButtonText = "Select"
        }
        val directoryTextField = JTextField(chooser.currentDirectory.path)

        val optionsPanel = JPanel(GridLayout(1, 2, 10, 0)).apply {
            add(JButton("Save").apply {
                addActionListener {
                    ProgramModel.saveImage(
                        directoryTextField.text,
                        filenameField.text,
                        filetypeChooser.selectedItem as String,
                        paintingPanel.saveState(true)!!
                    )
                    dialog.isVisible = false
                }
            })
            add(JButton("Cancel").apply {
                addActionListener {
                    dialog.isVisible = false
                }
            })
        }

        return dialog.apply {
            setLocationRelativeTo(null)
            isResizable = false
            setSize(480, 320)
            rootPane.contentPane.add(JPanel().apply {
                layout = GridBagLayout()
                border = BorderFactory.createEmptyBorder(0, 10, 0, 10)

                val constraints = GridBagConstraints().apply {
                    fill = GridBagConstraints.HORIZONTAL
                    weighty = 1.0
                    ipady = 30
                    ipadx = 10
                }

                // First row
                constraints.apply {
                    weightx = 0.0
                    gridy = 0
                    gridx = 0
                }
                add(JLabel("File name:"), constraints)
                constraints.apply {
                    weightx = 0.9
                    gridx = 1
                    gridwidth = 2
                }
                add(filenameField, constraints)

                // Second row
                constraints.apply {
                    weightx = 0.0
                    gridy = 1
                    gridx = 0
                    gridwidth = 1
                }
                add(JLabel("File extension:"), constraints)

                constraints.apply {
                    weightx = 0.9
                    gridx = 1
                    gridwidth = 2
                }
                add(filetypeChooser, constraints)

                // Third row
                constraints.apply {
                    weightx = 0.0
                    gridy = 2
                    gridx = 0
                    gridwidth = 1
                }
                add(JLabel("Output directory:"), constraints)
                constraints.apply {
                    weightx = 0.9
                    gridx = 1
                }
                add(directoryTextField, constraints)
                constraints.apply {
                    weightx = 0.0
                    gridx = 2
                }
                add(JButton("Browse").apply {
                    addActionListener {
                        if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                            directoryTextField.text = chooser.selectedFile.path
                        }
                    }
                }, constraints)

                // Fourth row
                constraints.apply {
                    weightx = 0.5
                    gridx = 0
                    gridy = 3
                    gridwidth = 3
                    ipadx = 0
                    ipady = 15
                }
                add(optionsPanel, constraints)
            })
        }
    }

    // About window
    private fun createAbout(): JFrame {
        return JFrame("Help").apply {
            setSize(360, 224)
            setLocationRelativeTo(null)
            add(JPanel().apply {
                border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
                defaultCloseOperation = DISPOSE_ON_CLOSE
                val text = """
                    KJPaint (c:)
                    
                    Made as coursework for OOP classes in Electrotechnical University of Saint-Petersburg, Russia
                    
                    May, 2022   
                """.trimIndent()
                add(JTextArea(text, 7, 30).apply {
                    isOpaque = false
                    background = Color(0, 0, 0, 0)
                    wrapStyleWord = true
                    lineWrap = true
                    font = Font("Comic Sans MS", Font.BOLD, 14)
                    isEditable = false
                })
            })
        }
    }

    // Help window
    private fun createHelp(): JFrame {
        return JFrame("Help").apply {
            setSize(576, 360)
            setLocationRelativeTo(null)
            defaultCloseOperation = DISPOSE_ON_CLOSE
            add(JPanel().apply {
                border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
                val faqText = if (!jar) {
                    String(FileInputStream("$DOCUMENTS_PATH/faq.txt").readAllBytes())
                } else {
                    val stream = this::class.java.getResourceAsStream("${JAR_RESOURCES}/doc/faq.txt")

                    if (stream == null) {
                        "Could not load help"
                    } else {
                        String(stream.readAllBytes())
                    }
                }
                add(JScrollPane(JTextArea(
                    faqText,
                    20,
                    48
                ).apply {
                    wrapStyleWord = true
                    lineWrap = true
                }).apply {
                    verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                    horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                })
            })
        }
    }
}