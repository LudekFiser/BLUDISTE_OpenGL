package gui;

import camera.Camera;
import graphics.TextRenderer;
import main.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;

public class Menu {
    private boolean isActive = false;
    private final long window;
    private final TextRenderer textRenderer;

    private Vector3f savedPosition;
    private float savedYaw, savedPitch;
    private Camera camera; // Přidáme referenci na kameru

    private Window gameWindow; // Přidej tuto proměnnou

    public Menu(long window, Camera camera, Window gameWindow) {
        this.window = window;
        this.textRenderer = new TextRenderer();
        this.camera = camera;
        this.gameWindow = gameWindow; // Ulož si referenci na Window
    }

    /*public void toggle() {
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
    }*/
    public void toggle() {
        isActive = !isActive;

        if (isActive) {
            // ✅ ULOŽÍME AKTUÁLNÍ STAV KAMERY
            savedPosition = new Vector3f(camera.getPosition());
            savedYaw = camera.getYaw();
            savedPitch = camera.getPitch();

            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        } else {
            // ✅ OBNOVÍME STAV KAMERY JEN POKUD HRA NEBYLA RESTARTOVÁNA
            if (savedPosition != null) {
                camera.setPosition(savedPosition);
                camera.setYaw(savedYaw);
                camera.setPitch(savedPitch);
            }

            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    /*public void render() {
        if (!isActive) return;

        // ✅ DEAKTIVACE SHADERU (pouze fixní vykreslování)
        GL30.glUseProgram(0);

        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, 800, 600, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        drawButton(300, 250, 500, 300, "Continue", 1.0f, 1.0f, 0.0f);
        drawButton(300, 350, 500, 400, "Restart", 1.0f, 0.0f, 0.0f);
        drawButton(300, 450, 500, 500, "Quit game", 1.0f, 0.0f, 0.0f);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST);
    }*/
    public void render() {
        if (!isActive) return;

        // ✅ Získání aktuální velikosti okna
        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        GLFW.glfwGetWindowSize(window, windowWidth, windowHeight);

        // ✅ Přizpůsobení projekce aktuální velikosti okna
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, windowWidth[0], windowHeight[0], 0, -1, 1); // 🔥 Nové rozměry okna
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // ✅ Přepočet souřadnic tlačítek podle velikosti okna
        float scaleX = windowWidth[0] / 800f;
        float scaleY = windowHeight[0] / 600f;

        drawButton(300 * scaleX, 250 * scaleY, 500 * scaleX, 300 * scaleY, "Continue", 1.0f, 1.0f, 0.0f);
        drawButton(300 * scaleX, 350 * scaleY, 500 * scaleX, 400 * scaleY, "Restart", 1.0f, 0.0f, 0.0f);
        drawButton(300 * scaleX, 450 * scaleY, 500 * scaleX, 500 * scaleY, "Quit game", 1.0f, 0.0f, 0.0f);

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

    /*public void handleClick(double x, double y) {
        if (isInsideButton(x, y, 300, 250, 500, 300)) {
            System.out.println("Pokračovat stisknuto");
            toggle();
        } else if (isInsideButton(x, y, 300, 450, 500, 500)) {
            System.out.println("Ukončit hru stisknuto");
            GLFW.glfwSetWindowShouldClose(window, true);
        } else if (isInsideButton(x, y, 300, 350, 500, 400)) {
            gameWindow.restartGame();
        }
    }*/
    public void handleClick(double x, double y) {
        // ✅ Přepočet kliknutí na aktuální velikost okna
        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        GLFW.glfwGetWindowSize(window, windowWidth, windowHeight);

        float scaleX = 800f / windowWidth[0];
        float scaleY = 600f / windowHeight[0];

        x *= scaleX;
        y *= scaleY;

        System.out.println("Kliknutí na: " + x + ", " + y);

        if (isInsideButton(x, y, 300, 250, 500, 300)) {
            System.out.println("Pokračovat stisknuto");
            toggle();
        } else if (isInsideButton(x, y, 300, 450, 500, 500)) {
            System.out.println("Ukončit hru stisknuto");
            GLFW.glfwSetWindowShouldClose(window, true);
        } else if (isInsideButton(x, y, 300, 350, 500, 400)) {
            gameWindow.restartGame();
        }
    }


    private boolean isInsideButton(double x, double y, float x1, float y1, float x2, float y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}