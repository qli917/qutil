package com.q.util;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SilentInstallManager {
    private final static SynchronousQueue<Intent> mInstallResults = new
            SynchronousQueue<>();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static synchronized boolean silentInstallApk(Context context,
                                                        final String apkFilePath) {
        boolean ret = false;
        File apkFile = new File(apkFilePath);
        PackageInstaller installer = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams sessionParams = new
                PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        sessionParams.setSize(apkFile.length());
        int sessionId = createSession(installer, sessionParams);
        if (sessionId != -1) {
            boolean isCopyOk = copyInstallApkFile(sessionId, installer, apkFilePath);
            if (isCopyOk) {
                boolean isInstalled = installPackage(context, sessionId,
                        installer,apkFilePath);
                if (isInstalled ){
                    ret = true;
                }
            }
        }
        return ret;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static int createSession(PackageInstaller installer,
                                     PackageInstaller.SessionParams sessionParams) {
        int sessionId = -1;
        try {
            sessionId = installer.createSession(sessionParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionId;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean copyInstallApkFile(int sessionId, PackageInstaller installer,
                                              String apkFilePath) {
        InputStream in = null;
        OutputStream out = null;
        PackageInstaller.Session session = null;
        boolean ret = false;
        try {
            File apkFile = new File(apkFilePath);
            session = installer.openSession(sessionId);
            out = session.openWrite("base.apk", 0, apkFile.length());
            in = new FileInputStream(apkFile);
            int count;
            byte[] buffer = new byte[65536];
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            session.fsync(out);
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            session.close();
        }
        return ret;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean installPackage(Context context, int sessionId,
                                          PackageInstaller installer, String filePath) {
        PackageInstaller.Session session = null;
        boolean ret = false;
        try {
            session = installer.openSession(sessionId);
            Intent intent = new Intent();
            intent.setAction("com.example.install");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(pendingIntent.getIntentSender());
            final Intent intentResult = mInstallResults.poll(60, TimeUnit.SECONDS);
            if (intentResult != null) {
                final int status = intentResult.getIntExtra(PackageInstaller.EXTRA_STATUS,
                        PackageInstaller.STATUS_FAILURE);
                if (status == PackageInstaller.STATUS_SUCCESS) {
                    ret = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ret;
    }

}
