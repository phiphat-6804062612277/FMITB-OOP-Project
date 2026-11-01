package system;

/**
 * CameraManager controls active camera views and floor switching.
 */
public class CameraManager {
    private CameraId activeCamera;
    private int activeFloor;

    public CameraManager() {
        reset();
    }

    public void reset() {
        this.activeCamera = CameraId.C1;
        this.activeFloor = 1;
    }

    public void selectCamera(CameraId cam) {
        if (cam != null) {
            this.activeCamera = cam;
            this.activeFloor = cam.getFloor();
        }
    }

    public void toggleFloor() {
        if (activeFloor == 1) {
            setFloor(2);
        } else {
            setFloor(1);
        }
    }

    public void setFloor(int floor) {
        this.activeFloor = floor;
        if (floor == 1) {
            if (activeCamera.getFloor() != 1) {
                this.activeCamera = CameraId.C1;
            }
        } else {
            if (activeCamera.getFloor() != 2) {
                this.activeCamera = CameraId.C3;
            }
        }
    }

    public CameraId getActiveCamera() {
        return activeCamera;
    }

    public int getActiveFloor() {
        return activeFloor;
    }
}
