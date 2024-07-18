package com.wjdaudtn.mission.figma

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityFigmaTwoBinding
import com.wjdaudtn.mission.databinding.CilpBinding
import com.wjdaudtn.mission.figma.database.TalkDao
import com.wjdaudtn.mission.figma.fragment.FigmaTwoFragmentPartOne
import com.wjdaudtn.mission.figma.fragment.FigmaTwoFragmentPartTwo

/**
 *packageName    : com.wjdaudtn.mission.figma
 * fileName       : FigmaTwoActivity
 * author         : licen
 * date           : 2024-07-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-11        licen       최초 생성
 */
class FigmaTwoActivity : AppCompatActivity() {
    data class StringAndType(val string: String, val type: Int)  //리사이클러 뷰에 들어갈 리스트에 들어갈 데이터클래스 생성
//    lateinit var figmaTwoContextList: MutableList<StringAndType> //리사이클러 뷰에 들어갈 리스트
    private lateinit var talkDao: TalkDao
    private lateinit var binding: ActivityFigmaTwoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFigmaTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        talkDao = FigmaTalkDatabaseInit().getTalkDao(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }
    //툴 바 매뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //매뉴 3개 생성
        val menuItem1: MenuItem? = menu?.add(0, 0, 0, "북마크")
        menuItem1?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        val menuItem2: MenuItem? = menu?.add(0, 1, 0, "overflow1")
        menuItem2?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        val menuItem3: MenuItem? = menu?.add(0, 2, 0, "overflow2")
        menuItem3?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

        val menuItemBinding = CilpBinding.inflate(layoutInflater) //CLIP 이미지 객채로 받음
        menuItem1?.actionView = menuItemBinding.root // 북 마크 이미지로 나오게 actionview 속성에 넣어줌

        return true
    }

    private fun initView(){

        //툴바
        setSupportActionBar(binding.toolbarFigmaTwo)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Name"
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        binding.btnPartOne.setOnClickListener(customClickListener)
        binding.btnPartTwo.setOnClickListener(customClickListener)

        hideCloseButton()// x버튼 숨기기

        binding.figmaTwoEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("onQueryTextSubmit","입력함")
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



//
//        //recyclerView
//        binding.figmaTwoRecyclerview.adapter = FigmaTwoAdapter(figmaTwoContextList)
//        binding.figmaTwoRecyclerview.layoutManager = LinearLayoutManager(baseContext)
//
//

//        binding.figmaTwoEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//            android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                figmaTwoContextList.add(StringAndType(query.toString(), 2))
//                binding.figmaTwoEditText.setQuery("", false)//텍스트 지우기
//                binding.figmaTwoEditText.clearFocus() //포커스 제거
//                hideCloseButton() // X 버튼 숨기기
//                return false
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                hideCloseButton() // X 버튼 숨기기
//                return true
//            }
//        })
//
//        binding.submitButton.setOnClickListener {
//            val query = binding.figmaTwoEditText.query.toString()
//            figmaTwoContextList.add(StringAndType(query, 2))
//            binding.figmaTwoEditText.setQuery("", false) // 텍스트를 지웁니다
//            binding.figmaTwoEditText.clearFocus() // 포커스를 제거합니다
//            binding.figmaTwoRecyclerview.scrollToPosition(figmaTwoContextList.size - 1) // RecyclerView가 데이터 설정 후 스크롤을 가장 아래로 이동
//            hideCloseButton() // X 버튼 숨기기
//        }

    }

    private val customClickListener:View.OnClickListener = (View.OnClickListener { v ->
        when(v.id){
            R.id.btn_part_one -> {
                Log.d("btn_part_one","파트1 버튼 클릭")
                showPartOneFragment()
            }

            R.id.btn_part_two -> {
                Log.d("btn_part_two", "파트2 버튼 클릭")
                showPartTwoFragment()
            }
        }
    })
    private fun showPartOneFragment(){
        val partOneFragment = FigmaTwoFragmentPartOne()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_figma_two, partOneFragment)
            .commit()
    }
    private fun showPartTwoFragment(){
        val partTwoFragment = FigmaTwoFragmentPartTwo()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_figma_two, partTwoFragment)
            .commit()
    }

    //버튼 숨기기 함수
    fun hideCloseButton() {
        try {
            // SearchView에서 Close 버튼을 찾아 숨기기
            val searchCloseButtonId = binding.figmaTwoEditText.context.resources
                .getIdentifier("android:id/search_close_btn", null, null)//x버튼의 네임을 어떻게 알 수있지?
            val closeButton = binding.figmaTwoEditText.findViewById<ImageView>(searchCloseButtonId) //close 버튼 객채 초기화
            closeButton?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


//클릭시 키보드 띄우는 법
//        binding.figmaTwoSearchView.setOnClickListener{
//            binding.figmaTwoSearchView.requestFocus()
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(binding.figmaTwoSearchView, InputMethodManager.SHOW_IMPLICIT)
//        }