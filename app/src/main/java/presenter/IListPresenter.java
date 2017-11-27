package presenter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mumu.filebrowser.model.IModel;
import com.mumu.filebrowser.views.IListView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by leonardo on 17-11-24.
 */

public interface IListPresenter<T> {
    int MODE_NORMAL_VIEW = 10;
    int MODE_MULTI_SELECT = 11;

    @IntDef({MODE_NORMAL_VIEW, MODE_MULTI_SELECT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewMode {
    }

    @NonNull
    List<T> getList();

    void onItemClick(@NonNull T item);

    void onItemLongClick(@NonNull T item);

    @IModel.LayoutStyle
    int getCurrentLayoutStyle();

    @ViewMode
    int getCurrentViewMode();

}