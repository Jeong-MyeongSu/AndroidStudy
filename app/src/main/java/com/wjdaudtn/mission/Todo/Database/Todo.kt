package com.wjdaudtn.mission.todo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Todo (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var millisecond: Long = 0,
    var alarmSwitch: Int = 0,
)



