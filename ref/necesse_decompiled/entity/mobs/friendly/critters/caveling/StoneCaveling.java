package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class StoneCaveling extends CavelingMob {
   public StoneCaveling() {
      super(200, 40);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.stoneCaveling;
      this.popParticleColor = new Color(105, 105, 105);
      this.singleRockSmallStringID = "surfacerocksmall";
      if (this.item == null) {
         this.item = new InventoryItem("goldore", GameRandom.globalRandom.getIntBetween(12, 24));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
