package necesse.gfx.gameFont;

public class TrueTypeGameFontSize {
   public final TrueTypeGameFontInfo info;
   public final int size;
   public final float fontSize;
   public final float lineGap;

   public TrueTypeGameFontSize(TrueTypeGameFontInfo var1, int var2) {
      this.info = var1;
      this.size = var2;
      this.fontSize = var1.getFontSize(var2);
      this.lineGap = var1.getFontSize(var2);
   }
}
