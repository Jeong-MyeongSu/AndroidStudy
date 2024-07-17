package com.wjdaudtn.mission.figma.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wjdaudtn.mission.todo.database.Todo

/**
 *packageName    : com.wjdaudtn.mission.figma.database
 * fileName       : MusicDatabase
 * author         : licen
 * date           : 2024-07-14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-14        licen       최초 생성
 */
@Database(entities = [MusicEntity::class], version = 3, exportSchema = false)
abstract class MusicDatabase: RoomDatabase() {
    abstract fun musicDao(): MusicDao
}