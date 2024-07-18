package com.wjdaudtn.mission.figma.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 *packageName    : com.wjdaudtn.mission.figma.database
 * fileName       : TalkDatabase
 * author         : licen
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        licen       최초 생성
 */
@Database(entities = [TalkEntity::class], version = 1, exportSchema = false)
abstract class TalkDatabase: RoomDatabase() {
    abstract fun talkDao(): TalkDao
}