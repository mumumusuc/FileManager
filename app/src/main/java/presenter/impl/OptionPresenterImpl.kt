package presenter.impl

import android.content.res.Resources
import android.util.Log
import android.view.View
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.utils.FileUtils
import com.mumu.filebrowser.utils.OptionUtils
import com.mumu.filebrowser.utils.OptionUtils.Companion.CREATE_TYPE_FILE
import com.mumu.filebrowser.utils.OptionUtils.Companion.CREATE_TYPE_FOLDER
import com.mumu.filebrowser.utils.OptionUtils.Companion.NO_ERROR
import com.mumu.filebrowser.views.IOptionView
import com.mumu.filebrowser.views.IOptionView.*
import presenter.IOptionPresenter
import presenter.IPresenter
import java.io.File

/**
 * Created by leonardo on 17-11-27.
 */
class OptionPresenterImpl : IOptionPresenter, IPresenter {
    private val TAG = OptionPresenterImpl::class.java.simpleName

    private var mOptionView: IOptionView? = null
    private var mResources: Resources? = null
    private val mModel: IPathModel = PathModel
    private var mState: Int = NULL
    private val mSelectedFiles = mutableSetOf<IFile>()

    init {
        EventBus.getInstance().register(this)
    }

    override fun <IOptionView> bindView(view: IOptionView?) {
        mOptionView = if (view == null) null else view as com.mumu.filebrowser.views.IOptionView
        if (view != null && view is View) {
            mResources = (view as View).context.resources
            setOptionState(mSelectedFiles.size)
        } else {
            mResources = null
        }
    }

    override fun onCopy() {
        mState = COPY
    }

    override fun onMove() {
        mState = CUT
    }

    override fun onRename() {
        mState = RENAME

        mOptionView?.showDialog(
                mResources?.getString(R.string.opt_name_rename),
                null,
                mResources?.getString(R.string.opt_msg_rename),
                false
        )
    }

    override fun onDelete() {
        mState = DELETE
        mOptionView?.showDialog(
                mResources?.getString(R.string.opt_name_delete),
                mResources?.getString(R.string.opt_msg_delete),
                null,
                false
        )
    }

    override fun onCreate() {
        mState = CREATE
        mOptionView?.showDialog(
                mResources?.getString(R.string.opt_name_create),
                null,
                mResources?.getString(R.string.opt_msg_create),
                true
        )
    }

    override fun onPaste() {
        mOptionView?.showDialog(
                mResources?.getString(R.string.opt_name_paste),
                mResources?.getString(R.string.opt_msg_paste),
                null,
                true
        )
    }

    override fun onCancel() {
        mState = NULL
        mOptionView?.dismissDialog()
    }

    override fun onConfirm(content: String?, select: Int) {
        when (mState) {
            CREATE -> {
                val legal: Boolean = FileUtils.checkFileName(content)
                if (legal) {
                    val state = OptionUtils.create(
                            buildFullPath(content!!),
                            when (select) {
                                0 -> {
                                    CREATE_TYPE_FOLDER
                                }
                                else -> {
                                    CREATE_TYPE_FILE
                                }
                            }
                    )
                    when (state) {
                        NO_ERROR -> {
                            mModel.setPath(mModel.currentCategory, mModel.currentPath, true)
                        }
                        else -> {
                            Log.w("OptionPresenterImpl", "state = $state")
                        }
                    }
                    onCancel()
                } else {
                    mOptionView?.showDialog("", "请检查文件名是否正确", "", true)
                }
            }
            DELETE -> {
                var state: Boolean
                mSelectedFiles.map {
                    state = OptionUtils.delete(it)
                    Log.i("OptionPresenterImpl", "delete = $state")
                    if (state) {
                        mModel.setPath(mModel.currentCategory, mModel.currentPath, true)
                    }
                }
                mModel.setPath(mModel.currentCategory, mModel.currentPath, true)
                onCancel()
            }
            RENAME -> {
                val legal: Boolean = FileUtils.checkFileName(content)
                if (legal) {
                    val state = OptionUtils.rename(
                            mSelectedFiles.toTypedArray()[0],
                            buildFullPath(content!!))
                    if (state == NO_ERROR) {
                        mModel.setPath(mModel.currentCategory, mModel.currentPath, true)
                    } else {
                        Log.w("OptionPresenterImpl", "rename = $state")
                    }
                    onCancel()
                } else {
                    mOptionView?.showDialog("", "请检查文件名是否正确", "", true)
                }
            }
        }

    }

    private fun buildFullPath(name: String): String = mModel.currentPath + File.separatorChar + name

    @Subscribe
    fun onSelectedFileChange(event: SelectedEvent) {
        mSelectedFiles.clear()
        mSelectedFiles.addAll(event.files)
        Log.d(TAG, "onSelectedFileChange -> ${mSelectedFiles.size} changes")
        setOptionState(mSelectedFiles.size)
    }

    private fun setOptionState(cnt: Int) {
        when (cnt) {
            0 -> {
                mOptionView?.enableOption(NULL, false)
                mOptionView?.enableOption(CREATE, true)
            }
            1 -> {
                setOptionState(2)
                mOptionView?.enableOption(RENAME, true)
            }
            else -> {
                mOptionView?.enableOption(NULL, false)
                mOptionView?.enableOption(CUT, true)
                mOptionView?.enableOption(COPY, true)
                mOptionView?.enableOption(DELETE, true)
            }
        }
    }
}