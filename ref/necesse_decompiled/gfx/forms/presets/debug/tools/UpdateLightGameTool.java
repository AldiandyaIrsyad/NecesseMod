package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.control.MouseWheelBuffer;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;

public class UpdateLightGameTool extends LevelSelectTilesDebugGameTool {
   private LevelsApplier levelsApplier;
   private MouseWheelBuffer wheelBuffer;

   public UpdateLightGameTool(DebugForm var1) {
      super(var1, "Update light");
      this.levelsApplier = UpdateLightGameTool.LevelsApplier.ONLY_CLIENT;
      this.wheelBuffer = new MouseWheelBuffer(false);
   }

   public void init() {
      super.init();
      this.onScroll((var1) -> {
         this.wheelBuffer.add(var1);
         LevelsApplier[] var2 = UpdateLightGameTool.LevelsApplier.values();
         this.levelsApplier = var2[Math.floorMod(this.levelsApplier.ordinal() + this.wheelBuffer.useAllScrollY(), var2.length)];
         this.setScrollUsage(this.levelsApplier.displayName);
         return true;
      }, this.levelsApplier.displayName);
   }

   public void onTileSelection(int var1, int var2, int var3, int var4) {
      this.levelsApplier.applier.accept(this, (var4x) -> {
         var4x.lightManager.updateStaticLight(var1, var2, var3, var4, true);
      });
      int var5 = var3 - var1 + 1;
      int var6 = var4 - var2 + 1;
      this.parent.client.setMessage("Updated light in a " + var5 + "x" + var6 + " area", Color.WHITE);
   }

   public void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
      Screen.initQuadDraw((var5 - var3 + 1) * 32, (var6 - var4 + 1) * 32).color(1.0F, 1.0F, 1.0F, 0.2F).draw(var1.getTileDrawX(var3), var1.getTileDrawY(var4));
      HUD.tileBoundOptions(var1, var3, var4, var5, var6).draw();
   }

   private static enum LevelsApplier {
      ONLY_CLIENT("Only client level", (var0, var1) -> {
         Level var2 = var0.getLevel();
         if (var2 != null) {
            var1.accept(var2);
         }

      }),
      ONLY_SERVER("Only server level", (var0, var1) -> {
         Level var2 = var0.getServerLevel();
         if (var2 != null) {
            var1.accept(var2);
         }

      }),
      BOTH("Both client and server level", (var0, var1) -> {
         Level var2 = var0.getLevel();
         if (var2 != null) {
            var1.accept(var2);
         }

         Level var3 = var0.getServerLevel();
         if (var3 != null) {
            var1.accept(var3);
         }

      });

      public final BiConsumer<UpdateLightGameTool, Consumer<Level>> applier;
      public final String displayName;

      private LevelsApplier(String var3, BiConsumer var4) {
         this.displayName = var3;
         this.applier = var4;
      }

      // $FF: synthetic method
      private static LevelsApplier[] $values() {
         return new LevelsApplier[]{ONLY_CLIENT, ONLY_SERVER, BOTH};
      }
   }
}
