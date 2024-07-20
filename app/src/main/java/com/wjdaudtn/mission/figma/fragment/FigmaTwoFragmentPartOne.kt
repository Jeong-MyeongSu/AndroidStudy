package com.wjdaudtn.mission.figma.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.databinding.FragmentFigmaTwoPartOneBinding
import com.wjdaudtn.mission.figma.FigmaTalkDatabaseInit
import com.wjdaudtn.mission.figma.adapter.FigmaTwoAdapterPartOne
import com.wjdaudtn.mission.figma.database.TalkDao
import com.wjdaudtn.mission.figma.database.TalkEntity


class FigmaTwoFragmentPartOne : Fragment() {

    private lateinit var talkDaoInstance: TalkDao
    private lateinit var binding: FragmentFigmaTwoPartOneBinding
    private lateinit var mAdapterPartOne: FigmaTwoAdapterPartOne


    private var who:Int = 0

    data class TalkItem(
        var id: Int,
        var content: String,
        var user: Int
    ) // adapter에 넣기위한 클래스객체

    private lateinit var talkList: MutableList<TalkItem> // adapter 에 넣기 위한 객체 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        talkList = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFigmaTwoPartOneBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        talkDaoInstance = FigmaTalkDatabaseInit().getTalkDao(requireContext())

        val useAlternativeUser = arguments?.getBoolean(ARG_USE_ALTERNATIVE_USER, false) ?: false
        who = if(useAlternativeUser) 1 else 2

        for (i in 0 until talkDaoInstance.getTalkAll().size) {
            talkList.add(createListInit(i + 1, useAlternativeUser))
            Log.d("talklist", talkList[i].content)
        }

        mAdapterPartOne = FigmaTwoAdapterPartOne(talkList, object: FigmaTwoAdapterPartOne.ZipperUpCallback{
            override fun zipperUpRequest() {
                hideKeyboard()
            }
        }, this)
        binding.figmaTwoRecyclerviewPartOne.layoutManager = LinearLayoutManager(requireContext())
        binding.figmaTwoRecyclerviewPartOne.adapter = mAdapterPartOne
        binding.figmaTwoRecyclerviewPartOne.scrollToPosition(talkList.size-1)

//      포커스가 바뀔때 리스너
        binding.figmaTwoEditText.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                zipperUp()
                Log.d("포커스 바뀜", "포커스 바뀜")
            } else {
                zipperUp()
                Log.d("포커스 바뀜", "포커스 바뀜")
            }
        }

        //searchView query 리스너
        binding.figmaTwoEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("onQueryTextSubmit", "${binding.figmaTwoEditText.query}입력함")

                submitTextUpdate(who, useAlternativeUser)

                binding.figmaTwoEditText.setQuery("", false)//텍스트 지우기
                binding.figmaTwoEditText.clearFocus() //포커스 제거
                hideCloseButton() // X 버튼 숨기기

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                hideCloseButton() // X 버튼 숨기기

                return true
            }
        })
        //버튼 으로 TextSubmit
        binding.submitButton.setOnClickListener {
            val query = binding.figmaTwoEditText.query.toString()
            Log.d("onQueryTextSubmitButton", "${query}, 입력함")

            submitTextUpdate(who,useAlternativeUser)

            binding.figmaTwoEditText.setQuery("", false) // 텍스트를 지웁니다
            binding.figmaTwoEditText.clearFocus() // 포커스를 제거합니다
            hideCloseButton() // X 버튼 숨기기
        }

        //mainActivity 버튼 눌렀을 때도 최대로 올라 오도록
        zipperUp()
    }


    private fun createListInit(id: Int, useAlternativeUser: Boolean): TalkItem {
        val talkEntity = talkDaoInstance.getTalkId(id)
        return TalkItem(
            id = talkEntity!!.id,
            content = talkEntity.content,
            user = if (useAlternativeUser) {
                if (talkEntity.userNum == 1) 2 else 1
            } else {
                if (talkEntity.userNum == 1) 1 else 2
            }
        )
    }

        //버튼 숨기기 함수
    fun hideCloseButton() {
        try {
            // SearchView에서 Close 버튼을 찾아 숨기기
            val searchCloseButtonId = binding.figmaTwoEditText.context.resources
                .getIdentifier("android:id/search_close_btn", null, null)//x버튼의 네임을 어떻게 알 수있지?
            val closeButton =
                binding.figmaTwoEditText.findViewById<ImageView>(searchCloseButtonId) //close 버튼 객채 초기화
            closeButton?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //데이터베이스 insert 후 talkList.add, recyclerview update
    private fun submitTextUpdate(num: Int, useAlternativeUser: Boolean) {
        talkDaoInstance.setInsertTalk(
            TalkEntity(
                content = binding.figmaTwoEditText.query.toString(),
                userNum = num
            )
        )//데이터베이스 삽입
        plusTalkList(useAlternativeUser) //view에 들어갈 객체에 .add
    }

    fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.figmaTwoEditText.clearFocus() // 포커스를 제거합니다
    }

    private fun plusTalkList(useAlternativeUser: Boolean) {
        talkList.add(createListInit(talkDaoInstance.getTalkAll().size,useAlternativeUser))
        mAdapterPartOne.updateList()
        zipperUp() // 포커스를 맞춘 상태에서 스크롤
    }
    private fun zipperUp(){
        binding.figmaTwoRecyclerviewPartOne.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onGlobalLayout() {
                    binding.figmaTwoRecyclerviewPartOne.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mAdapterPartOne.notifyDataSetChanged()
                    binding.figmaTwoRecyclerviewPartOne.scrollToPosition(talkList.size - 1)
                }
            })
        Log.d("지퍼 올려","지퍼 올려")
    }

    companion object {
        private const val ARG_USE_ALTERNATIVE_USER = "useAlternativeUser"

        @JvmStatic
        fun newInstance(useAlternativeUser: Boolean): FigmaTwoFragmentPartOne {
            return FigmaTwoFragmentPartOne().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_USE_ALTERNATIVE_USER, useAlternativeUser)
                }
            }
        }
    }
}