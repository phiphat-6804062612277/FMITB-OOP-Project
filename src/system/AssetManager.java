package system;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * AssetManager handles loading, caching, and serving images from the assets/images/ directory
 * using ImageIcon and 1D arrays as taught in Lecture 4, Lecture 9, and Lecture 10.
 */
public class AssetManager {
    private static AssetManager instance;
    private static final int MAX_IMAGES = 30;

    private final String[] imageNames;
    private final Image[] images;
    private int imageCount;
    private final File assetsDir;

    private AssetManager() {
        this.imageNames = new String[MAX_IMAGES];
        this.images = new Image[MAX_IMAGES];
        this.imageCount = 0;
        File dir = new File("assets/images");
        if (!dir.exists()) {
            dir = new File("../assets/images");
        }
        this.assetsDir = dir;
        preloadAssets();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    private void preloadAssets() {
        String[] commonImages = {
            "menu_bg.jpg", "game_over.png", "game_win.png",
            "bathroom_bg.png", "door_open.png", "door_closed.png",
            "vent_open.png", "vent_closed.png",
            "sahur_at_door.png", "tralala_at_vent.png",
            "cam1_empty.png", "cam1_sahur.png", "cam1_tralala.png", "cam1_tralala_sahur.png",
            "cam2_empty.png", "cam2_sahur.png",
            "cam3_empty.png", "cam3_sahur.png",
            "cam4_empty.png", "cam4_sahur.png"
        };

        for (int i = 0; i < commonImages.length; i++) {
            loadImage(commonImages[i]);
        }
    }

    /**
     * Linear search in imageNames array (Lecture 4: Array Searching).
     */
    private int findImageIndex(String filename) {
        for (int i = 0; i < imageCount; i++) {
            if (imageNames[i].equals(filename)) {
                return i;
            }
        }
        return -1;
    }

    private void putInCache(String key, Image img) {
        int idx = findImageIndex(key);
        if (idx != -1) {
            images[idx] = img;
        } else if (imageCount < MAX_IMAGES) {
            imageNames[imageCount] = key;
            images[imageCount] = img;
            imageCount++;
        }
    }

    public Image getImage(String filename) {
        int index = findImageIndex(filename);
        if (index != -1) {
            return images[index];
        }
        return loadImage(filename);
    }

    private Image loadImage(String filename) {
        File file = new File(assetsDir, filename);
        if (file.exists() && file.isFile()) {
            ImageIcon icon = new ImageIcon(file.getPath());
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage();
                putInCache(filename, img);
                return img;
            }
        } else {
            URL url = getClass().getResource("/assets/images/" + filename);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                if (icon.getIconWidth() > 0) {
                    Image img = icon.getImage();
                    putInCache(filename, img);
                    return img;
                }
            }
        }

        putInCache(filename, null);
        return null;
    }
}
