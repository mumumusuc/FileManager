package com.mumu.filebrowser.presenter;

import com.mumu.filebrowser.model.IPathModel;

/**
 * Created by leonardo on 17-11-24.
 */

public interface INavigationPresenter {
    boolean onNavigation(@IPathModel.Category int category);
}
