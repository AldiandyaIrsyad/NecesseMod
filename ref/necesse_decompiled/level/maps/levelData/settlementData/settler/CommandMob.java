package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public interface CommandMob {
   boolean hasCommandOrders();

   boolean canBeCommanded(Client var1);

   boolean canBeCommanded(ServerClient var1);

   void clearCommandsOrders(ServerClient var1);

   void commandFollow(ServerClient var1, Mob var2);

   void commandGuard(ServerClient var1, int var2, int var3);

   void commandAttack(ServerClient var1, Mob var2);

   void setHideOnLowHealth(boolean var1);

   boolean getHideOnLowHealth();
}
