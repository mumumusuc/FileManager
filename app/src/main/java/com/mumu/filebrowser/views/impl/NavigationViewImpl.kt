package com.mumu.filebrowser.views.impl

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.NavigationView
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mumu.filebrowser.R
import com.mumu.filebrowser.model.IPathModel.*
import com.mumu.filebrowser.views.INavigationView
import presenter.INavigationPresenter
import presenter.IPresenter
import presenter.impl.NavigationPresenterImpl

/**
 * Created by leonardo on 17-11-24.
 */
class NavigationViewImpl : NavigationView, INavigationView, NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private val sNavigationPresenter: INavigationPresenter = NavigationPresenterImpl()
    }

    private val mMap: BiMap<Int, Int> = HashBiMap.create()

    init {
        mMap.put(R.id.nav_camera, CAMERA)
        mMap.put(R.id.nav_music, MUSIC)
        mMap.put(R.id.nav_picture, PICTURE)
        mMap.put(R.id.nav_video, VIDEO)
        mMap.put(R.id.nav_document, DOCUMENT)
        mMap.put(R.id.nav_download, DOWNLOAD)
        mMap.put(R.id.nav_storage, STORAGE)
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setNavigationItemSelectedListener(this);
    }

    @SuppressLint("RestrictedApi")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (sNavigationPresenter as IPresenter).bindView(null)
    }

    @SuppressLint("RestrictedApi")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (sNavigationPresenter as IPresenter).bindView(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isCheckable = true
        //menu.
        val category = mMap.get(item.itemId)
        return sNavigationPresenter.onNavigation(category!!)
    }

    override fun select(category: Int) {
        menu.findItem(mMap.inverse().get(category) ?: -1).isChecked = true
        onNavigationItemSelected(menu.findItem(mMap.inverse().get(category) ?: -1))
    }

}
