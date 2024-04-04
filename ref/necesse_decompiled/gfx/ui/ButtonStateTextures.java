package necesse.gfx.ui;

import java.io.FileNotFoundException;
import necesse.gfx.gameTexture.GameTexture;

public class ButtonStateTextures {
   public GameTexture active;
   public GameTexture inactive;
   public GameTexture highlighted;
   public GameTexture downActive;
   public GameTexture downInactive;
   public GameTexture downHighlighted;

   public ButtonStateTextures(GameInterfaceStyle var1, String var2) {
      this.active = var1.fromFile(var2);
      this.inactive = this.fromFile(var1, var2, "_inactive", this.active);
      this.highlighted = this.fromFile(var1, var2, "_highlighted", this.active);
      this.downActive = this.fromFile(var1, var2, "_down", this.active);
      this.downInactive = this.fromFile(var1, var2, "_down_inactive", this.downActive);
      this.downHighlighted = this.fromFile(var1, var2, "_down_highlighted", this.downActive);
   }

   protected GameTexture fromFile(GameInterfaceStyle var1, String var2, String var3, GameTexture var4) {
      try {
         return var1.fromFileRaw(var2 + var3);
      } catch (FileNotFoundException var8) {
         try {
            return var1.fromDefaultFileRaw(var2 + var3);
         } catch (FileNotFoundException var7) {
            return var4;
         }
      }
   }
}
