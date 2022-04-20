package com.liucj.lib_common;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;

public class AppGlobals {
    private static Application mApplication;

    /**
     * 通过反射得到Application 对象
     *
     * @return
     */
    public static Application getApplication() {
        if (mApplication == null) {
            try {
                mApplication = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mApplication;
    }
}
