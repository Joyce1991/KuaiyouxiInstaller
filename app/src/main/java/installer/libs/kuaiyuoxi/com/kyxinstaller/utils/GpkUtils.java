package installer.libs.kuaiyuoxi.com.kyxinstaller.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import installer.libs.kuaiyuoxi.com.kyxinstaller.domain.InstallManifest;
import installer.libs.kuaiyuoxi.com.kyxinstaller.model.IProgresser;

/**
 * Created by xujiaoyong on 15/6/19.
 */
public class GpkUtils {


    public static boolean isCanInstallToExternal(String apkPath,Context context){
        boolean canExternal = true;
        try{
            PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_META_DATA);

            Bundle bundle = info.applicationInfo.metaData;

            if (bundle != null) {
                String value = bundle.getString("kyx_external");
                if (value != null && value.equals("no")) {
                    canExternal = false;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return canExternal;
    }

    public static InstallManifest decodeManifest(File tempDir
                                                 ,File gpkFile,IProgresser progresser)throws  Exception{
        File manifestFile = new File(tempDir.getAbsolutePath() + "/" + GpkConstants.MAINIFEST_DAT);

        ZipUtils.unzipFile(gpkFile, GpkConstants.MAINIFEST_DAT, manifestFile.getAbsolutePath(), progresser);
        String manifestText = IOUtils.readFile(manifestFile.getAbsolutePath());
        byte[] data = Base.decode(manifestText);
        data = SecurityUtils.decrypt(data);
        manifestText = new String(data, "gbk");


        InstallManifest manifest = jsonTransMainifest(manifestText);

        return manifest;
    }
    public static InstallManifest getManifest(File gpkFile)throws  Exception{

        File tempDir =  gpkFile.getParentFile();

        if (tempDir == null){
            throw  new RuntimeException("Temp dir not found");
        }

        File manifestFile = new File(tempDir.getAbsolutePath() + "/" + gpkFile.getName()+"_"+GpkConstants.MAINIFEST_DAT);
        ZipUtils.unzipFile(gpkFile, GpkConstants.MAINIFEST_DAT, manifestFile.getAbsolutePath(), null);
        String manifestText = IOUtils.readFile(manifestFile.getAbsolutePath());
        byte[] data = Base.decode(manifestText);
        data = SecurityUtils.decrypt(data);
        manifestText = new String(data, "gbk");
        InstallManifest manifest = jsonTransMainifest(manifestText);
        manifestFile.delete();
        return manifest;
    }





    private static InstallManifest jsonTransMainifest(String jsonStr)
            throws JSONException {
        InstallManifest manifest = new InstallManifest();
        JSONObject rootJson = new JSONObject(jsonStr);

        JSONObject phoneBaseInfo = rootJson.getJSONObject("gpkBaseInfo");
        String cpuType = phoneBaseInfo.getString("cpuType");
        manifest.setCpuType(cpuType); // 1

        int sdkVersion = phoneBaseInfo.getInt("sdkVersion");
        manifest.setSdkVersion(sdkVersion);

        String screenDensity = phoneBaseInfo.getString("screenDensity");
        manifest.setScreenDensity(screenDensity); // 2

        JSONObject dataValidation = rootJson.getJSONObject("dataValidation");
        long apkCRC32 = dataValidation.getLong("apkCRC32");


        manifest.setApkCRC32(apkCRC32); // 3


        JSONObject dataBaseInfo = rootJson.getJSONObject("dataBaseInfo");
        String copyPath = dataBaseInfo.getString("copyPath");
        copyPath = copyPath.replace("\n", "");
        manifest.setCopyPath(copyPath); // 5


        JSONObject baseInfoJson = rootJson.getJSONObject("apkBaseInfo");
        String appName = baseInfoJson.getString("appName");
        manifest.setAppName(appName); // 6
        int appSize = baseInfoJson.getInt("appSize");
        manifest.setAppSize(appSize); // 7
        String packageName = baseInfoJson.getString("packageName");
        manifest.setPackageName(packageName); // 8
        String versionName = baseInfoJson.getString("versionName");
        manifest.setVersionName(versionName); // 9

        String gpkVersion = rootJson.optString("gpkVersion");
        if (TextUtils.isEmpty(gpkVersion)) {
            manifest.setGpkVersion("old");
        } else {
            manifest.setGpkVersion("new");
        }

        return manifest;
    }

}
