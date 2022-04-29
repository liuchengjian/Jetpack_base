package com.liucj.jetpack_base

import android.util.Log
import com.liucj.lib_common.taskflow.ITaskCreator
import com.liucj.lib_common.taskflow.Project
import com.liucj.lib_common.taskflow.Task
import com.liucj.lib_common.taskflow.TaskFlowManager

object TaskStartUp {
    const val TASK_BLOCK_1 = "task_block_1"
    const val TASK_BLOCK_2 = "task_block_2"
    const val TASK_BLOCK_3 = "task_block_3"

    const val TASK_ASYNC_1 = "task_async_1"
    const val TASK_ASYNC_2 = "task_async_2"
    const val TASK_ASYNC_3 = "task_async_3"

    @JvmStatic
    fun start() {
        Log.e("TaskStarUp", "start")
        val project = Project.Builder("TaskStartUp", createTaskCreator())
                .add(TASK_BLOCK_1)
                .add(TASK_BLOCK_2)
                .add(TASK_BLOCK_3)
                .add(TASK_ASYNC_1).dependOn(TASK_BLOCK_1)
                .add(TASK_ASYNC_2).dependOn(TASK_BLOCK_2)
                .add(TASK_ASYNC_3).dependOn(TASK_BLOCK_3)
                .build()

        TaskFlowManager
                .addBlockTask(TASK_BLOCK_1)
                .addBlockTask(TASK_BLOCK_2)
                .addBlockTask(TASK_BLOCK_3)
                .start(project)

        Log.e("TaskStartUp","end")
    }

    private fun createTaskCreator(): ITaskCreator {
        return object : ITaskCreator {
            override fun createTask(taskName: String): Task {
                when (taskName) {
                    TASK_ASYNC_1 -> return createTask(taskName, true)
                    TASK_ASYNC_2 -> return createTask(taskName, true)
                    TASK_ASYNC_3 -> return createTask(taskName, true)

                    TASK_BLOCK_1 -> return createTask(taskName, false)
                    TASK_BLOCK_2 -> return createTask(taskName, false)
                    TASK_BLOCK_3 -> return createTask(taskName, false)
                }
                return createTask("default", false)
            }


        }
    }

    fun createTask(taskName: String, isAsync: Boolean): Task {
        return object : Task(taskName, isAsync) {
            override fun run(id: String) {

                Thread.sleep(if (isAsync) 2000 else 1000)
                Log.e("TaskStartUp","task $taskName,$isAsync,finished")
            }

        }
    }

}