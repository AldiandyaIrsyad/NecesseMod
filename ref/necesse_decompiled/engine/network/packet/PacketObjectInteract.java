package necesse.engine.network.packet;

import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.players.ObjectInteractEvent;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class PacketObjectInteract extends Packet {
   public final int levelIdentifierHashCode;
   public final int slot;
   public final int tileX;
   public final int tileY;

   public PacketObjectInteract(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.slot = var2.getNextByteUnsigned();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
   }

   public PacketObjectInteract(Level var1, int var2, int var3, int var4) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.slot = var2;
      this.tileX = var3;
      this.tileY = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(this.levelIdentifierHashCode);
      var5.putNextByteUnsigned(var2);
      var5.putNextShortUnsigned(var3);
      var5.putNextShortUnsigned(var4);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         ClientClient var3 = var2.getClient(this.slot);
         if (var3 != null && var3.playerMob != null) {
            GameEvents.triggerEvent(new ObjectInteractEvent(var2.getLevel(), this.tileX, this.tileY, var3.playerMob), (var3x) -> {
               var2.getLevel().getLevelObject(this.tileX, this.tileY).interact(var3.playerMob);
            });
         }

      }
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.slot == this.slot) {
         if (!var3.checkHasRequestedSelf() || var3.isDead()) {
            return;
         }

         var3.checkSpawned();
         Level var4 = var2.world.getLevel(var3);
         if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            LevelObject var5 = var4.getLevelObject(this.tileX, this.tileY);
            if (var5.inInteractRange(var3.playerMob)) {
               if (var5.canInteract(var3.playerMob)) {
                  GameEvents.triggerEvent(new ObjectInteractEvent(var4, this.tileX, this.tileY, var3.playerMob), (var4x) -> {
                     var5.interact(var3.playerMob);
                     var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
                  });
               } else {
                  GameLog.warn.println("Client tried to interact with non interactable object " + var5.object.getStringID() + " at " + var5.tileX + ", " + var5.tileY);
               }
            }
         } else {
            GameLog.warn.println("Client " + var3.getName() + " tried call interact at wrong level");
         }
      } else {
         GameLog.warn.println("Client " + var3.getName() + " tried call interact from wrong slot");
      }

   }
}
