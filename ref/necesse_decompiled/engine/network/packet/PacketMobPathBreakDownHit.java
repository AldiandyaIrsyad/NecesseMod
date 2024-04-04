package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.level.maps.LevelObject;

public class PacketMobPathBreakDownHit extends Packet {
   public final int mobUniqueID;
   public final boolean dir;
   public final boolean horizontal;
   public final int tileX;
   public final int tileY;

   public PacketMobPathBreakDownHit(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.dir = var2.getNextBoolean();
      this.horizontal = var2.getNextBoolean();
      this.tileX = var2.getNextInt();
      this.tileY = var2.getNextInt();
   }

   public PacketMobPathBreakDownHit(Mob var1, LevelObject var2, boolean var3, boolean var4) {
      this.mobUniqueID = var1.getUniqueID();
      this.dir = var3;
      this.horizontal = var4;
      this.tileX = var2.tileX;
      this.tileY = var2.tileY;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(this.mobUniqueID);
      var5.putNextBoolean(var3);
      var5.putNextBoolean(var4);
      var5.putNextInt(this.tileX);
      var5.putNextInt(this.tileY);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 != null) {
            var3.onPathBreakDownHit(new LevelObject(var2.getLevel(), this.tileX, this.tileY), this.dir, this.horizontal);
            var3.refreshClientUpdateTime();
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }
      }

   }
}
