package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.level.maps.Level;

public class PacketMouseBeamEventUpdate extends Packet {
   public final int eventUniqueID;
   public final int targetAngle;
   public final float currentAngle;

   public PacketMouseBeamEventUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventUniqueID = var2.getNextInt();
      this.targetAngle = var2.getNextShortUnsigned();
      this.currentAngle = var2.getNextFloat();
   }

   public PacketMouseBeamEventUpdate(MouseBeamLevelEvent var1, int var2, float var3) {
      this.eventUniqueID = var1.getUniqueID();
      this.targetAngle = var2;
      this.currentAngle = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.eventUniqueID);
      var4.putNextShortUnsigned(var2);
      var4.putNextFloat(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var3.getLevel();
      LevelEvent var5 = var4.entityManager.getLevelEvent(this.eventUniqueID, false);
      if (var5 instanceof MouseBeamLevelEvent) {
         MouseBeamLevelEvent var6 = (MouseBeamLevelEvent)var5;
         if (var6.player == var3.playerMob && (var6.handlingClient == null || var6.handlingClient == var3)) {
            var6.targetAngle = this.targetAngle;
            float var7 = this.currentAngle;
            if (Settings.giveClientsPower) {
               var6.currentAngle = var7;
            } else {
               var7 = var6.currentAngle;
            }

            var2.network.sendToClientsAtExcept(new PacketMouseBeamEventUpdate(var6, this.targetAngle, var7), (ServerClient)var3, var3);
            return;
         }
      }

      GameLog.warn.println("Could not find client submitted MouseBeamLevelEvent update event for " + var3.getName());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         LevelEvent var3 = var2.getLevel().entityManager.getLevelEvent(this.eventUniqueID, false);
         if (var3 instanceof MouseBeamLevelEvent) {
            MouseBeamLevelEvent var4 = (MouseBeamLevelEvent)var3;
            var4.targetAngle = this.targetAngle;
         } else {
            var2.network.sendPacket(new PacketRequestLevelEvent(this.eventUniqueID));
         }

      }
   }
}
