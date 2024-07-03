package com.wjdaudtn.mission.todo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
public interface TodoDao {

    @Insert
    fun setInsertTodo(todo:Todo): Long

    @Update
    fun setUpdateTodo(todo:Todo)

    @Delete
    fun setDeleteTodo(todo:Todo)

    @Query("SELECT * FROM Todo")
    fun getTodoAll():MutableList<Todo>

    @Query("SELECT * FROM Todo WHERE id = :todoId")
    fun getTodoById(todoId: Int): Todo?

}