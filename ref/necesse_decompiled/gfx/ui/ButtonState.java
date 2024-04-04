package necesse.gfx.ui;

import java.awt.Color;
import java.util.function.Function;
import necesse.gfx.gameTexture.GameTexture;

public enum ButtonState {
   ACTIVE((var0) -> {
      return var0.active;
   }, (var0) -> {
      return var0.downActive;
   }, (var0) -> {
      return var0.activeElementColor;
   }, (var0) -> {
      return var0.activeButtonTextColor;
   }),
   HIGHLIGHTED((var0) -> {
      return var0.highlighted;
   }, (var0) -> {
      return var0.downHighlighted;
   }, (var0) -> {
      return var0.highlightElementColor;
   }, (var0) -> {
      return var0.highlightButtonTextColor;
   }),
   INACTIVE((var0) -> {
      return var0.inactive;
   }, (var0) -> {
      return var0.downInactive;
   }, (var0) -> {
      return var0.inactiveElementColor;
   }, (var0) -> {
      return var0.inactiveButtonTextColor;
   });

   public final Function<ButtonStateTextures, GameTexture> textureGetter;
   public final Function<ButtonStateTextures, GameTexture> downTextureGetter;
   public final Function<GameInterfaceStyle, Color> elementColorGetter;
   public final Function<GameInterfaceStyle, Color> textColorGetter;

   private ButtonState(Function var3, Function var4, Function var5, Function var6) {
      this.textureGetter = var3;
      this.downTextureGetter = var4;
      this.elementColorGetter = var5;
      this.textColorGetter = var6;
   }

   // $FF: synthetic method
   private static ButtonState[] $values() {
      return new ButtonState[]{ACTIVE, HIGHLIGHTED, INACTIVE};
   }
}
