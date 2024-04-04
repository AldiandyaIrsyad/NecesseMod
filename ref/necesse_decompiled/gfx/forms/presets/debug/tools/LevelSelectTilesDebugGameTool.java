package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.control.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;

public abstract class LevelSelectTilesDebugGameTool extends MouseDebugGameTool {
   private LevelSelectTilesGameTool levelSelectGameTool;

   public LevelSelectTilesDebugGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.onLeftEvent((var0) -> {
         return true;
      }, "Select area");
      this.levelSelectGameTool = new LevelSelectTilesGameTool(-100) {
         public Level getLevel() {
            return LevelSelectTilesDebugGameTool.this.getLevel();
         }

         public int getMouseX() {
            return LevelSelectTilesDebugGameTool.this.getMouseX();
         }

         public int getMouseY() {
            return LevelSelectTilesDebugGameTool.this.getMouseY();
         }

         public void onTileSelection(int var1, int var2, int var3, int var4) {
            LevelSelectTilesDebugGameTool.this.onTileSelection(var1, var2, var3, var4);
         }

         public void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
            LevelSelectTilesDebugGameTool.this.drawTileSelection(var1, var2, var3, var4, var5, var6);
         }

         public GameTooltips getTooltips() {
            return LevelSelectTilesDebugGameTool.this.getTooltips();
         }
      };
   }

   public void init() {
      this.levelSelectGameTool.init();
   }

   public boolean inputEvent(InputEvent var1) {
      return this.levelSelectGameTool.inputEvent(var1) ? true : super.inputEvent(var1);
   }

   public abstract void onTileSelection(int var1, int var2, int var3, int var4);

   public abstract void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);
}
