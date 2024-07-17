package com.wjdaudtn.mission.figma.database

import android.media.MediaPlayer
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *packageName    : com.wjdaudtn.mission.figma.database
 * fileName       : MusicEntity
 * author         : licen
 * date           : 2024-07-14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-14        licen       최초 생성
 */
@Entity
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val resourceId: Int = 0,
    val title: String = "",
    val artist: String = ""
)
