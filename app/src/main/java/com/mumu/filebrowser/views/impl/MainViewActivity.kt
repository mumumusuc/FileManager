package com.mumu.filebrowser.views.impl

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.mumu.filebrowser.Config
import com.mumu.filebrowser.R
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.views.*
import com.mumu.filebrowser.presenter.IMainPresenter
import com.mumu.filebrowser.presenter.IPresenter
import com.mumu.filebrowser.presenter.impl.MainPresenterImpl

class MainViewActivity : AppCompatActivity(), IMainView {
    companion object {
        val sMainPresenter: IMainPresenter = MainPresenterImpl()
    }

    val mPathModel = PathModel

    override fun getContext(): Context = baseContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (sMainPresenter as IPresenter).bindView(this)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBar?.title = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        (sMainPresenter as IPresenter).bindView(null)
    }

    override fun onBackPressed() {
        if (Config.backControlPath()) {
            if (mPathModel.enterParent()) {
                return
            }
        }
        super.onBackPressed()
    }
}