package com.mumu.filebrowser.presenter.impl

import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.views.INavigationView
import com.mumu.filebrowser.presenter.INavigationPresenter
import com.mumu.filebrowser.presenter.IPresenter

/**
 * Created by leonardo on 17-11-24.
 */
class NavigationPresenterImpl : INavigationPresenter, IPresenter {
    private var mNavigationView: INavigationView? = null
    private val mPathModel: IPathModel = PathModel

    override fun <INavigationView> bindView(view: INavigationView?) {
        mNavigationView = if (view == null) null else view as com.mumu.filebrowser.views.INavigationView
    }

    override fun onNavigation(navigation: Int): Boolean {
        mPathModel.enter(navigation)
        return true
    }

}