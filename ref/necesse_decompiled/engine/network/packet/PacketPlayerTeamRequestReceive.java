package necesse.engine.network.packet;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class PacketPlayerTeamRequestReceive extends Packet {
   public final long auth;
   public final String name;

   public PacketPlayerTeamRequestReceive(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.auth = var2.getNextLong();
      this.name = var2.getNextString();
   }

   public PacketPlayerTeamRequestReceive(long var1, String var3) {
      this.auth = var1;
      this.name = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextLong(var1);
      var4.putNextString(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      FairType var3 = new FairType();
      var3.append(ChatMessage.fontOptions, Localization.translate("misc", "teamrequest", "name", this.name));
      var3.append(ChatMessage.fontOptions, "\n");
      var3.append(ChatMessage.fontOptions, GameColor.GREEN.getColorCode());
      var3.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "acceptbutton") + "]", (var2x) -> {
         if (var2x.getID() == -100) {
            if (!var2x.state) {
               var2.network.sendPacket(new PacketPlayerTeamRequestReply(this.auth, true));
               var2.chat.removeMessagesIf((var1) -> {
                  return var1 instanceof TeamRequestMessage && ((TeamRequestMessage)var1).auth == this.auth;
               });
            }

            return true;
         } else {
            return false;
         }
      }, (Supplier)null));
      var3.append(ChatMessage.fontOptions, " ");
      var3.append(ChatMessage.fontOptions, GameColor.RED.getColorCode());
      var3.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "declinebutton") + "]", (var2x) -> {
         if (var2x.getID() == -100) {
            if (!var2x.state) {
               var2.network.sendPacket(new PacketPlayerTeamRequestReply(this.auth, false));
               var2.chat.removeMessagesIf((var1) -> {
                  return var1 instanceof TeamRequestMessage && ((TeamRequestMessage)var1).auth == this.auth;
               });
            }

            return true;
         } else {
            return false;
         }
      }, (Supplier)null));
      var2.chat.removeMessagesIf((var1x) -> {
         return var1x instanceof TeamRequestMessage && ((TeamRequestMessage)var1x).auth == this.auth;
      });
      var2.chat.addMessage((ChatMessage)(new TeamRequestMessage(var3.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions)), this.auth)));
   }

   private static class TeamRequestMessage extends ChatMessage {
      public final long auth;

      public TeamRequestMessage(FairType var1, long var2) {
         super(var1);
         this.auth = var2;
      }
   }
}
