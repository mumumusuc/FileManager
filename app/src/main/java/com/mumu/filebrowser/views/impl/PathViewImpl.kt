package com.mumu.filebrowser.views.impl

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import com.mumu.filebrowser.views.IPathView
import presenter.IPathPresenter
import presenter.IPresenter
import presenter.impl.PathPresenterImpl

/**
 * Created by leonardo on 17-11-25.
 */
class PathViewImpl : TextView, IPathView {
    companion object {
        private var sPathPresenter: IPathPresenter? = null
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
        if (sPathPresenter == null) {
            sPathPresenter = PathPresenterImpl(context.applicationContext)
        }
        movementMethod = LinkMovementMethod.getInstance();
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (sPathPresenter as IPresenter).bindView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (sPathPresenter as IPresenter).bindView(null)

    }

    override fun showPath(path: CharSequence) {
        setText(path, BufferType.SPANNABLE)
    }

}