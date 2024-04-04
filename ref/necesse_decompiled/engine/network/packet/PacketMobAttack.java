package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;

public class PacketMobAttack extends Packet {
   public final int mobUniqueID;
   public final int x;
   public final int y;

   public PacketMobAttack(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.x = var2.getNextInt();
      this.y = var2.getNextInt();
   }

   public PacketMobAttack(Mob var1, int var2, int var3) {
      this.mobUniqueID = var1.getUniqueID();
      this.x = var2;
      this.y = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.mobUniqueID);
      var4.putNextInt(var2);
      var4.putNextInt(var3);
   }

   void putData(int var1, int var2, int var3) {
      this.putInt(0, var1);
      this.putInt(4, var2);
      this.putInt(8, var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = (Mob)var2.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         if (var3 != null) {
            var3.showAttack(this.x, this.y, false);
         }

      }
   }
}
