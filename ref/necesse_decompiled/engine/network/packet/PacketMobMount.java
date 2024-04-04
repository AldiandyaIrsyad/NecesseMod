package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketMobMount extends Packet {
   public final int mounterUniqueID;
   public final int mountUniqueID;
   public final boolean setMounterPos;
   public final float mounterX;
   public final float mounterY;

   public PacketMobMount(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mounterUniqueID = var2.getNextInt();
      this.mountUniqueID = var2.getNextInt();
      this.setMounterPos = var2.getNextBoolean();
      if (this.setMounterPos) {
         this.mounterX = var2.getNextFloat();
         this.mounterY = var2.getNextFloat();
      } else {
         this.mounterX = -1.0F;
         this.mounterY = -1.0F;
      }

   }

   public PacketMobMount(int var1, int var2, boolean var3, float var4, float var5) {
      this.mounterUniqueID = var1;
      this.mountUniqueID = var2;
      this.mounterX = var4;
      this.mounterY = var5;
      this.setMounterPos = var3;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(var1);
      var6.putNextInt(var2);
      var6.putNextBoolean(var3);
      if (var3) {
         var6.putNextFloat(var4);
         var6.putNextFloat(var5);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mounterUniqueID, var2.getLevel());
         if (this.mountUniqueID == -1) {
            if (var3 != null) {
               var3.dismount();
            }
         } else {
            Mob var4 = GameUtils.getLevelMob(this.mountUniqueID, var2.getLevel());
            if (var3 != null && var4 != null) {
               var3.mount(var4, this.setMounterPos, this.mounterX, this.mounterY);
            }
         }

      }
   }
}
