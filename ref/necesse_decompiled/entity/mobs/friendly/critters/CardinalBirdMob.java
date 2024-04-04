package necesse.entity.mobs.friendly.critters;

import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;

public class CardinalBirdMob extends BirdMob {
   public CardinalBirdMob() {
   }

   protected GameTexture getTexture() {
      return MobRegistry.Textures.cardinalBird;
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }
}
