package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.sidebar.PresetFilterSidebarForm;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetCopyFilter;

public class PresetCopyGameTool extends LevelSelectTilesDebugGameTool {
   private PresetCopyFilter filter = new PresetCopyFilter();
   private PresetFilterSidebarForm filterForm;

   public PresetCopyGameTool(DebugForm var1) {
      super(var1, "Copy preset");
   }

   public void init() {
      super.init();
      if (this.filterForm != null) {
         this.filterForm.invalidate();
      }

      if (GlobalData.getCurrentState() instanceof MainGame) {
         this.filterForm = new PresetFilterSidebarForm("Copy filter", this.filter);
         ((MainGame)GlobalData.getCurrentState()).formManager.addSidebar(this.filterForm);
      }

   }

   public void onTileSelection(int var1, int var2, int var3, int var4) {
      Level var5 = this.getServerLevel();
      if (var5 == null) {
         var5 = this.getLevel();
      }

      if (var5 != null) {
         int var6 = var3 - var1 + 1;
         int var7 = var4 - var2 + 1;
         Preset var8 = Preset.copyFromLevel(var5, var1, var2, var6, var7);
         var8.replaceTile(TileRegistry.emptyID, -1);
         Screen.putClipboardDefault(var8.getSaveData(this.filter).getScript());
         this.parent.client.setMessage("Copied to clipboard", Color.WHITE);
      }

   }

   public void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
      Screen.initQuadDraw((var5 - var3 + 1) * 32, (var6 - var4 + 1) * 32).color(1.0F, 1.0F, 0.0F, 0.2F).draw(var1.getTileDrawX(var3), var1.getTileDrawY(var4));
      HUD.tileBoundOptions(var1, var3, var4, var5, var6).draw();
   }

   public void isCancelled() {
      super.isCancelled();
      if (this.filterForm != null) {
         this.filterForm.invalidate();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.filterForm != null) {
         this.filterForm.invalidate();
      }

   }
}
