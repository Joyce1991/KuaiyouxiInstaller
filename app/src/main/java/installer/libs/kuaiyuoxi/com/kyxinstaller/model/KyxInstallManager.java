package installer.libs.kuaiyuoxi.com.kyxinstaller.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.androidannotations.annotations.rest.RequiresAuthentication;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


import installer.libs.kuaiyuoxi.com.kyxinstaller.BuildConfig;
import installer.libs.kuaiyuoxi.com.kyxinstaller.utils.GpkConstants;

/**
 * Created by xujiaoyong on 15/6/19.
 */
public class KyxInstallManager {

    private ExecutorService mExecutor;

    private LinkedBlockingQueue<InstallFile> mFileQueue;

    private AtomicBoolean mShutdowned;

    private HashMap<File,KyxInstallTask> mRunningTasks;

    private String mBasePath;
    private String mTempDirPath;
    private Context mContext;


    private Handler mHandler;

    private File mTempDir;


    private LinkedList<IKyxInstallListener> mListeners;



    public KyxInstallManager( String basePath, String tempDirPath,Context context){
        this.mBasePath = basePath;
        this.mTempDirPath = tempDirPath;
        this.mContext = context;
        mShutdowned = new AtomicBoolean(false);
        mFileQueue = new LinkedBlockingQueue<InstallFile>();
        mExecutor = Executors.newCachedThreadPool();
        mRunningTasks = new HashMap<File, KyxInstallTask>();

        mListeners  = new LinkedList<IKyxInstallListener>();

        mHandler = new InstallMsgHandler();

        buildTempDir();


        start();

    }

    public synchronized void addInstallListener(IKyxInstallListener listener){
        if (!mListeners.contains(listener)){
            mListeners.add(listener);
        }
    }


    public synchronized void removeInstallListener(IKyxInstallListener listener){

            mListeners.remove(listener);

    }
    private void buildTempDir(){

        mTempDir = new File( mTempDirPath);

        mTempDir.mkdirs();
    }


    /**
     * 安装GPK文件，并将数据包存放到指定目录
     * @param file     准备进行安装的文件
     * @param basePath 数据包存放路径
     * @return  返回安装任务是否添加成功
     */
    public boolean install(File file,String basePath){
        InstallFile installFile = new InstallFile();
        installFile.setBasePath(basePath);
        installFile.setFile(file);

        if (mFileQueue.contains(installFile)){
            return false;
        }
        return  mFileQueue.offer(installFile);
    }


    /**
     * 安装GPK文件，并将数据包存放到指定目录
     * @param file     准备进行安装的文件
     * @return  返回安装任务是否添加成功
     */
    public boolean install(File file){
        return install(file, mBasePath);
    }

    public void shutdown(){
        mShutdowned.set(true);
    }



    /**
     * 取消安装GPK文件
     * @param file     准备进行取消安装操作的文件
     */
    public void cancelInstall(File file){


        InstallFile installFile = new InstallFile();

        installFile.setFile(file);

        mFileQueue.remove(installFile);


        KyxInstallTask task = mRunningTasks.get(file);

        if (task != null){
            task.cancel();

        }

    }


    private void start(){
        mExecutor.execute(mTaskThread);
    }



    private Runnable mTaskThread = new Runnable() {
        @Override
        public void run() {
            while(!mShutdowned.get()){
                try{

                    InstallFile installFile =  mFileQueue.take();
                    if (BuildConfig.DEBUG){
                        Log.i("kyx_installer","installFile:"+installFile);
                    }
                    if (installFile != null){
                        KyxInstallTask runningTask = new KyxInstallTask(installFile.getFile(),
                                installFile.getBasePath(),mContext);
                        runningTask.setHandler(mHandler);
                        runningTask.setTempDirPath(mTempDir.getAbsolutePath());

                        mRunningTasks.put(installFile.getFile(),runningTask);
                        Future<File> future = mExecutor.submit(runningTask);
                        future.get();

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            mExecutor.shutdown();

        }
    };





    private static final class InstallFile{
        private File file;

        private String basePath;




        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InstallFile that = (InstallFile) o;

            return file.equals(that.file);

        }

        @Override
        public int hashCode() {

            return file.hashCode();
        }
    }



    /**
     * 查询文件是否正在安装
     * @param file     需要进行查询的文件
     * @return  返回文件是否正在安装
     */
    public boolean isRunning(File file){
        return mRunningTasks.containsKey(file);
    }



    /**
     * 查询安装文件数据包总长度
     * @param file     需要进行查询的文件
     * @return  返回总长度
     */
    public long getTaskDataSize(File file){
        KyxInstallTask task = mRunningTasks.get(file);
        if (task != null){
            return task.getDataSize();
        }
        return 0;
    }


    /**
     * 查询安装文件数据包解压进度
     * @param file     需要进行查询的文件
     * @return  返回解压进度
     */
    public long getTaskProgress(File file){
        KyxInstallTask task = mRunningTasks.get(file);
        if (task != null){
            return task.getProgress();
        }
        return 0;
    }



    private final class InstallMsgHandler extends  Handler{
        @Override
        public void handleMessage(Message msg) {
            KyxInstallTask installer = (KyxInstallTask) msg.obj;
            switch (msg.what){
                case  GpkConstants.MSG_ERROR:

                    onError(installer);
                    break;

                case  GpkConstants.MSG_PREPARE:
                    onPrepare(installer);

                    break;
                case  GpkConstants.MSG_COMPLETE:
                    onComplete(installer);

                    break;
                case  GpkConstants.MSG_PROGRESS:

                    onProgress(installer);
                    break;
                case  GpkConstants.MSG_CANCELED:

                    onCanceled(installer);
                    break;


            }
            super.handleMessage(msg);
        }
    }


    private void onError(KyxInstallTask installer){
        mRunningTasks.remove(installer.getInstallFile());
        for (IKyxInstallListener installListener : mListeners){
            installListener.onError(installer.getInstallFile(),installer.getErrorCode());
        }
    }
    private void onComplete(KyxInstallTask installer){
        mRunningTasks.remove(installer.getInstallFile());
        for (IKyxInstallListener installListener : mListeners){
            installListener.onComplete(installer.getInstallFile());
        }
    }
    private void onPrepare(KyxInstallTask installer){
        for (IKyxInstallListener installListener : mListeners){
            installListener.onPrepare(installer.getInstallFile());
        }
    }
    private void onCanceled(KyxInstallTask installer){
        mRunningTasks.remove(installer.getInstallFile());
        for (IKyxInstallListener installListener : mListeners){
            installListener.onCanceled(installer.getInstallFile());
        }
    }

    private void onProgress(KyxInstallTask installer){
        for (IKyxInstallListener installListener : mListeners){
            installListener.onUnpacking( installer.getInstallFile(),installer.getProgress(),installer.getDataSize());
        }
    }

}
