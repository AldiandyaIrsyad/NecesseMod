package necesse.entity.manager;

import java.util.HashSet;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public interface MobDeathListenerEntityComponent extends EntityComponent {
   void onLevelMobDied(Mob var1, Attacker var2, HashSet<Attacker> var3);
}
