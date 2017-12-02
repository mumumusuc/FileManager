package presenter.impl


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.google.common.eventbus.Subscribe
import com.google.common.io.Files
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.OpenEvent
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.utils.FileUtils
import com.mumu.filebrowser.views.IMainView
import presenter.IMainPresenter
import presenter.IPresenter
import java.io.File

/**
 * Created by leonardo on 17-11-24.
 */
class MainPresenterImpl() : IMainPresenter, IPresenter {
    var mMainView: IMainView? = null

    init {
        EventBus.getInstance().register(this)
    }

    override fun <IMainView> bindView(view: IMainView?) {
        mMainView = if (view == null) null else view as com.mumu.filebrowser.views.IMainView
    }

    @Subscribe
    fun onOpenEvent(event: OpenEvent) {
        if (mMainView == null) {
            return
        }
        val path = event.path
        val type = FileUtils.getMIMEType(Files.getFileExtension(path)) ?: return
        val intent = Intent()
        intent.action = android.content.Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                    mMainView!!.context,
                    mMainView!!.context.getBasePackageName() + ".fileProvider",
                    File(path))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(File(path))
        }
        intent.setDataAndType(uri, type)
        mMainView!!.context.startActivity(intent)
    }
}