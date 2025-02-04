package gui;

import camera.Camera;
import graphics.TextRenderer;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class Menu {
    private boolean isActive = false;
    private final long window;
    private final TextRenderer textRenderer;

    private Vector3f savedPosition;
    private float savedYaw, savedPitch;
    private Camera camera; // Přidáme referenci na kameru

    public Menu(long window, Camera camera) {
        this.window = window;
        this.textRenderer = new TextRenderer();
        this.camera = camera;
    }

    public void toggle() {
        isActive = !isActive;

        if (isActive) {
            // ✅ ULOŽÍME AKTUÁLNÍ STAV KAMERY
            savedPosition = new Vector3f(camera.getPosition());
            savedYaw = camera.getYaw();
            savedPitch = camera.getPitch();

            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        } else {
            // ✅ OBNOVÍME STAV KAMERY
            camera.setPosition(savedPosition);
            camera.setYaw(savedYaw);
            camera.setPitch(savedPitch);

            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void render() {
        if (!isActive) return;

        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, 800, 600, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        drawButton(300, 250, 500, 300, "Continue", 1.0f, 1.0f, 0.0f);
        drawButton(300, 350, 500, 400, "Quit game", 1.0f, 0.0f, 0.0f);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST);
    }

    private void drawButton(float x1, float y1, float x2, float y2, String text, float r, float g, float b) {
        glColor3f(0.3f, 0.3f, 0.3f);
        glBegin(GL_QUADS);
        glVertex2f(x1, y1);
        glVertex2f(x2, y1);
        glVertex2f(x2, y2);
        glVertex2f(x1, y2);
        glEnd();

        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINE_LOOP);
        glVertex2f(x1, y1);
        glVertex2f(x2, y1);
        glVertex2f(x2, y2);
        glVertex2f(x1, y2);
        glEnd();

        textRenderer.renderText(text, x1 + (x2 - x1) / 2 - text.length() * 6, y1 + (y2 - y1) / 2 + 5, r, g, b);
    }

    public void handleClick(double x, double y) {
        if (isInsideButton(x, y, 300, 250, 500, 300)) {
            System.out.println("Pokračovat stisknuto");
            toggle();
        } else if (isInsideButton(x, y, 300, 350, 500, 400)) {
            System.out.println("Ukončit hru stisknuto");
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }

    private boolean isInsideButton(double x, double y, float x1, float y1, float x2, float y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }
}
