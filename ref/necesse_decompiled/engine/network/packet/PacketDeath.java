package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public class PacketDeath extends Packet {
   public final int mobUniqueID;
   public final float knockbackX;
   public final float knockbackY;
   public final boolean isDeath;

   public PacketDeath(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.knockbackX = var2.getNextFloat();
      this.knockbackY = var2.getNextFloat();
      this.isDeath = var2.getNextBoolean();
   }

   public PacketDeath(int var1, float var2, float var3, boolean var4) {
      this.mobUniqueID = var1;
      this.knockbackX = var2;
      this.knockbackY = var3;
      this.isDeath = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(var1);
      var5.putNextFloat(var2);
      var5.putNextFloat(var3);
      var5.putNextBoolean(var4);
   }

   public PacketDeath(Mob var1, float var2, float var3, boolean var4) {
      this(var1.getUniqueID(), var2, var3, var4);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 != null) {
            var3.remove(this.knockbackX, this.knockbackY, (Attacker)null, this.isDeath);
         }

      }
   }
}
