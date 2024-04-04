package necesse.entity.mobs.friendly.critters;

import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;

public class CanaryBirdMob extends BirdMob {
   public CanaryBirdMob() {
   }

   protected GameTexture getTexture() {
      return MobRegistry.Textures.canaryBird;
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
