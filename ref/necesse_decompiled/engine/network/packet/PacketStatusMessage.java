package necesse.engine.network.packet;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketStatusMessage extends Packet {
   public final GameMessage message;
   public final Color color;
   public final float seconds;

   public PacketStatusMessage(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.message = GameMessage.fromContentPacket(var2.getNextContentPacket());
      this.color = new Color(var2.getNextInt());
      this.seconds = var2.getNextFloat();
   }

   public PacketStatusMessage(GameMessage var1, Color var2, float var3) {
      this.message = var1;
      this.color = var2;
      this.seconds = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextContentPacket(var1.getContentPacket());
      var4.putNextInt(var2.getRGB());
      var4.putNextFloat(var3);
   }

   public PacketStatusMessage(String var1, Color var2, float var3) {
      this((GameMessage)(new StaticMessage(var1)), var2, var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.setMessage(this.message, this.color, this.seconds);
   }
}
