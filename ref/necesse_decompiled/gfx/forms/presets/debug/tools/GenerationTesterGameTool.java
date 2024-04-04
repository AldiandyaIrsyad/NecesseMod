package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;

public class GenerationTesterGameTool extends MouseDebugGameTool {
   private boolean setServer;

   public GenerationTesterGameTool(DebugForm var1) {
      super(var1, "Generation tester");
      this.onLeftClick((var1x) -> {
         long var2 = GameRandom.globalRandom.nextLong();
         Level var4 = this.getLevel();
         if (var4 != null) {
            GenerationTools.placeRandomObjectVeinOnTile(var4, new GameRandom(var2), this.getMouseTileX(), this.getMouseTileY(), 8, 14, TileRegistry.getTileID("grasstile"), ObjectRegistry.getObjectID("stonewall"), 0.25F, false);
         }

         Level var5 = this.getServerLevel();
         if (var5 != null && this.setServer) {
            GenerationTools.placeRandomObjectVeinOnTile(var5, new GameRandom(var2), this.getMouseTileX(), this.getMouseTileY(), 8, 14, TileRegistry.getTileID("grasstile"), ObjectRegistry.getObjectID("stonewall"), 0.25F, false);
         }

         return true;
      }, "Test generation");
      this.onRightClick((var1x) -> {
         var1.client.reloadMap();
         return true;
      }, "Reload client map");
      this.onScroll((var2) -> {
         this.setServer = !this.setServer;
         var1.client.setMessage("Override server: " + this.setServer, new Color(255, 255, 255));
         return true;
      }, "Toggle server override");
   }

   public void init() {
      this.setServer = false;
   }
}
