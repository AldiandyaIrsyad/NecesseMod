package necesse.engine.quest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public abstract class HaveKilledMobsQuest extends Quest {
   protected ArrayList<HaveKilledObjective> objectives;

   public HaveKilledMobsQuest() {
      this.objectives = new ArrayList();
   }

   public HaveKilledMobsQuest(HaveKilledObjective var1, HaveKilledObjective... var2) {
      this.objectives = new ArrayList();
      this.objectives = new ArrayList();
      this.objectives.add(var1);
      this.objectives.addAll(Arrays.asList(var2));
   }

   public HaveKilledMobsQuest(String var1, int var2) {
      this(new HaveKilledObjective(var1, var2));
   }

   public HaveKilledMobsQuest(int var1, int var2) {
      this(new HaveKilledObjective(var1, var2));
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         HaveKilledObjective var3 = (HaveKilledObjective)var2.next();
         SaveData var4 = new SaveData("objective");
         var4.addUnsafeString("mobStringID", MobRegistry.getMobStringID(var3.mobID));
         var4.addInt("mobsToKill", var3.mobsToKill);
         var1.addSaveData(var4);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.objectives.clear();
      String var2 = var1.getUnsafeString("mobStringID", (String)null, false);
      if (var2 != null) {
         int var3 = MobRegistry.getMobID(var2);
         if (var3 == -1) {
            throw new IllegalStateException("Could not find mob with stringID " + var2);
         }

         int var4 = var1.getInt("mobsToKill", 1);
         this.objectives.add(new HaveKilledObjective(var3, var4));
      }

      Iterator var7 = var1.getLoadDataByName("objective").iterator();

      while(var7.hasNext()) {
         LoadData var8 = (LoadData)var7.next();
         String var9 = var8.getUnsafeString("mobStringID", (String)null, false);
         if (var9 != null) {
            int var5 = MobRegistry.getMobID(var9);
            if (var5 == -1) {
               throw new IllegalStateException("Could not find mob with stringID " + var9);
            }

            int var6 = var8.getInt("mobsToKill", 1);
            this.objectives.add(new HaveKilledObjective(var5, var6));
         }
      }

      if (this.objectives.isEmpty()) {
         throw new IllegalStateException("Could not find any objectives");
      }
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.objectives.size());
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         HaveKilledObjective var3 = (HaveKilledObjective)var2.next();
         var1.putNextInt(var3.mobID);
         var1.putNextShortUnsigned(var3.mobsToKill);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.objectives.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextInt();
         int var5 = var1.getNextShortUnsigned();
         this.objectives.add(new HaveKilledObjective(var4, var5));
      }

   }

   public void tick(ServerClient var1) {
   }

   public GameMessage getDescription() {
      return null;
   }

   public boolean canComplete(NetworkClient var1) {
      Iterator var2;
      HaveKilledObjective var3;
      int var4;
      if (var1.isServer()) {
         var2 = this.objectives.iterator();

         while(var2.hasNext()) {
            var3 = (HaveKilledObjective)var2.next();
            var4 = var1.getServerClient().characterStats().mob_kills.getKills(MobRegistry.getMobStringID(var3.mobID));
            if (var4 < var3.mobsToKill) {
               return false;
            }
         }
      } else if (var1.isClient()) {
         var2 = this.objectives.iterator();

         while(var2.hasNext()) {
            var3 = (HaveKilledObjective)var2.next();
            var4 = var1.getClientClient().getClient().characterStats.mob_kills.getKills(MobRegistry.getMobStringID(var3.mobID));
            if (var4 < var3.mobsToKill) {
               return false;
            }
         }
      }

      return true;
   }

   public void drawProgress(PlayerMob var1, int var2, int var3, int var4, Color var5, boolean var6) {
      NetworkClient var7 = var1.getNetworkClient();

      for(Iterator var8 = this.objectives.iterator(); var8.hasNext(); var3 += 20) {
         HaveKilledObjective var9 = (HaveKilledObjective)var8.next();
         String var10 = Localization.translate("quests", "killmob", "mob", MobRegistry.getDisplayName(var9.mobID));
         FontOptions var11 = (new FontOptions(16)).outline(var6);
         if (var5 != null) {
            var11.color(var5);
         }

         FontManager.bit.drawString((float)var2, (float)var3, var10, var11);
         var3 += 16;
         String var12 = MobRegistry.getKillHint(var9.mobID);
         if (var12 != null) {
            FontOptions var13 = (new FontOptions(12)).outline(var6);
            if (var5 != null) {
               var13.color(var5);
            }

            ArrayList var14 = GameUtils.breakString(var12, var13, var4);

            for(Iterator var15 = var14.iterator(); var15.hasNext(); var3 += 12) {
               String var16 = (String)var15.next();
               FontManager.bit.drawString((float)var2, (float)(var3 + 2), var16, var13);
            }

            var3 += 2;
         }

         int var17 = var9.getCurrentKills(var7);
         float var18 = var9.mobsToKill == 0 ? 1.0F : (float)var17 / (float)var9.mobsToKill;
         Color var19 = var18 == 1.0F ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
         FontOptions var20 = (new FontOptions(16)).outline(var6).color(var19);
         Achievement.drawProgressbarText(var2, var3, var4, 5, var18, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, var17 + "/" + var9.mobsToKill, var20);
      }

   }

   public int getDrawProgressHeight(int var1, boolean var2) {
      int var3 = 0;
      Iterator var4 = this.objectives.iterator();

      while(var4.hasNext()) {
         HaveKilledObjective var5 = (HaveKilledObjective)var4.next();
         var3 += 36;
         String var6 = MobRegistry.getKillHint(var5.mobID);
         if (var6 != null) {
            FontOptions var7 = (new FontOptions(12)).outline(var2);
            ArrayList var8 = GameUtils.breakString(var6, var7, var1);
            var3 += var8.size() * 12 + 2;
         }
      }

      return var3;
   }

   public static class HaveKilledObjective {
      public int mobID;
      public int mobsToKill;

      public HaveKilledObjective(int var1, int var2) {
         if (var1 < 0) {
            throw new IllegalArgumentException("mobID cannot be negative");
         } else if (var2 < 1) {
            throw new IllegalArgumentException("mobsToKill cannot be less than 1");
         } else {
            this.mobID = var1;
            this.mobsToKill = var2;
         }
      }

      public HaveKilledObjective(String var1, int var2) {
         this(MobRegistry.getMobID(var1), var2);
      }

      public int getCurrentKills(NetworkClient var1) {
         int var2 = 0;
         if (var1.isServer()) {
            var2 = var1.getServerClient().characterStats().mob_kills.getKills(MobRegistry.getMobStringID(this.mobID));
         } else if (var1.isClient()) {
            var2 = var1.getClientClient().getClient().characterStats.mob_kills.getKills(MobRegistry.getMobStringID(this.mobID));
         }

         return Math.min(var2, this.mobsToKill);
      }
   }
}
