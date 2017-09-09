package com.example.minho.remember;

        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.graphics.drawable.BitmapDrawable;
        import android.graphics.drawable.Drawable;
        import android.media.ExifInterface;

        import java.io.File;
        import java.io.IOException;

public class Recycler_item {
    String type;
    String content;
    String path;
    int voice;
    String time;
    String date;

    public String getDate() {
        return date;
    }

    String getType(){
        return this.type;
    }
    public synchronized int getPhotoOrientationDegree(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }
    public synchronized Bitmap getRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError e)
            {
            }
        }

        return bitmap;
    }
    Bitmap getImage(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        File imgFile = new File(content);
        int degree = getPhotoOrientationDegree(content);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        myBitmap = getRotatedBitmap(myBitmap, degree);
        return myBitmap;
    }
    String getContent(){
        return this.content;
    }

    String getTime() {
        return this.time;
    }

    Recycler_item(String type, String content, String time, String date){
        this.type = type;
        this.content=content;
        this.time = time;
        this.date = date;

    }

}
