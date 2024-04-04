package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.GameRaidFrequency;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.TicketSystemList;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.events.SettlementDefendZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.jobs.UseWorkstationLevelJob;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.regionSystem.SemiRegion;

public class SettlementLevelData extends LevelData {
   public static int MIN_SECONDS_RAID_TIMER = 5400;
   public static int MAX_SECONDS_RAID_TIMER = 9000;
   public static int INCREASES_RAID_TIMER_SECONDS_PER_RAID = 1800;
   public static int UPPER_LIMIT_RAID_TIMER_SECONDS = 18000;
   public static int MIN_SECONDS_TRAVELING_TIMER = 300;
   public static int MAX_SECONDS_TRAVELING_TIMER = 1200;
   public static int AUTO_EXPAND_DEFEND_ZONE_RANGE = 12;
   public static int MAX_RESTRICT_ZONES = 50;
   public static final ArrayList<TravelingHumanOdds> travelingHumanMobs = new ArrayList();
   public static TravelingHumanOdds travelingRecruits = new TravelingHumanOdds("recruit") {
      public boolean canSpawn(SettlementLevelData var1) {
         int var2 = var1.countTotalSettlers();
         int var3 = var1.getLevel().getWorldSettings().maxSettlersPerSettlement;
         if (var3 >= 0 && var2 >= var3) {
            return false;
         } else {
            float var4 = 2.0F;

            for(int var5 = 0; var5 < var1.getQuestTiersCompleted() && var5 < SettlementQuestTier.questTiers.size(); ++var5) {
               var4 += ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var5)).getExpectedSettlersIncrease();
            }

            if ((float)var2 < var4) {
               return true;
            } else {
               double var7 = var1.settlers.stream().map(LevelSettler::getMob).filter(Objects::nonNull).mapToInt(SettlerMob::getSettlerHappiness).average().orElse(100.0);
               return var7 > 50.0;
            }
         }
      }

      public int getTickets(SettlementLevelData var1) {
         return 1000;
      }

      public TravelingHumanArrive getNewHuman(SettlementLevelData var1) {
         TicketSystemList var2 = new TicketSystemList();
         Iterator var3 = SettlerRegistry.getSettlers().iterator();

         while(var3.hasNext()) {
            Settler var4 = (Settler)var3.next();
            var4.addNewRecruitSettler(var1, false, var2);
         }

         HumanMob var6;
         do {
            if (var2.isEmpty()) {
               return null;
            }

            Supplier var5 = (Supplier)var2.getAndRemoveRandomObject(GameRandom.globalRandom);
            var6 = (HumanMob)var5.get();
         } while(var6 == null);

         return new TravelingHumanArrive(this, var6);
      }
   };
   public static TravelingHumanOdds deadRecruits = new TravelingHumanOdds("recruit") {
      public boolean canSpawn(SettlementLevelData var1) {
         return !var1.diedSettlerRecruitStringIDs.isEmpty();
      }

      public int getTickets(SettlementLevelData var1) {
         return 1000;
      }

      public TravelingHumanArrive getNewHuman(SettlementLevelData var1) {
         while(true) {
            if (!var1.diedSettlerRecruitStringIDs.isEmpty()) {
               String var2 = (String)var1.diedSettlerRecruitStringIDs.remove(GameRandom.globalRandom.nextInt(var1.diedSettlerRecruitStringIDs.size()));
               Settler var3 = SettlerRegistry.getSettler(var2);
               if (var3 == null) {
                  continue;
               }

               SettlerMob var4 = var3.getNewSettlerMob(var1.getLevel());
               if (!(var4 instanceof HumanMob)) {
                  continue;
               }

               return new TravelingHumanArrive(this, (HumanMob)var4);
            }

            return null;
         }
      }
   };
   private long settlementTicks;
   public ArrayList<LevelSettler> settlers = new ArrayList();
   private HashMap<Point, SettlementBed> beds = new HashMap();
   public SettlementRoomsManager rooms = new SettlementRoomsManager(this);
   private Point objectEntityPos;
   private Point homestonePos;
   private ArrayList<Waystone> waystones = new ArrayList();
   private ArrayList<SettlementInventory> storage = new ArrayList();
   private ArrayList<SettlementWorkstation> workstations = new ArrayList();
   private SettlementWorkZoneManager workZones = new SettlementWorkZoneManager(this);
   private ItemCategoriesFilter newSettlerDiet;
   public boolean newSettlerSelfManageEquipment;
   public boolean newSettlerEquipmentPreferArmorSets;
   private ItemCategoriesFilter newSettlerEquipmentFilter;
   private int restrictZoneIndexCounter;
   private HashMap<Integer, RestrictZone> restrictZones;
   private int newSettlerRestrictZoneUniqueID;
   private Zoning defendZone;
   public boolean autoExpandDefendZone;
   public SettlementStorageRecords storageRecords;
   public boolean firstExpandDefendZone;
   private String questTier;
   private boolean hasCompletedQuestTier;
   private int totalCompletedQuests;
   private HashMap<Long, SettlementClientQuests> clientQuestsMap;
   private String startAtQuestTier;
   private long nextTravelingHuman;
   private int curTravelingHuman;
   private boolean nextTravelingHumanRecruit;
   private String lastTravelingHumanIdentifier;
   private ArrayList<String> diedSettlerRecruitStringIDs;
   private boolean hasNonAFKTeamMembers;
   private GameRaidFrequency lastRaidFrequencySetting;
   private long nextRaid;
   private int currentRaid;
   private float nextRaidDifficultyMod;
   private boolean lastRaidCheckWasNight;
   private int raidsCounter;
   private final HashSet<Class<? extends ContainerEvent>> eventsToSend;

   public static TravelingHumanArrive getNewTravelingHuman(GameRandom var0, SettlementLevelData var1) {
      TicketSystemList var2 = new TicketSystemList();
      Iterator var3 = travelingHumanMobs.iterator();

      while(var3.hasNext()) {
         TravelingHumanOdds var4 = (TravelingHumanOdds)var3.next();
         if (var4.canSpawn(var1)) {
            var2.addObject(var4.getTickets(var1), var4);
         }
      }

      while(!var2.isEmpty()) {
         TravelingHumanOdds var5 = (TravelingHumanOdds)var2.getAndRemoveRandomObject(var0);
         if (var5 != null) {
            TravelingHumanArrive var6 = var5.getNewHuman(var1);
            if (var6 != null) {
               return var6;
            }
         }
      }

      return null;
   }

   public SettlementLevelData() {
      this.newSettlerDiet = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
      this.newSettlerSelfManageEquipment = true;
      this.newSettlerEquipmentPreferArmorSets = true;
      this.newSettlerEquipmentFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
      this.restrictZones = new HashMap();
      this.defendZone = new Zoning();
      this.autoExpandDefendZone = true;
      this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(0)).stringID;
      this.clientQuestsMap = new HashMap();
      this.diedSettlerRecruitStringIDs = new ArrayList();
      this.nextRaidDifficultyMod = 1.0F;
      this.eventsToSend = new HashSet();
   }

   public void setLevel(Level var1) {
      boolean var2 = this.level == null;
      super.setLevel(var1);
      this.lastRaidFrequencySetting = var1.getWorldSettings().raidFrequency;
      this.defendZone.limits = new Rectangle(var1.width, var1.height);
      if (var2) {
         this.storageRecords = new SettlementStorageRecords(var1);
         this.resetTravelingHumanTime(false, false);
         this.resetNextRaidTimer(false, false);
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("settlementTicks", this.settlementTicks);
      var1.addUnsafeString("questTier", this.questTier);
      var1.addBoolean("hasCompletedQuestTier", this.hasCompletedQuestTier);
      var1.addInt("totalCompletedQuests", this.totalCompletedQuests);
      SaveData var2 = new SaveData("clientQuests");
      Iterator var3 = this.clientQuestsMap.values().iterator();

      SaveData var5;
      while(var3.hasNext()) {
         SettlementClientQuests var4 = (SettlementClientQuests)var3.next();
         var5 = new SaveData("quests");
         var4.addSaveData(var5);
         var2.addSaveData(var5);
      }

      var1.addSaveData(var2);
      var1.addLong("nextTravelingHuman", this.nextTravelingHuman);
      var1.addInt("curTravelingHuman", this.curTravelingHuman);
      var1.addBoolean("nextTravelingHumanRecruit", this.nextTravelingHumanRecruit);
      if (this.lastTravelingHumanIdentifier != null) {
         var1.addSafeString("lastTravelingHumanIdentifier", this.lastTravelingHumanIdentifier);
      }

      if (!this.diedSettlerRecruitStringIDs.isEmpty()) {
         var1.addSafeStringCollection("diedSettlerRecruitStringIDs", this.diedSettlerRecruitStringIDs);
      }

      var1.addLong("nextRaid", this.nextRaid);
      var1.addInt("currentRaid", this.currentRaid);
      var1.addFloat("nextRaidDifficultyMod", this.nextRaidDifficultyMod);
      var1.addInt("raidsCounter", this.raidsCounter);
      if (this.objectEntityPos != null) {
         var1.addPoint("entityPos", this.objectEntityPos);
      }

      if (this.homestonePos != null) {
         var1.addPoint("homestonePos", this.homestonePos);
      }

      SaveData var13 = new SaveData("waystones");
      Iterator var14 = this.waystones.iterator();

      SaveData var6;
      while(var14.hasNext()) {
         Waystone var16 = (Waystone)var14.next();
         var6 = new SaveData("");
         var16.addSaveData(var6);
         var13.addSaveData(var6);
      }

      var1.addSaveData(var13);
      SaveData var15 = new SaveData("newSettlerDiet");
      this.newSettlerDiet.addSaveData(var15);
      var1.addSaveData(var15);
      var1.addBoolean("newSettlerSelfManageEquipment", this.newSettlerSelfManageEquipment);
      var1.addBoolean("newSettlerEquipmentPreferArmorSets", this.newSettlerEquipmentPreferArmorSets);
      var5 = new SaveData("newSettlerEquipmentFilter");
      this.newSettlerEquipmentFilter.addSaveData(var5);
      var1.addSaveData(var5);
      if (this.newSettlerRestrictZoneUniqueID != 0) {
         var1.addInt("newSettlerRestrictZoneUniqueID", this.newSettlerRestrictZoneUniqueID);
      }

      if (!this.restrictZones.isEmpty()) {
         var6 = new SaveData("restrictZones");
         this.restrictZones.values().stream().sorted(Comparator.comparingInt((var0) -> {
            return var0.index;
         })).forEach((var1x) -> {
            SaveData var2 = new SaveData("restrictZone");
            var1x.addSaveData(var2);
            var6.addSaveData(var2);
         });
         var1.addSaveData(var6);
      }

      var1.addBoolean("autoExpandDefendZone", this.autoExpandDefendZone);
      this.defendZone.addZoneSaveData("defendZone", var1);
      var6 = new SaveData("SETTLERS");
      Iterator var7 = this.settlers.iterator();

      SaveData var9;
      while(var7.hasNext()) {
         LevelSettler var8 = (LevelSettler)var7.next();
         var9 = new SaveData("SETTLER");
         var8.addSaveData(var9);
         var6.addSaveData(var9);
      }

      var1.addSaveData(var6);
      SaveData var17 = new SaveData("BEDS");
      Iterator var18 = this.beds.values().iterator();

      SaveData var10;
      while(var18.hasNext()) {
         SettlementBed var20 = (SettlementBed)var18.next();
         var10 = new SaveData("BED");
         var20.addSaveData(var10);
         var17.addSaveData(var10);
      }

      var1.addSaveData(var17);
      this.rooms.addSaveData(var1);
      SaveData var19 = new SaveData("STORAGE");
      Iterator var21 = this.storage.iterator();

      while(var21.hasNext()) {
         SettlementInventory var22 = (SettlementInventory)var21.next();
         SaveData var11 = new SaveData("INVENTORY");
         var22.addSaveData(var11);
         var19.addSaveData(var11);
      }

      var1.addSaveData(var19);
      var9 = new SaveData("WORKSTATIONS");
      Iterator var23 = this.workstations.iterator();

      while(var23.hasNext()) {
         SettlementWorkstation var24 = (SettlementWorkstation)var23.next();
         SaveData var12 = new SaveData("WORKSTATION");
         var24.addSaveData(var12);
         var9.addSaveData(var12);
      }

      var1.addSaveData(var9);
      var10 = new SaveData("WORKZONES");
      this.workZones.addSaveData(var10);
      var1.addSaveData(var10);
   }

   public static boolean hasObjectEntityPos(LoadData var0) {
      return var0.hasLoadDataByName("entityPos");
   }

   public static GameMessage getName(LoadData var0) {
      LoadData var1 = var0.getFirstLoadDataByName("name");
      return var1 != null ? GameMessage.loadSave(var1) : null;
   }

   public static long getOwnerAuth(LoadData var0) {
      return var0.getLong("ownerAuth", -1L, false);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.settlementTicks = var1.getLong("settlementTicks", 0L, false);
      String var2 = var1.getUnsafeString("questTier", (String)null);
      this.hasCompletedQuestTier = var1.getBoolean("hasCompletedQuestTier", false, false);
      int var3;
      if (var2 == null) {
         this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(0)).stringID;
      } else {
         try {
            var3 = Integer.parseInt(var2);
            var3 = Math.max(var3, 0);
            if (var3 >= SettlementQuestTier.questTiers.size()) {
               this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(SettlementQuestTier.questTiers.size() - 1)).stringID;
               this.hasCompletedQuestTier = true;
            } else {
               this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var3)).stringID;
               this.hasCompletedQuestTier = false;
            }
         } catch (NumberFormatException var21) {
            this.questTier = var2;
            if (SettlementQuestTier.questTiers.stream().noneMatch((var1x) -> {
               return var1x.stringID.equals(var2);
            })) {
               this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(0)).stringID;
            }
         }
      }

      if (this.hasCompletedQuestTier) {
         var3 = SettlementQuestTier.getTierIndex(this.questTier);
         if (var3 < SettlementQuestTier.questTiers.size() - 1) {
            this.hasCompletedQuestTier = false;
            this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var3 + 1)).stringID;
         }
      }

      this.totalCompletedQuests = var1.getInt("totalCompletedQuests", 0);
      var3 = var1.getInt("questUniqueID", 0, false);
      if (var3 != 0) {
         this.getLevel().getServer().world.getQuests().removeQuest(var3);
      }

      int var4 = var1.getInt("tierQuestUniqueID", 0, false);
      if (var4 != 0) {
         this.getLevel().getServer().world.getQuests().removeQuest(var3);
      }

      LoadData var5 = var1.getFirstLoadDataByName("clientQuests");
      LoadData var7;
      if (var5 != null) {
         Iterator var6 = var5.getLoadDataByName("quests").iterator();

         while(var6.hasNext()) {
            var7 = (LoadData)var6.next();

            try {
               SettlementClientQuests var8 = new SettlementClientQuests(this, var7);
               this.clientQuestsMap.put(var8.clientAuth, var8);
            } catch (LoadDataException var19) {
               System.err.println("Could not load settlement client quests at level " + this.level.getIdentifier() + ": " + var19.getMessage());
            } catch (Exception var20) {
               System.err.println("Unknown error loading settlement client quests at level " + this.level.getIdentifier());
               var20.printStackTrace();
            }
         }
      } else {
         GameLog.warn.println("Could not load any settlement client quests at level " + this.level.getIdentifier());
      }

      if (var1.hasLoadDataByName("ownerAuth")) {
         boolean var22 = var1.getBoolean("nameSet", false, false);
         Object var25 = getName(var1);
         if (var25 == null) {
            var25 = new LocalMessage("settlement", "defname", "biome", this.level.biome.getLocalization());
         }

         long var26 = var1.getLong("ownerAuth", -1L);
         boolean var10 = var1.getBoolean("isPrivate", false);
         this.level.settlementLayer.migrate(var22, (GameMessage)var25, var26, var10);
      }

      if (var1.getFirstLoadDataByName("questGeneratedWorldTime") != null) {
         long var23 = this.level.settlementLayer.getOwnerAuth();
         if (var23 != -1L) {
            PlayerTeam var28 = this.level.settlementLayer.getTeam();
            if (var28 != null) {
               System.out.println("Migrated old settlement quest tier to " + var28.getMemberCount() + " team members: " + this.questTier);
               Iterator var9 = var28.getMembers().iterator();

               while(var9.hasNext()) {
                  long var33 = (Long)var9.next();
                  SettlementClientQuests var12 = this.getClientsQuests(var33);
                  var12.setTier(SettlementQuestTier.getTier(this.questTier), this.hasCompletedQuestTier);
               }
            } else {
               SettlementClientQuests var30 = this.getClientsQuests(var23);
               System.out.println("Migrated old settlement quest tier to owner " + var23 + ": " + this.questTier);
               var30.setTier(SettlementQuestTier.getTier(this.questTier), this.hasCompletedQuestTier);
            }
         }
      }

      this.nextTravelingHuman = var1.getLong("nextTravelingHuman", this.nextTravelingHuman);
      this.curTravelingHuman = var1.getInt("curTravelingHuman", this.curTravelingHuman);
      this.nextTravelingHumanRecruit = var1.getBoolean("nextTravelingHumanRecruit", this.nextTravelingHumanRecruit);
      this.lastTravelingHumanIdentifier = var1.getSafeString("lastTravelingHumanIdentifier", this.lastTravelingHumanIdentifier, false);
      this.diedSettlerRecruitStringIDs = new ArrayList(var1.getSafeStringCollection("diedSettlerRecruitStringIDs", this.diedSettlerRecruitStringIDs, false));
      this.nextRaid = var1.getLong("nextRaid", this.nextRaid);
      this.currentRaid = var1.getInt("currentRaid", this.currentRaid);
      this.nextRaidDifficultyMod = var1.getFloat("nextRaidDifficultyMod", this.nextRaidDifficultyMod);
      this.lastRaidCheckWasNight = this.getLevel().getWorldEntity().isNight();
      this.raidsCounter = var1.getInt("raidsThisTier", this.raidsCounter, false);
      this.raidsCounter = var1.getInt("raidsCounter", this.raidsCounter);
      this.objectEntityPos = var1.getPoint("entityPos", (Point)null, false);
      this.getLevel().settlementLayer.setActive(this.objectEntityPos != null);
      this.homestonePos = var1.getPoint("homestonePos", (Point)null, false);
      this.waystones.clear();
      LoadData var24 = var1.getFirstLoadDataByName("waystones");
      LoadData var32;
      if (var24 != null) {
         int var27 = this.getMaxWaystones();
         Iterator var29 = var24.getLoadData().iterator();

         while(var29.hasNext()) {
            var32 = (LoadData)var29.next();
            if (this.waystones.size() >= var27) {
               break;
            }

            try {
               this.waystones.add(new Waystone(var32));
            } catch (Exception var18) {
               GameLog.warn.println("Could not load saved waystone");
            }
         }
      }

      var7 = var1.getFirstLoadDataByName("newSettlerDiet");
      if (var7 != null) {
         this.newSettlerDiet.applyLoadData(var7);
      }

      this.newSettlerSelfManageEquipment = var1.getBoolean("newSettlerSelfManageEquipment", this.newSettlerSelfManageEquipment, false);
      this.newSettlerEquipmentPreferArmorSets = var1.getBoolean("newSettlerEquipmentPreferArmorSets", this.newSettlerEquipmentPreferArmorSets, false);
      LoadData var31 = var1.getFirstLoadDataByName("newSettlerEquipmentFilter");
      if (var31 != null) {
         this.newSettlerEquipmentFilter.applyLoadData(var31);
      }

      this.restrictZoneIndexCounter = 0;
      this.newSettlerRestrictZoneUniqueID = var1.getInt("newSettlerRestrictZoneUniqueID", this.newSettlerRestrictZoneUniqueID, false);
      this.restrictZones = new HashMap();
      var32 = var1.getFirstLoadDataByName("restrictZones");
      if (var32 != null) {
         Iterator var34 = var32.getLoadDataByName("restrictZone").iterator();

         while(var34.hasNext()) {
            LoadData var11 = (LoadData)var34.next();

            try {
               RestrictZone var37 = new RestrictZone(this, this.restrictZoneIndexCounter++, var11);
               this.restrictZones.put(var37.uniqueID, var37);
            } catch (LoadDataException var16) {
               System.err.println("Could not load settlement restrict zone at level " + this.level.getIdentifier() + ": " + var16.getMessage());
            } catch (Exception var17) {
               System.err.println("Unknown error loading settlement restrict zone at level " + this.level.getIdentifier());
               var17.printStackTrace();
            }
         }
      }

      if (var1.getFirstLoadDataByName("autoExpandDefendZone") == null) {
         this.firstExpandDefendZone = true;
      }

      this.autoExpandDefendZone = var1.getBoolean("autoExpandDefendZone", this.autoExpandDefendZone, false);
      this.defendZone.applyZoneSaveData("defendZone", var1);
      this.beds = new HashMap();
      this.rooms = new SettlementRoomsManager(this);
      this.settlers = new ArrayList();
      LoadData var35 = var1.getFirstLoadDataByName("BEDS");
      if (var35 != null) {
         var35.getLoadDataByName("BED").stream().filter(LoadData::isArray).forEach((var1x) -> {
            try {
               SettlementBed var2 = new SettlementBed(this, var1x);
               if (var2.isValidBed()) {
                  this.beds.put(new Point(var2.tileX, var2.tileY), var2);
               }
            } catch (Exception var3) {
               System.err.println("Could not load settlement bed at level " + this.level.getIdentifier());
            }

         });
      }

      this.rooms.loadSaveData(var1);
      Stream var36 = Stream.empty();
      var36 = Stream.concat(var36, var1.getLoadDataByName("SETTLER").stream().filter(LoadData::isArray));
      LoadData var38 = var1.getFirstLoadDataByName("SETTLERS");
      if (var38 != null) {
         var36 = Stream.concat(var36, var38.getLoadDataByName("SETTLER").stream().filter(LoadData::isArray));
      }

      var36.forEach((var1x) -> {
         try {
            LevelSettler var2 = new LevelSettler(this, var1x);
            this.settlers.add(var2);
         } catch (Exception var3) {
            System.err.println("Could not load settlement settler at level " + this.level.getIdentifier());
            var3.printStackTrace();
         }

      });
      this.storage = new ArrayList();
      LoadData var13 = var1.getFirstLoadDataByName("STORAGE");
      if (var13 != null) {
         var13.getLoadDataByName("INVENTORY").stream().filter(LoadData::isArray).forEachOrdered((var1x) -> {
            try {
               SettlementInventory var2 = SettlementInventory.fromLoadData(this.level, var1x);
               this.storage.add(var2);
            } catch (LoadDataException var3) {
               System.err.println("Could not load settlement inventory at level " + this.level.getIdentifier() + ": " + var3.getMessage());
            } catch (Exception var4) {
               System.err.println("Unknown error loading settlement inventory at level " + this.level.getIdentifier());
               var4.printStackTrace();
            }

         });
      }

      this.workstations = new ArrayList();
      LoadData var14 = var1.getFirstLoadDataByName("WORKSTATIONS");
      if (var14 != null) {
         var14.getLoadDataByName("WORKSTATION").stream().filter(LoadData::isArray).forEachOrdered((var1x) -> {
            try {
               SettlementWorkstation var2 = new SettlementWorkstation(this, var1x);
               this.workstations.add(var2);
            } catch (LoadDataException var3) {
               System.err.println("Could not load settlement work station at level " + this.level.getIdentifier() + ": " + var3.getMessage());
            } catch (Exception var4) {
               System.err.println("Unknown error loading settlement work station at level " + this.level.getIdentifier());
               var4.printStackTrace();
            }

         });
      }

      LoadData var15 = var1.getFirstLoadDataByName("WORKZONES");
      if (var15 != null) {
         this.workZones = new SettlementWorkZoneManager(this, var15);
      } else {
         this.workZones = new SettlementWorkZoneManager(this);
      }

   }

   public void tickTile(int var1, int var2) {
      this.addOrValidateBed(var1, var2);
      this.rooms.findAndCalculateRoom(var1, var2);
   }

   public SettlementBed addOrValidateBed(int var1, int var2) {
      Point var3 = new Point(var1, var2);
      SettlementBed var4;
      if (!this.beds.containsKey(var3)) {
         var4 = new SettlementBed(this, var3.x, var3.y);
         if (var4.isValidBed()) {
            this.beds.put(var3, var4);
            return var4;
         } else {
            return null;
         }
      } else {
         var4 = (SettlementBed)this.beds.get(var3);
         if (var4.isValidBed()) {
            return var4;
         } else {
            LevelSettler var5 = var4.getSettler();
            if (var5 != null) {
               var5.assignBed((SettlementBed)null);
            }

            this.beds.remove(var3);
            return null;
         }
      }
   }

   public boolean isMobPartOf(SettlerMob var1) {
      Iterator var2 = this.settlers.iterator();

      LevelSettler var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (LevelSettler)var2.next();
      } while(var3.getMob() != var1);

      return true;
   }

   public ArrayList<LevelSettler> getSettlers() {
      return this.settlers;
   }

   public void tick() {
      super.tick();
      if (this.objectEntityPos != null) {
         SettlementFlagObjectEntity var1 = this.getObjectEntity();
         if (var1 == null) {
            this.setObjectEntityPos((Point)null);
         }
      }

      Iterator var7 = this.eventsToSend.iterator();

      while(var7.hasNext()) {
         Class var2 = (Class)var7.next();

         try {
            Constructor var3 = var2.getConstructor(SettlementLevelData.class);
            ContainerEvent var4 = (ContainerEvent)var3.newInstance(this);
            var4.applyAndSendToClientsAt(this.getLevel());
         } catch (NoSuchMethodException var5) {
            System.err.println("Could not send " + var2.getSimpleName() + " from SettlementLevelData, missing constructor with SettlementLevelData parameter");
         } catch (InstantiationException | InvocationTargetException | IllegalAccessException var6) {
            System.err.println("Error constructing " + var2.getSimpleName() + " from SettlementLevelData: " + var6.getClass().getSimpleName() + ", " + var6.getMessage());
         }
      }

      this.eventsToSend.clear();
   }

   public void tickSettlement(SettlementFlagObjectEntity var1) {
      if (this.objectEntityPos != null) {
         if (var1.getX() == this.objectEntityPos.x && var1.getY() == this.objectEntityPos.y) {
            if (this.lastRaidFrequencySetting != this.level.getWorldSettings().raidFrequency) {
               this.resetNextRaidTimer(false, true);
               this.lastRaidFrequencySetting = this.level.getWorldSettings().raidFrequency;
            }

            Performance.record(this.level.tickManager(), "tickSettlement", (Runnable)(() -> {
               ++this.settlementTicks;
               this.workZones.tickSecond();
               if ((this.settlementTicks + 5L) % 10L == 0L) {
                  this.tickJobs();
               }

               LevelSettler var2;
               if (this.settlementTicks % 10L == 0L) {
                  this.tickBedsValid();
                  this.hasNonAFKTeamMembers = this.level.settlementLayer.streamTeamMembers().anyMatch((var0) -> {
                     return !var0.isAFK();
                  });
                  if (!this.level.settlementLayer.isRaidActive() && GameRandom.globalRandom.getChance(0.16F)) {
                     this.tickFindSettlerBeds();
                     this.tickSpawnInSettlers();
                     this.tickHostAchievements();
                  }

                  for(int var1 = 0; var1 < this.settlers.size(); ++var1) {
                     var2 = (LevelSettler)this.settlers.get(var1);
                     if (!var2.isOutsideLevel) {
                        SettlerMob var3 = var2.getMob();
                        if (var3 == null) {
                           SettlementBed var4 = var2.getBed();
                           if (var4 != null) {
                              var4.clearSettler();
                           }

                           this.settlers.remove(var1);
                           --var1;
                           this.sendEvent(SettlementSettlersChangedEvent.class);
                        }
                     } else {
                        SettlersWorldData var8 = SettlersWorldData.getSettlersData(this.level.getServer());
                        var8.returnIfShould(var2.mobUniqueID, this);
                     }
                  }

                  HashSet var5 = new HashSet();
                  Iterator var6 = this.beds.values().iterator();

                  while(var6.hasNext()) {
                     SettlementBed var9 = (SettlementBed)var6.next();
                     if (var9.getSettler() != null && var5.contains(var9.getSettler().mobUniqueID)) {
                        var9.clearSettler();
                        var9.isLocked = false;
                        this.sendEvent(SettlementSettlersChangedEvent.class);
                     }

                     if (var9.getSettler() != null) {
                        var5.add(var9.getSettler().mobUniqueID);
                     }
                  }
               }

               Iterator var7 = this.settlers.iterator();

               while(var7.hasNext()) {
                  var2 = (LevelSettler)var7.next();
                  var2.updateHome();
                  var2.tick();
               }

               this.tickTravelingHuman();
               this.tickNextRaid();
            }));
         }
      }
   }

   protected void tickJobs() {
      Performance.record(this.level.tickManager(), "tickJobs", (Runnable)(() -> {
         SettlementStorageRecords var1 = new SettlementStorageRecords(this.getLevel());
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         JobsLevelData var4 = JobsLevelData.getJobsLevelData(this.getLevel());
         Performance.record(this.level.tickManager(), "getWorkstation", (Runnable)(() -> {
            for(int var4x = 0; var4x < this.workstations.size(); ++var4x) {
               SettlementWorkstation var5 = (SettlementWorkstation)this.workstations.get(var4x);
               if (!var5.isValid()) {
                  this.workstations.remove(var4x);
                  --var4x;
                  (new SettlementSingleWorkstationsEvent(this, var5.tileX, var5.tileY)).applyAndSendToClientsAt(this.getLevel());
               } else {
                  SettlementRequestInventory var6 = var5.getFuelInventory();
                  if (var6 != null) {
                     var2.add(new TempRequest(var6));
                     var6.removeInvalidPickups();
                     var6.dropOffSimulation.update();
                  }

                  SettlementInventory var7 = var5.getProcessingInputInventory();
                  if (var7 != null) {
                     var7.removeInvalidPickups();
                     var7.dropOffSimulation.update();
                  }

                  SettlementInventory var8 = var5.getProcessingOutputInventory();
                  if (var8 != null) {
                     var3.add(new TempStorage(var8));
                     var8.removeInvalidPickups();
                     var8.dropOffSimulation.update();
                  }

                  var4.addJob(new UseWorkstationLevelJob(var5, () -> {
                     return this.workstations.contains(var5);
                  }));
               }
            }

         }));
         ArrayList var5 = new ArrayList(this.storage.size());
         Performance.record(this.level.tickManager(), "getStorage", (Runnable)(() -> {
            for(int var3 = 0; var3 < this.storage.size(); ++var3) {
               SettlementInventory var4 = (SettlementInventory)this.storage.get(var3);
               TempStorage var5x = new TempStorage(var4);
               if (var5x.range == null) {
                  this.storage.remove(var3);
                  --var3;
                  (new SettlementSingleStorageEvent(this, var5x.storage.tileX, var5x.storage.tileY)).applyAndSendToClientsAt(this.getLevel());
               } else {
                  SettlementRequestInventory var6 = var4.getFuelInventory();
                  if (var6 != null) {
                     var2.add(new TempRequest(var6));
                     var6.removeInvalidPickups();
                     var6.dropOffSimulation.update();
                  }

                  var5.add(var5x);
                  var5x.storage.removeInvalidPickups();
                  var5x.storage.dropOffSimulation.update();
               }
            }

         }));
         Performance.record(this.level.tickManager(), "sortStorage", (Runnable)(() -> {
            var5.sort(Comparator.comparingInt((var0) -> {
               return -var0.priority;
            }));
         }));
         Performance.record(this.level.tickManager(), "removeOldStorage", (Runnable)(() -> {
            Iterator var2 = var5.iterator();

            while(var2.hasNext()) {
               TempStorage var3 = (TempStorage)var2.next();
               var4.streamJobsInTile(var3.storage.tileX, var3.storage.tileY).filter((var0) -> {
                  return var0.getID() == LevelJobRegistry.haulFromID;
               }).forEach(LevelJob::remove);
            }

         }));
         Performance.record(this.level.tickManager(), "removeOldOutputs", (Runnable)(() -> {
            Iterator var2 = var3.iterator();

            while(var2.hasNext()) {
               TempStorage var3x = (TempStorage)var2.next();
               var4.streamJobsInTile(var3x.storage.tileX, var3x.storage.tileY).filter((var0) -> {
                  return var0.getID() == LevelJobRegistry.haulFromID;
               }).forEach(LevelJob::remove);
            }

         }));
         Performance.record(this.level.tickManager(), "handleRequests", (Runnable)(() -> {
            Iterator var4x = var2.iterator();

            while(var4x.hasNext()) {
               TempRequest var5x = (TempRequest)var4x.next();
               LinkedList var6 = new LinkedList();
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  TempStorage var8 = (TempStorage)var7.next();
                  this.addInvalidDropOffPositions(var5x, var8, var6);
               }

               var7 = var2.iterator();

               while(var7.hasNext()) {
                  TempRequest var9 = (TempRequest)var7.next();
                  if (var5x != var9) {
                     this.addInvalidDropOffPositions(var5x, var9, var6);
                  }
               }

               var7 = var6.iterator();

               while(var7.hasNext()) {
                  HaulFromLevelJob var10 = (HaulFromLevelJob)var7.next();
                  var4.addJob(var10, true);
               }
            }

         }));
         Performance.record(this.level.tickManager(), "handleOutputs", (Runnable)(() -> {
            Iterator var4x = var3.iterator();

            while(var4x.hasNext()) {
               TempStorage var5x = (TempStorage)var4x.next();
               LinkedList var6 = new LinkedList();
               Performance.record(this.level.tickManager(), "handleStorageJobs", (Runnable)(() -> {
                  this.handleStorageJobs(var5x, var6, var5, (SettlementStorageRecords)null);
               }));
               Iterator var7 = var6.iterator();

               while(var7.hasNext()) {
                  HaulFromLevelJob var8 = (HaulFromLevelJob)var7.next();
                  var4.addJob(var8, true);
               }
            }

         }));
         Performance.record(this.level.tickManager(), "handleStorage", (Runnable)(() -> {
            for(int var4x = 0; var4x < var5.size(); ++var4x) {
               TempStorage var5x = (TempStorage)var5.get(var4x);
               LinkedList var6 = new LinkedList();
               Performance.record(this.level.tickManager(), "handleStorageJobs", (Runnable)(() -> {
                  this.handleStorageJobs(var5x, var6, var5, var1);
               }));
               Performance.record(this.level.tickManager(), "addDropOffs", (Runnable)(() -> {
                  for(int var5xx = 0; var5xx < var4x; ++var5xx) {
                     TempStorage var6x = (TempStorage)var5.get(var5xx);
                     if (var6x.priority > var5x.priority) {
                        this.addDropOffPositions(var5x, var6x, var6);
                     }
                  }

               }));
               Performance.record(this.level.tickManager(), "addJobs", (Runnable)(() -> {
                  Iterator var2 = var6.iterator();

                  while(var2.hasNext()) {
                     HaulFromLevelJob var3 = (HaulFromLevelJob)var2.next();
                     var4.addJob(var3, true);
                  }

               }));
               var5x.storage.haulFromLevelJobs = var6;
            }

         }));
         Performance.record(this.level.tickManager(), "setupRequestJobs", (Runnable)(() -> {
            Iterator var2 = this.storage.iterator();

            while(var2.hasNext()) {
               SettlementInventory var3 = (SettlementInventory)var2.next();
               var4.addJob(new HasStorageLevelJob(var3, () -> {
                  return this.storage.contains(var3);
               }), true);
            }

         }));
         Performance.record(this.level.tickManager(), "tickWorkZones", (Runnable)(() -> {
            this.workZones.tickJobs();
         }));
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            TempRequest var7 = (TempRequest)var6.next();
            var7.requestInventory.addHaulJobs(var4, var1, var7.priority);
         }

         this.storageRecords = var1;
      }));
   }

   private void handleStorageJobs(TempStorage var1, LinkedList<HaulFromLevelJob> var2, ArrayList<TempStorage> var3, SettlementStorageRecords var4) {
      InventoryRange var5 = new InventoryRange(var1.range.inventory.copy(), var1.range.startSlot, var1.range.endSlot);

      for(int var6 = var1.range.endSlot; var6 >= var1.range.startSlot; --var6) {
         InventoryItem var7 = var5.inventory.getItem(var6);
         if (var7 != null) {
            Performance.record(this.level.tickManager(), "handlePickups", (Runnable)(() -> {
               Iterator var3 = ((GameLinkedList)var1.storage.pickupSlots.get(var6)).iterator();

               while(var3.hasNext()) {
                  SettlementStoragePickupSlot var4 = (SettlementStoragePickupSlot)var3.next();
                  var7.setAmount(var7.getAmount() - var4.item.getAmount());
               }

            }));
            if (var4 != null) {
               Performance.record(this.level.tickManager(), "setRecords", (Runnable)(() -> {
                  int var4x = var7.getAmount();
                  if (var4x > 0) {
                     var4.add(var7, new SettlementStorageRecord(var1.storage, var6, var7, var4x));
                  }

               }));
            }

            if (var7.getAmount() > 0) {
               int var9 = (Integer)Performance.record(this.level.tickManager(), "getRemoveAmount", (Supplier)(() -> {
                  return Math.min(var1.storage.getFilter().getRemoveAmount(this.level, var7, var5), var7.getAmount());
               }));
               if (var9 > 0) {
                  var5.inventory.setAmount(var6, var7.getAmount() - var9);
                  HaulFromLevelJob var10 = (HaulFromLevelJob)var2.stream().filter((var2x) -> {
                     return var2x.item.equals(this.level, var7, true, false, "pickups");
                  }).findFirst().orElse((Object)null);
                  if (var10 != null) {
                     var10.item.setAmount(var10.item.getAmount() + var9);
                  } else {
                     var2.add(new HaulFromLevelJob(var1.storage, var7.copy(var9)));
                  }
               }
            }
         }
      }

      Performance.record(this.level.tickManager(), "findDropOffs", (Runnable)(() -> {
         Performance.record(this.level.tickManager(), "storage", (Runnable)(() -> {
            Iterator var3x = var3.iterator();

            while(true) {
               TempStorage var4;
               do {
                  if (!var3x.hasNext()) {
                     return;
                  }

                  var4 = (TempStorage)var3x.next();
               } while(var1 == var4);

               Iterator var5 = var2.iterator();

               while(var5.hasNext()) {
                  HaulFromLevelJob var6 = (HaulFromLevelJob)var5.next();
                  int var7 = var4.storage.canAddFutureDropOff(var6.item);
                  if (var7 > 0) {
                     var6.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(var4.storage, var4.priority, var7));
                  }
               }
            }
         }));
      }));
   }

   private void addDropOffPositions(TempStorage var1, TempStorage var2, LinkedList<HaulFromLevelJob> var3) {
      LinkedList var4 = new LinkedList();

      InventoryItem var6;
      for(int var5 = var1.range.endSlot; var5 >= var1.range.startSlot; --var5) {
         var6 = var1.range.inventory.getItem(var5);
         if (var6 != null) {
            InventoryItem var7 = var6.copy();
            Iterator var8 = ((GameLinkedList)var1.storage.pickupSlots.get(var5)).iterator();

            while(var8.hasNext()) {
               SettlementStoragePickupSlot var9 = (SettlementStoragePickupSlot)var8.next();
               var7.setAmount(var7.getAmount() - var9.item.getAmount());
            }

            if (var7.getAmount() > 0 && var2.storage.getFilter().matchesItem(var7)) {
               InventoryItem var12 = (InventoryItem)var4.stream().filter((var2x) -> {
                  return var2x.equals(this.level, var7, true, false, "pickups");
               }).findFirst().orElse((Object)null);
               if (var12 != null) {
                  var12.setAmount(var12.getAmount() + var7.getAmount());
               } else {
                  var4.add(var7.copy());
               }
            }
         }
      }

      Iterator var10 = var4.iterator();

      while(var10.hasNext()) {
         var6 = (InventoryItem)var10.next();
         int var11 = var2.storage.canAddFutureDropOff(var6);
         if (var11 > 0) {
            HaulFromLevelJob var13 = (HaulFromLevelJob)var3.stream().filter((var2x) -> {
               return var2x.item.equals(this.level, var6, true, false, "pickups");
            }).findFirst().orElse((Object)null);
            if (var13 != null) {
               if (var13.dropOffPositions.stream().noneMatch((var1x) -> {
                  return var1x.storage == var2.storage;
               })) {
                  var13.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(var2.storage, var2.priority, var11));
               }
            } else {
               HaulFromLevelJob var14 = new HaulFromLevelJob(var1.storage, var6.copy());
               var14.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(var2.storage, var2.priority, var11));
               var3.add(var14);
            }
         }
      }

   }

   private void addInvalidDropOffPositions(TempStorage var1, TempStorage var2, LinkedList<HaulFromLevelJob> var3) {
      LinkedList var4 = new LinkedList();

      InventoryItem var6;
      for(int var5 = var1.range.endSlot; var5 >= var1.range.startSlot; --var5) {
         var6 = var1.range.inventory.getItem(var5);
         if (var6 != null && !var1.range.inventory.isItemValid(var5, var6)) {
            InventoryItem var7 = var6.copy();
            Iterator var8 = ((GameLinkedList)var1.storage.pickupSlots.get(var5)).iterator();

            while(var8.hasNext()) {
               SettlementStoragePickupSlot var9 = (SettlementStoragePickupSlot)var8.next();
               var7.setAmount(var7.getAmount() - var9.item.getAmount());
            }

            if (var7.getAmount() > 0 && var2.storage.getFilter().matchesItem(var7)) {
               InventoryItem var12 = (InventoryItem)var4.stream().filter((var2x) -> {
                  return var2x.equals(this.level, var7, true, false, "pickups");
               }).findFirst().orElse((Object)null);
               if (var12 != null) {
                  var12.setAmount(var12.getAmount() + var7.getAmount());
               } else {
                  var4.add(var7.copy());
               }
            }
         }
      }

      Iterator var10 = var4.iterator();

      while(var10.hasNext()) {
         var6 = (InventoryItem)var10.next();
         int var11 = var2.storage.canAddFutureDropOff(var6);
         if (var11 > 0) {
            HaulFromLevelJob var13 = (HaulFromLevelJob)var3.stream().filter((var2x) -> {
               return var2x.item.equals(this.level, var6, true, false, "pickups");
            }).findFirst().orElse((Object)null);
            if (var13 != null) {
               if (var13.dropOffPositions.stream().noneMatch((var1x) -> {
                  return var1x.storage == var2.storage;
               })) {
                  var13.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(var2.storage, var2.priority, var11));
               }
            } else {
               HaulFromLevelJob var14 = new HaulFromLevelJob(var1.storage, var6.copy());
               var14.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(var2.storage, var2.priority, var11));
               var3.add(var14);
            }
         }
      }

   }

   protected void tickBedsValid() {
      HashSet var1 = new HashSet();
      HashSet var2 = new HashSet();
      Iterator var3 = this.beds.values().iterator();

      while(true) {
         while(var3.hasNext()) {
            SettlementBed var4 = (SettlementBed)var3.next();
            if (!var4.isValidBed()) {
               var4.clearSettler();
               var1.add(new Point(var4.tileX, var4.tileY));
            } else if (!var2.contains(new Point(var4.tileX, var4.tileY))) {
               if (var4.getSettler() != null) {
                  if (!var4.getSettler().settler.isValidBed(var4)) {
                     var4.clearSettler();
                  } else {
                     var2.add(new Point(var4.tileX, var4.tileY));
                     SettlementRoom var5 = this.rooms.getRoom(var4.tileX, var4.tileY);
                     if (var5 != null && (this.firstExpandDefendZone || this.autoExpandDefendZone)) {
                        var5.expandDefendZone();
                     }
                  }
               }
            } else if (var4.getSettler() != null) {
               var4.clearSettler();
            }
         }

         if (!var1.isEmpty()) {
            var3 = var1.iterator();

            while(var3.hasNext()) {
               Point var6 = (Point)var3.next();
               this.beds.remove(var6);
            }

            this.sendEvent(SettlementSettlersChangedEvent.class);
         }

         this.firstExpandDefendZone = false;
         return;
      }
   }

   protected void tickFindSettlerBeds() {
      Iterator var1 = this.settlers.iterator();

      while(var1.hasNext()) {
         LevelSettler var2 = (LevelSettler)var1.next();
         if (!var2.isOutsideLevel && var2.getBed() == null) {
            SettlementBed var3 = this.findBedForSettler(var2);
            SettlerMob var4 = var2.getMob();
            if (var4 != null) {
               if (var3 != null) {
                  this.moveSettler(var2, var3, (ServerClient)null);
               } else {
                  var4.makeSettler(this, var2);
               }
            }
         }
      }

   }

   protected void tickSpawnInSettlers() {
      Iterator var1 = SettlerRegistry.getSettlers().iterator();

      while(var1.hasNext()) {
         Settler var2 = (Settler)var1.next();
         if (var2.canSpawnInSettlement(this, this.level.settlementLayer.getStats())) {
            SettlerMob var3 = var2.getNewSettlerMob(this.level);
            var3.setSettlerSeed(GameRandom.globalRandom.nextInt());
            this.level.entityManager.mobs.add(var3.getMob());
            this.moveIn(new LevelSettler(this, var3));
            System.out.println("Settler " + var2.getStringID() + " moved automatically in at " + this.level.getIdentifier());
            break;
         }
      }

   }

   private void tickHostAchievements() {
      HashSet var1 = new HashSet(SettlerRegistry.getSettlerIDs());
      Iterator var2 = this.settlers.iterator();

      while(var2.hasNext()) {
         LevelSettler var3 = (LevelSettler)var2.next();
         var1.remove(var3.settler.getID());
      }

      if (var1.isEmpty()) {
         GameUtils.streamServerClients(this.getLevel()).filter(ServerClient::achievementsLoaded).forEach((var0) -> {
            var0.achievements().COMPLETE_HOST.markCompleted(var0);
         });
      }

   }

   public void onReturned(int var1) {
      LevelSettler var2 = this.getSettler(var1);
      if (var2 != null) {
         var2.isOutsideLevel = false;
         this.walkIn(var2);
         this.sendEvent(SettlementSettlersChangedEvent.class);
      }

   }

   public void walkIn(LevelSettler var1) {
      SettlerMob var2 = var1.getMob();
      var1.updateHome();
      if (var2 instanceof HumanMob) {
         HumanMob var3 = (HumanMob)var2;
         if (var3.home != null) {
            var3.moveIn(var3.home.x, var3.home.y, true);
         }
      }

   }

   public void moveIn(LevelSettler var1) {
      SettlementBed var2 = this.findBedForSettler(var1);
      if (var2 == null || !this.moveSettler(var1, var2, (ServerClient)null)) {
         this.moveSettler(var1, (SettlementBed)null, (ServerClient)null);
      }

   }

   public boolean canMoveIn(LevelSettler var1, int var2) {
      SettlementBed var3 = this.findBedForSettler(var1);
      return var3 != null || var2 < 0 || this.settlers.stream().filter((var0) -> {
         return var0.getBed() == null;
      }).count() < (long)var2;
   }

   public SettlementBed findBedForSettler(LevelSettler var1) {
      Iterator var2 = this.beds.values().iterator();

      SettlementBed var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (SettlementBed)var2.next();
      } while(var3.getSettler() != null || var3.isLocked || !var1.settler.isValidBed(var3));

      return var3;
   }

   public boolean moveSettler(LevelSettler var1, SettlementBed var2, ServerClient var3) {
      if (var1.data != this) {
         throw new IllegalArgumentException("Settler settlement did not match this");
      } else {
         boolean var4 = false;
         if (this.settlers.stream().noneMatch((var1x) -> {
            return var1x.mobUniqueID == var1.mobUniqueID;
         })) {
            this.settlers.add(var1);
            var4 = true;
         }

         if (var2 == null) {
            if (var1.getBed() == null && !var4) {
               return false;
            } else {
               var1.assignBed((SettlementBed)null);
               this.sendEvent(SettlementSettlersChangedEvent.class);
               return true;
            }
         } else if (this.beds.get(new Point(var2.tileX, var2.tileY)) != var2) {
            throw new IllegalArgumentException("Bed was not in this settlement. Use SettlementLevelData.addOrValidateBed to get a bed");
         } else if (var2.getSettler() == var1) {
            return false;
         } else {
            GameMessage var5 = var1.settler.canUseBed(var2);
            if (var5 != null) {
               if (var3 != null) {
                  var3.sendChatMessage(var5);
               }

               return false;
            } else {
               LevelSettler var6 = var2.getSettler();
               if (var6 != null) {
                  SettlementBed var7 = var1.getBed();
                  if (var7 != null && var7.isValidBed()) {
                     GameMessage var8 = var6.settler.canUseBed(var7);
                     if (var8 == null) {
                        var6.assignBed(var7);
                        if (this.autoExpandDefendZone) {
                           SettlementRoom var9 = this.rooms.getRoom(var7.tileX, var7.tileY);
                           if (var9 != null) {
                              var9.expandDefendZone();
                           }
                        }
                     } else {
                        var6.assignBed((SettlementBed)null);
                     }
                  } else {
                     var6.assignBed((SettlementBed)null);
                  }
               }

               var1.assignBed(var2);
               if (this.autoExpandDefendZone) {
                  SettlementRoom var10 = this.rooms.getRoom(var2.tileX, var2.tileY);
                  if (var10 != null) {
                     var10.expandDefendZone();
                  }
               }

               this.sendEvent(SettlementSettlersChangedEvent.class);
               return true;
            }
         }
      }
   }

   public boolean moveSettler(int var1, SettlementBed var2, ServerClient var3) {
      LevelSettler var4 = this.getSettler(var1);
      if (var4 != null) {
         return this.moveSettler(var4, var2, var3);
      } else {
         if (var3 != null) {
            var3.sendChatMessage((GameMessage)(new LocalMessage("settlement", "notsettler")));
         }

         return false;
      }
   }

   public boolean moveSettler(int var1, int var2, int var3, ServerClient var4) {
      SettlementBed var5 = this.addOrValidateBed(var2, var3);
      return var5 != null ? this.moveSettler(var1, var5, var4) : false;
   }

   public boolean lockNoSettler(int var1, int var2, ServerClient var3) {
      SettlementBed var4 = this.addOrValidateBed(var1, var2);
      if (var4 != null) {
         if (var4.getSettler() != null) {
            var4.clearSettler();
         }

         if (!var4.isLocked) {
            var4.isLocked = true;
            this.sendEvent(SettlementSettlersChangedEvent.class);
         }

         return true;
      } else {
         return false;
      }
   }

   public LevelSettler getSettler(int var1) {
      return (LevelSettler)this.settlers.stream().filter((var1x) -> {
         return var1x.mobUniqueID == var1;
      }).findFirst().orElse((Object)null);
   }

   public boolean removeSettler(int var1, ServerClient var2) {
      for(int var3 = 0; var3 < this.settlers.size(); ++var3) {
         LevelSettler var4 = (LevelSettler)this.settlers.get(var3);
         if (var4.mobUniqueID == var1) {
            SettlerMob var5 = var4.getMob();
            if (var5 != null) {
               var5.moveOut();
            }

            if (var4.getBed() != null) {
               var4.getBed().clearSettler();
            }

            this.settlers.remove(var3);
            this.sendEvent(SettlementSettlersChangedEvent.class);
            return true;
         }
      }

      if (var2 != null) {
         var2.sendChatMessage((GameMessage)(new LocalMessage("settlement", "notsettler")));
      }

      return false;
   }

   public void renameSettler(int var1, String var2) {
      Iterator var3 = this.settlers.iterator();

      while(var3.hasNext()) {
         LevelSettler var4 = (LevelSettler)var3.next();
         if (var4.mobUniqueID == var1) {
            SettlerMob var5 = var4.getMob();
            if (var5 != null) {
               if (!var5.getSettlerName().equals(var2)) {
                  var5.setSettlerName(var2);
                  this.level.getServer().network.sendToClientsWithEntity(new PacketSpawnMob(var5.getMob()), var5.getMob());
               }

               return;
            }
         }
      }

   }

   public boolean hasSettler(Settler var1) {
      return this.settlers.stream().anyMatch((var1x) -> {
         return var1x.settler == var1;
      });
   }

   public int getSettlerCount(Settler var1) {
      return (int)this.settlers.stream().filter((var1x) -> {
         return var1x.settler == var1;
      }).count();
   }

   public int countSettlersWithBed() {
      return (int)this.settlers.stream().filter((var0) -> {
         return var0.getBed() != null;
      }).count();
   }

   public int countSettlersWithoutBed() {
      return (int)this.settlers.stream().filter((var0) -> {
         return var0.getBed() == null;
      }).count();
   }

   public int countTotalSettlers() {
      return this.settlers.size();
   }

   public Iterable<SettlementBed> getBeds() {
      return this.beds.values();
   }

   public SettlementFlagObjectEntity getObjectEntity() {
      if (this.objectEntityPos != null) {
         ObjectEntity var1 = this.level.entityManager.getObjectEntity(this.objectEntityPos.x, this.objectEntityPos.y);
         if (var1 instanceof SettlementFlagObjectEntity) {
            return (SettlementFlagObjectEntity)var1;
         }
      }

      return null;
   }

   public Point getHomestonePos() {
      if (this.homestonePos != null && !this.getLevel().getObject(this.homestonePos.x, this.homestonePos.y).getStringID().equals("homestone")) {
         this.homestonePos = null;
      }

      return this.homestonePos;
   }

   public void setHomestonePos(Point var1) {
      this.homestonePos = var1;
   }

   public Point getObjectEntityPos() {
      return this.objectEntityPos;
   }

   public void setObjectEntityPos(Point var1) {
      if (!Objects.equals(this.objectEntityPos, var1)) {
         this.objectEntityPos = var1;
         this.getLevel().settlementLayer.setActive(this.objectEntityPos != null);
         if (var1 != null && this.autoExpandDefendZone) {
            this.expandDefendZone(new Rectangle(var1.x - 8, var1.y - 8, 17, 17));
         }
      }

   }

   public int getMaxWaystones() {
      return this.getQuestTiersCompleted();
   }

   public ArrayList<Waystone> getWaystones() {
      return this.waystones;
   }

   public int getQuestTiersCompleted() {
      return SettlementQuestTier.getTierIndex(this.questTier) + (this.hasCompletedQuestTier ? 1 : 0);
   }

   public boolean hasCompletedQuestTier(String var1) {
      return this.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex(var1);
   }

   public int getTotalCompletedQuests() {
      return this.totalCompletedQuests;
   }

   public SettlementQuestTier getCurrentQuestTier() {
      SettlementQuestTier var1 = SettlementQuestTier.getTier(this.getQuestTiersCompleted());
      if (var1 != null) {
         this.hasCompletedQuestTier = false;
      }

      return var1;
   }

   public void setCurrentQuestTierDebug(ServerClient var1, SettlementQuestTier var2) {
      if (var1 != null) {
         SettlementClientQuests var3 = (SettlementClientQuests)this.clientQuestsMap.get(var1.authentication);
         if (var3 != null) {
            if (var2 == null) {
               var3.setTier((SettlementQuestTier)SettlementQuestTier.questTiers.get(SettlementQuestTier.questTiers.size() - 1), true);
            } else {
               var3.setTier(var2, false);
            }

            var3.removeCurrentQuest();
            var3.removeCurrentTierQuest();
         }
      }

      if (var2 == null) {
         var2 = (SettlementQuestTier)SettlementQuestTier.questTiers.get(SettlementQuestTier.questTiers.size() - 1);
         this.hasCompletedQuestTier = true;
      } else {
         this.hasCompletedQuestTier = false;
      }

      this.questTier = var2.stringID;
   }

   public void resetQuestsDebug() {
      Iterator var1 = this.clientQuestsMap.values().iterator();

      while(var1.hasNext()) {
         SettlementClientQuests var2 = (SettlementClientQuests)var1.next();
         var2.removeCurrentQuest();
         var2.removeCurrentTierQuest();
      }

   }

   public void onCompletedQuestTier(SettlementQuestTier var1) {
      int var2 = SettlementQuestTier.getTierIndex(this.questTier);
      int var3 = SettlementQuestTier.getTierIndex(var1.stringID);
      if (var2 <= var3) {
         int var4 = var3 + 1;
         if (var4 >= SettlementQuestTier.questTiers.size()) {
            this.hasCompletedQuestTier = true;
         } else {
            this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var4)).stringID;
         }

         this.raidsCounter = Math.max(0, this.raidsCounter - 1);
         this.resetNextRaidTimer(false, true);
      }

   }

   public SettlementClientQuests getClientsQuests(long var1) {
      return (SettlementClientQuests)this.clientQuestsMap.compute(var1, (var3, var4) -> {
         return var4 == null ? new SettlementClientQuests(this, var1) : var4;
      });
   }

   public SettlementClientQuests getClientsQuests(ServerClient var1) {
      return this.getClientsQuests(var1.authentication);
   }

   public boolean spawnTravelingHuman() {
      TravelingHumanArrive var1 = null;
      if (deadRecruits != null && deadRecruits.canSpawn(this)) {
         var1 = deadRecruits.getNewHuman(this);
         this.nextTravelingHumanRecruit = false;
      }

      if (var1 == null && this.nextTravelingHumanRecruit) {
         if (travelingRecruits != null && travelingRecruits.canSpawn(this)) {
            var1 = travelingRecruits.getNewHuman(this);
            this.nextTravelingHumanRecruit = false;
         }
      } else {
         this.nextTravelingHumanRecruit = true;
      }

      if (var1 == null) {
         var1 = getNewTravelingHuman(GameRandom.globalRandom, this);
      }

      if (var1 != null && var1.mob != null) {
         return this.spawnTravelingHuman(var1);
      } else {
         System.err.println("Error spawning new traveling human (null)");
         return false;
      }
   }

   public boolean spawnTravelingHuman(TravelingHumanArrive var1) {
      Point var2 = findRandomSpawnLevelPos(this.level, var1.mob, true);
      if (var2 != null) {
         var1.mob.setLevel(this.getLevel());
         var1.mob.startTravelingHuman(this);
         float var10002 = (float)var2.x;
         this.getLevel().entityManager.addMob(var1.mob, var10002, (float)var2.y);
         this.curTravelingHuman = var1.mob.getUniqueID();
         if (var1.odds != null) {
            this.lastTravelingHumanIdentifier = var1.odds.identifier;
         } else {
            this.lastTravelingHumanIdentifier = null;
         }

         this.resetTravelingHumanTime(false, false);
         GameMessage var3 = var1.getArriveMessage(this);
         if (var3 != null) {
            this.level.settlementLayer.streamTeamMembersAndOnIsland().forEach((var1x) -> {
               var1x.sendChatMessage(var3);
            });
         }

         return true;
      } else {
         return false;
      }
   }

   protected void tickTravelingHuman() {
      --this.nextTravelingHuman;
      if (this.nextTravelingHuman <= 0L) {
         if (this.curTravelingHuman != 0) {
            Mob var1 = (Mob)this.getLevel().entityManager.mobs.get(this.curTravelingHuman, false);
            if (var1 instanceof HumanMob && ((HumanMob)var1).isTravelingHuman()) {
               this.resetTravelingHumanTime(false, false);
               return;
            }
         }

         if (this.spawnTravelingHuman()) {
            this.resetTravelingHumanTime(false, false);
         } else {
            this.resetTravelingHumanTime(true, false);
         }
      }

   }

   public void onTravelingHumanLeave(HumanMob var1) {
      this.onTravelingHumanLeave(var1, new LocalMessage("settlement", "travelingleave", new Object[]{"mob", var1.getDisplayName(), "settlement", this.level.settlementLayer.getSettlementName()}));
   }

   public void onTravelingHumanLeave(HumanMob var1, GameMessage var2) {
      if (var2 != null) {
         this.level.settlementLayer.streamTeamMembersAndOnIsland().forEach((var2x) -> {
            var2x.sendChatMessage((GameMessage)(new LocalMessage("settlement", "travelingleave", new Object[]{"mob", var1.getDisplayName(), "settlement", this.level.settlementLayer.getSettlementName()})));
         });
      }

      this.resetTravelingHumanTime(false, false);
   }

   public void resetTravelingHumanTime(boolean var1, boolean var2) {
      long var3;
      if (!var1 && this.diedSettlerRecruitStringIDs.isEmpty()) {
         var3 = (long)GameRandom.globalRandom.getIntBetween(MIN_SECONDS_TRAVELING_TIMER, MAX_SECONDS_TRAVELING_TIMER);
      } else {
         var3 = (long)GameRandom.globalRandom.getIntBetween(300, 600);
      }

      if (var2) {
         this.nextTravelingHuman = Math.min(var3, this.nextTravelingHuman);
      } else {
         this.nextTravelingHuman = var3;
      }

   }

   public void tickNextRaid() {
      if (this.hasNonAFKTeamMembers && this.getLevel().getWorldSettings().raidFrequency != GameRaidFrequency.NEVER) {
         --this.nextRaid;
         boolean var1 = this.level.getWorldEntity().isNight();
         if (this.nextRaid <= 0L && var1 && !this.lastRaidCheckWasNight) {
            boolean var2 = this.level.settlementLayer.streamTeamMembers().anyMatch((var0) -> {
               return var0.isAFK() ? false : var0.getLevel().entityManager.mobs.stream().noneMatch(Mob::isBoss);
            });
            if (var2) {
               this.spawnRaid();
            } else {
               this.resetNextRaidTimer(true, false);
            }
         }

         this.lastRaidCheckWasNight = var1;
      }

   }

   public void resetNextRaidTimer(boolean var1, boolean var2) {
      long var3;
      if (var1) {
         var3 = (long)GameRandom.globalRandom.getIntBetween(300, 1200);
      } else {
         float var5 = 1.0F;
         switch (this.getLevel().getWorldSettings().raidFrequency) {
            case OFTEN:
               var5 = 0.5F;
               break;
            case RARELY:
               var5 = 4.0F;
         }

         var3 = (long)GameRandom.globalRandom.getIntBetween((int)((float)MIN_SECONDS_RAID_TIMER * var5), Math.min((int)((float)(MAX_SECONDS_RAID_TIMER + INCREASES_RAID_TIMER_SECONDS_PER_RAID * this.raidsCounter) * var5), UPPER_LIMIT_RAID_TIMER_SECONDS));
      }

      if (var2) {
         this.nextRaid = Math.min(var3, this.nextRaid);
      } else {
         this.nextRaid = var3;
      }

   }

   public boolean spawnRaid() {
      return this.spawnRaid((SettlementRaidLevelEvent.RaidDir)GameRandom.globalRandom.getOneOf((Object[])SettlementRaidLevelEvent.RaidDir.values()));
   }

   public boolean spawnRaid(SettlementRaidLevelEvent.RaidDir var1) {
      return this.spawnRaid(this.getNextRaid(), var1);
   }

   public boolean spawnRaid(SettlementRaidLevelEvent var1, SettlementRaidLevelEvent.RaidDir var2) {
      if (var1 != null) {
         if (this.currentRaid != 0) {
            LevelEvent var3 = this.level.entityManager.getLevelEvent(this.currentRaid, false);
            if (var3 != null) {
               var3.over();
            }
         }

         var1.setDifficultyModifier(this.nextRaidDifficultyMod);
         var1.setDirection(var2);
         this.level.entityManager.addLevelEvent(var1);
         this.currentRaid = var1.getUniqueID();
         this.resetNextRaidTimer(false, false);
         return true;
      } else {
         this.resetNextRaidTimer(true, false);
         return false;
      }
   }

   public SettlementRaidLevelEvent getNextRaid() {
      return this.countTotalSettlers() >= 3 ? (SettlementRaidLevelEvent)LevelEventRegistry.getEvent("humanraid") : null;
   }

   public float getNextRaidDifficultyMod() {
      return this.nextRaidDifficultyMod;
   }

   public void setRaidDifficultyMod(float var1) {
      this.nextRaidDifficultyMod = var1;
   }

   public void onRaidOver(SettlementRaidLevelEvent var1, float var2) {
      if (var1.getUniqueID() == this.currentRaid) {
         this.nextRaidDifficultyMod = var2;
         ++this.raidsCounter;
         this.currentRaid = 0;
         this.resetNextRaidTimer(false, false);
         if (!this.diedSettlerRecruitStringIDs.isEmpty()) {
            this.resetTravelingHumanTime(true, true);
         }
      }

   }

   public int getRaidsCounter() {
      return this.raidsCounter;
   }

   public void onSettlerDeath(LevelSettler var1) {
      if (var1 != null && var1.settler != null) {
         float var2 = var1.settler.getArriveAsRecruitAfterDeathChance(this);
         if (var2 > 0.0F && GameRandom.globalRandom.getChance(var2)) {
            this.diedSettlerRecruitStringIDs.add(var1.settler.getStringID());
            if (this.diedSettlerRecruitStringIDs.size() >= 5) {
               this.diedSettlerRecruitStringIDs.remove(GameRandom.globalRandom.nextInt(this.diedSettlerRecruitStringIDs.size()));
            }
         }

         for(int var3 = 0; var3 < this.settlers.size(); ++var3) {
            LevelSettler var4 = (LevelSettler)this.settlers.get(var3);
            if (var1 == var4 || var1.mobUniqueID == var4.mobUniqueID) {
               SettlementBed var5 = var4.getBed();
               if (var5 != null) {
                  var5.clearSettler();
               }

               this.settlers.remove(var3);
               this.sendEvent(SettlementSettlersChangedEvent.class);
               break;
            }
         }

      }
   }

   public SettlementInventory assignStorage(int var1, int var2) {
      SettlementInventory var3 = (SettlementInventory)this.storage.stream().filter((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      }).findFirst().orElseGet(() -> {
         SettlementInventory var3 = new SettlementInventory(this.getLevel(), var1, var2);
         if (var3.isValid()) {
            this.storage.add(var3);
            return var3;
         } else {
            return null;
         }
      });
      (new SettlementSingleStorageEvent(this, var1, var2)).applyAndSendToClientsAt(this.getLevel());
      return var3;
   }

   public void removeStorage(int var1, int var2) {
      this.storage.removeIf((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      });
      (new SettlementSingleStorageEvent(this, var1, var2)).applyAndSendToClientsAt(this.getLevel());
   }

   public SettlementInventory getStorage(int var1, int var2) {
      return (SettlementInventory)this.storage.stream().filter((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      }).findFirst().orElse((Object)null);
   }

   public ArrayList<SettlementInventory> getStorage() {
      return this.storage;
   }

   public SettlementWorkstation assignWorkstation(int var1, int var2) {
      SettlementWorkstation var3 = (SettlementWorkstation)this.workstations.stream().filter((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      }).findFirst().orElseGet(() -> {
         SettlementWorkstation var3 = new SettlementWorkstation(this, var1, var2);
         if (var3.isValid()) {
            this.workstations.add(var3);
            return var3;
         } else {
            return null;
         }
      });
      (new SettlementSingleWorkstationsEvent(this, var1, var2)).applyAndSendToClientsAt(this.getLevel());
      return var3;
   }

   public void removeWorkstation(int var1, int var2) {
      this.workstations.removeIf((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      });
      (new SettlementSingleWorkstationsEvent(this, var1, var2)).applyAndSendToClientsAt(this.getLevel());
   }

   public SettlementWorkstation getWorkstation(int var1, int var2) {
      return (SettlementWorkstation)this.workstations.stream().filter((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      }).findFirst().orElse((Object)null);
   }

   public ArrayList<SettlementWorkstation> getWorkstations() {
      return this.workstations;
   }

   public SettlementWorkZoneManager getWorkZones() {
      return this.workZones;
   }

   public Zoning getDefendZone() {
      return this.defendZone;
   }

   public boolean expandDefendZone(Collection<SemiRegion> var1, int var2) {
      if (var1 == null) {
         return false;
      } else {
         boolean var3 = false;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            SemiRegion var5 = (SemiRegion)var4.next();
            Iterator var6 = var5.getLevelTiles().iterator();

            while(var6.hasNext()) {
               Point var7 = (Point)var6.next();

               for(int var8 = var7.x - var2; var8 <= var7.x + var2; ++var8) {
                  if (var8 >= 0 && var8 < this.level.width) {
                     for(int var9 = var7.y - var2; var9 <= var7.y + var2; ++var9) {
                        if (var9 >= 0 && var9 < this.level.height) {
                           synchronized(this.defendZone) {
                              var3 = this.defendZone.addTile(var8, var9) || var3;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (var3) {
            (new SettlementDefendZoneChangedEvent(ZoningChange.full(this.defendZone))).applyAndSendToClientsAt(this.level);
         }

         return var3;
      }
   }

   public boolean applyDefendZoneChange(ZoningChange var1) {
      if (var1.applyTo(this.defendZone)) {
         (new SettlementDefendZoneChangedEvent(var1)).applyAndSendToClientsAt(this.level);
         return true;
      } else {
         return false;
      }
   }

   public boolean expandDefendZone(Rectangle var1) {
      if (this.defendZone.addRectangle(var1)) {
         (new SettlementDefendZoneChangedEvent(ZoningChange.expand(var1))).applyAndSendToClientsAt(this.level);
         return true;
      } else {
         return false;
      }
   }

   public boolean shrinkDefendZone(Rectangle var1) {
      if (this.defendZone.removeRectangle(var1)) {
         (new SettlementDefendZoneChangedEvent(ZoningChange.shrink(var1))).applyAndSendToClientsAt(this.level);
         return true;
      } else {
         return false;
      }
   }

   public int getNewSettlerRestrictZoneUniqueID() {
      return this.newSettlerRestrictZoneUniqueID;
   }

   public RestrictZone getNewSettlerRestrictZone() {
      return (RestrictZone)this.restrictZones.getOrDefault(this.newSettlerRestrictZoneUniqueID, (Object)null);
   }

   public void setNewSettlerRestrictZone(int var1) {
      if (this.newSettlerRestrictZoneUniqueID != var1) {
         this.newSettlerRestrictZoneUniqueID = var1;
      }

   }

   public Collection<RestrictZone> getRestrictZones() {
      return this.restrictZones.values();
   }

   public RestrictZone getRestrictZone(int var1) {
      return (RestrictZone)this.restrictZones.getOrDefault(var1, (Object)null);
   }

   public RestrictZone addNewRestrictZone() {
      int var1 = 1;

      for(int var2 = 0; var2 < 1000; ++var2) {
         var1 = GameRandom.globalRandom.nextInt();
         if (var1 != 0 && var1 != 1 && !this.restrictZones.containsKey(var1)) {
            break;
         }
      }

      AtomicInteger var5 = new AtomicInteger(this.restrictZones.size() + 1);
      Function var3 = (var0) -> {
         return new LocalMessage("ui", "settlementareadefname", new Object[]{"number", var0});
      };

      while(this.restrictZones.values().stream().anyMatch((var2x) -> {
         return var2x.name.translate().equals(((GameMessage)var3.apply(var5.get())).translate());
      })) {
         var5.addAndGet(1);
      }

      RestrictZone var4 = new RestrictZone(this, var1, this.restrictZoneIndexCounter++, (GameMessage)var3.apply(var5.get()));
      var4.colorHue = this.restrictZoneIndexCounter * 3 * 36 % 360;
      this.restrictZones.put(var1, var4);
      return var4;
   }

   public boolean deleteRestrictZone(int var1) {
      return this.restrictZones.remove(var1) != null;
   }

   public ItemCategoriesFilter getNewSettlerDiet() {
      return this.newSettlerDiet;
   }

   public ItemCategoriesFilter getNewSettlerEquipmentFilter() {
      return this.newSettlerEquipmentFilter;
   }

   public void sendEvent(Class<? extends ContainerEvent> var1) {
      this.eventsToSend.add(var1);
   }

   public GameTooltips getDebugTooltips() {
      StringTooltips var1 = new StringTooltips();
      var1.add("Next traveling: " + GameUtils.formatSeconds(this.nextTravelingHuman));
      var1.add("Current traveling: " + this.curTravelingHuman);
      var1.add("Next raid: " + GameUtils.formatSeconds(this.nextRaid));
      var1.add("Raid difficulty: " + this.nextRaidDifficultyMod);
      return var1;
   }

   public static Point findRandomSpawnLevelPos(Level var0, Mob var1, int var2, int var3, boolean var4) {
      Point var5 = var1.getPathMoveOffset();
      Point var6 = null;

      for(int var7 = 0; var7 < var2; ++var7) {
         Point var8 = (Point)GameRandom.globalRandom.getOneOf(() -> {
            return new Point(GameRandom.globalRandom.nextInt(var0.width - var3 * 2) + var3, var3);
         }, () -> {
            return new Point(var0.width - 5, GameRandom.globalRandom.nextInt(var0.height - var3 * 2) + var3);
         }, () -> {
            return new Point(GameRandom.globalRandom.nextInt(var0.width - var3 * 2) + var3, var0.height - var3);
         }, () -> {
            return new Point(5, GameRandom.globalRandom.nextInt(var0.height - var3 * 2) + var3);
         });
         var6 = new Point(var8.x * 32 + var5.x, var8.y * 32 + var5.y);
         if (!var1.collidesWith(var0, var6.x, var6.y)) {
            return var6;
         }
      }

      return var4 ? null : var6;
   }

   public static Point findRandomSpawnLevelPos(Level var0, Mob var1, boolean var2) {
      return findRandomSpawnLevelPos(var0, var1, 20, 5, var2);
   }

   public static SettlementLevelData getSettlementDataCreateIfNonExist(Level var0) {
      if (!var0.isServer()) {
         throw new IllegalArgumentException("Level must be server level");
      } else {
         SettlementLevelData var1 = getSettlementData(var0);
         if (var1 == null) {
            var1 = new SettlementLevelData();
            var0.addLevelData("settlement", var1);
         }

         return var1;
      }
   }

   public static SettlementLevelData getSettlementData(Level var0) {
      LevelData var1 = var0.getLevelData("settlement");
      return var1 instanceof SettlementLevelData ? (SettlementLevelData)var1 : null;
   }

   static {
      travelingHumanMobs.add(new TravelingHumanOdds("travelingmerchant") {
         public boolean canSpawn(SettlementLevelData var1) {
            return true;
         }

         public int getTickets(SettlementLevelData var1) {
            return this.identifier.equals(var1.lastTravelingHumanIdentifier) ? 35 : 100;
         }

         public TravelingHumanArrive getNewHuman(SettlementLevelData var1) {
            return new TravelingHumanArrive(this, (HumanMob)MobRegistry.getMob("travelingmerchant", var1.getLevel()));
         }
      });
      travelingHumanMobs.add(new TravelingHumanOdds("pawnbroker") {
         public boolean canSpawn(SettlementLevelData var1) {
            return !var1.hasSettler(SettlerRegistry.getSettler("pawnbroker"));
         }

         public int getTickets(SettlementLevelData var1) {
            return this.identifier.equals(var1.lastTravelingHumanIdentifier) ? 25 : 75;
         }

         public TravelingHumanArrive getNewHuman(SettlementLevelData var1) {
            return new TravelingHumanArrive(this, (HumanMob)MobRegistry.getMob("pawnbrokerhuman", var1.getLevel()));
         }
      });
      travelingHumanMobs.add(new TravelingHumanOdds("recruit") {
         public boolean canSpawn(SettlementLevelData var1) {
            return true;
         }

         public int getTickets(SettlementLevelData var1) {
            return this.identifier.equals(var1.lastTravelingHumanIdentifier) ? 5 : 10;
         }

         public TravelingHumanArrive getNewHuman(SettlementLevelData var1) {
            TicketSystemList var2 = new TicketSystemList();
            Iterator var3 = SettlerRegistry.getSettlers().iterator();

            while(var3.hasNext()) {
               Settler var4 = (Settler)var3.next();
               var4.addNewRecruitSettler(var1, true, var2);
            }

            HumanMob var6;
            do {
               if (var2.isEmpty()) {
                  return null;
               }

               Supplier var5 = (Supplier)var2.getAndRemoveRandomObject(GameRandom.globalRandom);
               var6 = (HumanMob)var5.get();
            } while(var6 == null);

            return new TravelingHumanArrive(this, var6);
         }
      });
   }

   private static class TempStorage {
      public final LevelStorage storage;
      public final InventoryRange range;
      public final int priority;

      public TempStorage(LevelStorage var1, int var2) {
         this.storage = var1;
         this.range = var1.getInventoryRange();
         this.priority = var2;
      }

      public TempStorage(SettlementInventory var1) {
         this(var1, var1.priority);
      }
   }

   private static class TempRequest extends TempStorage {
      public SettlementRequestInventory requestInventory;

      public TempRequest(SettlementRequestInventory var1) {
         super(var1, 0);
         this.requestInventory = var1;
      }
   }
}
