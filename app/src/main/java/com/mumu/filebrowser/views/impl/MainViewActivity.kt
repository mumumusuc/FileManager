package com.mumu.filebrowser.views.impl

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.mumu.filebrowser.R
import com.mumu.filebrowser.views.*
import presenter.IMainPresenter
import presenter.IPresenter
import presenter.impl.MainPresenterImpl

/**
 * Created by leonardo on 17-11-24.
 */
class MainViewActivity : AppCompatActivity(), IMainView {
    companion object {
        val sMainPresenter: IMainPresenter = MainPresenterImpl()
    }

    var mToolView: IToolView? = null

    override fun getContext(): Context = baseContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (sMainPresenter as IPresenter).bindView(this)
        setSupportActionBar(findViewById(R.id.toolbar))
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS),
                111)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBar?.title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mToolView = ToolViewImpl(menuInflater, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        (sMainPresenter as IPresenter).bindView(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return mToolView?.onActionItemSelected(item) ?: false
    }

}