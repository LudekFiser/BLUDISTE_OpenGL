package graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBTTBakedChar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBTTBakedChar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;

public class TextRenderer {
    private static final int FONT_BITMAP_WIDTH = 512;
    private static final int FONT_BITMAP_HEIGHT = 512;
    private static final int FIRST_CHAR = 32;
    private static final int NUM_CHARS = 96;

    private STBTTBakedChar.Buffer charData;
    private int textureID;

    public TextRenderer() {
        charData = STBTTBakedChar.malloc(NUM_CHARS);

        ByteBuffer fontBuffer;
        try {
            byte[] fontData = Files.readAllBytes(Paths.get("assets/fonts/menu.ttf"));
            fontBuffer = BufferUtils.createByteBuffer(fontData.length);
            fontBuffer.put(fontData);
            fontBuffer.flip();
        } catch (IOException e) {
            throw new RuntimeException("❌ Chyba: Fontový soubor nebyl nalezen!", e);
        }

        ByteBuffer bitmap = BufferUtils.createByteBuffer(FONT_BITMAP_WIDTH * FONT_BITMAP_HEIGHT);
        STBTruetype.stbtt_BakeFontBitmap(fontBuffer, 48, bitmap, FONT_BITMAP_WIDTH, FONT_BITMAP_HEIGHT, FIRST_CHAR, charData);

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, FONT_BITMAP_WIDTH, FONT_BITMAP_HEIGHT, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    }

    public void renderText(String text, float x, float y, float r, float g, float b) {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glColor4f(r, g, b, 1.0f);

        glBegin(GL_QUADS);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xBuf = stack.floats(x);
            FloatBuffer yBuf = stack.floats(y);

            for (char c : text.toCharArray()) {
                if (c < FIRST_CHAR || c >= FIRST_CHAR + NUM_CHARS) continue;

                STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
                STBTruetype.stbtt_GetBakedQuad(charData, FONT_BITMAP_WIDTH, FONT_BITMAP_HEIGHT, c - FIRST_CHAR, xBuf, yBuf, quad, true);

                glTexCoord2f(quad.s0(), quad.t0());
                glVertex2f(quad.x0(), quad.y0());

                glTexCoord2f(quad.s1(), quad.t0());
                glVertex2f(quad.x1(), quad.y0());

                glTexCoord2f(quad.s1(), quad.t1());
                glVertex2f(quad.x1(), quad.y1());

                glTexCoord2f(quad.s0(), quad.t1());
                glVertex2f(quad.x0(), quad.y1());

                quad.free();
            }
        }

        glEnd();
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }
}
