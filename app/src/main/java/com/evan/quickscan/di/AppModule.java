package com.evan.quickscan.di;

import android.app.Application;

import com.evan.quickscan.BaseApplication;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    BaseApplication provideBaseApplication(Application application){
        return (BaseApplication) application;
    }
}
