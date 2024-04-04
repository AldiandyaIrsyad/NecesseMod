package necesse.entity.mobs.friendly.human;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketHumanWorkUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldSettings;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.Entity;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.ActivityDescription;
import necesse.entity.mobs.ActivityDescriptionMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobInventory;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CustomMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.EquipmentActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.SettlerMission;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanDietFilterSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanEquipmentFilterSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkPrioritiesSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSettingRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.networkField.BooleanNetworkField;
import necesse.entity.mobs.networkField.IntNetworkField;
import necesse.entity.mobs.networkField.LongNetworkField;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameColor;
import necesse.gfx.GameHair;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.FishingPoleHolding;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootInventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.hudManager.floatText.HungryFloatText;
import necesse.level.maps.levelData.jobs.ConsumeFoodLevelJob;
import necesse.level.maps.levelData.jobs.FertilizeLevelJob;
import necesse.level.maps.levelData.jobs.FillApiaryFrameLevelJob;
import necesse.level.maps.levelData.jobs.FishingPositionLevelJob;
import necesse.level.maps.levelData.jobs.ForestryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestApiaryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.jobs.HuntMobLevelJob;
import necesse.level.maps.levelData.jobs.ManageEquipmentLevelJob;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.PlantCropLevelJob;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.StorePickupItemLevelJob;
import necesse.level.maps.levelData.jobs.UseWorkstationLevelJob;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;
import necesse.level.maps.levelData.settlementData.settler.DietThought;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;
import necesse.level.maps.levelData.settlementData.settler.PopulationThought;
import necesse.level.maps.levelData.settlementData.settler.RoomQuality;
import necesse.level.maps.levelData.settlementData.settler.RoomSize;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public abstract class HumanMob extends FriendlyMob implements SettlerMob, CommandMob, EntityJobWorker, FishingMob, HungerMob, ObjectUserMob, ActivityDescriptionMob, MobInventory {
   public static int travelingStayTimeSeconds = 480;
   public static int timeToTravelOut = 240;
   public static int humanPathIterations = 25000;
   public static int defaultJobPathIterations;
   public static int secondsToPassAtFullHunger;
   public static float adventurePartyHungerUsageMod;
   public Point home;
   public int maxSecondsFullHealthRegen = 300;
   public int settlerSeed;
   protected String settlerName;
   protected Point moveInPoint;
   protected Point moveOutPoint;
   private int settlerCheck;
   protected BooleanNetworkField clientHasCommandOrders = (BooleanNetworkField)this.registerNetworkField(new BooleanNetworkField(false));
   public int resetCommandsBuffer;
   public Mob commandFollowMob;
   public Point commandGuardPoint;
   public boolean commandMoveToGuardPoint;
   public boolean commandMoveToFollowPoint;
   public Mob commandAttackMob;
   public boolean canJoinAdventureParties = true;
   public final AdventurePartyHumanHandler adventureParty = new AdventurePartyHumanHandler(this);
   public final HashSet<Projectile> boomerangs = new HashSet();
   private ArrayList<ServerClient> interactClients;
   public final String settlerStringID;
   protected boolean isSettler;
   protected int settlerIslandX;
   protected int settlerIslandY;
   public LevelSettler levelSettler;
   protected int settlerQuestTier = -1;
   protected int settlerHappiness = 0;
   protected ItemCategoriesFilter loadedDietFilter = null;
   public FoodConsumableItem lastFoodEaten;
   public LinkedList<Integer> recentFoodItemIDsEaten = new LinkedList();
   public float hungerLevel = 0.2F;
   protected int defaultAttackAnimTime;
   protected int defaultAttackCooldown;
   protected InventoryItem customAttackItem;
   protected Runnable customShowAttack;
   private long lastWorkAnimationRequest = -1L;
   public final CustomMobAbility itemAttackAbility;
   public final CustomMobAbility itemSwingSpriteAbility;
   public final CustomMobAbility itemWorkSpriteAbility;
   public final BooleanMobAbility playConsumeSound;
   public final EmptyMobAbility showHungry;
   public final HumanWorkSettingRegistry workSettings;
   public final CustomMobAbility workSettingAction;
   protected long searchEquipmentCooldown;
   public BooleanNetworkField selfManageEquipment = (BooleanNetworkField)this.registerNetworkField(new BooleanNetworkField(true));
   public Inventory equipmentInventory = new Inventory(7) {
      public void updateSlot(int var1) {
         super.updateSlot(var1);
         if (var1 < 3) {
            HumanMob.this.equipmentBuffManager.updateArmorBuffs();
         } else if (var1 < 6) {
            HumanMob.this.equipmentBuffManager.updateCosmeticSetBonus();
         }

      }
   };
   public EquipmentBuffManager equipmentBuffManager = new EquipmentBuffManager(this) {
      public InventoryItem getArmorItem(int var1) {
         return HumanMob.this.equipmentInventory.getItem(var1);
      }

      public InventoryItem getCosmeticItem(int var1) {
         return HumanMob.this.equipmentInventory.getItem(3 + var1);
      }

      public ArrayList<InventoryItem> getTrinketItems() {
         return new ArrayList();
      }
   };
   public ArrayList<InventoryItem> workInventory = new ArrayList();
   public ActivityDescription currentActivity;
   public int hungryStrikeBuffer;
   public float leaveBuffer;
   public float leaveBufferWarnings;
   public BooleanNetworkField isOnStrike = (BooleanNetworkField)this.registerNetworkField(new BooleanNetworkField(false) {
      public void onChanged(Boolean var1) {
         super.onChanged(var1);
         if (!var1) {
            HumanMob.this.endStrikeBuffer = 0;
         }

      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onChanged(Object var1) {
         this.onChanged((Boolean)var1);
      }
   });
   public boolean hungerStrike;
   public int endStrikeBuffer;
   public int workBreakBuffer;
   public boolean onWorkBreak;
   public static int maxWorkBreakBufferAtFullHappiness;
   public static int maxWorkBreakBufferAtNoHappiness;
   public static int resetWorkBreakWhenBufferAt;
   public static float workBreakBufferUsageModAtFullHappiness;
   public static float workBreakBufferUsageModAtNoHappiness;
   public static float workBreakBufferRegenModAtFullHappiness;
   public static float workBreakBufferRegenModAtNoHappiness;
   public JobTypeHandler jobTypeHandler = new JobTypeHandler();
   protected boolean cancelJob;
   private boolean sendWorkUpdatePacket;
   public boolean workDirty;
   protected int nonSettlerHealth;
   protected int settlerHealth;
   protected boolean isTravelingHuman;
   protected long travelOutTime;
   protected SettlerMission currentMission;
   public boolean completedMission;
   public boolean missionFailed;
   public GameMessage missionFailedMessage;
   public ObjectUserActive objectUser;
   public BooleanNetworkField hideOnLowHealth = (BooleanNetworkField)this.registerNetworkField(new BooleanNetworkField(true));
   public IntNetworkField team = (IntNetworkField)this.registerNetworkField(new IntNetworkField(-10) {
      public void onChanged(Integer var1) {
         super.onChanged(var1);
         HumanMob.this.setTeam(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onChanged(Object var1) {
         this.onChanged((Integer)var1);
      }
   });
   public LongNetworkField owner = (LongNetworkField)this.registerNetworkField(new LongNetworkField(-1L) {
      public void onChanged(Long var1) {
         super.onChanged(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onChanged(Object var1) {
         this.onChanged((Long)var1);
      }
   });
   public boolean isHiding;
   public static String[] elderNames;
   public static String[] maleNames;
   public static String[] femaleNames;
   public static String[] neutralNames;

   public HumanMob(int var1, int var2, String var3) {
      super(var1);
      this.nonSettlerHealth = var1;
      this.settlerHealth = var2;
      this.settlerStringID = var3;
      this.attackAnimTime = this.attackCooldown;
      this.setSpeed(25.0F);
      this.setFriction(3.0F);
      this.setArmor(15);
      this.setTeam(-10);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.workSettings = new HumanWorkSettingRegistry();
      this.workSettingAction = (CustomMobAbility)this.registerAbility(new CustomMobAbility() {
         protected void run(Packet var1) {
            PacketReader var2 = new PacketReader(var1);
            int var3 = var2.getNextShortUnsigned();
            HumanWorkSetting var4 = HumanMob.this.workSettings.getSetting(var3);
            if (var4 != null) {
               var4.runAction(var2.getNextContentPacket());
            }

         }
      });
      this.itemAttackAbility = (CustomMobAbility)this.registerAbility(new CustomMobAbility() {
         protected void run(Packet var1) {
            PacketReader var2 = new PacketReader(var1);
            int var3 = var2.getNextInt();
            int var4 = var2.getNextInt();
            Item var5 = ItemRegistry.getItem(var2.getNextShortUnsigned());
            if (var5 != null) {
               HumanMob.this.customAttackItem = new InventoryItem(var5);
               HumanMob.this.attackAnimTime = var2.getNextInt();
               HumanMob.this.attackCooldown = Math.max(HumanMob.this.attackAnimTime, var2.getNextInt());
               HumanMob.this.customShowAttack = () -> {
                  var5.showAttack(HumanMob.this.getLevel(), var3, var4, HumanMob.this, HumanMob.this.getCurrentAttackHeight(), HumanMob.this.customAttackItem, HumanMob.this.attackSeed, new PacketReader(new Packet()));
               };
               HumanMob.this.showAttack(var3, var4, GameRandom.globalRandom.nextInt(32767), var5.showAttackAllDirections(HumanMob.this, HumanMob.this.customAttackItem));
               HumanMob.this.lastWorkAnimationRequest = -1L;
            }
         }
      });
      this.itemSwingSpriteAbility = (CustomMobAbility)this.registerAbility(new CustomMobAbility() {
         protected void run(Packet var1) {
            PacketReader var2 = new PacketReader(var1);
            int var3 = var2.getNextInt();
            int var4 = var2.getNextInt();
            InventoryItem var5 = new InventoryItem("swingspriteattack");
            if (var2.getNextBoolean()) {
               int var6 = var2.getNextShortUnsigned();
               var5.getGndData().setInt("itemID", var6);
            } else {
               var5.getGndData().setInt("itemID", -1);
            }

            HumanMob.this.attackAnimTime = var2.getNextInt();
            var5.getGndData().setBoolean("inverted", var2.getNextBoolean());
            HumanMob.this.customAttackItem = var5;
            HumanMob.this.customShowAttack = () -> {
            };
            HumanMob.this.showAttack(var3, var4, false);
            HumanMob.this.lastWorkAnimationRequest = -1L;
         }
      });
      this.itemWorkSpriteAbility = (CustomMobAbility)this.registerAbility(new CustomMobAbility() {
         protected void run(Packet var1) {
            PacketReader var2 = new PacketReader(var1);
            int var3 = var2.getNextInt();
            int var4 = var2.getNextInt();
            InventoryItem var5 = new InventoryItem("workspriteattack");
            if (var2.getNextBoolean()) {
               int var6 = var2.getNextShortUnsigned();
               var5.getGndData().setInt("itemID", var6);
            } else {
               var5.getGndData().setInt("itemID", -1);
            }

            HumanMob.this.attackAnimTime = var2.getNextInt();
            HumanMob.this.customAttackItem = var5;
            HumanMob.this.customShowAttack = () -> {
            };
            long var8 = HumanMob.this.attackTime;
            HumanMob.this.showAttack(var3, var4, false);
            if (!HumanMob.this.isAttacking) {
               HumanMob.this.attackTime = var8;
            }

            HumanMob.this.lastWorkAnimationRequest = HumanMob.this.getWorldEntity().getLocalTime();
         }
      });
      this.playConsumeSound = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            if (HumanMob.this.isClient()) {
               Screen.playSound(var1 ? GameResources.drink : GameResources.eat, SoundEffect.effect(HumanMob.this));
            }

         }
      });
      this.showHungry = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (!HumanMob.this.isServer()) {
               HumanMob.this.getLevel().hudManager.addElement(new HungryFloatText(HumanMob.this));
            }
         }
      });
      this.currentActivity = new ActivityDescription(this);
      this.jobTypeHandler.setJobHandler(ConsumeFoodLevelJob.class, 0, 0, 0, 0, (var1x, var2x) -> {
         return this.hungerLevel <= 0.25F;
      }, (var1x) -> {
         return ConsumeFoodLevelJob.getJobSequence(this, var1x, this, new HashSet(this.recentFoodItemIDsEaten));
      });
      this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).extraJobStreamer = ConsumeFoodLevelJob.getJobStreamer(() -> {
         return this.levelSettler == null ? null : this.levelSettler.dietFilter;
      });
      this.jobTypeHandler.setJobHandler(ManageEquipmentLevelJob.class, 0, 0, 0, 0, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (Boolean)this.selfManageEquipment.get() && this.searchEquipmentCooldown <= this.getWorldEntity().getTime();
      }, (var1x) -> {
         this.searchEquipmentCooldown = this.getWorldEntity().getTime() + (long)(GameRandom.globalRandom.getIntBetween(60, 300) * 1000);
         return ManageEquipmentLevelJob.getJobSequence(this, var1x, this);
      });
      this.jobTypeHandler.getJobHandler(ManageEquipmentLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(ManageEquipmentLevelJob.class).extraJobStreamer = ManageEquipmentLevelJob.getJobStreamer(() -> {
         return this.levelSettler == null ? null : this.levelSettler.equipmentFilter;
      }, () -> {
         return this.levelSettler == null || this.levelSettler.preferArmorSets;
      });
      this.jobTypeHandler.setJobHandler(HaulFromLevelJob.class, 0, 0, 0, 5000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerOnCurrentLevel() && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return HaulFromLevelJob.getJobSequence(this, (FoundJob)var1x);
      });
      this.jobTypeHandler.setJobHandler(UseWorkstationLevelJob.class, 0, 0, 0, 10000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerOnCurrentLevel() && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return UseWorkstationLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.setJobHandler(HasStorageLevelJob.class, 0, 0, 0, 0, (var1x, var2x) -> {
         return !this.hasCompletedMission() && this.isSettlerOnCurrentLevel();
      }, (var1x) -> {
         return HasStorageLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.setJobHandler(StorePickupItemLevelJob.class, 0, 0, 0, 5000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerOnCurrentLevel() && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return StorePickupItemLevelJob.getJobSequence(this, (FoundJob)var1x);
      });
      this.jobTypeHandler.getJobHandler(StorePickupItemLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(StorePickupItemLevelJob.class).extraJobStreamer = StorePickupItemLevelJob.getJobStreamer();
      this.jobTypeHandler.setJobHandler(ForestryLevelJob.class, 0, 0, 0, 4000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return ForestryLevelJob.getJobSequence(this, this.isSettler(), var1x);
      });
      this.jobTypeHandler.setJobHandler(PlantSaplingLevelJob.class, 0, 0, 0, 1000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel());
      }, (var1x) -> {
         return PlantSaplingLevelJob.getJobSequence(this, this.isSettler(), var1x);
      });
      this.jobTypeHandler.setJobHandler(HarvestFruitLevelJob.class, 0, 0, 0, 4000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return HarvestFruitLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.setJobHandler(HarvestCropLevelJob.class, 0, 0, 0, 4000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return HarvestCropLevelJob.getJobSequence(this, this.isSettler(), var1x);
      });
      this.jobTypeHandler.setJobHandler(PlantCropLevelJob.class, 0, 0, 0, 1000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel());
      }, (var1x) -> {
         return PlantCropLevelJob.getJobSequence(this, this.isSettler(), var1x);
      });
      this.jobTypeHandler.setJobHandler(FertilizeLevelJob.class, 0, 0, 0, 1000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return FertilizeLevelJob.getJobSequence(this, this.isSettler(), var1x);
      }, FertilizeLevelJob::getPreSequenceCompute);
      this.jobTypeHandler.setJobHandler(FishingPositionLevelJob.class, 0, 0, 10000, 50000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return FishingPositionLevelJob.getJobSequence(this, this, var1x);
      });
      this.jobTypeHandler.setJobHandler(HuntMobLevelJob.class, 0, 0, 0, 20000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return HuntMobLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.getJobHandler(HuntMobLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(HuntMobLevelJob.class).extraJobStreamer = HuntMobLevelJob.getJobStreamer();
      this.jobTypeHandler.setJobHandler(SlaughterHusbandryMobLevelJob.class, 0, 0, 0, 16000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return SlaughterHusbandryMobLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.getJobHandler(SlaughterHusbandryMobLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(SlaughterHusbandryMobLevelJob.class).extraJobStreamer = SlaughterHusbandryMobLevelJob.getJobStreamer();
      this.jobTypeHandler.setJobHandler(MilkHusbandryMobLevelJob.class, 0, 0, 0, 4000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return MilkHusbandryMobLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.getJobHandler(MilkHusbandryMobLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(MilkHusbandryMobLevelJob.class).extraJobStreamer = MilkHusbandryMobLevelJob.getJobStreamer();
      this.jobTypeHandler.setJobHandler(ShearHusbandryMobLevelJob.class, 0, 0, 0, 4000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return ShearHusbandryMobLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.getJobHandler(ShearHusbandryMobLevelJob.class).searchInLevelJobData = false;
      this.jobTypeHandler.getJobHandler(ShearHusbandryMobLevelJob.class).extraJobStreamer = ShearHusbandryMobLevelJob.getJobStreamer();
      this.jobTypeHandler.setJobHandler(HarvestApiaryLevelJob.class, 0, 0, 0, 8000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerOnCurrentLevel()) && !this.getWorkInventory().isFull();
      }, (var1x) -> {
         return HarvestApiaryLevelJob.getJobSequence(this, var1x);
      });
      this.jobTypeHandler.setJobHandler(FillApiaryFrameLevelJob.class, 0, 0, 0, 2000, (var1x, var2x) -> {
         return !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && !this.isSettler() || this.isSettlerOnCurrentLevel();
      }, (var1x) -> {
         return FillApiaryFrameLevelJob.getJobSequence(this, this.isSettler(), var1x);
      }, FillApiaryFrameLevelJob::getPreSequenceCompute);
      this.workSettings.registerSetting(new HumanWorkPrioritiesSetting(this));
      this.workSettings.registerSetting(new HumanDietFilterSetting(this));
      this.workSettings.registerSetting(new HumanEquipmentFilterSetting(this));
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.home != null) {
         var1.addPoint("home", this.home);
      }

      var1.addBoolean("hideInsideOnLowHealth", (Boolean)this.hideOnLowHealth.get());
      if (this.commandGuardPoint != null) {
         var1.addBoolean("commandMoveToGuardPoint", this.commandMoveToGuardPoint);
         var1.addPoint("commandGuardPoint", this.commandGuardPoint);
      }

      if (this.commandFollowMob != null) {
         var1.addInt("commandFollowMob", this.commandFollowMob.getUniqueID());
      }

      if (this.commandAttackMob != null) {
         var1.addInt("commandAttackMob", this.commandAttackMob.getUniqueID());
      }

      if (this.resetCommandsBuffer > 0) {
         var1.addInt("resetCommandsBuffer", this.resetCommandsBuffer);
      }

      this.adventureParty.addSaveData("adventurePartyAuth", var1);
      var1.addInt("settlerSeed", this.settlerSeed);
      var1.addBoolean("isSettler", this.isSettler);
      if (this.isSettler) {
         var1.addInt("settlerIslandX", this.settlerIslandX);
         var1.addInt("settlerIslandY", this.settlerIslandY);
      }

      var1.addBoolean("isTravelingHuman", this.isTravelingHuman);
      if (this.isTravelingHuman) {
         var1.addLong("travelOutTime", this.travelOutTime);
      }

      var1.addInt("settlerQuestTier", this.settlerQuestTier);
      var1.addFloat("hungerLevel", this.hungerLevel);
      if (this.lastFoodEaten != null) {
         var1.addUnsafeString("lastFoodEaten", this.lastFoodEaten.getStringID());
      }

      if (!this.recentFoodItemIDsEaten.isEmpty()) {
         String[] var2 = (String[])this.recentFoodItemIDsEaten.stream().map(ItemRegistry::getItemStringID).filter(Objects::nonNull).toArray((var0) -> {
            return new String[var0];
         });
         var1.addStringArray("recentFoodItemsEaten", var2);
      }

      if (this.moveInPoint != null) {
         var1.addPoint("moveInPoint", this.moveInPoint);
      }

      if (this.moveOutPoint != null) {
         var1.addPoint("moveOutPoint", this.moveOutPoint);
      }

      var1.addSafeString("settlerName", this.settlerName);
      var1.addInt("hungryStrikeBuffer", this.hungryStrikeBuffer);
      var1.addFloat("leaveBuffer", this.leaveBuffer);
      var1.addBoolean("isOnStrike", (Boolean)this.isOnStrike.get());
      if ((Boolean)this.isOnStrike.get()) {
         var1.addBoolean("hungerStrike", this.hungerStrike);
      }

      if (this.endStrikeBuffer > 0) {
         var1.addInt("endStrikeBuffer", this.endStrikeBuffer);
      }

      var1.addInt("workBreakBuffer", this.workBreakBuffer);
      var1.addBoolean("onWorkBreak", this.onWorkBreak);
      SaveData var7 = new SaveData("workPriorities");
      Iterator var3 = this.jobTypeHandler.getTypePriorities().iterator();

      while(true) {
         JobTypeHandler.TypePriority var4;
         do {
            if (!var3.hasNext()) {
               if (!var7.isEmpty()) {
                  var1.addSaveData(var7);
               }

               SaveData var8;
               if (!this.workInventory.isEmpty()) {
                  var8 = new SaveData("workInventory");
                  Iterator var9 = this.workInventory.iterator();

                  while(var9.hasNext()) {
                     InventoryItem var10 = (InventoryItem)var9.next();
                     SaveData var6 = new SaveData("item");
                     var10.addSaveData(var6);
                     var8.addSaveData(var6);
                  }

                  var1.addSaveData(var8);
               }

               if (this.currentMission != null) {
                  var8 = new SaveData("MISSION");
                  var8.addUnsafeString("stringID", this.currentMission.getStringID());
                  this.currentMission.addSaveData(this, var8);
                  var1.addSaveData(var8);
               }

               var1.addBoolean("completedMission", this.completedMission);
               var1.addBoolean("missionFailed", this.missionFailed);
               if (this.missionFailed && this.missionFailedMessage != null) {
                  var1.addSaveData(this.missionFailedMessage.getSaveData("missionFailedMessage"));
               }

               var1.addBoolean("selfManageEquipment", (Boolean)this.selfManageEquipment.get());
               var1.addSaveData(InventorySave.getSave(this.equipmentInventory, "equipment"));
               return;
            }

            var4 = (JobTypeHandler.TypePriority)var3.next();
         } while(var4.priority == 0 && !var4.disabledByPlayer);

         SaveData var5 = new SaveData(var4.type.getStringID());
         var4.addSaveData(var5);
         var7.addSaveData(var5);
      }
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.home = var1.getPoint("home", this.home, false);
      this.hideOnLowHealth.set(var1.getBoolean("hideInsideOnLowHealth", (Boolean)this.hideOnLowHealth.get(), false));
      this.commandMoveToGuardPoint = var1.getBoolean("commandMoveToGuardPoint", this.commandMoveToGuardPoint, false);
      this.commandGuardPoint = var1.getPoint("commandGuardPoint", this.commandGuardPoint, false);
      int var2 = var1.getInt("commandFollowMob", -1, false);
      if (var2 != -1) {
         this.runOnNextServerTick.add(() -> {
            this.commandFollowMob = GameUtils.getLevelMob(var2, this.getLevel());
         });
      }

      int var3 = var1.getInt("commandAttackMob", -1, false);
      if (var3 != -1) {
         this.runOnNextServerTick.add(() -> {
            this.commandAttackMob = GameUtils.getLevelMob(var3, this.getLevel());
         });
      }

      this.resetCommandsBuffer = var1.getInt("resetCommandsBuffer", this.resetCommandsBuffer, false);
      this.adventureParty.applyLoadData("adventurePartyAuth", var1);
      this.setSettlerSeed(var1.getInt("settlerSeed", GameRandom.globalRandom.nextInt(), false));
      this.isSettler = var1.getFirstLoadDataByName("settlerStringID") != null;
      this.isSettler = var1.getBoolean("isSettler", this.isSettler, false);
      if (this.isSettler) {
         this.settlerIslandX = var1.getInt("settlerIslandX", this.settlerIslandX, false);
         this.settlerIslandY = var1.getInt("settlerIslandY", this.settlerIslandY, false);
      }

      this.isTravelingHuman = var1.getBoolean("isTravelingHuman", this.isTravelingHuman, false);
      if (this.isTravelingHuman) {
         this.travelOutTime = var1.getLong("travelOutTime", 0L);
         if (this.travelOutTime <= 0L) {
            this.remove();
         }
      }

      this.settlerQuestTier = var1.getInt("settlerQuestTier", this.settlerQuestTier, 0, Integer.MAX_VALUE, false);
      LoadData var4 = var1.getFirstLoadDataByName("dietFilter");
      if (var4 != null) {
         this.loadedDietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
         this.loadedDietFilter.applyLoadData(var4);
      }

      this.hungerLevel = var1.getFloat("hungerLevel", this.hungerLevel, false);
      String var5 = var1.getUnsafeString("lastFoodEaten", (String)null, false);
      if (var5 != null) {
         String var6 = VersionMigration.tryFixStringID(var5, VersionMigration.oldItemStringIDs);
         Item var7 = ItemRegistry.getItem(var6);
         if (var7 != null && var7.isFoodItem()) {
            this.lastFoodEaten = (FoodConsumableItem)var7;
         }
      }

      this.recentFoodItemIDsEaten.clear();
      String[] var14 = var1.getStringArray("recentFoodItemsEaten", (String[])null, false);
      String var10;
      if (var14 != null) {
         String[] var15 = var14;
         int var8 = var14.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var10 = var15[var9];
            String var11 = VersionMigration.tryFixStringID(var10, VersionMigration.oldItemStringIDs);
            Item var12 = ItemRegistry.getItem(var11);
            if (var12 != null && var12.isFoodItem()) {
               this.recentFoodItemIDsEaten.add(var12.getID());
            }
         }
      }

      this.moveInPoint = var1.getPoint("moveInPoint", this.moveInPoint, false);
      this.moveOutPoint = var1.getPoint("moveOutPoint", this.moveOutPoint, false);
      this.settlerName = var1.getSafeString("settlerName", this.settlerName, false);
      this.hungryStrikeBuffer = var1.getInt("hungryStrikeBuffer", this.hungryStrikeBuffer, false);
      this.leaveBuffer = var1.getFloat("leaveBuffer", this.leaveBuffer, false);
      this.isOnStrike.set(var1.getBoolean("isOnStrike", (Boolean)this.isOnStrike.get(), false));
      this.hungerStrike = var1.getBoolean("hungerStrike", this.hungerStrike, false);
      this.endStrikeBuffer = var1.getInt("endStrikeBuffer", this.endStrikeBuffer, false);
      this.workBreakBuffer = var1.getInt("workBreakBuffer", this.workBreakBuffer, false);
      this.onWorkBreak = var1.getBoolean("onWorkBreak", this.onWorkBreak, false);
      LoadData var16 = var1.getFirstLoadDataByName("workPriorities");
      LoadData var19;
      if (var16 != null) {
         Iterator var17 = var16.getLoadData().iterator();

         while(var17.hasNext()) {
            var19 = (LoadData)var17.next();
            if (var19.isArray()) {
               JobTypeHandler.TypePriority var21 = this.jobTypeHandler.getPriority(var19.getName());
               if (var21 != null) {
                  var21.loadSaveData(var19);
               }
            }
         }
      }

      this.workInventory.clear();
      LoadData var18 = var1.getFirstLoadDataByName("workInventory");
      LoadData var22;
      if (var18 != null) {
         Iterator var20 = var18.getLoadData().iterator();

         while(var20.hasNext()) {
            var22 = (LoadData)var20.next();
            InventoryItem var23 = InventoryItem.fromLoadData(var22);
            if (var23 != null) {
               this.workInventory.add(var23);
            }
         }
      }

      var19 = var1.getFirstLoadDataByName("MISSION");
      if (var19 != null) {
         var10 = var19.getUnsafeString("stringID", (String)null, false);
         if (var10 != null) {
            try {
               this.currentMission = (SettlerMission)SettlerMission.registry.getNewInstance(var10);
               this.currentMission.applySaveData(this, var19);
            } catch (Exception var13) {
               System.err.println("Failed to load explorer mission");
               var13.printStackTrace();
               this.currentMission = null;
            }
         } else {
            GameLog.warn.println("Could not find explorer mission stringID");
         }
      }

      this.completedMission = var1.getBoolean("completedMission", this.completedMission, false);
      this.missionFailed = var1.getBoolean("missionFailed", this.missionFailed, false);
      var22 = var1.getFirstLoadDataByName("missionFailedMessage");
      if (var22 != null) {
         this.missionFailedMessage = GameMessage.loadSave(var22);
      }

      this.selfManageEquipment.set(var1.getBoolean("selfManageEquipment", (Boolean)this.selfManageEquipment.get(), false));
      LoadData var24 = var1.getFirstLoadDataByName("equipment");
      if (var24 != null) {
         this.equipmentInventory.override(InventorySave.loadSave(var24));
      }

      this.updateStats(false);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isSettler = var1.getNextBoolean();
      if (this.isSettler) {
         this.settlerIslandX = var1.getNextInt();
         this.settlerIslandY = var1.getNextInt();
      }

      this.isTravelingHuman = var1.getNextBoolean();
      this.settlerQuestTier = var1.getNextShort();
      this.setSettlerSeed(var1.getNextInt());
      if (var1.getNextBoolean()) {
         this.settlerName = var1.getNextString();
      } else {
         this.settlerName = null;
      }

      this.applyWorkPacket(var1);
      this.currentActivity.readSpawnPacket(var1);
      this.equipmentInventory.override(Inventory.getInventory(var1));
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.isSettler);
      if (this.isSettler) {
         var1.putNextInt(this.settlerIslandX);
         var1.putNextInt(this.settlerIslandY);
      }

      var1.putNextBoolean(this.isTravelingHuman);
      var1.putNextShort((short)this.settlerQuestTier);
      var1.putNextInt(this.settlerSeed);
      var1.putNextBoolean(this.settlerName != null);
      if (this.settlerName != null) {
         var1.putNextString(this.settlerName);
      }

      this.setupWorkPacket(var1);
      this.currentActivity.writeSpawnPacket(var1);
      this.equipmentInventory.writeContent(var1);
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      this.writeObjectUserPacket(var1);
      var1.putNextBoolean(this.currentMission != null);
      if (this.currentMission != null) {
         var1.putNextShortUnsigned(this.currentMission.getID());
         this.currentMission.setupMovementPacket(this, var1);
      }

      var1.putNextBoolean(this.completedMission);
      var1.putNextBoolean(this.missionFailed);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.readObjectUserPacket(var1);
      if (var1.getNextBoolean()) {
         this.currentMission = (SettlerMission)SettlerMission.registry.getNewInstance(var1.getNextShortUnsigned());
         this.currentMission.applyMovementPacket(this, var1);
      } else {
         this.currentMission = null;
      }

      this.completedMission = var1.getNextBoolean();
      this.missionFailed = var1.getNextBoolean();
   }

   public void setupWorkPacket(PacketWriter var1) {
      if (this.home != null) {
         var1.putNextBoolean(true);
         var1.putNextInt(this.home.x);
         var1.putNextInt(this.home.y);
      } else {
         var1.putNextBoolean(false);
      }

      var1.putNextInt(this.settlerHappiness);
      var1.putNextFloat(this.hungerLevel);
      var1.putNextBoolean(this.lastFoodEaten != null);
      if (this.lastFoodEaten != null) {
         var1.putNextShortUnsigned(this.lastFoodEaten.getID());
      }

      var1.putNextBoolean(this.onWorkBreak);
      var1.putNextShortUnsigned(this.workInventory.size());
      Iterator var2 = this.workInventory.iterator();

      while(var2.hasNext()) {
         InventoryItem var3 = (InventoryItem)var2.next();
         InventoryItem.addPacketContent(var3, var1);
      }

   }

   public void applyWorkPacket(PacketReader var1) {
      this.workDirty = true;
      int var2;
      int var3;
      if (var1.getNextBoolean()) {
         var2 = var1.getNextInt();
         var3 = var1.getNextInt();
         this.home = new Point(var2, var3);
      } else {
         this.home = null;
      }

      this.settlerHappiness = var1.getNextInt();
      this.hungerLevel = var1.getNextFloat();
      if (var1.getNextBoolean()) {
         var2 = var1.getNextShortUnsigned();
         Item var4 = ItemRegistry.getItem(var2);
         if (var4 != null && var4.isFoodItem()) {
            this.lastFoodEaten = (FoodConsumableItem)var4;
         } else {
            this.lastFoodEaten = null;
         }
      } else {
         this.lastFoodEaten = null;
      }

      this.onWorkBreak = var1.getNextBoolean();
      var2 = var1.getNextShortUnsigned();
      this.workInventory = new ArrayList(var2);

      for(var3 = 0; var3 < var2; ++var3) {
         this.workInventory.add(InventoryItem.fromContentPacket(var1));
      }

   }

   public void sendWorkUpdatePacket() {
      this.sendWorkUpdatePacket = true;
   }

   public void init() {
      super.init();
      this.workSettings.closeRegistry();
      this.interactClients = new ArrayList();
      if (this.settlerSeed == 0) {
         this.setSettlerSeed(GameRandom.globalRandom.nextInt());
      }

      this.settlerCheck = GameRandom.globalRandom.nextInt(600);
      this.updateStats(true);
      this.equipmentBuffManager.updateArmorBuffs();
      this.equipmentBuffManager.updateCosmeticSetBonus();
      this.equipmentBuffManager.updateTrinketBuffs();
      this.updateTeam();
      this.ai = new BehaviourTreeAI(this, new HumanAI(320, true, false, 25000), new AIMover(humanPathIterations));
      this.defaultAttackAnimTime = this.attackAnimTime;
      this.defaultAttackCooldown = this.attackCooldown;
      this.jobTypeHandler.startGlobalCooldown(this.getWorldEntity().getTime(), GameRandom.globalRandom.getIntBetween(10, 30) * 1000);
      this.jobTypeHandler.startCooldowns(this.getWorldEntity().getTime());
      this.searchEquipmentCooldown = this.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(60, 300) * 1000L;
      if (this.isServer() && this.isSettler() && !this.isSettlerOnCurrentLevel()) {
         SettlersWorldData var1 = SettlersWorldData.getSettlersData(this.getLevel().getServer());
         if (!var1.exists(this.getUniqueID())) {
            this.remove();
         } else {
            this.commandGuard((ServerClient)null, this.getX(), this.getY());
         }
      }

   }

   public void onLevelChanged() {
      super.onLevelChanged();
      if (this.isServer() && this.levelSettler != null) {
         this.levelSettler.isOutsideLevel = !this.isSettlerOnCurrentLevel();
         if (!this.levelSettler.isOutsideLevel) {
            SettlersWorldData var1 = SettlersWorldData.getSettlersData(this.getLevel().getServer());
            var1.remove(this.getUniqueID());
         } else {
            this.setHome((Point)null);
         }
      }

   }

   public void addInteractClient(ServerClient var1) {
      if (!this.interactClients.contains(var1)) {
         this.interactClients.add(var1);
      }

   }

   public boolean isBeingInteractedWith() {
      return !this.interactClients.isEmpty();
   }

   public void removeInteractClient(ServerClient var1) {
      this.interactClients.remove(var1);
   }

   public GameMessage getLocalization() {
      return (GameMessage)(this.settlerName == null ? super.getLocalization() : new LocalMessage("mob", this.getStringID() + "name", "name", this.settlerName));
   }

   public LootTable getLootTable() {
      LootTable var1 = new LootTable();
      Iterator var2 = this.workInventory.iterator();

      InventoryItem var3;
      while(var2.hasNext()) {
         var3 = (InventoryItem)var2.next();
         var1.items.add(new LootInventoryItem(var3));
      }

      for(int var4 = 0; var4 < this.equipmentInventory.getSize(); ++var4) {
         var3 = this.equipmentInventory.getItem(var4);
         if (var3 != null) {
            var1.items.add(new LootInventoryItem(var3));
         }
      }

      return var1;
   }

   protected void doBeforeHitCalculatedLogic(MobBeforeHitCalculatedEvent var1) {
      super.doBeforeHitCalculatedLogic(var1);
      if (!var1.isPrevented() && var1.getExpectedHealth() <= 0 && this.adventureParty.isInAdventureParty()) {
         this.adventureParty.onSecondWindAttempt(var1);
      }

   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      if (this.isSettler() && this.isServer()) {
         SettlementLevelData var3 = this.getSettlementLevelData();
         if (var3 != null) {
            LevelSettler var4 = var3.getSettler(this.getUniqueID());
            if (var4 != null) {
               var3.onSettlerDeath(var4);
               if (this.levelSettler != null) {
                  this.levelSettler.onSettlerDeath();
               }
            }

            LocalMessage var5 = new LocalMessage("deaths", "settlerfrom", new Object[]{"victim", this.getLocalization(), "settlement", var3.getLevel().settlementLayer.getSettlementName()});
            GameMessageBuilder var6 = (new GameMessageBuilder()).append("\u00a79").append(DeathMessageTable.getDeathMessage(var1, var5));
            var3.getLevel().settlementLayer.streamTeamMembers().forEach((var1x) -> {
               var1x.sendChatMessage(var6);
            });
         }
      }

   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      if (!this.removed() && this.getLevel() != null && this.isServer() && this.adventureParty.isInAdventureParty()) {
         ServerClient var5 = this.adventureParty.getServerClient();
         var5.adventureParty.serverRemove(this, true, var4);
      }

      super.remove(var1, var2, var3, var4);
   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS : null;
   }

   public void clientTick() {
      this.equipmentBuffManager.clientTickEffects();
      super.clientTick();
      if (this.objectUser != null) {
         this.objectUser.tick();
      }

      this.tickHunger();
      InventoryItem.tickList(this, this, this, this.getWorldSettings(), 1.0F, this.workInventory);
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

      this.boomerangs.removeIf(Entity::removed);
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.equipmentInventory.tickItems(this);
      }));
      if (this.currentMission != null) {
         this.currentMission.clientTick(this);
      }

   }

   public void serverTick() {
      super.serverTick();
      float var1;
      if (this.leaveBuffer > 0.0F && this.hungerLevel > 0.0F && !this.isOnStrike()) {
         var1 = 1.3888889E-5F;
         this.leaveBuffer -= var1;
      }

      if ((Boolean)this.isOnStrike.get()) {
         if (this.hungerStrike) {
            if (this.hungerLevel > 0.0F) {
               this.isOnStrike.set(false);
            }
         } else if ((float)this.getSettlerHappiness() >= 0.5F) {
            this.isOnStrike.set(false);
         }
      }

      if (this.endStrikeBuffer > 0) {
         if (!this.getLevel().settlementLayer.isRaidActive() && !this.getLevel().settlementLayer.isRaidApproaching() && this.hasCommandOrders()) {
            this.clearCommandsOrders((ServerClient)null);
         }

         this.endStrikeBuffer -= 50;
      }

      if ((Boolean)this.isOnStrike.get() && this.endStrikeBuffer <= 0) {
         this.isOnStrike.set(false);
      }

      if (!this.hasActiveJob() && !this.isOnStrike() && !this.hasCommandOrders()) {
         var1 = this.isSettler() ? GameMath.limit((float)this.getSettlerHappiness() / 100.0F, 0.0F, 1.0F) : 0.5F;
         float var2 = GameMath.lerp(var1, workBreakBufferRegenModAtNoHappiness, workBreakBufferRegenModAtFullHappiness);
         this.workBreakBuffer += (int)(50.0F * var2);
         if (this.onWorkBreak && this.workBreakBuffer >= resetWorkBreakWhenBufferAt) {
            this.sendWorkUpdatePacket();
            this.onWorkBreak = false;
         }

         int var3 = GameMath.lerp(var1, maxWorkBreakBufferAtNoHappiness, maxWorkBreakBufferAtFullHappiness);
         if (this.workBreakBuffer >= var3) {
            this.workBreakBuffer = var3;
            if (this.onWorkBreak) {
               this.sendWorkUpdatePacket();
            }

            this.onWorkBreak = false;
         }
      }

      if (this.objectUser != null) {
         this.objectUser.tick();
      }

      this.tickHunger();
      InventoryItem.tickList(this, this, this, this.getWorldSettings(), 1.0F, this.workInventory);
      if (this.resetCommandsBuffer > 0) {
         this.resetCommandsBuffer -= 50;
         if (this.resetCommandsBuffer <= 0) {
            this.clearCommandsOrders((ServerClient)null);
         }
      }

      this.adventureParty.serverTick();
      if (this.isSettler()) {
         if (this.commandFollowMob != null && !this.commandFollowMob.isSamePlace(this)) {
            this.commandFollowMob = null;
         }

         ++this.settlerCheck;
         if (this.settlerCheck > 600) {
            this.runSettlerCheck();
            this.settlerCheck = 0;
         }
      }

      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

      this.boomerangs.removeIf(Entity::removed);

      for(int var4 = 0; var4 < this.interactClients.size(); ++var4) {
         if (((ServerClient)this.interactClients.get(var4)).isDisposed()) {
            this.interactClients.remove(var4);
            --var4;
         }
      }

      this.clientHasCommandOrders.set(this.hasCommandOrders());
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.equipmentInventory.tickItems(this);
      }));
      this.serverTickInventorySync(this.getLevel().getServer(), this);
      if (this.sendWorkUpdatePacket && this.isServer()) {
         this.sendWorkUpdatePacket = false;
         this.getLevel().getServer().network.sendToClientsAt(new PacketHumanWorkUpdate(this), (Level)this.getLevel());
      }

      if (this.isTravelingHuman) {
         this.travelOutTime -= 50L;
         if (this.moveOutPoint == null) {
            if (this.travelOutTime <= 0L) {
               this.endTravelingHuman();
            }
         } else if (this.travelOutTime <= (long)(-timeToTravelOut * 1000)) {
            this.remove();
         }
      }

      if (this.currentMission != null) {
         if (!this.currentMission.isOver()) {
            this.currentMission.serverTick(this);
         }

         if (this.currentMission.isOver()) {
            this.currentMission = null;
         }
      }

      if (this.isSettlerOnCurrentLevel()) {
         if (this.getCurrentMission() != null) {
            this.setActivity("idle", 1000, new LocalMessage("activities", "onmission"));
         } else if (this.isOnStrike() && !this.getLevel().settlementLayer.isRaidActive() && !this.getLevel().settlementLayer.isRaidApproaching()) {
            this.setActivity("idle", 1000, new LocalMessage("activities", "onstrike"));
         } else if (this.isHiding) {
            this.setActivity("idle", 1000, new LocalMessage("activities", "hiding"));
         } else if (this.getWorldEntity().isNight() && !this.getLevel().settlementLayer.isRaidActive()) {
            this.setActivity("idle", 1000, new LocalMessage("activities", "resting"));
         } else if (this.isOnWorkBreak()) {
            this.setActivity("idle", 1000, new LocalMessage("activities", "onbreak"));
         } else if (this.getWorkInventory().isFull()) {
            this.setActivity("idle", 1000, new LocalMessage("activities", "inventoryfull"));
         }
      }

      this.currentActivity.serverTick();
   }

   public final void setHome(int var1, int var2) {
      this.setHome(new Point(var1, var2));
   }

   public void setHome(Point var1) {
      if (!Objects.equals(this.home, var1)) {
         this.home = var1;
         this.sendWorkUpdatePacket();
      }

   }

   public int getHomeX() {
      return this.home == null ? -1 : this.home.x;
   }

   public int getHomeY() {
      return this.home == null ? -1 : this.home.y;
   }

   public void setUsingObject(ObjectUserActive var1) {
      this.objectUser = var1;
      var1.init(this);
   }

   public ObjectUserActive getUsingObject() {
      return this.objectUser;
   }

   public void clearUsingObject() {
      this.objectUser = null;
   }

   public Rectangle getSelectBox(int var1, int var2) {
      return this.objectUser != null ? this.objectUser.getUserSelectBox() : super.getSelectBox(var1, var2);
   }

   public float getHungerLevel() {
      return this.hungerLevel;
   }

   public void tickHunger() {
      if (this.isSettler()) {
         if (this.getCurrentMission() == null) {
            boolean var1 = this.adventureParty.isInAdventureParty() && !this.isSettlerOnCurrentLevel();
            if (!this.getWorldEntity().isNight() || var1) {
               double var2 = (double)secondsToPassAtFullHunger;
               if (var1) {
                  var2 /= (double)adventurePartyHungerUsageMod;
               }

               float var4 = (float)(50.0 / (1000.0 * var2));
               this.useHunger(var4, false);
               if (this.hungerLevel <= 0.0F && !this.isOnStrike() && this.isServer()) {
                  this.hungryStrikeBuffer += 50;
                  if (this.hungryStrikeBuffer >= 300000) {
                     this.hungryStrikeBuffer = 0;
                     this.addLeaveBuffer(0.2F);
                     this.attemptStartStrike(true, false);
                  }
               }

               if (this.hungerLevel < 0.1F) {
                  this.lastFoodEaten = null;
               } else {
                  this.hungryStrikeBuffer = 0;
               }

            }
         }
      }
   }

   public void useHunger(float var1, boolean var2) {
      this.hungerLevel = Math.max(0.0F, this.hungerLevel - var1);
   }

   public void addHunger(float var1) {
      this.hungerLevel = Math.min(this.hungerLevel + var1, 1.0F + var1);
      this.workDirty = true;
   }

   public boolean useFoodItem(FoodConsumableItem var1, boolean var2) {
      boolean var3 = HungerMob.super.useFoodItem(var1, var2);
      this.playConsumeSound.runAndSend(var1.drinkSound);
      this.lastFoodEaten = var1;
      this.recentFoodItemIDsEaten.addFirst(var1.getID());

      while(this.recentFoodItemIDsEaten.size() > ((DietThought)Settler.dietThoughts.last()).variety) {
         this.recentFoodItemIDsEaten.removeLast();
      }

      this.updateHappiness();
      this.sendWorkUpdatePacket();
      return var3;
   }

   public void addLeaveBuffer(float var1) {
      if (this.isServer()) {
         WorldSettings var2 = this.getWorldSettings();
         if (var2 == null || var2.survivalMode) {
            this.leaveBuffer += var1;
            if (this.isSettler()) {
               Level var3 = this.getSettlementServerLevel();
               LocalMessage var4 = null;
               if (this.leaveBuffer >= 0.8F && this.leaveBufferWarnings < this.leaveBuffer) {
                  this.leaveBufferWarnings = this.leaveBuffer;
                  var4 = new LocalMessage("misc", "settlerhungrywarning2", new Object[]{"settler", this.getLocalization(), "settlement", var3.settlementLayer.getSettlementName()});
               } else if (this.leaveBuffer >= 0.6F && this.leaveBufferWarnings < this.leaveBuffer) {
                  this.leaveBufferWarnings = this.leaveBuffer;
                  var4 = new LocalMessage("misc", "settlerhungrywarning2", new Object[]{"settler", this.getLocalization(), "settlement", var3.settlementLayer.getSettlementName()});
               } else if (this.leaveBuffer >= 0.4F && this.leaveBufferWarnings < this.leaveBuffer) {
                  this.leaveBufferWarnings = this.leaveBuffer;
                  var4 = new LocalMessage("misc", "settlerhungrywarning1", new Object[]{"settler", this.getLocalization(), "settlement", var3.settlementLayer.getSettlementName()});
               } else {
                  this.leaveBufferWarnings = 0.0F;
               }

               if (this.leaveBuffer >= 1.0F) {
                  SettlementLevelData var5 = this.getSettlementLevelData();
                  if (var5 != null && var5.removeSettler(this.getUniqueID(), (ServerClient)null)) {
                     var4 = new LocalMessage("misc", "settlerhungryleft", new Object[]{"settler", this.getLocalization(), "settlement", var3.settlementLayer.getSettlementName()});
                  }
               }

               if (var4 != null) {
                  var3.settlementLayer.streamTeamMembers().forEach((var1x) -> {
                     var1x.sendChatMessage(var4);
                  });
               }
            }

         }
      }
   }

   public void setSettlerSeed(int var1) {
      this.settlerSeed = var1;
      if (this.settlerName == null) {
         this.settlerName = this.getRandomName(new GameRandom((long)this.settlerSeed));
      }

   }

   public int getSettlerSeed() {
      return this.settlerSeed;
   }

   public void setSettlerName(String var1) {
      this.settlerName = var1;
   }

   public String getSettlerName() {
      return this.settlerName;
   }

   public Point getDrawPos() {
      return this.objectUser != null ? this.objectUser.getUserAppearancePos() : super.getDrawPos();
   }

   public int getDrawX() {
      return this.objectUser != null ? this.objectUser.getUserAppearancePos().x : super.getDrawX();
   }

   public int getDrawY() {
      return this.objectUser != null ? this.objectUser.getUserAppearancePos().y : super.getDrawY();
   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (!this.isAttacking) {
         this.turnTo(var1);
      }

   }

   public boolean canInteract(Mob var1) {
      return true;
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return Localization.translate("controls", "talktip");
   }

   protected void turnTo(Mob var1) {
      int var2 = var1.getX() - this.getX();
      int var3 = var1.getY() - this.getY();
      this.setFacingDir((float)var2, (float)var3);
   }

   public int getRockSpeed() {
      return 20;
   }

   public boolean isBusy() {
      return this.currentMission != null;
   }

   public boolean canBePushed(Mob var1) {
      if (this.objectUser != null && this.objectUser.object.preventsUsersPushed()) {
         return false;
      } else {
         return this.currentMission != null && !this.currentMission.isMobVisible(this) ? false : super.canBePushed(var1);
      }
   }

   public boolean canPushMob(Mob var1) {
      if (this.objectUser != null && this.objectUser.object.preventsUsersPushed()) {
         return false;
      } else {
         return this.currentMission != null && !this.currentMission.isMobVisible(this) ? false : super.canPushMob(var1);
      }
   }

   public boolean isVisible() {
      return this.currentMission != null ? this.currentMission.isMobVisible(this) : super.isVisible();
   }

   public MoveToPoint getMoveToPoint() {
      if (this.currentMission != null) {
         return this.currentMission.getMoveOutPoint(this);
      } else if (this.moveOutPoint != null) {
         if (this.isAtEdgeOfMap(5)) {
            this.remove();
         }

         return new MoveToPoint(this.moveOutPoint, false) {
            public boolean moveIfPathFailed(float var1) {
               return var1 >= 30.0F;
            }

            public boolean isAtLocation(float var1, boolean var2) {
               if (var2) {
                  return var1 < 2.0F;
               } else {
                  return var1 < 30.0F;
               }
            }

            public void onAtLocation() {
               HumanMob.this.remove();
            }
         };
      } else {
         return this.moveInPoint != null ? new MoveToPoint(this.moveInPoint, true) {
            public boolean moveIfPathFailed(float var1) {
               return var1 >= 30.0F;
            }

            public boolean isAtLocation(float var1, boolean var2) {
               if (var2) {
                  return var1 < 5.0F;
               } else {
                  return var1 < 30.0F;
               }
            }

            public void onAtLocation() {
               HumanMob.this.stopMovingIn();
            }
         } : null;
      }
   }

   public void assignBed(LevelSettler var1, SettlementBed var2, boolean var3) {
      this.moveOutPoint = null;
      this.levelSettler = var1;
      if (var2 != null) {
         this.moveIn(var2.tileX, var2.tileY, var3);
      } else {
         Point var4 = null;
         SettlementLevelData var5 = this.getSettlementLevelData();
         if (var5 != null) {
            var4 = var5.getObjectEntityPos();
         }

         if (var4 == null) {
            var4 = new Point(this.getLevel().width / 2, this.getLevel().height / 2);
         }

         this.moveIn(var4.x, var4.y, var3);
      }

   }

   public void moveIn(int var1, int var2, boolean var3) {
      this.setHome(var1, var2);
      if (var3) {
         this.moveInPoint = this.getNewMoveInPoint(var1, var2);
      }

   }

   public boolean isMovingIn() {
      return this.moveInPoint != null;
   }

   public void stopMovingIn() {
      this.moveInPoint = null;
   }

   public void moveOut() {
      if (this.moveOutPoint == null) {
         this.moveOutPoint = this.getNewEdgeOfMapPoint();
         this.isSettler = false;
         this.levelSettler = null;
         this.cancelJob();
      }
   }

   public boolean isMovingOut() {
      return this.moveOutPoint != null;
   }

   protected Point getNewMoveInPoint(int var1, int var2) {
      if (this.getLevel().isOutside(var1, var2)) {
         return new Point(var1, var2);
      } else {
         ArrayList var3 = new ArrayList();
         ArrayList var4 = this.getLevel().regionManager.getRoom(var1, var2);
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            SemiRegion var6 = (SemiRegion)var5.next();
            Iterator var7 = var6.getLevelTiles().iterator();

            while(var7.hasNext()) {
               Point var8 = (Point)var7.next();
               if (!this.getLevel().getObject(var8.x, var8.y).isSolid(this.getLevel(), var8.x, var8.y)) {
                  var3.add(var8);
               }
            }
         }

         if (var3.isEmpty()) {
            return new Point(var1, var2);
         } else {
            return (Point)GameRandom.globalRandom.getOneOf((List)var3);
         }
      }
   }

   public boolean isAtEdgeOfMap(int var1) {
      return !(new Rectangle(var1 * 32, var1 * 32, this.getLevel().width * 32 - var1 * 2 * 32, this.getLevel().height * 32 - var1 * 2 * 32)).contains(this.getPositionPoint());
   }

   public Point getNewEdgeOfMapPoint() {
      byte var1 = 3;
      return (Point)GameRandom.globalRandom.getOneOf((Object[])(new Point(var1 + GameRandom.globalRandom.nextInt(this.getLevel().width - var1 * 2), var1), new Point(this.getLevel().width - 1 - var1, var1 + GameRandom.globalRandom.nextInt(this.getLevel().height - var1 * 2)), new Point(var1 + GameRandom.globalRandom.nextInt(this.getLevel().width - var1 * 2), this.getLevel().height - 1 - var1), new Point(var1, var1 + GameRandom.globalRandom.nextInt(this.getLevel().height - var1 * 2))));
   }

   public boolean startMission(SettlerMission var1) {
      if (var1.canStart(this)) {
         this.cancelJob();
         if (this.adventureParty.isInAdventureParty()) {
            this.adventureParty.clear(true);
         }

         this.currentMission = var1;
         this.currentMission.start(this);
         this.sendMovementPacket(false);
         return true;
      } else {
         return false;
      }
   }

   public SettlerMission getCurrentMission() {
      return this.currentMission;
   }

   public List<ExpeditionList> getPossibleExpeditions() {
      return Collections.emptyList();
   }

   public boolean hasCompletedMission() {
      return this.completedMission;
   }

   public void clearMissionResult() {
      this.completedMission = false;
      this.missionFailed = false;
      this.missionFailedMessage = null;
      this.sendMovementPacket(false);
   }

   public void updateTeam() {
      if (this.getLevel() != null && !this.isClient()) {
         int var1 = -10;
         long var2 = -1L;
         if (this.isSettler()) {
            int var4;
            if (this.isSettlerOnCurrentLevel()) {
               var4 = this.getLevel().settlementLayer.getTeamID();
               var2 = this.getLevel().settlementLayer.getOwnerAuth();
            } else {
               SettlementLevelData var5 = this.getSettlementLevelData();
               var4 = var5.getLevel().settlementLayer.getTeamID();
               var2 = var5.getLevel().settlementLayer.getOwnerAuth();
            }

            if (var4 != -1) {
               var1 = var4;
            }
         } else if (this.isTravelingHuman()) {
            var1 = -1;
         }

         this.team.set(var1);
         this.owner.set(var2);
      }
   }

   public void tickSettler(SettlementLevelData var1, LevelSettler var2) {
      this.levelSettler = var2;
      this.updateTeam();
      this.updateSettlerStats(var1, var2);
      this.updateHappiness();
   }

   public void makeSettler(SettlementLevelData var1, LevelSettler var2) {
      if (!this.isSettler()) {
         this.hungerLevel = 0.2F;
         this.selfManageEquipment.set(var1.newSettlerSelfManageEquipment);
         this.markDirty();
      }

      if (this.loadedDietFilter != null) {
         var2.dietFilter.loadFromCopy(this.loadedDietFilter);
         this.loadedDietFilter = null;
      }

      this.isSettler = true;
      this.isTravelingHuman = false;
      this.settlerIslandX = var1.getLevel().getIslandX();
      this.settlerIslandY = var1.getLevel().getIslandY();
      this.levelSettler = var2;
      this.setSettlerSeed(var2.settlerSeed);
      this.assignBed(var2, var2.getBed(), false);
      this.updateSettlerStats(var1, var2);
      this.updateTeam();
   }

   public void startTravelingHuman(SettlementLevelData var1) {
      this.levelSettler = null;
      this.isSettler = false;
      this.isTravelingHuman = true;
      this.settlerQuestTier = var1.getQuestTiersCompleted();
      this.updateStats(true);
      Point var2 = null;
      SettlementLevelData var3 = SettlementLevelData.getSettlementData(this.getLevel());
      if (var3 != null) {
         var2 = var3.getObjectEntityPos();
      }

      if (var2 == null) {
         var2 = new Point(this.getLevel().width / 2, this.getLevel().height / 2);
      }

      this.moveIn(var2.x, var2.y, true);
      this.home = new Point(this.moveInPoint);
      this.travelOutTime = (long)(travelingStayTimeSeconds * 1000);
      this.updateTeam();
   }

   public void endTravelingHuman() {
      this.moveOut();
      SettlementLevelData var1 = SettlementLevelData.getSettlementData(this.getLevel());
      if (var1 != null) {
         var1.onTravelingHumanLeave(this);
      }

   }

   public void updateSettlerStats(SettlementLevelData var1, LevelSettler var2) {
      int var3 = this.settlerQuestTier;
      this.settlerQuestTier = var1.getQuestTiersCompleted();
      if (var3 != this.settlerQuestTier) {
         this.updateStats(true);
         this.markDirty();
      }

   }

   public List<HappinessModifier> getHappinessModifiers() {
      ArrayList var1 = new ArrayList();
      if (this.lastFoodEaten != null && this.lastFoodEaten.quality != null) {
         var1.add(this.lastFoodEaten.quality.getModifier());
      } else {
         var1.add(FoodQuality.noFoodModifier);
      }

      int var2 = (int)this.recentFoodItemIDsEaten.stream().distinct().count();
      DietThought var3 = Settler.getDietThought(var2);
      if (var3 != null) {
         var1.add(var3.getModifier());
      }

      if (this.levelSettler != null) {
         if (this.levelSettler.getBed() != null) {
            SettlementRoom var4 = this.levelSettler.getBed().getRoom();
            if (var4 != null) {
               RoomSize var5 = Settler.getRoomSize(var4.getRoomSize());
               if (var5 != null) {
                  var1.add(var5.getModifier());
               }

               RoomQuality var6 = Settler.getRoomQuality(var4.getFurnitureScore());
               if (var6 != null) {
                  var1.add(var6.getModifier());
               }

               if (var4.getRoomProperty("outsidefloor") > 0) {
                  var1.add(new HappinessModifier(-10, (new GameMessageBuilder()).append("settlement", "roommissingfloor")));
               }

               if (var4.getRoomProperty("lights") <= 0) {
                  var1.add(new HappinessModifier(-10, (new GameMessageBuilder()).append("settlement", "roommissinglights")));
               }

               int var7 = var4.getOccupiedBeds();
               if (var7 > 1) {
                  int var8 = Math.min(10 + (var7 - 1) * 10, 50);
                  var1.add(new HappinessModifier(-var8, (new GameMessageBuilder()).append("settlement", "sharingroom").append(" (" + (var7 - 1) + ")")));
               }
            } else {
               var1.add(HappinessModifier.bedOutsideModifier);
            }
         } else {
            var1.add(HappinessModifier.noBedModifier);
         }

         PopulationThought var9 = Settler.getPopulationThough(this.levelSettler.data.countTotalSettlers());
         if (var9 != null) {
            var1.add(var9.getModifier());
         }
      }

      return var1;
   }

   public void updateHappiness() {
      int var1 = this.settlerHappiness;
      this.settlerHappiness = 0;
      List var2 = this.getHappinessModifiers();

      HappinessModifier var4;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); this.settlerHappiness += var4.happiness) {
         var4 = (HappinessModifier)var3.next();
      }

      if (var1 != this.settlerHappiness) {
         this.sendWorkUpdatePacket();
         if (this.settlerHappiness >= 100) {
            Level var5 = this.getSettlementServerLevel();
            if (var5 != null) {
               var5.settlementLayer.streamTeamMembers().forEach((var0) -> {
                  if (var0.achievementsLoaded()) {
                     var0.achievements().CLOUD_NINE.markCompleted(var0);
                  }

               });
            }
         }
      }

   }

   public boolean doesEatFood() {
      return !this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).disabledBySettler;
   }

   public int getSettlerHappiness() {
      return this.settlerHappiness;
   }

   public GameMessage getCurrentActivity() {
      GameMessage var1 = this.currentActivity.getCurrentActivity();
      return (GameMessage)(var1 != null ? var1 : new LocalMessage("activities", "idle"));
   }

   public void updateStats(boolean var1) {
      int var2 = this.getHealth();
      float var3 = (float)this.getHealth() / (float)this.getMaxHealth();
      if (!this.isSettler() && !this.isTravelingHuman()) {
         this.buffManager.removeBuff((Buff)BuffRegistry.SETTLER_STATS_BUFF, false);
         this.setMaxHealth(this.nonSettlerHealth);
         this.setArmor(50);
      } else {
         EquipmentActiveBuff var4 = new EquipmentActiveBuff(BuffRegistry.SETTLER_STATS_BUFF, this);
         ArrayList var5 = SettlementQuestTier.questTiers;

         for(int var6 = 0; var6 < Math.min(this.settlerQuestTier, var5.size()); ++var6) {
            ModifierValue[] var7 = ((SettlementQuestTier)var5.get(var6)).getTierCompletedSettlerModifiers(this);
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               ModifierValue var10 = var7[var9];
               var10.add(var4);
            }
         }

         this.buffManager.addBuff(var4, false, true);
         this.setMaxHealth(this.settlerHealth);
         this.setArmor(0);
      }

      this.buffManager.forceUpdateBuffs();
      if (var1) {
         this.setHealth(Math.max(var2, (int)((float)this.getMaxHealth() * var3)));
      }

   }

   public boolean isFriendlyClient(NetworkClient var1) {
      if (var1 != null && var1.playerMob != null) {
         long var2 = (Long)this.owner.get();
         if (var2 != -1L) {
            if (var2 == var1.authentication) {
               return true;
            }

            if (var1.pvpEnabled()) {
               Object var4 = null;
               if (this.isClient()) {
                  var4 = this.getClient().getClientByAuth(var2);
               } else if (this.isServer()) {
                  var4 = this.getServer().getClientByAuth(var2);
               }

               if (var4 != null && !((NetworkClient)var4).pvpEnabled()) {
                  return true;
               }

               if (var4 == null && !this.getWorldSettings().forcedPvP) {
                  return true;
               }
            } else if (!this.getWorldSettings().forcedPvP) {
               return true;
            }
         }

         return this.isSameTeam(var1.playerMob);
      } else {
         return false;
      }
   }

   public boolean isFriendlyHuman(HumanMob var1) {
      if (!this.isTravelingHuman() && !var1.isTravelingHuman()) {
         int var2 = this.getTeam();
         int var3 = var1.getTeam();
         if (var2 == -1 && var3 == -1) {
            return true;
         } else if (var2 >= 0 && var2 == var3) {
            return true;
         } else {
            long var4 = (Long)this.owner.get();
            long var6 = (Long)var1.owner.get();
            if (var4 == var6) {
               return true;
            } else {
               if (!this.getWorldSettings().forcedPvP && var4 != -1L && var6 != -1L) {
                  Object var8 = null;
                  Object var9 = null;
                  if (this.isClient()) {
                     var8 = this.getClient().getClientByAuth(var4);
                     var9 = this.getClient().getClientByAuth(var6);
                  } else if (this.isServer()) {
                     var8 = this.getServer().getClientByAuth(var4);
                     var9 = this.getServer().getClientByAuth(var6);
                  }

                  if (var8 == null || var9 == null) {
                     return true;
                  }

                  if (!((NetworkClient)var8).pvpEnabled() || !((NetworkClient)var9).pvpEnabled()) {
                     return true;
                  }
               }

               return false;
            }
         }
      } else {
         return true;
      }
   }

   public GameMessage willJoinAdventureParty(ServerClient var1) {
      return this.isOnStrike() ? new LocalMessage("ui", "settlerjoinpartynothappy") : null;
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      Stream var1 = super.getDefaultModifiers();
      if (!this.isSettler()) {
         var1 = Stream.concat(var1, Stream.of(new ModifierValue(BuffModifiers.ALL_DAMAGE, 2.0F), new ModifierValue(BuffModifiers.ARMOR_PEN_FLAT, 1000)));
      }

      if (this.getLevel() != null) {
         if (this.isSettler() && this.adventureParty.isInAdventureParty()) {
            var1 = Stream.concat(var1, Stream.of((new ModifierValue(BuffModifiers.FIRE_DAMAGE)).max(0.0F), (new ModifierValue(BuffModifiers.POISON_DAMAGE)).max(0.0F)));
         }

         if (this.getLevel().settlementLayer.isRaidActive()) {
            var1 = Stream.concat(var1, Stream.of(new ModifierValue(BuffModifiers.SPEED_FLAT, 10.0F)));
         }

         if (this.getHealthPercent() <= 0.4F) {
            var1 = Stream.concat(var1, Stream.of(new ModifierValue(BuffModifiers.KNOCKBACK_INCOMING_MOD, 0.5F)));
         }
      }

      return var1;
   }

   public float getRegenFlat() {
      if (this.adventureParty.isInAdventureParty() && !this.isSettlerOnCurrentLevel()) {
         return super.getRegenFlat();
      } else {
         int var1 = this.getMaxHealth();
         return Math.max((float)var1 / (float)this.maxSecondsFullHealthRegen, super.getRegenFlat());
      }
   }

   public boolean canTarget(Mob var1) {
      if (var1.isPlayer && this.isFriendlyClient(((PlayerMob)var1).getNetworkClient())) {
         return false;
      } else if (var1 instanceof HumanMob) {
         return !this.isFriendlyHuman((HumanMob)var1);
      } else {
         return super.canTarget(var1);
      }
   }

   public boolean canBeTargeted(Mob var1, NetworkClient var2) {
      return var2 != null && this.isFriendlyClient(var2) ? false : super.canBeTargeted(var1, var2);
   }

   public boolean isSameTeam(Mob var1) {
      return var1 instanceof HumanMob ? this.isFriendlyHuman((HumanMob)var1) : super.isSameTeam(var1);
   }

   public boolean isSettler() {
      return this.isSettler;
   }

   public LevelIdentifier getSettlementLevelIdentifier() {
      return !this.isSettler() ? null : new LevelIdentifier(this.settlerIslandX, this.settlerIslandY, 0);
   }

   public String getSettlerStringID() {
      return this.settlerStringID;
   }

   public boolean isTravelingHuman() {
      return this.isTravelingHuman;
   }

   public Inventory getInventory() {
      return this.equipmentInventory;
   }

   public InventoryItem getDisplayArmor(int var1, InventoryItem var2) {
      if (!this.equipmentInventory.isSlotClear(3 + var1) && this.equipmentInventory.getItemSlot(3 + var1).isArmorItem()) {
         return this.equipmentInventory.getItem(3 + var1);
      } else {
         if (!this.equipmentInventory.isSlotClear(var1)) {
            if (var1 == 0 && !Settings.showSettlerHeadArmor) {
               return var2;
            }

            if (this.equipmentInventory.getItemSlot(var1).isArmorItem()) {
               return this.equipmentInventory.getItem(var1);
            }
         }

         return var2;
      }
   }

   public InventoryItem getDisplayArmor(int var1, String var2) {
      return this.getDisplayArmor(var1, var2 == null ? null : new InventoryItem(var2));
   }

   public Point getJobSearchTile() {
      return this.home != null ? new Point(this.home.x, this.home.y) : new Point(this.getTileX(), this.getTileY());
   }

   public JobSequence findJob() {
      if (this.getWorldEntity().isNight()) {
         return null;
      } else if (this.objectUser != null) {
         return null;
      } else if (this.getLevel().settlementLayer.isRaidActive()) {
         return null;
      } else if (this.isHiding) {
         return null;
      } else if (this.isTravelingHuman()) {
         return null;
      } else if (this.hasCommandOrders()) {
         return null;
      } else if (this.adventureParty.isInAdventureParty()) {
         return null;
      } else {
         return !this.isSettlerOnCurrentLevel() && this.getLevel().settlementLayer.isActive() ? null : EntityJobWorker.super.findJob();
      }
   }

   public ZoneTester getJobRestrictZone() {
      return this.levelSettler != null ? this.levelSettler.isTileInRestrictZoneTester() : (var0, var1) -> {
         return true;
      };
   }

   public JobTypeHandler getJobTypeHandler() {
      return this.jobTypeHandler;
   }

   public WorkInventory getWorkInventory() {
      return new WorkInventory() {
         public ListIterator<InventoryItem> listIterator() {
            return HumanMob.this.workInventory.listIterator();
         }

         public Iterable<InventoryItem> items() {
            return HumanMob.this.workInventory;
         }

         public Stream<InventoryItem> stream() {
            return HumanMob.this.workInventory.stream();
         }

         public void markDirty() {
            HumanMob.this.sendWorkUpdatePacket();
         }

         public void add(InventoryItem var1) {
            if (HumanMob.this.isSettler()) {
               var1.combineOrAddToList(HumanMob.this.getLevel(), (PlayerMob)null, HumanMob.this.workInventory, "add");
               this.markDirty();
            }
         }

         public int getCanAddAmount(InventoryItem var1) {
            if (this.getTotalItemStacks() > 4) {
               return 0;
            } else {
               float var2 = (Float)HumanMob.this.workInventory.stream().reduce(0.0F, (var0, var1x) -> {
                  return var0 + var1x.getBrokerValue();
               }, Float::sum);
               float var3 = 300.0F - var2;
               if (var3 < 0.0F) {
                  return 0;
               } else {
                  float var4 = var1.item.getBrokerValue(var1);
                  return Math.min((int)(var3 / var4), var1.getAmount());
               }
            }
         }

         public boolean isFull() {
            if (this.getTotalItemStacks() > 4) {
               return true;
            } else {
               float var1 = (Float)HumanMob.this.workInventory.stream().reduce(0.0F, (var0, var1x) -> {
                  return var0 + var1x.getBrokerValue();
               }, Float::sum);
               return var1 > 300.0F;
            }
         }

         public int getTotalItemStacks() {
            return HumanMob.this.workInventory.size();
         }

         public boolean isEmpty() {
            return HumanMob.this.workInventory.isEmpty();
         }
      };
   }

   public boolean hasActiveJob() {
      return this.ai.blackboard.getObject(ActiveJob.class, "currentJob") != null;
   }

   public boolean isInWorkAnimation() {
      return this.isAttacking;
   }

   public boolean isJobCancelled() {
      return this.cancelJob;
   }

   public void resetJobCancelled() {
      this.cancelJob = false;
   }

   public void cancelJob() {
      this.cancelJob = true;
   }

   public int getWorkBreakBuffer() {
      return this.workBreakBuffer;
   }

   public boolean isOnWorkBreak() {
      return this.onWorkBreak;
   }

   public void useWorkBreakBuffer(int var1) {
      float var2 = this.isSettler() ? GameMath.limit((float)this.getSettlerHappiness() / 100.0F, 0.0F, 1.0F) : 0.5F;
      float var3 = GameMath.lerp(var2, workBreakBufferUsageModAtNoHappiness, workBreakBufferUsageModAtFullHappiness);
      this.workBreakBuffer -= (int)((float)var1 * var3);
      if (this.workBreakBuffer <= 0 && !this.onWorkBreak) {
         this.onWorkBreak = true;
         this.attemptStartStrike(false, true);
         this.sendWorkUpdatePacket();
      }

   }

   public boolean isOnStrike() {
      return (Boolean)this.isOnStrike.get();
   }

   public void attemptStartStrike(boolean var1, boolean var2) {
      if (this.isSettlerOnCurrentLevel() && !this.isOnStrike()) {
         float var3 = GameMath.limit((float)this.getSettlerHappiness() / 100.0F, 0.0F, 1.0F);
         if (var1 || (double)var3 < 0.5) {
            float var4 = (float)(-Math.pow((double)(var3 * 2.0F), 0.75)) + 1.0F;
            if (var1 || GameRandom.globalRandom.getChance(var4)) {
               this.isOnStrike.set(true);
               this.hungerStrike = var1;
               this.endStrikeBuffer = 300000;
               this.cancelJob();
               if (var2) {
                  SettlementLevelData var5 = this.getSettlementLevelData();
                  if (var5 != null) {
                     Iterator var6 = var5.getSettlers().iterator();

                     while(var6.hasNext()) {
                        LevelSettler var7 = (LevelSettler)var6.next();
                        SettlerMob var8 = var7.getMob();
                        if (var8 instanceof EntityJobWorker) {
                           ((EntityJobWorker)var8).attemptStartStrike(false, false);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setActivity(String var1, int var2, GameMessage var3) {
      this.currentActivity.setActivity(var1, var2, var3);
   }

   public void clearActivity(String var1) {
      this.currentActivity.clearActivity(var1);
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.customShowAttack != null) {
         this.customShowAttack.run();
      }

   }

   public void showPickupAnimation(int var1, int var2, Item var3, int var4) {
      this.attackSprite(var1, var2, var3, var4, true);
   }

   public void showPlaceAnimation(int var1, int var2, Item var3, int var4) {
      this.attackSprite(var1, var2, var3, var4, false);
   }

   public void showWorkAnimation(int var1, int var2, Item var3, int var4) {
      long var5 = this.getWorldEntity().getLocalTime() - this.lastWorkAnimationRequest;
      if (var5 >= (long)(var4 / 2)) {
         Packet var7 = new Packet();
         PacketWriter var8 = new PacketWriter(var7);
         var8.putNextInt(var1);
         var8.putNextInt(var2);
         var8.putNextBoolean(var3 != null);
         if (var3 != null) {
            var8.putNextShortUnsigned(var3.getID());
         }

         var8.putNextInt(var4);
         this.itemWorkSpriteAbility.runAndSend(var7);
         this.lastWorkAnimationRequest = this.getWorldEntity().getLocalTime();
      }

   }

   public void showAttackAnimation(int var1, int var2, Item var3, int var4) {
      this.attackItem(var1, var2, var3, var4, var4);
   }

   public void giveBaitBack(BaitItem var1) {
   }

   public void stopFishing() {
      this.isAttacking = false;
   }

   public void showFishingWaitAnimation(FishingRodItem var1, int var2, int var3) {
      this.customAttackItem = FishingPoleHolding.setGNDData(new InventoryItem("fishinghold"), var1);
      this.attackAnimTime = 5000;
      this.showAttack(var2, var3, false);
   }

   public boolean isFishingSwingDone() {
      this.getAttackAnimProgress();
      return !this.isAttacking;
   }

   public void giveCaughtItem(FishingEvent var1, InventoryItem var2) {
      this.getWorkInventory().add(var2);
   }

   public LevelIdentifier getRecruitedToLevel(ServerClient var1) {
      return null;
   }

   public boolean isValidRecruitment(SettlementCache var1, ServerClient var2) {
      return true;
   }

   public void onRecruited(ServerClient var1, SettlementLevelData var2, LevelSettler var3) {
      if (var1 != null && var1.achievementsLoaded()) {
         var1.achievements().HEADHUNTER.markCompleted(var1);
      }

   }

   public boolean hasCommandOrders() {
      if (this.getLevel() != null && this.isClient()) {
         return (Boolean)this.clientHasCommandOrders.get();
      } else {
         return this.commandGuardPoint != null || this.commandFollowMob != null || this.commandAttackMob != null;
      }
   }

   public boolean canBeCommanded(Client var1) {
      if (this.isBusy()) {
         return false;
      } else if (var1.adventureParty.contains(this)) {
         return true;
      } else if (this.isSettlerOnCurrentLevel() && this.isOnStrike() && !this.getLevel().settlementLayer.isRaidActive() && !this.getLevel().settlementLayer.isRaidApproaching()) {
         return false;
      } else {
         int var2 = var1.getTeam();
         if (var2 == -1) {
            return (Long)this.owner.get() == GameAuth.getAuthentication();
         } else {
            return (Integer)this.team.get() == var2;
         }
      }
   }

   public boolean canBeCommanded(ServerClient var1) {
      if (this.isBusy()) {
         return false;
      } else if (var1.adventureParty.contains(this)) {
         return true;
      } else if (this.isSettlerOnCurrentLevel() && this.isOnStrike() && !this.getLevel().settlementLayer.isRaidActive() && !this.getLevel().settlementLayer.isRaidApproaching()) {
         return false;
      } else {
         Level var2 = this.getSettlementServerLevel();
         return var2 == null ? false : var2.settlementLayer.doesClientHaveAccess(var1);
      }
   }

   public void clearCommandsOrders(ServerClient var1) {
      boolean var2 = this.hasCommandOrders();
      this.commandFollowMob = null;
      this.commandGuardPoint = null;
      this.commandMoveToGuardPoint = false;
      this.commandMoveToFollowPoint = false;
      this.commandAttackMob = null;
      this.sendNextMovementPacket = true;
      this.adventureParty.clear(true);
      if (var2) {
         this.ai.blackboard.submitEvent("wanderNow", new AIEvent());
      }

   }

   public void commandFollow(ServerClient var1, Mob var2) {
      this.cancelJob();
      this.commandFollowMob = var2;
      this.commandGuardPoint = null;
      this.commandMoveToGuardPoint = false;
      this.commandMoveToFollowPoint = true;
      this.commandAttackMob = null;
      this.sendNextMovementPacket = true;
      this.ai.blackboard.submitEvent("resetTarget", new AIEvent());
      this.ai.blackboard.submitEvent("newCommandSet", new AIEvent());
      if (this.canJoinAdventureParties && var1 != null && var2 == var1.playerMob && this.willJoinAdventureParty(var1) == null && this.adventureParty.getServerClient() != var1) {
         this.adventureParty.set(var1);
      }

      this.resetCommandsBuffer = 0;
   }

   public void commandGuard(ServerClient var1, int var2, int var3) {
      this.cancelJob();
      this.commandFollowMob = null;
      this.commandGuardPoint = new Point(var2, var3);
      this.commandMoveToGuardPoint = true;
      this.commandMoveToFollowPoint = false;
      this.commandAttackMob = null;
      this.sendNextMovementPacket = true;
      if (var1 != null && !this.isSamePlace(var1.playerMob)) {
         this.getLevel().entityManager.changeMobLevel(this, var1.playerMob.getLevel(), var1.playerMob.getX(), var1.playerMob.getY(), true);
         this.ai.blackboard.mover.stopMoving(this);
      }

      if (this.isSettlerOnCurrentLevel()) {
         this.adventureParty.clear(true);
      } else if (this.canJoinAdventureParties && var1 != null && this.willJoinAdventureParty(var1) == null && this.adventureParty.getServerClient() != var1) {
         this.adventureParty.set(var1);
      }

      this.ai.blackboard.submitEvent("newCommandSet", new AIEvent());
      this.resetCommandsBuffer = 600000;
   }

   public void commandAttack(ServerClient var1, Mob var2) {
      if (var2 == null || var2.canBeTargeted(this, (NetworkClient)null)) {
         this.commandMoveToGuardPoint = false;
         this.commandMoveToFollowPoint = false;
         this.commandAttackMob = var2;
         this.ai.blackboard.submitEvent("newCommandSet", new AIEvent());
         this.resetCommandsBuffer = 600000;
      }

   }

   public int getBoomerangsUsage() {
      return (int)Math.ceil((double)(Float)this.boomerangs.stream().reduce(0.0F, (var0, var1) -> {
         return var0 + var1.getBoomerangUsage();
      }, Float::sum));
   }

   public void setHideOnLowHealth(boolean var1) {
      this.hideOnLowHealth.set(var1);
   }

   public boolean getHideOnLowHealth() {
      return (Boolean)this.hideOnLowHealth.get();
   }

   public Mob getMob() {
      return this;
   }

   public Predicate<Mob> filterHumanTargets() {
      return (var0) -> {
         return var0.isHostile;
      };
   }

   public void attackSprite(int var1, int var2, Item var3, int var4, boolean var5) {
      Packet var6 = new Packet();
      PacketWriter var7 = new PacketWriter(var6);
      var7.putNextInt(var1);
      var7.putNextInt(var2);
      var7.putNextBoolean(var3 != null);
      if (var3 != null) {
         var7.putNextShortUnsigned(var3.getID());
      }

      var7.putNextInt(var4);
      var7.putNextBoolean(var5);
      this.itemSwingSpriteAbility.runAndSend(var6);
   }

   public void attackSprite(int var1, int var2, String var3, int var4, boolean var5) {
      this.attackSprite(var1, var2, var3 == null ? null : ItemRegistry.getItem(var3), var4, var5);
   }

   public void attackItem(int var1, int var2, Item var3, int var4, int var5) {
      Packet var6 = new Packet();
      PacketWriter var7 = new PacketWriter(var6);
      var7.putNextInt(var1);
      var7.putNextInt(var2);
      var7.putNextShortUnsigned(var3.getID());
      var7.putNextInt(var4);
      var7.putNextInt(var5);
      this.itemAttackAbility.runAndSend(var6);
   }

   public void attackItem(int var1, int var2, InventoryItem var3) {
      this.attackItem(var1, var2, var3.item, var3.item.getAttackAnimTime(var3, this), Math.max(var3.item.getAttackCooldownTime(var3, this), var3.item.getItemCooldownTime(var3, this)));
   }

   public void attackItem(int var1, int var2, String var3, int var4, int var5) {
      this.attackItem(var1, var2, ItemRegistry.getItem(var3), var4, var5);
   }

   public float getAttackAnimProgress() {
      float var1 = super.getAttackAnimProgress();
      if (!this.isAttacking) {
         long var2 = this.getWorldEntity().getLocalTime() - this.lastWorkAnimationRequest;
         if (this.lastWorkAnimationRequest > 0L && var2 < (long)this.attackAnimTime) {
            this.attackTime = this.getWorldEntity().getTime();
            this.isAttacking = true;
         } else {
            this.attackAnimTime = this.defaultAttackAnimTime;
            this.customAttackItem = null;
            this.customShowAttack = null;
         }
      }

      return var1;
   }

   public void attack(int var1, int var2, boolean var3) {
      this.attackCooldown = this.defaultAttackCooldown;
      super.attack(var1, var2, var3);
   }

   protected HumanDrawOptions setCustomItemAttackOptions(HumanDrawOptions var1) {
      float var2 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         return this.customAttackItem != null ? var1.itemAttack(this.customAttackItem, (PlayerMob)null, var2, this.attackDir.x, this.attackDir.y) : var1.itemAttack(new InventoryItem("woodsword"), (PlayerMob)null, var2, this.attackDir.x, this.attackDir.y);
      } else {
         if (this.isOnStrike() && !this.getLevel().settlementLayer.isRaidActive() && !this.getLevel().settlementLayer.isRaidApproaching()) {
            var1.holdItem(new InventoryItem("strikebanner"));
         }

         return var1;
      }
   }

   public DrawOptions getMarkerDrawOptions(int var1, int var2, GameLight var3, GameCamera var4, int var5, int var6, PlayerMob var7) {
      QuestMarkerOptions var8 = this.getMarkerOptions(var7);
      return var8 != null ? var8.getDrawOptions(var1, var2, var3, var4, var5, var6) : () -> {
      };
   }

   public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
      return this.completedMission ? new QuestMarkerOptions('?', this.missionFailed ? QuestMarkerOptions.grayColor : QuestMarkerOptions.orangeColor) : null;
   }

   public boolean shouldDrawOnMap() {
      if (!this.isVisible()) {
         return false;
      } else {
         return this.isSettler() || this.isTravelingHuman();
      }
   }

   public boolean isVisibleOnMap(Client var1, LevelMap var2) {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      Settler var4 = this.getSettler();
      if (var4 != null) {
         this.getSettler().getSettlerIcon().initDraw().size(16, 16).draw(var2 - 8, var3 - 8);
      }

   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-8, -8, 16, 16);
   }

   public GameTooltips getMapTooltips() {
      StringTooltips var1 = new StringTooltips(this.getDisplayName());
      if (this.isSettler()) {
         GameMessage var2 = this.getCurrentActivity();
         if (var2 != null) {
            if (this.hasCommandOrders()) {
               var1.add(var2.translate(), (GameColor)GameColor.RED, 300);
            } else {
               var1.add(var2.translate(), (GameColor)GameColor.LIGHT_GRAY, 300);
            }
         }
      }

      return var1;
   }

   public boolean shouldSave() {
      return !this.isMovingOut() && this.shouldSave;
   }

   protected void addHoverTooltips(ListGameTooltips var1, boolean var2) {
      super.addHoverTooltips(var1, var2);
      if (this.isSettler()) {
         GameMessage var3 = this.getCurrentActivity();
         if (var3 != null) {
            if (this.hasCommandOrders()) {
               var1.add((Object)(new StringTooltips(var3.translate(), GameColor.RED, 300)));
            } else {
               var1.add((Object)(new StringTooltips(var3.translate(), GameColor.LIGHT_GRAY, 300)));
            }
         }
      }

   }

   protected void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("Home: " + (this.home == null ? null : this.home.x + ", " + this.home.y));
      if (this.isTravelingHuman) {
         var1.add("Leave: " + GameUtils.formatSeconds(this.travelOutTime / 1000L));
      }

      if (this.currentMission != null) {
         var1.add("currentMission: " + this.currentMission.getStringID());
         this.currentMission.addDebugTooltips(var1);
      }

      var1.add("completedMission: " + this.completedMission);
      var1.add("missionFailed: " + this.missionFailed);
      if (this.isSettler) {
         var1.add("tier: " + this.settlerQuestTier);
         var1.add("happiness: " + this.getSettlerHappiness() + "%");
         var1.add("hunger: " + (int)(this.getHungerLevel() * 100.0F) + "%");
         if (this.lastFoodEaten != null) {
            var1.add("lastFoodEaten: " + this.lastFoodEaten.getStringID() + " (" + this.lastFoodEaten.quality.displayName.translate() + ")");
         }
      }

      if (!this.isClient()) {
         var1.add("workBuffer: " + this.workBreakBuffer);
      }

      var1.add("onWorkBreak: " + this.onWorkBreak);
      if (!this.isClient()) {
         int var2 = (int)this.recentFoodItemIDsEaten.stream().distinct().count();
         var1.add("recentFood: (" + var2 + ") " + Arrays.toString(this.recentFoodItemIDsEaten.stream().map(ItemRegistry::getItemStringID).toArray()));
      }

   }

   public GameMessage getRandomAngryMessage() {
      ArrayList var1 = this.getAngryMessages();
      return (GameMessage)GameRandom.globalRandom.getOneOf((List)var1);
   }

   protected ArrayList<GameMessage> getAngryMessages() {
      ArrayList var1 = this.getLocalMessages("humanangry", 6);
      if (this.defendsHimselfAgainstPlayers()) {
         var1.addAll(this.getLocalMessages("humanangryactive", 3));
      }

      return var1;
   }

   protected boolean defendsHimselfAgainstPlayers() {
      return true;
   }

   public GameMessage getRandomAttackMessage() {
      ArrayList var1 = this.getAttackMessages();
      return (GameMessage)GameRandom.globalRandom.getOneOf((List)var1);
   }

   protected ArrayList<GameMessage> getAttackMessages() {
      return !this.defendsHimselfAgainstPlayers() ? this.getAngryMessages() : this.getLocalMessages("humanattack", 6);
   }

   public GameMessage getRandomMessage(GameRandom var1, ServerClient var2) {
      ArrayList var3 = this.getMessages(var2);
      return (GameMessage)var1.getOneOf((List)var3);
   }

   protected ArrayList<GameMessage> getMessages(ServerClient var1) {
      return this.getLocalMessages("humantalk", 5);
   }

   protected final ArrayList<GameMessage> getLocalMessages(String var1, int var2) {
      return this.getLocalMessages("mobmsg", var1, var2);
   }

   protected final ArrayList<GameMessage> getLocalMessages(String var1, String var2, int var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(new LocalMessage(var1, var2 + (var5 + 1)));
      }

      return var4;
   }

   protected HumanMob getRandomHuman(String var1) {
      return this.getRandomHuman(var1, 1600);
   }

   protected HumanMob getRandomHuman(String var1, int var2) {
      Predicate var3;
      if (this.isSettler()) {
         if (!this.isSettlerOnCurrentLevel()) {
            return null;
         }

         var3 = (var0) -> {
            return var0.isSettler();
         };
      } else {
         var3 = (var0) -> {
            return !var0.isSettler();
         };
      }

      HumanMob[] var4 = (HumanMob[])this.getLevel().entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(this.getX(), this.getY(), var2), 0).filter((var1x) -> {
         return var1x.getStringID().contains(var1) && var1x instanceof HumanMob;
      }).map((var0) -> {
         return (HumanMob)var0;
      }).filter(var3).toArray((var0) -> {
         return new HumanMob[var0];
      });
      return (HumanMob)GameRandom.globalRandom.getOneOf((Object[])var4);
   }

   public HumanGender convertHairGender(GameHair.HairGender var1) {
      if (var1 == GameHair.HairGender.NEUTRAL) {
         return HumanMob.HumanGender.NEUTRAL;
      } else if (var1 == GameHair.HairGender.MALE) {
         return HumanMob.HumanGender.MALE;
      } else {
         return var1 == GameHair.HairGender.FEMALE ? HumanMob.HumanGender.FEMALE : HumanMob.HumanGender.NEUTRAL;
      }
   }

   public HumanGender getHumanGender() {
      return HumanMob.HumanGender.NEUTRAL;
   }

   protected String getRandomName(GameRandom var1) {
      HumanGender var2 = this.getHumanGender();
      if (var2 == HumanMob.HumanGender.NEUTRAL) {
         return getRandomName(var1, neutralNames);
      } else if (var2 == HumanMob.HumanGender.MALE) {
         return getRandomName(var1, maleNames);
      } else {
         return var2 == HumanMob.HumanGender.FEMALE ? getRandomName(var1, femaleNames) : "NULL";
      }
   }

   public static String getRandomName(GameRandom var0, String[] var1) {
      return var1[var0.nextInt(var1.length)];
   }

   static {
      defaultJobPathIterations = humanPathIterations;
      secondsToPassAtFullHunger = 1200;
      adventurePartyHungerUsageMod = 2.0F;
      maxWorkBreakBufferAtFullHappiness = 180000;
      maxWorkBreakBufferAtNoHappiness = 60000;
      resetWorkBreakWhenBufferAt = 60000;
      workBreakBufferUsageModAtFullHappiness = 0.5F;
      workBreakBufferUsageModAtNoHappiness = 3.0F;
      workBreakBufferRegenModAtFullHappiness = 1.0F;
      workBreakBufferRegenModAtNoHappiness = 0.2F;
      elderNames = new String[]{"Diligo", "Haydex", "Kamikaze", "Subject", "Vireyar", "Fair"};
      maleNames = new String[]{"Aaron", "Adam", "Alastair", "Albert", "Alexander", "Alfred", "Alton", "Andrew", "Archie", "Arthur", "Basil", "Ben", "Benny", "Billy", "Blake", "Brandon", "Brayden", "Brent", "Bruce", "Cain", "Calvin", "Carl", "Chad", "Charles", "Chris", "Cody", "Dale", "Darren", "Dave", "David", "Dean", "Dennis", "Devon", "Dexter", "Dustin", "Elvis", "Eric", "Francis", "Garrett", "Gavin", "Glen", "Harrison", "Henry", "Issac", "Jake", "James", "Jared", "Jason", "Jayden", "Jeff", "Jordan", "Josh", "Justin", "Kaine", "Karl", "Ken", "Kent", "King", "Kyle", "Larry", "Lloyd", "Marc", "Marco", "Mason", "Matthew", "Michael", "Mick", "Nick", "Oliver", "Olly", "Patrick", "Paul", "Peter", "Phil", "Philip", "Ricardo", "Ricky", "Rob", "Roger", "Samuel", "Scott", "Sean", "Seth", "Shane", "Shawn", "Sheldon", "Stan", "Steve", "Tim", "Tobias", "Tom", "Tony", "Troy", "Warren", "Will", "Zach"};
      femaleNames = new String[]{"Abigail", "Abitha", "Alexis", "Alicia", "Amity", "Angel", "Anne", "Aphra", "Aurinda", "Azuba", "Betsy", "Bonny", "Camille", "Candace", "Carmen", "Carolyn", "Christine", "Cess", "Charity", "Charlotte", "Christine", "Claire", "Clara", "Connie", "Cornelia", "Cristi", "Denise", "Diana", "Diane", "Edith", "Eleanor", "Electra", "Elizabeth", "Emeline", "Emma", "Esther", "Faith", "Fidelity", "Frances", "Francine", "Georgine", "Gloria", "Grace", "Harriet", "Hazel", "Helen", "Henrietta", "Hepzibah", "Hester", "Isabella", "Jane", "Jess", "Joy", "Joyce", "Judith", "Kathrine", "Kaz", "Kristine", "Laura", "Leona", "Lisha", "Lucy", "Lydia", "Maggie", "Martha", "Martina", "Mary", "Megan", "Melissa", "Mellie", "Mercy", "Natalie", "Ness", "Nina", "Penny", "Petra", "Phila", "Phoebe", "Rebekah", "Rosanna", "Sara", "Shirley", "Sophia", "Sue", "Susanna", "Tabitha", "Tamara", "Tess", "Tiara", "Violet", "Virginia", "Winnie"};
      neutralNames = new String[]{"Alex", "Ash", "Bobby", "Casey", "Darcy", "Dylan", "Frankie", "George", "Glenn", "Jesse", "Leigh", "Lindsay", "Robin", "Ryan", "Sam"};
   }

   public static enum HumanGender {
      NEUTRAL,
      MALE,
      FEMALE;

      private HumanGender() {
      }

      // $FF: synthetic method
      private static HumanGender[] $values() {
         return new HumanGender[]{NEUTRAL, MALE, FEMALE};
      }
   }
}
