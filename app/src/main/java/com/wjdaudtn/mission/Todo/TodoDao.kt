package com.wjdaudtn.mission.Todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
public interface TodoDao {

    @Insert
    fun setInsertTodo(todo:Todo)

    @Update
    fun setUpdateTodo(todo:Todo)

    @Delete
    fun setDeleteTodo(todo:Todo)

    @Query("SELECT * FROM Todo")
    fun getUserAll():MutableList<Todo>

}