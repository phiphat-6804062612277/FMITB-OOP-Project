package system;

/**
 * GameTimer handles the 5-minute survival countdown (300 seconds).
 */
public class GameTimer {
    private final float totalDurationSeconds;
    private float timeRemainingSeconds;
    private boolean isRunning;

    public GameTimer(float durationSeconds) {
        this.totalDurationSeconds = durationSeconds;
        this.timeRemainingSeconds = durationSeconds;
        this.isRunning = false;
    }

    public void start() {
        this.isRunning = true;
    }

    public void stop() {
        this.isRunning = false;
    }

    public void reset() {
        this.timeRemainingSeconds = totalDurationSeconds;
        this.isRunning = false;
    }

    public void update(float deltaSeconds) {
        if (!isRunning) {
            return;
        }

        timeRemainingSeconds -= deltaSeconds;
        if (timeRemainingSeconds < 0.0f) {
            timeRemainingSeconds = 0.0f;
            isRunning = false;
        }
    }

    public boolean isTimeUp() {
        return timeRemainingSeconds <= 0.0f;
    }

    public float getElapsedTime() {
        return totalDurationSeconds - timeRemainingSeconds;
    }

    public float getProgress() {
        return Math.min(1.0f, Math.max(0.0f, getElapsedTime() / totalDurationSeconds));
    }

    /**
     * Formats remaining time as MM:SS (e.g. "04:59")
     */
    public String getFormattedRemainingTime() {
        int totalSec = (int) Math.ceil(timeRemainingSeconds);
        int minutes = totalSec / 60;
        int seconds = totalSec % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
