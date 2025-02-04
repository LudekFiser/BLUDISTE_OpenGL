package graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.Random;

public class Renderer {
    private int floorTextureID;
    private int normalMapID;
    private int roughnessMapID;

    public Renderer() {
        floorTextureID = TextureLoader.loadTexture("assets/textures/floor/PavingStones089_4K-PNG_Color.png");
        //normalMapID = TextureLoader.loadTexture("assets/textures/floor/PavingStones089_4K-PNG_NormalGL.png");
        //roughnessMapID = TextureLoader.loadTexture("assets/textures/floor/PavingStones089_4K-PNG_Roughness.png");
    }

    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


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

}




/*
public class Renderer {
    private int floorTextureID;
    private static final int GRID_SIZE = 10;  // Velikost mřížky podlahy
    private static final float TILE_SIZE = 10.0f;  // Velikost dlaždice
    private static final float UV_SCALE = 1.0f; // Měřítko UV
    private float[][] randomRotations;
    private float[][] randomOffsetsU;
    private float[][] randomOffsetsV;

    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        drawFloor();
        drawCube();
    }

    public Renderer() {
        floorTextureID = TextureLoader.loadTexture("assets/textures/floor/PavingStones089_4K-PNG_Color.png");

        // 🔥 Předgenerování random hodnot (fixní pro celou hru)
        randomRotations = new float[GRID_SIZE * 2][GRID_SIZE * 2];
        randomOffsetsU = new float[GRID_SIZE * 2][GRID_SIZE * 2];
        randomOffsetsV = new float[GRID_SIZE * 2][GRID_SIZE * 2];

        Random rand = new Random();
        for (int x = 0; x < GRID_SIZE * 2; x++) {
            for (int z = 0; z < GRID_SIZE * 2; z++) {
                randomRotations[x][z] = rand.nextInt(4) * 90.0f;  // 0°, 90°, 180°, 270°
                randomOffsetsU[x][z] = rand.nextFloat() * 0.5f;
                randomOffsetsV[x][z] = rand.nextFloat() * 0.5f;
            }
        }
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
        GL11.glColor3f(1.0f, 1.0f, 1.0f);

        for (int x = -GRID_SIZE; x < GRID_SIZE; x++) {
            for (int z = -GRID_SIZE; z < GRID_SIZE; z++) {
                int gridX = x + GRID_SIZE;
                int gridZ = z + GRID_SIZE;

                float randomRotation = randomRotations[gridX][gridZ];
                float randomOffsetU = randomOffsetsU[gridX][gridZ];
                float randomOffsetV = randomOffsetsV[gridX][gridZ];

                float u1 = randomOffsetU, v1 = randomOffsetV;
                float u2 = u1 + UV_SCALE, v2 = v1 + UV_SCALE;

                // Pokud je rotace 90° nebo 270°, prohodíme UV souřadnice
                if (randomRotation == 90.0f || randomRotation == 270.0f) {
                    float temp = u1;
                    u1 = v1;
                    v1 = temp;

                    temp = u2;
                    u2 = v2;
                    v2 = temp;
                }

                // Kreslení dlaždice s pevně předgenerovanou rotací a posunem
                GL11.glTexCoord2f(u1, v1); GL11.glVertex3f(x * TILE_SIZE, -1.0f, z * TILE_SIZE);
                GL11.glTexCoord2f(u2, v1); GL11.glVertex3f((x + 1) * TILE_SIZE, -1.0f, z * TILE_SIZE);
                GL11.glTexCoord2f(u2, v2); GL11.glVertex3f((x + 1) * TILE_SIZE, -1.0f, (z + 1) * TILE_SIZE);
                GL11.glTexCoord2f(u1, v2); GL11.glVertex3f(x * TILE_SIZE, -1.0f, (z + 1) * TILE_SIZE);
            }
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

}*/