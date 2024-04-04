package necesse.engine.save;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStats;
import necesse.entity.mobs.PlayerMob;

public class CharacterSaveNetworkData {
   public final Packet playerData;
   public final boolean cheatsEnabled;
   public final Packet characterStatsData;
   public final String playerName;

   public CharacterSaveNetworkData(CharacterSave var1) {
      this.playerData = new Packet();
      var1.player.setupLoadedCharacterPacket(new PacketWriter(this.playerData));
      this.cheatsEnabled = var1.cheatsEnabled;
      if (var1.characterStats != null) {
         this.characterStatsData = new Packet();
         var1.characterStats.setupContentPacket(new PacketWriter(this.characterStatsData));
      } else {
         this.characterStatsData = null;
      }

      this.playerName = var1.player.playerName;
   }

   public CharacterSaveNetworkData(PacketReader var1) {
      this.playerData = var1.getNextContentPacket();
      this.cheatsEnabled = var1.getNextBoolean();
      if (var1.getNextBoolean()) {
         this.characterStatsData = var1.getNextContentPacket();
      } else {
         this.characterStatsData = null;
      }

      this.playerName = var1.getNextString();
   }

   public void write(PacketWriter var1) {
      var1.putNextContentPacket(this.playerData);
      var1.putNextBoolean(this.cheatsEnabled);
      var1.putNextBoolean(this.characterStatsData != null);
      if (this.characterStatsData != null) {
         var1.putNextContentPacket(this.characterStatsData);
      }

      var1.putNextString(this.playerName);
   }

   public void applyToPlayer(PlayerMob var1) {
      var1.applyLoadedCharacterPacket(new PacketReader(this.playerData));
      var1.playerName = this.playerName;
   }

   public boolean applyToStats(PlayerStats var1) {
      if (this.characterStatsData != null) {
         var1.applyContentPacket(new PacketReader(this.characterStatsData));
         return true;
      } else {
         return false;
      }
   }
}
