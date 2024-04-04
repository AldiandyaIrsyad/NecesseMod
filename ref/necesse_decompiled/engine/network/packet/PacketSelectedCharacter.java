package necesse.engine.network.packet;

import java.util.Iterator;
import java.util.Map;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.CharacterSaveNetworkData;
import necesse.engine.util.GameUtils;
import necesse.gfx.HumanLook;

public class PacketSelectedCharacter extends Packet {
   public final int characterUniqueID;
   public final CharacterSaveNetworkData networkData;

   public PacketSelectedCharacter(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.characterUniqueID = var2.getNextInt();
      if (var2.getNextBoolean()) {
         this.networkData = new CharacterSaveNetworkData(var2);
      } else {
         this.networkData = null;
      }

   }

   public PacketSelectedCharacter(int var1, CharacterSave var2) {
      this.characterUniqueID = var1;
      this.networkData = var2 == null ? null : new CharacterSaveNetworkData(var2);
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      if (this.networkData != null) {
         var3.putNextBoolean(true);
         this.networkData.write(var3);
      } else {
         var3.putNextBoolean(false);
      }

   }

   public PacketSelectedCharacter(CharacterSave var1) {
      this(var1.characterUniqueID, var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (!var2.world.settings.allowOutsideCharacters) {
         var3.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "outsidecharactersnotallowed")));
      } else {
         if (!var3.hasSubmittedCharacter()) {
            if (this.networkData != null) {
               GameMessage var4 = GameUtils.isValidPlayerName(this.networkData.playerName);
               if (var4 != null) {
                  var3.sendPacket(new PacketCharacterSelectError(new HumanLook(), var4));
                  return;
               }

               Iterator var5 = var2.usedNames.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var5.next();
                  Long var7 = (Long)var6.getKey();
                  String var8 = (String)var6.getValue();
                  if (var7 != var3.authentication && var8.equalsIgnoreCase(this.networkData.playerName)) {
                     var3.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "characternameinuse", "name", this.networkData.playerName)));
                     return;
                  }
               }

               if (this.networkData.playerName.equalsIgnoreCase(Settings.serverOwnerName)) {
                  var3.setPermissionLevel(PermissionLevel.OWNER, false);
               }

               if (this.networkData.cheatsEnabled && !var2.world.settings.allowCheats) {
                  if (var3.getPermissionLevel().getLevel() < PermissionLevel.OWNER.getLevel()) {
                     var3.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "characterhascheats")));
                     return;
                  }

                  var2.world.settings.enableCheats();
                  System.out.println(this.networkData.playerName + " enabled cheats by joining with a cheats enabled character");
               }
            }

            var3.applyLoadedCharacterPacket(this);
            var3.sendConnectingMessage();
         }

      }
   }
}
