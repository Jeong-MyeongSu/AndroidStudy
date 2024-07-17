package com.wjdaudtn.mission.figma.adapter

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.FigmaOneSectionItemBinding
import com.wjdaudtn.mission.figma.FigmaDatabaseInit
import com.wjdaudtn.mission.figma.FigmaOneActivity
import com.wjdaudtn.mission.figma.FigmaOneActivity.MediaPlayerView
import com.wjdaudtn.mission.figma.database.MusicDao

/**
 *packageName    : com.wjdaudtn.mission.figma.adapter
 * fileName       : FigmaOneAdapter
 * author         : licen
 * date           : 2024-07-14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-14        licen       최초 생성
 */


interface MusicPlayStateListener {
    fun musicPlay(item: MediaPlayerView, position: Int)
    fun btnChange(item: MediaPlayerView)
    fun linkSeekBar(item: MediaPlayerView)
}

class FigmaOneAdapter(
    private val musicList: MutableList<MediaPlayerView>,
    private val activity: FigmaOneActivity,
    private val musicPlayListener: MusicPlayStateListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dbinstance: MusicDao = FigmaDatabaseInit().getMusicDao(activity.baseContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FigmaOneHolder(
            FigmaOneSectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FigmaOneHolder).bind()
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    /* 정지 요청 왔음 */
    fun onStopPlease(position:Int) {
        stopPlayer(musicList[position])
        musicPlayListener.linkSeekBar(musicList[position])
    }
    fun onStartPausePlease(position: Int){
        if(musicList[position].mediaPlayer.isPlaying){
            pausePlayer(musicList[position])
        }else{
            musicList[position].mediaPlayer.start()
            musicPlayListener.btnChange(musicList[position])
        }
    }




    //데이터 베이스 id, resourceId로 미디어 플레이어 리턴
    private fun createMediaPlayerFromDatabase(id: Int): MediaPlayer {
        val musicEntity = dbinstance.getMusicById(id)
        val resourceId = musicEntity?.resourceId
            ?: throw IllegalArgumentException("Music ID not found in database")
        return MediaPlayer.create(activity.baseContext, resourceId)
    }

    //일시 정지
    private fun pausePlayer(item: MediaPlayerView) {
        item.mediaPlayer.pause()
        musicPlayListener.btnChange(item)
    }

    //음악이 시작 될 때
    private fun startPlayer(item: MediaPlayerView) {
        item.mediaPlayer.start()
    }

    private fun stopPlayer(item: MediaPlayerView) {
        item.mediaPlayer.release()
        item.mediaPlayer = createMediaPlayerFromDatabase(item.id)
        musicPlayListener.btnChange(item)
    }

    inner class FigmaOneHolder(val binding: FigmaOneSectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind() {
            musicList.let {
                val item = it[adapterPosition] //musicList 를 포지션 별로 item 에 넣어줌
                binding.figmaOneSectionItemTitle.text = item.title
                binding.figmaOneSectionItemArtist.text = item.artists

                val isMusicOn: Boolean = item.mediaPlayer.isPlaying
                val resourceIcon = if (isMusicOn) R.drawable.pause else R.drawable.right_button_2
                binding.btnPlay.background =
                    ContextCompat.getDrawable(activity.baseContext, resourceIcon)


                binding.btnPlay.setOnClickListener {
                    Log.d("sectionBtn", "sectionBtn, $adapterPosition")

                    // 현재 아이템을 제외한 다른 아이템의 MediaPlayer를 중지
                    for (i in 0 until musicList.size) {
                        val currentItem = musicList[i]
                        // currentPosition을 확인하여 seekTo가 0이 아닌 경우 release() 호출
                        if (i != adapterPosition && (currentItem.mediaPlayer.isPlaying || currentItem.mediaPlayer.currentPosition != 0)) {
                            Log.d("돌고 있다", "i는 음악이 나오고 있어")
                            stopPlayer(currentItem)

                        }
                    }

                    if (!item.mediaPlayer.isPlaying) {
                        Log.d("!mediaPlayer.isPlaying", "현재 플레이스에 뮤직이 안틀어져있어 틀어야해")
                        startPlayer(item)
                        musicPlayListener.musicPlay(item,adapterPosition)
                        musicPlayListener.btnChange(item)
                        musicPlayListener.linkSeekBar(item)

                    } else {
                        Log.d("mediaPlayer.isPlaying", "현재 플레이스에 뮤직이 틀어져있어 꺼야해")
                        pausePlayer(item)
                    }
                    notifyDataSetChanged()
                }
                item.mediaPlayer.setOnCompletionListener {
                    Log.d("노래 끝남", "노래 끝남")
                    item.mediaPlayer.pause()
                    item.mediaPlayer.seekTo(0) // 재설정
                    musicPlayListener.btnChange(item)
                    notifyDataSetChanged()
                }
            }

        }
    }
}
