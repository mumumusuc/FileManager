package com.mumu.filebrowser.views;

import com.mumu.filebrowser.model.IPathModel;

/**
 * Created by leonardo on 17-11-24.
 */

public interface INavigationView {
    void select(@IPathModel.Category int category);
}
