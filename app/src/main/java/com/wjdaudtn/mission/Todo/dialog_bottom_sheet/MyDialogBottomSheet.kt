package com.wjdaudtn.mission.todo.dialog_bottom_sheet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.FragmentMyDialogBottomSheetBinding
import com.wjdaudtn.mission.todo.AddActivity
import com.wjdaudtn.mission.todo.DataBaseInit
import com.wjdaudtn.mission.todo.TodoMainActivity
import com.wjdaudtn.mission.todo.database.Todo
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ID


class MyDialogBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentMyDialogBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var todoItemId: Int = 0


    interface OnDeleteListener {
        fun success(item: Todo)
        fun fail()
        fun finish(state: E_STUTS, item: Todo?) // true: 삭제 성공, false: 삭제 실패
    }

    enum class E_STUTS {
        SUCCESS,
        FAIL
    }

    private var onDeleteListener: OnDeleteListener? = null


    companion object {
        fun newInstance(todoItemId: Int, onDeleteListener: OnDeleteListener): MyDialogBottomSheet {
            val fragment = MyDialogBottomSheet()
            fragment.todoItemId = todoItemId
            fragment.onDeleteListener = onDeleteListener
            return fragment
        }//일단 chatgpt 한테 물어 봐서 사용
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyDialogBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        isCancelable = false

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_change -> {
                    val intent = Intent(
                        (activity as TodoMainActivity).baseContext,
                        AddActivity::class.java
                    )
                    intent.putExtra(RESULT_KEY_ID, todoItemId)
                    (activity as TodoMainActivity).requestLauncher.launch(intent)
                    dismiss()
                }

                R.id.item_delete -> {
                    val dbInstance =
                        DataBaseInit().getTodoDao((activity as TodoMainActivity).baseContext)
                    val todoItem = dbInstance.getTodoById(todoItemId)
                    if (todoItem == null) {
                        Log.e(tag, "$todoItemId 로 조회 했지만 Todo DB 에서 찾지 못했다.");
                        onDeleteListener!!.finish(E_STUTS.FAIL, null)
                    } else {
                        onDeleteListener!!.finish(E_STUTS.SUCCESS, todoItem)
                        dbInstance.setDeleteTodo(todoItem)
                    }
                    dismiss()
                }

            }
            false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}