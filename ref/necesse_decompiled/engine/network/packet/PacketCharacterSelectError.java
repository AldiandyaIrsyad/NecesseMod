package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.HumanLook;

public class PacketCharacterSelectError extends Packet {
   public final HumanLook look;
   public final GameMessage error;

   public PacketCharacterSelectError(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.look = new HumanLook(var2);
      this.error = GameMessage.fromContentPacket(var2.getNextContentPacket());
   }

   public PacketCharacterSelectError(HumanLook var1, GameMessage var2) {
      this.look = var1;
      this.error = var2;
      PacketWriter var3 = new PacketWriter(this);
      var1.setupContentPacket(var3, true);
      var3.putNextContentPacket(var2.getContentPacket());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.loading.createCharPhase.submitError(this.look, this.error);
   }
}
