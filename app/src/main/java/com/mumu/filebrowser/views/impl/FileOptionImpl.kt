package com.mumu.filebrowser.views.impl

import android.content.DialogInterface.BUTTON_POSITIVE
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.utils.FileUtils
import com.mumu.filebrowser.utils.OptionUtils
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.IOptionView

/**
 * Created by leonardo on 17-11-22.
 */

class FileOptionImpl constructor(view: View) : IOptionView, View.OnClickListener {

    val mContext = view.context
    val mCopy: TextView = view.findViewById(R.id.opt_copy)
    val mCut: TextView = view.findViewById(R.id.opt_cut)
    val mPaste: TextView = view.findViewById(R.id.opt_paste)
    val mRename: TextView = view.findViewById(R.id.opt_rename)
    val mCreate: TextView = view.findViewById(R.id.opt_create)
    val mDelete: TextView = view.findViewById(R.id.opt_delete)
    var mDialog: AlertDialog? = null
    val mDialogView: View = LayoutInflater.from(mContext).inflate(R.layout.option_dialog, null, false)
    var mEditor: EditText? = null
    var mSelector: RadioGroup? = null
    var mMessage: TextView? = null

    init {
        showOptionDepandsOnSelected(0)
        mCopy.setOnClickListener(this)
        mCut.setOnClickListener(this)
        mPaste.setOnClickListener(this)
        mRename.setOnClickListener(this)
        mCreate.setOnClickListener(this)
        mDelete.setOnClickListener(this)
        EventBus.getInstance().register(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.opt_copy -> {
            }
            R.id.opt_cut -> {
            }
            R.id.opt_paste -> {
                showDialog(v)
            }
            R.id.opt_rename -> {
                showDialog(v)
            }
            R.id.opt_create -> {
                showDialog(v)
            }
            R.id.opt_delete -> {
                showDialog(v)
            }
            mDialog?.getButton(BUTTON_POSITIVE)?.id -> {
                val name = mEditor?.text?.toString()
                if (onDialogConfirm(name) && mDialog?.isShowing ?: false) {
                    mDialog?.dismiss()
                }
                return
            }
        }
    }

    private fun onDialogConfirm(name: String?): Boolean {
        val legal: Boolean = FileUtils.checkFileName(name)
        if (legal) {
            OptionUtils.create(name,
                    when (mSelector?.checkedRadioButtonId) {
                        R.id.opt_create_folder -> {
                            OptionUtils.CREATE_TYPE_FOLDER
                        }
                        else -> {
                            OptionUtils.CREATE_TYPE_FILE
                        }
                    }
            )
        } else {
            showDialogMessage("请检查文件名是否正确", true)
        }
        return legal
    }

    @Subscribe
    fun onSelectedEvent(event: SelectedEvent) {
        val files = event.files
        var size = files?.size ?: 0
        if (size == 1 && !(files[0] as FileWrapper).isSelected) {
            size = 0
        }
        showOptionDepandsOnSelected(size)
    }

    private fun showOptionDepandsOnSelected(size: Int) {
        when (size) {
            0 -> {
                enableOption(mCreate, true)
                enableOption(mCopy, false);
                enableOption(mCut, false);
                enableOption(mPaste, false);
                enableOption(mRename, false);
                enableOption(mDelete, false);
            }
            1 -> {
                enableOption(mCreate, false)
                enableOption(mCopy, true);
                enableOption(mCut, true);
                enableOption(mPaste, false);
                enableOption(mRename, true);
                enableOption(mDelete, true);
            }
            else -> {
                enableOption(mCreate, false)
                enableOption(mCopy, true);
                enableOption(mCut, true);
                enableOption(mPaste, false);
                enableOption(mRename, false);
                enableOption(mDelete, true);
            }
        }
    }

    private fun enableOption(view: TextView, enable: Boolean) {
        view.isEnabled = enable
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setView(mDialogView)
        mEditor = mDialogView.findViewById(R.id.dialog_editor)
        mSelector = mDialogView.findViewById(R.id.dialog_selector)
        mMessage = mDialogView.findViewById(R.id.dialog_message)
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        mDialog = builder.create()
    }

    private fun showDialog(view: View) {
        if (mDialog == null) {
            createDialog()
        }
        when (view) {
            mCreate -> {
                mDialog?.setTitle(R.string.opt_name_create)
                showDialogMessage(null, false)
                showDialogSelector(true)
                showDialogEditor(mContext.getString(R.string.opt_msg_create), true)
            }
            mDelete -> {
                mDialog?.setTitle(R.string.opt_name_delete)
                showDialogMessage(mContext.getString(R.string.opt_msg_delete), true)
                showDialogSelector(false)
                showDialogEditor(null, false)
            }
            mRename -> {
                mDialog?.setTitle(R.string.opt_name_rename)
                showDialogMessage(mContext.getString(R.string.opt_msg_rename), true)
                showDialogSelector(false)
                showDialogEditor(null, false)
            }
            mPaste -> {
                mDialog?.setTitle(R.string.opt_name_paste)
                showDialogMessage(mContext.getString(R.string.opt_msg_paste), true)
                showDialogSelector(false)
                showDialogEditor(null, false)
            }
        }
        if (!mDialog!!.isShowing) {
            mDialog!!.show()
            mDialog!!.getButton(BUTTON_POSITIVE).setOnClickListener(this)
        }
    }

    private fun showDialogMessage(msg: String?, show: Boolean) {
        mMessage?.visibility = if (show) VISIBLE else GONE
        mMessage?.text = msg
    }

    private fun showDialogSelector(show: Boolean) {
        mSelector?.visibility = if (show) VISIBLE else GONE
    }

    private fun showDialogEditor(hint: String?, show: Boolean) {
        mEditor?.visibility = if (show) VISIBLE else GONE
        mEditor?.hint = hint
    }

    override fun copy(from: String, to: String) {
    }

    override fun move(from: String, to: String) {
    }

    override fun rename(from: String, to: String) {
    }

    override fun delete(file: IFile) {
    }

    override fun delete(src: String) {
    }

    override fun create(name: String, type: Int) {
    }
}