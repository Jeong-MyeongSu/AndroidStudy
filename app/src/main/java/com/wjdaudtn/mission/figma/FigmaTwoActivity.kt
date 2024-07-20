package com.wjdaudtn.mission.figma

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.internal.ViewUtils
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityFigmaTwoBinding
import com.wjdaudtn.mission.databinding.CilpBinding
import com.wjdaudtn.mission.figma.database.TalkDao
import com.wjdaudtn.mission.figma.database.TalkEntity
import com.wjdaudtn.mission.figma.fragment.FigmaTwoFragmentPartOne

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

    private lateinit var talkDao: TalkDao
    private lateinit var binding: ActivityFigmaTwoBinding

    private lateinit var mFragmentPartOne: FigmaTwoFragmentPartOne
    private lateinit var mFragmentPartTwo: FigmaTwoFragmentPartOne

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFigmaTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        talkDao = FigmaTalkDatabaseInit().getTalkDao(applicationContext) //데이터베이스 초기화

        mFragmentPartOne = FigmaTwoFragmentPartOne.newInstance(true) //Fragment PartOne 초기화
        mFragmentPartTwo = FigmaTwoFragmentPartOne.newInstance(false) //Fragment PartOne 초기화

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

    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    private fun initView() {

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

    }

    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_part_one -> {
                Log.d("btn_part_one", "파트1 버튼 클릭")
                showPartOneFragment()
            }

            R.id.btn_part_two -> {
                Log.d("btn_part_two", "파트2 버튼 클릭")
                showPartTwoFragment()
            }
        }
    })

    private fun showPartOneFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_figma_two, mFragmentPartOne)
            .commit()
    }

    private fun showPartTwoFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_figma_two, mFragmentPartTwo)
            .commit()
    }
}

//클릭시 키보드 띄우는 법
//        binding.figmaTwoSearchView.setOnClickListener{
//            binding.figmaTwoSearchView.requestFocus()
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(binding.figmaTwoSearchView, InputMethodManager.SHOW_IMPLICIT)
//        }


//마진 설정 하는법
//private fun setFragmentContainerMargin(size: Int) {
//    val marginBottomDp = size // 원하는 마진 값 (dp 단위)
//    val marginBottomPx = TypedValue.applyDimension(
//        TypedValue.COMPLEX_UNIT_DIP,
//        marginBottomDp.toFloat(),
//        resources.displayMetrics
//    ).toInt()
//
//    val layoutParams = binding.fragmentContainerFigmaTwo.layoutParams as ConstraintLayout.LayoutParams
//    layoutParams.bottomMargin = marginBottomPx
//    binding.fragmentContainerFigmaTwo.layoutParams = layoutParams
//}