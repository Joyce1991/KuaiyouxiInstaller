package installer.libs.kuaiyuoxi.com.kyxinstaller.model;

/**
 * Created by xujiaoyong on 15/6/19.
 */
public interface IProgresser {


    public void onLoaded(long len);


    public boolean checkStoped()throws  Exception;

}
