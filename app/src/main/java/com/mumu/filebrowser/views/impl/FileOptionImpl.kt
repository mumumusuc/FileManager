package com.mumu.filebrowser.views.impl

import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.IFileOption

/**
 * Created by leonardo on 17-11-22.
 */

class FileOptionImpl constructor(view: View) : IFileOption, View.OnClickListener {


    val mCopy: TextView = view.findViewById(R.id.opt_copy)
    val mCut: TextView = view.findViewById(R.id.opt_cut)
    val mPaste: TextView = view.findViewById(R.id.opt_paste)
    val mRename: TextView = view.findViewById(R.id.opt_rename)
    val mCreate: TextView = view.findViewById(R.id.opt_create)
    val mDelete: TextView = view.findViewById(R.id.opt_delete)

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
        Toast.makeText(v!!.context, "copy", Toast.LENGTH_SHORT).show()
        when (v!!.id) {
            R.id.opt_copy -> {
            }
            R.id.opt_create -> {
            }
            R.id.opt_paste -> {
            }
            R.id.opt_rename -> {
            }
            R.id.opt_create -> {
            }
            R.id.opt_delete -> {
            }
        }
    }

    @Subscribe
    fun onSelectedEvent(event: SelectedEvent) {
        val files = event.files
        showOptionDepandsOnSelected(files?.size ?: 0)
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