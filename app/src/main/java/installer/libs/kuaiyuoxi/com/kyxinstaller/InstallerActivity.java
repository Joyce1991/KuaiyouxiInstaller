package installer.libs.kuaiyuoxi.com.kyxinstaller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.LinkedList;

import installer.libs.kuaiyuoxi.com.kyxinstaller.model.IKyxInstallListener;
import installer.libs.kuaiyuoxi.com.kyxinstaller.model.KyxInstallManager;

@EActivity(R.layout.activity_installer)
public class InstallerActivity extends Activity{

    @ViewById(R.id.kyx_installerListview) ListView mListView;
    @ViewById(R.id.kyx_installer_path) EditText mEditorText;
    @ViewById(R.id.kyx_installer_add) Button btnAdd;
    @ViewById(R.id.kyx_item_operation) Button btnCancell;

    @AfterViews void setText(){
        mEditorText.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.gpk");
        mListView.setAdapter(mAdapter);
    }

    LinkedList<File> mInstallFiles;

    private KyxInstallManager mInstallManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstallFiles = new LinkedList<File>();

        buildInstallManager();

        String gpkPath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/1.gpk";

       installFile(new File(gpkPath1));
    }


    /**
     * 构建安装管理器
     */
    private void buildInstallManager(){

        //设置数据包存放根目录，一般为存储卡根目录
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //设置解压临时文件目录
        String tempDirPath = basePath+"/kuaiyouxi/install_temp/";
        mInstallManager = new KyxInstallManager(basePath,tempDirPath,this);
        //添加安装监听回调
        mInstallManager.addInstallListener(mListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstallManager.removeInstallListener(mListener);
        mInstallManager.shutdown();;
    }

    @Click(R.id.kyx_installer_add) void addTask() {
        installFile();
    }

    @Click(R.id.kyx_item_operation) void cancell() {
        Log.i("kyx_installer","cancel");
        File file = (File) btnCancell.getTag();
        mInstallManager.cancelInstall(file);
    }



    /**
     * 安装文件
     */
    private void installFile(File file){
        boolean offered =  mInstallManager.install(file);
        if (!offered){
            Toast.makeText(this,"add failure",Toast.LENGTH_LONG).show();
        }
        mInstallFiles.add(file);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 安装文件
     */
    private void installFile(){
       String path =  mEditorText.getText().toString();
        if (TextUtils.isEmpty(path)){
            Toast.makeText(this,"path can not empty",Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(path);
        installFile(file);

    }


    private class ViewHolder {
        TextView nameView;
        ProgressBar progressBar;
        Button operationBtn;
    }



    private IKyxInstallListener mListener = new IKyxInstallListener() {
        @Override
        public void onPrepare(File file) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(File file, int errorCode) {
            mAdapter.notifyDataSetChanged();


        }

        @Override
        public void onUnpacking(File file, long progress, long length) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCanceled(File file) {
            mInstallFiles.remove(file);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"cancel:"+file.getName(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onComplete(File file) {
            mInstallFiles.remove(file);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"complete:"+file.getName(),Toast.LENGTH_LONG).show();
        }
    };

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mInstallFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null){
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_installer,null);
                holder = new ViewHolder();
                holder.nameView = (TextView) convertView.findViewById(R.id.kyx_item_name);
                holder.operationBtn = (Button) convertView.findViewById(R.id.kyx_item_operation);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.kyx_item_progressbar);
//                holder.operationBtn.setOnClickListener(InstallerActivity.this);
                convertView.setTag(holder);
            }else{
                holder  = (ViewHolder) convertView.getTag();
            }



            File file = mInstallFiles.get(position);
            String status = mInstallManager.isRunning(file) ? "installing" :"waiting";
            holder.nameView.setText(file.getName() + "_" + status);

            long progress = mInstallManager.getTaskProgress(file);
            long dataSize = mInstallManager.getTaskDataSize(file);

            int percent = (int) ((double) progress / (double) dataSize * 100);
            holder.progressBar.setProgress(percent);
            holder.progressBar.setMax(100);

            holder.operationBtn.setTag(file);

            return convertView;
        }
    };
}
