package necesse.engine.network.packet;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class PacketQuestShareReceive extends Packet {
   public final int sharerSlot;
   public final int questUniqueID;
   public final GameMessage questTitle;

   public PacketQuestShareReceive(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.sharerSlot = var2.getNextByteUnsigned();
      this.questUniqueID = var2.getNextInt();
      this.questTitle = GameMessage.fromContentPacket(var2.getNextContentPacket());
   }

   public PacketQuestShareReceive(ServerClient var1, Quest var2) {
      this.questUniqueID = var2.getUniqueID();
      this.sharerSlot = var1.slot;
      this.questTitle = var2.getTitle();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(this.sharerSlot);
      var3.putNextInt(this.questUniqueID);
      var3.putNextContentPacket(this.questTitle.getContentPacket());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var4 = var2.getClient(this.sharerSlot);
      String var3;
      if (var4 != null) {
         var3 = var4.getName();
      } else {
         var3 = "N/A";
         var2.network.sendPacket(new PacketRequestPlayerData(this.sharerSlot));
      }

      FairType var5 = new FairType();
      var5.append(ChatMessage.fontOptions, (new LocalMessage("misc", "questinvite", new Object[]{"player", var3, "quest", this.questTitle})).translate());
      var5.append(ChatMessage.fontOptions, "\n");
      var5.append(ChatMessage.fontOptions, GameColor.GREEN.getColorCode());
      var5.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "acceptbutton") + "]", (var2x) -> {
         if (var2x.getID() == -100 && !var2x.state) {
            var2.network.sendPacket(new PacketQuestShareReply(this.questUniqueID, true));
            var2.chat.removeMessagesIf((var1) -> {
               return var1 instanceof QuestShareMessage && ((QuestShareMessage)var1).questUniqueID == this.questUniqueID;
            });
            return true;
         } else {
            return false;
         }
      }, (Supplier)null));
      var5.append(ChatMessage.fontOptions, " ");
      var5.append(ChatMessage.fontOptions, GameColor.RED.getColorCode());
      var5.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "declinebutton") + "]", (var2x) -> {
         if (var2x.getID() == -100 && !var2x.state) {
            var2.network.sendPacket(new PacketQuestShareReply(this.questUniqueID, false));
            var2.chat.removeMessagesIf((var1) -> {
               return var1 instanceof QuestShareMessage && ((QuestShareMessage)var1).questUniqueID == this.questUniqueID;
            });
            return true;
         } else {
            return false;
         }
      }, (Supplier)null));
      var2.chat.addMessage((ChatMessage)(new QuestShareMessage(var5.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions)), this.questUniqueID)));
   }

   private static class QuestShareMessage extends ChatMessage {
      public final int questUniqueID;

      public QuestShareMessage(FairType var1, int var2) {
         super(var1);
         this.questUniqueID = var2;
      }
   }
}
