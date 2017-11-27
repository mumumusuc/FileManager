package presenter.impl

import android.content.Context
import android.util.Log
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.Config
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.FocusedEvent
import com.mumu.filebrowser.eventbus.events.LayoutChangeEvent
import com.mumu.filebrowser.eventbus.events.PathChangeEvent
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.model.IModel
import com.mumu.filebrowser.model.IModel.LAYOUT_STYLE_GRID
import com.mumu.filebrowser.model.IModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.model.impl.ModelImpl
import com.mumu.filebrowser.views.IListView
import presenter.IListPresenter
import presenter.IListPresenter.*
import presenter.IPresenter
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import android.os.Build
import android.view.View
import com.mumu.filebrowser.utils.FileUtils


/**
 * Created by leonardo on 17-11-24.
 */
class ListPresenterImpl() : IListPresenter<IFile>, IPresenter {
    private val mModel: IModel = ModelImpl
    private var mListView: IListView<IFile>? = null
    private var mContext: Context? = null
    @IListPresenter.ViewMode
    private var mCurrentViewMode = MODE_NORMAL_VIEW
    private var mCurrentPath = ""
    private val mSelectedState: MutableSet<IFile> = mutableSetOf<IFile>()

    init {
        EventBus.getInstance().register(this)
    }

    override fun getList(): List<IFile> {
        if (mCurrentPath.equals(mModel.currentPath)) {
            open(FileWrapper(mModel.currentPath))
            mCurrentPath = mModel.currentPath
        }
        return mModel.currentFiles
    }

    override fun <IListView> bindView(view: IListView?) {
        mListView = if (view == null) null else view as com.mumu.filebrowser.views.IListView<IFile>
        mListView?.setEmptyView(R.layout.empty_list)
        if (mListView != null && mListView is View) {
            mContext = (mListView as View).context
        } else {
            mContext = null
        }
    }

    override fun onItemClick(item: IFile) {
        Log.d("onItemClick", "" + mCurrentViewMode + "," + item.name)
        open(item)
    }

    override fun onItemLongClick(item: IFile) {
        mCurrentViewMode = MODE_MULTI_SELECT
        onItemClick(item)
    }

    override fun getCurrentLayoutStyle() = mModel.layoutStyle

    override fun getCurrentViewMode() = mCurrentViewMode

    private fun open(file: IFile) {
        when (mCurrentViewMode) {
            MODE_NORMAL_VIEW -> {
                if (Config.doubleClickOpen()) {
                    mListView?.focus(file)
                } else {
                    if (file.isFolder) {
                        mModel.setPath(mModel.currentCategory, file.path)
                        mSelectedState.clear()
                    } else {
                        openFile(file)
                    }
                }
                EventBus.getInstance().post(FocusedEvent(file))
            }
            MODE_MULTI_SELECT -> {
                file as FileWrapper
                val selected = !file.isSelected
                file.isSelected = selected
                mListView?.select(file)
                if (selected) {
                    mSelectedState += file
                } else if (mSelectedState.contains(file)) {
                    mSelectedState -= file
                }
                if (mSelectedState.size == 0) {
                    mCurrentViewMode = MODE_NORMAL_VIEW
                    EventBus.getInstance().post(FocusedEvent(FileWrapper(mModel.currentPath)))
                } else {
                    EventBus.getInstance().post(SelectedEvent(mSelectedState.toTypedArray()))
                }
            }
        }
    }

    @Subscribe
    fun onPathChangeEvent(event: PathChangeEvent) {
        open(FileWrapper(mModel.currentPath))
        mListView?.notifyDataSetChanged()
    }

    @Subscribe
    fun onLayoutChangeEvent(event: LayoutChangeEvent) {
        when (mModel.layoutStyle) {
            LAYOUT_STYLE_LIST -> {
                mListView?.showAsList(true)
            }
            LAYOUT_STYLE_GRID -> {
                mListView?.showAsGrid(true)
            }
        }
    }

    private fun openFile(file: IFile) {
        val type = FileUtils.Companion.getMIMEType(file.suffix) ?: return
        val intent = Intent()
        intent.action = android.content.Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                    mContext,
                    mContext?.getBasePackageName() + ".fileProvider",
                    (file as FileWrapper).asFile())
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile((file as FileWrapper).asFile())
        }
        intent.setDataAndType(uri, type)
        mContext?.startActivity(intent)
    }
}
