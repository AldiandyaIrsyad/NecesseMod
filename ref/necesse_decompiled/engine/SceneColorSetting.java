package necesse.engine;

import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.shader.ColorShader;

public enum SceneColorSetting {
   Normal(new LocalMessage("settingsui", "colornormal"), (var0) -> {
      var0.pass1f("brightness", 1.0F);
      var0.pass1f("contrast", 1.0F);
      var0.pass1f("gamma", 1.0F);
      var0.pass1f("vibrance", 0.0F);
      var0.pass1f("vibranceRedBalance", 1.0F);
      var0.pass1f("vibranceGreenBalance", 1.0F);
      var0.pass1f("vibranceBlueBalance", 1.0F);
      var0.pass1f("red", 1.0F);
      var0.pass1f("green", 1.0F);
      var0.pass1f("blue", 1.0F);
   }),
   Vibrant(new LocalMessage("settingsui", "colorvibrant"), (var0) -> {
      var0.pass1f("brightness", 1.0F);
      var0.pass1f("contrast", 1.1F);
      var0.pass1f("gamma", 1.0F);
      var0.pass1f("vibrance", 0.2F);
      var0.pass1f("vibranceRedBalance", 1.0F);
      var0.pass1f("vibranceGreenBalance", 1.0F);
      var0.pass1f("vibranceBlueBalance", 1.0F);
      var0.pass1f("red", 1.0F);
      var0.pass1f("green", 1.0F);
      var0.pass1f("blue", 1.0F);
   });

   public final GameMessage displayName;
   public final Consumer<ColorShader> shaderSetup;

   private SceneColorSetting(GameMessage var3, Consumer var4) {
      this.displayName = var3;
      this.shaderSetup = var4;
   }

   // $FF: synthetic method
   private static SceneColorSetting[] $values() {
      return new SceneColorSetting[]{Normal, Vibrant};
   }
}
