package com.primex.key;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.primex.key.utils.JKSGenerator;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etAlias = findViewById(R.id.etAlias);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etFullName = findViewById(R.id.etFullName);
        EditText etOrganization = findViewById(R.id.etOrganization);
        EditText etCity = findViewById(R.id.etCity);
        SeekBar sbValidity = findViewById(R.id.sbValidity);
        TextView tvYears = findViewById(R.id.tvYears);
        Button btnCreate = findViewById(R.id.btnCreate);

        sbValidity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvYears.setText(progress + " Years");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    generateKeystore(
                            etAlias.getText().toString().trim(),
                            etPassword.getText().toString().trim(),
                            etFullName.getText().toString().trim(),
                            etOrganization.getText().toString().trim(),
                            etCity.getText().toString().trim(),
                            sbValidity.getProgress()
                    );
                } else {
                    requestPermission();
                }
            }
        });
    }

    private void generateKeystore(String alias, String pass, String name, String org, String city, int years) {
        if (alias.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Alias and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Downloads/PrimeXKey folder logic
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File primeXFolder = new File(downloadsFolder, "PrimeXKey");

        if (!primeXFolder.exists()) {
            primeXFolder.mkdirs(); // Agar folder nahi hai toh banayega
        }

        String fileName = alias + "_" + System.currentTimeMillis() + ".jks";
        File fileToSave = new File(primeXFolder, fileName);

        Toast.makeText(this, "Generating JKS... Please wait.", Toast.LENGTH_SHORT).show();

        // Background thread me generate karna zaroori hai taaki app hang na ho
        new Thread(new Runnable() {
            @Override
            public void run() {
                String finalName = name.isEmpty() ? "Developer" : name;
                String finalOrg = org.isEmpty() ? "PrimeX" : org;
                String finalCity = city.isEmpty() ? "CyberCity" : city;
                int finalYears = years == 0 ? 1 : years;

                boolean success = JKSGenerator.generateAndSave(
                        alias,
                        pass,
                        pass, // Alias and Store pass same rakhe hain for simplicity
                        finalName,
                        finalOrg,
                        finalCity,
                        finalYears,
                        2048,
                        "SHA256withRSA",
                        fileToSave
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(MainActivity.this, "Saved successfully at: Downloads/PrimeXKey/" + fileName, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Generation Failed! Check logs.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private boolean checkPermission() {
        // Android 11+ (API 30) ke liye alag storage system hota hai
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Toast.makeText(this, "Please allow 'All Files Access' manually in Settings to save JKS in Downloads folder", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
}
