package comp.example.ahsimapc.textscanner;

import android.content.Context;

import java.io.File;

public class PhotoLab {
  static   Context context;

  private static   PhotoLab  mPhotoLab;
    public static  PhotoLab getPhotoLabInstance(Context context1)
    {    context=context1;
        if(mPhotoLab==null)
        {  mPhotoLab=new PhotoLab();
            return mPhotoLab ;
        }else
        {
            return mPhotoLab;
        }
    }




    public File getFile(Photo photo)
    {  File file=context.getFilesDir();
        return new File(file,photo.getFileName());

    }
}
