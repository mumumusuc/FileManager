package presenter.impl


import com.mumu.filebrowser.views.IMainView
import presenter.IMainPresenter
import presenter.IPresenter

/**
 * Created by leonardo on 17-11-24.
 */
class MainPresenterImpl() : IMainPresenter, IPresenter {
    var mMainView:IMainView? = null

    override fun <IMainView> bindView(view: IMainView?) {
        mMainView = view as com.mumu.filebrowser.views.IMainView
    }
}