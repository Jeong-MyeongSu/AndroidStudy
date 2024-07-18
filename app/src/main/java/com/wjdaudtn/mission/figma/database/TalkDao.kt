package com.wjdaudtn.mission.figma.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 *packageName    : com.wjdaudtn.mission.figma.database
 * fileName       : TalkDao
 * author         : licen
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        licen       최초 생성
 */
@Dao
interface TalkDao {
    @Insert
    fun setInsertTalk(talkEntity: TalkEntity) : Long

    @Update
    fun setUpdateTalk(talkEntity: TalkEntity)

    @Delete
    fun setDeleteTalk(talkEntity: TalkEntity)

    @Query("SELECT * FROM TalkEntity")
    fun getTalkAll(): MutableList<TalkEntity>

    @Query("SELECT * FROM TalkEntity WHERE id = :talkId")
    fun getTalkId(talkId: Int): TalkEntity?
}