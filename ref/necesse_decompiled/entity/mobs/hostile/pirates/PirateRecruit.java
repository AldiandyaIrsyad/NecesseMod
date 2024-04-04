package necesse.entity.mobs.hostile.pirates;

import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaxHealthGetter;

public class PirateRecruit extends PirateMob {
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(75, 140, 200, 250, 320);
   public static MaxHealthGetter SUMMONED_MAX_HEALTH = new MaxHealthGetter(35, 60, 75, 60, 85);
   private HumanTexture texture;

   public PirateRecruit() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.texture = null;
   }

   public void setSummoned() {
      super.setSummoned();
      this.difficultyChanges.setMaxHealth(SUMMONED_MAX_HEALTH);
   }

   public void init() {
      super.init();
      if (this.texture == null && this.getLevel() != null) {
         this.texture = (new GameRandom((long)this.getUniqueID())).nextBoolean() ? MobRegistry.Textures.pirateRecruit1 : MobRegistry.Textures.pirateRecruit2;
      }

   }

   protected HumanTexture getPirateTexture() {
      return this.texture;
   }
}
