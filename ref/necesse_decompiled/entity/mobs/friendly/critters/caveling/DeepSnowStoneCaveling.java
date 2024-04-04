package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class DeepSnowStoneCaveling extends CavelingMob {
   public DeepSnowStoneCaveling() {
      super(600, 50);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.deepSnowStoneCaveling;
      this.popParticleColor = new Color(49, 56, 60);
      this.singleRockSmallStringID = "deepsnowcaverocksmall";
      if (this.item == null) {
         this.item = (InventoryItem)GameRandom.globalRandom.getOneOf((Object[])(new InventoryItem("glacialore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12))));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
