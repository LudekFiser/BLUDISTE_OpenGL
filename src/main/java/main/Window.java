package main;

import camera.Camera;
import graphics.Renderer;

import gui.Menu;
import input.InputHandler;


import maze.Maze3D;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import transforms.Vec3D;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengles.GLES20.glEnable;

public class Window {
    private long window;
    private int width, height;
    private String title;
    private Camera camera;
    private InputHandler inputHandler;
    private Renderer renderer;


    private Menu menu;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void run() {
        init();
        loop();
        GLFW.glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Nepodařilo se inicializovat GLFW!");
        }

        window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (window == 0) {
            throw new RuntimeException("Nepodařilo se vytvořit okno!");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);
        GL.createCapabilities();



        camera = new Camera(0, 0, 2);  // ✅ Hráč začíná nad podlahou
        menu = new Menu(window, camera);
        //Maze3D maze = new Maze3D(60, 60, camera);
        inputHandler = new InputHandler(window, camera, menu);
        renderer = new Renderer(camera);


        GL11.glEnable(GL11.GL_DEPTH_TEST);  // ✅ Aktivujeme správné vykreslování hloubky
        GL11.glDisable(GL11.GL_CULL_FACE); // ✅ Zakážeme odstranění zadních ploch
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);  // ✅ Vyhlazení polygonů
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

        // 🔹 NASTAVENÍ PERSPEKTIVY BEZ `GLU`
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        // Vytvoření projekční matice
        float aspectRatio = (float) width / height;
        Matrix4f projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(70.0f),
                aspectRatio,
                0.1f,
                100.0f);

        // Nahrání matice do OpenGL
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            projectionMatrix.get(fb);
            GL11.glLoadMatrixf(fb);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void loop() {

        long lastTime = System.nanoTime();
        int frames = 0;
        long fpsCounter = System.currentTimeMillis();

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glLoadIdentity();
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(16);
                camera.getViewMatrix().get(fb);
                GL11.glMultMatrixf(fb);
            }

            // 1️⃣ Vykreslení herní scény, ale jen pokud menu není aktivní
            if (!menu.isActive()) {
                renderer.render();
            }

            // 2️⃣ Pokud je menu aktivní, vykresli ho přes hru
            if (menu.isActive()) {
                menu.render();
            }

            menu.render();

            inputHandler.processInput();
            camera.update();  // ✅ Aktualizace gravitace a skákání

            // FPS Counter do názvu okna
            long currentTime = System.nanoTime();
            frames++;
            if (System.currentTimeMillis() - fpsCounter >= 1000) {
                GLFW.glfwSetWindowTitle(window, title + " | FPS: " + frames);
                frames = 0;
                fpsCounter += 1000;
            }

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }
}





