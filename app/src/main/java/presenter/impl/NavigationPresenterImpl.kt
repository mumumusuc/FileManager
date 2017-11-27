package presenter.impl

import android.view.View
import com.mumu.filebrowser.R
import com.mumu.filebrowser.model.IModel
import com.mumu.filebrowser.model.impl.ModelImpl
import com.mumu.filebrowser.utils.FileUtils
import com.mumu.filebrowser.views.INavigationView
import presenter.INavigationPresenter
import presenter.IPresenter

/**
 * Created by leonardo on 17-11-24.
 */
class NavigationPresenterImpl : INavigationPresenter, IPresenter {
    private var mNavigationView: INavigationView? = null
    private val mModel: IModel = ModelImpl

    override fun <INavigationView> bindView(view: INavigationView?) {
        mNavigationView = if (view == null) null else view as com.mumu.filebrowser.views.INavigationView
    }

    override fun onNavigation(navigation: String): Boolean {
        mModel.setPath(navigation, FileUtils.getNavigationPath(navigation)!!)
        return true
    }

}