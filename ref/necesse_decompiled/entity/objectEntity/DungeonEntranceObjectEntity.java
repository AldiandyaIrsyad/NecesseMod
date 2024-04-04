package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;
import necesse.level.maps.biomes.dungeon.DungeonLevel;

public class DungeonEntranceObjectEntity extends PortalObjectEntity {
   public DungeonEntranceObjectEntity(Level var1, int var2, int var3) {
      super(var1, "dungeonentrance", var2, var3, var1.getIdentifier(), var2, var3);
   }

   public void init() {
      super.init();
      if (this.getLevel() != null) {
         LevelIdentifier var1 = this.getLevel().getIdentifier();
         if (var1.isIslandPosition()) {
            Point var2;
            if (var1.getIslandDimension() == -100) {
               this.destinationIdentifier = new LevelIdentifier(var1.getIslandX(), var1.getIslandY(), -101);
               var2 = DungeonArenaLevel.getLadderPosition();
            } else {
               this.destinationIdentifier = new LevelIdentifier(var1.getIslandX(), var1.getIslandY(), -100);
               var2 = DungeonLevel.getSpawnLadderPos(this.getLevel());
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
         GameObject var2 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("dungeonexit"));
         if (var2 != null) {
            GameObject var3 = var1x.getObject(this.destinationTileX, this.destinationTileY);
            if (var3.getID() != var2.getID()) {
               boolean var4 = false;

               for(int var5 = 0; var5 < var1x.width; ++var5) {
                  for(int var6 = 0; var6 < var1x.height; ++var6) {
                     if (var1x.getObjectID(var5, var6) == var2.getID()) {
                        this.destinationTileX = var5;
                        this.destinationTileY = var6;
                        var4 = true;
                        break;
                     }
                  }

                  if (var4) {
                     break;
                  }
               }
            }

            var2.placeObject(var1x, this.destinationTileX, this.destinationTileY, 0);
            PortalObjectEntity var7 = (PortalObjectEntity)var1x.entityManager.getObjectEntity(this.destinationTileX, this.destinationTileY);
            if (var7 != null) {
               var7.destinationTileX = this.getX();
               var7.destinationTileY = this.getY();
               var7.destinationIdentifier = this.getLevel().getIdentifier();
            }
         }

         return true;
      }, true);
      this.runClearMobs(var2);
   }
}
