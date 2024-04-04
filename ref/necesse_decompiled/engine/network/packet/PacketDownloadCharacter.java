package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.CharacterSave;
import necesse.gfx.HumanLook;

public class PacketDownloadCharacter extends Packet {
   public final int characterUniqueID;

   public PacketDownloadCharacter(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.characterUniqueID = var2.getNextInt();
   }

   public PacketDownloadCharacter(int var1) {
      this.characterUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (!var2.world.settings.allowOutsideCharacters) {
         var3.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "outsidecharactersnotallowed")));
      } else {
         if (!var3.hasSubmittedCharacter()) {
            if (var3.getCharacterUniqueID() == this.characterUniqueID) {
               var3.sendPacket(new PacketDownloadCharacterResponse(this.characterUniqueID, new CharacterSave(var3)));
            } else {
               GameLog.warn.println(var3.getName() + " tried to download unknown character with uniqueID " + this.characterUniqueID);
            }
         }

      }
   }
}
