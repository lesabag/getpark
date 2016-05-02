package home.getpark;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by liore on 4/27/2016.
 */
public class FirebaseCntx extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}