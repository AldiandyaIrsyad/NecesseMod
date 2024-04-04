package necesse.engine.achievements;

import com.codedisaster.steamworks.SteamAchievementUnlocked;
import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAchievementUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameAchievements;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class BoolAchievement extends Achievement {
   protected boolean completed = false;

   public BoolAchievement(String var1, GameMessage var2, GameMessage var3) {
      super(var1, var2, var3);
   }

   public BoolAchievement(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public void runStatsUpdate(ServerClient var1) {
   }

   public void markCompleted(ServerClient var1) {
      if (!this.isCompleted()) {
         if (!var1.getServer().world.settings.achievementsEnabled()) {
            return;
         }

         this.completed = true;
         this.updateTimeCompleted();
         var1.getServer().network.sendToAllClients(new PacketAchievementUpdate(var1, this, true));
      }

   }

   public void loadSteam(SteamGameAchievements var1) {
      SteamAchievementUnlocked var2 = var1.getAchievementByName(this.stringID);
      if (var2 != null) {
         this.completed = var2.unlocked;
         this.completedTime = var2.unlockTime;
      }

   }

   public void drawProgress(int var1, int var2, int var3, boolean var4) {
      String var5 = Localization.translate("achievement", this.isCompleted() ? "complete" : "incomplete");
      Color var6 = this.isCompleted() ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
      FontOptions var7 = (new FontOptions(16)).outline(var4).color(var6);
      int var8 = FontManager.bit.getWidthCeil(var5, var7);
      int var9 = var1 + var3 - var8 - 10;
      FontManager.bit.drawString((float)var9, (float)var2, var5, var7);
   }

   public void addSaveData(SaveData var1) {
      if (this.isCompleted()) {
         var1.addInt("completed", 1);
      }

   }

   public void applyLoadData(LoadData var1) {
      if (var1.hasLoadDataByName("completed")) {
         this.completed = true;
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextBoolean(this.completed);
   }

   public void applyContentPacket(PacketReader var1) {
      this.completed = var1.getNextBoolean();
   }
}
