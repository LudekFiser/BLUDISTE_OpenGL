package input;

import camera.Camera;

import gui.Menu;
import main.Window;

import maze.Maze3D;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import transforms.Vec3D;

public class InputHandler {
    private long window;
    private Camera camera;

    private float sensitivity = 0.1f;
    private double lastMouseX = 400, lastMouseY = 300;

    private boolean escPressed = false;
    private Menu menu;


    private boolean flyTogglePressed = false;  // ✅ Ochrana proti opakovanému stisku F


    public InputHandler(long window, Camera camera, Menu menu) {
        this.window = window;
        this.camera = camera;
        this.menu = menu;

        GLFW.glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            float xOffset = (float) (xpos - lastMouseX) * sensitivity;
            float yOffset = (float) (lastMouseY - ypos) * sensitivity; // Inverze Y

            lastMouseX = xpos;
            lastMouseY = ypos;

            camera.rotate(yOffset, xOffset);
        });

        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public void processInput() {
        float dx = 0, dz = 0;
        if (camera == null) return;  // ✅ Ochrana před NullPointerException
        Vector3f movement = new Vector3f();

        // 🔹 Vektor směru pohledu (DOPŘEDU/DOZADU - OSA Z)
        Vector3f sideways = new Vector3f(
                (float) Math.sin(Math.toRadians(camera.getYaw())),
                0,
                (float) -Math.cos(Math.toRadians(camera.getYaw()))
        );

        // 🔹 Vektor pravé strany (DOLEVA/DOPRAVA - OSA X)
        Vector3f forward = new Vector3f(sideways).cross(new Vector3f(0, 1, 0)).normalize();

        // ✅ Výchozí rychlost
        float movementSpeed = 0.1f;

        // ✅ Pokud je `SHIFT` stisknutý, rychlost se zvýší
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            movementSpeed = 0.2f;  // ✅ Sprint
        }

        // 🔹 SPRÁVNÉ přiřazení WASD pohybu:
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            movement.add(forward);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            movement.sub(forward);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            movement.add(sideways);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            movement.sub(sideways);
        }
        // 🔹 Přepínání režimu létání (F)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS && !flyTogglePressed) {
            camera.toggleFlyMode();
            System.out.println("LETANI");
            flyTogglePressed = true;  // ✅ Zabrání opakovanému přepínání při držení klávesy
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F) == GLFW.GLFW_RELEASE) {
            flyTogglePressed = false;
        }

        // 🔹 Létání nahoru/dolů (E/Q) – pouze pokud je aktivní létání
        if (camera.isFlying()) {
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS) {
                movement.add(new Vector3f(0, movementSpeed, 0));  // 🔼 Nahoru
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
                movement.sub(new Vector3f(0, movementSpeed, 0));  // 🔽 Dolů
            }
        }

        // ✅ Pokud je pohyb, normalizujeme a aplikujeme rychlost (včetně sprintu)
        if (movement.lengthSquared() > 0) {
            movement.normalize().mul(movementSpeed);  // ✅ Použijeme rychlost

            // ✅ Přidáme souřadnice objektu pro detekci kolize
            float objX = 0.0f;  // X souřadnice objektu
            float objZ = -1.0f; // Z souřadnice objektu
            float objWidth = 0.6f;  // Šířka objektu
            float objHeight = 0.6f; // Výška objektu

            camera.move(movement, objX, objZ, objWidth, objHeight);  // ✅ Zachová kolize + sprint!
        }

        // ✅ Skákání
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            camera.jump();
        }


        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS && !escPressed) {
            escPressed = true;
            menu.toggle();
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_RELEASE) {
            escPressed = false;
        }

        if (menu.isActive()) {
            return;  // ✅ Pokud je menu aktivní, vstupy se ignorují
        }

        GLFW.glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (menu.isActive() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
                double[] xpos = new double[1];
                double[] ypos = new double[1];

                GLFW.glfwGetCursorPos(win, xpos, ypos);
                menu.handleClick(xpos[0], ypos[0]);
            }
        });



    }

}