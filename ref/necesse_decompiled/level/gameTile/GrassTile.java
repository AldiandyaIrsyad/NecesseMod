package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;

public class GrassTile extends TerrainSplatterTile {
   public static double growChance = GameMath.getAverageSuccessRuns(7000.0);
   public static double spreadChance = GameMath.getAverageSuccessRuns(850.0);
   private final GameRandom drawRandom;

   public GrassTile() {
      super(false, "grass");
      this.mapColor = new Color(66, 152, 66);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.04F, "grassseed")});
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      addSimulateGrow(var1, var2, var3, growChance, var4, "grass", var6, var7);
   }

   public static void addSimulateGrow(Level var0, int var1, int var2, double var3, long var5, String var7, SimulatePriorityList var8, boolean var9) {
      addSimulateGrow(var0, var1, var2, var3, var5, var7, (var0x, var1x, var2x, var3x, var4) -> {
         return var0x.canPlace(var1x, var2x, var3x, var4) == null;
      }, var8, var9);
   }

   public static void addSimulateGrow(Level var0, int var1, int var2, double var3, long var5, String var7, CanPlacePredicate var8, SimulatePriorityList var9, boolean var10) {
      if (var0.getObjectID(var1, var2) == 0) {
         double var11 = Math.max(1.0, GameMath.getRunsForSuccess(var3, GameRandom.globalRandom.nextDouble()));
         long var13 = (long)((double)var5 - var11);
         if (var13 > 0L) {
            GameObject var15 = ObjectRegistry.getObject(ObjectRegistry.getObjectID(var7));
            if (var8.check(var15, var0, var1, var2, 0)) {
               var9.add(var1, var2, var13, () -> {
                  if (var8.check(var15, var0, var1, var2, 0)) {
                     var15.placeObject(var0, var1, var2, 0);
                     if (var10) {
                        var0.sendObjectUpdatePacket(var1, var2);
                     }
                  }

               });
            }
         }
      }

   }

   public double spreadToDirtChance() {
      return spreadChance;
   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         if (var1.getObjectID(var2, var3) == 0 && GameRandom.globalRandom.getChance(growChance)) {
            GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("grass"));
            if (var4.canPlace(var1, var2, var3, 0) == null) {
               var4.placeObject(var1, var2, var3, 0);
               var1.sendObjectUpdatePacket(var2, var3);
            }
         }

      }
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public int getTerrainPriority() {
      return 0;
   }

   public interface CanPlacePredicate {
      boolean check(GameObject var1, Level var2, int var3, int var4, int var5);
   }
}
