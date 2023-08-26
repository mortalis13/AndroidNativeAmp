package org.home.amp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getName();
    private static final int AUDIO_EFFECT_REQUEST = 0;
    private static final int OBOE_API_AAUDIO = 0;
    private static final int OBOE_API_OPENSL_ES=1;

    private Button toggleEffectButton;
    private boolean isPlaying = false;

    private int apiSelection = OBOE_API_AAUDIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleEffectButton = findViewById(R.id.button_toggle_effect);
        toggleEffectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEffect();
            }
        });
        toggleEffectButton.setText(getString(R.string.start_effect));

        Engine.setDefaultStreamValues(this);
        
        Engine.create();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onDestroy() {
        stopEffect();
        Engine.delete();
        super.onDestroy();
    }

    public void toggleEffect() {
        if (isPlaying) {
            stopEffect();
        } else {
            Engine.setAPI(apiSelection);
            startEffect();
        }
    }

    private void startEffect() {
        Log.d(TAG, "Attempting to start");

        if (!isRecordPermissionGranted()){
            requestRecordPermission();
            return;
        }

        boolean success = Engine.setEffectOn(true);
        if (success) {
            toggleEffectButton.setText(R.string.stop_effect);
            isPlaying = true;
        } else {
            toggleEffectButton.setText("Error");
            isPlaying = false;
        }
    }

    private void stopEffect() {
        Log.d(TAG, "Playing, attempting to stop");
        Engine.setEffectOn(false);
        toggleEffectButton.setText(R.string.start_effect);
        isPlaying = false;
    }

    private boolean isRecordPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestRecordPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_EFFECT_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (AUDIO_EFFECT_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), getString(R.string.need_record_audio_permission), Toast.LENGTH_SHORT).show();
        } else {
            toggleEffect();
        }
    }
}
