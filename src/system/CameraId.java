package system;

/**
 * CameraId class representing the 4 CCTV cameras with their code and floor.
 */
public class CameraId {
    public static final CameraId C1 = new CameraId("C.1", 1);
    public static final CameraId C2 = new CameraId("C.2", 1);
    public static final CameraId C3 = new CameraId("C.3", 2);
    public static final CameraId C4 = new CameraId("C.4", 2);

    private final String code;
    private final int floor;

    public CameraId(String code, int floor) {
        this.code = code;
        this.floor = floor;
    }

    public String getCode() {
        return code;
    }

    public int getFloor() {
        return floor;
    }
}
