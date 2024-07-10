package com.wjdaudtn.mission.todo

import android.content.Context
import androidx.room.Room
import com.wjdaudtn.mission.todo.database.TodoDao
import com.wjdaudtn.mission.todo.database.TodoDatabase

class DataBaseInit {  //싱글톤을 위한 클래스
    private lateinit var todoDatabase: TodoDatabase  // TodoDatabase 객체 생성
    private lateinit var todoDao: TodoDao   // database 인터페이스 객체 생성

    fun getTodoDao(context: Context): TodoDao { // todoDao를 주기 위한 function
        if (!::todoDatabase.isInitialized) { // todoDatabase 객체가 초기화 되어 있는지 확인
            settingDatabase(context)    //todoDatabase 객체가 초기화 되어 있지 않으면 todoDatabase 초기화, todoDao 초기화
        }
        return todoDao // database interface 리턴
    }

    private fun settingDatabase(context: Context) {  // database 초기화 작업 function
        todoDatabase = Room.databaseBuilder(      //룸 데이터베이스 빌더로 database 초기화
            context.applicationContext, // 매개변수로 받은 context
            TodoDatabase::class.java,   // database 파일
            "Todo_db"   //database 이름
        ).fallbackToDestructiveMigration() //스키마가 변경되면 기존 데이터 삭제하고 새로 생성 데이터 무결성을 보장 하지만 데이터 손실을 초래 데이터 손실을 피하기 위해선 .addMigrations(클래스 이름) 을 사용
            .allowMainThreadQueries()     //메인 스레드에서 데이터 베이스 쿼리를 허용 실제 애플리케이션 에서는 백그라운드 스레드에서 데이터 베이스 작업이 수행하도록 해야함
            .build() // 빌드하여 database instance 생성
        todoDao = todoDatabase.todoDao()  //database interface 초기화
    }

}