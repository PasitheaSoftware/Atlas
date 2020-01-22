package com.software.pasithea.atlas;

import android.app.Application;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.log10;
import static java.lang.Math.round;

class AtlasAudioManager {
    private static final String TAG = "AtlasAudioManager";

    private static final int MIN_BUILD = Build.VERSION_CODES.M;
    public static final int AUDIOFOCUS_MIN_BUILD = Build.VERSION_CODES.O;

    private static Application mApplication = null;

    private static int mCurrentAudioVolume;
    private static int mMaxAudioVolume;
    private static int[] audioDevicesList = {3,4,7,8,22};

    private static AudioFocusRequest mFocusRequest = null;
    private static AudioManager mAudioManager;
    private static MediaRecorder mRecorder;

    public AtlasAudioManager(Application application){
        mApplication = application;
    }

    /*
    Audio focus management.
    This group of methods will request the audio focus for this app. The audiofocus is hardcoded to
    AUDIOFOCUS_GAIN_TRANSIENT, this means that the app can duck when another audio notification is
    coming.
    The methods are:
    setaudiomanager(activity): Set the audioManager for the specified activity.
    checkAudioFocus(): Ask for the audio focus.
     */
    protected void requestAudioFocus(){
        mFocusRequest = createFocusRequest();
        setAudioManager();
        requestFocus(mAudioManager);
    }

    protected void abandonFocus(){
        if(Build.VERSION.SDK_INT >= AUDIOFOCUS_MIN_BUILD){
            try {
                mAudioManager.abandonAudioFocusRequest(getFocusRequest());
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                Log.e(TAG, "abandonFocus: ", e);
            }
        }
    }

    private void setAudioManager() {
        mAudioManager = (AudioManager) mApplication.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setParameters("noise_suppression=on");
    }

    private AudioManager.OnAudioFocusChangeListener getAudioFocusListener(){
        AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = null;
        mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) {
                }
            }
        };
        return mAudioFocusChangeListener;
    }

    private void requestFocus(AudioManager manager){
        if(manager.isMusicActive()){
            Log.i(TAG, "RequestAudioFocus: AudioManager is playing music");
        } else {
            Log.i(TAG, "RequestAudioFocus: AudioManager is not playing music");
        }

        int res = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            res = manager.requestAudioFocus(getFocusRequest());
        } else {
            res = manager.requestAudioFocus(getAudioFocusListener(),
                    AudioManager.STREAM_ACCESSIBILITY,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.i(TAG, "onCreate: Focus granted");
        }
    }

    private AudioFocusRequest getFocusRequest() {
        return mFocusRequest;
    }

    private AudioFocusRequest createFocusRequest() {
        AudioAttributes playbackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        if (Build.VERSION.SDK_INT >= AUDIOFOCUS_MIN_BUILD) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(getAudioFocusListener())
                    .build();
        }
        return mFocusRequest;
    }

    /*
    Audio volume management
    Manage the volume of the apps. By default the volume is set following the simple alogrithm:
    <p>
    IF headset is connected (either wired or bluetooth):<br>
    Volume = maxVolume/2 (to avoid that the sound being too loud)<br>
    ELSE:<br>
    Volume = maxVolume
    </p>
    The volume level can also be forced withe the method setAudioVolume(int) and the volume can be
    restored to its previous level with the method restoreVolume()
     */
    protected void setAudioVolume() {
        manageVolume();
        AudioDeviceInfo[] mDeviceInfo = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        ArrayList<Integer> connectedDevices = new ArrayList<Integer>();
        for (int i = 0; i < mDeviceInfo.length; i++) {
            for (int sub : audioDevicesList) {
                if (mDeviceInfo[i].getType() == sub) {
                    connectedDevices.add(sub);
                }
            }
        }

        if (connectedDevices.size() > 0){
            int volume = (int)round(getMaxAudioVolume()/2);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_VIBRATE);
        } else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_VIBRATE);
        }
    }

    protected void setAudioVolume(int volume){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    }

    protected void restoreVolume(){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, getCurrentAudioVolume(), AudioManager.FLAG_SHOW_UI);
    }

    public static int getCurrentAudioVolume() {
        return mCurrentAudioVolume;
    }

    public static void setCurrentAudioVolume(int currentAudioVolume) {
        mCurrentAudioVolume = currentAudioVolume;
    }

    public static int getMaxAudioVolume() {
        return mMaxAudioVolume;
    }

    public static void setMaxAudioVolume(int maxAudioVolume) {
        mMaxAudioVolume = maxAudioVolume;
    }

    private static void manageVolume(){
        mAudioManager = (AudioManager) mApplication.getSystemService(Context.AUDIO_SERVICE);
        setCurrentAudioVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        setMaxAudioVolume(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    }

    /**
     * Dynamic ambient sound management /!\ EXPERIMENTAL FUNCTION /!\
     * Function to detect the ambient sound and compute automatically the correct
     * volume level based on the ambient noise.
     * This function is still work in progress and has not been evaluated in a real app.<br>
     *     USE IT AT YOUR OWN RISK
     *     </br>
     * use startRecorder() to record the ambient sound. To have a correct estimation the recording
     * time must be at least 2 seconds. The recording file is store in /dev/null meaning there is no
     * file stored in a device storage.
     * When the recording is done ... TO CONTINUE
     */
    public void startRecorder(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile("/dev/null");
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "startRecorder: "+ e);
        }
        try {
            mRecorder.start();
        } catch (SecurityException e){
            e.printStackTrace();
            Log.e(TAG, "startRecorder: " + e);
        }
    }

    public void stopRecorder(){
        if (mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public int computeExpectedVolume(){
        double dbdiff = getRecorderAmplitude()-(getCurrentAudioVolume()*getStepValue());
        if(dbdiff >= 1.0){
            double stepIncrease = dbdiff/getStepValue();
            int newVolume = getCurrentAudioVolume()+(int)stepIncrease;
            Log.d(TAG, "computeExpectedVolume: change; "+stepIncrease + "- New Volume: " + newVolume);
            return newVolume;
        } else {
            Log.d(TAG, "computeExpectedVolume: No change");
            return getCurrentAudioVolume();
        }
    }

    private double getRecorderAmplitude(){
        double amp = mRecorder.getMaxAmplitude();
        double amp2db = round(20*log10(amp));
        return amp2db;
    }

    private double getStepValue(){
        int maxVolume = getMaxAudioVolume();
        double step = 100/(maxVolume+1);
        return step;
    }
}
