package presenter.impl

import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.utils.FileUtils
import com.mumu.filebrowser.views.INavigationView
import presenter.INavigationPresenter
import presenter.IPresenter

/**
 * Created by leonardo on 17-11-24.
 */
class NavigationPresenterImpl : INavigationPresenter, IPresenter {
    private var mNavigationView: INavigationView? = null
    private val mModel: IPathModel = PathModel

    override fun <INavigationView> bindView(view: INavigationView?) {
        mNavigationView = if (view == null) null else view as com.mumu.filebrowser.views.INavigationView
    }

    override fun onNavigation(navigation: String): Boolean {
        mModel.setPath(navigation, FileUtils.getNavigationPath(navigation)!!,true)
        return true
    }

}