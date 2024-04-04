package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class DeepSandStoneCaveling extends CavelingMob {
   public DeepSandStoneCaveling() {
      super(800, 55);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.deepSandStoneCaveling;
      this.popParticleColor = new Color(144, 117, 58);
      this.singleRockSmallStringID = "deepsandcaverocksmall";
      if (this.item == null) {
         this.item = (InventoryItem)GameRandom.globalRandom.getOneOf((Object[])(new InventoryItem("ancientfossilore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12))));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
