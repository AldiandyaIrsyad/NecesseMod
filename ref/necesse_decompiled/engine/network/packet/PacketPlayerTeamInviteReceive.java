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

public class PacketPlayerTeamInviteReceive extends Packet {
   public final int teamID;
   public final String teamName;

   public PacketPlayerTeamInviteReceive(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.teamID = var2.getNextInt();
      this.teamName = var2.getNextString();
   }

   public PacketPlayerTeamInviteReceive(int var1, String var2) {
      this.teamID = var1;
      this.teamName = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextString(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      FairType var3 = new FairType();
      var3.append(ChatMessage.fontOptions, Localization.translate("misc", "teaminvite", "team", this.teamName));
      var3.append(ChatMessage.fontOptions, "\n");
      var3.append(ChatMessage.fontOptions, GameColor.GREEN.getColorCode());
      var3.append(FairCharacterGlyph.fromString(ChatMessage.fontOptions, "[" + Localization.translate("ui", "acceptbutton") + "]", (var2x) -> {
         if (var2x.getID() == -100) {
            if (!var2x.state) {
               var2.network.sendPacket(new PacketPlayerTeamInviteReply(this.teamID, true));
               var2.chat.removeMessagesIf((var1) -> {
                  return var1 instanceof TeamInviteMessage && ((TeamInviteMessage)var1).teamID == this.teamID;
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
               var2.network.sendPacket(new PacketPlayerTeamInviteReply(this.teamID, false));
               var2.chat.removeMessagesIf((var1) -> {
                  return var1 instanceof TeamInviteMessage && ((TeamInviteMessage)var1).teamID == this.teamID;
               });
            }

            return true;
         } else {
            return false;
         }
      }, (Supplier)null));
      var2.chat.removeMessagesIf((var1x) -> {
         return var1x instanceof TeamInviteMessage && ((TeamInviteMessage)var1x).teamID == this.teamID;
      });
      var2.chat.addMessage((ChatMessage)(new TeamInviteMessage(var3.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions)), this.teamID)));
   }

   private static class TeamInviteMessage extends ChatMessage {
      public final int teamID;

      public TeamInviteMessage(FairType var1, int var2) {
         super(var1);
         this.teamID = var2;
      }
   }
}
