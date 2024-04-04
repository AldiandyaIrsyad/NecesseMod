package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;

public class PacketMobJump extends Packet {
   public final int mobUniqueID;
   public final float x;
   public final float y;
   public final float dx;
   public final float dy;

   public PacketMobJump(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.x = var2.getNextFloat();
      this.y = var2.getNextFloat();
      this.dx = var2.getNextFloat();
      this.dy = var2.getNextFloat();
   }

   public PacketMobJump(Mob var1, float var2, float var3) {
      if (!(var1 instanceof JumpingMobInterface)) {
         throw new IllegalArgumentException("Mob must implement JumpingMobInterface");
      } else {
         this.mobUniqueID = var1.getUniqueID();
         this.x = var1.x;
         this.y = var1.y;
         this.dx = var2;
         this.dy = var3;
         PacketWriter var4 = new PacketWriter(this);
         var4.putNextInt(this.mobUniqueID);
         var4.putNextFloat(this.x);
         var4.putNextFloat(this.y);
         var4.putNextFloat(var2);
         var4.putNextFloat(var3);
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = (Mob)var2.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         if (var3 instanceof JumpingMobInterface) {
            var3.updatePosFromServer(this.x, this.y, false);
            ((JumpingMobInterface)var3).runJump(this.dx, this.dy);
            var3.refreshClientUpdateTime();
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }

      }
   }
}
