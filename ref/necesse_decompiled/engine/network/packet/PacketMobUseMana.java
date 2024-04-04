package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class PacketMobUseMana extends Packet {
   public final int mobUniqueID;
   private final float currentMana;

   public PacketMobUseMana(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.currentMana = var2.getNextFloat();
   }

   public PacketMobUseMana(Mob var1) {
      this.mobUniqueID = var1.getUniqueID();
      this.currentMana = var1.getMana();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobUniqueID);
      var2.putNextFloat(this.currentMana);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 == null) {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         } else {
            var3.setManaHidden(this.currentMana);
            var3.lastManaSpentTime = var3.getWorldEntity().getTime();
            if (this.currentMana <= 0.0F) {
               var3.isManaExhausted = true;
               var3.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, var3, 1000, (Attacker)null), false);
            }
         }
      }

   }
}
