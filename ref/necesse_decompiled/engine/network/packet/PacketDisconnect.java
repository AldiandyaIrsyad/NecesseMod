package necesse.engine.network.packet;

import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketDisconnect extends Packet {
   public final int slot;
   public final Code code;
   public final Packet codeContent;

   public PacketDisconnect(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      int var3 = var2.getNextByteUnsigned();
      if (var3 > 250) {
         var3 = (byte)var3;
      }

      this.slot = var3;
      this.code = PacketDisconnect.Code.getCode(var2.getNextByteUnsigned());
      this.codeContent = var2.getNextContentPacket();
   }

   public PacketDisconnect(PacketDisconnect var1, int var2) {
      this(var2, var1.code, var1.codeContent);
   }

   public PacketDisconnect(int var1, Code var2) {
      this(var1, var2, new Packet());
   }

   public PacketDisconnect(int var1, GameMessage var2) {
      this(var1, PacketDisconnect.Code.CUSTOM, var2.getContentPacket());
   }

   private PacketDisconnect(int var1, Code var2, Packet var3) {
      this.slot = var1;
      this.code = var2;
      this.codeContent = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var4.putNextByteUnsigned(var2.getID());
      var4.putNextContentPacket(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot != var3.slot) {
         System.out.print("Player " + var3.authentication + " (\"" + var3.getName() + "\", slot " + var3.slot + ") tried to disconnect wrong client slot: " + this.slot);
      } else {
         if (this.code != PacketDisconnect.Code.CLIENT_DISCONNECT) {
            System.out.print("Player " + var3.authentication + " (\"" + var3.getName() + "\", slot " + var3.slot + ") tried to disconnect wrong code: " + this.code);
         } else {
            System.out.println("Player " + var3.authentication + " (\"" + var3.getName() + "\") disconnected with message: " + this.code.getErrorMessage(this.codeContent));
            var2.disconnectClient(var3, this);
         }

      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (this.slot != var2.getSlot() && this.slot != -2 && this.slot != -1) {
         if (var2.getClient(this.slot) != null) {
            var2.chat.addMessage(Localization.translate("disconnect", "chatmsg", "name", var2.getClient(this.slot).getName(), "msg", this.code.getErrorMessage(this.codeContent)));
            if (var2.getPlayer(this.slot) != null && var2.getPlayer(this.slot).isRiding()) {
               var2.getPlayer(this.slot).dismount();
            }
         }

         var2.clearClient(this.slot);
         var2.loading.playersPhase.submitLoadedPlayer(this.slot);
      } else {
         if (this.code == PacketDisconnect.Code.WRONG_PASSWORD) {
            long var3 = (new PacketReader(this.codeContent)).getNextLong();
            var2.loading.connectingPhase.submitWrongPassword(var3);
         }

         var2.error(this.code.getErrorMessage(this.codeContent), false);
      }

   }

   public static PacketDisconnect wrongPassword(Server var0) {
      Packet var1 = new Packet();
      (new PacketWriter(var1)).putNextLong(var0.world.getUniqueID());
      return new PacketDisconnect(-1, PacketDisconnect.Code.WRONG_PASSWORD, var1);
   }

   public static PacketDisconnect kickPacket(int var0, String var1) {
      Packet var2 = new Packet();
      (new PacketWriter(var2)).putNextString(var1);
      return new PacketDisconnect(var0, PacketDisconnect.Code.KICK, var2);
   }

   public static PacketDisconnect networkError(int var0, String var1) {
      Packet var2 = new Packet();
      (new PacketWriter(var2)).putNextString(var1);
      return new PacketDisconnect(var0, PacketDisconnect.Code.NETWORK_ERROR, var2);
   }

   public static PacketDisconnect clientDisconnect(int var0, String var1) {
      Packet var2 = new Packet();
      (new PacketWriter(var2)).putNextString(var1);
      return new PacketDisconnect(var0, PacketDisconnect.Code.CLIENT_DISCONNECT, var2);
   }

   public static enum Code {
      NULL((var0) -> {
         return Localization.translate("disconnect", "unknown");
      }),
      INTERNAL_ERROR((var0) -> {
         return Localization.translate("disconnect", "internal");
      }),
      KICK((var0) -> {
         String var1 = (new PacketReader(var0)).getNextString();
         if (var1.length() == 0) {
            var1 = Localization.translate("disconnect", "noreason");
         }

         return Localization.translate("disconnect", "kicked", "reason", var1);
      }),
      CLIENT_NOT_RESPONDING((var0) -> {
         return Localization.translate("disconnect", "clientresponding");
      }),
      SERVER_STOPPED((var0) -> {
         return Localization.translate("disconnect", "serverstopped");
      }),
      SERVER_ERROR((var0) -> {
         return Localization.translate("disconnect", "servererror");
      }),
      CLIENT_ERROR((var0) -> {
         return Localization.translate("disconnect", "clienterror");
      }),
      WRONG_PASSWORD((var0) -> {
         return Localization.translate("disconnect", "wrongpassword");
      }),
      MISSING_CLIENT((var0) -> {
         return Localization.translate("disconnect", "missingclient");
      }),
      BANNED_CLIENT((var0) -> {
         return Localization.translate("disconnect", "banned");
      }),
      WRONG_VERSION((var0) -> {
         return Localization.translate("disconnect", "wrongversion");
      }),
      ALREADY_PLAYING((var0) -> {
         return Localization.translate("disconnect", "alreadyplaying");
      }),
      SERVER_FULL((var0) -> {
         return Localization.translate("disconnect", "serverfull");
      }),
      CLIENT_DISCONNECT((var0) -> {
         String var1 = (new PacketReader(var0)).getNextString();
         if (var1.length() == 0) {
            var1 = Localization.translate("disconnect", "nomessage");
         }

         return var1;
      }),
      MISSING_APPEARANCE((var0) -> {
         return Localization.translate("disconnect", "missingappearance");
      }),
      NETWORK_ERROR((var0) -> {
         String var1 = (new PacketReader(var0)).getNextString();
         String var2 = "";
         if (var1.length() == 0) {
            var2 = Localization.translate("disconnect", "networkerror");
         } else {
            var2 = Localization.translate("disconnect", "networkerrormsg", "msg", var1);
         }

         return var2;
      }),
      STATE_DESYNC((var0) -> {
         return Localization.translate("disconnect", "statedesync");
      }),
      INVITE_ONLY((var0) -> {
         return Localization.translate("disconnect", "inviteonly");
      }),
      FRIENDS_ONLY((var0) -> {
         return Localization.translate("disconnect", "friendsonly");
      }),
      CUSTOM((var0) -> {
         return GameMessage.fromContentPacket(var0).translate();
      });

      public Function<Packet, String> errorMessage;

      private Code(Function var3) {
         this.errorMessage = var3;
      }

      public String getErrorMessage(Packet var1) {
         return (String)this.errorMessage.apply(var1);
      }

      public int getID() {
         return this.ordinal();
      }

      public static Code getCode(int var0) {
         Code[] var1 = values();
         return var0 >= 0 && var0 < var1.length ? var1[var0] : NULL;
      }

      // $FF: synthetic method
      private static Code[] $values() {
         return new Code[]{NULL, INTERNAL_ERROR, KICK, CLIENT_NOT_RESPONDING, SERVER_STOPPED, SERVER_ERROR, CLIENT_ERROR, WRONG_PASSWORD, MISSING_CLIENT, BANNED_CLIENT, WRONG_VERSION, ALREADY_PLAYING, SERVER_FULL, CLIENT_DISCONNECT, MISSING_APPEARANCE, NETWORK_ERROR, STATE_DESYNC, INVITE_ONLY, FRIENDS_ONLY, CUSTOM};
      }
   }
}
