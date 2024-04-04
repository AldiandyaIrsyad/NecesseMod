package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.presets.debug.DebugForm;

public class SoundTestGameTool extends MouseDebugGameTool {
   public SoundTestGameTool(DebugForm var1) {
      super(var1, "Sound test");
      this.onLeftClick((var2) -> {
         int var3 = this.getMouseX();
         int var4 = this.getMouseY();
         PlayerMob var5 = var1.client.getPlayer();
         Screen.playSound(GameResources.tap, SoundEffect.effect((float)var3, (float)var4));
         var1.client.chat.addMessage("Sound offset: " + (var3 - var5.getX()) + ", " + (var4 - var5.getY()));
         return true;
      }, "Play sound effect here");
      this.onRightClick((var1x) -> {
         Screen.clearGameTool(this);
         return true;
      }, "Cancel");
   }

   public void init() {
   }
}
