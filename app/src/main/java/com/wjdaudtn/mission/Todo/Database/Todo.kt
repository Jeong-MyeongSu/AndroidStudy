package com.wjdaudtn.mission.Todo.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var year: Int = 0,
    var month: Int = 0,
    var dayOfMonth: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0,
    var alramSwitch: Int = 0,
)



