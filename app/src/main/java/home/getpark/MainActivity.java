package home.getpark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.firebase.client.Firebase;
import com.google.android.gms.plus.Plus;


public class MainActivity extends Activity {

    private Firebase mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Check Authentication
        mRef = new Firebase(Constants.FIREBASE_URL);
        Log.i("MainActivity", "Check Authentication");
        if (mRef.getAuth() == null) {
            loadLoginView();
        } else {
            loadSearchParkActivityView();
        }
    }

    private void loadLoginView() {
        Log.i("MainActivity", "+loadLoginView()");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadSearchParkActivityView() {
        Log.i("MainActivity", "+loadLoginView()");
        Intent intent = new Intent(this, SearchParkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}