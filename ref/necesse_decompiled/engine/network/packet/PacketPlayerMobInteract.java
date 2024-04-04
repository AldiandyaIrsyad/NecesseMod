package necesse.engine.network.packet;

import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.players.MobInteractEvent;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class PacketPlayerMobInteract extends Packet {
   public final int slot;
   public final int targetMobUniqueID;

   public PacketPlayerMobInteract(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.targetMobUniqueID = var2.getNextInt();
   }

   public PacketPlayerMobInteract(int var1, int var2) {
      this.slot = var1;
      this.targetMobUniqueID = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextInt(this.targetMobUniqueID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         PlayerMob var3 = var2.getPlayer(this.slot);
         if (var3 != null) {
            Mob var4 = GameUtils.getLevelMob(this.targetMobUniqueID, var2.getLevel());
            if (var4 != null) {
               GameEvents.triggerEvent(new MobInteractEvent(var4, var3), (var2x) -> {
                  var4.interact(var3);
               });
            } else {
               var2.network.sendPacket(new PacketRequestMobData(this.targetMobUniqueID));
            }
         } else {
            var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
         }

      }
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot == var3.slot && var3.checkHasRequestedSelf() && !var3.isDead()) {
         var3.checkSpawned();
         Level var4 = var2.world.getLevel(var3);
         Mob var5 = GameUtils.getLevelMob(this.targetMobUniqueID, var4);
         if (var5 != null) {
            if (var5.inInteractRange(var3.playerMob)) {
               if (var5.canInteract(var3.playerMob)) {
                  GameEvents.triggerEvent(new MobInteractEvent(var5, var3.playerMob), (var2x) -> {
                     var5.interact(var3.playerMob);
                  });
                  var2.network.sendToClientsAt(this, (ServerClient)var3);
               } else {
                  GameLog.warn.println("Client tried to interact with not interactable mob " + var5.getStringID() + ", " + var5.getUniqueID());
               }
            }
         } else {
            var2.network.sendPacket(new PacketRemoveMob(this.targetMobUniqueID), (ServerClient)var3);
         }

      }
   }
}
