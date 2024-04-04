// Source code is decompiled from a .class file using FernFlower decompiler.
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.Screen.CURSOR;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.control.Control;
import necesse.engine.events.loot.MobLootTableDropsEvent;
import necesse.engine.events.loot.MobPrivateLootTableDropsEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketDeath;
import necesse.engine.network.packet.PacketHitMob;
import necesse.engine.network.packet.PacketMobFollowUpdate;
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.packet.PacketMobMana;
import necesse.engine.network.packet.PacketMobMovement;
import necesse.engine.network.packet.PacketMobPathBreakDownHit;
import necesse.engine.network.packet.PacketMobResilience;
import necesse.engine.network.packet.PacketMobUseLife;
import necesse.engine.network.packet.PacketMobUseMana;
import necesse.engine.network.packet.PacketPlayerCollisionHit;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.network.packet.PacketTileDamage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.BuffRegistry.Debuffs;
import necesse.engine.registries.MobRegistry.Textures;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.TileDamageType;
import necesse.entity.manager.MobHealthChangeListenerEntityComponent;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ability.MobAbilityRegistry;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.EmptyAINode;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.networkField.MobNetworkFieldRegistry;
import necesse.entity.mobs.networkField.NetworkField;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.ProgressBarDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.floatText.DamageText;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.PathBreakDownLevelData;
import necesse.level.maps.light.GameLight;

public class Mob extends Entity implements Attacker {
   public static MobSpawnArea MOB_SPAWN_AREA = new MobSpawnArea(700, 1400);
   public ModifierValue<Integer> spawnLightThreshold;
   public static MobSpawnArea CRITTER_SPAWN_AREA = new MobSpawnArea(800, 1400);
   public static Attacker TOO_BUFFED_ATTACKER = new 1();
   public final IDData idData;
   public final MobDifficultyChanges difficultyChanges;
   public BehaviourTreeAI<?> ai;
   public boolean countStats;
   public int dir;
   private int armor;
   private int health;
   private int maxHealth;
   protected int loadedHealth;
   private float mana;
   protected float loadedMana;
   private int maxMana;
   private float resilience;
   protected float loadedResilience;
   private int maxResilience;
   protected float resilienceDecay;
   protected float resilienceDecayDelay;
   public boolean isManaExhausted;
   private float friction;
   private float speed;
   private float swimSpeed;
   private float regen;
   private float combatRegen;
   private float manaRegen;
   private float combatManaRegen;
   public float accelerationMod;
   public float decelerationMod;
   private float knockbackModifier;
   private double regenBuffer;
   public float dx;
   public float dy;
   public float colDx;
   public float colDy;
   protected float nX;
   protected float nY;
   protected boolean isSmoothSnapped;
   public boolean staySmoothSnapped;
   protected int hitCooldown;
   protected int attackCooldown;
   public long hitTime;
   public long attackTime;
   public long lastCombatTime;
   public long combatStartTime;
   public long lastManaSpentTime;
   public long removedTime;
   public boolean isAttacking;
   public boolean onCooldown;
   public boolean isHostile;
   public boolean isCritter;
   protected int cornerSkip;
   protected boolean isStatic;
   protected Rectangle collision;
   protected Rectangle hitBox;
   protected Rectangle selectBox;
   public boolean prioritizeVerticalDir;
   protected MobMovement currentMovement;
   protected boolean hasArrivedAtTarget;
   protected boolean stopMoveWhenArrive;
   public int moveAccuracy;
   public float moveX;
   public float moveY;
   protected float movePosTolerance;
   protected boolean sendNextMovementPacket;
   protected boolean nextMovementPacketDirect;
   protected boolean sendNextHealthPacket;
   protected boolean nextHealthPacketFull;
   protected boolean sendNextResiliencePacket;
   protected boolean nextResiliencePacketFull;
   protected boolean sendNextManaPacket;
   protected boolean nextManaPacketFull;
   protected int movementUpdateCooldown;
   protected int healthUpdateCooldown;
   protected int resilienceUpdateCooldown;
   protected int manaUpdateCooldown;
   protected long movementUpdateTime;
   protected long healthUpdateTime;
   protected long resilienceUpdateTime;
   protected long manaUpdateTime;
   protected long resilienceGainedTime;
   public int mount;
   public int rider;
   public boolean mountSetMounterPos;
   protected boolean overrideMountedWaterWalking;
   public String secondType;
   private int team;
   public BuffManager buffManager;
   private HashMap<String, Long> genericCooldowns;
   private HashSet<Attacker> attackers;
   public LinkedList<Runnable> runOnNextServerTick;
   public LinkedList<Runnable> runOnNextClientTick;
   public ArrayList<ItemPickupEntity> itemsDropped;
   public boolean canDespawn;
   public boolean shouldSave;
   public boolean isSummoned;
   public boolean dropsLoot;
   public final boolean isPlayer;
   public final boolean isHuman;
   protected boolean hasDied;
   protected int followingSlot;
   protected float currentSpeed;
   protected double distanceRan;
   protected double distanceRidden;
   protected int moveSent;
   protected int healthSent;
   protected int resilienceSent;
   protected int manaSent;
   protected OpenedDoors openedDoors;
   protected long lastPathBreakTime;
   public int pathBreakCooldown;
   public MobHitCooldowns collisionHitCooldowns;
   private MobNetworkFieldRegistry networkFields;
   private MobAbilityRegistry abilities;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public Mob(int health) {
      this.spawnLightThreshold = new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0);
      this.idData = new IDData();
      this.difficultyChanges = new MobDifficultyChanges(this);
      this.ai = new BehaviourTreeAI(this, new EmptyAINode());
      this.countStats = false;
      this.combatStartTime = -1L;
      this.cornerSkip = 12;
      this.prioritizeVerticalDir = true;
      this.hasArrivedAtTarget = true;
      this.stopMoveWhenArrive = false;
      this.moveAccuracy = 5;
      this.movePosTolerance = 0.0F;
      this.movementUpdateCooldown = 5000;
      this.healthUpdateCooldown = 5000;
      this.resilienceUpdateCooldown = 5000;
      this.manaUpdateCooldown = 5000;
      this.mount = -1;
      this.rider = -1;
      this.team = -1;
      this.runOnNextServerTick = new LinkedList();
      this.runOnNextClientTick = new LinkedList();
      this.itemsDropped = new ArrayList();
      this.shouldSave = true;
      this.isSummoned = false;
      this.dropsLoot = true;
      this.followingSlot = -1;
      this.openedDoors = new OpenedDoors(this);
      this.pathBreakCooldown = 1000;
      this.collisionHitCooldowns = new MobHitCooldowns();
      this.networkFields = new MobNetworkFieldRegistry(this);
      this.abilities = new MobAbilityRegistry(this);
      if (this instanceof PlayerMob) {
         this.isPlayer = true;
         this.idData.setData(-1, "player");
      } else {
         this.isPlayer = false;
         MobRegistry.instance.applyIDData(this.getClass(), this.idData);
      }

      this.isHuman = this instanceof HumanMob;
      this.buffManager = new BuffManager(this);
      this.attackers = new HashSet();
      this.genericCooldowns = new HashMap();
      this.secondType = this.getStringID();
      this.setMaxHealth(health);
      this.setHealthHidden(health);
      this.setMaxResilience(0);
      this.setMaxMana(100);
      this.setManaHidden(100.0F);
      this.collision = new Rectangle();
      this.hitBox = new Rectangle();
      this.selectBox = new Rectangle();
      this.hitCooldown = 0;
      this.attackCooldown = 1000;
      this.setRegen(1.0F);
      this.setResilienceDecay(1.0F);
      this.resilienceDecayDelay = 4000.0F;
      this.setManaRegen(0.0F);
      this.setCombatRegen(0.0F);
      this.setCombatManaRegen(0.4F);
      this.accelerationMod = 0.5F;
      this.decelerationMod = 1.0F;
      this.setKnockbackModifier(1.0F);
      this.setPos(0.0F, 0.0F, true);
      this.dir = GameRandom.globalRandom.nextInt(4);
      this.setFriction(0.3F);
      this.setSwimSpeed(0.5F);
   }

   public void onConstructed(Level level) {
      if (level != null) {
         this.setLevel(level);
      }

      this.setHealthHidden(this.getMaxHealth());
   }

   public void addSaveData(SaveData save) {
      save.addInt("uniqueID", this.getUniqueID());
      save.addInt("dir", this.dir);
      save.addFloat("x", this.x);
      save.addFloat("y", this.y);
      save.addFloat("dx", this.dx);
      save.addFloat("dy", this.dy);
      save.addInt("maxhealth", this.maxHealth);
      save.addInt("health", this.getHealth());
      save.addInt("maxresilience", this.maxResilience);
      save.addFloat("resilience", this.resilience);
      save.addInt("maxmana", this.maxMana);
      save.addFloat("mana", this.getMana());
      save.addBoolean("canDespawn", this.canDespawn);
      save.addLong("lastCombatTime", this.lastCombatTime);
      if (this.usesMana()) {
         save.addLong("lastManaSpentTime", this.lastManaSpentTime);
         save.addBoolean("isManaExhausted", this.isManaExhausted);
      }

      SaveData openedDoorsData;
      if (!this.openedDoors.isEmpty()) {
         openedDoorsData = new SaveData("openedDoors");
         Iterator var3 = this.openedDoors.iterator();

         while(var3.hasNext()) {
            OpenedDoor door = (OpenedDoor)var3.next();
            openedDoorsData.addPoint("", new Point(door.tileX, door.tileY));
         }

         save.addSaveData(openedDoorsData);
      }

      openedDoorsData = new SaveData("BUFFS");
      this.buffManager.addSaveData(openedDoorsData);
      save.addSaveData(openedDoorsData);
   }

   public void applyLoadData(LoadData save) {
      this.setUniqueID(save.getInt("uniqueID", this.getRealUniqueID()));
      this.dir = save.getInt("dir");
      this.x = save.getFloat("x");
      this.y = save.getFloat("y");
      this.nX = this.x;
      this.nY = this.y;
      this.isSmoothSnapped = true;
      this.dx = save.getFloat("dx");
      this.dy = save.getFloat("dy");
      this.setMaxHealth(save.getInt("maxhealth", this.getMaxHealthFlat()));
      this.loadedHealth = save.getInt("health", this.getMaxHealthFlat());
      this.setHealthHidden(this.loadedHealth);
      this.setMaxResilience(save.getInt("maxresilience", this.getMaxResilienceFlat(), false));
      this.loadedResilience = save.getFloat("resilience", 0.0F, false);
      this.setResilienceHidden(this.loadedResilience);
      if (this.usesMana()) {
         this.setMaxMana(save.getInt("maxmana", this.getMaxManaFlat()));
         this.loadedMana = save.getFloat("mana", (float)this.getMaxManaFlat());
         this.setManaHidden(this.loadedMana);
      }

      this.canDespawn = Boolean.parseBoolean(save.getFirstDataByName("canDespawn"));
      this.lastCombatTime = save.getLong("lastCombatTime", this.lastCombatTime, false);
      this.lastManaSpentTime = save.getLong("lastManaSpentTime", this.lastManaSpentTime, false);
      this.isManaExhausted = save.getBoolean("isManaExhausted", this.isManaExhausted, false);
      LoadData openedDoorsData = save.getFirstLoadDataByName("openedDoors");
      if (openedDoorsData != null) {
         Iterator var3 = openedDoorsData.getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData openedDoorData = (LoadData)var3.next();
            if (openedDoorData.isData()) {
               try {
                  Point point = LoadData.getPoint(openedDoorData);
                  this.openedDoors.add(point.x, point.y, this.getX(), this.getY(), !this.getLevel().getObject(point.x, point.y).isSwitched);
               } catch (Exception var6) {
                  GameLog.warn.println("Could not load mob opened door: " + openedDoorData.getData());
               }
            }
         }
      }

      LoadData buffs = save.getFirstLoadDataByName("BUFFS");
      if (buffs != null) {
         this.buffManager.applyLoadData(buffs);
      }

   }

   public boolean shouldSendSpawnPacket() {
      return true;
   }

   public Mob getSpawnPacketMaster() {
      return null;
   }

   public void setupSpawnPacket(PacketWriter writer) {
      writer.putNextInt(this.getUniqueID());
      this.setupHealthPacket(writer, true);
      this.sendNextHealthPacket = false;
      this.nextHealthPacketFull = false;
      this.setupResiliencePacket(writer, true);
      this.sendNextResiliencePacket = false;
      this.nextResiliencePacketFull = false;
      this.setupMovementPacket(writer);
      this.sendNextMovementPacket = false;
      this.nextMovementPacketDirect = false;
      if (this.usesMana()) {
         writer.putNextBoolean(true);
         this.setupManaPacket(writer, true);
      } else {
         writer.putNextBoolean(false);
      }

      this.sendNextManaPacket = false;
      this.nextManaPacketFull = false;
      writer.putNextByte((byte)this.followingSlot);
      writer.putNextBoolean(this.mountSetMounterPos);
      writer.putNextLong(this.lastCombatTime);
      if (this.usesMana()) {
         writer.putNextBoolean(true);
         writer.putNextLong(this.lastManaSpentTime);
         writer.putNextBoolean(this.isManaExhausted);
      } else {
         writer.putNextBoolean(false);
      }

      this.networkFields.writeSpawnPacket(writer);
      this.buffManager.setupContentPacket(writer);
   }

   public void applySpawnPacket(PacketReader reader) {
      this.refreshClientUpdateTime();
      this.setUniqueID(reader.getNextInt());
      this.applyHealthPacket(reader, true);
      if (this.getHealth() != this.loadedHealth) {
         this.setHealthHidden(this.loadedHealth);
      }

      this.applyResiliencePacket(reader, true);
      if (this.getResilience() != this.loadedResilience) {
         this.setResilienceHidden(this.loadedResilience);
      }

      this.applyMovementPacket(reader, true);
      if (reader.getNextBoolean()) {
         this.applyManaPacket(reader, true);
         if (this.getMana() != this.loadedMana) {
            this.setManaHidden(this.loadedMana);
         }
      }

      byte fSlot = reader.getNextByte();
      this.followingSlot = fSlot == -1 ? -1 : fSlot & 255;
      this.mountSetMounterPos = reader.getNextBoolean();
      this.lastCombatTime = reader.getNextLong();
      if (reader.getNextBoolean()) {
         this.lastManaSpentTime = reader.getNextLong();
         this.isManaExhausted = reader.getNextBoolean();
      }

      this.networkFields.readSpawnPacket(reader);
      this.buffManager.applyContentPacket(reader);
   }

   public void setupMovementPacket(PacketWriter writer) {
      writer.putNextMaxValue(this.dir, 3);
      writer.putNextFloat(this.x);
      writer.putNextFloat(this.y);
      writer.putNextFloat(this.dx);
      writer.putNextFloat(this.dy);
      writer.putNextBoolean(this.rider != -1);
      if (this.rider != -1) {
         writer.putNextInt(this.rider);
      }

      writer.putNextBoolean(this.mount != -1);
      if (this.mount != -1) {
         writer.putNextInt(this.mount);
      }

      writer.putNextBoolean(this.stopMoveWhenArrive);
      if (this.currentMovement != null) {
         writer.putNextShort((short)this.currentMovement.getID());
         this.currentMovement.setupPacket(this, writer);
      } else {
         writer.putNextShort((short)-1);
      }

   }

   public void applyMovementPacket(PacketReader reader, boolean isDirect) {
      this.refreshClientUpdateTime();
      this.dir = reader.getNextMaxValue(3);
      float x = reader.getNextFloat();
      float y = reader.getNextFloat();
      float dx = reader.getNextFloat();
      float dy = reader.getNextFloat();
      boolean hasRider = reader.getNextBoolean();
      int rider = hasRider ? reader.getNextInt() : -1;
      if (this.rider != rider) {
         this.rider = rider;
         this.updateRider();
      }

      boolean hasMount = reader.getNextBoolean();
      int mount = hasMount ? reader.getNextInt() : -1;
      if (this.mount != mount) {
         this.mount = mount;
         this.updateMount();
      }

      this.updatePosFromServer(x, y, dx, dy, isDirect);
      this.stopMoveWhenArrive = reader.getNextBoolean();
      int movementID = reader.getNextShort();
      if (movementID != -1) {
         this.currentMovement = (MobMovement)MobMovement.registry.getNewInstance(movementID);
         this.currentMovement.applyPacket(this, reader);
      } else {
         this.currentMovement = null;
      }

   }

   public void setupHealthPacket(PacketWriter writer, boolean isFull) {
      if (isFull) {
         writer.putNextInt(this.maxHealth);
      }

      writer.putNextInt(this.getHealth());
   }

   public void applyHealthPacket(PacketReader reader, boolean isFull) {
      if (isFull) {
         this.setMaxHealth(reader.getNextInt());
      }

      this.loadedHealth = reader.getNextInt();
      int delta = Math.abs(this.loadedHealth - this.getHealth());
      if (this.loadedHealth == 0 || delta > 1) {
         this.setHealthHidden(this.loadedHealth, 0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void setupResiliencePacket(PacketWriter writer, boolean isFull) {
      if (isFull) {
         writer.putNextInt(this.maxResilience);
      }

      writer.putNextFloat(this.getResilience());
      writer.putNextLong(this.resilienceGainedTime);
   }

   public void applyResiliencePacket(PacketReader reader, boolean isFull) {
      if (isFull) {
         this.setMaxResilience(reader.getNextInt());
      }

      this.loadedResilience = reader.getNextFloat();
      float delta = Math.abs(this.loadedResilience - this.getResilience());
      if (this.loadedResilience == 0.0F || this.loadedResilience >= (float)this.getMaxResilience() || delta >= 1.0F) {
         this.setResilienceHidden(this.loadedResilience);
      }

      this.resilienceGainedTime = reader.getNextLong();
   }

   public void setupManaPacket(PacketWriter writer, boolean isFull) {
      if (isFull) {
         writer.putNextInt(this.maxMana);
      }

      writer.putNextFloat(this.getMana());
      writer.putNextLong(this.lastManaSpentTime);
      writer.putNextBoolean(this.isManaExhausted);
   }

   public void applyManaPacket(PacketReader reader, boolean isFull) {
      if (isFull) {
         this.setMaxMana(reader.getNextInt());
      }

      this.loadedMana = reader.getNextFloat();
      float delta = Math.abs(this.loadedMana - this.getMana());
      if (this.loadedMana == 0.0F || delta >= 1.0F) {
         this.setManaHidden(this.loadedMana);
      }

      this.lastManaSpentTime = reader.getNextLong();
      this.isManaExhausted = reader.getNextBoolean();
   }

   protected void moveX(float mod) {
      this.x += (this.dx + this.colDx) * mod / 250.0F;
   }

   protected void moveY(float mod) {
      this.y += (this.dy + this.colDy) * mod / 250.0F;
   }

   public void init() {
      super.init();
      WorldEntity worldEntity = this.getWorldEntity();
      if (worldEntity != null) {
         this.healthUpdateTime = worldEntity.getTime();
         this.movementUpdateTime = worldEntity.getTime();
         if (this.usesMana()) {
            this.manaUpdateTime = worldEntity.getTime();
         }
      }

      this.buffManager.forceUpdateBuffs();
      this.difficultyChanges.init();
      this.networkFields.closeRegistry();
      this.abilities.closeRegistry();
      this.countStats = true;
      this.updateMount();
      this.updateRider();
      this.refreshClientUpdateTime();
   }

   public void onLoadingComplete() {
      super.onLoadingComplete();
      this.handleLoadedValues();
   }

   protected void handleLoadedValues() {
      this.buffManager.forceUpdateBuffs();
      if (this.getHealth() < this.loadedHealth) {
         this.setHealthHidden(this.loadedHealth);
         this.loadedHealth = 0;
      }

      if (this.getResilience() < this.loadedResilience) {
         this.setResilienceHidden(this.loadedResilience);
         this.loadedResilience = 0.0F;
      }

      if (this.getMana() < this.loadedMana) {
         this.setManaHidden(this.loadedMana);
         this.loadedMana = 0.0F;
      }

   }

   public void onUnloading() {
      super.onUnloading();
      this.ai.onUnloading();
   }

   public void clientTick() {
      while(!this.runOnNextClientTick.isEmpty()) {
         ((Runnable)this.runOnNextClientTick.removeFirst()).run();
      }

      this.difficultyChanges.tick();
      if (!this.isPlayer && this.getTimeSinceClientUpdate() >= (long)(this.movementUpdateCooldown + 10000)) {
         this.refreshClientUpdateTime();
         if (this.getRider() == null) {
            this.requestServerUpdate();
         }
      }

      this.buffManager.clientTick();
      this.tickRegen();
      this.tickLevel();
      if ((Boolean)this.buffManager.getModifier(BuffModifiers.EMITS_LIGHT)) {
         this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y);
      }

      if (this.usesMana() && this.isManaExhausted) {
         this.buffManager.addBuff(new ActiveBuff(Debuffs.MANA_EXHAUSTION, this, 1000, (Attacker)null), false);
      }

   }

   public void requestServerUpdate() {
      if (this.isClient()) {
         GameLog.debug.println("Client requesting update for mob " + this);
         this.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.getUniqueID()));
      }

   }

   public void serverTick() {
      while(!this.runOnNextServerTick.isEmpty()) {
         ((Runnable)this.runOnNextServerTick.removeFirst()).run();
      }

      this.difficultyChanges.tick();
      if (this.isServer()) {
         this.networkFields.tickSync();
      }

      this.buffManager.serverTick();
      this.tickOpenedDoors();
      this.tickRegen();
      this.tickLevel();
      if (Float.isNaN(this.x) || Float.isNaN(this.y)) {
         System.out.println("Mob " + this.getRealUniqueID() + " bugged out and was removed.");
         this.remove();
      }

      if (this.isFollowing()) {
         PlayerMob fPlayer = this.getFollowingServerPlayer();
         if (fPlayer == null) {
            this.followingSlot = -1;
         } else if (!fPlayer.isSamePlace(this)) {
            this.onFollowingAnotherLevel(fPlayer);
         }
      }

      Performance.record(this.getLevel().tickManager(), "ai", () -> {
         if (!this.isMounted()) {
            this.ai.tick();
         }

      });
      if (this.movementUpdateTime + (long)this.movementUpdateCooldown < this.getTime()) {
         this.sendMovementPacket(false);
      }

      if (this.canTakeDamage() && this.healthUpdateTime + (long)this.healthUpdateCooldown < this.getTime()) {
         this.sendHealthPacket(false);
      }

      if (this.usesMana()) {
         if (this.manaUpdateTime + (long)this.manaUpdateCooldown < this.getTime()) {
            this.sendManaPacket(false);
         }

         if (this.isManaExhausted) {
            this.buffManager.addBuff(new ActiveBuff(Debuffs.MANA_EXHAUSTION, this, 1000, (Attacker)null), false);
         }
      }

   }

   public void tickCurrentMovement(float delta) {
      this.moveX = 0.0F;
      this.moveY = 0.0F;
      if (this.isMounted()) {
         Mob mounted = this.getRider();
         if (mounted != null) {
            this.dir = mounted.dir;
            this.moveX = mounted.moveX;
            this.moveY = mounted.moveY;
         }
      } else if (this.currentMovement != null) {
         this.hasArrivedAtTarget = this.currentMovement.tick(this);
         if (this.stopMoveWhenArrive && this.hasArrivedAtTarget) {
            this.stopMoving();
         }
      } else {
         this.hasArrivedAtTarget = true;
      }

   }

   public void tickMovement(float delta) {
      if (!this.removed()) {
         Point2D.Float oldPos = new Point2D.Float(this.x, this.y);
         Mob mount;
         if (this.isRiding()) {
            mount = this.getMount();
            if (mount != null) {
               this.setPos(mount.x, mount.y, true);
               if (!this.isAttacking) {
                  this.dir = mount.dir;
               }

               this.dx = 0.0F;
               this.dy = 0.0F;
               this.buffManager.tickMovement(delta);
               this.tickSendSyncPackets();
               return;
            }
         }

         this.tickCurrentMovement(delta);
         this.calcAcceleration(this.getSpeed(), this.getFriction(), this.moveX, this.moveY, delta);
         mount = this.getRider();
         this.colDx = 0.0F;
         this.colDy = 0.0F;
         this.checkCollision();
         this.tickCollisionMovement(delta, mount);
         this.colDx = 0.0F;
         this.colDy = 0.0F;
         Point2D.Float newPos = new Point2D.Float(this.x, this.y);
         float distance = (float)oldPos.distance(newPos);
         this.currentSpeed = distance / (delta / 250.0F);
         this.distanceRan += (double)distance;
         if (mount != null) {
            mount.currentSpeed = distance;
            mount.distanceRidden += (double)distance;
         }

         this.calcNetworkSmooth(delta);
         this.buffManager.tickMovement(delta);
         this.tickSendSyncPackets();
      }
   }

   public void tickSendSyncPackets() {
      if (this.isServer()) {
         if (this.sendNextMovementPacket) {
            if (this.getRider() == null) {
               ++this.moveSent;
               this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobMovement(this, this.nextMovementPacketDirect), this);
               this.nextMovementPacketDirect = false;
            }

            this.movementUpdateTime = this.getTime();
            this.sendNextMovementPacket = false;
         }

         if (this.sendNextHealthPacket) {
            if (this.canTakeDamage()) {
               ++this.healthSent;
               this.getServer().network.sendToClientsWithEntity(new PacketMobHealth(this, this.nextHealthPacketFull), this);
               this.nextHealthPacketFull = false;
            }

            this.healthUpdateTime = this.getTime();
            this.sendNextHealthPacket = false;
         }

         if (this.sendNextResiliencePacket) {
            if (this.getMaxResilience() > 0) {
               ++this.resilienceSent;
               this.getServer().network.sendToClientsWithEntity(new PacketMobResilience(this, this.nextResiliencePacketFull), this);
               this.nextResiliencePacketFull = false;
            }

            this.resilienceUpdateTime = this.getTime();
            this.sendNextResiliencePacket = false;
         }

         if (this.sendNextManaPacket) {
            if (this.usesMana()) {
               ++this.manaSent;
               this.getServer().network.sendToClientsWithEntity(new PacketMobMana(this, this.nextManaPacketFull), this);
               this.nextManaPacketFull = false;
            }

            this.manaUpdateTime = this.getTime();
            this.sendNextManaPacket = false;
         }
      }

   }

   protected void tickCollisionMovement(float delta, Mob rider) {
      Mob doorOpener = rider == null ? this : rider;
      CollisionFilter collisionFilter = this.getLevelCollisionFilter();
      ArrayList<LevelObjectHit> currentCollisions = this.getLevel().getCollisions(this.getCollision(), collisionFilter);
      LinkedList<Runnable> finishedMovementActions = new LinkedList();
      Objects.requireNonNull(finishedMovementActions);
      Consumer<Runnable> onFinishedMovement = finishedMovementActions::add;
      if (currentCollisions.isEmpty()) {
         Rectangle newCol;
         ArrayList newCols;
         boolean collided;
         boolean stop;
         int i;
         Rectangle col;
         ArrayList cols;
         if (this.dx != 0.0F || this.colDx != 0.0F) {
            this.moveX(delta);
            newCol = this.getCollision();
            newCols = this.getLevel().getCollisions(newCol, collisionFilter);
            collided = true;
            stop = true;
            if (!this.checkAndHandleCollisions(newCols, newCol, doorOpener, true, true, onFinishedMovement)) {
               collided = false;
            }

            if (collided && this.cornerSkip > 0) {
               for(i = 2; i <= this.cornerSkip; i += 2) {
                  if (this.dy >= 0.0F) {
                     col = this.getCollision(this.getX(), this.getY() + i);
                     cols = this.getLevel().getCollisions(col, collisionFilter);
                     if (cols.isEmpty()) {
                        this.y += Math.abs(this.dx) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }

                     if (!this.checkAndHandleCollisions(cols, col, doorOpener, false, false, onFinishedMovement)) {
                        this.y += Math.abs(this.dx) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }
                  }

                  if (this.dy <= 0.0F) {
                     col = this.getCollision(this.getX(), this.getY() - i);
                     cols = this.getLevel().getCollisions(col, collisionFilter);
                     if (cols.isEmpty()) {
                        this.y -= Math.abs(this.dx) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }

                     if (!this.checkAndHandleCollisions(cols, col, doorOpener, false, false, onFinishedMovement)) {
                        this.y -= Math.abs(this.dx) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }
                  }
               }
            }

            if (collided) {
               this.moveX(-delta);
               if (stop) {
                  if ((Boolean)this.buffManager.getModifier(BuffModifiers.BOUNCY)) {
                     this.dx = -this.dx;
                  } else {
                     this.dx = 0.0F;
                  }
               }
            }
         }

         if (this.dy != 0.0F || this.colDy != 0.0F) {
            this.moveY(delta);
            newCol = this.getCollision();
            newCols = this.getLevel().getCollisions(newCol, collisionFilter);
            collided = true;
            stop = true;
            if (!this.checkAndHandleCollisions(newCols, newCol, doorOpener, true, false, onFinishedMovement)) {
               collided = false;
            }

            if (collided && this.cornerSkip > 0) {
               for(i = 2; i <= this.cornerSkip; i += 2) {
                  if (this.dx >= 0.0F) {
                     col = this.getCollision(this.getX() + i, this.getY());
                     cols = this.getLevel().getCollisions(col, collisionFilter);
                     if (cols.isEmpty()) {
                        this.x += Math.abs(this.dy) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }

                     if (!this.checkAndHandleCollisions(cols, col, doorOpener, false, true, onFinishedMovement)) {
                        this.x += Math.abs(this.dy) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }
                  }

                  if (this.dx <= 0.0F) {
                     col = this.getCollision(this.getX() - i, this.getY());
                     cols = this.getLevel().getCollisions(col, collisionFilter);
                     if (cols.isEmpty()) {
                        this.x -= Math.abs(this.dy) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }

                     if (!this.checkAndHandleCollisions(cols, col, doorOpener, false, true, onFinishedMovement)) {
                        this.x -= Math.abs(this.dy) / 4.0F / 250.0F * delta;
                        stop = false;
                        break;
                     }
                  }
               }
            }

            if (collided) {
               this.moveY(-delta);
               if (stop) {
                  if ((Boolean)this.buffManager.getModifier(BuffModifiers.BOUNCY)) {
                     this.dy = -this.dy;
                  } else {
                     this.dy = 0.0F;
                  }
               }
            }
         }
      } else {
         if (collisionFilter != null) {
            Set<Point> currentTiles = (Set)currentCollisions.stream().filter((h) -> {
               return !h.invalidPos();
            }).map((h) -> {
               return new Point(h.tileX, h.tileY);
            }).collect(Collectors.toSet());
            if (this.isPlayer) {
               collisionFilter = collisionFilter.copy().addFilter((tp) -> {
                  return tp.tileID() == TileRegistry.emptyID || !currentTiles.contains(new Point(tp.tileX, tp.tileY));
               });
            } else {
               collisionFilter = collisionFilter.copy().addFilter((tp) -> {
                  return !currentTiles.contains(new Point(tp.tileX, tp.tileY));
               });
            }
         }

         ArrayList newCollisions;
         if (this.dx != 0.0F | this.colDx != 0.0F) {
            this.moveX(delta * 0.5F);
            newCollisions = this.getLevel().getCollisions(this.getCollision(), collisionFilter);
            if (!newCollisions.isEmpty()) {
               this.moveX(-delta * 0.5F);
               if ((Boolean)this.buffManager.getModifier(BuffModifiers.BOUNCY)) {
                  this.dx = -this.dx;
               } else {
                  this.dx = 0.0F;
               }
            }
         }

         if (this.dy != 0.0F | this.colDy != 0.0F) {
            this.moveY(delta * 0.5F);
            newCollisions = this.getLevel().getCollisions(this.getCollision(), collisionFilter);
            if (!newCollisions.isEmpty()) {
               this.moveY(-delta * 0.5F);
               if ((Boolean)this.buffManager.getModifier(BuffModifiers.BOUNCY)) {
                  this.dy = -this.dy;
               } else {
                  this.dy = 0.0F;
               }
            }
         }
      }

      finishedMovementActions.forEach(Runnable::run);
   }

   protected boolean isTileOnPath(int tileX, int tileY) {
      return this.ai.blackboard.mover.hasDestinationInPath(tileX, tileY);
   }

   protected boolean checkAndHandleCollisions(ArrayList<LevelObjectHit> collisions, Rectangle myCollision, Mob doorOpener, boolean doAction, boolean horizontal, Consumer<Runnable> onFinishedMovement) {
      if (collisions.isEmpty()) {
         return false;
      } else {
         LinkedList<LevelObject> actionDoors = null;
         boolean breakDown = false;
         CollisionFilter collisionFilter = this.getLevelCollisionFilter();
         Iterator var10 = collisions.iterator();

         while(true) {
            LevelObject lo;
            while(var10.hasNext()) {
               LevelObjectHit hit = (LevelObjectHit)var10.next();
               if (hit.tileX == -1 && hit.tileY == -1) {
                  return true;
               }

               lo = hit.getLevelObject();
               PathDoorOption doorOption = doorOpener.getPathDoorOption();
               if (doorOption == null) {
                  return true;
               }

               if (!lo.object.isDoor || !doorOption.canOpen(lo.tileX, lo.tileY)) {
                  if (!doorOption.canBreakDown(lo.tileX, lo.tileY) || !this.isTileOnPath(lo.tileX, lo.tileY)) {
                     return true;
                  }

                  if (lo.object.pathCollidesIfBreakDown(lo.level, lo.tileX, lo.tileY, collisionFilter, myCollision)) {
                     return true;
                  }

                  if (!breakDown) {
                     actionDoors = new LinkedList();
                  }

                  actionDoors.add(lo);
                  breakDown = true;
               } else if (!breakDown && !this.openedDoors.hasMobServerOpened(lo.tileX, lo.tileY)) {
                  if (lo.object.pathCollidesIfOpen(lo.level, lo.tileX, lo.tileY, collisionFilter, myCollision)) {
                     return true;
                  }

                  if (actionDoors == null) {
                     actionDoors = new LinkedList();
                  }

                  actionDoors.add(lo);
               }
            }

            if (breakDown) {
               if (doAction) {
                  actionDoors.forEach((lox) -> {
                     doorOpener.pathBreakDown(lox, horizontal, onFinishedMovement);
                  });
               }

               return true;
            }

            if (doAction && actionDoors != null) {
               boolean opened = false;

               for(Iterator var15 = actionDoors.iterator(); var15.hasNext(); opened = doorOpener.switchDoor(lo) || opened) {
                  lo = (LevelObject)var15.next();
               }

               return !opened;
            }

            return false;
         }
      }
   }

   public void setMovement(MobMovement movement) {
      boolean changed = !Objects.equals(this.currentMovement, movement);
      this.currentMovement = movement;
      if (changed || movement == null) {
         this.hasArrivedAtTarget = movement == null;
      }

      if (changed) {
         this.sendMovementPacket(false);
      }

   }

   public void stopMoving() {
      this.setMovement((MobMovement)null);
   }

   public MobMovement getCurrentMovement() {
      return this.currentMovement;
   }

   public boolean hasCurrentMovement() {
      return this.currentMovement != null;
   }

   public boolean hasArrivedAtTarget() {
      return this.hasArrivedAtTarget;
   }

   public void stopMovementOnArrive(boolean val) {
      this.stopMoveWhenArrive = val;
   }

   public void tickOpenedDoors() {
      if (!this.openedDoors.isEmpty()) {
         PathDoorOption pathDoorOption = this.getPathDoorOption();
         if (pathDoorOption == null) {
            this.openedDoors.clear();
         } else {
            Iterator<OpenedDoor> iterator = this.openedDoors.iterator();

            while(true) {
               while(iterator.hasNext()) {
                  OpenedDoor od = (OpenedDoor)iterator.next();
                  if (od.isValid(this.getLevel()) && pathDoorOption.canClose(od.tileX, od.tileY)) {
                     Mob mount = this.getMount();
                     Mob collider = mount == null ? this : mount;
                     if (!od.switchedDoorCollides(this.getLevel(), collider.getCollision(), collider.getLevelCollisionFilter()) && !od.entityCollidesWithSwitchedDoor(this.getLevel()) && !od.clientCollidesWithSwitchedDoor(this.getLevel())) {
                        this.switchDoor(od);
                        iterator.remove();
                     }
                  } else {
                     iterator.remove();
                  }
               }

               return;
            }
         }
      }
   }

   protected static boolean staticSwitchDoor(LevelObject lo) {
      lo.object.onPathOpened(lo.level, lo.tileX, lo.tileY);
      return true;
   }

   protected boolean pathBreakDown(LevelObject lo, boolean horizontal, Consumer<Runnable> onFinishedMovement) {
      if (!this.isClient() && this.lastPathBreakTime < this.getWorldEntity().getTime() - (long)this.pathBreakCooldown) {
         this.lastPathBreakTime = this.getWorldEntity().getTime();
         boolean dir = horizontal ? this.dx > 0.0F : this.dy > 0.0F;
         List<Rectangle> collisions = lo.getCollisions(lo.rotation);
         int hitX = lo.tileX * 32 + 16;
         int hitY = lo.tileY * 32 + 16;
         Rectangle bounds = null;
         Iterator var9 = collisions.iterator();

         while(var9.hasNext()) {
            Rectangle collision = (Rectangle)var9.next();
            if (bounds == null) {
               bounds = collision;
            } else {
               bounds = bounds.union(collision);
            }
         }

         if (bounds != null) {
            if (horizontal) {
               hitY = (int)bounds.getCenterY();
               hitX = dir ? bounds.x : bounds.x + bounds.width;
            } else {
               hitX = (int)bounds.getCenterX();
               hitY = dir ? bounds.y : bounds.y + bounds.height;
            }
         }

         int damage = this.getPathBreakDownDamage(lo);
         PathBreakDownLevelData data = PathBreakDownLevelData.getPathBreakDownData(this.getLevel());
         int totalDamage = data.doDamage(lo.tileX, lo.tileY, damage);
         if (totalDamage > lo.object.objectHealth) {
            lo.object.onPathBreakDown(lo.level, lo.tileX, lo.tileY, damage, hitX, hitY);
            data.clear(lo.tileX, lo.tileY);
         }

         this.getLevel().getServer().network.sendToClientsWithTile(new PacketTileDamage(this.getLevel(), TileDamageType.Object, lo.tileX, lo.tileY, damage, true, hitX, hitY), this.getLevel(), lo.tileX, lo.tileY);
         if (this.ai != null) {
            this.ai.resetStuck();
         }

         onFinishedMovement.accept(() -> {
            this.onPathBreakDownHit(lo, dir, horizontal);
         });
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobPathBreakDownHit(this, lo, dir, horizontal), this);
         }

         return true;
      } else {
         return false;
      }
   }

   public int getPathBreakDownDamage(LevelObject lo) {
      return 10;
   }

   public void onPathBreakDownHit(LevelObject lo, boolean dir, boolean horizontal) {
      Mob mount = this.getMount();
      Mob target = mount == null ? this : mount;
      if (horizontal) {
         target.dx = dir ? -50.0F : 50.0F;
      } else {
         target.dy = dir ? -50.0F : 50.0F;
      }

   }

   protected boolean switchDoor(LevelObject lo) {
      if (!this.isClient() && !this.isPlayer) {
         OpenedDoor last = this.openedDoors.get(lo.tileX, lo.tileY);
         if (last != null) {
            last.mobX = this.getX();
            last.mobY = this.getY();
            return true;
         }

         PathDoorOption pathDoorOption = this.getPathDoorOption();
         if (pathDoorOption != null && pathDoorOption.canClose(lo.tileX, lo.tileY) && staticSwitchDoor(lo)) {
            this.openedDoors.add(lo.tileX, lo.tileY, this.getX(), this.getY(), lo.object.isSwitched);
            return true;
         }
      }

      return false;
   }

   protected void switchDoor(OpenedDoor od) {
      od.switchDoor(this.getLevel());
   }

   public boolean inLiquid() {
      return this.inLiquid(this.getX(), this.getY());
   }

   public boolean inLiquid(int x, int y) {
      return !this.isRiding() && !this.isWaterWalking() && this.getLevel() != null && this.getLevel().inLiquid(x, y);
   }

   public void tickLevel() {
      this.getLevel().getLevelTile(this.getX() / 32, this.getY() / 32).tick(this);
      this.getLevel().getLevelObject(this.getX() / 32, this.getY() / 32).tick(this);
   }

   protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
      float accModX = this.getDecelerationModifier();
      float accModY = this.getDecelerationModifier();
      if (moveX != 0.0F || moveY != 0.0F) {
         if (moveX != 0.0F) {
            accModX = this.getAccelerationModifier();
         }

         if (moveY != 0.0F) {
            accModY = this.getAccelerationModifier();
         }

         if (!this.isAttacking) {
            this.setFacingDir(moveX, moveY);
         }
      }

      Point2D.Float normalize = GameMath.normalize(moveX, moveY);
      if (friction != 0.0F) {
         this.dx += (speed * friction * normalize.x - friction * this.dx) * delta / 250.0F * accModX;
         this.dy += (speed * friction * normalize.y - friction * this.dy) * delta / 250.0F * accModY;
      } else if (normalize.x != 0.0F || normalize.y != 0.0F) {
         this.dx += (speed * normalize.x - this.dx) * delta / 250.0F * accModX;
         this.dy += (speed * normalize.y - this.dy) * delta / 250.0F * accModY;
      }

      if (moveX == 0.0F && Math.abs(this.dx) < speed / 20.0F) {
         this.dx = 0.0F;
      }

      if (moveY == 0.0F && Math.abs(this.dy) < speed / 20.0F) {
         this.dy = 0.0F;
      }

   }

   public int stoppingDistance(float friction, float currentSpeed) {
      return currentSpeed == 0.0F ? 0 : (int)(currentSpeed / friction);
   }

   public void setFacingDir(float deltaX, float deltaY) {
      float threshold = 0.2F;
      if (this.prioritizeVerticalDir) {
         if (Math.abs(deltaX) - Math.abs(deltaY) <= threshold) {
            this.dir = deltaY < 0.0F ? 0 : 2;
         } else {
            this.dir = deltaX < 0.0F ? 3 : 1;
         }
      } else if (Math.abs(deltaY) - Math.abs(deltaX) <= threshold) {
         this.dir = deltaX < 0.0F ? 3 : 1;
      } else {
         this.dir = deltaY < 0.0F ? 0 : 2;
      }

   }

   protected void calcNetworkSmooth(float delta) {
      if (this.isServer()) {
         this.nX = this.x;
         this.nY = this.y;
         this.isSmoothSnapped = true;
      } else {
         if (this.staySmoothSnapped) {
            this.isSmoothSnapped = true;
         }

         if (this.isSmoothSnapped) {
            this.nX = this.x;
            this.nY = this.y;
         } else {
            float deltaX = this.x - this.nX;
            float deltaY = this.y - this.nY;
            Point2D.Float tempPoint = new Point2D.Float(deltaX, deltaY);
            float dist = (float)tempPoint.distance(0.0, 0.0);
            if (dist == 0.0F) {
               dist = 0.1F;
            }

            float normX = tempPoint.x / dist;
            float normY = tempPoint.y / dist;
            Point2D.Float dir = new Point2D.Float(normX, normY);
            float speed = (this.getSpeed() + dist) * (delta / 250.0F);
            if (speed > dist) {
               this.isSmoothSnapped = true;
               this.nX = this.x;
               this.nY = this.y;
            } else {
               this.nX += dir.x * speed;
               this.nY += dir.y * speed;
            }
         }

      }
   }

   public int getDrawX() {
      return !this.isSmoothSnapped && !this.staySmoothSnapped ? (int)this.nX : this.getX();
   }

   public int getDrawY() {
      return !this.isSmoothSnapped && !this.staySmoothSnapped ? (int)this.nY : this.getY();
   }

   public final void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
      Point drawPos = this.getDrawPos();
      this.addDrawablesLoop(list, tileList, topList, level, drawPos.x, drawPos.y, tickManager, camera, perspective, false);
      this.addExtraDrawables(list, tileList, topList, level, drawPos.x, drawPos.y, tickManager, camera, perspective);
      this.addStatusBarDrawable(overlayList, level, drawPos.x, drawPos.y, tickManager, camera, perspective);
      if (GlobalData.debugActive()) {
         topList.add((tm) -> {
            Screen.drawLineRGBA(camera.getDrawX(this.x), camera.getDrawY(this.y), camera.getDrawX(drawPos.x), camera.getDrawY(drawPos.y), this.isSmoothSnapped ? 0.2F : 0.8F, this.isSmoothSnapped ? 0.8F : 0.2F, 0.2F, 1.0F);
            if (Settings.serverPerspective) {
               FontManager.bit.drawString((float)camera.getDrawX(this.x), (float)camera.getDrawY(this.y), this.moveSent + ", " + this.healthSent + ", " + this.resilienceSent + ", " + this.manaSent, (new FontOptions(12)).outline());
               if (this.ai.blackboard.mover.hasPath()) {
                  this.ai.blackboard.mover.getPath().drawPath(this, camera);
               }
            }

         });
      }

   }

   private void addDrawablesLoop(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, boolean fromMount) {
      LinkedList<MobDrawable> drawables = new LinkedList();
      LinkedList<LevelSortedDrawable> riderDrawables = new LinkedList();
      Mob mount = this.getMount();
      if (mount == null || fromMount) {
         this.addDrawables(drawables, tileList, topList, level, x, y, tickManager, camera, perspective);
      }

      if (this.isMounted()) {
         Mob rider = this.getRider();
         if (rider != null) {
            rider.addDrawablesLoop(riderDrawables, tileList, topList, level, x, y, tickManager, camera, perspective, true);
         }
      }

      list.add(new 2(this, this, y, drawables, riderDrawables));
   }

   protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
   }

   protected void addExtraDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
   }

   public boolean isHealthBarVisible() {
      return Settings.showMobHealthBars && this.isVisible() && this.getHealthUnlimited() < this.getMaxHealth() && (!this.isBoss() || !Settings.showBossHealthBars);
   }

   public Rectangle getHealthBarBounds(int x, int y) {
      Rectangle selectBox = this.getSelectBox(x, y);
      int width = GameMath.limit(selectBox.width, 32, 64);
      x = selectBox.x + selectBox.width / 2 - width / 2;
      y = selectBox.y - Settings.UI.healthbar_small_background.getHeight() - 2;
      return new Rectangle(x, y, width, Settings.UI.healthbar_small_background.getHeight());
   }

   public void addStatusBarDrawable(OrderableDrawables list, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
      if (perspective != this) {
         if (this.isHealthBarVisible()) {
            Rectangle bounds = this.getHealthBarBounds(x, y);
            if (bounds == null) {
               return;
            }

            int health = this.getHealthUnlimited();
            int maxHealth = this.getMaxHealth();
            float perc = GameMath.limit((float)health / (float)maxHealth, 0.0F, 1.0F);
            int drawX = camera.getDrawX(bounds.x);
            int drawY = camera.getDrawY(bounds.y);
            GameLight light = level.getLightLevel((bounds.x + bounds.width / 2) / 32, (bounds.y + 4) / 32);
            float alphaMod = GameMath.lerp(light.getFloatLevel(), 0.2F, 1.0F);
            Color statusColor = GameUtils.getStatusColorRedPref(perc, 0.75F, 0.7F, 2.2F);
            Color finalFillColor = new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), (int)(240.0F * alphaMod));
            Color finalBackgroundColor = new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), (int)(190.0F * alphaMod));
            DrawOptions options = (new ProgressBarDrawOptions(Settings.UI.healthbar_small_fill, bounds.width)).color(finalBackgroundColor).addBar(Settings.UI.healthbar_small_background, perc).color(finalFillColor).minWidth(4).end().pos(drawX, drawY);
            list.add((tm) -> {
               options.draw();
            });
         }

      }
   }

   protected void addShadowDrawables(OrderableDrawables list, int x, int y, GameLight light, GameCamera camera) {
      if (!(Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY) && !this.isRiding()) {
         TextureDrawOptions shadowOptions = this.getShadowDrawOptions(x, y, light, camera);
         if (shadowOptions != null) {
            list.add((tm) -> {
               shadowOptions.draw();
            });
         }

      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
      GameTexture shadowTexture = Textures.human_shadow;
      int res = shadowTexture.getHeight();
      int drawX = camera.getDrawX(x) - res / 2;
      int drawY = camera.getDrawY(y) - res / 2;
      drawY += this.getBobbing(x, y);
      return shadowTexture.initDraw().sprite(this.dir, 0, res).light(light).pos(drawX, drawY);
   }

   public boolean isVisibleOnMap(Client client, LevelMap map) {
      return this.isBoss() || super.isVisibleOnMap(client, map);
   }

   public CollisionFilter getLevelCollisionFilter() {
      return (new CollisionFilter()).mobCollision();
   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.BASIC_DOOR_OPTIONS : null;
   }

   public boolean estimateCanMoveTo(int tileX, int tileY, boolean acceptAdjacentTile) {
      CollisionFilter collisionFilter = this.getLevelCollisionFilter();
      if (collisionFilter != null && collisionFilter.hasAdders()) {
         PathDoorOption doorOption = this.getPathDoorOption();
         return doorOption != null ? doorOption.canMoveToTile(this.getTileX(), this.getTileY(), tileX, tileY, acceptAdjacentTile) : RegionPathfinding.canMoveToTile(this.getLevel(), this.getTileX(), this.getTileY(), tileX, tileY, doorOption, acceptAdjacentTile);
      } else {
         return true;
      }
   }

   public boolean canBeTargetedFromAdjacentTiles() {
      return false;
   }

   public final boolean collidesWith(Level level, int x, int y) {
      return level.collides(this.getCollision(x, y), this.getLevelCollisionFilter());
   }

   public final boolean collidesWith(Level level) {
      return this.collidesWith(level, this.getX(), this.getY());
   }

   public boolean collidesWith(Mob other) {
      return other != this && this.getCollision().intersects(other.getCollision());
   }

   public Rectangle getCollision(int x, int y) {
      return new Rectangle(x + this.collision.x, y + this.collision.y, this.collision.width, this.collision.height);
   }

   public Rectangle getCollision() {
      return this.getCollision(this.getX(), this.getY());
   }

   public Rectangle getHitBox(int x, int y) {
      return new Rectangle(x + this.hitBox.x, y + this.hitBox.y, this.hitBox.width, this.hitBox.height);
   }

   public Rectangle getHitBox() {
      return this.getHitBox(this.getX(), this.getY());
   }

   public Rectangle getSelectBox(int x, int y) {
      return new Rectangle(x + this.selectBox.x, y + this.selectBox.y, this.selectBox.width, this.selectBox.height);
   }

   public Rectangle getSelectBox() {
      return this.getSelectBox(this.getX(), this.getY());
   }

   public void updatePosFromServer(float x, float y, boolean isDirect) {
      this.updatePosFromServer(x, y, this.dx, this.dy, isDirect);
   }

   public void updatePosFromServer(float x, float y, float dx, float dy, boolean isDirect) {
      if (isDirect || this.movePosTolerance <= 0.0F || this.getDistance(x, y) >= this.movePosTolerance) {
         this.setPos(x, y, isDirect);
         this.dx = dx;
         this.dy = dy;
      }

   }

   public void setPos(float x, float y, boolean isDirect) {
      if (this.isRiding()) {
         Mob mount = this.getMount();
         if (mount != null) {
            mount.setPos(x, y, isDirect);
         }
      }

      this.x = x;
      this.y = y;
      if (isDirect) {
         this.nX = x;
         this.nY = y;
         this.isSmoothSnapped = true;
      } else {
         this.isSmoothSnapped = false;
      }

   }

   protected void checkCollision() {
      Performance.record(this.getLevel().tickManager(), "checkCollision", () -> {
         Rectangle collision = this.getCollision();
         int range = (int)GameMath.max(new double[]{GameMath.diagonalMoveDistance(0, 0, collision.width, collision.height), 100.0});
         this.getLevel().entityManager.streamAreaMobsAndPlayers(this.x, this.y, range).filter((m) -> {
            return m != this && this.collidesWith(m);
         }).forEach(this::collidedWith);
      });
   }

   public boolean collidesWithAnyMob(Level level, int x, int y) {
      Rectangle collision = this.getCollision(x, y);
      int range = (int)GameMath.max(new double[]{GameMath.diagonalMoveDistance(0, 0, collision.width, collision.height), 100.0});
      return level.entityManager.streamAreaMobsAndPlayers((float)x, (float)y, range).anyMatch((m) -> {
         return m != this && m.canPushMob(this) && this.canBePushed(m) && this.collidesWith(m);
      });
   }

   protected void collidedWith(Mob other) {
      if (this.mount != other.getRealUniqueID() && this.rider != other.getRealUniqueID()) {
         if (other.canPushMob(this) && this.canBePushed(other)) {
            Point2D.Float normVec = GameMath.normalize(this.x - other.x, this.y - other.y);
            if (normVec.x == 0.0F && normVec.y == 0.0F) {
               normVec = new Point2D.Float(0.0F, -1.0F);
            }

            float dist = this.getDistance(other);
            if (dist < 1.0F) {
               dist = 1.0F;
            }

            this.colDx += normVec.x * 50.0F / dist;
            this.colDy += normVec.y * 50.0F / dist;
         }

         if (other.isPlayer) {
            if (this.isServer()) {
               if (!Settings.giveClientsPower && this.collisionHitCooldowns.canHit(other) && this.canCollisionHit(other)) {
                  this.handleCollisionHit(other);
               }
            } else if (this.isClient() && this.getLevel().getClient().allowClientsPower() && ((PlayerMob)other).getNetworkClient() == this.getLevel().getClient().getClient() && this.collisionHitCooldowns.canHit(other) && this.canCollisionHit(other)) {
               GameDamage collisionDamage = this.getCollisionDamage(other);
               if (collisionDamage != null) {
                  this.collisionHitCooldowns.startCooldown(other);
                  other.startHitCooldown();
                  this.getLevel().getClient().network.sendPacket(new PacketPlayerCollisionHit(this));
               }
            }
         } else if (this.isServer() && this.collisionHitCooldowns.canHit(other) && this.canCollisionHit(other)) {
            this.handleCollisionHit(other);
         }

      }
   }

   public final void handleCollisionHit(Mob target) {
      GameDamage collisionDamage = this.getCollisionDamage(target);
      if (collisionDamage != null) {
         this.handleCollisionHit(target, collisionDamage, this.getCollisionKnockback(target));
         this.collisionHitCooldowns.startCooldown(target);
      }

   }

   public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
      target.isServerHit(damage, target.x - this.x, target.y - this.y, (float)knockback, this);
   }

   public boolean canPushMob(Mob other) {
      return true;
   }

   public boolean canBePushed(Mob other) {
      return true;
   }

   public boolean canCollisionHit(Mob target) {
      return !this.removed() && target.canBeTargeted(this, this.getPvPOwner()) && target.canBeHit(this);
   }

   public GameDamage getCollisionDamage(Mob target) {
      return null;
   }

   public int getCollisionKnockback(Mob target) {
      return 100;
   }

   public float getDistance(Mob other) {
      return (float)(new Point2D.Float(this.x, this.y)).distance((double)other.x, (double)other.y);
   }

   public float getDistance(float x, float y) {
      return (float)(new Point2D.Float(this.x, this.y)).distance((double)x, (double)y);
   }

   public NetworkClient getPvPOwner() {
      if (this.isServer()) {
         if (this.followingSlot != -1) {
            return this.getLevel().getServer().getClient(this.followingSlot);
         }

         if (this.rider >= 0 && this.rider < this.getLevel().getServer().getSlots()) {
            return this.getLevel().getServer().getClient(this.rider);
         }
      } else if (this.isClient()) {
         if (this.followingSlot != -1) {
            return this.getLevel().getClient().getClient(this.followingSlot);
         }

         if (this.rider >= 0 && this.rider < this.getLevel().getClient().getSlots()) {
            return this.getLevel().getClient().getClient(this.rider);
         }
      }

      return null;
   }

   public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
      if ((Boolean)this.buffManager.getModifier(BuffModifiers.UNTARGETABLE)) {
         return false;
      } else if (!this.canTakeDamage()) {
         return false;
      } else if (this.getUniqueID() == attacker.getUniqueID()) {
         return false;
      } else if (this.getUniqueID() == attacker.mount) {
         return false;
      } else if (this.isSameTeam(attacker)) {
         return false;
      } else if (!this.isSamePlace(attacker)) {
         return false;
      } else if (attacker.isInAttackOwnerChain(this)) {
         return false;
      } else if (!attacker.canTarget(this)) {
         return false;
      } else {
         NetworkClient fClient = null;
         if (this.isServer()) {
            fClient = this.getFollowingServerClient();
         } else if (this.isClient()) {
            fClient = this.getFollowingClientClient();
         }

         if (fClient != null && attacker == ((NetworkClient)fClient).playerMob) {
            return false;
         } else {
            NetworkClient pvpOwner = this.getPvPOwner();
            return pvpOwner == null || attackerClient == null || pvpOwner == attackerClient || pvpOwner.pvpEnabled() && attackerClient.pvpEnabled();
         }
      }
   }

   public boolean canTarget(Mob target) {
      return true;
   }

   public int getFlyingHeight() {
      if (this.isRiding()) {
         Mob mount = this.getMount();
         if (mount != null) {
            return mount.getFlyingHeight();
         }
      }

      return 0;
   }

   public final boolean isFlying() {
      return this.getFlyingHeight() > 0;
   }

   public boolean canHitThroughCollision() {
      return false;
   }

   public boolean canBeHit(Attacker attacker) {
      if (!this.canTakeDamage()) {
         return false;
      } else {
         Mob mounted = this.getRider();
         if (mounted != null && mounted.canTakeDamage()) {
            return mounted.canBeHit(attacker);
         } else {
            return this.getWorldEntity().getTime() >= this.hitTime + (long)this.hitCooldown;
         }
      }
   }

   public int getHitCooldownUniqueID() {
      return this.getUniqueID();
   }

   public void startHitCooldown() {
      this.hitTime = this.getWorldEntity().getTime();
   }

   public long getTimeSinceLastHit() {
      return this.getWorldEntity().getTime() - this.hitTime;
   }

   public void spawnDeathParticles(float knockbackX, float knockbackY) {
   }

   protected void playDeathSound() {
      float pitch = (Float)GameRandom.globalRandom.getOneOf(new Float[]{0.95F, 1.0F, 1.05F});
      Screen.playSound(GameResources.npcdeath, SoundEffect.effect(this).volume(0.8F).pitch(pitch));
   }

   public void spawnRemoveParticles(float knockbackX, float knockbackY) {
   }

   public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
      if (!this.canTakeDamage()) {
         return null;
      } else {
         int damageResisted = 0;
         int damage = event.damage;
         this.startHitCooldown();
         this.lastCombatTime = this.hitTime;
         Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
         if (attacker != null) {
            attacker.getAttackOwners().forEach((m) -> {
               m.lastCombatTime = this.hitTime;
            });
         }

         if (!event.wasPrevented) {
            damage = event.damage - (int)this.getResilience();
            if (damage < 0) {
               this.setResilienceHidden((float)(-damage));
               damageResisted = event.damage;
            } else {
               this.setHealthHidden(this.getHealth() - damage, event.knockbackX, event.knockbackY, attacker);
               if (this.getResilience() > 0.0F) {
                  damageResisted = (int)this.getResilience();
                  this.setResilienceHidden(0.0F);
               }
            }

            float finalKnockback = event.knockbackAmount;
            if (attackOwner != null && attackOwner.isPlayer) {
               finalKnockback *= this.getWorldSettings().difficulty.knockbackGivenModifier;
            }

            this.knockback(event.knockbackX, event.knockbackY, finalKnockback);
         }

         this.doWasHitLogic(event);
         if (attackOwner != null) {
            attackOwner.doHasAttackedLogic(event);
         }

         if (this.getLevel() != null && !this.isServer()) {
            if (Settings.showDamageText && event.showDamageTip) {
               if (damageResisted > 0) {
                  this.spawnResistedDamageText(damageResisted, event.isCrit ? 20 : 16, event.isCrit);
               }

               if (damage > 0) {
                  this.spawnDamageText(damage, event.isCrit ? 20 : 16, event.isCrit);
               }
            }

            if (event.playHitSound) {
               if (damageResisted > 0) {
                  this.playResilienceSound();
               }

               if (damage > 0) {
                  this.playHitSound();
               }
            }
         }

         return event;
      }
   }

   public void spawnDamageText(int damage, int size, boolean isCrit) {
      Rectangle box = this.getSelectBox();
      int rndX = GameRandom.globalRandom.getIntOffset(box.x + box.width / 2, box.width / 4);
      int rndY = GameRandom.globalRandom.getIntOffset(box.y + box.height, box.height / 4);
      this.getLevel().hudManager.addElement(new DamageText(rndX, rndY, damage, (new FontOptions(size)).outline().color(isCrit ? new Color(225, 120, 0) : Color.YELLOW), isCrit ? GameRandom.globalRandom.getIntBetween(50, 65) : GameRandom.globalRandom.getIntBetween(25, 45)));
   }

   public void spawnResistedDamageText(int damage, int size, boolean isCrit) {
      Rectangle box = this.getSelectBox();
      int rndX = GameRandom.globalRandom.getIntOffset(box.x + box.width / 2, box.width / 4);
      int rndY = GameRandom.globalRandom.getIntOffset(box.y + box.height, box.height / 4);
      this.getLevel().hudManager.addElement(new DamageText(rndX, rndY, damage, (new FontOptions(size)).outline().color(new Color(125, 125, 125)), isCrit ? GameRandom.globalRandom.getIntBetween(50, 65) : GameRandom.globalRandom.getIntBetween(25, 45)));
   }

   public void knockback(float x, float y, float knockback) {
      if (this.isRiding()) {
         Mob mount = this.getMount();
         if (mount != null) {
            mount.knockback(x, y, knockback);
            return;
         }
      }

      knockback *= this.getKnockbackModifier();
      if (knockback != 0.0F) {
         Point2D.Float normVec = GameMath.normalize(x, y);
         this.dx += normVec.x * knockback;
         this.dy += normVec.y * knockback;
      }

   }

   public MobWasHitEvent isServerHit(GameDamage damage, float x, float y, float knockback, Attacker attacker) {
      if (this.removed()) {
         return null;
      } else if (!this.canTakeDamage()) {
         return null;
      } else {
         Mob attackOwner;
         if (this.isMounted()) {
            attackOwner = this.getRider();
            if (attackOwner != null && attackOwner.canTakeDamage()) {
               attackOwner.isServerHit(damage, x, y, knockback, attacker);
               return null;
            }
         }

         attackOwner = attacker != null ? attacker.getAttackOwner() : null;
         int beforeHealth = this.getHealth();
         MobBeforeHitEvent beforeHitEvent = new MobBeforeHitEvent(this, attacker, damage, x, y, knockback);
         this.doBeforeHitLogic(beforeHitEvent);
         if (attackOwner != null) {
            attackOwner.doBeforeAttackedLogic(beforeHitEvent);
         }

         MobBeforeHitCalculatedEvent beforeHitCalculatedEvent = new MobBeforeHitCalculatedEvent(beforeHitEvent);
         this.doBeforeHitCalculatedLogic(beforeHitCalculatedEvent);
         if (attackOwner != null) {
            attackOwner.doBeforeAttackedCalculatedLogic(beforeHitCalculatedEvent);
         }

         MobWasHitEvent hitEvent = new MobWasHitEvent(beforeHitCalculatedEvent);
         this.sendHitPacket(hitEvent, attacker);
         this.isHit(hitEvent, attacker);
         if (attackOwner != null && this.countStats && attackOwner.isPlayer) {
            ServerClient client = ((PlayerMob)attackOwner).getServerClient();
            int healthDamage = beforeHealth - this.getHealth();
            if (healthDamage > 0 && this.countDamageDealt()) {
               client.newStats.type_damage_dealt.addDamage(damage.type, healthDamage);
            }

            if (healthDamage >= this.getMaxHealth() && AchievementManager.ONE_TAPPED_MOBS.contains(this.getStringID()) && client.achievementsLoaded()) {
               client.achievements().ONE_TAPPED.markCompleted(client);
            }
         }

         return hitEvent;
      }
   }

   public DeathMessageTable getDeathMessages() {
      return DeathMessageTable.fromRange("generic", 8);
   }

   public GameMessage getAttackerName() {
      return this.getLocalization();
   }

   public GameMessage getLocalization() {
      return MobRegistry.getLocalization(this.getID());
   }

   public final String getDisplayName() {
      return this.getLocalization().translate();
   }

   public Mob getFirstAttackOwner() {
      return this;
   }

   public int getAttackerUniqueID() {
      return this.getUniqueID();
   }

   protected void sendHitPacket(MobWasHitEvent event, Attacker attacker) {
      if (this.isServer()) {
         this.getLevel().getServer().network.sendToClientsWithEntity(new PacketHitMob(this, event, attacker), this);
      }
   }

   protected void doBeforeHitLogic(MobBeforeHitEvent event) {
      if (!this.isMounted()) {
         this.ai.beforeHit(event);
      }

      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onBeforeHit(event);
      });
   }

   protected void doBeforeAttackedLogic(MobBeforeHitEvent event) {
      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onBeforeAttacked(event);
      });
   }

   protected void doBeforeHitCalculatedLogic(MobBeforeHitCalculatedEvent event) {
      if (!this.isMounted()) {
         this.ai.beforeHitCalculated(event);
      }

      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onBeforeHitCalculated(event);
      });
   }

   protected void doBeforeAttackedCalculatedLogic(MobBeforeHitCalculatedEvent event) {
      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onBeforeAttackedCalculated(event);
      });
   }

   protected void doWasHitLogic(MobWasHitEvent event) {
      if (!this.isMounted()) {
         this.ai.wasHit(event);
      }

      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onWasHit(event);
      });
   }

   protected void doHasAttackedLogic(MobWasHitEvent event) {
      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onHasAttacked(event);
      });
   }

   protected void doHasKilledTarget(MobWasKilledEvent event) {
      this.buffManager.getArrayBuffs().forEach((b) -> {
         b.onHasKilledTarget(event);
      });
   }

   protected void playHitSound() {
      float pitch = (Float)GameRandom.globalRandom.getOneOf(new Float[]{0.95F, 1.0F, 1.05F});
      Screen.playSound(GameResources.npchurt, SoundEffect.effect(this).pitch(pitch));
   }

   protected void playResilienceSound() {
      float pitch = (Float)GameRandom.globalRandom.getOneOf(new Float[]{0.95F, 1.0F, 1.05F});
      Screen.playSound(GameResources.cling, SoundEffect.effect(this).pitch(pitch));
   }

   public void startAttackCooldown() {
      this.attackTime = this.getWorldEntity().getTime();
   }

   public long getNextAttackCooldown() {
      return this.attackTime + (long)this.attackCooldown - this.getWorldEntity().getTime();
   }

   public boolean canAttack() {
      this.onCooldown = this.getNextAttackCooldown() > 0L;
      if ((Boolean)this.buffManager.getModifier(BuffModifiers.INTIMIDATED)) {
         return false;
      } else {
         return !this.onCooldown;
      }
   }

   public LootTable getLootTable() {
      return new LootTable();
   }

   public LootTable getPrivateLootTable() {
      return new LootTable();
   }

   public boolean dropsLoot() {
      return this.dropsLoot;
   }

   public double getDistanceRan() {
      return this.distanceRan;
   }

   public double getDistanceRidden() {
      return this.distanceRidden;
   }

   public float getCurrentSpeed() {
      return this.currentSpeed;
   }

   protected void tickRegen() {
      if (!this.removed()) {
         this.buffManager.tickDamageOverTime();
         if (!this.isInCombat()) {
            this.regenBuffer += (double)(this.getRegen() / 20.0F);
         }

         this.regenBuffer += (double)(this.getCombatRegen() / 20.0F);
         int delta;
         if (this.regenBuffer >= 1.0 || this.regenBuffer <= -1.0) {
            delta = (int)this.regenBuffer;
            this.regenBuffer -= (double)delta;
            if (delta < 0 && this.getHealth() > 0 || delta > 0 && this.getHealth() < this.getMaxHealth()) {
               this.setHealth(this.getHealth() + delta, (Attacker)null);
            }
         }

         delta = this.getMaxResilience();
         float manaRegenBuffer;
         if (delta > 0) {
            manaRegenBuffer = this.getResilienceRegen();
            if (manaRegenBuffer > 0.0F) {
               this.addResilienceHidden(manaRegenBuffer / 20.0F);
            } else if (this.getResilience() > 0.0F && (float)this.resilienceGainedTime + this.resilienceDecayDelay < (float)this.getTime()) {
               float resilienceDecay = this.getResilienceDecay();
               if (resilienceDecay > 0.0F) {
                  this.addResilienceHidden(-resilienceDecay / 20.0F);
               }
            }
         }

         if (this.usesMana()) {
            manaRegenBuffer = 0.0F;
            if (!this.isInCombat()) {
               manaRegenBuffer += this.getManaRegen() / 20.0F;
            }

            if (this.getWorldEntity().getTime() > this.lastManaSpentTime + 5000L) {
               this.isManaExhausted = false;
               if (this.buffManager.hasBuff(Debuffs.MANA_EXHAUSTION)) {
                  this.buffManager.removeBuff(Debuffs.MANA_EXHAUSTION, false);
               }
            }

            manaRegenBuffer += this.getCombatManaRegen() / 20.0F;
            this.setManaHidden(this.getMana() + manaRegenBuffer);
         }

      }
   }

   public boolean isInCombat() {
      if (this.getWorldEntity() == null) {
         return false;
      } else {
         return this.getWorldEntity().getTime() < this.lastCombatTime + 5000L;
      }
   }

   public void forceInCombat() {
      this.lastCombatTime = this.getWorldEntity().getTime();
   }

   public long getTimeSinceStartCombat() {
      if (this.combatStartTime < 0L) {
         return this.combatStartTime;
      } else {
         return this.getHealth() == this.getMaxHealth() ? -1L : this.getWorldEntity().getTime() - this.combatStartTime;
      }
   }

   public void sendMovementPacket(boolean isDirect) {
      this.sendNextMovementPacket = true;
      this.nextMovementPacketDirect = this.nextMovementPacketDirect || isDirect;
   }

   public void sendHealthPacket(boolean isFull) {
      if (this.canTakeDamage()) {
         this.sendNextHealthPacket = true;
         this.nextHealthPacketFull = this.nextHealthPacketFull || isFull;
      }
   }

   public void sendResiliencePacket(boolean isFull) {
      if (this.getMaxResilience() > 0) {
         this.sendNextResiliencePacket = true;
         this.nextResiliencePacketFull = this.nextResiliencePacketFull || isFull;
      }
   }

   public void sendManaPacket(boolean isFull) {
      if (this.usesMana()) {
         this.sendNextManaPacket = true;
         this.nextManaPacketFull = this.nextManaPacketFull || isFull;
      }
   }

   public boolean isAccelerating() {
      return this.moveX != 0.0F || this.moveY != 0.0F;
   }

   public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
      return location.checkNotInLiquid().checkNotSolidTile().checkNotOnSurfaceInsideOnFloor().checkNotLevelCollides();
   }

   public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
      return false;
   }

   public void onSpawned(int posX, int posY) {
      this.setPos((float)posX, (float)posY, true);
      this.setHealth(this.getMaxHealth());
   }

   public boolean canDespawn() {
      return !this.canDespawn ? false : GameUtils.streamNetworkClients(this.getLevel()).filter((c) -> {
         return c.playerMob != null;
      }).noneMatch((c) -> {
         return this.getDistance(c.playerMob) < (float)MOB_SPAWN_AREA.maxSpawnDistance;
      });
   }

   public void attack(int x, int y, boolean showAllDirections) {
      this.attackTime = this.getWorldEntity().getTime();
      if (showAllDirections) {
         this.setFacingDir((float)(x - this.getX()), (float)(y - this.getY()));
      } else if (x > this.getX()) {
         this.dir = 1;
      } else {
         this.dir = 3;
      }

   }

   public long getLastAttackTime() {
      return this.attackTime;
   }

   public long getTimeSinceLastAttack() {
      return this.getTime() - this.attackTime;
   }

   public void showAttack(int x, int y, boolean showAllDirections) {
   }

   public final void runNetworkFieldUpdate(PacketReader reader) {
      this.networkFields.readUpdatePacket(reader);
   }

   public <T extends NetworkField<?>> T registerNetworkField(T field) {
      return this.networkFields.registerField(field);
   }

   public final void runAbility(int id, PacketReader reader) {
      this.abilities.runAbility(id, reader);
   }

   public <T extends MobAbility> T registerAbility(T ability) {
      return this.abilities.registerAbility(ability);
   }

   public void interact(PlayerMob player) {
   }

   public Point getInteractPos() {
      return this.getDrawPos();
   }

   public boolean inInteractRange(Mob mob) {
      Point interactPos = this.getInteractPos();
      return mob.getDistance((float)interactPos.x, (float)interactPos.y) <= 75.0F;
   }

   public boolean canInteract(Mob mob) {
      return false;
   }

   public Mob getMount() {
      if (!this.isRiding()) {
         return null;
      } else {
         Mob out = GameUtils.getLevelMob(this.mount, this.getLevel());
         if (out != null) {
            if (out.rider != this.getRealUniqueID()) {
               out.rider = this.getRealUniqueID();
            }

            if (out.removed()) {
               this.mount = -1;
               this.buffManager.updateBuffs();
               return null;
            }
         }

         return out;
      }
   }

   public Mob getRider() {
      if (!this.isMounted()) {
         return null;
      } else {
         Mob out = GameUtils.getLevelMob(this.rider, this.getLevel());
         if (out != null) {
            if (out.mount != this.getRealUniqueID()) {
               out.mount = this.getRealUniqueID();
               out.buffManager.updateBuffs();
            }

            if (out.removed()) {
               this.rider = -1;
               return null;
            }
         }

         return out;
      }
   }

   public boolean isMounted() {
      return this.rider != -1;
   }

   public boolean isRiding() {
      return this.mount != -1;
   }

   protected void doMountedLogic() {
   }

   public boolean mount(Mob mob, boolean setMounterPos) {
      return this.mount(mob, setMounterPos, this.x, this.y);
   }

   public boolean mount(Mob mob, boolean setMounterPos, float mounterX, float mounterY) {
      if (this.isRiding() && this.getMount() != mob) {
         this.dismount();
      }

      if (mob != null) {
         this.mountSetMounterPos = setMounterPos;
         if (setMounterPos) {
            if (mob.collidesWith(mob.getLevel(), this.getX(), this.getY())) {
               GameLog.debug.println("Looks like mount is colliding, swapping back to original mount position");
               mob.setPos(mounterX, mounterY, true);
            } else {
               mob.setPos((float)this.getX(), (float)this.getY(), true);
            }
         } else {
            this.setPos(mob.x, mob.y, true);
         }

         mob.rider = this.getUniqueID();
         this.mount = mob.getUniqueID();
         mob.doMountedLogic();
         this.buffManager.updateBuffs();
         return true;
      } else {
         return false;
      }
   }

   public void updateMount() {
      Mob mount = this.getMount();
      if (mount != null) {
         this.mount(mount, this.mountSetMounterPos);
      } else if (this.isRiding() && this.getLevel() != null) {
         if (this.isClient()) {
            this.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.mount));
         } else {
            this.dismount();
            this.sendMovementPacket(false);
         }
      }

   }

   public void updateRider() {
      Mob rider = this.getRider();
      if (rider != null) {
         rider.mount(this, rider.mountSetMounterPos);
      } else if (this.isMounted() && this.getLevel() != null) {
         if (this.isClient()) {
            this.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.rider));
         } else {
            this.dismounted();
            this.sendMovementPacket(false);
         }
      }

   }

   public void dismount() {
      Mob mount = this.getMount();
      this.mount = -1;
      this.buffManager.updateBuffs();
      if (mount != null) {
         this.dx = mount.dx;
         this.dy = mount.dy;
         mount.rider = -1;
         if (!this.collidesWith(this.getLevel(), this.getX() + 5, this.getY())) {
            this.setPos((float)(this.getX() + 5), (float)this.getY(), true);
         } else if (!this.collidesWith(this.getLevel(), this.getX(), this.getY() + 5)) {
            this.setPos((float)this.getX(), (float)(this.getY() + 5), true);
         } else if (!this.collidesWith(this.getLevel(), this.getX() - 5, this.getY())) {
            this.setPos((float)(this.getX() - 5), (float)this.getY(), true);
         } else if (!this.collidesWith(this.getLevel(), this.getX(), this.getY() - 5)) {
            this.setPos((float)this.getX(), (float)(this.getY() - 5), true);
         }
      }

   }

   public void dismounted() {
      Mob rider = this.getRider();
      if (rider != null) {
         rider.dx = this.dx;
         rider.dy = this.dy;
         rider.mount = -1;
         rider.buffManager.updateBuffs();
      }

      this.rider = -1;
   }

   public int getTeam() {
      Mob mounted = this.getRider();
      if (mounted != null) {
         return mounted.getTeam();
      } else {
         if (this.followingSlot != -1) {
            Object c;
            if (this.isServer()) {
               c = this.getFollowingServerClient();
            } else {
               c = this.getFollowingClientClient();
            }

            if (c != null && ((NetworkClient)c).playerMob != null) {
               return ((NetworkClient)c).playerMob.getTeam();
            }
         }

         return this.team;
      }
   }

   public boolean isFollowing() {
      return this.followingSlot != -1;
   }

   public void setFollowing(ServerClient client, boolean sendUpdatePacket) {
      if (client == null) {
         this.followingSlot = -1;
      } else {
         this.followingSlot = client.slot;
      }

      if (sendUpdatePacket) {
         this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobFollowUpdate(this.getUniqueID(), this.followingSlot), this);
      }

   }

   public ServerClient getFollowingServerClient() {
      return !this.isFollowing() ? null : this.getLevel().getServer().getClient(this.followingSlot);
   }

   public PlayerMob getFollowingServerPlayer() {
      ServerClient c = this.getFollowingServerClient();
      return c != null ? c.playerMob : null;
   }

   public ClientClient getFollowingClientClient() {
      return !this.isFollowing() ? null : this.getLevel().getClient().getClient(this.followingSlot);
   }

   public PlayerMob getFollowingClientPlayer() {
      ClientClient c = this.getFollowingClientClient();
      return c != null ? c.playerMob : null;
   }

   public PlayerMob getFollowingPlayer() {
      if (this.isServer()) {
         return this.getFollowingServerPlayer();
      } else {
         return this.isClient() ? this.getFollowingClientPlayer() : null;
      }
   }

   public void onFollowingAnotherLevel(PlayerMob player) {
      this.getLevel().entityManager.changeMobLevel(this, player.getLevel(), player.getX(), player.getY(), true);
   }

   public void applyFollowUpdatePacket(PacketMobFollowUpdate packet) {
      this.refreshClientUpdateTime();
      this.followingSlot = packet.followingSlot;
   }

   public boolean isSameTeam(Mob other) {
      if (this.getTeam() != -1 && other.getTeam() != -1) {
         return this.getTeam() == other.getTeam();
      } else {
         return false;
      }
   }

   public void setTeam(int team) {
      this.team = team;
   }

   public int getBobbing() {
      return this.getBobbing(this.getX(), this.getY());
   }

   public int getBobbing(int x, int y) {
      Mob mount = this.getMount();
      if (mount != null) {
         return mount.getBobbing(x, y);
      } else if (!this.inLiquid(x, y)) {
         return 0;
      } else if (this.dx == 0.0F && this.dy == 0.0F) {
         return this.getLevel().getLevelTile(x / 32, y / 32).getLiquidBobbing();
      } else {
         int rock = this.getWaterRockSpeed();
         int halfRock = rock / 2;
         if (halfRock == 0) {
            return 0;
         } else if (this.dir == 0) {
            return y % rock / halfRock;
         } else if (this.dir == 1) {
            return x % rock / halfRock;
         } else if (this.dir == 2) {
            return y % rock / halfRock;
         } else {
            return this.dir == 3 ? x % rock / halfRock : 0;
         }
      }
   }

   protected int getRockSpeed() {
      return 15;
   }

   protected int getWaterRockSpeed() {
      return (int)(this.getSpeed() * 2.0F);
   }

   public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
      if (!this.removed()) {
         this.buffManager.addBuff(buff, sendUpdatePacket);
      }
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.empty();
   }

   public Stream<ModifierValue<?>> getDefaultRiderModifiers() {
      return Stream.empty();
   }

   public Point getPathMoveOffset() {
      return new Point(16, 16);
   }

   public final void setSpeed(float speed) {
      this.speed = speed;
   }

   public void setSwimSpeed(float swimSpeed) {
      this.swimSpeed = swimSpeed;
   }

   public void setFriction(float friction) {
      this.friction = friction;
   }

   public void addAttackers(Collection<Attacker> attackers) {
      this.attackers.addAll(attackers);
   }

   public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
      if (this.canTakeDamage() || health >= this.getHealth()) {
         int beforeHealth = this.getHealth();
         int maxHealth = this.getMaxHealth();
         if (beforeHealth == maxHealth) {
            WorldEntity worldEntity = this.getWorldEntity();
            if (worldEntity != null) {
               this.combatStartTime = worldEntity.getTime();
            }
         }

         this.health = Math.min(health, maxHealth);
         if (this.health == maxHealth) {
            this.attackers.clear();
         }

         MobHealthChangedEvent healthChangedEvent = new MobHealthChangedEvent(beforeHealth, this.health, fromNetworkUpdate);
         if (this.getLevel() != null && beforeHealth != this.health) {
            this.getLevel().entityManager.componentManager.streamAll(MobHealthChangeListenerEntityComponent.class).forEach((listener) -> {
               listener.onLevelMobHealthChanged(this, beforeHealth, this.health, knockbackX, knockbackY, attacker);
            });
         }

         this.buffManager.submitMobEvent(healthChangedEvent);
         if (this.isServer()) {
            int damage = beforeHealth - this.getHealth();
            if (damage > 0) {
               if (this.countStats && this.isPlayer) {
                  PlayerMob pl = (PlayerMob)this;
                  if (pl.getServerClient() != null) {
                     pl.getServerClient().newStats.damage_taken.increment(damage);
                  }
               }

               if (attacker != null) {
                  this.attackers.add(attacker);
                  Iterator var13 = attacker.getAttackOwnerChain().iterator();

                  while(var13.hasNext()) {
                     Mob mob = (Mob)var13.next();
                     this.attackers.add(mob);
                     if (this.countStats && this.countDamageDealt() && mob.isPlayer) {
                        ((PlayerMob)mob).getServerClient().newStats.damage_dealt.increment(damage);
                     }
                  }
               }
            }
         }

         if (!this.isClient() && this.getHealth() <= 0) {
            this.remove(knockbackX, knockbackY, attacker);
         }

      }
   }

   public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker) {
      this.setHealthHidden(health, knockbackX, knockbackY, attacker, false);
   }

   public final void setHealthHidden(int health) {
      this.setHealthHidden(health, 0.0F, 0.0F, (Attacker)null);
   }

   public void setHealth(int health, float knockbackX, float knockbackY, Attacker attacker) {
      if (this.canTakeDamage()) {
         this.setHealthHidden(health, knockbackX, knockbackY, attacker);
         if (this.getLevel() != null && this.isServer()) {
            this.sendHealthPacket(false);
         }

      }
   }

   public final void setHealth(int health) {
      this.setHealth(health, 0.0F, 0.0F, (Attacker)null);
   }

   public final void setHealth(int health, Attacker attacker) {
      this.setHealth(health, 0.0F, 0.0F, attacker);
   }

   public void setResilienceHidden(float resilience) {
      if (resilience >= this.getResilience()) {
         this.resilienceGainedTime = this.getTime();
      }

      this.resilience = GameMath.limit(resilience, 0.0F, (float)this.getMaxResilience());
   }

   public void addResilienceHidden(float resilience) {
      if (resilience > 0.0F) {
         this.setResilienceHidden(this.getResilience() + resilience * (Float)this.buffManager.getModifier(BuffModifiers.RESILIENCE_GAIN));
      } else {
         this.setResilienceHidden(this.getResilience() + resilience);
      }

   }

   public void addResilience(float resilience) {
      this.addResilienceHidden(resilience);
      if (this.getLevel() != null && this.isServer()) {
         this.sendResiliencePacket(false);
      }

   }

   public boolean canGiveResilience(Attacker attacker) {
      return true;
   }

   public int getStopManaExhaustLimit() {
      return Math.max(this.getMaxMana() / 20, 1);
   }

   public void setManaHidden(float mana) {
      int maxMana = this.getMaxMana();
      this.mana = GameMath.limit(mana, 0.0F, (float)maxMana);
      int stopExhaustionLimit = this.getStopManaExhaustLimit();
      if (this.mana >= (float)stopExhaustionLimit) {
         this.isManaExhausted = false;
         if (this.buffManager.hasBuff(Debuffs.MANA_EXHAUSTION)) {
            this.buffManager.removeBuff(Debuffs.MANA_EXHAUSTION, false);
         }
      }

   }

   public void setMana(float mana) {
      this.setManaHidden(mana);
      if (this.getLevel() != null && this.isServer()) {
         this.sendManaPacket(false);
      }

   }

   public void useMana(float usedMana, ServerClient except) {
      if (this.usesMana()) {
         float modifiedUsedMana = usedMana * (Float)this.buffManager.getModifier(BuffModifiers.MANA_USAGE);
         if (modifiedUsedMana > 0.0F) {
            this.lastManaSpentTime = this.getWorldEntity().getTime();
            float finalMana = this.getMana() - modifiedUsedMana;
            this.setManaHidden(finalMana);
            if (finalMana <= 0.0F) {
               this.isManaExhausted = true;
               this.buffManager.addBuff(new ActiveBuff(Debuffs.MANA_EXHAUSTION, this, 1000, (Attacker)null), false);
            }

            if (this.getLevel() != null && this.isServer()) {
               if (except != null) {
                  this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketMobUseMana(this), this, except);
               } else {
                  this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobUseMana(this), this);
               }
            }
         }

      }
   }

   public void useLife(int usedLife, ServerClient except, GameMessage attackerName) {
      this.useLife(usedLife, except, (Attacker)(new 3(this, attackerName)));
   }

   public void useLife(int usedLife, ServerClient except, Attacker attacker) {
      if (usedLife > 0) {
         this.useLife(this.getHealth() - usedLife, usedLife, except, attacker);
      }
   }

   public void useLife(int currentLife, int usedLife, ServerClient except, Attacker attacker) {
      this.setHealth(currentLife, attacker);
      if (this.getLevel() != null) {
         if (this.isServer()) {
            if (except != null) {
               this.getLevel().getServer().network.sendToClientsAtExcept(new PacketMobUseLife(this, usedLife), this.getLevel(), except);
            } else {
               this.getLevel().getServer().network.sendToClientsAt(new PacketMobUseLife(this, usedLife), this.getLevel());
            }
         } else {
            this.spawnDamageText(usedLife, 12, false);
         }
      }

   }

   public final void remove() {
      this.remove(0.0F, 0.0F, (Attacker)null);
   }

   public final void remove(float knockbackX, float knockbackY, Attacker attacker) {
      this.remove(knockbackX, knockbackY, attacker, this.getHealth() <= 0);
   }

   public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
      if (!this.removed()) {
         super.remove();
         this.hasDied = this.hasDied || isDeath;
         this.buffManager.clearBuffs();
         this.ai.isRemoved();
         if (this.getLevel() != null) {
            if (this.isServer()) {
               if (this.isFollowing()) {
                  ServerClient c = this.getFollowingServerClient();
                  if (c != null) {
                     c.removeFollower(this, false);
                  }
               }

               if (isDeath) {
                  this.onDeath(attacker, this.attackers);
               }

               this.getLevel().getServer().network.sendToClientsWithEntity(new PacketDeath(this, knockbackX, knockbackY, isDeath), this);
            } else if (isDeath) {
               this.spawnDeathParticles(knockbackX, knockbackY);
               this.playDeathSound();
            } else {
               this.spawnRemoveParticles(knockbackX, knockbackY);
            }
         }

         this.dismount();
         this.dismounted();
      }

   }

   public void onRemovedFromManager() {
      super.onRemovedFromManager();
      this.removedTime = this.getTime();
   }

   protected Iterable<Attacker> getAttackers() {
      return this.attackers;
   }

   protected Stream<Attacker> streamAttackers() {
      return this.attackers.stream();
   }

   public Point getLootDropsPosition(ServerClient privateClient) {
      return new Point(this.getX(), this.getY());
   }

   public int getTileWanderPriority(TilePosition pos) {
      if (!pos.isLiquidTile() && !pos.isShore()) {
         return 0;
      } else {
         int height = pos.level.liquidManager.getHeight(pos.tileX, pos.tileY);
         return -1000 + height;
      }
   }

   protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
      Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
      if (attackOwner != null) {
         attackOwner.doHasKilledTarget(new MobWasKilledEvent(this, attacker));
      }

      this.getLevel().onMobDied(this, attacker, attackers);
      this.itemsDropped.clear();
      boolean dropsLoot = this.dropsLoot();
      if (dropsLoot) {
         ArrayList<InventoryItem> drops = this.getLootTable().getNewList(GameRandom.globalRandom, (Float)this.getLevel().buffManager.getModifier(LevelModifiers.LOOT), new Object[]{this});
         this.getLevel().getExtraMobDrops(this).addItems(drops, GameRandom.globalRandom, (Float)this.getLevel().buffManager.getModifier(LevelModifiers.LOOT), new Object[]{this});
         LootTablePresets.globalMobDrops.addItems(drops, GameRandom.globalRandom, (Float)this.getLevel().buffManager.getModifier(LevelModifiers.LOOT), new Object[]{this});
         GameSeasons.addMobDrops(this, drops, GameRandom.globalRandom, (Float)this.getLevel().buffManager.getModifier(LevelModifiers.LOOT));
         Point publicLootPosition = this.getLootDropsPosition((ServerClient)null);
         publicLootPosition.x = GameMath.limit(publicLootPosition.x, 32, this.getLevel().width * 32 - 32);
         publicLootPosition.y = GameMath.limit(publicLootPosition.y, 32, this.getLevel().height * 32 - 32);
         MobLootTableDropsEvent dropEvent;
         GameEvents.triggerEvent(dropEvent = new MobLootTableDropsEvent(this, publicLootPosition, drops));
         if (dropEvent.dropPos != null && dropEvent.drops != null) {
            Iterator var8 = dropEvent.drops.iterator();

            while(var8.hasNext()) {
               InventoryItem item = (InventoryItem)var8.next();
               ItemPickupEntity entity = item.getPickupEntity(this.getLevel(), (float)dropEvent.dropPos.x, (float)dropEvent.dropPos.y);
               if (this.isBoss()) {
                  entity.showsLightBeam = true;
               }

               this.getLevel().entityManager.pickups.add(entity);
               this.itemsDropped.add(entity);
            }
         }
      }

      attackers.stream().map(Attacker::getAttackOwner).filter((m) -> {
         return m != null && m.isPlayer;
      }).distinct().forEach((m) -> {
         ServerClient client = ((PlayerMob)m).getServerClient();
         if (this.countStats && this.countKillStat()) {
            client.newStats.mob_kills.addKill(this);
         }

         if (this.isBoss() && client.achievementsLoaded() && this.getTimeSinceStartCombat() < 30000L) {
            client.achievements().TOO_EASY.markCompleted(client);
         }

         if (dropsLoot) {
            ArrayList<InventoryItem> privateDrops = this.getPrivateLootTable().getNewList(GameRandom.globalRandom, (Float)this.getLevel().buffManager.getModifier(LevelModifiers.LOOT), new Object[]{this, client});
            client.addQuestDrops(privateDrops, this, GameRandom.globalRandom);
            this.getLevel().getExtraPrivateMobDrops(this, client).addItems(privateDrops, GameRandom.globalRandom, (Float)this.getLevel().buffManager.getModifier(LevelModifiers.LOOT), new Object[]{this, client});
            Point privateLootPosition = this.getLootDropsPosition(client);
            privateLootPosition.x = GameMath.limit(privateLootPosition.x, 32, this.getLevel().width * 32 - 32);
            privateLootPosition.y = GameMath.limit(privateLootPosition.y, 32, this.getLevel().height * 32 - 32);
            MobPrivateLootTableDropsEvent privateDropEvent;
            GameEvents.triggerEvent(privateDropEvent = new MobPrivateLootTableDropsEvent(this, client, privateLootPosition, privateDrops));
            ItemPickupEntity pickupEntity;
            if (privateDropEvent.dropPos != null && privateDropEvent.drops != null) {
               for(Iterator var7 = privateDropEvent.drops.iterator(); var7.hasNext(); this.getLevel().entityManager.pickups.add(pickupEntity)) {
                  InventoryItem item = (InventoryItem)var7.next();
                  pickupEntity = item.copy(item.getAmount()).getPickupEntity(this.getLevel(), (float)privateDropEvent.dropPos.x, (float)privateDropEvent.dropPos.y);
                  pickupEntity.setReservedAuth(client.authentication);
                  if (this.isBoss()) {
                     pickupEntity.showsLightBeam = true;
                  }
               }
            }
         }

      });
   }

   public void restore() {
      super.restore();
      this.attackers.clear();
      this.hasDied = false;
   }

   public boolean hasDied() {
      return this.hasDied;
   }

   public final boolean countKillStat() {
      return MobRegistry.countMobKillStat(this.getID());
   }

   public boolean countDamageDealt() {
      return true;
   }

   public void setArmor(int armor) {
      this.armor = armor;
   }

   public void setMaxHealth(int maxHealth) {
      this.maxHealth = maxHealth;
   }

   public void setMaxResilience(int maxResilience) {
      this.maxResilience = maxResilience;
   }

   public void setMaxMana(int maxMana) {
      this.maxMana = maxMana;
   }

   public void setRegen(float regen) {
      this.regen = regen;
   }

   public void setCombatRegen(float combatRegen) {
      this.combatRegen = combatRegen;
   }

   public void setResilienceDecay(float decay) {
      this.resilienceDecay = decay;
   }

   public void setManaRegen(float manaRegen) {
      this.manaRegen = manaRegen;
   }

   public void setCombatManaRegen(float combatManaRegen) {
      this.combatManaRegen = combatManaRegen;
   }

   public void setKnockbackModifier(float knockbackResistance) {
      this.knockbackModifier = knockbackResistance;
   }

   public float getCritChance() {
      return (Float)this.buffManager.getModifier(BuffModifiers.CRIT_CHANCE);
   }

   public float getCritDamageModifier() {
      return (Float)this.buffManager.getModifier(BuffModifiers.CRIT_DAMAGE);
   }

   public float getSpeed() {
      return (this.speed + (Float)this.buffManager.getModifier(BuffModifiers.SPEED_FLAT)) * this.getSpeedModifier();
   }

   public float getSpeedModifier() {
      float swimMod = this.inLiquid() && !this.isFlying() ? this.getSwimSpeed() : 1.0F;
      GameTile tile = this.getLevel().getTile(this.getTileX(), this.getTileY());
      GameObject object = this.getLevel().getObject(this.getTileX(), this.getTileY());
      float buffMod = (Float)this.buffManager.getAndApplyModifiers(BuffModifiers.SPEED, new ModifierValue[]{tile.getSpeedModifier(this), object.getSpeedModifier(this)});
      float slowMod = 1.0F - (Float)this.buffManager.getAndApplyModifiers(BuffModifiers.SLOW, new ModifierValue[]{tile.getSlowModifier(this), object.getSlowModifier(this)});
      float attackMod = 1.0F;
      Mob mounted = this.getRider();
      if (mounted != null && mounted.isAttacking) {
         attackMod = mounted.getAttackingMovementModifier();
      } else if (this.isAttacking) {
         attackMod = this.getAttackingMovementModifier();
      }

      return buffMod * attackMod * slowMod * swimMod;
   }

   public float getSwimSpeed() {
      return this.swimSpeed * (Float)this.buffManager.getModifier(BuffModifiers.SWIM_SPEED);
   }

   public float getFriction() {
      float frictionMod = (Float)this.buffManager.getAndApplyModifiers(BuffModifiers.FRICTION, new ModifierValue[]{this.getLevel().getTile(this.getTileX(), this.getTileY()).getFrictionModifier(this), this.getLevel().getObject(this.getTileX(), this.getTileY()).getFrictionModifier(this)});
      return this.friction * frictionMod;
   }

   public final int getMaxHealthFlat() {
      return this.maxHealth;
   }

   public int getMaxHealth() {
      return Math.max(Math.round((float)(this.getMaxHealthFlat() + (Integer)this.buffManager.getModifier(BuffModifiers.MAX_HEALTH_FLAT)) * this.getMaxHealthModifier()), 1);
   }

   public float getMaxHealthModifier() {
      return (Float)this.buffManager.getModifier(BuffModifiers.MAX_HEALTH);
   }

   public int getHealthUnlimited() {
      return this.health;
   }

   public int getHealth() {
      int maxHealth = this.getMaxHealth();
      if (this.health > maxHealth) {
         this.health = maxHealth;
      } else if (this.health < 0) {
         this.health = 0;
      }

      return this.health;
   }

   public float getHealthPercent() {
      return (float)this.getHealth() / (float)this.getMaxHealth();
   }

   public int getMaxResilienceFlat() {
      return this.maxResilience;
   }

   public float getResilience() {
      if (this.resilience > (float)this.getMaxResilience()) {
         this.resilience = (float)this.getMaxResilience();
      } else if (this.resilience < 0.0F) {
         this.resilience = 0.0F;
      }

      return this.resilience;
   }

   public int getMaxResilience() {
      return (int)((float)this.getMaxResilienceFlat() + (float)(Integer)this.buffManager.getModifier(BuffModifiers.MAX_RESILIENCE_FLAT) * (Float)this.buffManager.getModifier(BuffModifiers.MAX_RESILIENCE));
   }

   public int getMaxManaFlat() {
      return this.maxMana;
   }

   public int getMaxMana() {
      return Math.max((int)((float)(this.getMaxManaFlat() + (Integer)this.buffManager.getModifier(BuffModifiers.MAX_MANA_FLAT)) * (Float)this.buffManager.getModifier(BuffModifiers.MAX_MANA)), 1);
   }

   public float getMana() {
      int maxMana = this.getMaxMana();
      if (this.mana > (float)maxMana) {
         this.mana = (float)maxMana;
      } else if (this.mana < 0.0F) {
         this.mana = 0.0F;
      }

      return this.mana;
   }

   public int getArmorFlat() {
      return this.armor;
   }

   public float getArmor() {
      return (float)(this.getArmorFlat() + (Integer)this.buffManager.getModifier(BuffModifiers.ARMOR_FLAT)) * this.getArmorModifier();
   }

   public float getArmorModifier() {
      return (Float)this.buffManager.getModifier(BuffModifiers.ARMOR);
   }

   public float getArmorAfterPen(float armorPen) {
      return Math.max(0.0F, this.getArmor() - armorPen);
   }

   public float getRegenFlat() {
      return this.regen;
   }

   public float getRegen() {
      return (this.getRegenFlat() + (Float)this.buffManager.getModifier(BuffModifiers.HEALTH_REGEN_FLAT)) * (Float)this.buffManager.getModifier(BuffModifiers.HEALTH_REGEN);
   }

   public float getCombatRegenFlat() {
      return this.combatRegen;
   }

   public float getCombatRegen() {
      return (this.getCombatRegenFlat() + (Float)this.buffManager.getModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT)) * (Float)this.buffManager.getModifier(BuffModifiers.COMBAT_HEALTH_REGEN);
   }

   public float getResilienceDecay() {
      return (this.resilienceDecay + (Float)this.buffManager.getModifier(BuffModifiers.RESILIENCE_DECAY_FLAT)) * (Float)this.buffManager.getModifier(BuffModifiers.RESILIENCE_DECAY);
   }

   public float getResilienceRegen() {
      return (Float)this.buffManager.getModifier(BuffModifiers.RESILIENCE_REGEN_FLAT) * (Float)this.buffManager.getModifier(BuffModifiers.RESILIENCE_REGEN);
   }

   public float getManaRegenFlat() {
      return this.manaRegen;
   }

   public float getManaRegen() {
      return (this.getManaRegenFlat() + (Float)this.buffManager.getModifier(BuffModifiers.MANA_REGEN_FLAT)) * (Float)this.buffManager.getModifier(BuffModifiers.MANA_REGEN);
   }

   public float getCombatManaRegenFlat() {
      return this.combatManaRegen;
   }

   public float getCombatManaRegen() {
      return (this.getCombatManaRegenFlat() + (Float)this.buffManager.getModifier(BuffModifiers.COMBAT_MANA_REGEN_FLAT)) * (Float)this.buffManager.getModifier(BuffModifiers.COMBAT_MANA_REGEN);
   }

   public float getPoisonTotal() {
      return (Float)this.buffManager.getModifier(BuffModifiers.POISON_DAMAGE_FLAT) * (Float)this.buffManager.getModifier(BuffModifiers.POISON_DAMAGE);
   }

   public float getFireDamageTotal() {
      return (Float)this.buffManager.getModifier(BuffModifiers.FIRE_DAMAGE_FLAT) * (Float)this.buffManager.getModifier(BuffModifiers.FIRE_DAMAGE);
   }

   public float getFrostDamageTotal() {
      return (Float)this.buffManager.getModifier(BuffModifiers.FROST_DAMAGE_FLAT) * (Float)this.buffManager.getModifier(BuffModifiers.FROST_DAMAGE);
   }

   public float getAccelerationModifier() {
      return this.accelerationMod * (Float)this.buffManager.getModifier(BuffModifiers.ACCELERATION);
   }

   public float getDecelerationModifier() {
      return this.decelerationMod * (Float)this.buffManager.getModifier(BuffModifiers.DECELERATION);
   }

   public float getKnockbackModifier() {
      return Math.max(0.0F, this.knockbackModifier * (Float)this.buffManager.getModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD));
   }

   public float getAttackingMovementModifier() {
      return 0.5F + 0.5F * Math.abs((Float)this.buffManager.getModifier(BuffModifiers.ATTACK_MOVEMENT_MOD) - 1.0F);
   }

   public float getIncomingDamageModifier() {
      return (Float)this.buffManager.getModifier(BuffModifiers.INCOMING_DAMAGE_MOD);
   }

   public float getOutgoingDamageModifier() {
      return (Float)this.buffManager.getModifier(BuffModifiers.ALL_DAMAGE);
   }

   public boolean isWaterWalking() {
      if (!this.overrideMountedWaterWalking) {
         Mob m = this.getRider();
         if (m != null) {
            return m.isWaterWalking();
         }
      }

      return (Boolean)this.buffManager.getModifier(BuffModifiers.WATER_WALKING);
   }

   public boolean isVisible() {
      return true;
   }

   public final Point getAnimSprite() {
      return this.getAnimSprite(this.getX(), this.getY(), this.dir);
   }

   public Point getAnimSprite(int x, int y, int dir) {
      Point p = new Point(0, dir);
      if (!this.inLiquid(x, y)) {
         if (this.dx == 0.0F & this.dy == 0.0F) {
            p.x = 0;
         } else {
            p.x = (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 4 + 1;
         }
      } else {
         p.x = 5;
      }

      return p;
   }

   public final Point getSpriteOffset(Point sprite) {
      return this.getSpriteOffset(sprite.x, sprite.y);
   }

   public Point getSpriteOffset(int spriteX, int spriteY) {
      Point p = new Point(0, 0);
      if (spriteY == 1 || spriteY == 3) {
         p.y = 2;
      }

      p.x += this.getRiderDrawXOffset();
      p.y += this.getRiderDrawYOffset();
      return p;
   }

   public int getRiderDir(int startDir) {
      return startDir;
   }

   public boolean canLevelInteract() {
      return !this.isStatic;
   }

   public boolean canTakeDamage() {
      return !this.isStatic;
   }

   public boolean usesMana() {
      return false;
   }

   public int getRiderDrawXOffset() {
      return 0;
   }

   public int getRiderDrawYOffset() {
      return -32;
   }

   public GameTexture getRiderMask() {
      return null;
   }

   public int getRiderMaskXOffset() {
      return 0;
   }

   public int getRiderMaskYOffset() {
      return 0;
   }

   public int getRiderArmSpriteX() {
      return 2;
   }

   public boolean shouldSave() {
      return this.shouldSave;
   }

   public boolean isBoss() {
      return MobRegistry.isBossMob(this.getID());
   }

   public boolean isLavaImmune() {
      return this.isBoss();
   }

   public boolean isSlimeImmune() {
      return this.isLavaImmune();
   }

   public boolean isOnGenericCooldown(String key) {
      return this.getGenericCooldownLeft(key) > 0L;
   }

   public void startGenericCooldown(String key, long cooldownMillis) {
      this.genericCooldowns.put(key, this.getTime() + cooldownMillis);
   }

   public long getGenericCooldownLeft(String key) {
      return (Long)this.genericCooldowns.getOrDefault(key, 0L) - this.getTime();
   }

   protected String getInteractTip(PlayerMob perspective, boolean debug) {
      return Localization.translate("controls", "interacttip");
   }

   public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
      if (!debug && !this.isVisible()) {
         return false;
      } else {
         ListGameTooltips tips = new ListGameTooltips();
         this.addHoverTooltips(tips, debug);
         if (debug) {
            if (!Screen.isKeyDown(340)) {
               this.addDebugTooltips(tips);
            } else {
               List<FairTypeTooltip> modTips = (List)this.buffManager.getModifierTooltips().stream().map((mf) -> {
                  return mf.toTooltip(true);
               }).collect(Collectors.toList());
               if (modTips.isEmpty()) {
                  tips.add(new StringTooltips(Localization.translate("bufftooltip", "nomodifiers"), GameColor.YELLOW));
               } else {
                  tips.addAll(modTips);
               }
            }
         }

         if (this.canInteract(perspective)) {
            Screen.setCursor(CURSOR.INTERACT);
            if (Settings.showControlTips) {
               String controlMsg = this.getInteractTip(perspective, debug);
               if (controlMsg != null) {
                  tips.add(new InputTooltip(Control.MOUSE2, controlMsg, this.inInteractRange(perspective) ? 1.0F : 0.7F));
               }
            }
         }

         Screen.addTooltip(tips, TooltipLocation.INTERACT_FOCUS);
         return true;
      }
   }

   protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
      if (this.canTakeDamage() && this.getMaxHealth() > 1) {
         tooltips.add(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
      } else {
         tooltips.add(this.getDisplayName());
      }

   }

   protected void addDebugTooltips(ListGameTooltips tooltips) {
      tooltips.add("Name: " + this.getDisplayName());
      tooltips.add("UniqueID: " + this.getRealUniqueID());
      tooltips.add("Health: " + this.getHealth() + "/" + this.getMaxHealth());
      tooltips.add("Armor: " + this.getArmor() + " (" + this.getArmorFlat() + ")");
      tooltips.add("Pos: " + this.getX() + ", " + this.getY());
      tooltips.add("Speed: " + this.getCurrentSpeed());
      tooltips.add("Movement: " + this.currentMovement + ", " + this.hasArrivedAtTarget);
      tooltips.add("Team: " + this.getTeam());
      Iterator var2 = this.buffManager.getBuffs().values().iterator();

      while(var2.hasNext()) {
         ActiveBuff ab = (ActiveBuff)var2.next();
         ab.addDebugTooltips(tooltips);
      }

   }

   public String toString() {
      return super.toString() + "{" + this.getUniqueID() + ", " + this.getHostString() + ", " + this.getDisplayName() + "}";
   }
}
