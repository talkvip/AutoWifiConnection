package org.emsg.wifiauto.control;

import android.app.Activity;

import org.emsg.wifiauto.Constants;



public class ViewControlManager {
    
    public static LoginControl getLoginControl(String intentTag,Activity context){
        if(intentTag.equals(Constants.INTENT_PARAM_EMSGLOGIN)){
            return new EmsgLoginControl(context);
        }else if(intentTag.equals(Constants.INTENT_PARAM_PASSWORD)){
            return new ModifyPasswodControl(context);
        }else{
            return new WifiLoginControl(context);
        }
    }

}
