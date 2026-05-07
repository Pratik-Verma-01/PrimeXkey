package prime.prime_keystore;

import android.app.Activity; // AppCompatActivity की जगह नार्मल Activity
import android.os.Bundle;
import com.primex.key.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
