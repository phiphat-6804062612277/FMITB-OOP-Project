package tools;

import java.io.*;
import javax.sound.sampled.*;

/**
 * Tool for processing and normalizing raw WAV audio files in assets/sounds/backup/
 * and saving the balanced audio files into assets/sounds/.
 */
public class ProcessAudioLevels {
    public static void main(String[] args) throws Exception {
        File backupDir = new File("assets/sounds/backup");
        File targetDir = new File("assets/sounds");

        if (!backupDir.exists()) {
            System.err.println("Backup directory not found: " + backupDir.getAbsolutePath());
            return;
        }

        // Target dB adjustments for each sound
        adjustAndSave(backupDir, targetDir, "door_closed.wav", 1.5, "door_closed.wav", "door_close.wav");
        adjustAndSave(backupDir, targetDir, "door_open.wav", 10.0, "door_open.wav");
        adjustAndSave(backupDir, targetDir, "vent_close.wav", -1.5, "vent_close.wav", "vent_closed.wav");
        adjustAndSave(backupDir, targetDir, "vent_open.wav", 4.0, "vent_open.wav");
        adjustAndSave(backupDir, targetDir, "jumpscare.wav", -5.5, "jumpscare.wav");
        adjustAndSave(backupDir, targetDir, "monster_banging.wav", -3.5, "monster_banging.wav");
        adjustAndSave(backupDir, targetDir, "ambient.wav", 8.0, "ambient.wav");
        adjustAndSave(backupDir, targetDir, "win.wav", 15.0, "win.wav");

        System.out.println("Audio processing complete!");
    }

    private static void adjustAndSave(File srcDir, File destDir, String srcName, double gainDb, String... destNames) throws Exception {
        File srcFile = new File(srcDir, srcName);
        if (!srcFile.exists()) {
            System.err.println("File not found: " + srcFile.getAbsolutePath());
            return;
        }

        AudioInputStream ais = AudioSystem.getAudioInputStream(srcFile);
        AudioFormat fmt = ais.getFormat();
        byte[] rawBytes = ais.readAllBytes();
        ais.close();

        double multiplier = Math.pow(10.0, gainDb / 20.0);
        int bytesPerSample = fmt.getSampleSizeInBits() / 8;
        boolean bigEndian = fmt.isBigEndian();

        byte[] outBytes = new byte[rawBytes.length];

        for (int i = 0; i < rawBytes.length; i += bytesPerSample) {
            int sample = 0;
            if (bytesPerSample == 1) {
                sample = (rawBytes[i] & 0xFF) - 128;
                sample <<= 8;
            } else if (bytesPerSample == 2) {
                if (bigEndian) sample = (rawBytes[i] << 8) | (rawBytes[i + 1] & 0xFF);
                else sample = (rawBytes[i + 1] << 8) | (rawBytes[i] & 0xFF);
            }

            // Apply gain with soft clipping to prevent digital harshness
            double processed = sample * multiplier;
            if (processed > 32700.0) {
                processed = 32700.0 + (32767.0 - 32700.0) * Math.tanh((processed - 32700.0) / (32767.0 - 32700.0));
            } else if (processed < -32700.0) {
                processed = -32700.0 + (-32768.0 - -32700.0) * Math.tanh((processed - -32700.0) / (-32768.0 - -32700.0));
            }

            short outSample = (short) Math.max(-32768, Math.min(32767, (int) Math.round(processed)));

            if (bytesPerSample == 1) {
                outBytes[i] = (byte) ((outSample >> 8) + 128);
            } else if (bytesPerSample == 2) {
                if (bigEndian) {
                    outBytes[i] = (byte) ((outSample >> 8) & 0xFF);
                    outBytes[i + 1] = (byte) (outSample & 0xFF);
                } else {
                    outBytes[i] = (byte) (outSample & 0xFF);
                    outBytes[i + 1] = (byte) ((outSample >> 8) & 0xFF);
                }
            }
        }

        for (String destName : destNames) {
            File destFile = new File(destDir, destName);
            ByteArrayInputStream bais = new ByteArrayInputStream(outBytes);
            AudioInputStream outAis = new AudioInputStream(bais, fmt, outBytes.length / fmt.getFrameSize());
            AudioSystem.write(outAis, AudioFileFormat.Type.WAVE, destFile);
            outAis.close();
            System.out.printf("Wrote %s (gain: %+.1f dB)\n", destName, gainDb);
        }
    }
}
