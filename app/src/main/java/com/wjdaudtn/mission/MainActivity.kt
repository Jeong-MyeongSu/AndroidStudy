package com.wjdaudtn.mission

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.databinding.ActivityMainBinding
import com.wjdaudtn.mission.databinding.ItemMainStudyBinding
import com.wjdaudtn.mission.figma.FigmaOneActivity
import com.wjdaudtn.mission.figma.FigmaTwoActivity
import com.wjdaudtn.mission.figma.music.MusicActivity
import com.wjdaudtn.mission.recyclerView.ViewHolderTwo
import com.wjdaudtn.mission.todo.TodoMainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        initBinding()
    }

    private fun initBinding() {
        binding.rcyStudy.layoutManager = LinearLayoutManager(baseContext)
        binding.rcyStudy.adapter = StudyAdapter(
            // TODO: 새로 학습 & 미션 추가 될때 아래 리스트 에 추가
            mutableListOf(
                Study(
                    "JetPack",
                    "RecyclerView",
                    "RecyclerView Sample",
                    "2024-06-09",
                    ViewHolderTwo::class.java
                ),
                Study(
                    "JetPack",
                    "Todo",
                    "Todo List - 할일 추가/수정/삭제, 알림 등록, 지정된 날짜 알림 확인",
                    "2024-06-12",
                    TodoMainActivity::class.java
                ),
                Study(
                    "JetPack, Android SDK",
                    "Figma",
                    "Music List & 교차 플레이, Room DB, MediaPlayer 사용",
                    "2024-07-10",
                    FigmaOneActivity::class.java
                ),
                Study(
                    "JetPack, Android SDK",
                    "Figma",
                    "Music List & 교차 플레이, Room DB, MediaPlayer 사용 (2)",
                    "2024-07-10",
                    MusicActivity::class.java
                ),
                Study(
                    "JetPack",
                    "Figma",
                    "채팅 앱",
                    "2024-07-10",
                    FigmaTwoActivity::class.java
                ),
            )
        )
    }

    /**
     * 학습 목록 관리
     * (data) Study
     * (view) ItemMainStudyBinding -> item_main_study.xml
     * (adapter) StudyAdapter
     * (holder) StudyHolder
     */
    data class Study(
        val category: String, // 학습 종류
        val title: String, // 대 주제
        val comment: String, // 내용
        val startDate: String, // 시작 날짜

        // 이동할 화면 Activity
        val activity: Class<out AppCompatActivity>

        // TODO: 추가 필요한 정보 있으면 추가
        // ex: 파일 경로 / 진행 상태(진행중, 완료) / 기타
    )

    class StudyAdapter(
        private val studyList: MutableList<Study>
    ) : RecyclerView.Adapter<StudyAdapter.StudyHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StudyAdapter.StudyHolder {
            val binding =
                ItemMainStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StudyHolder(binding)
        }

        override fun onBindViewHolder(holder: StudyAdapter.StudyHolder, position: Int) {
            holder.itemBinding()
        }

        override fun getItemCount(): Int {
            return studyList.size
        }

        inner class StudyHolder(var binding: ItemMainStudyBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun itemBinding() {
                val startDate = studyList[adapterPosition].startDate
                binding.tvStudyCategory.text = studyList[adapterPosition].category
                binding.tvStudyTitle.text = studyList[adapterPosition].title
                binding.tvStudyComment.text = studyList[adapterPosition].comment
                binding.tvStudyStartDate.text = startDate

                getDaysSinceStartDate(startDate).let {
                    binding.tvStudyStartDate.append("  D+${it}")
                }

                binding.root.setOnClickListener {
                    val intent = Intent(binding.root.context, studyList[adapterPosition].activity)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        /**
         * 시작 날짜부터 현재까지의 일수 계산
         *
         * @param startDate
         * @param dateFormat
         * @return
         */
        fun getDaysSinceStartDate(startDate: String, dateFormat: String = "yyyy-MM-dd"): Long {
            val dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            val startDateParsed = dateFormat.parse(startDate)
            val currentDate = Date()

            return if (startDateParsed != null) {
                val diff = currentDate.time - startDateParsed.time
                diff / (1000 * 60 * 60 * 24)
            } else {
                -1 // Parsing error
            }
        }

    }
}