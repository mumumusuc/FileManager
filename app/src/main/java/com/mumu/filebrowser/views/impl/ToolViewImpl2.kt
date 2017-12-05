package com.mumu.filebrowser.views.impl

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mumu.filebrowser.R
import com.mumu.filebrowser.presenter.IPresenter
import com.mumu.filebrowser.presenter.IToolPresenter
import com.mumu.filebrowser.presenter.impl.ToolPresenterImpl
import com.mumu.filebrowser.views.IToolView

class ToolViewImpl2 : LinearLayout, IToolView, View.OnClickListener {
    private val mActionPre: TextView by lazy { findViewById<TextView>(R.id.tool_pre) }
    private val mActionNext: TextView by lazy { findViewById<TextView>(R.id.tool_next) }
    private val mActionRefresh: TextView by lazy { findViewById<TextView>(R.id.tool_refresh) }
    private val mActionSelect: TextView by lazy { findViewById<TextView>(R.id.tool_select) }
    private val mActionStyle: TextView by lazy { findViewById<TextView>(R.id.tool_style) }
    private val mActionSearch: TextView by lazy { findViewById<TextView>(R.id.tool_search) }

    companion object {
        private val sPresenter: IToolPresenter = ToolPresenterImpl()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        mActionPre.setOnClickListener(this)
        mActionNext.setOnClickListener(this)
        mActionRefresh.setOnClickListener(this)
        mActionSelect.setOnClickListener(this)
        mActionStyle.setOnClickListener(this)
        mActionSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tool_pre -> {
                sPresenter?.onPrevious()
            }
            R.id.tool_next -> {
                sPresenter?.onNext()
            }
            R.id.tool_refresh -> {
                sPresenter?.onRefresh()
            }
            R.id.tool_select -> {
                sPresenter?.onSelect()
            }
            R.id.tool_style -> {
                sPresenter?.onChangeLayout()
            }
            R.id.tool_search -> {
                sPresenter?.onSearch()
            }
        }
    }

    override fun enableAction(action: Int, enable: Boolean) {
        when (action) {
            R.id.tool_pre -> {
                mActionPre.isEnabled = enable
            }
            R.id.tool_next -> {
                mActionNext.isEnabled = enable
            }
            R.id.tool_refresh -> {
                mActionRefresh.isEnabled = enable
            }
            R.id.tool_select -> {
                mActionSelect.isEnabled = enable
            }
            R.id.tool_style -> {
                mActionStyle.isEnabled = enable
            }
            R.id.tool_search -> {
                mActionSearch.isEnabled = enable
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (sPresenter as IPresenter).bindView(this)
    }

    override fun onDetachedFromWindowInternal() {
        super.onDetachedFromWindowInternal()
        (sPresenter as IPresenter).bindView(null)
    }

    override fun showListIcon() {
        mActionStyle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tool_style_list, 0, 0, 0)
        mActionStyle.setText(R.string.tool_title_list)
    }

    override fun showGridIcon() {
        mActionStyle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tool_style_grid, 0, 0, 0)
        mActionStyle.setText(R.string.tool_title_grid)
    }

    override fun cancelAllActions(): Boolean {
        //   TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }
}