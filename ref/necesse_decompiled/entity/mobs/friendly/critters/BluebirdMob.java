package necesse.entity.mobs.friendly.critters;

import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;

public class BluebirdMob extends BirdMob {
   public BluebirdMob() {
   }

   protected GameTexture getTexture() {
      return MobRegistry.Textures.bluebird;
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
