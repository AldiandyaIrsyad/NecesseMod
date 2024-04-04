package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.temple.TempleArenaLevel;
import necesse.level.maps.biomes.temple.TempleLevel;

public class TempleEntranceObjectEntity extends PortalObjectEntity {
   public TempleEntranceObjectEntity(Level var1, int var2, int var3) {
      super(var1, "templeentrance", var2, var3, var1.getIdentifier(), 50, 50);
   }

   public void init() {
      super.init();
      if (this.getLevel() != null) {
         LevelIdentifier var1 = this.getLevel().getIdentifier();
         if (var1.isIslandPosition()) {
            Point var2;
            if (var1.getIslandDimension() == -200) {
               this.destinationIdentifier = new LevelIdentifier(var1.getIslandX(), var1.getIslandY(), -201);
               var2 = TempleArenaLevel.getExitPosition();
            } else {
               this.destinationIdentifier = new LevelIdentifier(var1.getIslandX(), var1.getIslandY(), -200);
               var2 = TempleLevel.getEntranceSpawnPos(var1.getIslandX(), var1.getIslandY());
            }

            this.destinationTileX = var2.x;
            this.destinationTileY = var2.y;
         } else {
            this.destinationIdentifier = var1;
            this.destinationTileX = this.getTileX();
            this.destinationTileY = this.getTileY();
         }
      }

   }

   public void use(Server var1, ServerClient var2) {
      this.teleportClientToAroundDestination(var2, (var1x) -> {
         GameObject var2 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("templeexit"));
         if (var2 != null) {
            var2.placeObject(var1x, this.destinationTileX - 1, this.destinationTileY, 0);
            PortalObjectEntity var3 = (PortalObjectEntity)var1x.entityManager.getObjectEntity(this.destinationTileX - 1, this.destinationTileY);
            if (var3 != null) {
               var3.destinationTileX = this.getX();
               var3.destinationTileY = this.getY();
               var3.destinationIdentifier = this.getLevel().getIdentifier();
            }
         }

         return true;
      }, true);
      this.runClearMobs(var2);
   }
}
