package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;

public class PacketChatMessage extends Packet {
   public final int slot;
   public final GameMessage gameMessage;

   public PacketChatMessage(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.gameMessage = GameMessage.fromContentPacket(var2.getNextContentPacket());
   }

   public PacketChatMessage(int var1, GameMessage var2) {
      this.slot = var1;
      this.gameMessage = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextContentPacket(var2.getContentPacket());
   }

   public PacketChatMessage(GameMessage var1) {
      this(-1, (GameMessage)var1);
   }

   public PacketChatMessage(String var1) {
      this(-1, (GameMessage)(new StaticMessage(var1)));
   }

   public PacketChatMessage(int var1, String var2) {
      this(var1, (GameMessage)(new StaticMessage(var2)));
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      String var4 = this.gameMessage.translate();
      if (this.slot == var3.slot) {
         if (var4.startsWith("/")) {
            var2.sendCommand(var4.substring(1), var3);
         } else {
            System.out.println("(" + var3.getName() + "): " + var4);
            var2.network.sendToAllClientsExcept(this, var3);
         }
      } else {
         System.out.println("Received message from wrong slot sent by slot " + var3.slot + ": " + this.slot + ": " + var4);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      String var3 = this.gameMessage.translate();
      if ((byte)this.slot == -1) {
         var2.chat.addMessage(var3);
      } else {
         ClientClient var4 = var2.getClient(this.slot);
         PlayerMob var5 = var4 == null ? null : var4.playerMob;
         var2.chat.addMessage((var5 == null ? "N/A" : var5.getDisplayName()) + ": " + var3);
         if (var4 == null) {
            var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
         } else {
            Level var6 = var2.getLevel();
            if (var5 != null && var4.isSamePlace(var6)) {
               var6.hudManager.addElement(new ChatBubbleText(var5, GameColor.stripCodes(var3)));
            }
         }
      }

   }
}
