package com.liucj.lib_common.taskflow

/**
 * 任务组
 */
class Project private constructor(id:String):Task(id){
    lateinit var endTask: Task//任务组中 所有任务的结束节点
    lateinit var startTask: Task//任务值的开始节点

    override fun start() {
        startTask.start()
    }
    override fun run(id: String) {
        //不需要处理
    }

    override fun behind(behindTask: Task) {
        //给任务组添加后置任务组的时候，那么这个任务应该添加到组中谁的后面？？
        endTask.behind(behindTask)//新来的后置任务 添加到任务组结束节点的后面，
        // 所有的人都结束了，这个后置任务才执行

    }

    override fun dependOn(dependTask: Task) {
        startTask.dependOn(dependTask)
    }



    override fun removeDependence(dependTask: Task) {
        startTask.removeDependence(dependTask)
    }

    override fun removeBehind(behindTask: Task) {
        endTask.removeBehind(behindTask)
    }

    class Builder(projectName:String,iTaskCreator: ITaskCreator){
        private val mTaskFactory = TaskFactory(iTaskCreator)
        private val mStartTask:Task= CriticalTask(projectName+"_start")
        private val mEndTask:Task= CriticalTask(projectName+"_end")
        private val mProject:Project= Project(projectName)
        private var mPriority = 0//默认为该任务组中 所有任务优先级醉最高的
        //本次添加Task 是否把start节点当做依赖，
        // 如果task存在与其他task的依赖关系，哪家不能直接添加到start节点的后面
        //那就需要通过dependOn来指定关系
        private var mCurrentTaskShouldDependOnStartTask = true
        private var mCurrentAddTask:Task? = null

        fun add(id: String):Builder{
            val task = mTaskFactory.getTask(id)
            if(task.priority>mPriority){
                mPriority = task.priority
            }
            return add(task)
        }

        private fun add(task: Task): Builder {
            if(mCurrentTaskShouldDependOnStartTask&&mCurrentAddTask!=null){
                mStartTask.behind(mCurrentAddTask!!)
            }
            mCurrentAddTask = task
            mCurrentTaskShouldDependOnStartTask =true
            mCurrentAddTask!!.behind(mEndTask)
            return this
        }

        fun dependOn(id: String):Builder{
            return dependOn(mTaskFactory.getTask(id))
        }

        private fun dependOn(task: Task): Builder {
            //确定刚才我们所添加进来的mCurrentAddTask 与task的依赖关系
            //--mCurrentAddTask依赖于task
            task.behind(mCurrentAddTask!!)

            mEndTask.removeDependence(task)
            mCurrentTaskShouldDependOnStartTask = false
            return this
        }

        fun build():Project{
            if(mCurrentAddTask==null){
                mStartTask.behind(mEndTask)
            }else{
                if(mCurrentTaskShouldDependOnStartTask){
                    mStartTask.behind(mCurrentAddTask!!)
                }
            }
            mStartTask.priority = mPriority
            mEndTask.priority= mPriority
            mProject.startTask = mStartTask
            mProject.endTask = mEndTask

            return mProject
        }

    }
    private class TaskFactory(private val iTaskCreator: ITaskCreator){
        //利用iTaskCreator创建Task 实例并管理

        private val mCacheTasks :MutableMap<String,Task> = HashMap()
        fun getTask(id: String):Task{
            var task = mCacheTasks.get(id)
            if(task!=null){
                return task
            }
            task = iTaskCreator.createTask(id)

            requireNotNull(task){"create task fail"}
            mCacheTasks[id] = task
            return task
        }
    }

    internal class CriticalTask internal constructor(id: String):Task(id){
        override fun run(id: String) {
            //不需要处理
        }

    }
}