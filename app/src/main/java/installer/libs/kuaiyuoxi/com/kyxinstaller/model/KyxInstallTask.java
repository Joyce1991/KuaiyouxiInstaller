package installer.libs.kuaiyuoxi.com.kyxinstaller.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipException;


import installer.libs.kuaiyuoxi.com.kyxinstaller.domain.InstallManifest;
import installer.libs.kuaiyuoxi.com.kyxinstaller.utils.GpkConstants;
import installer.libs.kuaiyuoxi.com.kyxinstaller.utils.GpkUtils;
import installer.libs.kuaiyuoxi.com.kyxinstaller.utils.IOUtils;
import installer.libs.kuaiyuoxi.com.kyxinstaller.utils.InstallException;
import installer.libs.kuaiyuoxi.com.kyxinstaller.utils.ZipUtils;

/**
 * Created by xujiaoyong on 15/6/18.
 */
public class KyxInstallTask implements Callable<File>, IProgresser {



    private static final int TIME_GAP = 1000;

    private File mInstallFile;

    private String mBasePath;
    private Handler mHandler;
    private String mTempDirPath;
    private Context mContext;
    private AtomicBoolean mStopFlag;
    private String mPackageName;



    private File mDataDir;
    private File mTempDir;
    private long mDataSize;
    private int mErrorCode;
    private long mProgress;
    private boolean mUnpacking;


    private long mPreviousTime;



    public KyxInstallTask(File installFile, String basePath, Context context){
        this.mInstallFile = installFile;
        this.mBasePath = basePath;
        this.mContext = context;
        mStopFlag = new AtomicBoolean(false);
    }




    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void setTempDirPath(String mTempDirPath) {
        this.mTempDirPath = mTempDirPath;
    }



    private void install() {


        try {
            if (!mInstallFile.exists()) {
                throw new InstallException(InstallException.FILE_NOT_FOUNT, "File Not Found:" + mInstallFile.getAbsolutePath());
            }
            onPrepare();

            String fileName = mInstallFile.getName();
            if (fileName.endsWith(".apk")) {
                installApk(mInstallFile);
            } else {
                installGpk();
            }

            clean(mTempDir,null,false);
            onComplete();


        }  catch (Throwable ex) {
            if (ex instanceof  InstallException){
                InstallException installException = (InstallException) ex;
                if (installException.getErrorCode() == InstallException.FLAG_STOPED){
                    onStop();
                }else{
                    onError(installException.getErrorCode());
                }
            }else if (ex instanceof ZipException) {
                onError(InstallException.FILE_ZIP_UNVALID);
            }else {
                onError(InstallException.ERROR_OTHER);
            }
            clean(mTempDir,mDataDir,true);
        }


    }


    public void onStop(){
        mHandler.sendMessage(mHandler.obtainMessage(GpkConstants.MSG_CANCELED, this));
    }

    public void onPrepare() {
        mHandler.sendMessage(mHandler.obtainMessage(GpkConstants.MSG_PREPARE, this));
    }

    public void onError(int errorCode) {
        mErrorCode = errorCode;
        mHandler.sendMessage(mHandler.obtainMessage(GpkConstants.MSG_ERROR, this));
    }

    public void onComplete() {
        mHandler.sendMessage(mHandler.obtainMessage(GpkConstants.MSG_COMPLETE, this));
    }



    /**
     * 取消当前安装进程
     */
    public void cancel() {
        mStopFlag.set(true);
    }


    private void installGpk() throws Throwable {
        mTempDir = createTempDir();


        InstallManifest manifest = GpkUtils.decodeManifest(mTempDir, mInstallFile, this);

        mPackageName = manifest.getPackageName();


       int requiredVersion =  manifest.getSdkVersion();
        if (requiredVersion > Build.VERSION.SDK_INT){
            throw new InstallException(InstallException.SYSTEM_VERSION_DOWN,
                    "Require systemVersion :" + requiredVersion
                            + ",but your systemVersion :"
                            + Build.VERSION.SDK_INT);
        }

        mDataSize = ZipUtils.calcuteDataSize(mInstallFile, this);


        File apkFile = new File(mTempDir + File.separator + GpkConstants.APPLICATION_APK);


        ZipUtils.unzipFile(mInstallFile, GpkConstants.APPLICATION_APK, apkFile.getAbsolutePath(), this);


        if (TextUtils.isEmpty(mBasePath) ||
                !GpkUtils.isCanInstallToExternal(apkFile.getAbsolutePath(), mContext)) {
            mBasePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";
        }




        long freeSpace = IOUtils.getAvailableExternalMemorySize();


        if (freeSpace <= mDataSize) {
            throw new InstallException(InstallException.NO_SPACE_LEFT,
                    "No Space Left:" + mInstallFile.getAbsolutePath()
                            + ",freeSpace:"
                            + freeSpace + ",dataSize:" + mDataSize);
        }


        String dataPath = manifest.getCopyPath().replace("\\", "/");

        dataPath = convertDataPath(dataPath);


        mDataDir = new File(dataPath);


        mUnpacking = true;

        ZipUtils.unzipDirectory(mInstallFile, dataPath, this);


        installApk(apkFile);


    }


    private  String convertDataPath(String dataPath) {

        dataPath = dataPath.substring(0, dataPath.lastIndexOf("/"));

        if (dataPath.startsWith("/mnt/sdcard/")) {
            dataPath = dataPath.replace("/mnt/sdcard/", mBasePath + "/");
        }
        if (dataPath.startsWith("/mnt/sdcard")) {
            dataPath = dataPath.replace("/mnt/sdcard", mBasePath + "/");
        }
        if (dataPath.startsWith("/sdcard/")) {
            dataPath = dataPath.replace("/sdcard/", mBasePath+ "/");
        }
        if (dataPath.startsWith("/sdcard")) {
            dataPath = dataPath.replace("/sdcard", mBasePath+ "/");
        }
        return dataPath;


    }



    private void clean(File tempDir, File dataDir,boolean deleteApk) {
        if (deleteApk){
            IOUtils.deleteFileByExculde(tempDir);
        }else{
            IOUtils.deleteFileByExculde(tempDir, ".apk");
        }



        if (dataDir != null) {
            String dataDirPath = dataDir.getAbsolutePath();

            String text = dataDirPath.toLowerCase();
            String androidDataPath = null;
            String androidObbPath = null;
            if (text.endsWith("/android/") || text.endsWith("/android")) {
                 androidDataPath = dataDirPath+"/data/"+ mPackageName +"/";
                 androidObbPath = dataDirPath+"/obb/"+ mPackageName +"/";

            } else if (text.endsWith("/android/data") || text.endsWith("/android/data/")) {
                androidDataPath = dataDirPath+"/"+ mPackageName +"/";

            } else if (text.endsWith("/android/obb") || text.endsWith("/android/obb/")) {
                androidObbPath = dataDirPath+"/"+ mPackageName +"/";
            } else if (text.endsWith(mBasePath.toLowerCase())) {

                androidDataPath = dataDirPath+"/Android/data/"+ mPackageName +"/";
                androidObbPath = dataDirPath+"/Android/obb/"+ mPackageName +"/";

            } else if (!text.endsWith(".obb") || !text.endsWith(mPackageName) ||
                    !text.endsWith(mPackageName + "/")) {
                File parentFile = dataDir.getParentFile();
                if (parentFile != null){
                    androidDataPath = parentFile.getAbsolutePath();
                    if (androidDataPath.endsWith(mBasePath)) {
                        androidDataPath = null;
                    }
                }
            }else if(dataDir.equals(new File(mBasePath))){
                androidDataPath = mBasePath+"/Android/data/"+ mPackageName +"/";
                androidObbPath = mBasePath+"/Android/obb/"+ mPackageName +"/";
            }

            if (!TextUtils.isEmpty(androidDataPath)){
                clean(androidDataPath);


            }

            if (!TextUtils.isEmpty(androidObbPath)){
                clean(androidObbPath);
            }


        }


    }


    private void clean(String path){
        File cleanDir =   new File(path);
        File baseDir = new File(mBasePath);
        if (!baseDir.equals(cleanDir)){
            IOUtils.deleteFile(cleanDir);
        }
    }


    private void installApk(File apkFile) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

        mContext.startActivity(intent);
    }






    private File createTempDir() {
        File tempDir = new File(mTempDirPath + "/" + System.currentTimeMillis() + "/");
        tempDir.mkdirs();
        return tempDir;
    }

    @Override
    public void onLoaded(long len) {
        if (mUnpacking){
            mProgress += len;
            long gapTime = System.currentTimeMillis() - mPreviousTime;
            if (gapTime >= TIME_GAP){
                mHandler.sendMessage(mHandler.obtainMessage(GpkConstants.MSG_PROGRESS, this));
                mPreviousTime = System.currentTimeMillis();
            }


        }


    }

    @Override
    public boolean checkStoped()throws  Exception {
        boolean flag = mStopFlag.get();
        if (flag){
            throw  new InstallException(InstallException.FLAG_STOPED,"stoped");
        }

        return flag;

    }


    public boolean isCanceled(){
        return mStopFlag.get();
    }

    public long getProgress() {
        return mProgress;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public long getDataSize() {
        return mDataSize;
    }

    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public File call() throws Exception {

        install();
        return mInstallFile;
    }

    public File getInstallFile() {
        return mInstallFile;
    }


}
