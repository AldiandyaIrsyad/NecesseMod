package necesse.gfx.ui;

import java.awt.Color;
import necesse.gfx.gameTexture.GameTexture;

public class ButtonColorTextures {
   public ButtonStateTextures base;
   public ButtonStateTextures green;
   public ButtonStateTextures yellow;
   public ButtonStateTextures red;

   public ButtonColorTextures(GameInterfaceStyle var1, String var2) {
      this.base = new ButtonStateTextures(var1, var2);
      this.green = new ButtonStateTextures(var1, var2 + "_green");
      this.yellow = new ButtonStateTextures(var1, var2 + "_yellow");
      this.red = new ButtonStateTextures(var1, var2 + "_red");
   }

   public GameTexture getButtonTexture(ButtonColor var1, ButtonState var2) {
      return (GameTexture)var2.textureGetter.apply((ButtonStateTextures)var1.colorGetter.apply(this));
   }

   public GameTexture getButtonDownTexture(ButtonColor var1, ButtonState var2) {
      return (GameTexture)var2.downTextureGetter.apply((ButtonStateTextures)var1.colorGetter.apply(this));
   }

   public Color getButtonColor(GameInterfaceStyle var1, ButtonState var2) {
      return (Color)var2.elementColorGetter.apply(var1);
   }

   public Color getTextColor(GameInterfaceStyle var1, ButtonState var2) {
      return (Color)var2.textColorGetter.apply(var1);
   }
}
