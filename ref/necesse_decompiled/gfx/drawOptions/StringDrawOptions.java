package necesse.gfx.drawOptions;

import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class StringDrawOptions {
   public final FontOptions fontOptions;
   private String str;

   public StringDrawOptions(FontOptions var1, String var2) {
      this.fontOptions = var1;
      this.string(var2);
   }

   public StringDrawOptions string(String var1) {
      this.str = var1;
      return this;
   }

   public StringDrawOptions append(String var1) {
      this.str = this.str + var1;
      return this;
   }

   public DrawOptions pos(int var1, int var2) {
      return () -> {
         FontManager.bit.drawString((float)var1, (float)var2, this.str, this.fontOptions);
      };
   }

   public DrawOptions posCenterX(int var1, int var2) {
      return () -> {
         int var3 = FontManager.bit.getWidthCeil(this.str, this.fontOptions) / 2;
         FontManager.bit.drawString((float)(var1 - var3), (float)var2, this.str, this.fontOptions);
      };
   }

   public DrawOptions posCenter(int var1, int var2) {
      return () -> {
         int var3 = FontManager.bit.getWidthCeil(this.str, this.fontOptions) / 2;
         int var4 = FontManager.bit.getHeightCeil(this.str, this.fontOptions) / 2;
         FontManager.bit.drawString((float)(var1 - var3), (float)(var2 - var4), this.str, this.fontOptions);
      };
   }

   public void draw(int var1, int var2) {
      this.pos(var1, var2).draw();
   }

   public void drawCenterX(int var1, int var2) {
      this.posCenterX(var1, var2).draw();
   }

   public void drawCenter(int var1, int var2) {
      this.posCenter(var1, var2).draw();
   }
}
