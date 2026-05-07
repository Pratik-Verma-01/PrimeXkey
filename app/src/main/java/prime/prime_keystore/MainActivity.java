package prime.prime_keystore;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.primex.key.R; // <-- बस ये एक लाइन का जादू है!

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}