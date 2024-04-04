package necesse.entity.manager;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public interface RemovedHitHandler<T> {
   void onRemoved(ServerClient var1, int var2, T var3, int var4, Mob var5);
}
