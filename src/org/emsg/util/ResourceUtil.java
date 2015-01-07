package org.emsg.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.emsg.wificonnect.R;

import java.lang.reflect.Field;

public class ResourceUtil {
    public static Drawable getImageResourceIDFromLeve(int lever,Context context){
        switch(lever){
            case 1:return context.getResources().getDrawable(R.drawable.icon_wifilev1);
            case 2:return context.getResources().getDrawable(R.drawable.icon_wifilev2);
            case 3:return context.getResources().getDrawable(R.drawable.icon_wifilev3);
            case 4:return context.getResources().getDrawable(R.drawable.icon_wifilev4);
            default:
                return context.getResources().getDrawable(R.drawable.icon_wifilev1);
        }
    }
   public static int getStringId(String fc) {
       int myimageId = R.string.apk;
       try {
           if (fc != null) {
               try {
                   Field field_up = R.string.class.getField(fc);
                   myimageId = field_up.getInt(new R.string());
               } catch (Exception e) {
                   System.out.println(e);
               }
           }
       } catch (Exception e) {
       }
       return myimageId;
   }

}
