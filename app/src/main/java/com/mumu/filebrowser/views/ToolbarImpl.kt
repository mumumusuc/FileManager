package com.mumu.filebrowser.views

import android.support.transition.ChangeBounds
import android.support.transition.Scene
import android.support.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.ChangeLayoutEvent

/**
 * Created by leonardo on 17-11-16.
 */
class ToolbarImpl(inflater: MenuInflater, menu: Menu) : ITools, View.OnClickListener {
    val menu = menu!!

    val changeAction: MenuItem
    val searchAction: MenuItem
    val searchRoot: ViewGroup
    val searchBg: View
    val searchSearch: View
    val searchClear: View
    val searchEdit: EditText

    init {
        inflater.inflate(R.menu.main, menu);
        changeAction = menu.findItem(R.id.action_change_style)
        searchAction = menu.findItem(R.id.action_search)
        searchRoot = searchAction.actionView as ViewGroup
        searchBg = searchRoot.findViewById(R.id.action_search_root)
        searchSearch = searchRoot.findViewById(R.id.action_search_search)
        searchClear = searchRoot.findViewById(R.id.action_search_clear)
        searchEdit = searchRoot.findViewById(R.id.action_search_edit)
        searchSearch.setOnClickListener(this)
        searchClear.setOnClickListener(this)
        EventBus.getInstance().register(this)
    }

    override fun onActionItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (item.itemId) {
            R.id.action_change_style -> {
                EventBus.getInstance().post(ChangeLayoutEvent(ChangeLayoutEvent.ASK, null))
                return true
            }
        }
        return false;
    }

    private fun expandSearchView(expand: Boolean) {
        if (expand) {
            searchEdit.visibility = View.VISIBLE
            searchClear.visibility = View.VISIBLE
            searchBg.setBackgroundResource(R.drawable.search_bg)
        } else {
            searchEdit.visibility = View.GONE
            searchClear.visibility = View.GONE
            searchBg.background = null
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
                TransitionManager.beginDelayedTransition(searchRoot, ChangeBounds())
                expandSearchView(false)
            }
        }
    }

    private fun isExpand() = searchEdit.visibility == View.VISIBLE

    private fun doSearch(str: String) {

    }

    override fun cancelAllActions(): Boolean {
        return false
    }

    @Subscribe
    fun onChangeLayoutEvent(event: ChangeLayoutEvent) {
        if (event.type == ChangeLayoutEvent.ACK) {
            val layout = event.layout!!
            if (layout == IListView.LAYOUT_STYLE_LIST) {
                changeAction.setIcon(R.drawable.ic_tool_style_list)
                changeAction.setTitle(R.string.tool_title_list)
            } else if (layout == IListView.LAYOUT_STYLE_GRID) {
                changeAction.setIcon(R.drawable.ic_tool_style_grid)
                changeAction.setTitle(R.string.tool_title_grid)
            }
        }
    }

}