package com.mumu.filebrowser.views.impl

import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.IFileOption
import java.util.zip.Inflater

/**
 * Created by leonardo on 17-11-22.
 */

class FileOptionImpl constructor(view: View) : IFileOption, View.OnClickListener {

    val mContext = view.context
    val mCopy: TextView = view.findViewById(R.id.opt_copy)
    val mCut: TextView = view.findViewById(R.id.opt_cut)
    val mPaste: TextView = view.findViewById(R.id.opt_paste)
    val mRename: TextView = view.findViewById(R.id.opt_rename)
    val mCreate: TextView = view.findViewById(R.id.opt_create)
    val mDelete: TextView = view.findViewById(R.id.opt_delete)
    var mDialog: AlertDialog? = null
    val mDialogView: View = LayoutInflater.from(mContext).inflate(R.layout.option_dialog, null, false)

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
                if (mDialog?.isShowing ?: false)
                    mDialog?.dismiss()
                return
            }
        }
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
                mDialog?.findViewById<EditText>(R.id.editor)?.hint = mContext.getString(R.string.opt_msg_create)
                mDialog?.findViewById<View>(R.id.selector)?.visibility = VISIBLE
                mDialog?.setTitle(R.string.opt_name_create)
            }
            mDelete -> {
                mDialog?.setTitle(R.string.opt_name_delete)
                mDialog?.setMessage(mContext.getString(R.string.opt_msg_delete))
            }
            mRename -> {
                mDialog?.setTitle(R.string.opt_name_rename)
                mDialog?.findViewById<View>(R.id.selector)?.visibility = GONE
                mDialog?.findViewById<EditText>(R.id.editor)?.hint = mContext.getString(R.string.opt_msg_rename)
            }
            mPaste -> {
                mDialog?.setTitle(R.string.opt_name_paste)
                mDialog?.setMessage(mContext.getString(R.string.opt_msg_paste))
            }
        }
        if (!mDialog!!.isShowing) {
            mDialog!!.show()
            mDialog!!.getButton(BUTTON_POSITIVE).setOnClickListener(this)
        }
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