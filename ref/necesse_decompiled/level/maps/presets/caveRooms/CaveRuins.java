package necesse.level.maps.presets.caveRooms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins extends Preset {
   public static ArrayList<CaveRuinGetter> caveRuinGetters = new ArrayList(Arrays.asList(CaveRuins1::new, CaveRuins2::new, CaveRuins3::new, CaveRuins4::new, CaveRuins5::new, CaveRuins6::new, CaveRuins7::new, CaveRuins8::new, CaveRuins9::new, CaveRuins10::new));

   public CaveRuins(String var1, GameRandom var2, WallSet var3, FurnitureSet var4, String var5) {
      super(var1);
      this.applyRuinsLogic(var2, var3, var4, var5);
   }

   public CaveRuins(int var1, int var2) {
      super(var1, var2);
   }

   public void applyRuinsLogic(GameRandom var1, WallSet var2, FurnitureSet var3, String var4) {
      applyRuinsLogic(this, var1, var2, var3, var4);
   }

   public static void applyRuinsLogic(Preset var0, GameRandom var1, WallSet var2, FurnitureSet var3, String var4) {
      var2.replacePreset(WallSet.wood, var0);
      var3.replacePreset(FurnitureSet.oak, var0);
      var0.replaceTile(TileRegistry.stoneFloorID, TileRegistry.getTileID(var4));
      float var5 = var1.getFloatBetween(0.4F, 0.8F);
      float var6 = var1.getFloatBetween(var5 - 0.05F, var5 + 0.2F);

      for(int var7 = 0; var7 < var0.width; ++var7) {
         for(int var8 = 0; var8 < var0.height; ++var8) {
            boolean var9 = true;
            int var10 = var0.getObject(var7, var8);
            if (var10 != -1) {
               if (var10 == var3.chest) {
                  continue;
               }

               GameObject var11 = ObjectRegistry.getObject(var10);
               MultiTile var12 = var11.getMultiTile(var0.getObjectRotation(var7, var8));
               if (var12.isMaster && var1.getChance(var5)) {
                  var12.streamIDs(var7, var8).forEach((var1x) -> {
                     if (var0.getObject(var1x.tileX, var1x.tileY) == (Integer)var1x.value) {
                        var0.setObject(var1x.tileX, var1x.tileY, -1);
                     }

                  });
               } else {
                  var9 = false;
               }
            }

            if (var9) {
               int var13 = var0.getTile(var7, var8);
               if (var13 != -1 && var1.getChance(var6)) {
                  var0.setTile(var7, var8, -1);
               }
            }
         }
      }

   }

   @FunctionalInterface
   public interface CaveRuinGetter {
      Preset get(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6);
   }
}
