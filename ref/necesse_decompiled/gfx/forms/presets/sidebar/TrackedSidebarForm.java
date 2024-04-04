package necesse.gfx.forms.presets.sidebar;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.GameCache;
import necesse.engine.GlobalData;
import necesse.engine.achievements.Achievement;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormAchievementTrackedComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormQuestTrackedComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;

public class TrackedSidebarForm extends SidebarForm {
   private static LinkedList<TrackedSidebarForm> forms = new LinkedList();
   private static LinkedList<Integer> trackedQuests = new LinkedList();
   private static LinkedList<Achievement> trackedAchievements = new LinkedList();
   private MainGameFormManager mainFormManager;
   private Client client;
   private LinkedList<Quest> quests = new LinkedList();

   public static void addTrackedQuest(Client var0, Quest var1) {
      trackedQuests.add(var1.getUniqueID());
      forms.forEach((var0x) -> {
         var0x.updateList(true);
      });
      saveTrackedQuests(var0);
   }

   public static void removeTrackedQuest(Client var0, Quest var1) {
      removeTrackedQuest(var0, var1.getUniqueID());
   }

   public static void removeTrackedQuest(Client var0, int var1) {
      trackedQuests.remove(var1);
      forms.forEach((var0x) -> {
         var0x.updateList(true);
      });
      saveTrackedQuests(var0);
   }

   public static boolean isQuestTracked(Quest var0) {
      return trackedQuests.contains(var0.getUniqueID());
   }

   public static void addTrackedAchievement(Achievement var0) {
      if (!trackedAchievements.contains(var0)) {
         trackedAchievements.add(var0);
         forms.forEach((var0x) -> {
            var0x.updateList(true);
         });
         saveTrackedAchievements();
      }
   }

   public static void removeTrackedAchievement(Achievement var0) {
      trackedAchievements.remove(var0);
      forms.forEach((var0x) -> {
         var0x.updateList(true);
      });
      saveTrackedAchievements();
   }

   public static boolean isAchievementTracked(Achievement var0) {
      return trackedAchievements.contains(var0);
   }

   public static void loadTrackedQuests(Client var0) {
      trackedQuests.clear();
      LoadData var1 = GameCache.getSave(var0.loading.getClientCachePath("TrackedQuests"));
      if (var1 != null) {
         int[] var2 = var1.getIntArray("quests", new int[0]);
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            trackedQuests.add(var5);
         }

         forms.forEach((var0x) -> {
            var0x.updateList(true);
         });
      }
   }

   private static void saveTrackedQuests(Client var0) {
      trackedQuests.removeIf((var1x) -> {
         return var0.quests.getQuest(var1x) == null;
      });
      SaveData var1 = new SaveData("TrackedQuests");
      var1.addIntArray("quests", trackedQuests.stream().mapToInt((var0x) -> {
         return var0x;
      }).toArray());
      GameCache.cacheSave(var1, var0.loading.getClientCachePath("TrackedQuests"));
   }

   public static void loadTrackedAchievements() {
      LoadData var0 = GameCache.getSave("trackedachievements");
      if (var0 != null) {
         String[] var1 = var0.getStringArray("achievements", new String[0]);
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            GlobalData.achievements().getAchievements().stream().filter((var1x) -> {
               return var1x.stringID.equals(var4);
            }).findFirst().ifPresent((var0x) -> {
               trackedAchievements.add(var0x);
            });
         }

      }
   }

   private static void saveTrackedAchievements() {
      SaveData var0 = new SaveData("TrackedAchievements");
      var0.addStringArray("achievements", (String[])trackedAchievements.stream().map((var0x) -> {
         return var0x.stringID;
      }).toArray((var0x) -> {
         return new String[var0x];
      }));
      GameCache.cacheSave(var0, "trackedachievements");
   }

   public TrackedSidebarForm(MainGameFormManager var1) {
      super("trackedquests", var1.getSidebarWidth(), 0);
      this.mainFormManager = var1;
      this.drawBase = false;
   }

   public void onSidebarUpdate(int var1, int var2) {
      super.onSidebarUpdate(var1, var2);
      this.updateList(false);
   }

   public void updateList(boolean var1) {
      this.quests.clear();
      Integer[] var2 = (Integer[])trackedQuests.toArray(new Integer[0]);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Integer var5 = var2[var4];
         Quest var6 = this.client.quests.getQuest(var5);
         if (var6 == null) {
            removeTrackedQuest(this.client, var5);
            return;
         }

         this.quests.add(var6);
      }

      this.clearComponents();
      this.setWidth(this.mainFormManager.getSidebarWidth());
      FormFlow var7 = new FormFlow(0);
      boolean var8;
      Iterator var9;
      if (!trackedAchievements.isEmpty()) {
         this.addComponent(new FormLocalLabel("ui", "achievements", (new FontOptions(20)).outline().color(new Color(200, 200, 200)), -1, 5, var7.next(20)));
         var8 = false;

         for(var9 = trackedAchievements.iterator(); var9.hasNext(); var8 = true) {
            Achievement var10 = (Achievement)var9.next();
            if (var8) {
               var7.next(10);
            }

            this.addComponent((FormAchievementTrackedComponent)var7.nextY(new FormAchievementTrackedComponent(0, 0, this.getWidth(), Math.min(this.getWidth(), 200), var10)));
         }

         var7.next(5);
      }

      if (!this.quests.isEmpty()) {
         var7.next(5);
         this.addComponent(new FormLocalLabel("ui", "quests", (new FontOptions(20)).outline().color(new Color(200, 200, 200)), -1, 5, var7.next(20)));
         var8 = false;

         for(var9 = this.quests.iterator(); var9.hasNext(); var8 = true) {
            Quest var11 = (Quest)var9.next();
            if (var8) {
               var7.next(10);
            }

            this.addComponent((FormQuestTrackedComponent)var7.nextY(new FormQuestTrackedComponent(0, 0, this.getWidth(), Math.min(this.getWidth(), 200), var11)));
         }

         var7.next(5);
      }

      this.setHeight(var7.next());
      if (var1) {
         this.mainFormManager.fixSidebar();
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.quests.stream().anyMatch(Quest::isRemoved)) {
         this.updateList(true);
      }

      if (trackedAchievements.removeIf(Achievement::isCompleted)) {
         saveTrackedAchievements();
         this.updateList(true);
      }

      super.draw(var1, var2, var3);
   }

   public List<Rectangle> getHitboxes() {
      return super.getHitboxes();
   }

   public boolean isMouseOver(InputEvent var1) {
      return false;
   }

   public boolean isValid(Client var1) {
      return true;
   }

   public void onAdded(Client var1) {
      super.onAdded(var1);
      this.client = var1;
      this.updateList(true);
      forms.add(this);
   }

   public void onRemoved(Client var1) {
      super.onRemoved(var1);
      forms.remove(this);
   }
}
