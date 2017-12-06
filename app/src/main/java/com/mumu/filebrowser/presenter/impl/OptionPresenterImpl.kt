package com.mumu.filebrowser.presenter.impl

import android.os.AsyncTask
import android.util.Log
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.utils.Utils
import com.mumu.filebrowser.utils.OptionUtils
import com.mumu.filebrowser.utils.OptionUtils.CREATE_TYPE_FILE
import com.mumu.filebrowser.utils.OptionUtils.CREATE_TYPE_FOLDER
import com.mumu.filebrowser.utils.OptionUtils.NO_ERROR
import com.mumu.filebrowser.views.IOptionView
import com.mumu.filebrowser.views.IOptionView.*
import com.mumu.filebrowser.presenter.IOptionPresenter
import com.mumu.filebrowser.presenter.IPresenter
import java.io.File

class OptionPresenterImpl : IOptionPresenter, IPresenter {
    private val TAG = OptionPresenterImpl::class.java.simpleName

    private var mOptionView: IOptionView? = null
    private val mPathModel: IPathModel = PathModel
    private var mState: Int = NULL
    private val mSelectedFiles = mutableSetOf<String>()

    init {
        EventBus.getInstance().register(this)
    }

    override fun <IOptionView> bindView(view: IOptionView?) {
        mOptionView = if (view == null) null else view as com.mumu.filebrowser.views.IOptionView
        if (view != null) {
            setOptionState(mSelectedFiles.size)
        }
    }

    override fun onCopy() {
        mState = COPY
        mOptionView?.enableOption(NULL, false)
        mOptionView?.enableOption(CUT, true)
        mOptionView?.enableOption(PASTE, true)
        mOptionView?.enableOption(DELETE, true)
        EventBus.getInstance().post(SelectedEvent(null))
    }

    override fun onMove() {
        mState = CUT
        mOptionView?.enableOption(NULL, false)
        mOptionView?.enableOption(COPY, true)
        mOptionView?.enableOption(PASTE, true)
        mOptionView?.enableOption(DELETE, true)
        EventBus.getInstance().post(SelectedEvent(null))
    }

    override fun onRename() {
        mState = RENAME
        val res = mOptionView?.resources
        mOptionView?.showDialog(
                res?.getString(R.string.opt_name_rename),
                null,
                res?.getString(R.string.opt_msg_rename)
        )
    }

    override fun onDelete() {
        mState = DELETE
        val res = mOptionView?.resources
        mOptionView?.showDialog(
                res?.getString(R.string.opt_name_delete),
                res?.getString(R.string.opt_msg_delete),
                null
        )
    }

    override fun onCreate() {
        mState = CREATE
        val res = mOptionView?.resources
        mOptionView?.showDialog(
                res?.getString(R.string.opt_name_create),
                null,
                res?.getString(R.string.opt_msg_create)
        )
    }

    override fun onPaste() {
        val res = mOptionView?.resources
        mOptionView?.showDialog(
                res?.getString(R.string.opt_name_paste),
                res?.getString(R.string.opt_msg_paste),
                null
        )
    }

    override fun onCancel() {
        mState = NULL
        mOptionView?.dismissDialog()
    }

    override fun onConfirm(content: String?) {
        val res = mOptionView?.resources
        when (mState) {
            CREATE -> {
                val legal: Boolean = Utils.checkFileName(content)
                if (legal) {
                    val state = OptionUtils.create(buildFullPath(content!!), CREATE_TYPE_FOLDER
                    )
                    Log.i("OptionPresenterImpl", "state = $state")
                    if (state == NO_ERROR) {
                        onCancel()
                    } else {
                        mOptionView?.showDialog("", res?.getString(R.string.opt_error_file_exist), "")
                    }
                } else {
                    Log.i(TAG, "illegal name,show warning")
                    mOptionView?.showDialog("", res?.getString(R.string.opt_error_bad_name), "")
                }
            }
            DELETE -> {
                //var state: Int
                /*mSelectedFiles.map {
                    state = OptionUtils.delete(it)
                    Log.i("OptionPresenterImpl", "delete = $state")
                }*/
                BackgroundProcess().execute(object : Processor<String> {
                    override fun process(arg: String): Boolean {
                        val state = OptionUtils.delete(arg)
                        Log.i(TAG, "deleting = ${arg}")
                        return state == NO_ERROR
                    }
                })
                //onCancel()
            }
            RENAME -> {
                val legal: Boolean = Utils.checkFileName(content)
                if (legal) {
                    val state = OptionUtils.rename(
                            mSelectedFiles.toTypedArray()[0],
                            buildFullPath(content!!))
                    Log.w("OptionPresenterImpl", "rename = $state")
                    onCancel()
                } else {
                    mOptionView?.showDialog("", res?.getString(R.string.opt_error_bad_name), "")
                }
            }
            COPY -> {
                val targetPath = mPathModel.path
                /*mSelectedFiles.forEach {
                    val state = OptionUtils.copy(it, targetPath)
                    Log.w("OptionPresenterImpl", "copy = $it, state = $state")
                }*/
                BackgroundProcess().execute(object : Processor<String> {
                    override fun process(arg: String): Boolean {
                        val state = OptionUtils.copy(arg, targetPath)
                        Log.i(TAG, "copying = ${arg}")
                        return state == NO_ERROR
                    }
                })
                //onCancel()
            }
            CUT -> {
                val targetPath = mPathModel.path
                /*mSelectedFiles.forEach {
                        val state = OptionUtils.move(it, targetPath)
                        Log.w("OptionPresenterImpl", "move = $it, state = $state")
                    }*/
                BackgroundProcess().execute(object : Processor<String> {
                    override fun process(arg: String): Boolean {
                        val state = OptionUtils.move(arg, targetPath)
                        Log.i(TAG, "moving = ${arg}")
                        return state == NO_ERROR
                    }
                })
                // onCancel()
            }
        }

    }

    private fun buildFullPath(name: String): String = mPathModel.path + File.separatorChar + name

    @Subscribe
    fun onSelectedFileChange(event: SelectedEvent) {
        if (mState == COPY || mState == CUT) {
            return
        }
        mSelectedFiles.clear()
        if (event.files != null) {
            mSelectedFiles.addAll(event.files)
        }
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

    private interface Processor<T> {
        fun process(arg: T): Boolean
    }

    private inner class BackgroundProcess : AsyncTask<Processor<String>, Float, Pair<Int, Int>>() {
        override fun doInBackground(vararg params: Processor<String>?): Pair<Int, Int> {
            if (params?.isEmpty() || mSelectedFiles?.isEmpty()) {
                return Pair(0, 0)
            }
            var sucessedCnt = 0
            val total = mSelectedFiles.size
            mSelectedFiles.forEachIndexed { index, s ->
                run {
                    val result = params[0]?.process(s) ?: false
                    Log.d(TAG, "processing ${s} ... ${if (result) "success" else "failed"}")
                    if (result) {
                        sucessedCnt++
                        publishProgress(sucessedCnt / total.toFloat())
                    }
                }
            }
            return Pair(sucessedCnt, total)
        }

        override fun onProgressUpdate(vararg values: Float?) {
            //TODO : show progress
            Log.d(TAG, "completing ${(values?.get(0) ?: 0f) * 100}%")
            mOptionView?.showProgress(values?.get(0) ?: 0f)
        }

        override fun onPostExecute(result: Pair<Int, Int>?) {
            Log.d(TAG, "onResout -> {${result?.first},${result?.second}}")
            mOptionView?.dismissProgress()
        }
    }

}