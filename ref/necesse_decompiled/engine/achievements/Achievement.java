package necesse.engine.achievements;

import java.awt.Color;
import java.time.Instant;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameAchievements;
import necesse.gfx.GameResources;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public abstract class Achievement {
   public final String stringID;
   private int dataID;
   protected long completedTime;
   protected GameTexture completeTexture;
   protected GameTexture incompleteTexture;
   public final GameMessage name;
   public final GameMessage description;
   private boolean isDirty;

   public void setDataID(int var1) {
      if (this.dataID != -1) {
         throw new IllegalArgumentException("Cannot set data id twice");
      } else {
         this.dataID = var1;
      }
   }

   public int getDataID() {
      return this.dataID;
   }

   public Achievement(String var1, GameMessage var2, GameMessage var3) {
      this.dataID = -1;
      this.stringID = var1;
      this.name = var2;
      this.description = var3;
      this.completedTime = -1L;
   }

   public Achievement(String var1, String var2, String var3) {
      this(var1, (GameMessage)(new LocalMessage("achievement", var2)), (GameMessage)(new LocalMessage("achievement", var3)));
   }

   public abstract boolean isCompleted();

   public abstract void runStatsUpdate(ServerClient var1);

   public final long getTimeCompleted() {
      return this.completedTime;
   }

   protected void updateTimeCompleted() {
      this.completedTime = this.getNow();
   }

   protected long getNow() {
      return Instant.now().toEpochMilli() / 1000L;
   }

   public void loadTextures(Achievement var1) {
      if (var1 == null) {
         this.completeTexture = this.borderTexture(this.stringID + "_complete");
         this.incompleteTexture = this.borderTexture(this.stringID + "_incomplete");
      } else {
         this.completeTexture = var1.completeTexture;
         this.incompleteTexture = var1.incompleteTexture;
      }

   }

   protected GameTexture borderTexture(String var1) {
      GameTexture var2 = GameTexture.fromFile("achievements/border", true);
      GameTexture var3 = GameTexture.fromFile("achievements/border_mask", true);
      GameTexture var4 = GameTexture.fromFile("achievements/" + var1, true);
      GameTexture var5 = new GameTexture(var4);
      var4.makeFinal();
      var5.merge(var3, 0, 0, MergeFunction.ALPHA_MASK);
      var5.merge(var2, 0, 0, MergeFunction.NORMAL);
      var5.makeFinal();
      return var5;
   }

   public abstract void loadSteam(SteamGameAchievements var1);

   public void drawIcon(int var1, int var2) {
      GameTexture var3;
      if (this.isCompleted()) {
         var3 = this.completeTexture;
      } else {
         var3 = this.incompleteTexture;
      }

      if (var3 == null) {
         var3 = GameResources.error;
      }

      var3.initDraw().size(40, 40).draw(var1, var2);
   }

   public abstract void drawProgress(int var1, int var2, int var3, boolean var4);

   public abstract void addSaveData(SaveData var1);

   public abstract void applyLoadData(LoadData var1);

   public void addCompletedTimeSave(SaveData var1) {
      var1.addLong("time", this.completedTime);
   }

   public void applyCompletedTimeSave(LoadData var1) {
      this.completedTime = var1.getLong("time", -1L);
   }

   public abstract void setupContentPacket(PacketWriter var1);

   public abstract void applyContentPacket(PacketReader var1);

   public final void markDirty() {
      this.isDirty = true;
   }

   public void clean() {
      this.isDirty = false;
   }

   public final boolean isDirty() {
      return this.isDirty;
   }

   public static void drawProgressbar(int var0, int var1, int var2, int var3, float var4) {
      drawProgressbarText(var0, var1, var2, var3, var4, (String)null, (Color)null);
   }

   public static void drawProgressbar(int var0, int var1, int var2, int var3, float var4, Color var5, Color var6) {
      drawProgressbarText(var0, var1, var2, var3, var4, var5, var6, (String)null, (FontOptions)null);
   }

   public static void drawProgressbarText(int var0, int var1, int var2, int var3, float var4, String var5, Color var6) {
      if (var5 != null && var6 == null) {
         var6 = var4 == 1.0F ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
      }

      drawProgressbarText(var0, var1, var2, var3, var4, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, var5, var6 == null ? null : (new FontOptions(16)).color(var6));
   }

   public static void drawProgressbarText(int var0, int var1, int var2, int var3, float var4, Color var5, Color var6, String var7, FontOptions var8) {
      var4 = Math.min(1.0F, Math.max(0.0F, var4));
      int var9 = var1;
      int var10 = var2;
      int var11;
      if (var7 != null) {
         var11 = FontManager.bit.getWidthCeil(var7, var8);
         int var12 = FontManager.bit.getHeightCeil(var7, var8);
         var9 = var1 + var12 / 2 - var3 / 2;
         var10 = var2 - var11 - 20;
         int var13 = var0 + var2 - var11 - 10;
         FontManager.bit.drawString((float)var13, (float)var1, var7, var8);
      }

      var11 = (int)((float)var10 * var4);
      Screen.initQuadDraw(var10 + 4, var3 + 4).color(var5).draw(var0 - 2, var9 - 2);
      Screen.initQuadDraw(var11, var3).color(var6).draw(var0, var9);
   }
}
