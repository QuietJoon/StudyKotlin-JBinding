import javafx.application.Application

import archive.*

fun main(args : Array<String>) {
    println("StudyKotlin-JBinding")
    if (jBindingChecker()) Application.launch(GUI().javaClass, *args)
    println("End")
}
