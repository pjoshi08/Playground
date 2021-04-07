package com.pj.playground.presentation.reader

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.lifecycle.ViewModelProvider
import com.pj.playground.R
import com.pj.playground.domain.Document
import com.pj.playground.framework.MajesticViewModelFactory
import com.pj.playground.presentation.library.LibraryFragment
import com.pj.playground.util.createOpenIntent
import kotlinx.android.synthetic.main.fragment_reader.*


class ReaderFragment : Fragment() {

    companion object {
        fun newInstance(document: Document) =
            ReaderFragment().apply {
                arguments = ReaderViewModel.createArguments(document)
            }
    }

    private lateinit var viewModel: ReaderViewModel
    private lateinit var adapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initAdapter()
        setAdapterToRecyclerView()

        initViewModel()

        setObservers()

        processForConfigChange(savedInstanceState)

        setClickListeners()
    }

    private fun showPage(page: PdfRenderer.Page) {
        iv_page.visibility = View.VISIBLE
        pagesTextView.visibility = View.VISIBLE
        tabPreviousPage.visibility = View.VISIBLE
        tabNextPage.visibility = View.VISIBLE

        iv_page.drawable?.let { (it as? BitmapDrawable)?.bitmap?.recycle() }

        val size = Point()
        val pageHeight: Int?
        val pageWidth: Int?
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = activity?.windowManager?.currentWindowMetrics
            val windowInsets = windowMetrics?.windowInsets
            val insets = windowInsets?.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            val insetsWidth = insets?.right?.plus(insets.left)
            val insetsHeight = insets?.top?.plus(insets.bottom)

            val bounds = windowMetrics?.bounds
            pageWidth = bounds?.width()?.minus(insetsWidth ?: 0)
            pageHeight = bounds?.height()?.minus(insetsHeight ?: 0)
        } else {
            @Suppress("DEPRECATION")
            activity?.windowManager?.defaultDisplay?.getSize(size)

            pageWidth = size.x
            pageHeight = page.height * pageWidth / page.width
        }

        val bitmap = Bitmap.createBitmap(
            pageWidth ?: 0,
            pageHeight ?: 0,
            Bitmap.Config.ARGB_8888
        )

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        iv_page.setImageBitmap(bitmap)

        pagesTextView.text = getString(
            R.string.page_navigation_format,
            page.index + 1,
            viewModel.renderer.value?.pageCount
        )

        page.close()
    }

    private fun initAdapter() {
        adapter = BookmarkAdapter { viewModel.openBookmark(it) }
    }

    private fun setAdapterToRecyclerView() {
        bookmarksRecyclerView.adapter = adapter
    }

    private fun setObservers() {
        viewModel.document.observe(viewLifecycleOwner, {
            if (it == Document.EMPTY) {
                //show file picker action
                startActivityForResult(createOpenIntent(), LibraryFragment.READ_REQUEST_CODE)
            }
        })

        viewModel.bookmarks.observe(viewLifecycleOwner, {
            adapter.update(it)
        })

        viewModel.isBookmarked.observe(viewLifecycleOwner, {
            val bookmarkDrawable = if (it) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border
            tabBookmark.setCompoundDrawablesRelativeWithIntrinsicBounds(0, bookmarkDrawable, 0, 0)
        })

        viewModel.isInLibrary.observe(viewLifecycleOwner, {
            val libraryDrawable = if (it) R.drawable.ic_library else R.drawable.ic_library_border
            tabLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(0, libraryDrawable, 0, 0)
        })

        viewModel.currentPage.observe(viewLifecycleOwner, { showPage(it) })
        viewModel.hasNextPage.observe(viewLifecycleOwner, { tabNextPage.isEnabled = it })
        viewModel.hasPreviousPage.observe(viewLifecycleOwner, { tabPreviousPage.isEnabled = it })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, MajesticViewModelFactory)
            .get(ReaderViewModel::class.java)
    }

    private fun processForConfigChange(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.loadArguments(arguments)
        } else {
            // Recreating fragment after configuration change, reopen current page so it can be rendered again.
            viewModel.reopenPage()
        }
    }

    private fun setClickListeners() {
        tabBookmark.setOnClickListener { viewModel.toggleBookmark() }
        tabLibrary.setOnClickListener { viewModel.toggleInLibrary() }
        tabNextPage.setOnClickListener { viewModel.nextPage() }
        tabPreviousPage.setOnClickListener { viewModel.previousPage() }
    }
}