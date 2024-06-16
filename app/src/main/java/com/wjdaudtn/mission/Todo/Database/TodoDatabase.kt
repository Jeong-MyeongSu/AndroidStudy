package com.wjdaudtn.mission.Todo.Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 2, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}