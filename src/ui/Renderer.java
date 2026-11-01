package ui;

import java.awt.Graphics;

/**
 * Interface for screen renderers.
 */
public interface Renderer {
    public abstract void render(Graphics g, int screenWidth, int screenHeight);
}
