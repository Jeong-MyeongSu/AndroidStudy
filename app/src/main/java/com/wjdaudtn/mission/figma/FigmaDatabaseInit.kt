package com.wjdaudtn.mission.figma

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.figma.database.MusicDao
import com.wjdaudtn.mission.figma.database.MusicDatabase
import com.wjdaudtn.mission.figma.database.MusicEntity
import java.io.IOException

/**
 *packageName    : com.wjdaudtn.mission.figma
 * fileName       : FigmaDatabaseInit
 * author         : licen
 * date           : 2024-07-14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-14        licen       최초 생성
 */
class FigmaDatabaseInit {
    private lateinit var musicDatabase: MusicDatabase
    private lateinit var musicDao: MusicDao

    fun getMusicDao(context: Context):MusicDao{
        if(!::musicDatabase.isInitialized){
            setDatabase(context)
        }
        return musicDao
    }

    private fun setDatabase(context:Context){
        musicDatabase = Room.databaseBuilder(
            context.applicationContext,
            MusicDatabase::class.java,
            "music_db",
        ).fallbackToDestructiveMigration() //스키마가 변경되면 기존 데이터 삭제하고 새로 생성 데이터 무결성을 보장 하지만 데이터 손실을 초래 데이터 손실을 피하기 위해선 .addMigrations(클래스 이름) 을 사용
            .allowMainThreadQueries()     //메인 스레드에서 데이터 베이스 쿼리를 허용 실제 애플리케이션 에서는 백그라운드 스레드에서 데이터 베이스 작업이 수행하도록 해야함
            .build() // 빌드하여 database instance 생성
        musicDao = musicDatabase.musicDao()

        //일단 볼수 있게 정적인 데이터 3개 넣기
        if(musicDao.getMusicAll().isEmpty()){
            val initialMusic = listOf(
                MusicEntity(resourceId = R.raw.dreams, title = "Dreams", artist = "Benjamin Tissot"),
                MusicEntity(resourceId = R.raw.yesterday, title = "YesterDay", artist = "Aventure"),
                MusicEntity(resourceId = R.raw.moonlightdrive, title ="Moonlight Drive", artist = "Yunior Aroonte")
            )

            for (music in initialMusic) {
                if (musicDao.getMusicById(music.id) == null) {
                    musicDao.setInsertMusic(music)
                }
            }
        }

    }

}