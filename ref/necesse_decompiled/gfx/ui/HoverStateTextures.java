package necesse.gfx.ui;

import java.io.FileNotFoundException;
import necesse.gfx.gameTexture.GameTexture;

public class HoverStateTextures {
   public GameTexture active;
   public GameTexture highlighted;

   public HoverStateTextures(GameInterfaceStyle var1, String var2) {
      this.active = var1.fromFile(var2);
      this.highlighted = this.fromFile(var1, var2, "_highlighted", this.active);
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
