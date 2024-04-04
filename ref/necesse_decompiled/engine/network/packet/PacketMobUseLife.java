package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public class PacketMobUseLife extends Packet {
   public final int mobUniqueID;
   private final int currentLife;
   private final int usedLife;

   public PacketMobUseLife(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.currentLife = var2.getNextInt();
      this.usedLife = var2.getNextInt();
   }

   public PacketMobUseLife(Mob var1, int var2) {
      this.mobUniqueID = var1.getUniqueID();
      this.currentLife = var1.getHealth();
      this.usedLife = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextInt(this.currentLife);
      var3.putNextInt(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 == null) {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         } else {
            var3.useLife(this.currentLife, this.usedLife, (ServerClient)null, (Attacker)null);
         }
      }

   }
}
