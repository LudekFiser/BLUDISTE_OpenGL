package camera;

import maze.Maze3D;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import transforms.Vec3D;


public class Camera {
    private Vector3f position;
    private float pitch, yaw;

    private float velocityY = 0.0f;  // ✅ Rychlost ve směru Y (pro gravitaci a skákání)
    private boolean isJumping = false;

    private boolean isFlying = false;  // ✅ Režim létání

    private static final float GRAVITY = -0.05f;  // ✅ Simulace gravitace
    private static final float JUMP_FORCE = 0.7f;  // ✅ Síla skoku
    private static final float GROUND_LEVEL = 0.0f;  // ✅ Výška podlahy

    private Maze3D maze;



    public Camera(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
        this.pitch = 0;
        this.yaw = -90f;
    }

    public void move(Vector3f delta, float objX, float objZ, float width, float height) {
        Vector3f newPosition = new Vector3f(position);

        float playerSize = 0.005f;  // ✅ Menší hitbox hráče pro snadnější obcházení

        // ✅ Otestujeme osu X - pohybujeme se pouze pokud nevstoupíme do kolize
        newPosition.x += delta.x;
        if (!collidesWith(newPosition.x, position.z, objX, objZ, width, height, playerSize)) {
            position.x = newPosition.x;
        }

        // ✅ Otestujeme osu Z - pohybujeme se pouze pokud nevstoupíme do kolize
        newPosition.set(position);
        newPosition.z += delta.z;
        if (!collidesWith(position.x, newPosition.z, objX, objZ, width, height, playerSize)) {
            position.z = newPosition.z;
        }

        // ✅ Umožníme volný pohyb nahoru/dolů (létání ignoruje kolize)
        position.y += delta.y;
    }


    public boolean canMoveTo(float newX, float newZ, Maze3D maze) {
        if (this.maze == null) {
            System.err.println("ERROR: Maze is NULL in canMoveTo()!");
            return false;  // ⚠️ Vrátíme false, aby hra nespadla
        }
        float playerSize = 0.2f; // 🔥 Zvýšíme hitbox hráče (dříve 0.005f)

        int cellX1 = (int) ((newX - playerSize - maze.getMazeOffsetX()) / maze.getCellSize());
        int cellX2 = (int) ((newX + playerSize - maze.getMazeOffsetX()) / maze.getCellSize());

        int cellZ1 = (int) ((newZ - playerSize - maze.getMazeOffsetZ()) / maze.getCellSize());
        int cellZ2 = (int) ((newZ + playerSize - maze.getMazeOffsetZ()) / maze.getCellSize());

        // ✅ Ověříme všechny čtyři body (levý dolní, pravý dolní, levý horní, pravý horní)
        return maze.getCell(cellZ1, cellX1) == 0 &&
                maze.getCell(cellZ1, cellX2) == 0 &&
                maze.getCell(cellZ2, cellX1) == 0 &&
                maze.getCell(cellZ2, cellX2) == 0;
    }


    private boolean collidesWithCube(float px, float py, float pz) {
        float size = 1.0f; // Velikost krychle
        float x = 0f, y = -0.5f, z = -3f; // Pozice krychle
        float buffer = 0.2f; // 🔥 Extra prostor kolem krychle

        return (px > x - buffer && px < x + size + buffer &&
                py > y - buffer && py < y + size + buffer &&
                pz > z - size - buffer && pz < z + buffer);
    }



    public void jump() {
        if (!isJumping) {  // ✅ Skáče jen pokud je na zemi
            velocityY = JUMP_FORCE;
            isJumping = true;
        }
    }
    public void toggleFlyMode() {
        isFlying = !isFlying;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void update() {
        System.out.println("Update kamera: " + position);
        if (isJumping) {
            velocityY += GRAVITY;  // ✅ Simulace gravitace
            position.y += velocityY;

            if (position.y <= GROUND_LEVEL) {  // ✅ Zabrání propadnutí pod podlahu
                position.y = GROUND_LEVEL;
                velocityY = 0;
                isJumping = false;
            }
        }
    }

    public void rotate(float pitchDelta, float yawDelta) {
        pitch += pitchDelta;
        yaw += yawDelta;

        // Omez rozsah pohledu nahoru/dolů
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;
    }

    public Matrix4f getViewMatrix() {
        Vector3f front = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch))
        );

        Vector3f target = new Vector3f(position).add(front);
        return new Matrix4f().lookAt(position, target, new Vector3f(0, 1, 0));
    }



    public float getYaw() {
        return yaw;
    }


    private boolean collidesWith(float px, float pz, float objX, float objZ, float width, float height, float playerSize) {
        return (px + playerSize > objX - width / 2 && px - playerSize < objX + width / 2 &&
                pz + playerSize > objZ - height / 2 && pz - playerSize < objZ + height / 2);
    }



    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setMaze(Maze3D maze) {
        this.maze = maze;
    }
}