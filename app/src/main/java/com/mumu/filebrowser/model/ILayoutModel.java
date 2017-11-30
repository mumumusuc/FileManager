package com.mumu.filebrowser.model;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by leonardo on 17-11-30.
 */

public interface ILayoutModel {

    int LAYOUT_STYLE_LIST = 0;
    int LAYOUT_STYLE_GRID = 1;

    @IntDef({LAYOUT_STYLE_LIST, LAYOUT_STYLE_GRID})
    @Retention(RetentionPolicy.SOURCE)
    @interface LayoutStyle {
    }

    void setLayoutStyle(@LayoutStyle int layout);

    @LayoutStyle
    int getLayoutStyle();
}
