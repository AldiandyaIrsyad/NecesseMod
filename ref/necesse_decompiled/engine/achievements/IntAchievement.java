package necesse.engine.achievements;

import com.codedisaster.steamworks.SteamAchievementUnlocked;
import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAchievementUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameAchievements;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class IntAchievement extends Achievement {
   protected final int min;
   protected final int max;
   protected final DrawMode drawMode;
   protected boolean completed;
   protected final Supplier<Integer> progressGetter;

   public IntAchievement(String var1, GameMessage var2, GameMessage var3, Supplier<Integer> var4, int var5, int var6, DrawMode var7) {
      super(var1, var2, var3);
      this.completed = false;
      if (var5 >= var6) {
         throw new IllegalArgumentException("Achievement min must be lower than max");
      } else {
         this.min = var5;
         this.max = var6;
         this.drawMode = var7;
         this.progressGetter = var4;
      }
   }

   public IntAchievement(String var1, String var2, String var3, Supplier<Integer> var4, int var5, int var6, DrawMode var7) {
      this(var1, (GameMessage)(new LocalMessage("achievement", var2)), (GameMessage)(new LocalMessage("achievement", var3)), var4, var5, var6, var7);
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public void runStatsUpdate(ServerClient var1) {
      if (!this.isCompleted()) {
         if (!var1.getServer().world.settings.achievementsEnabled()) {
            return;
         }

         this.completed = (Integer)this.progressGetter.get() >= this.max;
         if (this.isCompleted()) {
            this.updateTimeCompleted();
            System.out.println("Completed " + this.stringID + " achievement");
            var1.getServer().network.sendToAllClients(new PacketAchievementUpdate(var1, this, true));
         }
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
      int var5 = this.max - this.min;
      int var6 = this.isCompleted() ? var5 : Math.max(0, (Integer)this.progressGetter.get() - this.min);
      float var7 = (float)var6 / (float)var5;
      String var8 = "N/A";
      boolean var9 = false;
      switch (this.drawMode) {
         case NORMAL:
            var8 = var6 + "/" + var5;
            var9 = true;
            break;
         case PERCENT:
            var8 = (int)(var7 * 100.0F) + "%";
            var9 = true;
            break;
         case BOOL:
            var8 = Localization.translate("achievement", this.isCompleted() ? "complete" : "incomplete");
            var9 = false;
      }

      Color var10;
      FontOptions var11;
      if (var9) {
         var10 = var7 == 1.0F ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
         var11 = (new FontOptions(16)).outline(var4).color(var10);
         Achievement.drawProgressbarText(var1, var2, var3, 5, var7, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, var8, var11);
      } else {
         var10 = this.isCompleted() ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
         var11 = (new FontOptions(16)).outline(var4).color(var10);
         int var12 = FontManager.bit.getWidthCeil(var8, var11);
         int var13 = var1 + var3 - var12 - 10;
         FontManager.bit.drawString((float)var13, (float)var2, var8, var11);
      }

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
      var1.putNextBoolean(this.isCompleted());
   }

   public void applyContentPacket(PacketReader var1) {
      this.completed = var1.getNextBoolean();
   }

   public static enum DrawMode {
      NORMAL,
      PERCENT,
      BOOL;

      private DrawMode() {
      }

      // $FF: synthetic method
      private static DrawMode[] $values() {
         return new DrawMode[]{NORMAL, PERCENT, BOOL};
      }
   }
}
