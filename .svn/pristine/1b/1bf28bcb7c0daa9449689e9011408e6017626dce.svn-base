package org.emsg.wifiauto.control;

import android.app.Activity;
public abstract class LoginControl implements IViewControl{
    public LoginControl(Activity context){
    }
    public  void actionLogin(){
        if(filterLogin()){
            login();
        }
    }
    protected abstract void  login();
    protected abstract boolean  filterLogin();
}
