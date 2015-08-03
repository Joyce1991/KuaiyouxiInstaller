package installer.libs.kuaiyuoxi.com.kyxinstaller.model;

import java.io.File;

/**
 * Created by xujiaoyong on 15/6/13.
 */
public interface IKyxInstallListener {

    public void onPrepare(File file);

    public void onError(File file,int errorCode);

    public void onUnpacking(File file,long progress,long length);



    public void onCanceled(File file);

    public void onComplete(File file);

}
