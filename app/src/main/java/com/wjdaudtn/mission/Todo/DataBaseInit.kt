package com.wjdaudtn.mission.todo

import android.content.Context
import androidx.room.Room
import com.wjdaudtn.mission.todo.database.TodoDao
import com.wjdaudtn.mission.todo.database.TodoDatabase

class DataBaseInit {
    private lateinit var todoDatabase: TodoDatabase
    private lateinit var todoDao: TodoDao

    fun getTodoDao(context: Context): TodoDao {
        if (!::todoDatabase.isInitialized) {
            settingDatabase(context)
        }
        return todoDao
    }

    private fun settingDatabase(context: Context) {
        todoDatabase = Room.databaseBuilder(
            context.applicationContext,
            TodoDatabase::class.java,
            "Todo_db"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        todoDao = todoDatabase.todoDao()
    }

}