package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class PacketLogicGateOutputUpdate extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   private final PacketReader reader;

   public PacketLogicGateOutputUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.reader = new PacketReader(var2);
   }

   public PacketLogicGateOutputUpdate(LogicGateEntity var1) {
      this.levelIdentifierHashCode = var1.level.getIdentifierHashCode();
      this.tileX = var1.tileX;
      this.tileY = var1.tileY;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.levelIdentifierHashCode);
      var2.putNextShortUnsigned(this.tileX);
      var2.putNextShortUnsigned(this.tileY);
      this.reader = new PacketReader(var2);
      var1.setupOutputUpdate(var2);
   }

   public PacketReader getReader() {
      return new PacketReader(this.reader);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         LogicGateEntity var3 = var2.getLevel().logicLayer.getEntity(this.tileX, this.tileY);
         if (var3 != null) {
            var3.applyOutputUpdate(this.getReader());
         } else {
            GameLog.warn.println("Got invalid logic gate output update");
         }

      }
   }
}
