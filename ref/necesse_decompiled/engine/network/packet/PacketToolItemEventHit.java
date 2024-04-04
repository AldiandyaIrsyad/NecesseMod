package necesse.engine.network.packet;

import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketToolItemEventHit extends Packet {
   public final int eventUniqueID;
   public final int targetUniqueID;

   public PacketToolItemEventHit(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventUniqueID = var2.getNextInt();
      this.targetUniqueID = var2.getNextInt();
   }

   public PacketToolItemEventHit(ToolItemEvent var1, Mob var2) {
      this.eventUniqueID = var1.getUniqueID();
      this.targetUniqueID = var2.getUniqueID();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.eventUniqueID);
      var3.putNextInt(this.targetUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var3.getLevel();
      Mob var5 = GameUtils.getLevelMob(this.targetUniqueID, var4);
      LevelEvent var6 = var4.entityManager.getLevelEvent(this.eventUniqueID, true);
      if (var5 != null && var6 instanceof ToolItemEvent) {
         this.serverHit(var3, (ToolItemEvent)var6, var5);
      } else {
         var4.entityManager.submittedHits.submitToolItemEventHit(var3, this.eventUniqueID, this.targetUniqueID, this::serverHit, (var0, var1x, var2x, var3x, var4x) -> {
            if (var2x == null) {
               var0.sendPacket(new PacketLevelEventOver(var1x));
            }

            if (var4x == null) {
               var0.sendPacket(new PacketRemoveMob(var3x));
            }

         });
      }

   }

   private void serverHit(ServerClient var1, ToolItemEvent var2, Mob var3) {
      if (var2.mob == var1.playerMob || var2.handlingClient == var1 && !var3.isPlayer && var2.canHit(var3, Settings.giveClientsPower ? 1000 : 100)) {
         var2.hit(var3, var1);
      }

   }
}
