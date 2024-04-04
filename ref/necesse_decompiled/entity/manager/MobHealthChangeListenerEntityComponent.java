package necesse.entity.manager;

import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public interface MobHealthChangeListenerEntityComponent extends EntityComponent {
   void onLevelMobHealthChanged(Mob var1, int var2, int var3, float var4, float var5, Attacker var6);
}
