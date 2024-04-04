package necesse.gfx.ui;

import java.util.function.Function;

public enum ButtonColor {
   BASE((var0) -> {
      return var0.base;
   }),
   GREEN((var0) -> {
      return var0.green;
   }),
   YELLOW((var0) -> {
      return var0.yellow;
   }),
   RED((var0) -> {
      return var0.red;
   });

   public final Function<ButtonColorTextures, ButtonStateTextures> colorGetter;

   private ButtonColor(Function var3) {
      this.colorGetter = var3;
   }

   // $FF: synthetic method
   private static ButtonColor[] $values() {
      return new ButtonColor[]{BASE, GREEN, YELLOW, RED};
   }
}
