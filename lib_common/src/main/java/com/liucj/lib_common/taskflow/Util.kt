package com.liucj.lib_common.taskflow

object Util{
    //比较两个任务先后执行顺序
    //优先级越高预先执行
    fun compareTask(task1: Task,task2: Task):Int{
        if (task1.priority<task2.priority){
            return 1
        }
        if(task1.priority>task2.priority){
            return -1
        }
        return 0
    }
}