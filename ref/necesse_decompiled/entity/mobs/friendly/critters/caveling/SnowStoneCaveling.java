package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class SnowStoneCaveling extends CavelingMob {
   public SnowStoneCaveling() {
      super(250, 40);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.snowStoneCaveling;
      this.popParticleColor = new Color(200, 200, 200);
      this.singleRockSmallStringID = "snowsurfacerocksmall";
      if (this.item == null) {
         this.item = new InventoryItem("frostshard", GameRandom.globalRandom.getIntBetween(8, 12));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
