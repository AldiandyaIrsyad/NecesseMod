package necesse.entity.manager;

import necesse.entity.mobs.Mob;

public interface OnMobSpawnedListenerEntityComponent extends EntityComponent {
   void onMobSpawned(Mob var1);
}
