package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketMobAbilityLevelEventHit extends Packet {
   public final int eventUniqueID;
   public final int targetUniqueID;
   public final Packet content;

   public PacketMobAbilityLevelEventHit(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventUniqueID = var2.getNextInt();
      this.targetUniqueID = var2.getNextInt();
      if (var2.hasNext()) {
         this.content = var2.getNextContentPacket();
      } else {
         this.content = null;
      }

   }

   public PacketMobAbilityLevelEventHit(MobAbilityLevelEvent var1, Mob var2, Packet var3) {
      this.eventUniqueID = var1.getUniqueID();
      this.targetUniqueID = var2.getUniqueID();
      this.content = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.eventUniqueID);
      var4.putNextInt(this.targetUniqueID);
      if (var3 != null) {
         var4.putNextContentPacket(var3);
      }

   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var3.getLevel();
      LevelEvent var5 = var4.entityManager.getLevelEvent(this.eventUniqueID, true);
      Mob var6 = GameUtils.getLevelMob(this.targetUniqueID, var4);
      if (var6 != null && var5 instanceof MobAbilityLevelEvent) {
         this.serverHit(var3, (MobAbilityLevelEvent)var5, var6);
      } else {
         var4.entityManager.submittedHits.submitMobAbilityLevelEventHit(var3, this.eventUniqueID, this.targetUniqueID, this::serverHit, (var0, var1x, var2x, var3x, var4x) -> {
            if (var2x == null) {
               var0.sendPacket(new PacketLevelEventOver(var1x));
            }

            if (var4x == null) {
               var0.sendPacket(new PacketRemoveMob(var3x));
            }

         });
      }

   }

   private void serverHit(ServerClient var1, MobAbilityLevelEvent var2, Mob var3) {
      if (var3 == var1.playerMob || var2.handlingClient == var1 && !var3.isPlayer) {
         var2.serverHit(var3, this.content, true);
      }

   }
}
