import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.input.TransferMode
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

import kotlinx.coroutines.*

class GUI : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Study 7-Zip JBinding!"
        primaryStage.isAlwaysOnTop = true
        val fxml = javaClass.getResource("fxml/Main.fxml")
        val root: Parent = FXMLLoader.load(fxml)
        val scene = Scene(root)
        val filePathsLabel = root.lookup("#FilePathsLabel") as Label
        val statusIndicator = root.lookup("#StatusIndicator") as Rectangle
        val differencesLabel = root.lookup("#DifferencesLabel") as TextArea
        val analyzedIndicator = root.lookup("#AnalyzedIndicator") as Rectangle

        scene.onDragOver = EventHandler { event ->
            val db = event.dragboard
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY)
            } else {
                event.consume()
            }
        }

        scene.onDragDropped = EventHandler { event ->
            val db = event.dragboard
            var success = false
            if (db.hasFiles()) {
                success = true
                var filePath: String?

                for (file in db.files) { db.files
                    filePath = file.absolutePath
                    println(filePath)
                }

                val firstResult = rawFileAnalyze(db.files)
                val theArchivePaths = firstResult.firstOrSinglePaths

                filePathsLabel.text = firstResult.paths
                statusIndicator.fill = Paint.valueOf(firstResult.colorName)

                println("Make the table")
                val theTable = makeTheTable(theArchivePaths, theDebugDirectory)
                theTable.prepareWorkingDirectory()

                for ( anArchiveSet in theTable.theArchiveSets)
                    printItemList(anArchiveSet, anArchiveSet.getThisIDs())

                for ( anItemRecord in theTable.theItemTable ) {
                    print(anItemRecord.key.toString())
                    println(anItemRecord.value.toString())
                }

                println("Difference only")
                var count = 0
                var resultList = mutableListOf<String>()
                for (anItemEntry in theTable.theItemTable) {
                    if (!anItemEntry.value.isFilled && !anItemEntry.value.isExtracted) {
                        count++
                        val stringBuilder = StringBuilder()
                        stringBuilder.append(anItemEntry.key.toString())
                        stringBuilder.append(anItemEntry.value.toString())
                        val theString = stringBuilder.toString()
                        resultList.add(theString)
                        println(theString)
                    }
                }

                GlobalScope.launch() {
                    var runCount = 1
                    while (true) {
                        analyzedIndicator.fill = Paint.valueOf("GRAY")
                        println("Phase #$runCount")
                        if (async{theTable.runOnce()}.await()) break

                        for (anArchiveSet in theTable.theArchiveSets)
                            printItemList(anArchiveSet, anArchiveSet.getThisIDs())

                        for (anItemRecord in theTable.theItemTable) {
                            print(anItemRecord.key.toString())
                            println(anItemRecord.value.toString())
                        }

                        println("Difference only")
                        count = 0
                        resultList = mutableListOf<String>()
                        for (anItemEntry in theTable.theItemTable) {
                            if (!anItemEntry.value.isFilled && !anItemEntry.value.isExtracted) {
                                count++
                                val stringBuilder = StringBuilder()
                                stringBuilder.append(anItemEntry.key.toString())
                                stringBuilder.append(anItemEntry.value.toString())
                                val theString = stringBuilder.toString()
                                resultList.add(theString)
                                println(theString)
                            }
                        }
                        println("Same")
                        resultList.add("---------------- Same ----------------")
                        for (anItemEntry in theTable.theItemTable) {
                            if (anItemEntry.value.isFilled || anItemEntry.value.isExtracted) {
                                val stringBuilder = StringBuilder()
                                stringBuilder.append(anItemEntry.key.toString())
                                stringBuilder.append(anItemEntry.value.toString())
                                val theString = stringBuilder.toString()
                                resultList.add(theString)
                                println(theString)
                            }
                        }
                        runCount++
                    }

                    if (count == 0) {
                        println("Have no different files in the ArchiveSets")

                        resultList = mutableListOf()
                        resultList.add("Have no different files in the ArchiveSets")
                        for (anItemEntry in theTable.theItemTable) {
                            val stringBuilder = StringBuilder()
                            stringBuilder.append(anItemEntry.key.toString())
                            stringBuilder.append(anItemEntry.value.toString())
                            val theString = stringBuilder.toString()
                            resultList.add(theString)
                            println(theString)
                        }
                    }

                    differencesLabel.text = resultList.joinToString(separator = "\n")
                    analyzedIndicator.fill = Paint.valueOf(if (count == 0) "Green" else "Red")

                    theTable.closeAllArchiveSets()
                    theTable.removeAllArchiveSets()
                }

                println("End a phase")
            } else {
                filePathsLabel.text = "No File"
                statusIndicator.fill = Paint.valueOf("Red")
            }
            event.isDropCompleted = success
            event.consume()
        }
        primaryStage.scene = scene
        primaryStage.show()
    }
}
