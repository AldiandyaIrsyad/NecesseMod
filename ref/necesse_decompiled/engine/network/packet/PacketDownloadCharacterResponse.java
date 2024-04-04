package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.CharacterSaveNetworkData;

public class PacketDownloadCharacterResponse extends Packet {
   public final int characterUniqueID;
   public final CharacterSaveNetworkData networkData;

   public PacketDownloadCharacterResponse(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.characterUniqueID = var2.getNextInt();
      this.networkData = new CharacterSaveNetworkData(var2);
   }

   public PacketDownloadCharacterResponse(int var1, CharacterSave var2) {
      this.characterUniqueID = var1;
      this.networkData = new CharacterSaveNetworkData(var2);
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      this.networkData.write(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.loading.createCharPhase.submitDownloadedCharacter(this);
   }
}
