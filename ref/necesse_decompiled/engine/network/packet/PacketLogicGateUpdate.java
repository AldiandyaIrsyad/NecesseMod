package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;

public class PacketLogicGateUpdate extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final int gateID;
   public final Packet content;

   public PacketLogicGateUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.gateID = var2.getNextShort();
      if (this.gateID != -1) {
         this.content = var2.getNextContentPacket();
      } else {
         this.content = null;
      }

   }

   public PacketLogicGateUpdate(Level var1, int var2, int var3, int var4, LogicGateEntity var5) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.tileX = var2;
      this.tileY = var3;
      this.gateID = var4;
      if (var5 != null) {
         this.content = new Packet();
         var5.writePacket(new PacketWriter(this.content));
      } else {
         this.content = null;
      }

      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(this.levelIdentifierHashCode);
      var6.putNextShortUnsigned(var2);
      var6.putNextShortUnsigned(var3);
      var6.putNextShort((short)var4);
      if (this.content != null) {
         var6.putNextContentPacket(this.content);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         var2.getLevel().logicLayer.setLogicGate(this.tileX, this.tileY, this.gateID, this.content == null ? null : new PacketReader(this.content));
      }
   }
}
