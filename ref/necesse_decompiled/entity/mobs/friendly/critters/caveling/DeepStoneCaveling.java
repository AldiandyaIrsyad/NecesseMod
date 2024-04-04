package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class DeepStoneCaveling extends CavelingMob {
   public DeepStoneCaveling() {
      super(500, 50);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.deepStoneCaveling;
      this.popParticleColor = new Color(38, 42, 44);
      this.singleRockSmallStringID = "deepcaverocksmall";
      if (this.item == null) {
         this.item = (InventoryItem)GameRandom.globalRandom.getOneOf((Object[])(new InventoryItem("tungstenore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12))));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
