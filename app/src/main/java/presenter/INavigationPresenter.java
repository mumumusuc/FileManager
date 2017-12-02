package presenter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.mumu.filebrowser.model.IPathModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by leonardo on 17-11-24.
 */

public interface INavigationPresenter {
    boolean onNavigation(@IPathModel.Category int category);
}
