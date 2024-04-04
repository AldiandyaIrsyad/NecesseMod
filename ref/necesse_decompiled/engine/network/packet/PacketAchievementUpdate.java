package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.achievements.Achievement;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.steam.SteamData;

public class PacketAchievementUpdate extends Packet {
   public final int slot;
   public final boolean submitCompleted;
   public final int achievementID;
   public final Packet achievementContent;

   public PacketAchievementUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.submitCompleted = var2.getNextBoolean();
      this.achievementID = var2.getNextShortUnsigned();
      this.achievementContent = var2.getNextContentPacket();
   }

   public PacketAchievementUpdate(ServerClient var1, Achievement var2, boolean var3) {
      this.slot = var1.slot;
      this.submitCompleted = var3;
      this.achievementID = var2.getDataID();
      this.achievementContent = new Packet();
      var2.setupContentPacket(new PacketWriter(this.achievementContent));
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(this.slot);
      var4.putNextBoolean(var3);
      var4.putNextShortUnsigned(this.achievementID);
      var4.putNextContentPacket(this.achievementContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      Achievement var3 = GlobalData.achievements().getAchievement(this.achievementID);
      if (this.submitCompleted) {
         ClientClient var4 = var2.getClient(this.slot);
         if (var4 != null) {
            var2.chat.addMessage(Localization.translate("achievement", "chatcomplete", "player", var4.getName(), "achievement", var3.name.translate()));
         }
      }

      if (this.slot == var2.getSlot()) {
         var3.applyContentPacket(new PacketReader(this.achievementContent));
         GlobalData.achievements().saveAchievementsFileSafe();
         if (this.submitCompleted) {
            SteamData.setAchievement(var3.stringID);
            SteamData.forceStoreStatsAndAchievements();
         }
      }

   }
}
