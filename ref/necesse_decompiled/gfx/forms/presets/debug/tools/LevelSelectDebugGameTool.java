package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.control.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;

public abstract class LevelSelectDebugGameTool extends MouseDebugGameTool {
   private LevelSelectGameTool levelSelectGameTool;

   public LevelSelectDebugGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.onLeftEvent((var0) -> {
         return true;
      }, "Select area");
      this.levelSelectGameTool = new LevelSelectGameTool(-100) {
         public Level getLevel() {
            return LevelSelectDebugGameTool.this.getLevel();
         }

         public int getMouseX() {
            return LevelSelectDebugGameTool.this.getMouseX();
         }

         public int getMouseY() {
            return LevelSelectDebugGameTool.this.getMouseY();
         }

         public void onSelection(int var1, int var2, int var3, int var4) {
            LevelSelectDebugGameTool.this.onSelection(var1, var2, var3, var4);
         }

         public void drawSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
            LevelSelectDebugGameTool.this.drawSelection(var1, var2, var3, var4, var5, var6);
         }

         public GameTooltips getTooltips() {
            return LevelSelectDebugGameTool.this.getTooltips();
         }
      };
   }

   public void init() {
      this.levelSelectGameTool.init();
   }

   public boolean inputEvent(InputEvent var1) {
      return this.levelSelectGameTool.inputEvent(var1) ? true : super.inputEvent(var1);
   }

   public abstract void onSelection(int var1, int var2, int var3, int var4);

   public abstract void drawSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);
}
