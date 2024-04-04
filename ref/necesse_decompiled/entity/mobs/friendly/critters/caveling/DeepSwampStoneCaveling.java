package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class DeepSwampStoneCaveling extends CavelingMob {
   public DeepSwampStoneCaveling() {
      super(700, 55);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.deepSwampStoneCaveling;
      this.popParticleColor = new Color(34, 50, 37);
      this.singleRockSmallStringID = "deepswampcaverocksmall";
      if (this.item == null) {
         this.item = (InventoryItem)GameRandom.globalRandom.getOneOf((Object[])(new InventoryItem("myceliumore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12))));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
