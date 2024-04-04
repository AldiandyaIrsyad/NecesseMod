package necesse.entity.mobs.hostile.bosses;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.level.maps.Level;

public class BossMob extends HostileMob {
   public static int BOSS_MULTIPLAYER_RESPAWN_TIME = 20000;
   public static int BOSS_SINGLEPLAYER_RESPAWN_TIME = 6000;

   public static int getBossRespawnTime(Mob var0) {
      Level var1 = var0.getLevel();
      if (var1.isServer() && var1.getServer().getPlayersOnline() <= 1) {
         return BOSS_SINGLEPLAYER_RESPAWN_TIME;
      } else {
         return var1.isClient() && var1.getClient().streamClients().count() <= 1L ? BOSS_SINGLEPLAYER_RESPAWN_TIME : BOSS_MULTIPLAYER_RESPAWN_TIME;
      }
   }

   public BossMob(int var1) {
      super(var1);
      this.canDespawn = false;
      this.shouldSave = false;
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      return false;
   }

   public int getRespawnTime() {
      return getBossRespawnTime(this);
   }
}
