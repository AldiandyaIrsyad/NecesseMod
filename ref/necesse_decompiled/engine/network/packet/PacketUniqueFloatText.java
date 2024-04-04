package necesse.engine.network.packet;

import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

public class PacketUniqueFloatText extends Packet {
   public int levelX;
   public int levelY;
   public final GameMessage message;
   public String uniqueType;
   public int hoverTime;

   public PacketUniqueFloatText(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelX = var2.getNextInt();
      this.levelY = var2.getNextInt();
      this.message = GameMessage.fromPacket(var2);
      if (var2.getNextBoolean()) {
         this.uniqueType = var2.getNextString();
      } else {
         this.uniqueType = null;
      }

      this.hoverTime = var2.getNextInt();
   }

   public PacketUniqueFloatText(int var1, int var2, GameMessage var3, String var4, int var5) {
      this.levelX = var1;
      this.levelY = var2;
      this.message = (GameMessage)Objects.requireNonNull(var3);
      this.uniqueType = var4;
      this.hoverTime = var5;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(var1);
      var6.putNextInt(var2);
      var3.writePacket(var6);
      var6.putNextBoolean(var4 != null);
      if (var4 != null) {
         var6.putNextString(var4);
      }

      var6.putNextInt(var5);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         String var3 = this.message.translate();
         UniqueFloatText var4 = new UniqueFloatText(this.levelX, this.levelY, var3, (new FontOptions(16)).outline(), this.uniqueType);
         var4.hoverTime = this.hoverTime;
         var2.getLevel().hudManager.addElement(var4);
      }
   }
}
