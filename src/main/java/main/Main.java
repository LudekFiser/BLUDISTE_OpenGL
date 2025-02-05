package main;

import camera.Camera;
import graphics.Renderer;
import org.lwjgl.opengl.GL;


public class Main {
    public static void main(String[] args) {
        Window window = new Window(800, 600, "OpenGL Test");
        window.run();
    }
}

