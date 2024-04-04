package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;

public class GravelTile extends TerrainSplatterTile {
   private final GameRandom drawRandom;

   public GravelTile() {
      super(false, "gravel");
      this.mapColor = new Color(117, 102, 58);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{(new LootItem("stone")).preventLootMultiplier()});
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public boolean canBePlacedOn(Level var1, int var2, int var3, GameTile var4) {
      return false;
   }

   public int getTerrainPriority() {
      return 300;
   }
}
