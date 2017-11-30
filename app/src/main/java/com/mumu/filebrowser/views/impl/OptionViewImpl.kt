package com.mumu.filebrowser.views.impl

import android.content.Context
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.mumu.filebrowser.R
import com.mumu.filebrowser.views.IOptionView
import com.mumu.filebrowser.views.IOptionView.*
import presenter.IOptionPresenter
import presenter.IPresenter
import presenter.impl.OptionPresenterImpl

/**
 * Created by leonardo on 17-11-22.
 */

class OptionViewImpl : android.support.v7.widget.GridLayout, IOptionView, View.OnClickListener {
    companion object {
        private val sPresenter: IOptionPresenter = OptionPresenterImpl()
    }

    var mCopy: TextView? = null
    var mCut: TextView? = null
    var mPaste: TextView? = null
    var mRename: TextView? = null
    var mCreate: TextView? = null
    var mDelete: TextView? = null
    var mDialog: AlertDialog? = null
    var mDialogView: View? = null
    var mEditor: EditText? = null
    var mSelector: RadioGroup? = null
    var mMessage: TextView? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun onFinishInflate() {
        super.onFinishInflate()
        mCopy = findViewById(R.id.opt_copy)
        mCut = findViewById(R.id.opt_cut)
        mPaste = findViewById(R.id.opt_paste)
        mRename = findViewById(R.id.opt_rename)
        mCreate = findViewById(R.id.opt_create)
        mDelete = findViewById(R.id.opt_delete)
        mCopy?.setOnClickListener(this)
        mCut?.setOnClickListener(this)
        mPaste?.setOnClickListener(this)
        mRename?.setOnClickListener(this)
        mCreate?.setOnClickListener(this)
        mDelete?.setOnClickListener(this)
        mDialogView = LayoutInflater.from(context).inflate(R.layout.option_dialog, null, false)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (sPresenter as IPresenter).bindView(this)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (sPresenter as IPresenter).bindView(null)
    }

    override fun enableOption(option: Int, enable: Boolean) {
        when (option) {
            NULL -> {
                mCreate?.isEnabled = enable
                mCopy?.isEnabled = enable
                mCut?.isEnabled = enable
                mRename?.isEnabled = enable
                mDelete?.isEnabled = enable
                mPaste?.isEnabled = enable
            }
            CREATE -> {
                mCreate?.isEnabled = enable
            }
            COPY -> {
                mCopy?.isEnabled = enable
            }
            CUT -> {
                mCut?.isEnabled = enable
            }
            RENAME -> {
                mRename?.isEnabled = enable
            }
            DELETE -> {
                mDelete?.isEnabled = enable
            }
            PASTE -> {
                mPaste?.isEnabled = enable
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.opt_copy -> {
                sPresenter.onCopy()
            }
            R.id.opt_cut -> {
                sPresenter.onMove()
            }
            R.id.opt_paste -> {
                sPresenter.onPaste()
            }
            R.id.opt_rename -> {
                sPresenter.onRename()
            }
            R.id.opt_create -> {
                sPresenter.onCreate()
            }
            R.id.opt_delete -> {
                sPresenter.onDelete()
            }
            mDialog?.getButton(BUTTON_POSITIVE)?.id -> {
                val name = mEditor?.text?.toString()
                val check = if (mSelector?.checkedRadioButtonId == R.id.opt_create_folder) 0 else 1
                sPresenter.onConfirm(name, check)
            }
            mDialog?.getButton(BUTTON_NEGATIVE)?.id -> {
                sPresenter.onCancel()
            }
        }
    }

    override fun showDialog(title: String, msg: String?, hint: String?, useRadio: Boolean) {
        if (mDialog == null) {
            createDialog()
        }
        if (!title.isEmpty()) {
            mDialog?.setTitle(title)
        }
        showDialogMessage(msg, msg != null)
        showDialogSelector(useRadio)
        showDialogEditor(hint, hint != null)
        if (!mDialog!!.isShowing) {
            mDialog!!.show()
            mDialog!!.getButton(BUTTON_POSITIVE).setOnClickListener(this)
        }
    }

    override fun dismissDialog() {
        if (mDialog?.isShowing ?: false) {
            mDialog?.dismiss()
        }
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setView(mDialogView)
        mEditor = mDialogView?.findViewById(R.id.dialog_editor)
        mSelector = mDialogView?.findViewById(R.id.dialog_selector)
        mMessage = mDialogView?.findViewById(R.id.dialog_message)
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        mDialog = builder.create()
    }

    private fun showDialogMessage(msg: String?, show: Boolean) {
        mMessage?.visibility = if (show) VISIBLE else GONE
        if (!msg.isNullOrBlank()) {
            mMessage?.text = msg
        }
    }

    private fun showDialogSelector(show: Boolean) {
        mSelector?.visibility = if (show) VISIBLE else GONE
    }

    private fun showDialogEditor(hint: String?, show: Boolean) {
        mEditor?.visibility = if (show) VISIBLE else GONE
        if (!hint.isNullOrBlank()) {
            mEditor?.hint = hint
        }
    }
}