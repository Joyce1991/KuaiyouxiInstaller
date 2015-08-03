package installer.libs.kuaiyuoxi.com.kyxinstaller.domain;

import android.graphics.Bitmap;

/**
 * Created by xujiaoyong on 15/6/13.
 */
public class InstallManifest {



    private String appName;
    private long appSize;
    private String packageName;
    private String versionName;
    private int sdkVersion;

    private String copyPath;

    private long apkCRC32;

    private String cpuType;
    private String screenDensity;

    private Bitmap icon;
    private String gpkVersion;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(int sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getCopyPath() {
        return copyPath;
    }

    public void setCopyPath(String copyPath) {
        this.copyPath = copyPath;
    }

    public long getApkCRC32() {
        return apkCRC32;
    }

    public void setApkCRC32(long apkCRC32) {
        this.apkCRC32 = apkCRC32;
    }

    public String getCpuType() {
        return cpuType;
    }

    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }

    public String getScreenDensity() {
        return screenDensity;
    }

    public void setScreenDensity(String screenDensity) {
        this.screenDensity = screenDensity;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getGpkVersion() {
        return gpkVersion;
    }

    public void setGpkVersion(String gpkVersion) {
        this.gpkVersion = gpkVersion;
    }
}
