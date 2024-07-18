package com.wjdaudtn.mission.figma.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.databinding.FragmentFigmaTwoPartOneBinding
import com.wjdaudtn.mission.figma.FigmaTalkDatabaseInit
import com.wjdaudtn.mission.figma.adapter.FigmaTwoAdapterPartOne
import com.wjdaudtn.mission.figma.database.TalkDao


class FigmaTwoFragmentPartOne : Fragment() {
    private lateinit var talkDaoInstance: TalkDao
    private lateinit var binding: FragmentFigmaTwoPartOneBinding

    data class TalkItem(
        var id: Int,
        var content: String,
        var user: Int
    ) //adapter에 넣기위한 클래스객체

    private lateinit var talkList: MutableList<TalkItem> //adapter 에 넣기 위한 객체 리스트


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        talkDaoInstance = FigmaTalkDatabaseInit().getTalkDao(requireContext())
        binding = FragmentFigmaTwoPartOneBinding.inflate(layoutInflater)
        talkList = mutableListOf()
        for (i in 0 until talkDaoInstance.getTalkAll().size) {
            talkList.add(createListInit(i + 1))
            Log.d("talklist", talkList[i].content)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFigmaTwoPartOneBinding.inflate(inflater, container, false)
        binding.figmaTwoRecyclerviewPartOne.layoutManager = LinearLayoutManager(requireContext())
        binding.figmaTwoRecyclerviewPartOne.adapter = FigmaTwoAdapterPartOne(talkList)
        return binding.root
    }

    private fun createListInit(id: Int): TalkItem {
        val talkEntity = talkDaoInstance.getTalkId(id)
        return TalkItem(
            id = talkEntity!!.id,
            content = talkEntity.content,
            user = if (talkEntity.userNum == 1) 1 else 2
        )
    }
}