package necesse.entity.objectEntity;

import java.util.function.Predicate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class DungeonExitObjectEntity extends PortalObjectEntity {
   public DungeonExitObjectEntity(Level var1, int var2, int var3, int var4, int var5) {
      super(var1, "dungeonexit", var2, var3, var1.getIdentifier(), var4, var5);
      LevelIdentifier var6 = var1.getIdentifier();
      if (var6.isIslandPosition()) {
         this.destinationIdentifier = new LevelIdentifier(var6.getIslandX(), var6.getIslandY(), 0);
      }

   }

   public void use(Server var1, ServerClient var2) {
      this.teleportClientToAroundDestination(var2, (Predicate)null, true);
      this.runClearMobs(var2);
   }
}
