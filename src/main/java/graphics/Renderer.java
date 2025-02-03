package graphics;

import org.lwjgl.opengl.GL11;

public class Renderer {
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
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glColor3f(0.0f, 1.0f, 0.0f);  // ✅ Zelená podlaha

        // ✅ Zvětšená podlaha pro lepší testování
        GL11.glVertex3f(-50.0f, -1.0f, -50.0f);
        GL11.glVertex3f(50.0f, -1.0f, -50.0f);
        GL11.glVertex3f(50.0f, -1.0f, 50.0f);
        GL11.glVertex3f(-50.0f, -1.0f, 50.0f);

        GL11.glEnd();
    }

    public void renderMaze(int[][] maze) {
        GL11.glColor3f(1.0f, 1.0f, 1.0f);  // Bílé zdi
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                if (maze[y][x] == 1) {  // Je zde zeď?
                    drawWall(x, y);
                }
            }
        }
    }

    private void drawWall(float x, float y) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glVertex3f(x, 0, y);
        GL11.glVertex3f(x + 1, 0, y);
        GL11.glVertex3f(x + 1, 1, y);
        GL11.glVertex3f(x, 1, y);
        GL11.glEnd();
    }

    public void renderSolution(int[][] solution) {
        GL11.glColor3f(0.0f, 0.0f, 1.0f);  // Modrá barva cesty
        for (int y = 0; y < solution.length; y++) {
            for (int x = 0; x < solution[0].length; x++) {
                if (solution[y][x] == 1) {
                    drawPath(x, y);
                }
            }
        }
    }

    private void drawPath(float x, float y) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(x, 0.1f, y);
        GL11.glVertex3f(x + 1, 0.1f, y);
        GL11.glVertex3f(x + 1, 0.1f, y + 1);
        GL11.glVertex3f(x, 0.1f, y + 1);
        GL11.glEnd();
    }
}
