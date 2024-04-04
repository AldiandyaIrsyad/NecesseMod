package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LogicGateRegistry;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;

public class PacketPlaceLogicGate extends Packet {
   public final int levelIdentifierHashCode;
   public final int slot;
   public final int gateID;
   public final int tileX;
   public final int tileY;

   public PacketPlaceLogicGate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.slot = var2.getNextByteUnsigned();
      this.gateID = var2.getNextShortUnsigned();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
   }

   public PacketPlaceLogicGate(Level var1, ServerClient var2, int var3, int var4, int var5) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.slot = var2.slot;
      this.gateID = var3;
      this.tileX = var4;
      this.tileY = var5;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(this.levelIdentifierHashCode);
      var6.putNextByteUnsigned(this.slot);
      var6.putNextShortUnsigned(var3);
      var6.putNextShortUnsigned(var4);
      var6.putNextShortUnsigned(var5);
   }

   public GameLogicGate getGate() {
      return LogicGateRegistry.getLogicGate(this.gateID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         GameLogicGate var3 = this.getGate();
         if (this.slot == var2.getSlot()) {
            var3.playPlaceSound(this.tileX, this.tileY);
         }

         var3.placeGate(var2.getLevel(), this.tileX, this.tileY);
      }
   }
}
