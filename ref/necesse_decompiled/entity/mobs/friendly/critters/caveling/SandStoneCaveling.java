package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class SandStoneCaveling extends CavelingMob {
   public SandStoneCaveling() {
      super(350, 45);
   }

   public void init() {
      super.init();
      this.texture = MobRegistry.Textures.sandStoneCaveling;
      this.popParticleColor = new Color(215, 215, 125);
      this.singleRockSmallStringID = "sandcaverocksmall";
      if (this.item == null) {
         this.item = new InventoryItem("quartz", GameRandom.globalRandom.getIntBetween(8, 12));
      }

   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
