import javafx.application.Application

import archive.*

fun main(args : Array<String>) {
    println("StudyKotlin-JBinding")

    try {
        initialize(theIgnoringListPath)
    } catch (e: Exception) {
        println("Fail to load IgnoringList")
        return
    }

    if (jBindingChecker()) Application.launch(GUI().javaClass, *args)
}
