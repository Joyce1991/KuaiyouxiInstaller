package installer.libs.kuaiyuoxi.com.kyxinstaller.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import installer.libs.kuaiyuoxi.com.kyxinstaller.model.IProgresser;

/**
 * Created by xujiaoyong on 15/6/18.
 */
public class ZipUtils {


    public static void unzipDirectory(File file, String rootDirPath, IProgresser progresser) throws
            Exception {

        ZipFile zf = null;

        try {

            zf = new ZipFile(file, "UTF-8");

            Enumeration<ZipArchiveEntry> entries = (Enumeration<ZipArchiveEntry>) zf.getEntries();
            while (entries.hasMoreElements()) {
                if (progresser.checkStoped()) {
                    break;
                }
                ZipArchiveEntry ze = entries.nextElement();
                if (ze == null) {
                    break;
                }

                String entryName = ze.getName().replace("\\", "/");
                File destFile = new File(rootDirPath + "/" + entryName);

                if (entryName.equals(GpkConstants.APPLICATION_APK) || entryName.contains("DS_Store")
                        || entryName.equals(GpkConstants.ICON) || entryName.equals(GpkConstants.MAINIFEST_DAT)
                        || entryName.equals(GpkConstants.MD5_DAT)) {
                    continue;
                }


                if (ze.isDirectory()) {
                    if (!destFile.exists()) {
                        destFile.mkdirs();
                    }

                } else {
                    File destDir = destFile.getParentFile();
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    try {
                        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(destFile, false));
                        BufferedInputStream ins = new BufferedInputStream(zf.getInputStream(ze));
                        IOUtils.in2out(ins, os, progresser);


                    } catch (Exception ex) {
                        throw ex;
                    }

                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            try {
                if (zf != null) {
                    zf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void unzipFile(File file, String entryName, String destFilePath
            , IProgresser progresser) throws Exception {
        ZipFile zipFile = null;


        try {
            zipFile = new ZipFile(file);

            ZipArchiveEntry entry = zipFile.getEntry(entryName);
            InputStream ins = new BufferedInputStream(zipFile.getInputStream(entry));

            OutputStream os = new BufferedOutputStream(
                    IOUtils.createFileOutputStream(new File(destFilePath)));

            IOUtils.in2out(ins, os, progresser);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

    }

    public static long calcuteDataSize(File zipFIle,IProgresser progresser)throws Exception {
        long length = 0;
        ZipFile zf = null;
        try {


            zf = new ZipFile(zipFIle);

            Enumeration<ZipArchiveEntry> entries = zf.getEntries();
            while ((progresser != null && !progresser.checkStoped())  && entries.hasMoreElements()) {
                ZipArchiveEntry ze = entries.nextElement();
                if (ze == null) {
                    break;
                }
                String catalogue = ze.getName().replace("\\", "/");
                if (catalogue.equals(GpkConstants.APPLICATION_APK)
                        || catalogue.equals(GpkConstants.ICON)
                        || catalogue.equals(GpkConstants.MAINIFEST_DAT)
                        || catalogue.equals(GpkConstants.MD5_DAT)) {

                    continue;
                }
                if (ze.isDirectory()) {

                    continue;
                }
                length += ze.getSize();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
           throw ex;
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return length;
    }
}
