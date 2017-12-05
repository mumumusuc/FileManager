package com.mumu.filebrowser.presenter.impl

import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.PathChangeEvent
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.model.ILayoutModel
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_GRID
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.LayoutModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.views.IToolView
import com.mumu.filebrowser.presenter.IPresenter
import com.mumu.filebrowser.presenter.IToolPresenter

/**
 * Created by leonardo on 17-11-25.
 */
class ToolPresenterImpl : IToolPresenter, IPresenter {
    private var mToolView: IToolView? = null
    private val mLayoutModel: ILayoutModel = LayoutModel
    private val mPathModel: IPathModel = PathModel

    init {
        EventBus.getInstance().register(this)
    }

    override fun <IToolView> bindView(view: IToolView?) {
        mToolView = if (view == null) null else view as com.mumu.filebrowser.views.IToolView
        when (mLayoutModel.layoutStyle) {
            LAYOUT_STYLE_LIST -> {
                mToolView?.showGridIcon()
            }
            LAYOUT_STYLE_GRID -> {
                mToolView?.showListIcon()
            }
        }
        checkButtonState()
    }

    override fun onPrevious() {
        mPathModel.enterPrevious()
    }

    override fun onNext() {
        mPathModel.enterNext()
    }

    override fun onRefresh() {
        mPathModel.refresh()
    }

    override fun onSelect() {
        EventBus.getInstance().post(SelectedEvent(null))
    }

    override fun onChangeLayout() {
        var style: Int
        when (mLayoutModel.layoutStyle) {
            LAYOUT_STYLE_LIST -> {
                mToolView?.showListIcon()
                style = LAYOUT_STYLE_GRID
            }
            else -> {
                mToolView?.showGridIcon()
                style = LAYOUT_STYLE_LIST
            }
        }
        mLayoutModel.layoutStyle = style
    }

    override fun onSearch() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkButtonState() {
        mToolView?.enableAction(R.id.tool_pre,mPathModel.havePreviousPath())
        mToolView?.enableAction(R.id.tool_next,mPathModel.haveNextPath())
    }

    @Subscribe
    fun onPathChangeEvent(event:PathChangeEvent){
        checkButtonState()
    }
}