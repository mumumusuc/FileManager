package presenter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IOptionView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by leonardo on 17-11-27.
 */

public interface IOptionPresenter {

    void onCopy();

    void onMove();

    void onPaste();

    void onRename();

    void onDelete();

    void onCreate();

    void onCancel();

    void onConfirm(String content, int select);
}
