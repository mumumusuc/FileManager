package presenter.impl

import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.LayoutChangeEvent
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.model.IModel
import com.mumu.filebrowser.model.IModel.LAYOUT_STYLE_GRID
import com.mumu.filebrowser.model.IModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.model.impl.ModelImpl
import com.mumu.filebrowser.views.IToolView
import presenter.IPresenter
import presenter.IToolPresenter

/**
 * Created by leonardo on 17-11-25.
 */
class ToolPresenterImpl : IToolPresenter, IPresenter {
    private var mToolView: IToolView? = null
    private val mModel: IModel = ModelImpl

    override fun <IToolView> bindView(view: IToolView?) {
        mToolView = if (view == null) null else view as com.mumu.filebrowser.views.IToolView
        when (mModel.layoutStyle) {
            LAYOUT_STYLE_LIST -> {
                mToolView?.showGridIcon()
            }
            LAYOUT_STYLE_GRID -> {
                mToolView?.showListIcon()
            }
        }
    }

    override fun onChangeLayout() {
        var style: Int
        when (mModel.layoutStyle) {
            LAYOUT_STYLE_LIST -> {
                mToolView?.showListIcon()
                style = LAYOUT_STYLE_GRID
            }
            else -> {
                mToolView?.showGridIcon()
                style = LAYOUT_STYLE_LIST
            }
        }
        mModel.layoutStyle = style
    }

    override fun onSearch() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAllSelect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBack() {
        mModel.setPath(mModel.currentCategory,FileWrapper(mModel.currentPath).parent!!)
    }
}