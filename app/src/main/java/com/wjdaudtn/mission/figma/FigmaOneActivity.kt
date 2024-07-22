package com.wjdaudtn.mission.figma

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityFigmaOneBinding
import com.wjdaudtn.mission.databinding.BookMarkBinding
import com.wjdaudtn.mission.figma.adapter.FigmaOneAdapter
import com.wjdaudtn.mission.figma.database.MusicDao


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

    private var handler = Handler() //seekbar handler
    private lateinit var musicDao: MusicDao

    //추상 클래스
    abstract class MediaPlayerView2(
        var id: Int,
        var resId: Int,
        var title: String,
        var artists: String,
        var mediaPlayer: MediaPlayer
    ) {
        abstract fun musicPlay(item: MediaPlayerView2, position: Int)
        abstract fun btnChange(item: MediaPlayerView2)
        abstract fun linkSeekBar(item: MediaPlayerView2)
    }

    private lateinit var adapterList2: MutableList<MediaPlayerView2>


    private var adapterPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFigmaOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicDao = FigmaDatabaseInit().getMusicDao(applicationContext)

        adapterList2 = mutableListOf()
        for (i in 0 until musicDao.getMusicAll().size) {
            adapterList2.add(multiListInit2(i + 1))
        }

    }

    private fun multiListInit2(id: Int): MediaPlayerView2 {
        val musicEntity = musicDao.getMusicById(id)
        return object : MediaPlayerView2(
            id = musicEntity!!.id,
            resId = musicEntity.resourceId,
            title = musicEntity.title,
            artists = musicEntity.artist,
            mediaPlayer = createMediaPlayerFromDatabase(musicEntity.id)
        ) {
            override fun musicPlay(item: MediaPlayerView2, position: Int) {
                Log.d("", item.toString())
                binding.bottomNavigationTitleText.text = item.title
                binding.bottomNavigationArtisText.text = item.artists
                binding.btnBottomNavigationStartAndPause.background =
                    ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
                adapterPosition = position
            }

            override fun btnChange(item: MediaPlayerView2) {
                val isMusicOn: Boolean = item.mediaPlayer.isPlaying //-1 정지, 0 일시 정지 1 켜짐
                val resourceIcon = if (isMusicOn) R.drawable.pause else R.drawable.right_button_2
                binding.btnBottomNavigationStartAndPause.background =
                    ContextCompat.getDrawable(baseContext, resourceIcon)
            }

            override fun linkSeekBar(item: MediaPlayerView2) {
                Log.d("linkSeekbar", "1")
                seekBarConnection(item)
            }
        }
    }

    private fun createMediaPlayerFromDatabase(id: Int): MediaPlayer {
        val musicEntity = musicDao.getMusicById(id)
        val resourceId = musicEntity?.resourceId
            ?: throw IllegalArgumentException("Music ID not found in database")
        return MediaPlayer.create(applicationContext, resourceId)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
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

    @SuppressLint("NotifyDataSetChanged")
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

        binding.figmaOneRecyclerview.adapter =
            FigmaOneAdapter(adapterList2, this)
        binding.figmaOneRecyclerview.layoutManager = LinearLayoutManager(baseContext)

        /* Adapter */
        val figmaOneAdapter: FigmaOneAdapter =
            binding.figmaOneRecyclerview.adapter as FigmaOneAdapter


        binding.btnBottomNavigationStop.setOnClickListener {
            figmaOneAdapter.onStopPlease(adapterPosition)
            handler.removeCallbacksAndMessages(null)// handler 실행 중단
            binding.customSeekBar.progress = 0
            figmaOneAdapter.notifyDataSetChanged()
        }
        binding.btnBottomNavigationStartAndPause.setOnClickListener {
            figmaOneAdapter.onStartPausePlease(adapterPosition)
            if (adapterPosition != -1) {
                val item = adapterList2[adapterPosition]
                seekBarConnection(item)
            }
            figmaOneAdapter.notifyDataSetChanged()
        }

    }

    private fun seekBarConnection(item: MediaPlayerView2) {
        binding.customSeekBar.max = item.mediaPlayer.duration
        updateSeekBar(item)
        binding.customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    item.mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun updateSeekBar(item: MediaPlayerView2) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (item.mediaPlayer.isPlaying) {
                    binding.customSeekBar.progress = item.mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)
                } else {
                    binding.customSeekBar.progress = item.mediaPlayer.currentPosition
                }
            }
        }, 1000)
    }
}


//인터페이스
//object : MusicPlayStateListener {
//    override fun musicPlay(item: MediaPlayerView, position:Int) {
//        Log.d("", item.toString())
//        binding.bottomNavigationTitleText.text = item.title
//        binding.bottomNavigationArtisText.text = item.artists
//        binding.btnBottomNavigationStartAndPause.background = ContextCompat.getDrawable(baseContext, R.drawable.right_button_2)
//        adapterPosition = position
//    }
//
//    override fun btnChange(item: MediaPlayerView) {
//        val isMusicOn: Boolean = item.mediaPlayer.isPlaying //-1 정지, 0 일시 정지 1 켜짐
//        val resourceIcon = if (isMusicOn) R.drawable.pause else R.drawable.right_button_2
//        binding.btnBottomNavigationStartAndPause.background =
//            ContextCompat.getDrawable(baseContext, resourceIcon)
//    }
//
//    override fun linkSeekBar(item: MediaPlayerView) {
//        seekBarConnection(item)
//    }
//}


//    private fun seekBarConnection(item: MediaPlayerView){
//        binding.customSeekBar.max = item.mediaPlayer.duration
//        updateSeekBar(item)
//        binding.customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                        if (fromUser) {
//                            item.mediaPlayer.seekTo(progress)
//                        }
//                    }
//
//                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//
//                    }
//
//                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
//
//                    }
//                })
//    }

//    private fun updateSeekBar(item: MediaPlayerView){
//        handler.postDelayed(object : Runnable {
//                    override fun run() {
//                        if (item.mediaPlayer.isPlaying) {
//                            binding.customSeekBar.progress = item.mediaPlayer.currentPosition
//                            handler.postDelayed(this, 1000)
//                        }
//                    }
//                }, 1000)
//    }

//    data class MediaPlayerView(
//        var id: Int,
//        var resId: Int,
//        var title: String,
//        var artists: String,
//        var mediaPlayer: MediaPlayer
//    ) //adapter에 넣기위한 클래스객체
//
//    private lateinit var adapterList: MutableList<MediaPlayerView> //adapter 에 넣기 위한 객체 리스트

//    private fun multiListInit(id: Int) : MediaPlayerView{
//        val musicEntity = musicDao.getMusicById(id)
//        class Super
//        return MediaPlayerView(
//            id = musicEntity!!.id,
//            resId = musicEntity.resourceId,
//            title = musicEntity.title,
//            artists = musicEntity.artist,
//            mediaPlayer = createMediaPlayerFromDatabase(musicEntity.id)
//        )
//    }

//        adapterList = mutableListOf()
//        for(i in 0 until musicDao.getMusicAll().size){
//            adapterList.add(multiListInit(i+1))
//        }