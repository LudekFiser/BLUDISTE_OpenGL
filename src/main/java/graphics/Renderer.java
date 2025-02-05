package graphics;

import camera.Camera;
import maze.Maze3D;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import transforms.Vec3D;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengles.GLES20.GL_TRIANGLES;
import static org.lwjgl.opengles.GLES32.GL_QUADS;
import static org.lwjglx.debug.GLmetadata.glBegin;
import static org.lwjglx.debug.opengl.GL11.glEnd;
import static org.lwjglx.debug.opengl.GL11.glVertex3f;

public class Renderer {
    private int floorTextureID;
    private Camera camera;

    private int vao, vbo;
    private Maze3D maze;
    public Renderer(Camera camera) {
        floorTextureID = TextureLoader.loadTexture("assets/textures/floor/PavingStones089_4K-PNG_Color.png");
        this.camera = camera;
        maze = new Maze3D(60, 60, camera);
        vao = GL30.glGenVertexArrays();
        vbo = GL30.glGenBuffers();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);


    }

    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        maze.render(this);  // ✅ Tímto zavoláš `drawWall()` pro každou stěnu
        drawFloor();
        drawCube();
    }

    private void drawCube() {
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glVertex3f(-0.5f, -0.5f, -1.0f);
        GL11.glVertex3f(0.5f, -0.5f, -1.0f);
        GL11.glVertex3f(0.5f, 0.5f, -1.0f);
        GL11.glVertex3f(-0.5f, 0.5f, -1.0f);

        GL11.glEnd();
    }

    private void drawFloor() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, floorTextureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(1.0f, 1.0f, 1.0f); // Reset barvy

        float scale = 20.0f;  // 🔥 Zvýšíme opakování textury

        // Souřadnice textury
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(-50.0f, -1.0f, -50.0f);

        GL11.glTexCoord2f(scale, 0);
        GL11.glVertex3f(50.0f, -1.0f, -50.0f);

        GL11.glTexCoord2f(scale, scale);
        GL11.glVertex3f(50.0f, -1.0f, 50.0f);

        GL11.glTexCoord2f(0, scale);
        GL11.glVertex3f(-50.0f, -1.0f, 50.0f);

        GL11.glEnd();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public void drawTriangle(Vec3D v1, Vec3D v2, Vec3D v3) {
        float[] vertices = {
                (float) v1.getX(), (float) v1.getY(), (float) v1.getZ(),
                (float) v2.getX(), (float) v2.getY(), (float) v2.getZ(),
                (float) v3.getX(), (float) v3.getY(), (float) v3.getZ()
        };

        // Vytvoříme buffer
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        // BIND VAO
        GL30.glBindVertexArray(vao);

        // BIND VBO
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);

        // ENABLE VERTEX ATTRIBUTES
        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);

        // DRAW TRIANGLE
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);

        // CLEANUP
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        MemoryUtil.memFree(vertexBuffer);
    }

    public void drawQuad(Vec3D v1, Vec3D v2, Vec3D v3, Vec3D v4) {
        drawTriangle(v1, v2, v3);
        drawTriangle(v1, v3, v4);
    }

}