package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CobwebObject extends GrassObject {
   public CobwebObject() {
      super("cobweb", -1);
      this.mapColor = new Color(146, 143, 135);
      this.displayMapTooltip = true;
      this.weaveAmount = 0.05F;
      this.extraWeaveSpace = 32;
      this.randomYOffset = 4.0F;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         return var1.getTileID(var2, var3) != TileRegistry.spiderNestID ? "wrongtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.spiderNestID;
   }

   public int getLightLevelMod(Level var1, int var2, int var3) {
      return 30;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "cobwebSetup", (Runnable)(() -> {
         Integer[] var7x = var3.getAdjacentObjectsInt(var4, var5);
         int var8 = 0;
         Integer[] var9 = var7x;
         int var10 = var7x.length;

         int var11;
         for(var11 = 0; var11 < var10; ++var11) {
            Integer var12 = var9[var11];
            if (var12 == this.getID()) {
               ++var8;
            }
         }

         byte var14;
         byte var15;
         if (var8 < 5) {
            var14 = 4;
            var15 = 7;
         } else {
            var14 = 0;
            var15 = 3;
         }

         var11 = var7.getTileDrawX(var4);
         int var16 = var7.getTileDrawY(var5);
         GameLight var13 = var3.getLightLevel(var4, var5);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 6, -5, 0, var14, var15);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 26, -5, 1, var14, var15);
      }));
   }

   public ModifierValue<Float> getSlowModifier(Mob var1) {
      return !var1.isHostile && !var1.isFlying() ? new ModifierValue(BuffModifiers.SLOW, 0.5F) : super.getSpeedModifier(var1);
   }
}
