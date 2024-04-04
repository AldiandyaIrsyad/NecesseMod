package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;

public class TextureDrawOptions extends TextureDrawOptionsMods implements DrawOptions {
   public final GameTexture texture;

   private TextureDrawOptions(GameTexture var1, TextureDrawOptionsObj var2) {
      super(var2);
      this.texture = var1;
   }

   private TextureDrawOptions(GameTexture var1) {
      super(new TextureDrawOptionsObj(var1, var1.getWidth(), var1.getHeight()));
      this.texture = var1;
   }

   protected TextureDrawOptions(TextureDrawOptions var1) {
      super(var1.opts);
      this.texture = var1.texture;
   }

   public TextureDrawOptions copy() {
      return new TextureDrawOptions(this.texture, new TextureDrawOptionsObj(this.opts));
   }

   public int getWidth() {
      return this.opts.width;
   }

   public int getHeight() {
      return this.opts.height;
   }

   public void draw() {
      this.opts.draw(true, true);
   }

   public static float pixel(int var0, int var1) {
      return (float)var0 / (float)var1;
   }

   public static float pixel(int var0, int var1, int var2) {
      return pixel(var0 * var1, var2);
   }

   public static float pixel(int var0, int var1, int var2, int var3) {
      return pixel(var0 * var2 + var1, var3);
   }

   public static TextureDrawOptionsStart initDraw(GameTexture var0) {
      return new TextureDrawOptionsStart(new TextureDrawOptions(var0));
   }
}
