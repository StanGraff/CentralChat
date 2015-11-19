package centralsoft.uco.edu.centralchat;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by Justin on 11/13/15.
 */
public class PlaySound {

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private Context ct;

    public static final int MESSAGE_SENT = 1;
    public static final int MESSAGE_RECEIVED = 2;

    PlaySound(Context ct) {
        this.ct = ct;
        initSounds();
    }

    private void initSounds() {
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(MESSAGE_SENT, soundPool.load(ct, R.raw.message_sent, 1));
        soundPoolMap.put(MESSAGE_RECEIVED, soundPool.load(ct, R.raw.message_received, 1));
    }

    public void playSound(int sound) {
        // Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0
        AudioManager mgr = (AudioManager) ct.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        // Play the sound with the correct volume
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
    }

    public void playSent() {
        playSound(MESSAGE_SENT);
    }

    public void playReceived() {
        playSound(MESSAGE_RECEIVED);
    }
}
