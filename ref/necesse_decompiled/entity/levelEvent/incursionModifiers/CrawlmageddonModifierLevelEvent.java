package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.MobRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.OnMobSpawnedListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.IncursionCrawlingZombieMob;

public class CrawlmageddonModifierLevelEvent extends LevelEvent implements OnMobSpawnedListenerEntityComponent {
   public CrawlmageddonModifierLevelEvent() {
      super(true);
      this.shouldSave = true;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void onMobSpawned(Mob var1) {
      if (this.level.isServer() && var1.isHostile && !var1.isSummoned && !var1.isBoss() && !(var1 instanceof IncursionCrawlingZombieMob)) {
         Mob var2 = MobRegistry.getMob("incursioncrawlingzombie", this.level);
         var2.resetUniqueID();
         var2.onSpawned(var1.getX(), var1.getY());
         this.level.entityManager.mobs.add(var2);
      }

   }
}
