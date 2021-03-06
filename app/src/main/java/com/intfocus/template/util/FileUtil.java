package com.intfocus.template.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;

import static com.intfocus.template.constant.Params.ASSETS;
import static com.intfocus.template.constant.Params.LOADING;
import static com.intfocus.template.constant.Params.USER_BEAN;
import static com.intfocus.template.util.K.K_USER_ID;

public class FileUtil {
    public static String basePath(Context context) {
        String basePath = storage_base(context);
        FileUtil.makeSureFolderExist(basePath);

        return basePath;
    }

    public static String userspace(Context context) {
        SharedPreferences mUserSP = context.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE);
        String spacePath = "";

        spacePath = String.format("%s/User-%s", FileUtil.basePath(context), mUserSP.getString(K_USER_ID, "0"));
        return spacePath;
    }

    /**
     * 传递目录名取得沙盒中的绝对路径(一级),不存在则创建，请慎用！
     *
     * @param dirName 目录名称，不存在则创建
     * @return 沙盒中的绝对路径
     */
    public static String dirPath(Context context, String dirName) {
        String pathName = String.format("%s/%s", FileUtil.userspace(context), dirName);
        FileUtil.makeSureFolderExist(pathName);

        return pathName;
    }

    public static String dirPath(Context context, String dirName, String fileName) {
        String pathName = FileUtil.dirPath(context, dirName);

        return String.format("%s/%s", pathName, fileName);
    }

    /**
     * 读取本地文件内容
     */
    public static String readFile(String pathName) {
        String string = null;
        File file = new File(pathName);
        if (file.exists()) {
            try {
                InputStream inputStream = new FileInputStream(new File(pathName));
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                string = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            string = "";
        }

        return string;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
            }
            LogUtil.d("response", "file is copy");
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    /**
     * 读取本地文件内容，并转化为json
     */
    public static JSONObject readConfigFile(String jsonPath) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (new File(jsonPath).exists()) {
                String string = FileUtil.readFile(jsonPath);
                jsonObject = new JSONObject(string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 字符串写入本地文件
     */
    public static void writeFile(String pathName, String content) throws IOException {
        File file = new File(pathName);
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        out.write(content.getBytes("utf-8"));
        out.close();
    }

    /**
     * 共享资源
     * 1. assets资源
     * 2. loading页面
     * 3. 登录缓存页面
     */
    public static String sharedPath(Context context) {
        String pathName = FileUtil.basePath(context) + "/" + K.K_SHARED_DIR_NAME;
        FileUtil.makeSureFolderExist(pathName);

        return pathName;
    }

    public static boolean makeSureFolderExist(String pathName) {
        File folder = new File(pathName);
        return folder.exists() && folder.isDirectory() || folder.mkdirs();
    }

    /**
     * 共享资源中的文件（夹）（忽略是否存在）
     */
    public static String sharedPath(Context context, String folderName) {
        if (!folderName.startsWith("/")) {
            folderName = String.format("/%s", folderName);
        }

        return String.format("%s%s", FileUtil.sharedPath(context), folderName);
    }

    /**
     * Generage MD5 value for ZIP file
     */
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte bytes : arrayBytes) {
            stringBuffer.append(Integer.toString((bytes & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    /**
     * algorithm can be "MD5", "SHA-1", "SHA-256"
     */
    private static String hashFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();
            inputStream.close();
            return convertByteArrayToHexString(hashedBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "hashFile - exception catched";
    }

    public static String MD5(File file) {
        return hashFile(file);
    }

    /**
     * 解压assets的zip压缩文件到指定目录
     *
     * @throws IOException
     */
    public static void unZip(InputStream inputStream, String outputDirectory, boolean isReWrite) throws IOException {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        //InputStream inputStream = getApplicationContext().getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
//        LogUtil.d("response", outputDirectory + File.separator + zipEntry.getName());
        // 使用1Mbuffer
        byte[] buffer = new byte[10 * 1024 * 1024];
        // 解压时字节计数
        int count;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    private static String MD5(InputStream inputStream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();
            return convertByteArrayToHexString(hashedBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "MD5 - exception catched";
    }

    /**
     * 检测sharedPath/{assets.zip, loading.zip} md5值与缓存文件中是否相等
     *
     * @param mContext 上下文
     * @param fileName 静态文件名称
     */
    public static void checkAssets(Context mContext, String fileName, boolean isInAssets) {
        try {
            String sharedPath = FileUtil.sharedPath(mContext);
            String zipFileName = String.format("%s.zip", fileName);
            SharedPreferences mAssetsSP = mContext.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE);
            SharedPreferences.Editor mAssetsSPEdit = mAssetsSP.edit();

            // InputStream zipStream = ctx.getApplicationContext().getAssets().open(zipName);
            String zipFilePath = String.format("%s/%s", sharedPath, zipFileName);
            String zipFolderPath = String.format("%s/%s", sharedPath, fileName);
            if (!(new File(zipFilePath)).exists()) {
                FileUtil.copyAssetFile(mContext, zipFileName, zipFilePath);
            }

            InputStream zipStream = new FileInputStream(zipFilePath);
            String md5String = FileUtil.MD5(zipStream);
            String keyName = String.format("local_%s_md5", fileName);

            boolean isShouldUnZip = true;
            isShouldUnZip = !("0".equals(mAssetsSP.getString(keyName, "0")) && mAssetsSP.getString(keyName, "0").equals(md5String));


            if (isShouldUnZip) {
                LogUtil.d("checkAssets", String.format("%s[%s] != %s", zipFileName, keyName, md5String));

                String folderPath = sharedPath;
                if (isInAssets) {
                    if ("icons".equals(fileName)) {
                        fileName = "images";
                    }
                    folderPath = String.format("%s/assets/%s/", sharedPath, fileName);
                } else {
                    File file = new File(zipFolderPath);
                    if (file.exists()) {
                        FileUtils.deleteDirectory(file);
                    }
                }

                // zipStream = ctx.getApplicationContext().getAssets().open(zipName);
                zipStream = new FileInputStream(zipFilePath);
                FileUtil.unZip(zipStream, folderPath, true);
                LogUtil.d("unZip", String.format("%s, %s", zipFileName, md5String));

                mAssetsSPEdit.putString(keyName, md5String).apply();
            }

            zipStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝程序自带的文件至指定文件
     *
     * @param assetName  程序自带的文件名称
     * @param outputPath 拷贝到指定文件的路径
     */
    public static void copyAssetFile(Context mContext, String assetName, String outputPath) {
        try {
            InputStream in = mContext.getApplicationContext().getAssets().open(assetName);
            FileOutputStream out = new FileOutputStream(outputPath);
            byte[] buffer = new byte[1024];
            int readPos;
            while ((readPos = in.read(buffer)) != -1) {
                out.write(buffer, 0, readPos);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部报表 JavaScript 文件路径
     *
     * @param groupID    群组ID
     * @param templateID 模板ID
     * @param reportID   报表ID
     * @return 文件路径
     */
    public static String reportJavaScriptDataPath(Context context, String groupID, String templateID, String reportID) {
        String assetsPath = FileUtil.sharedPath(context);
        String fileName = String.format(K.K_REPORT_DATA_FILE_NAME, groupID, templateID, reportID);
        return String.format("%s/assets/javascripts/%s", assetsPath, fileName);
    }

    /**
     * 保存截屏文件
     */
    public static File saveImage(String filePath, Bitmap bmp) {
        // 如果有目标文件，删除它
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        // 声明输出流
        FileOutputStream outStream = null;

        try {
            // 获得输出流，写入文件
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
            outStream.close();
            return file;
        } catch (IOException e) {
            LogUtil.e("snapshot", e.toString());
        }
        return null;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    //以下代码，原本uri返回的是file:///...，由于android4.4返回的是content:///... 需要转化格式
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getBitmapUrlPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void makeSureFolder(Context ctx, String folderName) {
        String cachedPath = String.format("%s/%s", FileUtil.basePath(ctx), folderName);
        FileUtil.makeSureFolderExist(cachedPath);
    }

    /**
     * app升级后，清除缓存头文件
     */
    public static void checkVersionUpgrade(Context ctx, String assetsPath, String sharedPath) {
        try {
            SharedPreferences mUserSP = ctx.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE);
            String versionConfigPath = String.format("%s/%s", assetsPath, K.K_CURRENT_VERSION_FILE_NAME);
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);

            String localVersion = "new-installer";
            boolean isUpgrade = true;
            if ((new File(versionConfigPath)).exists()) {
                localVersion = FileUtil.readFile(versionConfigPath);
                isUpgrade = !localVersion.equals(packageInfo.versionName);
            }

            if (isUpgrade) {
                LogUtil.d("VersionUpgrade",
                        String.format("%s => %s remove %s/%s", localVersion, packageInfo.versionName,
                                assetsPath, K.K_CACHED_HEADER_CONFIG_FILE_NAME));

                /*
                 * 用户报表数据js文件存放在公共区域
                 */
                String headerPath = String.format("%s/%s", sharedPath, K.K_CACHED_HEADER_CONFIG_FILE_NAME);
                File headerFile = new File(headerPath);
                if (headerFile.exists()) {
                    headerFile.delete();
                }

                FileUtil.writeFile(versionConfigPath, packageInfo.versionName);

                // 强制消息配置，重新上传服务器
                String pushConfigPath = String.format("%s/%s", FileUtil.basePath(ctx), K.K_PUSH_CONFIG_FILE_NAME);
                JSONObject pushJSON = FileUtil.readConfigFile(pushConfigPath);
                pushJSON.put(K.K_PUSH_IS_VALID, false);
                FileUtil.writeFile(pushConfigPath, pushJSON.toString());
            }
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String sharedPath, String assetsName) {
        File zipFilePath = new File(sharedPath);
        String zipFile = String.format("%s/%s", sharedPath, assetsName);
        File assetZip = new File(zipFile);
        if (!zipFilePath.exists()) {
            zipFilePath.mkdirs();
        }
        if (assetZip.isFile() && assetZip.exists()) {
            assetZip.delete();
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[4096];

            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();
            outputStream = new FileOutputStream(zipFile);

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

                LogUtil.d("hjjzz", "file download: " + fileSizeDownloaded + " of " + fileSize);
            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param mContext  上下文
     * @param assetName 静态文件名称 assets image
     */
    public static boolean unZipAssets(Context mContext, String assetName) {
        boolean isInAssets = true;
        String sharedPath = String.format("%s/%s", FileUtil.basePath(mContext), K.K_SHARED_DIR_NAME);
        if (ASSETS.equals(assetName) || LOADING.equals(assetName)) {
            isInAssets = false;
        }
        try {
            String zipFilePath = String.format("%s/%s", sharedPath, assetName + ".zip");

            String outputDirectory = sharedPath;
            if (isInAssets) {
                outputDirectory = String.format("%s/assets/%s/", sharedPath, assetName);
                if ("icons".equals(assetName)) {
                    outputDirectory = String.format("%s/assets/%s/", sharedPath, "images");
                }
            } else {
                File file = new File(String.format("%s/%s", sharedPath, assetName));
                if (file.exists()) {
                    FileUtils.deleteDirectory(file);
                }
            }

            InputStream inputStream = new FileInputStream(zipFilePath);
            // 创建解压目标目录
            File file = new File(outputDirectory);
            // 如果目标目录不存在，则创建
            if (!file.exists()) {
                file.mkdirs();
            }
            // 打开压缩文件
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            // 读取一个进入点
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            // 使用1Mbuffer
            byte[] buffer = new byte[10 * 1024 * 1024];
            // 解压时字节计数
            int count;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (zipEntry != null) {
                // 如果是一个目录
                if (zipEntry.isDirectory()) {
                    file = new File(outputDirectory + File.separator + zipEntry.getName());
                    // 文件需要覆盖或者是文件不存在
                    file.mkdir();
                } else {
                    // 如果是文件
                    file = new File(outputDirectory + File.separator + zipEntry.getName());
                    // 文件需要覆盖或者文件不存在，则解压文件
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
                // 定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return true;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static String storage_base(Context context) {
        return context.getFilesDir().getPath();
    }
}
