package presenter.impl

import com.mumu.filebrowser.Config
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.IListView
import presenter.IListPresenter
import presenter.IListPresenter.*
import presenter.IPresenter

/**
 * Created by leonardo on 17-11-24.
 */
class ListPresenterImpl() : IListPresenter<IFile>, IPresenter {
    private var mListView: IListView<IFile>? = null
    private var mList: MutableList<IFile> = mutableListOf<IFile>()
    private @IListPresenter.LayoutStyle
    var mCurrentLayoutStyle = LAYOUT_STYLE_LIST
    private @IListPresenter.ViewMode
    var mCurrentViewMode = MODE_NORMAL_VIEW
    private val mSelectedState: MutableSet<IFile> = mutableSetOf<IFile>()

    override fun getList() = mList

    override fun <IListView> bindView(view: IListView?) {
        mListView = view as com.mumu.filebrowser.views.IListView<IFile>
    }

    override fun onItemClick(item: IFile) {
        when (mCurrentViewMode) {
            MODE_NORMAL_VIEW -> {
                if (Config.doubleClickOpen()) {
                    mListView?.focus(item)
                    TODO("focus item")
                } else {
                    TODO("open item")
                }
            }
            MODE_MULTI_SELECT -> {
                item as FileWrapper
                val selected = !item.isSelected
                item.isSelected = selected
                mListView?.select(item)
                if (selected) {
                    mSelectedState += item
                } else if (mSelectedState.contains(item)) {
                    mSelectedState -= item
                }
                if (mSelectedState.size == 0) {
                    mCurrentViewMode = MODE_NORMAL_VIEW
                }
            }
        }
    }

    override fun onItemLongClick(item: IFile) {
        mCurrentViewMode = MODE_MULTI_SELECT
    }

    override fun getCurrentLayoutStyle() = mCurrentLayoutStyle

    override fun getCurrentViewMode() = mCurrentViewMode
}
