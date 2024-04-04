package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.AreaFinder;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class FindIslandMission extends RunOutMission {
   public static int minFindIslandSeconds = 60;
   public static int maxFindIslandSeconds = 900;
   public static int maxFindIslandDistance = 50;
   private IslandFinder islandFinder;
   private int biomeID;
   private List<String> knownIslands;
   private int ticks;

   public FindIslandMission() {
   }

   public FindIslandMission(int var1, List<String> var2) {
      this.biomeID = var1;
      this.knownIslands = var2;
   }

   public boolean canStart(HumanMob var1) {
      return var1 instanceof ExplorerHumanMob;
   }

   public void start(HumanMob var1) {
      super.start(var1);
      if (var1 instanceof ExplorerHumanMob) {
         ExplorerHumanMob var2 = (ExplorerHumanMob)var1;
         var2.foundIsland = false;
         var2.foundIslandCoord = null;
      }

   }

   public void addSaveData(HumanMob var1, SaveData var2) {
      super.addSaveData(var1, var2);
      var2.addUnsafeString("biomeStringID", BiomeRegistry.getBiomeStringID(this.biomeID));
      var2.addStringList("knownIslands", this.knownIslands);
      var2.addInt("ticks", this.ticks);
   }

   public void applySaveData(HumanMob var1, LoadData var2) {
      super.applySaveData(var1, var2);
      if (var2.hasLoadDataByName("biomeID")) {
         this.biomeID = var2.getInt("biomeID", this.biomeID);
      } else {
         this.biomeID = LevelSave.getMigratedBiomeID(var2.getUnsafeString("biomeStringID", (String)null), true);
      }

      this.knownIslands = var2.getStringList("knownIslands", new ArrayList());

      try {
         this.islandFinder = new IslandFinder(var1, var2.getFirstLoadDataByName("islandFinder"), false);
      } catch (RuntimeException var4) {
         this.islandFinder = null;
      }

      this.ticks = var2.getInt("ticks", this.ticks, false);
   }

   public void serverTick(HumanMob var1) {
      if (this.isOut) {
         if (this.islandFinder == null) {
            this.islandFinder = new IslandFinder(var1, var1.getLevel().getIslandX(), var1.getLevel().getIslandY(), maxFindIslandDistance, true);
         }

         ++this.ticks;
         if (this.ticks >= minFindIslandSeconds * 20) {
            double var2 = (double)this.islandFinder.getMaxTicks() / (double)((maxFindIslandSeconds - minFindIslandSeconds) * 20);
            int var4 = (int)(var2 * (double)(this.ticks - minFindIslandSeconds * 20));
            if (!this.islandFinder.isDone()) {
               if (this.islandFinder.getCurrentTickCount() < var4) {
                  this.islandFinder.tickFinder(var4 - this.islandFinder.getCurrentTickCount());
               }
            } else {
               Point var5 = this.islandFinder.getFirstFind();
               this.endMission(var1, var5);
            }
         }
      }

   }

   private void endMission(HumanMob var1, Point var2) {
      if (!this.isOver()) {
         this.markOver();
         if (var1 instanceof ExplorerHumanMob) {
            ExplorerHumanMob var3 = (ExplorerHumanMob)var1;
            var3.foundIsland = true;
            var3.foundIslandCoord = var2;
         }

         if (var1.home != null) {
            var1.moveIn(var1.home.x, var1.home.y, true);
         }

         var1.sendMovementPacket(true);
         var1.getLevel().settlementLayer.streamTeamMembers().forEach((var1x) -> {
            var1x.sendChatMessage((GameMessage)(new LocalMessage("ui", "settlerreturning", new Object[]{"settler", var1.getLocalization(), "settlement", var1.getLevel().settlementLayer.getSettlementName()})));
         });
      }
   }

   public void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("findIslandTicks: " + this.ticks);
   }

   private class IslandFinder extends AreaFinder {
      private HumanMob mob;

      public IslandFinder(HumanMob var2, int var3, int var4, int var5, boolean var6) {
         super(var3, var4, var5, var6);
         this.mob = var2;
      }

      public IslandFinder(HumanMob var2, LoadData var3, boolean var4) throws RuntimeException {
         super(var3, var4);
         this.mob = var2;
      }

      public boolean checkPoint(int var1, int var2) {
         if (FindIslandMission.this.knownIslands != null && FindIslandMission.this.knownIslands.contains(var1 + "x" + var2)) {
            return false;
         } else if (!this.mob.getLevel().getServer().world.worldEntity.isWithinWorldBorder(var1, var2)) {
            return false;
         } else {
            return this.mob.getLevel().getServer().levelCache.getBiomeID(var1, var2) == FindIslandMission.this.biomeID;
         }
      }
   }
}
