package necesse.gfx.ui;

import java.awt.Color;
import java.util.function.Function;
import necesse.gfx.gameTexture.GameTexture;

public class ButtonIcon {
   public GameTexture texture;
   public Function<ButtonState, Color> colorGetter;

   public ButtonIcon(GameInterfaceStyle var1, String var2, Function<ButtonState, Color> var3) {
      this.texture = var1.fromFile(var2);
      this.colorGetter = var3;
   }

   public ButtonIcon(GameInterfaceStyle var1, String var2, Color var3) {
      this(var1, var2, (var1x) -> {
         return var3;
      });
   }

   public ButtonIcon(GameInterfaceStyle var1, String var2, boolean var3) {
      this(var1, var2, var3 ? (var1x) -> {
         return (Color)var1x.textColorGetter.apply(var1);
      } : (var1x) -> {
         return (Color)var1x.elementColorGetter.apply(var1);
      });
   }

   public ButtonIcon(GameInterfaceStyle var1, String var2) {
      this(var1, var2, true);
   }
}
