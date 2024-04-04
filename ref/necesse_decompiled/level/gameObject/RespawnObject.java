package necesse.level.gameObject;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public interface RespawnObject {
   boolean canRespawn(Level var1, int var2, int var3, ServerClient var4);

   default boolean isCurrentSpawn(Level var1, int var2, int var3, ServerClient var4) {
      return var4.spawnLevelIdentifier.equals(var1.getIdentifier()) && var4.spawnTile.x == var2 && var4.spawnTile.y == var3;
   }

   default boolean removeSpawn(Level var1, int var2, int var3, ServerClient var4, boolean var5) {
      if (this.isCurrentSpawn(var1, var2, var3, var4)) {
         var4.resetSpawnPoint(var1.getServer());
         if (var5) {
            var4.sendChatMessage((GameMessage)(new LocalMessage("misc", "spawnremoved")));
         }

         return true;
      } else {
         return false;
      }
   }

   default void setSpawn(Level var1, int var2, int var3, ServerClient var4, boolean var5) {
      if (var4.achievementsLoaded()) {
         var4.achievements().SET_SPAWN.markCompleted(var4);
      }

      var4.spawnLevelIdentifier = var1.getIdentifier();
      var4.spawnTile = new Point(var2, var3);
      if (var5) {
         var4.sendChatMessage((GameMessage)(new LocalMessage("misc", "spawnset")));
      }

   }

   Point getSpawnOffset(Level var1, int var2, int var3, ServerClient var4);

   static Point calculateSpawnOffset(Level var0, int var1, int var2, ServerClient var3) {
      if (var0 == null) {
         return null;
      } else {
         GameObject var4 = var0.getObject(var1, var2);
         if (var4 instanceof RespawnObject) {
            return !((RespawnObject)var4).canRespawn(var0, var1, var2, var3) ? null : ((RespawnObject)var4).getSpawnOffset(var0, var1, var2, var3);
         } else {
            return null;
         }
      }
   }
}
