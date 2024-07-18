package com.wjdaudtn.mission.figma.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.databinding.FragmentFigmaTwoPartTwoBinding
import com.wjdaudtn.mission.figma.FigmaTalkDatabaseInit
import com.wjdaudtn.mission.figma.adapter.FigmaTwoAdapterPartTwo
import com.wjdaudtn.mission.figma.database.TalkDao


class FigmaTwoFragmentPartTwo : Fragment() {
    private lateinit var talkDaoInstance: TalkDao
    private lateinit var binding: FragmentFigmaTwoPartTwoBinding

    data class TalkItem2(
        var id: Int,
        var content: String,
        var user: Int
    ) //adapter에 넣기위한 클래스객체

    private lateinit var talkList2: MutableList<TalkItem2> //adapter 에 넣기 위한 객체 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        talkDaoInstance = FigmaTalkDatabaseInit().getTalkDao(requireContext())
        binding = FragmentFigmaTwoPartTwoBinding.inflate(layoutInflater)
        talkList2 = mutableListOf()
        for (i in 0 until talkDaoInstance.getTalkAll().size) {
            talkList2.add(createListInit(i + 1))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val view: View = inflater.inflate(R.layout.fragment_figma_two_part_two, container, false)
//        binding.figmaTwoRecyclerviewPartTwo.layoutManager = LinearLayoutManager(FigmaTwoActivity().baseContext)
//        binding.figmaTwoRecyclerviewPartTwo.adapter = FigmaTwoAdapterPartTwo(talkList2)
//        return view
        binding = FragmentFigmaTwoPartTwoBinding.inflate(inflater, container, false)
        binding.figmaTwoRecyclerviewPartTwo.layoutManager = LinearLayoutManager(requireContext())
        binding.figmaTwoRecyclerviewPartTwo.adapter = FigmaTwoAdapterPartTwo(talkList2)
        return binding.root
    }

    private fun createListInit(id: Int): TalkItem2 {
        val talkEntity = talkDaoInstance.getTalkId(id)
        return TalkItem2(
            id = talkEntity!!.id,
            content = talkEntity.content,
            user = if (talkEntity.userNum == 1) 2 else 1
        )
    }
}