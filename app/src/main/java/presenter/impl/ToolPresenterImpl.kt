package presenter.impl

import com.mumu.filebrowser.model.ILayoutModel
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_GRID
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.model.impl.LayoutModel
import com.mumu.filebrowser.views.IToolView
import presenter.IPresenter
import presenter.IToolPresenter

/**
 * Created by leonardo on 17-11-25.
 */
class ToolPresenterImpl : IToolPresenter, IPresenter {
    private var mToolView: IToolView? = null
    private val mLayoutModel: ILayoutModel = LayoutModel

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAllSelect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBack() {
        //mModel.setPath(mModel.currentCategory,FileWrapper(mModel.currentPath).parent!!,true)
    }
}