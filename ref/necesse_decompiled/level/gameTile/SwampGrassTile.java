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

public class SwampGrassTile extends TerrainSplatterTile {
   public static double growChance = GameMath.getAverageSuccessRuns(4900.0);
   public static double spreadChance = GameMath.getAverageSuccessRuns(550.0);
   private final GameRandom drawRandom;

   public SwampGrassTile() {
      super(false, "swampgrass");
      this.mapColor = new Color(80, 108, 55);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.04F, "swampgrassseed")});
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      GrassTile.addSimulateGrow(var1, var2, var3, growChance, var4, "swampgrass", var6, var7);
   }

   public double spreadToDirtChance() {
      return spreadChance;
   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         if (var1.getObjectID(var2, var3) == 0 && GameRandom.globalRandom.getChance(growChance)) {
            GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("swampgrass"));
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
}
