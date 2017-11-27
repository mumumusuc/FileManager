package com.mumu.filebrowser.views.impl

import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.view.*
import android.widget.EditText
import com.mumu.filebrowser.R
import com.mumu.filebrowser.views.IToolView
import presenter.IPresenter
import presenter.IToolPresenter
import presenter.impl.ToolPresenterImpl

/**
 * Created by leonardo on 17-11-16.
 */
class ToolViewImpl(inflater: MenuInflater, menu: Menu) : IToolView, View.OnClickListener, View.OnKeyListener, View.OnAttachStateChangeListener {
    companion object {
        private val sToolPresenter: IToolPresenter = ToolPresenterImpl()
    }

    val changeAction: MenuItem
    val searchAction: MenuItem
    val parentPathAction: MenuItem
    val searchRoot: ViewGroup
    val searchSearch: View
    val searchClear: View
    val searchEdit: EditText

    init {
        inflater.inflate(R.menu.toolbar_tools, menu);
        changeAction = menu.findItem(R.id.action_change_style)
        searchAction = menu.findItem(R.id.action_search)
        parentPathAction = menu.findItem(R.id.action_path_pre)
        searchRoot = searchAction.actionView as ViewGroup
        searchSearch = searchRoot.findViewById(R.id.action_search_search)
        searchClear = searchRoot.findViewById(R.id.action_search_clear)
        searchEdit = searchRoot.findViewById(R.id.action_search_edit)
        searchSearch.setOnClickListener(this)
        searchClear.setOnClickListener(this)
        searchEdit.setOnKeyListener(this)
        searchSearch.addOnAttachStateChangeListener(this)
    }

    override fun onViewDetachedFromWindow(v: View?) {
        (sToolPresenter as IPresenter).bindView(null)
    }

    override fun onViewAttachedToWindow(v: View?) {
        (sToolPresenter as IPresenter).bindView(this)
    }

    override fun onActionItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (item.itemId) {
            R.id.action_change_style -> {
                sToolPresenter?.onChangeLayout()
                return true
            }
            R.id.action_path_pre -> {
                sToolPresenter?.onBack()
                return true
            }
        }
        return false;
    }

    private fun expandSearchView(expand: Boolean) {
        if (expand) {
            searchEdit.visibility = View.VISIBLE
            searchClear.visibility = View.VISIBLE
            searchEdit.requestFocus()
        } else {
            searchEdit.visibility = View.GONE
            searchClear.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.action_search_search -> {
                if (!isExpand()) {
                    TransitionManager.beginDelayedTransition(searchRoot, ChangeBounds())
                    expandSearchView(true)
                } else {
                    doSearch("")
                }
            }
            R.id.action_search_clear -> {
                searchEdit.setText("")
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if ((event!!.action == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_BACK)) {
            return cancelAllActions()
        }
        return false;
    }

    private fun isExpand() = searchEdit.visibility == View.VISIBLE

    private fun doSearch(str: String) {

    }

    override fun cancelAllActions(): Boolean {
        if (isExpand()) {
            TransitionManager.beginDelayedTransition(searchRoot, ChangeBounds())
            expandSearchView(false)
            return true
        }
        return false;
    }

    override fun showListIcon() {
        changeAction.setIcon(R.drawable.ic_tool_style_list)
        changeAction.setTitle(R.string.tool_title_list)
    }

    override fun showGridIcon() {
        changeAction.setIcon(R.drawable.ic_tool_style_grid)
        changeAction.setTitle(R.string.tool_title_grid)
    }

}