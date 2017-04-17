package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHLight.PHLightAlertMode;

public class MyApplicationActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("HW3 Hue App");
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();

        Button func1Button = (Button) findViewById(R.id.buttonFunc1);
        func1Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { startAlert(); }
        });

        Button func2Button = (Button) findViewById(R.id.buttonFunc2);
        func2Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { stopAlert(); }
        });

        SeekBar mySeekBar = (SeekBar) findViewById(R.id.seekBar);
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int brightness, boolean b) {
                updateBrightness(brightness);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


    }

    public void startAlert() {
        int length = 5;
        EditText et = (EditText) findViewById(R.id.editText);
        String toParse = et.getText().toString();
        if(!toParse.equals(""))length = Integer.parseInt(toParse);

        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setAlertMode(PHLightAlertMode.ALERT_LSELECT);
//            bridge.updateLightState(light, lightState, listener);
            bridge.updateLightState(light, lightState);
        }


        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                PHBridge bridge = phHueSDK.getSelectedBridge();
                List<PHLight> allLights = bridge.getResourceCache().getAllLights();
                for (PHLight light : allLights) {
                    PHLightState lightState = new PHLightState();
                    lightState.setAlertMode(PHLightAlertMode.ALERT_NONE);
//            bridge.updateLightState(light, lightState, listener);
                    bridge.updateLightState(light, lightState);
                }
            }
        };
        handler.postDelayed(r, length * 1000);

//        try {
//            Thread.sleep(length * 1000);
//        } catch (InterruptedException e) {}
//
//        for (PHLight light : allLights) {
//            PHLightState lightState = new PHLightState();
//            lightState.setAlertMode(PHLightAlertMode.ALERT_NONE);
////            bridge.updateLightState(light, lightState, listener);
//            bridge.updateLightState(light, lightState);
//        }
    }

    public void stopAlert() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setAlertMode(PHLightAlertMode.ALERT_NONE);
//            bridge.updateLightState(light, lightState, listener);
            bridge.updateLightState(light, lightState);
        }
    }

    public void updateBrightness(int brightness) {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setBrightness(brightness);
            lightState.setTransitionTime(0);
//            bridge.updateLightState(light, lightState, listener);
            bridge.updateLightState(light, lightState);
        }
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
//    PHLightListener listener = new PHLightListener() {
//
//        @Override
//        public void onSuccess() {
//        }
//
//        @Override
//        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
//           Log.w(TAG, "Light has updated");
//        }
//
//        @Override
//        public void onError(int arg0, String arg1) {}
//
//        @Override
//        public void onReceivingLightDetails(PHLight arg0) {}
//
//        @Override
//        public void onReceivingLights(List<PHBridgeResource> arg0) {}
//
//        @Override
//        public void onSearchComplete() {}
//    };
    
    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
