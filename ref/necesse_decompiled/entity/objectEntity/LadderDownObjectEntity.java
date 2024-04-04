package necesse.entity.objectEntity;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.ComputedFunction;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.TileDamageType;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class LadderDownObjectEntity extends PortalObjectEntity {
   private int ladderDownID;
   private int ladderUpID;

   public LadderDownObjectEntity(String var1, Level var2, int var3, int var4, int var5, int var6, int var7) {
      super(var2, var1, var3, var4, var2.getIdentifier(), var3, var4);
      LevelIdentifier var8 = var2.getIdentifier();
      if (var8.isIslandPosition()) {
         this.destinationIdentifier = new LevelIdentifier(var8.getIslandX(), var8.getIslandY(), var5);
      }

      this.ladderDownID = var6;
      this.ladderUpID = var7;
   }

   public void use(Server var1, ServerClient var2) {
      ComputedFunction var3 = new ComputedFunction((var1x) -> {
         return var1x.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderUpID ? var1x.getObject(this.destinationTileX, this.destinationTileY).toolType == ToolType.UNBREAKABLE : false;
      });
      if (var1.world.levelManager.isLoaded(this.getDestinationIdentifier())) {
         Level var4 = var1.world.getLevel(this.getDestinationIdentifier());
         if ((Boolean)var3.get(var4)) {
            var2.sendChatMessage((GameMessage)(new LocalMessage("misc", "blockingexit")));
            return;
         }
      }

      this.teleportClientToAroundDestination(var2, (var4x) -> {
         if (!var3.isComputed() && (Boolean)var3.get(var4x)) {
            var2.sendChatMessage((GameMessage)(new LocalMessage("misc", "blockingexit")));
            return false;
         } else {
            if (var4x.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderUpID) {
               GameObject var5 = ObjectRegistry.getObject(this.ladderUpID);

               for(int var6 = -1; var6 <= 1; ++var6) {
                  int var7 = this.destinationTileX + var6;

                  for(int var8 = -1; var8 <= 1; ++var8) {
                     int var9 = this.destinationTileY + var8;
                     GameObject var10 = var4x.getObject(var7, var9);
                     if (var6 == 0 && var8 == 0) {
                        if (!var10.isRock && !var10.isGrass) {
                           var4x.entityManager.doDamageOverride(var7, var9, var10.objectHealth, TileDamageType.Object);
                        }

                        var5.placeObject(var4x, var7, var9, 0);
                        if (var4x.getTile(var7, var9).isLiquid) {
                           var4x.setTile(var7, var9, TileRegistry.dirtID);
                        }

                        var1.network.sendToClientsWithTile(new PacketChangeObject(var4x, var7, var9, this.ladderUpID), var4x, var7, var9);
                     } else {
                        if (var10.getID() != this.ladderUpID && var10.getID() != this.ladderDownID && var10.toolType != ToolType.UNBREAKABLE && (var10.isRock || var10.isGrass)) {
                           var4x.setObject(var7, var9, 0);
                           var1.network.sendToClientsWithTile(new PacketChangeObject(var4x, var7, var9, 0), var4x, var7, var9);
                        }

                        if (var4x.getTile(var7, var9).isLiquid) {
                           var4x.sendTileChangePacket(var1, var7, var9, TileRegistry.dirtID);
                        }
                     }
                  }
               }
            }

            var2.newStats.ladders_used.increment(1);
            this.runClearMobs(var2);
            return true;
         }
      }, true);
   }
}
