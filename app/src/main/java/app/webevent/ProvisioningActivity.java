package app.webevent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.URI;
import java.net.URISyntaxException;

public class ProvisioningActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                String data = result.getContents();
                if(data != null) {
                    if(data.contains("#")) {
                        try {
                            URI uri = new URI(data);
                            data = uri.getFragment();
                        } catch (URISyntaxException ignored) {}
                    }
                    SharedPreferences sp = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString("url", data);
                    spe.apply();
                    Intent in = new Intent(this, WebViewActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(in);
                    this.finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provisioning);
        Button btn = findViewById(R.id.btnLaunchScanner);
        btn.setOnClickListener(v -> {
            ScanOptions so = new ScanOptions();
            so.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            so.setOrientationLocked(false);
            so.setBeepEnabled(false);
            so.setPrompt("Scan event QR code");
            barcodeLauncher.launch(so);
        });

    }

}