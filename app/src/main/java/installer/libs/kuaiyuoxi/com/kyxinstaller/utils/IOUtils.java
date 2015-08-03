package installer.libs.kuaiyuoxi.com.kyxinstaller.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import installer.libs.kuaiyuoxi.com.kyxinstaller.model.IProgresser;

/**
 * Created by xujiaoyong on 15/6/18.
 */
public class IOUtils {



    public static FileOutputStream createFileOutputStream(File file)throws FileNotFoundException{
        File dir = file.getParentFile();
        if (!dir.exists()){
            dir.mkdirs();
        }

        return new FileOutputStream(file);
    }

    public static void in2out(InputStream ins ,OutputStream os)throws  Exception{
      in2out(ins,os,null);

    }



    public static String readFile( String filePath )throws  Exception
    {

        try
        {
            ByteArrayOutputStream  bos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream( filePath );

            in2out(fis, bos);
            byte[] buff =  bos.toByteArray();
            String result = new String(buff,"UTF-8");
            return result;
        }catch( Exception ex ) {

           throw  ex;
        }

    }

    public static void deleteFile(File file){
        deleteFileByExculde(file);
    }

    public static void deleteFileByExculde(File file, String... excludes)
    {
        if( file.isDirectory() )
        {
            File[] files = file.listFiles();
            if( files != null )
            {
                for( int i = 0; i < files.length; i++ )
                {
                    deleteFileByExculde(files[i], excludes);
                }
            }
        }
        else
        {
            String name = file.getName();
            if (excludes != null){
                for (String exclude : excludes){
                    if( name.endsWith(exclude) ){
                        return;
                    }
                }
            }

        }
        file.delete();

    }

    public static void in2out(InputStream ins ,OutputStream os,IProgresser progresser)throws  Exception{
        try {
            int len =0;
            byte[] buff = new byte[4 * 1024];

            while( (progresser == null || !progresser.checkStoped())  && (len = ins.read(buff)) != -1){
                os.write(buff,0,len);
                if (progresser!=null){
                    progresser.onLoaded(len);
                }
            }
        }catch (Exception ex){
            throw ex;
        }finally{
            if (os != null){
                try {
                    os.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if (ins != null){
                try {
                    ins.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

    }
    public static boolean isSDCardMouted() {
        try {
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getAvailableExternalMemorySize() {
        if (isSDCardMouted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }
}
