package com.wjdaudtn.mission.Todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo (
    @PrimaryKey(autoGenerate = true)
    var index: Int = 0,
    var title: String = "",
    var content:String = "",
)


