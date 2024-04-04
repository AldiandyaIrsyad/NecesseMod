package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class SwampStoneCaveling extends CavelingMob {
   public SwampStoneCaveling() {
      super(300, 45);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.swampStoneCaveling;
      this.popParticleColor = new Color(50, 62, 48);
      this.singleRockSmallStringID = "swampsurfacerocksmall";
      if (this.item == null) {
         this.item = new InventoryItem("ivyore", GameRandom.globalRandom.getIntBetween(12, 24));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
