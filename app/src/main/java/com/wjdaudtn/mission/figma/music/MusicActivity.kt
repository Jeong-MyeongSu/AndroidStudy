package com.wjdaudtn.mission.figma.music

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityMusicBinding
import com.wjdaudtn.mission.databinding.BookMarkBinding
import com.wjdaudtn.mission.figma.FigmaDatabaseInit

class MusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicBinding

    private val tag: String = "MusicActivity"

    // 핸들러 초기화
    private val handler = Handler(Looper.getMainLooper())

    data class PlayInfo(
        val pId: Int,
        val title: String,
        val artist: String,
        val resourceId: Int,
        val mediaPlayer: MediaPlayer,
        val event: FigmaMediaPlayer,
    )

    private var adapterPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val musicDao = FigmaDatabaseInit().getMusicDao(applicationContext)
        val playList: MutableList<PlayInfo> = mutableListOf()

        for (music in musicDao.getMusicAll()) {
            val mediaPlayer = MediaPlayer.create(this, music.resourceId)
            playList.add(
                PlayInfo(
                    music.id,
                    music.title,
                    music.artist,
                    music.resourceId,
                    mediaPlayer,
                    event = object : FigmaMediaPlayer() {
                        override fun play(media: MediaPlayer, position: Int) {
                            /* 이미 실행 중인 Media 모두 중지 */
                            for ((index, item) in playList.withIndex()) {
                                if (item.mediaPlayer.isPlaying) {
                                    item.mediaPlayer.seekTo(0)
                                    item.event.pause(item.mediaPlayer)
                                }
                            }

                            media.start()
                            adapterPosition = position
                            Log.d(tag, "play => ${music.title} / ${music.artist}")

                            val adapter = binding.figmaOneRecyclerview.adapter as MusicPlayAdapter
                            val musicPlayInfo: PlayInfo = adapter.getItem(adapterPosition)
                            initSeekBar(musicPlayInfo)

                            binding.bottomNavigationTitleText.text = music.title
                            binding.bottomNavigationArtisText.text = music.artist
                            binding.btnBottomNavigationStartAndPause.setImageResource(R.drawable.pause)
                        }

                        override fun pause(media: MediaPlayer) {
                            media.pause()
                            Log.d(tag, "pause => ${music.title} / ${music.artist}")

                            binding.btnBottomNavigationStartAndPause.setImageResource(R.drawable.play)
                        }

                        override fun stop(media: MediaPlayer) {
                            Log.d(tag, "stop => ${music.title} / ${music.artist}")

                            handler.removeCallbacksAndMessages(null)// handler 실행 중단
                            binding.btnBottomNavigationStartAndPause.setImageResource(R.drawable.play)
                            binding.customSeekBar.progress = 0
                        }
                    })
            )
        }

        if (playList.size != 0) {
            binding.bottomNavigationTitleText.text = playList[0].title
            binding.bottomNavigationArtisText.text = playList[0].artist
            adapterPosition = 0
        }

        binding.figmaOneRecyclerview.adapter = MusicPlayAdapter(playList)
        binding.figmaOneRecyclerview.layoutManager = LinearLayoutManager(baseContext)

        val adapter = binding.figmaOneRecyclerview.adapter as MusicPlayAdapter
        binding.btnBottomNavigationStop.setOnClickListener {
            val musicPlayInfo: PlayInfo = adapter.getItem(adapterPosition)
            musicPlayInfo.mediaPlayer.seekTo(0)
            musicPlayInfo.event.pause(musicPlayInfo.mediaPlayer)
            binding.tvPlayTime.text = "00:00"

            handler.removeCallbacksAndMessages(null)// handler 실행 중단
            binding.btnBottomNavigationStartAndPause.setImageResource(R.drawable.play)
            binding.customSeekBar.progress = 0

            adapter.notifyItemChanged(adapterPosition)
        }

        binding.btnBottomNavigationStartAndPause.setOnClickListener {
            if (adapterPosition == -1) {
                return@setOnClickListener
            }

            val musicPlayInfo: PlayInfo = adapter.getItem(adapterPosition)

            if (musicPlayInfo.mediaPlayer.isPlaying) {
                musicPlayInfo.event.pause(musicPlayInfo.mediaPlayer)
            } else {
                musicPlayInfo.event.play(musicPlayInfo.mediaPlayer, adapterPosition)
            }

            adapter.notifyItemChanged(adapterPosition)
        }

    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onPause() {
        super.onPause()
        stopSeekBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem1: MenuItem? = menu?.add(0, 0, 0, "북마크")
        menuItem1?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        val menuItem2: MenuItem? = menu?.add(0, 1, 0, "overflow1")
        menuItem2?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        val menuItem3: MenuItem? = menu?.add(0, 2, 0, "overflow2")
        menuItem3?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

        val menuItemBinding = BookMarkBinding.inflate(layoutInflater)
        menuItem1?.actionView = menuItemBinding.root

        return true
    }


    private fun initView() {
        setSupportActionBar(binding.toolbarFigmaOne)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Music Player"
        }
    }

    private fun initSeekBar(item: PlayInfo) {
        stopSeekBar()
        updateSeekBar(item)

        binding.customSeekBar.max = item.mediaPlayer.duration
        binding.customSeekBar.setOnSeekBarChangeListener(object : MusicOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    item.mediaPlayer.seekTo(progress)
                }
            }
        })

        // 전체 길이를 포맷팅하여 텍스트 뷰에 설정
        val duration = formatTime(item.mediaPlayer.duration)
        binding.tvFullTime.text = duration
    }

    private fun updateSeekBar(item: PlayInfo) {
        handler.postDelayed({
            val currentPosition = item.mediaPlayer.currentPosition

            // 애니메이션으로 SeekBar 업데이트
            ObjectAnimator.ofInt(binding.customSeekBar, "progress", currentPosition).apply {
                duration = 240 // 애니메이션 지속 시간 (밀리초)
                start()
            }

            // 현재 시간을 포맷팅하여 텍스트 뷰에 설정
            val currentTime = formatTime(currentPosition)
            binding.tvPlayTime.text = currentTime

            if (item.mediaPlayer.isPlaying) {
                updateSeekBar(item)
            }
        }, 250)
    }
    private fun stopSeekBar() {
        handler.removeCallbacksAndMessages(null)
    }

    // 밀리초를 분:초 형식으로 포맷팅하는 함수
    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}