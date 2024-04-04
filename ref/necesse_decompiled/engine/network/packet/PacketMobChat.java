package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameColor;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;

public class PacketMobChat extends Packet {
   public final int mobUniqueID;
   public final GameMessage message;

   public PacketMobChat(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.message = GameMessage.fromContentPacket(var2.getNextContentPacket());
   }

   public PacketMobChat(int var1, GameMessage var2) {
      this.mobUniqueID = var1;
      this.message = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextContentPacket(var2.getContentPacket());
   }

   public PacketMobChat(int var1, String var2) {
      this(var1, (GameMessage)(new StaticMessage(var2)));
   }

   public PacketMobChat(int var1, String var2, String var3) {
      this(var1, (GameMessage)(new LocalMessage(var2, var3)));
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 != null) {
            var3.getLevel().hudManager.addElement(new ChatBubbleText(var3, GameColor.stripCodes(this.message.translate())));
         }

      }
   }
}
