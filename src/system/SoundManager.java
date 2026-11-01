package system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
import javax.sound.sampled.*;

/**
 * SoundManager handles loading, caching, balancing, and playing WAV sound clips.
 * Uses 1D arrays, Scanner, and Thread/Runnable as taught in the OOP lectures.
 */
public class SoundManager {
    private static SoundManager instance;
    private static final int MAX_SOUNDS = 30;

    private final File soundsDir;

    // 1D Parallel Arrays for caching audio clips (Lecture 4)
    private final String[] clipNames;
    private final Clip[] clips;
    private int clipCount;

    // 1D Parallel Arrays for per-sound decibel offsets
    private final String[] offsetNames;
    private final float[] offsetValues;
    private int offsetCount;

    // Master volume (0.0 to 1.0)
    private float masterVolume = 0.85f;

    private SoundManager() {
        File dir = new File("assets/sounds");
        if (!dir.exists()) {
            dir = new File("../assets/sounds");
        }
        this.soundsDir = dir;
        this.clipNames = new String[MAX_SOUNDS];
        this.clips = new Clip[MAX_SOUNDS];
        this.clipCount = 0;

        this.offsetNames = new String[MAX_SOUNDS];
        this.offsetValues = new float[MAX_SOUNDS];
        this.offsetCount = 0;

        initGainOffsets();
        loadVolumeConfig();
        preloadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void setGainOffset(String filename, float dbValue) {
        for (int i = 0; i < offsetCount; i++) {
            if (offsetNames[i].equals(filename)) {
                offsetValues[i] = dbValue;
                return;
            }
        }
        if (offsetCount < MAX_SOUNDS) {
            offsetNames[offsetCount] = filename;
            offsetValues[offsetCount] = dbValue;
            offsetCount++;
        }
    }

    private float getGainOffset(String filename) {
        for (int i = 0; i < offsetCount; i++) {
            if (offsetNames[i].equals(filename)) {
                return offsetValues[i];
            }
        }
        return 0.0f;
    }

    private int findClipIndex(String filename) {
        for (int i = 0; i < clipCount; i++) {
            if (clipNames[i].equals(filename)) {
                return i;
            }
        }
        return -1;
    }

    private void putClipInCache(String filename, Clip clip) {
        int idx = findClipIndex(filename);
        if (idx != -1) {
            clips[idx] = clip;
        } else if (clipCount < MAX_SOUNDS) {
            clipNames[clipCount] = filename;
            clips[clipCount] = clip;
            clipCount++;
        }
    }

    private void initGainOffsets() {
        setGainOffset("door_close.wav", 0.0f);
        setGainOffset("door_open.wav", 0.0f);
        setGainOffset("vent_close.wav", 0.0f);
        setGainOffset("vent_open.wav", 0.0f);
        setGainOffset("monster_banging.wav", -2.0f);
        setGainOffset("jumpscare.wav", -3.5f);
        setGainOffset("ambient.wav", -4.0f);
        setGainOffset("win.wav", 0.0f);
    }

    private void loadVolumeConfig() {
        File configFile = new File(soundsDir, "volume_config.txt");
        if (!configFile.exists()) {
            return;
        }

        try {
            Scanner scanner = new Scanner(configFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.length() == 0 || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }
                int eqIndex = line.indexOf('=');
                String key = line.substring(0, eqIndex).trim().toLowerCase();
                String valStr = line.substring(eqIndex + 1).trim();

                try {
                    float val = Float.parseFloat(valStr);
                    if (key.equals("master_volume")) {
                        this.masterVolume = Math.max(0.0f, Math.min(1.0f, val));
                    } else {
                        String filename = resolvePrimaryFilename(key);
                        setGainOffset(filename, val);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            scanner.close();
        } catch (Exception ignored) {
        }
    }

    private void preloadSounds() {
        String[] sounds = {
            "ambient", "door_close", "door_open",
            "jumpscare", "monster_banging", "vent_close",
            "vent_open", "win"
        };
        for (int i = 0; i < sounds.length; i++) {
            getOrLoadClip(sounds[i]);
        }
    }

    public void playSound(final String soundName) {
        Thread soundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = getOrLoadClip(soundName);
                    if (clip != null) {
                        if (clip.isRunning()) {
                            clip.stop();
                        }
                        clip.setFramePosition(0);
                        applyVolume(clip, resolvePrimaryFilename(soundName));
                        clip.start();
                    }
                } catch (Exception ignored) {
                }
            }
        });
        soundThread.start();
    }

    public void playLoop(final String soundName) {
        Thread loopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = getOrLoadClip(soundName);
                    if (clip != null && !clip.isRunning()) {
                        clip.setFramePosition(0);
                        applyVolume(clip, resolvePrimaryFilename(soundName));
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                } catch (Exception ignored) {
                }
            }
        });
        loopThread.start();
    }

    public void stopSound(String soundName) {
        String filename = resolvePrimaryFilename(soundName);
        int index = findClipIndex(filename);
        if (index != -1 && clips[index] != null && clips[index].isRunning()) {
            clips[index].stop();
        }
    }

    private void applyVolume(Clip clip, String filename) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        try {
            FloatControl gainCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float baseOffsetDb = getGainOffset(filename);
            float effVol = Math.max(0.0001f, Math.min(1.0f, masterVolume));
            float linearDb = (float) (20.0 * Math.log10(effVol));
            float targetDb = linearDb + baseOffsetDb;
            float clampedDb = Math.max(gainCtrl.getMinimum(), Math.min(gainCtrl.getMaximum(), targetDb));
            gainCtrl.setValue(clampedDb);
        } catch (Exception ignored) {
        }
    }

    private String resolvePrimaryFilename(String soundName) {
        String key = soundName.trim().toLowerCase();
        if (key.endsWith(".wav")) {
            key = key.substring(0, key.length() - 4);
        }
        if (key.equals("door_close") || key.equals("door_closed")) {
            return "door_close.wav";
        } else if (key.equals("vent_close") || key.equals("vent_closed")) {
            return "vent_close.wav";
        } else if (key.equals("door_creak")) {
            return "door_open.wav";
        } else if (key.equals("bang")) {
            return "monster_banging.wav";
        } else {
            return key + ".wav";
        }
    }

    private Clip getOrLoadClip(String soundName) {
        String primaryFile = resolvePrimaryFilename(soundName);

        int existingIdx = findClipIndex(primaryFile);
        if (existingIdx != -1) {
            return clips[existingIdx];
        }

        File file = new File(soundsDir, primaryFile);
        if (file.exists() && file.isFile()) {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                applyVolume(clip, primaryFile);
                putClipInCache(primaryFile, clip);
                return clip;
            } catch (Exception ignored) {
            }
        }

        try {
            InputStream is = getClass().getResourceAsStream("/assets/sounds/" + primaryFile);
            if (is != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                applyVolume(clip, primaryFile);
                putClipInCache(primaryFile, clip);
                return clip;
            }
        } catch (Exception ignored) {
        }

        putClipInCache(primaryFile, null);
        return null;
    }
}
