package necesse.gfx.gameFont;

import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL11;

public class GameFontGlyphPositionTexture extends GameFontGlyphPosition {
   public final GameTexture texture;
   private final float x1;
   private final float x2;
   private final float y1;
   private final float y2;

   public GameFontGlyphPositionTexture(GameTexture var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.texture = var1;
      this.x1 = TextureDrawOptions.pixel(var2, var1.getWidth());
      this.x2 = TextureDrawOptions.pixel(var2 + var4, var1.getWidth());
      this.y1 = TextureDrawOptions.pixel(var3, var1.getHeight());
      this.y2 = TextureDrawOptions.pixel(var3 + var5, var1.getHeight());
   }

   public GameFontGlyphPositionTexture(GameTexture var1, GameFontGlyphPosition var2) {
      this(var1, var2.textureX, var2.textureY, var2.width, var2.height);
   }

   public void draw(float var1, float var2, float var3, float var4) {
      var1 += (float)this.drawXOffset;
      var2 += (float)this.drawYOffset;
      GL11.glBegin(7);
      GL11.glTexCoord2f(this.x1, this.y1);
      GL11.glVertex2f(var1, var2);
      GL11.glTexCoord2f(this.x1, this.y2);
      GL11.glVertex2f(var1, var2 + var4);
      GL11.glTexCoord2f(this.x2, this.y2);
      GL11.glVertex2f(var1 + var3, var2 + var4);
      GL11.glTexCoord2f(this.x2, this.y1);
      GL11.glVertex2f(var1 + var3, var2);
      GL11.glEnd();
   }

   public void drawNoBegin(float var1, float var2, float var3, float var4) {
      var1 += (float)this.drawXOffset;
      var2 += (float)this.drawYOffset;
      GL11.glTexCoord2f(this.x1, this.y1);
      GL11.glVertex2f(var1, var2);
      GL11.glTexCoord2f(this.x1, this.y2);
      GL11.glVertex2f(var1, var2 + var4);
      GL11.glTexCoord2f(this.x2, this.y2);
      GL11.glVertex2f(var1 + var3, var2 + var4);
      GL11.glTexCoord2f(this.x2, this.y1);
      GL11.glVertex2f(var1 + var3, var2);
   }
}
