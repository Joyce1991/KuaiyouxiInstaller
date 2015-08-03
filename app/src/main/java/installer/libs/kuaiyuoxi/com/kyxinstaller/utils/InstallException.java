package installer.libs.kuaiyuoxi.com.kyxinstaller.utils;

/**
 * Created by xujiaoyong on 15/6/18.
 */
public class InstallException extends  Exception {



    public static final int FLAG_STOPED = 0x999999;

    public static final int FILE_NOT_FOUNT  = 0x100001;

    public static final int NO_SPACE_LEFT = 0x100002;


    public static final int SYSTEM_VERSION_DOWN = 0x100003;


    public static final int FILE_ZIP_UNVALID = 0x100004;

    public static final int ERROR_OTHER = 0x110000;

    private int errorCode;



    public InstallException(int errorCode,String detailMessage) {

        this(detailMessage);

       this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public InstallException(String detailMessage) {
        super(detailMessage);
    }

    public InstallException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InstallException(Throwable throwable) {
        super(throwable);
    }
}
