package com.mumu.filebrowser.presenter.impl

import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.FocusedEvent
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.views.IOverview
import com.mumu.filebrowser.presenter.IOverviewPresenter
import com.mumu.filebrowser.presenter.IPresenter

class OverviewPresenterImpl : IOverviewPresenter, IPresenter {
    private var mOverview: IOverview? = null

    init {
        EventBus.getInstance().register(this)
    }

    override fun <IOverview> bindView(view: IOverview?) {
        mOverview = if (view == null) null else view as com.mumu.filebrowser.views.IOverview
        mOverview?.showFocusedview(PathModel.path)
    }

    @Subscribe
    fun onFocusedEvent(event: FocusedEvent) {
        val file = event.file
        if (file == null) {
            mOverview?.cleanDisplay()
        } else {
            mOverview?.showFocusedview(file)
        }
    }

    @Subscribe
    fun onSelectedFileChange(event: SelectedEvent) {
        val files = event.files ?: return
        if (files.isEmpty()) {
            mOverview?.cleanDisplay()
        } else {
            mOverview?.showSelectedView(files)
        }
    }
}
