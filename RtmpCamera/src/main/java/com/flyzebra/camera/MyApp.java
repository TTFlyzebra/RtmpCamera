/**
 * FileName: MyApp
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 11:02
 * Description:
 */
package com.flyzebra.camera;

import android.app.Application;

import com.flyzebra.utils.FlyLog;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlyLog.setTAG("ZEBRA-RTMP");
    }
}
