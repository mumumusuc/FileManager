package presenter.impl

import android.util.Log
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.FocusedEvent
import com.mumu.filebrowser.eventbus.events.SelectedEvent
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.IOverview
import presenter.IOverviewPresenter
import presenter.IPresenter

/**
 * Created by leonardo on 17-11-27.
 */
class OverviewPresenterImpl : IOverviewPresenter, IPresenter {
    private var mOverview: IOverview? = null

    init {
        EventBus.getInstance().register(this)
    }

    override fun <IOverview> bindView(view: IOverview?) {
        mOverview = if (view == null) null else view as com.mumu.filebrowser.views.IOverview
    }

    @Subscribe
    fun onFocusedEvent(event: FocusedEvent) {
        val file = event.file
        if (file == null) {
            mOverview?.cleanDisplay()
        } else {
            mOverview?.showFocusedview(FileWrapper.gets(file))
        }
    }

    @Subscribe
    fun onSelectedFileChange(event: SelectedEvent) {
        val files = event.files
        if (files == null || files.isEmpty()) {
            mOverview?.cleanDisplay()
        } else {
            val target = arrayOfNulls<IFile>(files.size)
            files.forEachIndexed { index, value ->
                run {
                    Log.d("Overview", "onSelectedFileChange -> name = $value")
                    target[index] = FileWrapper(value)
                }
            }
            mOverview?.showSelectedView(target)
        }
    }
}
