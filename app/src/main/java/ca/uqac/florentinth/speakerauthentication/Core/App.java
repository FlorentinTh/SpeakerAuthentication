package ca.uqac.florentinth.speakerauthentication.Core;

import android.app.Application;
import android.content.Context;

/**
 * Created by FlorentinTh on 11/13/2015.
 */
public class App extends Application {
    private static Application application;

    public static Application getApp() {
        return application;
    }

    public static Context getAppContext() {
        return getApp().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
