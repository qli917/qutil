package com.q.util;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallAppUtil {

    /**
     * 安装应用
     */
    public static void installApk(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        PackageInfo packageInfo = Utils.getApp().getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return;
        }
        String packageName = packageInfo.packageName;
        PackageInstaller packageInstaller = Utils.getApp().getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        sessionParams.setSize(file.length());
        PackageInstaller.Session session = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            int sessionId = packageInstaller.createSession(sessionParams);
            session = packageInstaller.openSession(sessionId);
            outputStream = session.openWrite(packageName, 0, file.length());
            inputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            session.fsync(outputStream);
            inputStream.close();
            inputStream = null;
            outputStream.close();
            outputStream = null;
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Utils.getApp(), 0, intent, 0);
            session.commit(pendingIntent.getIntentSender());
            session.close();
            session = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
