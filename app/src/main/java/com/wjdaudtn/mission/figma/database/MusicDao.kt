package com.wjdaudtn.mission.figma.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.wjdaudtn.mission.todo.database.Todo

/**
 *packageName    : com.wjdaudtn.mission.figma.database
 * fileName       : MusicDao
 * author         : licen
 * date           : 2024-07-14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-14        licen       최초 생성
 */
@Dao
interface MusicDao {
    @Insert
    fun setInsertMusic(musicEntity: MusicEntity):Long

    @Update
    fun setUpdateMusic(musicEntity: MusicEntity)

    @Delete
    fun setDeleteMusic(musicEntity: MusicEntity)

    @Query("SELECT * FROM MusicEntity")
    fun getMusicAll():MutableList<MusicEntity>

    @Query("SELECT * FROM MusicEntity WHERE id = :musicId")
    fun getMusicById(musicId: Int): MusicEntity?
}