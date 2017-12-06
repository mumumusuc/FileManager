package com.mumu.filebrowser.views.impl

import android.animation.ObjectAnimator
import android.app.Instrumentation
import android.content.Context
import android.hardware.input.InputManager
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.FloatProperty
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mumu.filebrowser.R
import com.mumu.filebrowser.presenter.IPresenter
import com.mumu.filebrowser.presenter.IToolPresenter
import com.mumu.filebrowser.presenter.impl.ToolPresenterImpl
import com.mumu.filebrowser.views.IToolView
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.BackEvent


class ToolViewImpl : LinearLayout, IToolView, View.OnClickListener {
    private val TAG = ToolViewImpl::class.java.simpleName

    private val mActionPre: TextView by lazy { findViewById<TextView>(R.id.tool_pre) }
    private val mActionNext: TextView by lazy { findViewById<TextView>(R.id.tool_next) }
    private val mActionRefresh: TextView by lazy { findViewById<TextView>(R.id.tool_refresh) }
    private val mActionSelect: TextView by lazy { findViewById<TextView>(R.id.tool_select) }
    private val mActionStyle: TextView by lazy { findViewById<TextView>(R.id.tool_style) }
    private val mActionSearch: View by lazy { findViewById<View>(R.id.tool_search_root) }
    private val mSearchIcon: ImageView by lazy { findViewById<ImageView>(R.id.tool_search_icon) }
    private val mSearchInput: EditText by lazy { findViewById<EditText>(R.id.tool_search_input) }
    private val mSearchConfirm: View by lazy { findViewById<View>(R.id.tool_search_search) }
    private val mSearchBarWidth: Float by lazy { resources.getDimension(R.dimen.tool_search_input_width) }
    private val WIDTH_PROPERTY = object : FloatProperty<EditText>("WIDTH_PROPERTY") {
        override fun setValue(view: EditText?, value: Float) {
            view?.width = value.toInt()
            view?.layoutParams?.width = value.toInt()
        }

        override fun get(view: EditText?): Float = view?.width?.toFloat() ?: 0f
    }

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
        mSearchConfirm.setOnClickListener(this)
        mSearchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged -> ${s.toString()}")
                sPresenter?.onSearch(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mSearchInput.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_BACK && searchBarExpanded()) {
                    expandSearchBar()
                    return@OnKeyListener true
                }
                EventBus.getInstance().post(BackEvent())
            }
            return@OnKeyListener false
        })
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
            R.id.tool_search_search -> {
                expandSearchBar()
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
            R.id.tool_search_search -> {
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

    private fun expandSearchBar() {
        val start = mSearchInput.width.toFloat()
        val end: Float
        val useAnim: Boolean
        if (start == 0f) {
            end = mSearchBarWidth
            useAnim = true
            mSearchInput.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!imm.isActive)
                imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else if (start == mSearchBarWidth) {
            end = 0f
            useAnim = true
            mSearchInput.clearFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive)
                imm.hideSoftInputFromWindow(windowToken, 0)
            sPresenter?.onCancelSearch()
        } else {
            useAnim = false
            end = 0f
        }
        if (useAnim) {
            mSearchInput.setText("")
            val anim = ObjectAnimator
                    .ofFloat<EditText>(mSearchInput, WIDTH_PROPERTY, start, end)
                    .setDuration(resources.getInteger(R.integer.item_icon_animation_duration).toLong())
            anim.interpolator = AccelerateInterpolator()
            anim.start()
        }
    }

    override fun showSearchWait(show: Boolean) {

    }

    private fun searchBarExpanded(): Boolean = mSearchInput.width == mSearchBarWidth.toInt()

}