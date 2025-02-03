package input;

import camera.Camera;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class InputHandler {
    private long window;
    private Camera camera;
    private float speed = 0.1f;
    private float sensitivity = 0.1f;
    private double lastMouseX = 400, lastMouseY = 300;


    public InputHandler(long window, Camera camera) {
        this.window = window;
        this.camera = camera;

        GLFW.glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            float xOffset = (float) (xpos - lastMouseX) * sensitivity;
            float yOffset = (float) (lastMouseY - ypos) * sensitivity; // Inverze Y

            lastMouseX = xpos;
            lastMouseY = ypos;

            camera.rotate(yOffset, xOffset);
        });

        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    /*public void processInput() {
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
            movementSpeed = 0.3f;  // ✅ Dvojnásobná rychlost při sprintu
        }

        // 🔹 SPRÁVNÉ přiřazení WASD pohybu:
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            //movement.add(forward);
            movement.add(forward.mul(movementSpeed, new Vector3f()));  // ✅ OPRAVENO
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            //movement.sub(forward);
            movement.sub(forward.mul(movementSpeed, new Vector3f()));  // ✅ OPRAVENO
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            //movement.add(sideways);
            movement.add(sideways.mul(movementSpeed, new Vector3f()));  // ✅ OPRAVENO
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            //movement.sub(sideways);
            movement.sub(sideways.mul(movementSpeed, new Vector3f()));  // ✅ OPRAVENO
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            camera.jump();
        }

        if (movement.lengthSquared() > 0) {
            movement.normalize().mul(0.1f);

            // ✅ Přidáme souřadnice objektu pro detekci kolize
            float objX = 0.0f;  // X souřadnice objektu
            float objZ = -1.0f; // Z souřadnice objektu
            float objWidth = 0.1f;  // Šířka objektu
            float objHeight = 0.1f; // Výška objektu

            camera.move(movement, objX, objZ, objWidth, objHeight);  // ✅ Teď to má správné argumenty!
        }

        // ESC zavře okno
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }*/
    public void processInput() {
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
            movementSpeed = 0.3f;  // ✅ Sprint
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

        // ✅ ESC zavře okno
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }

}
