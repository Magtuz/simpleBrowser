package com.magtuz.simplebrowser.di;

import android.content.Context;

import com.magtuz.simplebrowser.di.modules.ContextModule;
import com.magtuz.simplebrowser.ui.fragments.MainFragment;
import com.magtuz.simplebrowser.ui.fragments.WebPageFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by magtuz on 9/28/2017.
 */

@Singleton
@Component(modules = {ContextModule.class})
public interface AppComponent {
    Context getContext();

    void inject(MainFragment fragment);
    void inject(WebPageFragment fragment);
}
