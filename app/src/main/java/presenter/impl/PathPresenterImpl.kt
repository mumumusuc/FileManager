package presenter.impl

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import com.google.common.base.Splitter
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.PathChangeEvent
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.utils.FileUtils
import com.mumu.filebrowser.views.IPathView
import presenter.IPathPresenter
import presenter.IPresenter
import java.io.File

/**
 * Created by leonardo on 17-11-25.
 */
class PathPresenterImpl(context: Context) : IPathPresenter, IPresenter {
    private val mContext = context
    private val SPAN_IMG = "img"
    private val mPath: MutableList<String> = arrayListOf()
    private val mSSB: SpannableStringBuilder = SpannableStringBuilder();
    private var mPathView: IPathView? = null
    private val mModel: IPathModel = PathModel

    init {
        EventBus.getInstance().register(this)
    }

    override fun onPathClick(path: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <IPathView> bindView(view: IPathView?) {
        mPathView = if (view == null) view else view as com.mumu.filebrowser.views.IPathView
    }

    private fun addClickablePart(str: String): SpannableStringBuilder? {
        mSSB.clear()
        mSSB.clearSpans()
        val alias = mModel.currentCategory
        val subs = FileUtils.getNavigationPath(alias)
        val str = str.substring(subs!!.length, str.length)
        mPath.clear()
        mPath.addAll(Splitter.on(File.separatorChar).omitEmptyStrings().splitToList(str))
        val aliasName = mContext.resources.getString(FileUtils.getNavigationName(alias)!!)
        mSSB.append(aliasName)
        mSSB.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        mModel.setPath(alias, subs,true)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = mContext.resources.getColor(R.color.pathArrowColor, null)
                        ds.isUnderlineText = false
                    }
                }, 0, aliasName.length, 0)
        var i = 0
        val size = mPath.size
        while (i < size) {
            val start = mSSB.length
            val part = mPath.get(i)
            mSSB.append(SPAN_IMG).append(part)
            val arrow = CenterImageSpan(mContext, R.drawable.ic_path_arrow, CenterImageSpan.ALIGN_CENTER)
            mSSB.setSpan(arrow, start, start + SPAN_IMG.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            val sub = mPath.subList(0, i + 1)
            mSSB.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val target = buildFullPath(sub)
                            //Toast.makeText(mContext, target, Toast.LENGTH_SHORT).show()
                            mModel.setPath(alias, target!!,true)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = mContext.resources.getColor(R.color.pathArrowColor, null)
                            ds.isUnderlineText = false
                        }
                    }, start + SPAN_IMG.length, start + SPAN_IMG.length + part.length, 0
            )
            i++
        }
        return mSSB
    }

    private fun buildFullPath(pathlist: List<String>): String? {
        if (pathlist.isEmpty()) {
            return null
        }
        mSSB.clear()
        mSSB.append(FileUtils.getNavigationPath(mModel.currentCategory))
        for (str in pathlist) {
            mSSB.append(File.separatorChar).append(str)
        }
        return mSSB.toString()
    }

    internal class CenterImageSpan(context: Context, resourceId: Int, verticalAlignment: Int) : ImageSpan(context, resourceId, verticalAlignment) {
        override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
            if (mVerticalAlignment == DynamicDrawableSpan.ALIGN_BASELINE || mVerticalAlignment == DynamicDrawableSpan.ALIGN_BOTTOM) {
                super.draw(canvas, text, start, end, x, top, y, bottom, paint)
            } else {
                val b = drawable
                canvas.save()
                val fm = paint.fontMetricsInt
                val transY = y + (fm.descent + fm.ascent) / 2 - b.bounds.bottom / 2
                canvas.translate(x, transY.toFloat())
                b.draw(canvas)
                canvas.restore()
            }
        }

        companion object {
            val ALIGN_CENTER = 2
        }
    }

    @Subscribe
    fun onPathChangeEvent(event: PathChangeEvent) {
        mPathView?.showPath(addClickablePart(mModel.currentPath) as CharSequence)
    }
}