package com.example.comidapp.copiaEOI

interface TaskListener {
  fun deleteTask(idTask: String)
  fun completeTask(idTask: String, isDone: Boolean)
}