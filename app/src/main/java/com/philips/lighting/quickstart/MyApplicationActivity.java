package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
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
        Button randomButton;
        randomButton = (Button) findViewById(R.id.buttonRand);
        randomButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                randomLights();
            }

        });

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

        try {
            Thread.sleep(length * 1000);
        } catch (InterruptedException e) {}

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setAlertMode(PHLightAlertMode.ALERT_NONE);
//            bridge.updateLightState(light, lightState, listener);
            bridge.updateLightState(light, lightState);
        }
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

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();
        
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
//            lightState.setOn(true);
//            lightState.setAlertMode(PHLightAlertMode.ALERT_SELECT);

//            PHLightState state = new PHLightState();
//// Start blinking for up to 30 seconds or until an alert 'none' is received.
//            state.setAlertMode(PHLightAlertMode.ALERT_LSELECT);
//            bridge.setLightStateForDefaultGroup(state);
//
//            try { Thread.sleep(5000); } catch (InterruptedException e) {}
//
//// Stop blinking after 5 seconds
//            state.setAlertMode(PHLightAlertMode.ALERT_NONE);
//            bridge.setLightStateForDefaultGroup(state);


            // To validate your lightstate is valid (before sending to the bridge) you can use:  
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };
    
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
