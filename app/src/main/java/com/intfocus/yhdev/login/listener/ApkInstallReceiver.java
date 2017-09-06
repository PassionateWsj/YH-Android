package com.intfocus.yhdev.login.listener;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.intfocus.yhdev.R;

import java.io.File;

import static android.content.Intent.ACTION_VIEW;

/**
 * ****************************************************
 * author: jameswong
 * created on: 17/09/05 下午0:07
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class ApkInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            installApk(context);
        }
    }

    /**
     * 安装apk
     */
    private void installApk(Context context) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getResources().getString(R.string.app_name) + ".apk.download");
        if (file.exists()) {
            file.renameTo(new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getResources().getString(R.string.app_name) + ".apk"));
        }
        // 获取存储ID
        Uri downloadFileUri = Uri.fromFile(new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getResources().getString(R.string.app_name) + ".apk"));
        if (downloadFileUri != null) {
            Intent install = new Intent(ACTION_VIEW);
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } else {
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
        }

    }
}