package com.liucj.lib_common.taskflow

interface ITaskCreator {
    fun createTask(taskName:String):Task
}