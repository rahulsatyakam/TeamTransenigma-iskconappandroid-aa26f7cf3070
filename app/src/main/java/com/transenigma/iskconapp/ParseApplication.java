package com.transenigma.iskconapp;
import com.parse.Parse;

public class ParseApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8ih2cFwU0nN2Bdp39n7xGOlfumKbPD4ODldqFosk", "cWzfGS01dtGLVfjRYnjMekPDxrtXXkjK5dmVYk8d");
    }
}

