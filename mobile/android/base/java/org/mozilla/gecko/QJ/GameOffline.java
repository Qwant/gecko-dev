package org.mozilla.gecko.QJ;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gayamac on 09/02/2018.
 */

public class GameOffline {

    public static final GameOffline sharedInstance = new GameOffline();
    public String code;

    private GameOffline() {

    }

    public void load(Context context) {
        if (code == null) {
            InputStream is = null;
            try {
                is = context.getAssets().open("gameOffline.html");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                code = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
