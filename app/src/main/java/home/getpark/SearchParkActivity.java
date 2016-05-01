package home.getpark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SearchParkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_park);
        Toast.makeText(this, "YOU ARE SUCCESSFULLY LOGGED IN", Toast.LENGTH_LONG).show();
    }
}
