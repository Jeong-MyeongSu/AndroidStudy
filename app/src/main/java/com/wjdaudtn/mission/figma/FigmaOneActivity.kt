package com.wjdaudtn.mission.figma

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityFigmaOneBinding
import com.wjdaudtn.mission.databinding.BookMarkBinding

/**
 *packageName    : com.wjdaudtn.mission.figma
 * fileName       : FigmaOneActivity
 * author         : licen
 * date           : 2024-07-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-11        licen       최초 생성
 */
class FigmaOneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFigmaOneBinding
    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var mediaPlayer3: MediaPlayer
    private var mediaPlayerPlace : Int = 0 //미디어플레이어 몇번째가 실행중인지 알기위한 맴버 변수
    private var btnNum: Int = 0 //위와 동일 section에서 사용하기 위한 맴버 변수
    private var handler = Handler() //seekbar hadler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFigmaOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1.release() // MediaPlayer 자원 해제
        mediaPlayer2.release()
        mediaPlayer3.release()
        handler.removeCallbacksAndMessages(null)
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
        // toolbar 설정
        setSupportActionBar(binding.toolbarFigmaOne)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Title"
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // 음악 객체 초기화
        mediaPlayer1 = MediaPlayer.create(applicationContext, R.raw.dreams)
        mediaPlayer2 = MediaPlayer.create(applicationContext, R.raw.yesterday)
        mediaPlayer3 = MediaPlayer.create(applicationContext, R.raw.moonlightdrive)



        // 음악이 끝날 때 리스너 설정
        mediaPlayer1.setOnCompletionListener {
            finishMediaPlayer(mediaPlayerPlace)
        }
        mediaPlayer2.setOnCompletionListener {
            finishMediaPlayer(mediaPlayerPlace)
        }
        mediaPlayer3.setOnCompletionListener {
            finishMediaPlayer(mediaPlayerPlace)
        }

        //버튼 클릭 리스너
        binding.btnPlay1.setOnClickListener(customOnClickListener)
        binding.btnPlay2.setOnClickListener(customOnClickListener)
        binding.btnPlay3.setOnClickListener(customOnClickListener)


    }



    private val customOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_play1 -> {
                btnNum = 1
                Log.d("mediaPlayer1", "mediaPlayer1")
                if (!mediaPlayer1.isPlaying) {
                    startMediaPlayer(btnNum)
                    bottomNavigationButton(mediaPlayerPlace)
                    binding.bottomNavigationTitleText.text = getString(R.string.dreams)
                } else {
                    stopMediaPlayer(btnNum)
                }
            }

            R.id.btn_play2 -> {
                btnNum = 2
                Log.d("mediaPlayer2", "mediaPlayer2")
                if (!mediaPlayer2.isPlaying) {
                    startMediaPlayer(btnNum)
                    bottomNavigationButton(mediaPlayerPlace)
                    binding.bottomNavigationTitleText.text = getString(R.string.yesterday)

                } else {
                    stopMediaPlayer(btnNum)
                }
            }

            R.id.btn_play3 -> {
                btnNum = 3
                Log.d("mediaPlayer3", "mediaPlayer3")
                if (!mediaPlayer3.isPlaying) {
                    startMediaPlayer(btnNum)
                    bottomNavigationButton(mediaPlayerPlace)
                    binding.bottomNavigationTitleText.text = getString(R.string.moonlightdrive)
                } else {
                    stopMediaPlayer(btnNum)
                }
            }
        }
    }

    // 음악이 끝났을 때 실행되는 함수
    private fun finishMediaPlayer(mediaPlayer: Int) {
        when (mediaPlayer) {
            1 -> {
                mediaPlayerPlace = 0
                binding.btnPlay1.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
            }
            2 -> {
                mediaPlayerPlace = 0
                binding.btnPlay2.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
            }
            3 -> {
                mediaPlayerPlace = 0
                binding.btnPlay3.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
            }
        }
    }
    // 다른 음악을 틀었을 때 현재 음악 꺼짐 일시 정지 아닌 완전 정지 시 음악 꺼짐
    private fun compulsoryStopMediaPlayer(mediaPlayer: Int){
        when(mediaPlayer){
            1 -> {
                mediaPlayer1.pause()
                mediaPlayer1.seekTo(0) // 재설정
                binding.btnPlay1.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
            }
            2 -> {
                mediaPlayer2.pause()
                mediaPlayer2.seekTo(0) // 재설정
                binding.btnPlay2.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
            }
            3 -> {
                mediaPlayer3.pause()
                mediaPlayer3.seekTo(0) // 재설정
                binding.btnPlay3.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
            }
        }
    }
    //BottomNavigationButton
    private fun bottomNavigationButton(mediaPlayer : Int){
        binding.btnBottomNavigationStop.setOnClickListener{
            compulsoryStopMediaPlayer(mediaPlayer)
            binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.start_pause)
        }
        binding.btnBottomNavigationStartAndPause.setOnClickListener{
            when(mediaPlayer){
                1 -> {
                    Log.d("mediaPlayer1", "mediaPlayer1")
                    if (!mediaPlayer1.isPlaying) {
                        startMediaPlayer(mediaPlayer)
                    } else {
                        stopMediaPlayer(mediaPlayer)
                    }
                }

                2 -> {
                    Log.d("mediaPlayer2", "mediaPlayer2")
                    if (!mediaPlayer2.isPlaying) {
                        startMediaPlayer(mediaPlayer)
                    } else {
                        stopMediaPlayer(mediaPlayer)
                    }
                }

                3 -> {
                    Log.d("mediaPlayer3", "mediaPlayer3")
                    if (!mediaPlayer3.isPlaying) {
                        startMediaPlayer(mediaPlayer)
                    } else {
                        stopMediaPlayer(mediaPlayer)
                    }
                }
            }
        }
    }
    //btn 누르면 시작 하는 미디어 플레이어 시작 함수 .음악 시작
    private fun startMediaPlayer(mediaPlayer: Int){
        when(mediaPlayer){
            1 ->{
                Log.d("mediaPlayer1_isPlaying", "mediaPlayer1_isPlaying")
                seekBarConnection(mediaPlayer)
                if(mediaPlayerPlace == 0 || mediaPlayerPlace == 1){
                    mediaPlayerPlace = 1
                }else{
                    compulsoryStopMediaPlayer(mediaPlayerPlace)
                    mediaPlayerPlace = 1
                }
                binding.btnPlay1.background = ContextCompat.getDrawable(this, R.drawable.pause)
                mediaPlayer1.start() // 음악 재생 시작
                binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.pause)
            }
            2->{
                Log.d("mediaPlayer2_isPlaying", "mediaPlayer2_isPlaying")
                seekBarConnection(mediaPlayer)
                if(mediaPlayerPlace == 0 || mediaPlayerPlace == 2){
                    mediaPlayerPlace = 2
                }else{
                    compulsoryStopMediaPlayer(mediaPlayerPlace)
                    mediaPlayerPlace = 2
                }
                binding.btnPlay2.background = ContextCompat.getDrawable(baseContext, R.drawable.pause)
                mediaPlayer2.start() // 음악 재생 시작
                binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.pause)
            }
            3->{
                Log.d("mediaPlayer3_isPlaying", "mediaPlayer3_isPlaying")
                seekBarConnection(mediaPlayer)
                if(mediaPlayerPlace == 0 || mediaPlayerPlace == 3){
                    mediaPlayerPlace = 3
                }else{
                    compulsoryStopMediaPlayer(mediaPlayerPlace)
                    mediaPlayerPlace = 3
                }
                binding.btnPlay3.background = ContextCompat.getDrawable(baseContext, R.drawable.pause)
                mediaPlayer3.start() // 음악 재생 시작
                binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.pause)
            }

        }
    }
    //btn 누르면 시작 하는 미디어 플레이어 시작 함수2 .일시 정지
    private fun stopMediaPlayer(mediaPlayer: Int){
        when(mediaPlayer){
            1->{
                binding.btnPlay1.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
                mediaPlayer1.pause() // 음악 일시 정지
                binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.start_pause)
            }
            2->{
                binding.btnPlay2.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
                mediaPlayer2.pause() // 음악 일시 정지
                binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.start_pause)
            }
            3->{
                binding.btnPlay3.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
                mediaPlayer3.pause() // 음악 일시 정지
                binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.start_pause)
            }
        }
    }

    private fun seekBarConnection(mediaPlayer: Int){
        when(mediaPlayer){
            1->{
                binding.customSeekBar.max = mediaPlayer1.duration
                updateSeekBar(mediaPlayer)
                binding.customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer1.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }
                })
            }
            2->{
                binding.customSeekBar.max = mediaPlayer1.duration
                updateSeekBar(mediaPlayer)
                binding.customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer2.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }
                })
            }
            3->{
                binding.customSeekBar.max = mediaPlayer1.duration
                updateSeekBar(mediaPlayer)
                binding.customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer3.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }
                })
            }
        }
    }

    private fun updateSeekBar(mediaPlayer: Int) {
        when(mediaPlayer){
            1 -> {
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        if (mediaPlayer1.isPlaying) {
                            binding.customSeekBar.progress = mediaPlayer1.currentPosition
                            handler.postDelayed(this, 1000)
                        }
                    }
                }, 1000)
            }
            2 -> {
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        if (mediaPlayer2.isPlaying) {
                            binding.customSeekBar.progress = mediaPlayer2.currentPosition
                            handler.postDelayed(this, 1000)
                        }
                    }
                }, 1000)
            }
            3 ->{
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        if (mediaPlayer3.isPlaying) {
                            binding.customSeekBar.progress = mediaPlayer3.currentPosition
                            handler.postDelayed(this, 1000)
                        }
                    }
                }, 1000)
            }
        }
    }
}