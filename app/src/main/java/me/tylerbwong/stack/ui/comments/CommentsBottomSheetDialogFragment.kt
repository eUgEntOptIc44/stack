package me.tylerbwong.stack.ui.comments

import android.app.Dialog
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.comments_fragment.*
import me.tylerbwong.stack.R
import me.tylerbwong.stack.ui.questions.HeaderDataModel
import me.tylerbwong.stack.ui.utils.DynamicViewAdapter
import me.tylerbwong.stack.ui.utils.SpaceDataModel
import me.tylerbwong.stack.ui.utils.ViewHolderItemDecoration

class CommentsBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val viewModel by viewModels<CommentsViewModel>()
    private val adapter = DynamicViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.postId = arguments?.getInt(POST_ID) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.comments_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.apply {
            adapter = this@CommentsBottomSheetDialogFragment.adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                ViewHolderItemDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.item_spacing_main)
                )
            )
        }
        viewModel.data.observe(viewLifecycleOwner) {
            adapter.update(
                listOf(
                    SpaceDataModel(),
                    HeaderDataModel(
                        getString(R.string.comments),
                        getString(R.string.comment_count, it.size)
                    )
                ) + it.ifEmpty { listOf(SpaceDataModel()) }
            )
        }

        viewModel.fetchComments()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Prevent peeking when in landscape to avoid only showing top of bottom sheet
        if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
            dialog.setOnShowListener {
                dialog.findViewById<ViewGroup>(
                    com.google.android.material.R.id.design_bottom_sheet
                )?.let { bottomSheet ->
                    with(BottomSheetBehavior.from(bottomSheet)) {
                        peekHeight = bottomSheet.height
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        } else {
            dialog.setOnShowListener(null)
        }
        return dialog
    }

    companion object {
        private const val POST_ID = "post_id"

        fun show(fragmentManager: FragmentManager, postId: Int) {
            val fragment = CommentsBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(POST_ID, postId)
                }
            }
            fragment.show(fragmentManager, CommentsBottomSheetDialogFragment::class.java.simpleName)
        }
    }
}
