package com.mumu.filebrowser.views.impl

import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.TextView
import com.mumu.filebrowser.R
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.*
import presenter.IMainPresenter
import presenter.IPresenter
import presenter.impl.MainPresenterImpl

/**
 * Created by leonardo on 17-11-24.
 */
class MainViewActivity : AppCompatActivity(), IMainView {
    companion object {
        var sMainPresenter: IMainPresenter? = null
    }

    var mOverView: IOverview? = null
    var mToolView: IToolView? = null
    var mOptionView: IOptionView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (sMainPresenter == null) {
            sMainPresenter = MainPresenterImpl()
        }
        (sMainPresenter as IPresenter).bindView(this)
        setSupportActionBar(findViewById(R.id.toolbar))
        val overviewPanel = findViewById<View>(R.id.overview_panel)
        mOverView = OverviewImpl(overviewPanel)
        mOptionView = FileOptionImpl(overviewPanel)
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
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