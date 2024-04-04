package necesse.engine.quest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public abstract class KillMobsQuest extends Quest {
   protected ArrayList<KillObjective> objectives;
   protected GameMessage description;

   public KillMobsQuest() {
      this.objectives = new ArrayList();
   }

   public KillMobsQuest(KillObjective var1, KillObjective... var2) {
      this.objectives = new ArrayList();
      this.objectives = new ArrayList();
      this.objectives.add(var1);
      this.objectives.addAll(Arrays.asList(var2));
   }

   public KillMobsQuest(String var1, int var2) {
      this(new KillObjective(var1, var2));
   }

   public KillMobsQuest(int var1, int var2) {
      this(new KillObjective(var1, var2));
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         KillObjective var3 = (KillObjective)var2.next();
         SaveData var4 = new SaveData("objective");
         var4.addUnsafeString("mobStringID", MobRegistry.getMobStringID(var3.mobID));
         var4.addInt("mobsToKill", var3.mobsToKill);
         var4.addInt("currentKills", var3.currentKills);
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
         KillObjective var5 = new KillObjective(var3, var4);
         var5.currentKills = var1.getInt("currentKills", var5.currentKills);
         this.objectives.add(var5);
      }

      Iterator var8 = var1.getLoadDataByName("objective").iterator();

      while(var8.hasNext()) {
         LoadData var9 = (LoadData)var8.next();
         String var10 = var9.getUnsafeString("mobStringID", (String)null, false);
         if (var10 != null) {
            int var11 = MobRegistry.getMobID(var10);
            if (var11 == -1) {
               throw new IllegalStateException("Could not find mob with stringID " + var10);
            }

            int var6 = var9.getInt("mobsToKill", 1);
            KillObjective var7 = new KillObjective(var11, var6);
            var7.currentKills = var9.getInt("currentKills", var7.currentKills);
            this.objectives.add(var7);
         }
      }

      if (this.objectives.isEmpty()) {
         throw new IllegalStateException("Could not find any objectives");
      }
   }

   public void setupSpawnPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.objectives.size());
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         KillObjective var3 = (KillObjective)var2.next();
         var1.putNextInt(var3.mobID);
         var1.putNextShortUnsigned(var3.mobsToKill);
      }

      super.setupSpawnPacket(var1);
      this.setupPacket(var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      this.objectives.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextInt();
         int var5 = var1.getNextShortUnsigned();
         this.objectives.add(new KillObjective(var4, var5));
      }

      super.applySpawnPacket(var1);
      this.applyPacket(var1);
   }

   public void setupPacket(PacketWriter var1) {
      super.setupPacket(var1);
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         KillObjective var3 = (KillObjective)var2.next();
         var1.putNextShortUnsigned(var3.currentKills);
      }

   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      KillObjective var3;
      for(Iterator var2 = this.objectives.iterator(); var2.hasNext(); var3.currentKills = var1.getNextShortUnsigned()) {
         var3 = (KillObjective)var2.next();
      }

   }

   public void tick(ServerClient var1) {
      Iterator var2 = this.objectives.iterator();

      while(var2.hasNext()) {
         KillObjective var3 = (KillObjective)var2.next();
         var3.tick(this, var1);
      }

   }

   public GameMessage getDescription() {
      return null;
   }

   public boolean canComplete(NetworkClient var1) {
      Iterator var2 = this.objectives.iterator();

      KillObjective var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (KillObjective)var2.next();
      } while(var3.currentKills >= var3.mobsToKill);

      return false;
   }

   public void drawProgress(PlayerMob var1, int var2, int var3, int var4, Color var5, boolean var6) {
      for(Iterator var7 = this.objectives.iterator(); var7.hasNext(); var3 += 15) {
         KillObjective var8 = (KillObjective)var7.next();
         String var9 = Localization.translate("quests", "killmob", "mob", MobRegistry.getDisplayName(var8.mobID));
         FontOptions var10 = (new FontOptions(16)).outline(var6);
         if (var5 != null) {
            var10.color(var5);
         }

         FontManager.bit.drawString((float)var2, (float)var3, var9, var10);
         var3 += 16;
         String var11 = MobRegistry.getKillHint(var8.mobID);
         if (var11 != null) {
            FontOptions var12 = (new FontOptions(12)).outline(var6);
            if (var5 != null) {
               var12.color(var5);
            }

            ArrayList var13 = GameUtils.breakString(var11, var12, var4);

            for(Iterator var14 = var13.iterator(); var14.hasNext(); var3 += 12) {
               String var15 = (String)var14.next();
               FontManager.bit.drawString((float)var2, (float)(var3 + 2), var15, var12);
            }

            var3 += 2;
         }

         float var16 = var8.mobsToKill == 0 ? 1.0F : (float)var8.currentKills / (float)var8.mobsToKill;
         Color var17 = var16 == 1.0F ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
         FontOptions var18 = (new FontOptions(16)).outline(var6).color(var17);
         Achievement.drawProgressbarText(var2, var3, var4, 5, var16, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, var8.currentKills + "/" + var8.mobsToKill, var18);
      }

   }

   public int getDrawProgressHeight(int var1, boolean var2) {
      int var3 = 0;
      Iterator var4 = this.objectives.iterator();

      while(var4.hasNext()) {
         KillObjective var5 = (KillObjective)var4.next();
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

   public static class KillObjective {
      public int mobID;
      public int mobsToKill;
      protected int currentKills;
      protected HashMap<Long, Integer> prevClientKills;

      public KillObjective(int var1, int var2) {
         this.prevClientKills = new HashMap();
         if (var1 < 0) {
            throw new IllegalArgumentException("mobID cannot be negative");
         } else if (var2 < 1) {
            throw new IllegalArgumentException("mobsToKill cannot be less than 1");
         } else {
            this.mobID = var1;
            this.mobsToKill = var2;
         }
      }

      public KillObjective(String var1, int var2) {
         this(MobRegistry.getMobID(var1), var2);
      }

      public void tick(KillMobsQuest var1, ServerClient var2) {
         int var3 = var2.characterStats().mob_kills.getKills(MobRegistry.getMobStringID(this.mobID));
         int var4 = (Integer)this.prevClientKills.getOrDefault(var2.authentication, -1);
         if (var4 != -1) {
            int var5 = Math.max(0, var3 - var4);
            if (var5 > 0 && this.currentKills < this.mobsToKill) {
               this.currentKills = Math.min(this.mobsToKill, this.currentKills + var5);
               var1.markDirty();
            }
         }

         this.prevClientKills.put(var2.authentication, var3);
      }
   }
}
