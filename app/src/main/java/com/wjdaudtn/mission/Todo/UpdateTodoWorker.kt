package com.wjdaudtn.mission.todo

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wjdaudtn.mission.todo.util.Const.Companion.TODO_ID

/**
 *packageName    : com.wjdaudtn.mission.todo
 * fileName       : UpdateTodoWorker
 * author         : licen
 * date           : 2024-07-24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        licen       최초 생성
 */
class UpdateTodoWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val todoId = inputData.getInt(TODO_ID, -1)
        if (todoId != -1) {
            val dbInstance = DataBaseInit().getTodoDao(applicationContext)
            val todo = dbInstance.getTodoById(todoId)
            if (todo != null) {
                todo.alarmSwitch = 0 // 알람이 울려 알람 스위치 꺼짐
                dbInstance.setUpdateTodo(todo)
                // UI 업데이트가 필요한 경우, WorkManager를 통해 UI 작업을 수행할 수 없으므로
                // 이를 처리할 방법이 필요합니다 (예: 알림, 브로드캐스트).
            }
        }
        return Result.success()
    }
}