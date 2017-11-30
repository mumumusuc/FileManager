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
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.views.IListView
import presenter.IListPresenter
import presenter.IPresenter
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import android.os.Build
import android.view.View
import com.mumu.filebrowser.model.ILayoutModel
import com.mumu.filebrowser.model.impl.LayoutModel
import com.mumu.filebrowser.utils.FileUtils


/**
 * Created by leonardo on 17-11-24.
 */
class ListPresenterImpl : IListPresenter<IFile>, IPresenter {
    private val TAG = ListPresenterImpl::class.java.simpleName
    private val MODE_NORMAL_VIEW = 10L
    private val MODE_MULTI_SELECT = 11L

    private val mModel: IPathModel = PathModel
    private val mLayoutModel: ILayoutModel = LayoutModel
    private val mStateModel = SelectState
    private var mListView: IListView<IFile>? = null
    private var mContext: Context? = null
    private var mCurrentViewMode = MODE_NORMAL_VIEW
    private var mCurrentPath = ""

    init {
        EventBus.getInstance().register(this)
    }

    override fun getList(): List<IFile> {
        if (mCurrentPath == mModel.currentPath) {
            open(FileWrapper(mModel.currentPath), false)
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
        open(item, true)
    }

    override fun onItemLongClick(item: IFile) {
        mCurrentViewMode = MODE_MULTI_SELECT
        onItemClick(item)
    }

    override fun getCurrentLayoutStyle() = mLayoutModel.layoutStyle

    private fun open(file: IFile, set: Boolean) {
        when (mCurrentViewMode) {
            MODE_NORMAL_VIEW -> {
                if (Config.doubleClickOpen()) {
                    mListView?.focus(file)
                } else {
                    if (file.isFolder) {
                        if (set) {
                            mModel.setPath(mModel.currentCategory, file.path, true)
                        }
                        mStateModel.clean()
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
                    mStateModel.addSelectedFile(file)
                } else {
                    mStateModel.removeSelectedFile(file)
                }
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

    @Subscribe
    fun onPathChangeEvent(event: PathChangeEvent) {
        Log.d(TAG, "onPathChangeEvent -> ")
        open(FileWrapper(mModel.currentPath), false)
        mListView?.notifyDataSetChanged()
    }

    @Subscribe
    fun onLayoutChangeEvent(event: LayoutChangeEvent) {
        Log.d(TAG, "onLayoutChangeEvent -> ")
        when (LayoutModel.layoutStyle) {
            ILayoutModel.LAYOUT_STYLE_LIST -> {
                mListView?.showAsList(true)
            }
            ILayoutModel.LAYOUT_STYLE_GRID -> {
                mListView?.showAsGrid(true)
            }
        }
    }

    @Subscribe
    fun onSelectedFileChange(event: SelectedEvent) {
        val files = event.files
        Log.d(TAG, "onSelectedFileChange -> ${files.size} changes")
        if (files.isEmpty()) {
            mCurrentViewMode = MODE_NORMAL_VIEW
            EventBus.getInstance().post(FocusedEvent(FileWrapper(mModel.currentPath)))
        }
    }

    object SelectState {

        private val mSelectedState: MutableSet<IFile> = mutableSetOf<IFile>()

        fun clean() {
            mSelectedState.clear()
            EventBus.getInstance().post(SelectedEvent(getSelectedFiles()))
        }

        fun addSelectedFile(file: IFile) {
            mSelectedState += file
            EventBus.getInstance().post(SelectedEvent(getSelectedFiles()))
        }

        fun removeSelectedFile(file: IFile) {
            if (mSelectedState.contains(file)) {
                mSelectedState -= file
                EventBus.getInstance().post(SelectedEvent(getSelectedFiles()))
            }
        }

        fun getSelectedFiles(): Array<IFile> {
            return mSelectedState.toTypedArray()
        }
    }
}
