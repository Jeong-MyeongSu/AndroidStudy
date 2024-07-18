package com.wjdaudtn.mission.figma.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *packageName    : com.wjdaudtn.mission.figma.database
 * fileName       : TalkEntity
 * author         : licen
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        licen       최초 생성
 */
@Entity
data class TalkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String = "",
    val userNum: Int = 0
)
