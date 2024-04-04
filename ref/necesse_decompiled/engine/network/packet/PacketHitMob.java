package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;

public class PacketHitMob extends Packet {
   public final int uniqueID;
   public final float mobX;
   public final float mobY;
   public final float mobDx;
   public final float mobDy;
   public final int mobHealth;
   public final Packet hitEventContent;
   public final int attackerUniqueID;

   public PacketHitMob(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.uniqueID = var2.getNextInt();
      this.mobX = var2.getNextFloat();
      this.mobY = var2.getNextFloat();
      this.mobDx = var2.getNextFloat();
      this.mobDy = var2.getNextFloat();
      this.mobHealth = var2.getNextInt();
      this.hitEventContent = var2.getNextContentPacket();
      this.attackerUniqueID = var2.getNextInt();
   }

   public PacketHitMob(Mob var1, MobWasHitEvent var2, Attacker var3) {
      this.uniqueID = var1.getUniqueID();
      this.mobX = var1.x;
      this.mobY = var1.y;
      this.mobDx = var1.dx;
      this.mobDy = var1.dy;
      this.mobHealth = var1.getHealth();
      this.hitEventContent = new Packet();
      var2.writePacket(new PacketWriter(this.hitEventContent));
      this.attackerUniqueID = var3.getAttackerUniqueID();
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.uniqueID);
      var4.putNextFloat(this.mobX);
      var4.putNextFloat(this.mobY);
      var4.putNextFloat(this.mobDx);
      var4.putNextFloat(this.mobDy);
      var4.putNextInt(this.mobHealth);
      var4.putNextContentPacket(this.hitEventContent);
      var4.putNextInt(this.attackerUniqueID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.uniqueID, var2.getLevel());
         if (var3 != null && var3.getLevel() != null) {
            if (var3 != var2.getPlayer()) {
               var3.updatePosFromServer(this.mobX, this.mobY, this.mobDx, this.mobDy, false);
            }

            var3.refreshClientUpdateTime();
            var3.setHealthHidden(this.mobHealth);
            Attacker var4 = this.attackerUniqueID == -1 ? null : GameUtils.getLevelAttacker(this.attackerUniqueID, var2.getLevel());
            MobWasHitEvent var5 = new MobWasHitEvent(var3, var4, new PacketReader(this.hitEventContent));
            var3.isHit(var5, var4);
         }
      }

   }
}
