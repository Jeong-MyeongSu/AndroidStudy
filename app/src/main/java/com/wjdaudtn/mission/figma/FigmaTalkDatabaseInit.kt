package com.wjdaudtn.mission.figma

import android.content.Context
import androidx.room.Room
import com.wjdaudtn.mission.figma.database.TalkDao
import com.wjdaudtn.mission.figma.database.TalkDatabase
import com.wjdaudtn.mission.figma.database.TalkEntity

/**
 *packageName    : com.wjdaudtn.mission.figma
 * fileName       : FigmaTalkDatabaseInit
 * author         : licen
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        licen       최초 생성
 */
class FigmaTalkDatabaseInit {
    private lateinit var talkDao: TalkDao
    private lateinit var talkDatabase: TalkDatabase

    fun getTalkDao(context: Context):TalkDao{
        if(!::talkDatabase.isInitialized){
            setDatabase(context)
        }
        return talkDao
    }

    private fun setDatabase(context:Context) {
        talkDatabase = Room.databaseBuilder(
            context.applicationContext,
            TalkDatabase::class.java,
            "talk_db",
        )
            .fallbackToDestructiveMigration() //스키마가 변경되면 기존 데이터 삭제하고 새로 생성 데이터 무결성을 보장 하지만 데이터 손실을 초래 데이터 손실을 피하기 위해선 .addMigrations(클래스 이름) 을 사용
            .allowMainThreadQueries()     //메인 스레드에서 데이터 베이스 쿼리를 허용 실제 애플리케이션 에서는 백그라운드 스레드에서 데이터 베이스 작업이 수행하도록 해야함
            .build() // 빌드하여 database instance 생성
        talkDao = talkDatabase.talkDao()
        if(talkDao.getTalkAll().isEmpty()){
            val initialTalk = listOf(
                TalkEntity(content = "hi hi hi hi hi hi hi",userNum = 1),
                TalkEntity(content = "bye bye bye bye.", userNum = 2)
            )
            for (talk in initialTalk) {
                if (talkDao.getTalkId(talk.id) == null) {
                    talkDao.setInsertTalk(talk)
                }
            }
        }
    }
}