package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import necesse.engine.AreaFinder;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.events.players.ObjectInteractEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketActiveSetBuffAbility;
import necesse.engine.network.packet.PacketActiveSetBuffAbilityStopped;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbilityStopped;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.packet.PacketObjectInteract;
import necesse.engine.network.packet.PacketPlayerAction;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerAttack;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.engine.network.packet.PacketPlayerDropItem;
import necesse.engine.network.packet.PacketPlayerHunger;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerItemInteract;
import necesse.engine.network.packet.PacketPlayerItemMobInteract;
import necesse.engine.network.packet.PacketPlayerMobInteract;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerUseMount;
import necesse.engine.network.packet.PacketRequestActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketShowItemLevelInteract;
import necesse.engine.network.packet.PacketShowItemMobInteract;
import necesse.engine.network.packet.PacketSwapInventorySlots;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.sound.SoundEffect;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.ActiveBuffAbilityContainer;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.FoodBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.PlayerSprite;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ControllerInteractTarget;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.item.FishingPoleHolding;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.TorchItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.hudManager.floatText.DamageText;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.level.maps.light.GameLight;

public class PlayerMob extends AttackAnimMob implements FishingMob, HungerMob, ObjectUserMob {
   public static int distanceToRunAtFullHunger = 57600;
   public static int secondsToPassAtFullHunger = 10800;
   public static float attacksHungerModifier = 1.0F;
   public static int spawnInvincibilityFrameMilliseconds = 2000;
   public HumanLook look;
   public InventoryItem attackingItem;
   public boolean isInteractAttack;
   public int animAttack;
   private int animAttackTotal;
   private long animAttackCooldown;
   public boolean constantAttack;
   public boolean constantInteract;
   public PlayerInventorySlot attackSlot;
   private AttackHandler attackHandler;
   public final HashSet<Projectile> boomerangs = new HashSet();
   public final HashMap<Integer, Long> toolHits = new HashMap();
   public int beforeAttackDir;
   private boolean firstPress;
   private PlayerInventoryManager inv;
   private boolean inventoryExtended;
   private int selectedSlot;
   private boolean inventoryActionUpdate;
   private boolean maxManaReached;
   private long timeOfMaxMana;
   public EquipmentBuffManager equipmentBuffManager = new EquipmentBuffManager(this) {
      public InventoryItem getArmorItem(int var1) {
         return PlayerMob.this.getInv().armor.getItem(var1);
      }

      public InventoryItem getCosmeticItem(int var1) {
         return PlayerMob.this.getInv().cosmetic.getItem(var1);
      }

      public ArrayList<InventoryItem> getTrinketItems() {
         PlayerInventoryManager var1 = PlayerMob.this.getInv();
         ArrayList var2 = new ArrayList(var1.trinkets.getSize() + 1);
         var2.add(var1.equipment.getItem(1));

         for(int var3 = 0; var3 < var1.trinkets.getSize(); ++var3) {
            var2.add(var1.trinkets.getItem(var3));
         }

         return var2;
      }
   };
   private ActiveBuffAbilityContainer activeSetBuffAbility;
   private ActiveBuffAbilityContainer activeTrinketBuffAbility;
   private HashMap<String, ItemCooldown> itemCooldowns;
   private NetworkClient networkClient;
   public String playerName;
   public float hungerLevel = 1.3F;
   protected double lastDistanceRan;
   public boolean autoOpenDoors = true;
   public boolean hotbarLocked = false;
   private boolean smartMiningTogglePressed = false;
   private float lastControllerAimX;
   private float lastControllerAimY;
   private int refreshRemainingSpawnTime;
   private long spawnInvincibilityEndTime;
   protected ObjectUserActive objectUser;
   private static final int angleSnap = 5;
   public static Attacker STARVING_ATTACKER = new Attacker() {
      public GameMessage getAttackerName() {
         return new LocalMessage("deaths", "starvationname");
      }

      public DeathMessageTable getDeathMessages() {
         return this.getDeathMessages("starvation", 2);
      }

      public Mob getFirstAttackOwner() {
         return null;
      }
   };

   public PlayerMob(long var1, NetworkClient var3) {
      super(100);
      this.playerName = "player" + var1;
      this.networkClient = var3;
      if (var3 != null) {
         this.setTeam(var3.getTeamID());
      }

      this.look = new HumanLook();
      this.resetInv();
      this.dir = 2;
      this.setSpeed(40.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.hitCooldown = 500;
      this.attackTime = 0L;
      this.hitTime = 0L;
      this.itemCooldowns = new HashMap();
      this.beforeAttackDir = -1;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addBoolean("inventoryExtended", this.inventoryExtended);
      var1.addBoolean("autoOpenDoors", this.autoOpenDoors);
      var1.addBoolean("hotbarLocked", this.hotbarLocked);
      var1.addFloat("hungerLevel", this.hungerLevel);
      SaveData var2 = new SaveData("LOOK");
      this.look.addSaveData(var2);
      var1.addSaveData(var2);
      SaveData var3 = new SaveData("INVENTORY");
      this.getInv().addSaveData(var3);
      var1.addSaveData(var3);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventoryExtended = var1.getBoolean("inventoryExtended", this.inventoryExtended, false);
      this.autoOpenDoors = var1.getBoolean("autoOpenDoors", this.autoOpenDoors, false);
      this.hotbarLocked = var1.getBoolean("hotbarLocked", this.hotbarLocked, false);
      this.hungerLevel = var1.getFloat("hungerLevel", 1.0F, 0.0F, Float.MAX_VALUE, false);
      LoadData var2 = var1.getFirstLoadDataByName("LOOK");
      if (var2 != null) {
         this.look.applyLoadData(var2);
      } else {
         GameLog.warn.println("Could not load player look data");
      }

      LoadData var3 = var1.getFirstLoadDataByName("INVENTORY");
      if (var3 != null) {
         this.getInv().applyLoadData(var3);
      } else {
         GameLog.warn.println("Could not load player inventory data");
      }

      this.getInv().clean();
      this.openedDoors.clear();
   }

   public void addLoadedCharacterSaveData(SaveData var1) {
      var1.addInt("maxHealth", this.getMaxHealthFlat());
      var1.addInt("health", this.getHealth());
      var1.addInt("maxResilience", this.getMaxResilienceFlat());
      var1.addFloat("resilience", this.getResilience());
      var1.addInt("maxMana", this.getMaxManaFlat());
      var1.addFloat("mana", this.getMana());
      var1.addBoolean("inventoryExtended", this.inventoryExtended);
      var1.addBoolean("autoOpenDoors", this.autoOpenDoors);
      var1.addBoolean("hotbarLocked", this.hotbarLocked);
      var1.addFloat("hungerLevel", this.hungerLevel);
      SaveData var2 = new SaveData("BUFFS");
      this.buffManager.addSaveData(var2);
      var1.addSaveData(var2);
      SaveData var3 = new SaveData("LOOK");
      this.look.addSaveData(var3);
      var1.addSaveData(var3);
      SaveData var4 = new SaveData("INVENTORY");
      this.getInv().addSaveData(var4);
      var1.addSaveData(var4);
   }

   public void applyLoadedCharacterLoadData(LoadData var1) {
      this.setMaxHealth(var1.getInt("maxHealth", this.getMaxHealthFlat()));
      this.loadedHealth = var1.getInt("health", this.getMaxHealthFlat(), false);
      this.setHealthHidden(this.loadedHealth);
      this.setMaxResilience(var1.getInt("maxResilience", this.getMaxResilienceFlat()));
      this.loadedResilience = var1.getFloat("resilience", 0.0F, false);
      this.setResilienceHidden(this.loadedResilience);
      this.setMaxMana(var1.getInt("maxMana", this.getMaxManaFlat()));
      this.loadedMana = var1.getFloat("mana", (float)this.getMaxManaFlat(), false);
      this.setManaHidden(this.loadedMana);
      this.inventoryExtended = var1.getBoolean("inventoryExtended", this.inventoryExtended, false);
      this.autoOpenDoors = var1.getBoolean("autoOpenDoors", this.autoOpenDoors, false);
      this.hotbarLocked = var1.getBoolean("hotbarLocked", this.hotbarLocked, false);
      this.hungerLevel = var1.getFloat("hungerLevel", 1.0F, 0.0F, Float.MAX_VALUE, false);
      LoadData var2 = var1.getFirstLoadDataByName("BUFFS");
      if (var2 != null) {
         this.buffManager.applyLoadData(var2);
      }

      LoadData var3 = var1.getFirstLoadDataByName("LOOK");
      if (var3 != null) {
         this.look.applyLoadData(var3);
      } else {
         GameLog.warn.println("Could not load player look data");
      }

      LoadData var4 = var1.getFirstLoadDataByName("INVENTORY");
      if (var4 != null) {
         this.getInv().applyLoadData(var4);
      } else {
         GameLog.warn.println("Could not load player inventory data");
      }

      this.getInv().clean();
      this.equipmentBuffManager.updateAll();
   }

   public void setupLoadedCharacterPacket(PacketWriter var1) {
      var1.putNextInt(this.getMaxHealthFlat());
      var1.putNextInt(Math.max(this.getHealth(), this.loadedHealth));
      var1.putNextInt(this.getMaxResilienceFlat());
      var1.putNextFloat(Math.max(this.getResilience(), this.loadedResilience));
      var1.putNextInt(this.getMaxManaFlat());
      var1.putNextFloat(Math.max(this.getMana(), this.loadedMana));
      this.buffManager.setupContentPacket(var1);
      var1.putNextByteUnsigned(this.selectedSlot);
      var1.putNextBoolean(this.inventoryExtended);
      var1.putNextBoolean(this.autoOpenDoors);
      var1.putNextBoolean(this.hotbarLocked);
      this.look.setupContentPacket(var1, true);
      this.getInv().setupContentPacket(var1);
      var1.putNextFloat(this.hungerLevel);
   }

   public void applyLoadedCharacterPacket(PacketReader var1) {
      this.setMaxHealth(var1.getNextInt());
      this.loadedHealth = var1.getNextInt();
      this.setHealthHidden(this.loadedHealth);
      this.setMaxResilience(var1.getNextInt());
      this.loadedResilience = var1.getNextFloat();
      this.setResilienceHidden(this.loadedResilience);
      this.setMaxMana(var1.getNextInt());
      this.loadedMana = var1.getNextFloat();
      this.setManaHidden(this.loadedMana);
      this.buffManager.applyContentPacket(var1);
      this.selectedSlot = var1.getNextByteUnsigned();
      this.inventoryExtended = var1.getNextBoolean();
      this.autoOpenDoors = var1.getNextBoolean();
      this.hotbarLocked = var1.getNextBoolean();
      this.look.applyContentPacket(var1);
      this.getInv().applyContentPacket(var1);
      this.hungerLevel = var1.getNextFloat();
      this.equipmentBuffManager.updateAll();
      this.buffManager.forceUpdateBuffs();
      this.handleLoadedValues();
   }

   public double allowServerMovement(Server var1, ServerClient var2, float var3, float var4) {
      return this.allowServerMovement(var1, var2, var3, var4, this.dx, this.dy);
   }

   public double allowServerMovement(Server var1, ServerClient var2, float var3, float var4, float var5, float var6) {
      if (var2.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel() && var1.world.settings.allowCheats) {
         return -100000.0;
      } else if (Settings.giveClientsPower) {
         return -10000.0;
      } else {
         Point2D.Float var7 = new Point2D.Float(this.x, this.y);
         Point2D.Float var8 = new Point2D.Float(this.dx, this.dy);
         double var9 = var7.distance((double)var3, (double)var4) + var8.distance((double)var5, (double)var6);
         return var9 - (double)Server.clientMoveTolerance;
      }
   }

   public void setupPlayerMovementPacket(PacketWriter var1) {
      var1.putNextMaxValue(this.dir, 3);
      var1.putNextByteUnsigned(this.selectedSlot);
      var1.putNextFloat(this.moveX);
      var1.putNextFloat(this.moveY);
      var1.putNextBoolean(this.autoOpenDoors);
      var1.putNextBoolean(this.hotbarLocked);
      this.writeObjectUserPacket(var1);
      var1.putNextBoolean(this.activeSetBuffAbility != null);
      if (this.activeSetBuffAbility != null) {
         var1.putNextInt(this.activeSetBuffAbility.uniqueID);
      }

      var1.putNextBoolean(this.activeTrinketBuffAbility != null);
      if (this.activeTrinketBuffAbility != null) {
         var1.putNextInt(this.activeTrinketBuffAbility.uniqueID);
      }

   }

   public void applyPlayerMovementPacket(PacketPlayerMovement var1, PacketReader var2) {
      if (var1 != null) {
         this.setPos(var1.x, var1.y, var1.isDirect);
         this.dx = var1.dx;
         this.dy = var1.dy;
      }

      this.refreshClientUpdateTime();
      int var3 = var2.getNextMaxValue(3);
      if (this.isAttacking) {
         this.beforeAttackDir = var3;
      } else {
         this.dir = var3;
      }

      this.selectedSlot = var2.getNextByteUnsigned();
      this.moveX = var2.getNextFloat();
      this.moveY = var2.getNextFloat();
      if (this.isRiding()) {
         Mob var4 = this.getMount();
         if (var4 != null) {
            if (!this.isAttacking) {
               var4.dir = var3;
            }

            var4.setPos(this.x, this.y, true);
            var4.moveX = this.moveX;
            var4.moveY = this.moveY;
         }
      }

      this.autoOpenDoors = var2.getNextBoolean();
      this.hotbarLocked = var2.getNextBoolean();
      this.readObjectUserPacket(var2);
      ClientClient var5;
      int var6;
      if (var2.getNextBoolean()) {
         var6 = var2.getNextInt();
         if ((this.activeSetBuffAbility == null || this.activeSetBuffAbility.uniqueID != var6) && this.isClientClient()) {
            var5 = this.getClientClient();
            var5.getClient().network.sendPacket(new PacketRequestActiveTrinketBuffAbility(var5.slot));
         }
      } else {
         if (this.activeSetBuffAbility != null) {
            this.activeSetBuffAbility.onStopped(this);
         }

         this.activeSetBuffAbility = null;
      }

      if (var2.getNextBoolean()) {
         var6 = var2.getNextInt();
         if ((this.activeTrinketBuffAbility == null || this.activeTrinketBuffAbility.uniqueID != var6) && this.isClientClient()) {
            var5 = this.getClientClient();
            var5.getClient().network.sendPacket(new PacketRequestActiveTrinketBuffAbility(var5.slot));
         }
      } else {
         if (this.activeTrinketBuffAbility != null) {
            this.activeTrinketBuffAbility.onStopped(this);
         }

         this.activeTrinketBuffAbility = null;
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.inventoryExtended = var1.getNextBoolean();
      this.selectedSlot = var1.getNextByteUnsigned();
      this.isAttacking = var1.getNextBoolean();
      if (this.isAttacking) {
         this.attackingItem = InventoryItem.fromContentPacket(var1);
         float var2 = var1.getNextFloat();
         float var3 = var1.getNextFloat();
         this.attackDir = new Point2D.Float(var2, var3);
         this.attackTime = var1.getNextLong();
         this.attackSeed = var1.getNextShortUnsigned();
         this.animAttackTotal = this.attackingItem.item.getAnimAttacks(this.attackingItem);
         this.animAttack = 1;
         this.attackAnimTime = this.attackingItem.item.getAttackAnimTime(this.attackingItem, this);
         this.animAttackCooldown = (long)(this.attackAnimTime / this.animAttackTotal);
         this.attackCooldown = Math.max(this.attackingItem.item.getAttackCooldownTime(this.attackingItem, this), this.attackAnimTime);
         int var4 = this.attackingItem.item.getItemCooldownTime(this.attackingItem, this);
         if (var4 > 0) {
            this.startItemCooldown(this.attackingItem.item, this.attackTime, var4);
         }
      }

      this.look.applyContentPacket(var1);
      this.getInv().applyContentPacket(var1);
      this.hungerLevel = var1.getNextFloat();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.inventoryExtended);
      var1.putNextByteUnsigned(this.selectedSlot);
      var1.putNextBoolean(this.isAttacking && this.attackingItem != null);
      if (this.isAttacking && this.attackingItem != null) {
         this.attackingItem.addPacketContent(var1);
         var1.putNextFloat(this.attackDir.x);
         var1.putNextFloat(this.attackDir.y);
         var1.putNextLong(this.attackTime);
         var1.putNextShortUnsigned(this.attackSeed);
      }

      this.look.setupContentPacket(var1, true);
      this.getInv().setupContentPacket(var1);
      var1.putNextFloat(this.hungerLevel);
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      this.setupPlayerMovementPacket(var1);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.applyPlayerMovementPacket((PacketPlayerMovement)null, var1);
   }

   public void setupHealthPacket(PacketWriter var1, boolean var2) {
      super.setupHealthPacket(var1, var2);
      if (this.isServerClient()) {
         var1.putNextBoolean(this.getServerClient().isDead());
      } else if (this.isClientClient()) {
         var1.putNextBoolean(this.getClientClient().isDead());
      } else {
         var1.putNextBoolean(this.removed());
      }

   }

   public void applyHealthPacket(PacketReader var1, boolean var2) {
      super.applyHealthPacket(var1, var2);
      boolean var3 = var1.getNextBoolean();
      if (this.isClientClient()) {
         ClientClient var4 = this.getClientClient();
         if (var3 != var4.isDead()) {
            if (var3) {
               var4.die(5000);
            } else {
               this.restore();
            }
         }
      } else if (this.isServerClient()) {
         ServerClient var5 = this.getServerClient();
         if (var3 != var5.isDead()) {
            if (var3) {
               var5.die(5000);
            } else {
               this.restore();
            }
         }
      } else if (var3 != this.removed()) {
         if (var3) {
            this.remove();
         } else {
            this.restore();
         }
      }

   }

   public void applyInventoryPacket(PacketPlayerInventory var1) {
      this.refreshClientUpdateTime();
      if (this.getInv() != null) {
         this.getInv().applyContentPacket(new PacketReader(var1.inventoryContent));
      }
   }

   public void applyAppearancePacket(PacketPlayerAppearance var1) {
      this.refreshClientUpdateTime();
      this.look = new HumanLook(var1.look);
      this.inv.giveLookArmor(false);
      this.playerName = var1.name;
   }

   public void init() {
      super.init();
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().customAdder((var0, var1) -> {
         if (var0.tileID() == TileRegistry.emptyID) {
            var1.add(new Rectangle(var0.tileX * 32, var0.tileY * 32, 32, 32));
         }

      });
   }

   public void tickMovement(float var1) {
      if (this.refreshRemainingSpawnTime > 0) {
         this.spawnInvincibilityEndTime = this.getWorldEntity().getLocalTime() + (long)this.refreshRemainingSpawnTime;
         this.refreshRemainingSpawnTime = 0;
      }

      if (this.attackHandler != null) {
         if (this.isClient() && !Control.MOUSE1.isDown()) {
            this.attackHandler.endAttack();
         } else {
            this.attackHandler.tickUpdate(var1);
         }
      }

      if (this.isAttacking || this.onCooldown) {
         this.getAttackAnimProgress();
      }

      super.tickMovement(var1);
   }

   public PathDoorOption getPathDoorOption() {
      Level var1 = this.getLevel();
      if (var1 == null) {
         return null;
      } else {
         return this.autoOpenDoors ? var1.regionManager.CAN_OPEN_DOORS_OPTIONS : var1.regionManager.CANNOT_OPEN_CAN_CLOSE_DOORS_OPTIONS;
      }
   }

   public void serverTick() {
      if (this.objectUser != null) {
         this.objectUser.tick();
      }

      this.tickInventory();
      this.tickHunger();
      this.buffManager.serverTick();
      this.tickRegen();
      this.tickLevel();
      if (this.activeSetBuffAbility != null && !this.activeSetBuffAbility.tick(this)) {
         this.activeSetBuffAbility.onStopped(this);
         this.activeSetBuffAbility = null;
      }

      if (this.activeTrinketBuffAbility != null && !this.activeTrinketBuffAbility.tick(this)) {
         this.activeTrinketBuffAbility.onStopped(this);
         this.activeTrinketBuffAbility = null;
      }

      if (this.isAttacking || this.onCooldown) {
         this.getAttackAnimProgress();
      }

      this.boomerangs.removeIf(Entity::removed);
      InventoryItem var1 = this.getSelectedItem();
      if (var1 != null) {
         var1.item.tickHolding(var1, this);
      }

      if (this.healthUpdateTime + (long)this.healthUpdateCooldown < this.getTime()) {
         this.sendHealthPacket(false);
      }

      if (this.resilienceUpdateTime + (long)this.resilienceUpdateCooldown < this.getTime()) {
         this.sendResiliencePacket(false);
      }

      if (this.manaUpdateTime + (long)this.manaUpdateCooldown < this.getTime()) {
         this.sendManaPacket(false);
      }

      if (this.isManaExhausted) {
         this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, this, 1000, (Attacker)null), false);
         if (this.isManaExhausted) {
            this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, this, 1000, (Attacker)null), false);
         }
      }

   }

   public void clientTick() {
      if (this.objectUser != null) {
         this.objectUser.tick();
      }

      this.tickInventory();
      this.tickHunger();
      this.equipmentBuffManager.clientTickEffects();
      super.clientTick();
      Client var1;
      if (this.activeSetBuffAbility != null && !this.activeSetBuffAbility.tick(this)) {
         if (this.activeSetBuffAbility.isRunningClient) {
            var1 = this.getLevel().getClient();
            var1.network.sendPacket(new PacketActiveSetBuffAbilityStopped(var1.getSlot(), this.activeSetBuffAbility.uniqueID));
         }

         this.activeSetBuffAbility.onStopped(this);
         this.activeSetBuffAbility = null;
      }

      if (this.activeTrinketBuffAbility != null && !this.activeTrinketBuffAbility.tick(this)) {
         if (this.activeTrinketBuffAbility.isRunningClient) {
            var1 = this.getLevel().getClient();
            var1.network.sendPacket(new PacketActiveTrinketBuffAbilityStopped(var1.getSlot(), this.activeTrinketBuffAbility.uniqueID));
         }

         this.activeTrinketBuffAbility.onStopped(this);
         this.activeTrinketBuffAbility = null;
      }

      if (this.isAttacking || this.onCooldown) {
         this.getAttackAnimProgress();
      }

      this.boomerangs.removeIf(Entity::removed);
      InventoryItem var2 = this.getSelectedItem();
      if (var2 != null) {
         var2.item.tickHolding(var2, this);
      }

   }

   public int getRemainingSpawnInvincibilityTime() {
      if (this.refreshRemainingSpawnTime > 0) {
         return this.refreshRemainingSpawnTime;
      } else {
         WorldEntity var1 = this.getWorldEntity();
         return var1 != null ? (int)Math.max(this.spawnInvincibilityEndTime - var1.getLocalTime(), 0L) : 0;
      }
   }

   public boolean canBeHit(Attacker var1) {
      int var2 = this.getRemainingSpawnInvincibilityTime();
      return var2 > 0 ? false : super.canBeHit(var1);
   }

   public void refreshSpawnTime(int var1) {
      this.refreshRemainingSpawnTime = var1;
   }

   public void refreshSpawnTime() {
      this.refreshSpawnTime(spawnInvincibilityFrameMilliseconds);
   }

   public float getInvincibilityFrameAlpha() {
      int var1 = this.getRemainingSpawnInvincibilityTime();
      if (var1 > 0) {
         float var2;
         if (var1 > spawnInvincibilityFrameMilliseconds) {
            var2 = GameUtils.getAnimFloat((long)(var1 - spawnInvincibilityFrameMilliseconds), 200);
            return (float)(Math.sin((double)var2 * Math.PI * 2.0) + 1.0) / 2.0F;
         } else {
            var2 = Math.abs((float)var1 / (float)spawnInvincibilityFrameMilliseconds - 1.0F);
            return (float)Math.abs((Math.cos(18.84955592153876 / (double)(var2 + 0.5F)) + 1.0) / 2.0);
         }
      } else {
         return 1.0F;
      }
   }

   public boolean canBeTargeted(Mob var1, NetworkClient var2) {
      if (!super.canBeTargeted(var1, var2)) {
         return false;
      } else {
         if (var2 != null) {
            NetworkClient var3 = this.getNetworkClient();
            if (var2 == var3) {
               return false;
            }

            if (var3 != null && (!var3.pvpEnabled() || !var2.pvpEnabled())) {
               return false;
            }
         }

         return true;
      }
   }

   public int getBoomerangsUsage() {
      return (int)Math.ceil((double)(Float)this.boomerangs.stream().reduce(0.0F, (var0, var1) -> {
         return var0 + var1.getBoomerangUsage();
      }, Float::sum));
   }

   protected boolean switchDoor(LevelObject var1) {
      if (this.isClient() && this.getLevel().getClient().getPlayer() == this) {
         OpenedDoor var2 = this.openedDoors.get(var1.tileX, var1.tileY);
         if (var2 != null) {
            var2.mobX = this.getX();
            var2.mobY = this.getY();
            return true;
         } else {
            AtomicBoolean var3 = new AtomicBoolean();
            GameEvents.triggerEvent(new ObjectInteractEvent(this.getLevel(), var1.tileX, var1.tileY, this), (var3x) -> {
               if (staticSwitchDoor(var1)) {
                  this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(this.getLevel(), this.getLevel().getClient().getSlot(), var1.tileX, var1.tileY));
                  this.openedDoors.add(var1.tileX, var1.tileY, this.getX(), this.getY(), var1.object.isSwitched);
                  var3.set(true);
               }

            });
            return var3.get();
         }
      } else {
         return false;
      }
   }

   protected void switchDoor(OpenedDoor var1) {
      GameEvents.triggerEvent(new ObjectInteractEvent(this.getLevel(), var1.tileX, var1.tileY, this), (var2) -> {
         LevelObject var3 = new LevelObject(this.getLevel(), var1.tileX, var1.tileY);
         if (staticSwitchDoor(var3)) {
            this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(this.getLevel(), this.getLevel().getClient().getSlot(), var3.tileX, var3.tileY));
         }

      });
   }

   public void tickControls(MainGame var1, boolean var2, GameCamera var3) {
      boolean var4 = (Boolean)this.buffManager.getModifier(BuffModifiers.PARALYZED);
      if (Control.INVENTORY.isPressed()) {
         if (var1.showMap()) {
            var1.setShowMap(false);
         } else if (var1.formManager.getFocusForm() != null) {
            this.getLevel().getClient().closeContainer(true);
         } else {
            this.setInventoryExtended(!this.isInventoryExtended());
         }
      }

      if (Control.USE_MOUNT.isPressed() && this.objectUser == null) {
         InventoryItem var5 = this.inv.equipment.getItem(0);
         if (var5 != null && var5.item.isMountItem()) {
            MountItem var19 = (MountItem)var5.item;
            String var7 = var19.canUseMount(var5, this, this.getLevel());
            if (var7 == null) {
               this.getLevel().getClient().network.sendPacket(new PacketPlayerUseMount(this.getPlayerSlot(), this, var19));
            } else if (!var7.isEmpty()) {
               UniqueFloatText var8 = new UniqueFloatText(this.getX(), this.getY() - 20, var7, (new FontOptions(16)).outline().color(new Color(200, 100, 100)), "mountfail") {
                  public int getAnchorX() {
                     return PlayerMob.this.getX();
                  }

                  public int getAnchorY() {
                     return PlayerMob.this.getY() - 20;
                  }
               };
               this.getLevel().hudManager.addElement(var8);
            }
         } else {
            Mob var6 = this.getMount();
            if (var6 != null) {
               this.getLevel().getClient().network.sendPacket(new PacketPlayerMobInteract(this.getLevel().getClient().getSlot(), var6.getUniqueID()));
            }
         }
      }

      int var38;
      if (!var4 && Control.SET_ABILITY.isPressed()) {
         ObjectValue var17 = this.equipmentBuffManager.getSetBonusBuff();
         if (var17 != null) {
            if (var17.value instanceof BuffAbility) {
               BuffAbility var34 = (BuffAbility)var17.value;
               Packet var42 = var34.getAbilityContent(this, (ActiveBuff)var17.object, var3);
               if (var34.canRunAbility(this, (ActiveBuff)var17.object, var42)) {
                  var34.runAndSendAbility(this.getLevel().getClient(), this, (ActiveBuff)var17.object, var42);
               }

               return;
            }

            if (var17.value instanceof ActiveBuffAbility) {
               ActiveBuffAbility var31 = (ActiveBuffAbility)var17.value;
               ActiveBuff var39 = (ActiveBuff)var17.object;
               Packet var54 = var31.getStartAbilityContent(this, var39, var3);
               if (var31.canRunAbility(this, var39, var54)) {
                  var31.onActiveAbilityStarted(this, var39, var54);
                  var38 = GameRandom.globalRandom.nextInt();
                  if (this.activeSetBuffAbility != null) {
                     this.activeSetBuffAbility.onStopped(this);
                  }

                  this.activeSetBuffAbility = new ActiveBuffAbilityContainer(var38, true, var31, var39);
                  Client var48 = this.getLevel().getClient();
                  var48.network.sendPacket(new PacketActiveSetBuffAbility(var48.getSlot(), var39.buff, var38, var54));
               }

               return;
            }
         }
      }

      int var33;
      if (!var4 && Control.TRINKET_ABILITY.isPressed()) {
         Iterator var30 = this.equipmentBuffManager.getTrinketBuffs().iterator();

         while(var30.hasNext()) {
            ActiveTrinketBuff var28 = (ActiveTrinketBuff)var30.next();

            for(var33 = 0; var33 < var28.buffs.length; ++var33) {
               ActiveBuff var45;
               Packet var47;
               if (var28.buffs[var33] instanceof BuffAbility) {
                  BuffAbility var52 = (BuffAbility)var28.buffs[var33];
                  var45 = var28.activeBuffs[var33];
                  if (var45 != null) {
                     var47 = var52.getAbilityContent(this, var45, var3);
                     if (var52.canRunAbility(this, var45, var47)) {
                        var52.runAndSendAbility(this.getLevel().getClient(), this, var45, var47);
                     }

                     return;
                  }
               } else if (var28.buffs[var33] instanceof ActiveBuffAbility) {
                  ActiveBuffAbility var53 = (ActiveBuffAbility)var28.buffs[var33];
                  var45 = var28.activeBuffs[var33];
                  if (var45 != null) {
                     var47 = var53.getStartAbilityContent(this, var45, var3);
                     if (var53.canRunAbility(this, var45, var47)) {
                        var53.onActiveAbilityStarted(this, var45, var47);
                        int var50 = GameRandom.globalRandom.nextInt();
                        if (this.activeTrinketBuffAbility != null) {
                           this.activeTrinketBuffAbility.onStopped(this);
                        }

                        this.activeTrinketBuffAbility = new ActiveBuffAbilityContainer(var50, true, var53, var45);
                        Client var55 = this.getLevel().getClient();
                        var55.network.sendPacket(new PacketActiveTrinketBuffAbility(var55.getSlot(), var45.buff, var50, var47));
                     }

                     return;
                  }
               }
            }
         }

      } else if (!var4 && Control.HEALTH_POT.isPressed()) {
         this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.USE_HEALTH_POTION));
         this.runPlayerAction(PacketPlayerAction.PlayerAction.USE_HEALTH_POTION);
      } else if (!var4 && Control.MANA_POT.isPressed()) {
         this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.USE_MANA_POTION));
         this.runPlayerAction(PacketPlayerAction.PlayerAction.USE_MANA_POTION);
      } else if (!var4 && Control.EAT_FOOD.isPressed()) {
         this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.EAT_FOOD));
         this.runPlayerAction(PacketPlayerAction.PlayerAction.EAT_FOOD);
      } else if (!var4 && Control.BUFF_POTS.isPressed()) {
         this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.USE_BUFF_POTION));
         this.runPlayerAction(PacketPlayerAction.PlayerAction.USE_BUFF_POTION);
      } else {
         int var12;
         int var14;
         int var18;
         if (!var4 && Control.PLACE_TORCH.isPressed()) {
            for(var18 = 0; var18 < this.getInv().main.getSize(); ++var18) {
               final InventoryItem var21 = this.getInv().main.getItem(var18);
               if (var21 != null && var21.item instanceof TorchItem) {
                  Point var24;
                  if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                     Point2D.Float var32 = this.getControllerAimDir();
                     var24 = var21.item.getControllerAttackLevelPos(this.getLevel(), var32.x, var32.y, this, var21);
                  } else {
                     var24 = new Point(var3.getMouseLevelPosX(), var3.getMouseLevelPosY());
                  }

                  final TorchItem var36 = (TorchItem)var21.item;
                  final Level var9 = this.getLevel();
                  if (var36.canPlaceTorch(var9, var24.x, var24.y, var21, this)) {
                     this.tryAttack(new PlayerInventorySlot(this.getInv().main.getInventoryID(), var18), var24.x, var24.y);
                     return;
                  }

                  int var10 = var36.getTorchPlaceRange(var9, var21, this);
                  if (var10 > 0) {
                     Point2D.Float var11 = GameMath.normalize((float)(var24.x - this.getX()), (float)(var24.y - this.getY()));
                     var12 = (int)((float)this.getX() + var11.x * (float)var10) / 32;
                     int var13 = (int)((float)this.getY() + var11.y * (float)var10) / 32;
                     var14 = (int)Math.ceil((double)((float)var10 / 32.0F));
                     AreaFinder var15 = new AreaFinder(var12, var13, var14, true) {
                        public boolean checkPoint(int var1, int var2) {
                           return var36.canPlaceTorch(var9, var1 * 32 + 16, var2 * 32 + 16, var21, PlayerMob.this);
                        }
                     };
                     var15.runFinder();
                     if (var15.hasFound()) {
                        Point var16 = var15.getFirstFind();
                        this.tryAttack(new PlayerInventorySlot(this.getInv().main.getInventoryID(), var18), var16.x * 32 + 16, var16.y * 32 + 16);
                        return;
                     }
                  }
               }
            }
         }

         if (Control.PIPETTE.isPressed()) {
            Point var29 = new Point(var3.getMouseLevelPosX(), var3.getMouseLevelPosY());
            Level var26 = this.getLevel();
            GameTile var35 = var26.getTile(var29.x / 32, var29.y / 32);
            TileItem var51 = var35.getTileItem();
            GameObject var43 = var26.getObject(var29.x / 32, var29.y / 32);
            ObjectItem var46 = var43.getObjectItem();
            Object var49 = var43.getID() == 0 ? var51 : var46;
            if (var49 != null) {
               for(var12 = 0; var12 < this.getInv().main.getSize(); ++var12) {
                  InventoryItem var56 = this.getInv().main.getItem(var12);
                  if (var56 != null && var56.item.getID() == ((Item)var49).getID()) {
                     if (var12 < 10) {
                        this.setSelectedSlot(var12);
                     } else {
                        var14 = this.getSelectedSlot();
                        if (this.getInv().main.isSlotClear(var14)) {
                           this.getLevel().getClient().network.sendPacket(new PacketSwapInventorySlots(this, var12, var14));
                           this.getInv().main.swapItems(var12, var14);
                        } else {
                           int var57 = -1;

                           for(int var58 = 0; var58 < 10; ++var58) {
                              if (this.getInv().main.isSlotClear(var58)) {
                                 this.getLevel().getClient().network.sendPacket(new PacketSwapInventorySlots(this, var12, var58));
                                 this.getInv().main.swapItems(var12, var58);
                                 this.setSelectedSlot(var58);
                                 var57 = -1;
                                 break;
                              }

                              if (var57 == -1 && !this.getInv().main.isItemLocked(var58)) {
                                 var57 = var58;
                              }
                           }

                           if (var57 != -1) {
                              this.getLevel().getClient().network.sendPacket(new PacketSwapInventorySlots(this, var12, var57));
                              this.getInv().main.swapItems(var12, var57);
                              this.setSelectedSlot(var57);
                           }
                        }
                     }
                     break;
                  }
               }
            }

         } else {
            if (Control.SMART_MINING.isPressed() && !var1.formManager.isMouseOver(Control.SMART_MINING.getEvent())) {
               this.smartMiningTogglePressed = true;
            } else if (Control.SMART_MINING.isReleased()) {
               if (this.smartMiningTogglePressed && !var1.formManager.isMouseOver(Control.SMART_MINING.getEvent())) {
                  Settings.smartMining = !Settings.smartMining;
                  Settings.saveClientSettings();
                  Color var20 = Settings.smartMining ? new Color(100, 200, 100) : new Color(200, 100, 100);
                  UniqueFloatText var23 = new UniqueFloatText(this.getX(), this.getY() - 20, Localization.translate("misc", Settings.smartMining ? "smartminingon" : "smartminingoff"), (new FontOptions(16)).outline().color(var20), "smartmining") {
                     public int getAnchorX() {
                        return PlayerMob.this.getX();
                     }

                     public int getAnchorY() {
                        return PlayerMob.this.getY() - 20;
                     }
                  };
                  var23.riseTime = 500;
                  var23.fadeTime = 500;
                  var23.expandTime = 50;
                  this.getLevel().hudManager.addElement(var23);
                  Screen.playSound(GameResources.tick, SoundEffect.ui());
               }

               this.smartMiningTogglePressed = false;
            }

            if (ControllerInput.ATTACK.isPressed()) {
               if (ControllerInput.isCursorVisible()) {
                  this.tryAttack(var3.getMouseLevelPosX(), var3.getMouseLevelPosY());
               } else {
                  this.tryControllerAttack();
               }
            }

            if (ControllerInput.INTERACT.isPressed()) {
               if (ControllerInput.isCursorVisible()) {
                  this.runInteract(var3.getMouseLevelPosX(), var3.getMouseLevelPosY(), false);
               } else if (this.attackHandler != null) {
                  this.attackHandler.onControllerInteracted(ControllerInput.getAimX(), ControllerInput.getAimY());
                  this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractController(ControllerInput.getAimX(), ControllerInput.getAimY()));
               } else {
                  ControllerInteractTarget var22 = this.getControllerInteractTarget(false, this.getCurrentAttackHeight(), var3);
                  if (var22 != null) {
                     var22.runInteract();
                  }
               }
            }

            if (Control.NEXT_HOTBAR.isPressed()) {
               this.setSelectedSlot((this.selectedSlot + 1) % 10);
            }

            if (Control.PREV_HOTBAR.isPressed()) {
               this.setSelectedSlot((this.selectedSlot + 9) % 10);
            }

            for(var18 = 0; var18 < Control.HOTBAR_SLOTS.length; ++var18) {
               if (Control.HOTBAR_SLOTS[var18].isPressed()) {
                  this.setSelectedSlot(var18);
               }
            }

            if (var2) {
               float var27 = this.moveX;
               float var25 = this.moveY;
               var33 = this.dir;
               this.moveX = 0.0F;
               this.moveY = 0.0F;
               double var37;
               if (!var4 && (ControllerInput.MOVE.hasChanged() || ControllerInput.MOVE.getX() != 0.0F || ControllerInput.MOVE.getY() != 0.0F)) {
                  var37 = (new Point2D.Float()).distance((double)ControllerInput.MOVE.getX(), (double)ControllerInput.MOVE.getY());
                  if (var37 >= 0.7) {
                     this.moveX = ControllerInput.MOVE.getX();
                     this.moveY = ControllerInput.MOVE.getY();
                     this.lastControllerAimX = this.moveX;
                     this.lastControllerAimY = this.moveY;
                     this.snapMovementAngle();
                  } else if (var37 >= 0.4000000059604645 && !this.isAttacking) {
                     this.setFacingDir(ControllerInput.MOVE.getX(), ControllerInput.MOVE.getY());
                     this.lastControllerAimX = ControllerInput.MOVE.getX();
                     this.lastControllerAimY = ControllerInput.MOVE.getY();
                  }
               }

               if (ControllerInput.AIM.hasChanged() || ControllerInput.AIM.getX() != 0.0F || ControllerInput.AIM.getY() != 0.0F) {
                  var37 = (new Point2D.Float()).distance((double)ControllerInput.AIM.getX(), (double)ControllerInput.AIM.getY());
                  if (var37 >= 0.4000000059604645) {
                     this.lastControllerAimX = ControllerInput.AIM.getX();
                     this.lastControllerAimY = ControllerInput.AIM.getY();
                  }
               }

               if (!var4 && Control.MOVE_TO_MOUSE.isDown()) {
                  int var40 = var3.getMouseLevelPosX() - this.getX();
                  if (Math.abs(var40) > 4) {
                     this.moveX = (float)var40;
                  }

                  var38 = var3.getMouseLevelPosY() - this.getY();
                  if (Math.abs(var38) > 4) {
                     this.moveY = (float)var38;
                  }

                  if (this.moveX != 0.0F || this.moveY != 0.0F) {
                     this.snapMovementAngle();
                  }
               }

               boolean var44 = this.dx == 0.0F && this.dy == 0.0F;
               if (!var4 && Control.MOVE_LEFT.isDown()) {
                  var44 = false;
                  if (this.dir == 3) {
                     this.firstPress = false;
                  }

                  if (!this.firstPress) {
                     this.moveX = -1.0F;
                  } else {
                     if (!this.isAttacking) {
                        this.dir = 3;
                     }

                     this.firstPress = false;
                  }
               }

               if (!var4 && Control.MOVE_RIGHT.isDown()) {
                  var44 = false;
                  if (this.dir == 1) {
                     this.firstPress = false;
                  }

                  if (!this.firstPress) {
                     this.moveX = 1.0F;
                  } else {
                     if (!this.isAttacking) {
                        this.dir = 1;
                     }

                     this.firstPress = false;
                  }
               }

               if (!var4 && Control.MOVE_UP.isDown()) {
                  var44 = false;
                  if (this.dir == 0) {
                     this.firstPress = false;
                  }

                  if (!this.firstPress) {
                     this.moveY = -1.0F;
                  } else {
                     if (!this.isAttacking) {
                        this.dir = 0;
                     }

                     this.firstPress = false;
                  }
               }

               if (!var4 && Control.MOVE_DOWN.isDown()) {
                  var44 = false;
                  if (this.dir == 2) {
                     this.firstPress = false;
                  }

                  if (!this.firstPress) {
                     this.moveY = 1.0F;
                  } else {
                     if (!this.isAttacking) {
                        this.dir = 2;
                     }

                     this.firstPress = false;
                  }
               }

               if (var44) {
                  this.firstPress = true;
               }

               if (this.isRiding()) {
                  Mob var41 = this.getMount();
                  if (var41 != null) {
                     if (!this.isAttacking) {
                        var41.dir = this.dir;
                     }

                     var41.setPos(this.x, this.y, true);
                     var41.moveX = this.moveX;
                     var41.moveY = this.moveY;
                  }
               }

               if (var27 != this.moveX || var25 != this.moveY || var33 != this.dir) {
                  this.getLevel().getClient().sendMovementPacket(false);
               }
            }

         }
      }
   }

   public void runActiveSetBuffAbility(int var1, int var2, Packet var3) {
      ActiveBuff var4 = this.buffManager.getBuff(var1);
      if (var4 != null && var4.buff instanceof ActiveBuffAbility) {
         ActiveBuffAbility var5 = (ActiveBuffAbility)var4.buff;
         var5.onActiveAbilityStarted(this, var4, var3);
         if (this.activeSetBuffAbility != null) {
            this.activeSetBuffAbility.onStopped(this);
         }

         this.activeSetBuffAbility = new ActiveBuffAbilityContainer(var2, false, var5, var4);
      }

   }

   public void runActiveTrinketBuffAbility(int var1, int var2, Packet var3) {
      ActiveBuff var4 = this.buffManager.getBuff(var1);
      if (var4 != null && var4.buff instanceof ActiveBuffAbility) {
         ActiveBuffAbility var5 = (ActiveBuffAbility)var4.buff;
         var5.onActiveAbilityStarted(this, var4, var3);
         if (this.activeTrinketBuffAbility != null) {
            this.activeTrinketBuffAbility.onStopped(this);
         }

         this.activeTrinketBuffAbility = new ActiveBuffAbilityContainer(var2, false, var5, var4);
      }

   }

   public boolean runActiveSetBuffAbilityUpdate(int var1, Packet var2) {
      if (this.activeSetBuffAbility != null && this.activeSetBuffAbility.uniqueID == var1) {
         this.activeSetBuffAbility.update(this, var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean runActiveTrinketBuffAbilityUpdate(int var1, Packet var2) {
      if (this.activeTrinketBuffAbility != null && this.activeTrinketBuffAbility.uniqueID == var1) {
         this.activeTrinketBuffAbility.update(this, var2);
         return true;
      } else {
         return false;
      }
   }

   public void sendActiveSetBuffAbilityState(Server var1, ServerClient var2) {
      ServerClient var3 = this.getServerClient();
      if (this.activeSetBuffAbility != null) {
         Packet var4 = this.activeSetBuffAbility.buffAbility.getRunningAbilityContent(this, this.activeSetBuffAbility.activeBuff);
         var2.sendPacket(new PacketActiveSetBuffAbility(var3.slot, this.activeSetBuffAbility.activeBuff.buff, this.activeSetBuffAbility.uniqueID, var4));
      } else {
         var2.sendPacket(new PacketActiveTrinketBuffAbilityStopped(var3.slot, 0));
      }

   }

   public void sendActiveTrinketBuffAbilityState(Server var1, ServerClient var2) {
      ServerClient var3 = this.getServerClient();
      if (this.activeTrinketBuffAbility != null) {
         Packet var4 = this.activeTrinketBuffAbility.buffAbility.getRunningAbilityContent(this, this.activeTrinketBuffAbility.activeBuff);
         var2.sendPacket(new PacketActiveSetBuffAbility(var3.slot, this.activeTrinketBuffAbility.activeBuff.buff, this.activeTrinketBuffAbility.uniqueID, var4));
      } else {
         var2.sendPacket(new PacketActiveTrinketBuffAbilityStopped(var3.slot, 0));
      }

   }

   public void onActiveSetBuffAbilityStopped(int var1) {
      if (this.activeSetBuffAbility != null && (var1 == 0 || this.activeSetBuffAbility.uniqueID == var1)) {
         this.activeSetBuffAbility.onStopped(this);
         this.activeSetBuffAbility = null;
      }

   }

   public void onActiveTrinketBuffAbilityStopped(int var1) {
      if (this.activeTrinketBuffAbility != null && (var1 == 0 || this.activeTrinketBuffAbility.uniqueID == var1)) {
         this.activeTrinketBuffAbility.onStopped(this);
         this.activeTrinketBuffAbility = null;
      }

   }

   private void snapMovementAngle() {
      int var1 = (int)Math.toDegrees(Math.atan2((double)this.moveY, (double)this.moveX));
      var1 = var1 / 5 * 5;
      Point2D.Float var2 = GameMath.getAngleDir((float)var1);
      this.moveX = GameMath.toDecimals(var2.x, 2);
      this.moveY = GameMath.toDecimals(var2.y, 2);
   }

   public void tickCurrentMovement(float var1) {
   }

   public boolean isVisible() {
      NetworkClient var1 = this.getNetworkClient();
      return var1 != null && !var1.hasSpawned() ? false : super.isVisible();
   }

   public boolean canBePushed(Mob var1) {
      return this.objectUser != null && this.objectUser.object.preventsUsersPushed() ? false : super.canBePushed(var1);
   }

   public boolean canPushMob(Mob var1) {
      return this.objectUser != null && this.objectUser.object.preventsUsersPushed() ? false : super.canPushMob(var1);
   }

   public Rectangle getSelectBox(int var1, int var2) {
      return this.objectUser != null ? this.objectUser.getUserSelectBox() : super.getSelectBox(var1, var2);
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

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      return PlayerSprite.getDrawOptions(this, var2, var3, var8, var5, var7);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
         DrawOptionsList var11 = new DrawOptionsList();
         if (Debug.isActive() && this.getLevel() != null) {
            int var12 = this.getX() - var8.getX() - 20;
            int var13 = this.getY() - var8.getY();
            FontOptions var14 = new FontOptions(16);
            var11.add((new StringDrawOptions(var14, var5 + ", " + this.dx)).pos(var12, var13));
            var11.add((new StringDrawOptions(var14, var6 + ", " + this.dy)).pos(var12, var13 + 15));
            var11.add((new StringDrawOptions(var14, this.getFriction() + ", " + this.getSpeed() + ", " + this.getCurrentSpeed() + ", " + this.getDistanceRan())).pos(var12, var13 + 30));
            var11.add((new StringDrawOptions(var14, "Team: " + this.getTeam())).pos(var12, var13 + 45));
            var11.add((new StringDrawOptions(var14, "Mount: " + this.mount)).pos(var12, var13 + 60));
            var11.add((new StringDrawOptions(var14, "Boomerangs: " + this.getBoomerangsUsage() + " - " + GameUtils.join(this.boomerangs.stream().map((var0) -> {
               return var0.getStringID() + " (" + var0.getUniqueID() + ")";
            }).toArray(), ", "))).pos(var12, var13 + 75));
            var11.add((new StringDrawOptions(var14, "Dir: " + this.dir + ", " + this.beforeAttackDir)).pos(var12, var13 + 90));
         }

         var3.add((var1x) -> {
            var11.draw();
         });
         if (this.objectUser == null || this.objectUser.object.drawsUsers()) {
            final DrawOptions var15 = PlayerSprite.getDrawOptions(this, var5, var6, var10, var8, (Consumer)null);
            var1.add(new MobDrawable() {
               public void draw(TickManager var1) {
                  var15.draw();
               }
            });
            this.addShadowDrawables(var2, var5, var6, var10, var8);
         }

      }
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-10, -10, 20, 24);
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      if (this.getLevel() != null) {
         PlayerSprite.getIconAnimationDrawOptions(var2 - 15, var3 - 26, 32, 32, this).draw();
         String var4 = this.getDisplayName();
         FontOptions var5 = (new FontOptions(12)).color(200, 200, 200).outline();
         int var6 = FontManager.bit.getWidthCeil(var4, var5);
         FontManager.bit.drawString((float)(var2 - var6 / 2), (float)(var3 - 34), var4, var5);
      }
   }

   public void submitInputEvent(MainGame var1, InputEvent var2, GameCamera var3) {
      int var4 = var2.pos.sceneX + var3.getX();
      int var5 = var2.pos.sceneY + var3.getY();
      if (!var2.isMouseMoveEvent() && var2.state && !var1.formManager.isMouseOver() && !(Boolean)this.buffManager.getModifier(BuffModifiers.PARALYZED)) {
         if (var2.getID() == Control.MOUSE1.getKey()) {
            if (this.isAttacking || this.onCooldown) {
               return;
            }

            this.tryAttack(var4, var5);
         } else if (var2.getID() == Control.MOUSE2.getKey() && !this.runInteract(var4, var5, false) && this.getDraggingItem() != null) {
            ContainerAction var6;
            if (Control.INV_QUICK_MOVE.isDown()) {
               var6 = ContainerAction.TAKE_ONE;
            } else {
               var6 = ContainerAction.RIGHT_CLICK;
            }

            ContainerActionResult var7 = this.getLevel().getClient().getContainer().applyContainerAction(-1, var6);
            if (var7.error != null) {
               Screen.hudManager.addElement(new UniqueScreenFloatText(Screen.mousePos().hudX, Screen.mousePos().hudY, var7.error, (new FontOptions(16)).outline(), "dropError"));
            } else {
               this.getLevel().getClient().network.sendPacket(new PacketContainerAction(-1, var6, var7.value));
            }
         }

      }
   }

   public Point2D.Float getControllerAimDir() {
      float var1 = ControllerInput.getAimX();
      float var2 = ControllerInput.getAimY();
      if (var1 == 0.0F && var2 == 0.0F) {
         var1 = ControllerInput.MOVE.getX();
         var2 = ControllerInput.MOVE.getY();
      }

      if (var1 == 0.0F && var2 == 0.0F) {
         var1 = this.lastControllerAimX;
         var2 = this.lastControllerAimY;
      }

      if (var1 == 0.0F && var2 == 0.0F) {
         int var3 = this.dir;
         if (this.isAttacking && this.beforeAttackDir != -1) {
            var3 = this.beforeAttackDir;
         }

         if (var3 == 0) {
            var2 = -1.0F;
         } else if (var3 == 1) {
            var1 = 1.0F;
         } else if (var3 == 2) {
            var2 = 1.0F;
         } else if (var3 == 3) {
            var1 = -1.0F;
         } else {
            var1 = 1.0F;
         }
      }

      return new Point2D.Float(var1, var2);
   }

   public Point2D.Float getControllerAimDirNormalized() {
      Point2D.Float var1 = this.getControllerAimDir();
      return GameMath.normalize(var1.x, var1.y);
   }

   public void tryAttack(GameCamera var1) {
      if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
         this.tryControllerAttack();
      } else {
         this.tryAttack(var1.getMouseLevelPosX(), var1.getMouseLevelPosY());
      }

   }

   public void runInteract(boolean var1, GameCamera var2) {
      if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
         if (this.attackHandler != null) {
            this.attackHandler.onControllerInteracted(ControllerInput.getAimX(), ControllerInput.getAimY());
            this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractController(ControllerInput.getAimX(), ControllerInput.getAimY()));
         } else {
            ControllerInteractTarget var3 = this.getControllerInteractTarget(false, this.getCurrentAttackHeight(), var2);
            if (var3 != null) {
               var3.runInteract();
            }
         }
      } else {
         this.runInteract(var2.getMouseLevelPosX(), var2.getMouseLevelPosY(), var1);
      }

   }

   public void tryAttack(int var1, int var2) {
      this.tryAttack(this.getSelectedItemSlot(), var1, var2);
   }

   public boolean tryAttack(PlayerInventorySlot var1, int var2, int var3) {
      return this.runAttack(var1, var2, var3, this.getCurrentAttackHeight());
   }

   public void tryControllerAttack() {
      this.tryControllerAttack(this.getSelectedItemSlot());
   }

   public boolean tryControllerAttack(PlayerInventorySlot var1) {
      return this.runControllerAttack(var1, this.getCurrentAttackHeight());
   }

   public void runPlayerAction(PacketPlayerAction.PlayerAction var1) {
      Iterator var2;
      SlotPriority var3;
      ItemUsed var11;
      switch (var1) {
         case USE_HEALTH_POTION:
            var2 = this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "usehealthpotion").iterator();

            while(var2.hasNext()) {
               var3 = (SlotPriority)var2.next();
               if (!this.getInv().main.isSlotClear(var3.slot)) {
                  var11 = this.getInv().main.getItemSlot(var3.slot).useHealthPotion(this.getLevel(), this, this.getInv().main.getItem(var3.slot));
                  this.getInv().main.setItem(var3.slot, var11.item);
                  if (var11.used) {
                     return;
                  }
               }
            }

            return;
         case USE_MANA_POTION:
            var2 = this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "usemanapotion").iterator();

            while(var2.hasNext()) {
               var3 = (SlotPriority)var2.next();
               if (!this.getInv().main.isSlotClear(var3.slot)) {
                  var11 = this.getInv().main.getItemSlot(var3.slot).useManaPotion(this.getLevel(), this, this.getInv().main.getItem(var3.slot));
                  this.getInv().main.setItem(var3.slot, var11.item);
                  if (var11.used) {
                     return;
                  }
               }
            }

            return;
         case EAT_FOOD:
            var2 = this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "eatfood").iterator();

            while(var2.hasNext()) {
               var3 = (SlotPriority)var2.next();
               if (!this.getInv().main.isSlotClear(var3.slot)) {
                  var11 = this.getInv().main.getItemSlot(var3.slot).eatFood(this.getLevel(), this, this.getInv().main.getItem(var3.slot));
                  this.getInv().main.setItem(var3.slot, var11.item);
                  if (var11.used) {
                     return;
                  }
               }
            }

            return;
         case USE_BUFF_POTION:
            var2 = this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "usebuffpotion").iterator();

            while(var2.hasNext()) {
               var3 = (SlotPriority)var2.next();
               if (!this.getInv().main.isSlotClear(var3.slot)) {
                  this.getInv().main.setItem(var3.slot, this.getInv().main.getItemSlot(var3.slot).useBuffPotion(this.getLevel(), this, this.getInv().main.getItem(var3.slot)).item);
               }
            }

            if (this.isServerClient()) {
               ServerClient var10 = this.getServerClient();
               if (var10.adventureParty.getBuffPotionPolicy() == AdventureParty.BuffPotionPolicy.ON_HOTKEY) {
                  synchronized(var10.adventureParty) {
                     Iterator var4 = var10.adventureParty.getMobs().iterator();

                     while(var4.hasNext()) {
                        HumanMob var5 = (HumanMob)var4.next();

                        for(int var6 = 0; var6 < 100; ++var6) {
                           InventoryItem var7 = var5.adventureParty.tryConsumeItem("usebuffpotion");
                           if (var7 == null) {
                              break;
                           }

                           if (var6 == 99) {
                              GameLog.warn.println(var10.getName() + " party consumed 100 items on buff hotkey???");
                           }
                        }
                     }
                  }
               }
            }
      }

   }

   public GameMessage getLocalization() {
      return new StaticMessage(this.playerName);
   }

   public InventoryItem getSelectedHotbarItem() {
      return !this.getInv().main.isSlotClear(this.getSelectedSlot()) ? this.getInv().main.getItem(this.getSelectedSlot()) : null;
   }

   public InventoryItem getSelectedItem() {
      return this.getDraggingItem() != null ? this.getDraggingItem() : this.getSelectedHotbarItem();
   }

   public PlayerInventorySlot getSelectedItemSlot() {
      return this.getDraggingItem() != null ? new PlayerInventorySlot(this.getInv().drag, 0) : new PlayerInventorySlot(this.getInv().main, this.getSelectedSlot());
   }

   public InventoryItem getDraggingItem() {
      return this.getInv().drag.getItem(0);
   }

   public void setDraggingItem(InventoryItem var1) {
      this.getInv().drag.setItem(0, var1);
   }

   public void dropItem(InventoryItem var1) {
      if (this.isClient()) {
         System.err.println("Players cannot drop items client side");
      } else {
         float var2 = 0.0F;
         float var3 = 0.0F;
         if (this.dir == 0) {
            var2 = 0.0F;
            var3 = -1.0F;
         } else if (this.dir == 1) {
            var2 = 1.0F;
            var3 = 0.0F;
         } else if (this.dir == 2) {
            var2 = 0.0F;
            var3 = 1.0F;
         } else if (this.dir == 3) {
            var2 = -1.0F;
            var3 = 0.0F;
         }

         ItemPickupEntity var4 = var1.getPickupEntity(this.getLevel(), this.x, this.y, var2 * 175.0F, var3 * 175.0F);
         if (this.isServerClient()) {
            var4.authPickupCooldown.put(this.getServerClient().authentication, 2000);
         }

         var4.height = 20.0F;
         var4.dh = 40.0F;
         this.getLevel().entityManager.pickups.add(var4);
      }
   }

   public void dropDraggingItem(int var1) {
      this.inv.dropItem(this.inv.drag, 0, var1);
   }

   public void clientDropItem(PlayerInventory var1, int var2) {
      this.clientDropItem(var1, var2, var1.getAmount(var2));
   }

   public void clientDropItem(PlayerInventory var1, int var2, int var3) {
      if (this.isClient() && !var1.isSlotClear(var2)) {
         this.getLevel().getClient().network.sendPacket(new PacketPlayerDropItem(var1.getInventoryID(), var2, var3));
      }

   }

   public void spawnDamageText(int var1, int var2, boolean var3) {
      this.getLevel().hudManager.addElement(new DamageText(this, var1, (new FontOptions(var2)).outline().color(var3 ? new Color(255, 85, 0) : Color.RED), var3 ? GameRandom.globalRandom.getIntBetween(60, 70) : GameRandom.globalRandom.getIntBetween(40, 50)));
   }

   public int getPlayerSlot() {
      return this.getUniqueID();
   }

   public void playHitSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.hurt, SoundEffect.effect(this).pitch(var1));
   }

   public void forceEndAttack() {
      if (this.attackingItem != null && this.beforeAttackDir != -1 && !this.attackingItem.item.changesDir()) {
         this.dir = this.beforeAttackDir;
         this.beforeAttackDir = -1;
      }

      this.isAttacking = false;
      this.canAttack();
   }

   public float getAttackAnimProgress() {
      float var1 = (float)(this.getWorldEntity().getTime() - this.attackTime) / (float)this.attackAnimTime;
      if (var1 >= 1.0F) {
         this.forceEndAttack();
      }

      return Math.min(1.0F, var1);
   }

   public boolean runInteract(int var1, int var2, boolean var3) {
      return this.runInteract(var1, var2, var3, this.getCurrentAttackHeight());
   }

   public boolean runInteract(int var1, int var2, boolean var3, int var4) {
      if (GlobalData.getCurrentState().isRunning() && this.getLevel() != null) {
         if (this.attackHandler != null) {
            this.attackHandler.onMouseInteracted(var1, var2);
            this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractMouse(var1, var2));
            return false;
         } else if (this.onCooldown) {
            return false;
         } else {
            int var5 = var1 / 32;
            int var6 = var2 / 32;
            PlayerInventorySlot var7 = this.getSelectedItemSlot();
            InventoryItem var8 = this.getInv().getItem(var7);
            ItemInteractAction var9 = var8 != null && var8.item instanceof ItemInteractAction ? (ItemInteractAction)var8.item : null;
            Iterator var10 = this.getLevel().entityManager.mobs.getInRegionByTileRange(var5, var6, 8).iterator();

            while(var10.hasNext()) {
               Mob var11 = (Mob)var10.next();
               if (var11.isVisible() && var11.getSelectBox().contains(var1, var2)) {
                  if (var9 != null && !this.isItemOnCooldown(var8.item) && var9.canMobInteract(this.getLevel(), var11, this, var8)) {
                     this.attackSlot = var7;
                     this.attackingItem = var8;
                     this.attackDir = GameMath.normalize((float)var1 - this.x, (float)var2 - this.y);
                     this.constantInteract = var9.getConstantInteract(this.attackingItem);
                     Packet var12 = new Packet();
                     var9.setupMobInteractContentPacket(new PacketWriter(var12), this.getLevel(), var11, this, var8);
                     int var13 = Item.getRandomAttackSeed(GameRandom.globalRandom);
                     PacketPlayerItemMobInteract var14 = new PacketPlayerItemMobInteract(this, this.attackSlot, var8.item, var1, var2, var11, 0, var13, var12);
                     this.getLevel().getClient().network.sendPacket(var14);
                     this.getInv().setItem(this.attackSlot, var9.onMobInteract(this.getLevel(), var11, this, var4, var8, this.attackSlot, var13, new PacketReader(var12)));
                     this.showItemMobInteract(var9, var8, var1, var2, var11, var13, var12);
                     return true;
                  }

                  if (!var3 && var11.inInteractRange(this) && var11.canInteract(this)) {
                     this.getLevel().getClient().network.sendPacket(new PacketPlayerMobInteract(this.getLevel().getClient().getSlot(), var11.getUniqueID()));
                     this.constantInteract = false;
                     return true;
                  }
               }
            }

            if (var9 != null && var9.overridesObjectInteract(this.getLevel(), this, var8) && !this.isItemOnCooldown(var8.item) && var9.canLevelInteract(this.getLevel(), var1, var2, this, var8)) {
               this.runLevelInteract(var7, var8, var9, var1, var2, var4);
               return true;
            } else {
               if (!var3) {
                  LevelObject var15 = GameUtils.getInteractObjectHit(this.getLevel(), var1, var2, (var1x) -> {
                     return var1x.inInteractRange(this) && var1x.canInteract(this);
                  }, (LevelObject)null);
                  if (var15 != null) {
                     ObjectInteractEvent var16 = new ObjectInteractEvent(var15.level, var15.tileX, var15.tileY, this);
                     GameEvents.triggerEvent(var16);
                     if (!var16.isPrevented()) {
                        var15.interact(this);
                        this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(this.getLevel(), this.getLevel().getClient().getSlot(), var15.tileX, var15.tileY));
                        this.constantInteract = false;
                        return true;
                     }
                  }
               }

               if (var9 != null && !this.isItemOnCooldown(var8.item) && var9.canLevelInteract(this.getLevel(), var1, var2, this, var8)) {
                  this.runLevelInteract(var7, var8, var9, var1, var2, var4);
                  return true;
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public ControllerInteractTarget getControllerInteractTarget(boolean var1, final int var2, final GameCamera var3) {
      if (!GlobalData.getCurrentState().isRunning()) {
         return null;
      } else if (!this.onCooldown && this.attackHandler == null) {
         if (this.getLevel() == null) {
            return null;
         } else {
            float var4 = ControllerInput.getAimX();
            float var5 = ControllerInput.getAimY();
            if (var4 == 0.0F && var5 == 0.0F) {
               if (this.dir == 0) {
                  var5 = -1.0F;
               } else if (this.dir == 1) {
                  var4 = 1.0F;
               } else if (this.dir == 2) {
                  var5 = 1.0F;
               } else if (this.dir == 3) {
                  var4 = -1.0F;
               } else {
                  var4 = 1.0F;
               }
            }

            float var6 = GameMath.getAngle(new Point2D.Float(var4, var5));
            int var7 = (int)(GameMath.fixAngle(var6 + 90.0F + 22.5F) * 8.0F / 360.0F);
            LinkedList var8 = new LinkedList();
            LinkedList var9 = new LinkedList();
            byte var10 = 6;
            byte var11 = 6;
            switch (var7) {
               case 0:
                  var8.add(new Rectangle(this.getX() - var10 * 32 / 2, this.getY() - var11 * 32, var10 * 32, var11 * 32));
                  var9.add(new Rectangle(this.getTileX() - var10 / 2, this.getTileY() - var11, var10, var11));
                  break;
               case 1:
                  var8.add(new Rectangle(this.getX(), this.getY() - var10 * 32, var10 * 32, var11 * 32));
                  var9.add(new Rectangle(this.getTileX() + 1, this.getTileY() - var11, var10, var11));
                  break;
               case 2:
                  var8.add(new Rectangle(this.getX(), this.getY() - var10 * 32 / 2, var11 * 32, var10 * 32));
                  var9.add(new Rectangle(this.getTileX() + 1, this.getTileY() - var10 / 2, var11, var10));
                  break;
               case 3:
                  var8.add(new Rectangle(this.getX(), this.getY(), var10 * 32, var11 * 32));
                  var9.add(new Rectangle(this.getTileX() + 1, this.getTileY() + 1, var10, var11));
                  break;
               case 4:
                  var8.add(new Rectangle(this.getX() - var10 * 32 / 2, this.getY(), var10 * 32, var11 * 32));
                  var9.add(new Rectangle(this.getTileX() + 1 - var10 / 2, this.getTileY() + 1, var10, var11));
                  break;
               case 5:
                  var8.add(new Rectangle(this.getX() - var10 * 32, this.getY(), var10 * 32, var11 * 32));
                  var9.add(new Rectangle(this.getTileX() - var10, this.getTileY() + 1, var10, var11));
                  break;
               case 6:
                  var8.add(new Rectangle(this.getX() - var10 * 32, this.getY() - var10 * 32 / 2, var11 * 32, var10 * 32));
                  var9.add(new Rectangle(this.getTileX() - var10, this.getTileY() - var10 / 2, var11, var10));
                  break;
               case 7:
                  var8.add(new Rectangle(this.getX() - var10 * 32, this.getY() - var10 * 32, var10 * 32, var11 * 32));
                  var9.add(new Rectangle(this.getTileX() - var10, this.getTileY() - var11, var10, var11));
            }

            final PlayerInventorySlot var12 = this.getSelectedItemSlot();
            final InventoryItem var13 = this.getInv().getItem(var12);
            final ItemInteractAction var14 = var13 != null && var13.item instanceof ItemInteractAction ? (ItemInteractAction)var13.item : null;
            ArrayList var15 = this.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getTileX(), this.getTileY(), 1);
            Mob var18 = this.getMount();
            ObjectValue var19 = (ObjectValue)var15.stream().filter((var1x) -> {
               return var1x.isVisible() && var1x != var18;
            }).filter((var1x) -> {
               Rectangle var2 = var1x.getSelectBox();
               return var8.stream().anyMatch((var1) -> {
                  return var1.intersects(var2);
               });
            }).map((var9x) -> {
               if (var14 != null && !this.isItemOnCooldown(var13.item) && var14.canMobInteract(this.getLevel(), var9x, this, var13)) {
                  return new ObjectValue(var9x, new ControllerInteractTarget() {
                     public void runInteract() {
                        PlayerMob.this.attackSlot = var3;
                        PlayerMob.this.attackingItem = var2;
                        PlayerMob.this.attackDir = GameMath.normalize(var4, var5);
                        PlayerMob.this.constantInteract = var1.getConstantInteract(PlayerMob.this.attackingItem);
                        Packet var1x = new Packet();
                        Rectangle var2x = var9.getSelectBox();
                        int var3x = var2x.x + var2x.width / 2;
                        int var4x = var2x.y + var2x.height / 2;
                        var1.setupMobInteractContentPacket(new PacketWriter(var1x), PlayerMob.this.getLevel(), var9, PlayerMob.this, var2);
                        int var5x = Item.getRandomAttackSeed(GameRandom.globalRandom);
                        PacketPlayerItemMobInteract var6x = new PacketPlayerItemMobInteract(PlayerMob.this, PlayerMob.this.attackSlot, var2.item, var3x, var4x, var9, 0, var5x, var1x);
                        PlayerMob.this.getLevel().getClient().network.sendPacket(var6x);
                        PlayerMob.this.getInv().setItem(PlayerMob.this.attackSlot, var1.onMobInteract(PlayerMob.this.getLevel(), var9, PlayerMob.this, var6, var2, PlayerMob.this.attackSlot, var5x, new PacketReader(var1x)));
                        PlayerMob.this.showItemMobInteract(var1, var2, var3x, var4x, var9, var5x, var1x);
                     }

                     public DrawOptions getDrawOptions() {
                        Rectangle var1x = var9.getSelectBox();
                        return HUD.levelBoundOptions(var7, Settings.UI.controllerFocusBoundsColor, true, var1x);
                     }

                     public void onCurrentlyFocused() {
                        Rectangle var1x = var9.getSelectBox();
                        Screen.setTooltipsInteractFocus(InputPosition.fromScenePos(Screen.input(), var7.getDrawX(var1x.x), var7.getDrawY(var1x.y)));
                        var2.item.onMouseHoverMob(var2, var7, PlayerMob.this, var9, false);
                     }
                  });
               } else {
                  return !var1 && var9x.inInteractRange(this) && var9x.canInteract(this) ? new ObjectValue(var9x, new ControllerInteractTarget() {
                     public void runInteract() {
                        PlayerMob.this.getLevel().getClient().network.sendPacket(new PacketPlayerMobInteract(PlayerMob.this.getLevel().getClient().getSlot(), var9.getUniqueID()));
                        PlayerMob.this.constantInteract = false;
                     }

                     public DrawOptions getDrawOptions() {
                        Rectangle var1 = var9.getSelectBox();
                        return HUD.levelBoundOptions(var7, Settings.UI.controllerFocusBoundsColor, true, var1);
                     }

                     public void onCurrentlyFocused() {
                        Rectangle var1 = var9.getSelectBox();
                        Screen.setTooltipsInteractFocus(InputPosition.fromScenePos(Screen.input(), var7.getDrawX(var1.x), var7.getDrawY(var1.y)));
                        var9.onMouseHover(var7, PlayerMob.this, false);
                     }
                  }) : null;
               }
            }).filter(Objects::nonNull).min(Comparator.comparingDouble((var1x) -> {
               return (double)((Mob)var1x.object).getDistance(this);
            })).orElse((Object)null);
            ObjectValue var20 = null;
            if (var14 != null && !this.isItemOnCooldown(var13.item)) {
               final ItemControllerInteract var21 = var14.getControllerInteract(this.getLevel(), this, var13, true, var7, var8, var9);
               if (var21 != null) {
                  var20 = new ObjectValue(new Point(var21.levelX, var21.levelY), new ControllerInteractTarget() {
                     public void runInteract() {
                        if (var14.canLevelInteract(PlayerMob.this.getLevel(), var21.levelX, var21.levelY, PlayerMob.this, var13)) {
                           PlayerMob.this.runLevelInteract(var12, var13, var14, var21.levelX, var21.levelY, var2);
                        }

                     }

                     public DrawOptions getDrawOptions() {
                        return var21.getDrawOptions(var3);
                     }

                     public void onCurrentlyFocused() {
                        var21.onCurrentlyFocused(var3);
                     }
                  });
               }
            }

            ObjectValue var26 = null;
            if (!var1) {
               var26 = (ObjectValue)var9.stream().flatMap((var1x) -> {
                  LinkedList var2 = new LinkedList();

                  for(int var3 = 0; var3 < var1x.width; ++var3) {
                     for(int var4 = 0; var4 < var1x.height; ++var4) {
                        var2.add(this.getLevel().getLevelObject(var1x.x + var3, var1x.y + var4));
                     }
                  }

                  return var2.stream();
               }).filter((var1x) -> {
                  return var1x.inInteractRange(this) && var1x.canInteract(this);
               }).min(Comparator.comparingDouble((var1x) -> {
                  return (double)this.getDistance((float)(var1x.tileX * 32 + 16), (float)(var1x.tileY * 32 + 16));
               })).map((var2x) -> {
                  return new ObjectValue(var2x, new ControllerInteractTarget() {
                     public void runInteract() {
                        ObjectInteractEvent var1x = new ObjectInteractEvent(var2.level, var2.tileX, var2.tileY, PlayerMob.this);
                        GameEvents.triggerEvent(var1x);
                        if (!var1x.isPrevented()) {
                           var2.interact(PlayerMob.this);
                           PlayerMob.this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(PlayerMob.this.getLevel(), PlayerMob.this.getLevel().getClient().getSlot(), var2.tileX, var2.tileY));
                           PlayerMob.this.constantInteract = false;
                        }

                     }

                     public DrawOptions getDrawOptions() {
                        Rectangle var1x = var2.getMultiTile().getTileRectangle(var2.tileX, var2.tileY);
                        return HUD.tileBoundOptions(var1, Settings.UI.controllerFocusBoundsColor, true, var1x);
                     }

                     public void onCurrentlyFocused() {
                        Rectangle var1x = var2.getMultiTile().getTileRectangle(var2.tileX, var2.tileY);
                        Screen.setTooltipsInteractFocus(InputPosition.fromScenePos(Screen.input(), var1.getDrawX(var1x.x * 32), var1.getDrawY(var1x.y * 32)));
                        String var2x = var2.getInteractTip(PlayerMob.this, false);
                        if (var2x != null) {
                           Screen.addTooltip(new InputTooltip(Control.MOUSE2, var2x, var2.inInteractRange(PlayerMob.this) ? 1.0F : 0.7F), TooltipLocation.INTERACT_FOCUS);
                        }

                        var2.onMouseHover(var1, PlayerMob.this, false);
                     }
                  });
               }).orElse((Object)null);
            }

            ControllerInteractTarget var22 = null;
            double var23 = Double.MAX_VALUE;
            float var25;
            if (var19 != null) {
               var25 = this.getDistance((Mob)var19.object);
               if ((double)var25 < var23) {
                  var22 = (ControllerInteractTarget)var19.value;
                  var23 = (double)var25;
               }
            }

            if (var20 != null) {
               var25 = this.getDistance((float)((Point)var20.object).x, (float)((Point)var20.object).y);
               if ((double)var25 < var23) {
                  var22 = (ControllerInteractTarget)var20.value;
                  var23 = (double)var25;
               }
            }

            if (var26 != null) {
               var25 = this.getDistance((float)(((LevelObject)var26.object).tileX * 32 + 16), (float)(((LevelObject)var26.object).tileY * 32 + 16));
               if ((double)var25 < var23) {
                  var22 = (ControllerInteractTarget)var26.value;
                  var23 = (double)var25;
               }
            }

            if (var22 != null) {
               return var22;
            } else {
               if (var14 != null && !this.isItemOnCooldown(var13.item)) {
                  final ItemControllerInteract var27 = var14.getControllerInteract(this.getLevel(), this, var13, false, var7, var8, var9);
                  if (var27 != null) {
                     return new ControllerInteractTarget() {
                        public void runInteract() {
                           PlayerMob.this.runLevelInteract(var12, var13, var14, var27.levelX, var27.levelY, var2);
                        }

                        public DrawOptions getDrawOptions() {
                           return var27.getDrawOptions(var3);
                        }

                        public void onCurrentlyFocused() {
                           var27.onCurrentlyFocused(var3);
                        }
                     };
                  }
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   protected void runLevelInteract(PlayerInventorySlot var1, InventoryItem var2, ItemInteractAction var3, int var4, int var5, int var6) {
      this.attackSlot = var1;
      this.attackingItem = var2;
      this.attackDir = GameMath.normalize((float)var4 - this.x, (float)var5 - this.y);
      this.constantInteract = var3.getConstantInteract(this.attackingItem);
      Packet var7 = new Packet();
      var3.setupLevelInteractContentPacket(new PacketWriter(var7), this.getLevel(), var4, var5, this, var2);
      int var8 = Item.getRandomAttackSeed(GameRandom.globalRandom);
      PacketPlayerItemInteract var9 = new PacketPlayerItemInteract(this, this.attackSlot, this.attackingItem.item, var4, var5, 0, var8, var7);
      this.getLevel().getClient().network.sendPacket(var9);
      this.getInv().setItem(this.attackSlot, var3.onLevelInteract(this.getLevel(), var4, var5, this, var6, this.attackingItem, this.attackSlot, var8, new PacketReader(var7)));
      this.showItemLevelInteract(var3, this.attackingItem, var4, var5, var8, var7);
   }

   public void runInteract(PacketPlayerItemMobInteract var1, Mob var2) {
      int var3 = this.getCurrentAttackHeight();
      this.setPos(var1.playerX, var1.playerY, false);
      InventoryItem var4 = this.getInv().getItem(var1.getSlot());
      if (var4 != null && var4.item.getID() == var1.itemID && var4.item instanceof ItemInteractAction) {
         ItemInteractAction var5 = (ItemInteractAction)var4.item;
         if (this.attackHandler != null) {
            return;
         }

         boolean var6 = var5.canMobInteract(this.getLevel(), var2, this, var4);
         if (!var6 && !Settings.giveClientsPower) {
            return;
         }

         this.attackSlot = var1.getSlot();
         this.attackingItem = var4;
         this.getInv().setItem(this.attackSlot, var5.onMobInteract(this.getLevel(), var2, this, var3, var4, this.attackSlot, var1.seed, new PacketReader(var1.content)));
         this.showItemMobInteract(var5, var4, var1.attackX, var1.attackY, var2, var1.seed, var1.content);
         this.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowItemMobInteract(this, var4, var1.attackX, var1.attackY, var2, var1.seed, var1.content), (ServerClient)this.getServerClient(), this.getServerClient());
      } else {
         this.getInv().markFullDirty();
      }

   }

   public void runInteract(PacketPlayerItemInteract var1) {
      int var2 = this.getCurrentAttackHeight();
      this.setPos(var1.playerX, var1.playerY, false);
      InventoryItem var3 = this.getInv().getItem(var1.getSlot());
      if (var3 != null && var3.item.getID() == var1.itemID && var3.item instanceof ItemInteractAction) {
         ItemInteractAction var4 = (ItemInteractAction)var3.item;
         if (this.attackHandler != null) {
            return;
         }

         boolean var5 = var4.canLevelInteract(this.getLevel(), var1.attackX, var1.attackY, this, var3);
         if (!var5 && !Settings.giveClientsPower) {
            return;
         }

         this.attackSlot = var1.getSlot();
         this.attackingItem = var3;
         this.getInv().setItem(this.attackSlot, var4.onLevelInteract(this.getLevel(), var1.attackX, var1.attackY, this, var2, var3, this.attackSlot, var1.seed, new PacketReader(var1.content)));
         this.showItemLevelInteract(var4, var3, var1.attackX, var1.attackY, var1.seed, var1.content);
         this.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowItemLevelInteract(this, var3, var1.attackX, var1.attackY, var1.seed, var1.content), (ServerClient)this.getServerClient(), this.getServerClient());
      } else {
         this.getInv().markFullDirty();
      }

   }

   public boolean runControllerAttack(PlayerInventorySlot var1, int var2) {
      if (!GlobalData.getCurrentState().isRunning()) {
         return false;
      } else if ((Boolean)this.buffManager.getModifier(BuffModifiers.PARALYZED)) {
         return false;
      } else {
         InventoryItem var3;
         if (this.isAttacking) {
            var3 = this.attackingItem;
         } else {
            var3 = this.getInv().getItem(var1);
         }

         if (var3 != null) {
            Point2D.Float var4 = this.getControllerAimDir();
            this.lastControllerAimX = var4.x;
            this.lastControllerAimY = var4.y;
            Point var5 = var3.item.getControllerAttackLevelPos(this.getLevel(), var4.x, var4.y, this, var3);
            return this.runAttack(var1, var5.x, var5.y, var2);
         } else {
            return false;
         }
      }
   }

   public boolean runAttack(PlayerInventorySlot var1, int var2, int var3, int var4) {
      if (!GlobalData.getCurrentState().isRunning()) {
         return false;
      } else if (!(Boolean)this.buffManager.getModifier(BuffModifiers.PARALYZED) && !(Boolean)this.buffManager.getModifier(BuffModifiers.INTIMIDATED)) {
         if (this.isAttacking) {
            if (this.canAnimAttackAgain() && this.getNextAnimAttackCooldown() <= 0L) {
               HUD.SmartMineTarget var12 = null;
               if (Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                  var12 = HUD.getFirstSmartHitTile(this.getLevel(), this, this.attackingItem, var2, var3);
                  if (var12 != null) {
                     var2 = var12.x * 32 + 16;
                     var3 = var12.y * 32 + 16;
                  }
               }

               Packet var14 = new Packet();
               if (var12 != null && var12.setupAttackContentPacket != null) {
                  var12.setupAttackContentPacket.accept(new PacketWriter(var14));
               } else {
                  this.attackingItem.item.setupAttackContentPacket(new PacketWriter(var14), this.getLevel(), var2, var3, this, this.attackingItem);
               }

               int var15 = Item.getRandomAttackSeed(GameRandom.globalRandom);
               PacketPlayerAttack var16 = new PacketPlayerAttack(this, this.attackSlot, this.attackingItem.item, var2, var3, this.animAttack, var15, var14);
               this.getLevel().getClient().network.sendPacket(var16);
               this.getInv().setItem(this.attackSlot, this.attackingItem.item.onAttack(this.getLevel(), var2, var3, this, var4, this.attackingItem, this.attackSlot, this.animAttack, var15, new PacketReader(var14)));
               if (this.attackingItem.item.shouldRunOnAttackedBuffEvent(this.getLevel(), var2, var3, this, this.attackingItem, this.attackSlot, this.animAttack, var15, new PacketReader(var14))) {
                  Iterator var17 = this.buffManager.getArrayBuffs().iterator();

                  while(var17.hasNext()) {
                     ActiveBuff var20 = (ActiveBuff)var17.next();
                     var20.onItemAttacked(var2, var3, this, var4, this.attackingItem, this.attackSlot, this.animAttack);
                  }
               }

               if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger()) {
                  float var18 = this.attackingItem.item.getHungerUsage(this.attackingItem, this) * attacksHungerModifier;
                  if (var18 > 0.0F) {
                     this.useHunger(var18, false);
                  }
               }

               ++this.animAttack;
               return true;
            } else {
               return false;
            }
         } else if (this.onCooldown) {
            return false;
         } else if (this.attackHandler != null && !this.attackHandler.canRunAttack(this.getLevel(), var2, var3, this, this.attackingItem, var1)) {
            return false;
         } else {
            this.attackSlot = var1;
            if (!this.getInv().isSlotClear(this.attackSlot)) {
               this.attackingItem = this.getInv().getItem(this.attackSlot);
            } else {
               this.attackingItem = null;
            }

            this.attackDir = GameMath.normalize((float)var2 - this.x, (float)var3 - this.y);
            if (this.attackingItem != null) {
               this.constantAttack = this.attackingItem.item.getConstantUse(this.attackingItem);
               if (!this.isItemOnCooldown(this.attackingItem.item)) {
                  String var5 = this.attackingItem.item.canAttack(this.getLevel(), var2, var3, this, this.attackingItem);
                  if (var5 == null) {
                     HUD.SmartMineTarget var13 = null;
                     if (Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                        var13 = HUD.getFirstSmartHitTile(this.getLevel(), this, this.attackingItem, var2, var3);
                        if (var13 != null) {
                           var2 = var13.x * 32 + 16;
                           var3 = var13.y * 32 + 16;
                        }
                     }

                     Packet var7 = new Packet();
                     if (var13 != null && var13.setupAttackContentPacket != null) {
                        var13.setupAttackContentPacket.accept(new PacketWriter(var7));
                     } else {
                        this.attackingItem.item.setupAttackContentPacket(new PacketWriter(var7), this.getLevel(), var2, var3, this, this.attackingItem);
                     }

                     int var8 = Item.getRandomAttackSeed(GameRandom.globalRandom);
                     PacketPlayerAttack var9 = new PacketPlayerAttack(this, this.attackSlot, this.attackingItem.item, var2, var3, 0, var8, var7);
                     this.getLevel().getClient().network.sendPacket(var9);
                     this.getInv().setItem(this.attackSlot, this.attackingItem.item.onAttack(this.getLevel(), var2, var3, this, var4, this.attackingItem, this.attackSlot, 0, var8, new PacketReader(var7)));
                     if (this.attackingItem.item.shouldRunOnAttackedBuffEvent(this.getLevel(), var2, var3, this, this.attackingItem, this.attackSlot, 0, var8, new PacketReader(var7))) {
                        Iterator var10 = this.buffManager.getArrayBuffs().iterator();

                        while(var10.hasNext()) {
                           ActiveBuff var11 = (ActiveBuff)var10.next();
                           var11.onItemAttacked(var2, var3, this, var4, this.attackingItem, this.attackSlot, 0);
                        }
                     }

                     if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger()) {
                        float var19 = this.attackingItem.item.getHungerUsage(this.attackingItem, this) * attacksHungerModifier;
                        if (var19 > 0.0F) {
                           this.useHunger(var19, false);
                        }
                     }

                     this.showAttack(this.attackingItem, var2, var3, var8, var7);
                     return true;
                  }

                  if (!var5.isEmpty()) {
                     UniqueFloatText var6 = new UniqueFloatText(this.getX(), this.getY() - 20, var5, (new FontOptions(16)).outline().color(new Color(200, 100, 100)), "mountfail") {
                        public int getAnchorX() {
                           return PlayerMob.this.getX();
                        }

                        public int getAnchorY() {
                           return PlayerMob.this.getY() - 20;
                        }
                     };
                     this.getLevel().hudManager.addElement(var6);
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void runAttack(PacketPlayerAttack var1) {
      int var2 = this.getCurrentAttackHeight();
      InventoryItem var3 = this.getInv().getItem(var1.getSlot());
      if (var3 != null && var3.item.getID() == var1.itemID) {
         if (var1.animAttack >= 1 && var1.animAttack == this.animAttack && this.canAnimAttackAgain() && this.getNextAnimAttackCooldown() <= (long)(Settings.giveClientsPower ? 1000 : 50)) {
            var3 = this.getInv().getItem(this.attackSlot);
            this.getInv().setItem(this.attackSlot, var3.item.onAttack(this.getLevel(), var1.attackX, var1.attackY, this, var2, var3, this.attackSlot, this.animAttack, var1.seed, new PacketReader(var1.content)));
            if (this.attackingItem.item.shouldRunOnAttackedBuffEvent(this.getLevel(), var1.attackX, var1.attackY, this, var3, this.attackSlot, this.animAttack, var1.seed, new PacketReader(var1.content))) {
               Iterator var7 = this.buffManager.getArrayBuffs().iterator();

               while(var7.hasNext()) {
                  ActiveBuff var10 = (ActiveBuff)var7.next();
                  var10.onItemAttacked(var1.attackX, var1.attackY, this, var2, var3, this.attackSlot, this.animAttack);
               }
            }

            if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger()) {
               float var8 = this.attackingItem.item.getHungerUsage(this.attackingItem, this) * attacksHungerModifier;
               if (var8 > 0.0F) {
                  this.useHunger(var8, false);
                  this.sendHungerPacket();
               }
            }

            ++this.animAttack;
            return;
         }

         if (this.isAttacking) {
            if (this.getNextAttackCooldown() > (long)(Settings.giveClientsPower ? 1000 : 40)) {
               return;
            }

            this.forceEndAttack();
         }

         if (this.attackHandler != null && !this.attackHandler.canRunAttack(this.getLevel(), var1.attackX, var1.attackY, this, this.attackingItem, var1.getSlot())) {
            return;
         }

         String var4 = var3.item.canAttack(this.getLevel(), var1.attackX, var1.attackY, this, var3);
         if (var4 != null) {
            var3.item.onServerCanAttackFailed(this.getLevel(), var1.attackX, var1.attackY, this, var3, var4, Settings.giveClientsPower);
            if (!Settings.giveClientsPower) {
               return;
            }
         }

         this.attackSlot = var1.getSlot();
         this.attackingItem = var3;
         this.getInv().setItem(this.attackSlot, var3.item.onAttack(this.getLevel(), var1.attackX, var1.attackY, this, var2, var3, this.attackSlot, 0, var1.seed, new PacketReader(var1.content)));
         if (this.attackingItem.item.shouldRunOnAttackedBuffEvent(this.getLevel(), var1.attackX, var1.attackY, this, var3, this.attackSlot, 0, var1.seed, new PacketReader(var1.content))) {
            Iterator var5 = this.buffManager.getArrayBuffs().iterator();

            while(var5.hasNext()) {
               ActiveBuff var6 = (ActiveBuff)var5.next();
               var6.onItemAttacked(var1.attackX, var1.attackY, this, var2, var3, this.attackSlot, this.animAttack);
            }
         }

         if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger()) {
            float var9 = this.attackingItem.item.getHungerUsage(this.attackingItem, this) * attacksHungerModifier;
            if (var9 > 0.0F) {
               this.useHunger(var9, false);
               this.sendHungerPacket();
            }
         }

         this.showAttack(var3, var1.attackX, var1.attackY, var1.seed, var1.content);
         this.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this, var3, var1.attackX, var1.attackY, var1.seed, var1.content), (ServerClient)this.getServerClient(), this.getServerClient());
      } else {
         this.getInv().markFullDirty();
      }

   }

   public long getNextAnimAttackCooldown() {
      return this.attackTime + (long)this.animAttack * this.animAttackCooldown - this.getWorldEntity().getTime();
   }

   public boolean canAnimAttackAgain() {
      return this.isAttacking && this.animAttack < this.animAttackTotal && this.attackSlot.getItem(this.inv) != null && this.attackSlot.getItem(this.inv).item.getStringID().equals(this.attackingItem.item.getStringID());
   }

   public int getCurrentAttackHeight() {
      int var1 = 14;
      Mob var2 = this.getMount();
      if (var2 != null) {
         var1 -= var2.getBobbing(var2.getX(), var2.getY());
         if (var2.getLevel() != null) {
            var1 -= var2.getLevel().getTile(var2.getX() / 32, var2.getY() / 32).getMobSinkingAmount(var2);
         }
      } else {
         var1 -= this.getBobbing(this.getX(), this.getY());
         if (this.getLevel() != null) {
            var1 -= this.getLevel().getTile(this.getX() / 32, this.getY() / 32).getMobSinkingAmount(this);
         }
      }

      return var1;
   }

   public void showItemLevelInteract(ItemInteractAction var1, InventoryItem var2, int var3, int var4, int var5, Packet var6) {
      int var7 = this.getCurrentAttackHeight();
      var1.showLevelInteract(this.getLevel(), var3, var4, this, var7, var2, var5, new PacketReader(var6));
      this.attackingItem = var2;
      this.attackDir = GameMath.normalize((float)var3 - this.x, (float)var4 - this.y);
      this.beforeAttackDir = this.dir;
      if (var2.item.showAttackAllDirections(this, var2)) {
         this.setFacingDir((float)(var3 - this.getX()), (float)(var4 - this.getY()));
      } else if (var3 > this.getX()) {
         this.dir = 1;
      } else {
         this.dir = 3;
      }

      this.isAttacking = true;
      this.isInteractAttack = true;
      this.onCooldown = true;
      this.attackTime = this.getWorldEntity().getTime();
      this.animAttackTotal = 1;
      this.animAttack = 1;
      this.attackAnimTime = var1.getLevelInteractAttackAnimTime(var2, this);
      this.animAttackCooldown = (long)this.attackAnimTime;
      this.attackSeed = var5;
      this.attackCooldown = Math.max(var1.getLevelInteractCooldownTime(var2, this), this.attackAnimTime);
      int var8 = var2.item.getItemCooldownTime(var2, this);
      if (var8 > 0) {
         this.startItemCooldown(var2.item, this.attackTime, var8);
      }

   }

   public void showItemMobInteract(ItemInteractAction var1, InventoryItem var2, int var3, int var4, Mob var5, int var6, Packet var7) {
      int var8 = this.getCurrentAttackHeight();
      var1.showMobInteract(this.getLevel(), var5, this, var8, var2, var6, new PacketReader(var7));
      this.attackingItem = var2;
      this.attackDir = GameMath.normalize((float)var3 - this.x, (float)var4 - this.y);
      this.beforeAttackDir = this.dir;
      if (var2.item.showAttackAllDirections(this, var2)) {
         this.setFacingDir((float)(var3 - this.getX()), (float)(var4 - this.getY()));
      } else if (var3 > this.getX()) {
         this.dir = 1;
      } else {
         this.dir = 3;
      }

      this.isAttacking = true;
      this.isInteractAttack = true;
      this.onCooldown = true;
      this.attackTime = this.getWorldEntity().getTime();
      this.animAttackTotal = 1;
      this.animAttack = 1;
      this.attackAnimTime = var1.getMobInteractAnimTime(var2, this);
      this.animAttackCooldown = (long)this.attackAnimTime;
      this.attackSeed = var6;
      this.attackCooldown = Math.max(var1.getMobInteractCooldownTime(var2, this), this.attackAnimTime);
      int var9 = var2.item.getItemCooldownTime(var2, this);
      if (var9 > 0) {
         this.startItemCooldown(var2.item, this.attackTime, var9);
      }

   }

   public void showAttack(InventoryItem var1, int var2, int var3, int var4, Packet var5) {
      int var6 = this.getCurrentAttackHeight();
      var1.item.showAttack(this.getLevel(), var2, var3, this, var6, var1, var4, new PacketReader(var5));
      this.attackingItem = var1;
      this.attackDir = GameMath.normalize((float)var2 - this.x, (float)var3 - this.y);
      this.beforeAttackDir = this.dir;
      if (var1.item.showAttackAllDirections(this, var1)) {
         this.setFacingDir((float)(var2 - this.getX()), (float)(var3 - this.getY()));
      } else if (var2 > this.getX()) {
         this.dir = 1;
      } else {
         this.dir = 3;
      }

      this.isAttacking = true;
      this.isInteractAttack = false;
      this.onCooldown = true;
      this.attackTime = this.getWorldEntity().getTime();
      this.animAttackTotal = var1.item.getAnimAttacks(var1);
      this.animAttack = 1;
      this.attackAnimTime = this.attackingItem.item.getAttackAnimTime(var1, this);
      this.animAttackCooldown = (long)(this.attackAnimTime / this.animAttackTotal);
      this.attackSeed = var4;
      this.attackCooldown = Math.max(this.attackingItem.item.getAttackCooldownTime(var1, this), this.attackAnimTime);
      int var7 = var1.item.getItemCooldownTime(var1, this);
      if (var7 > 0) {
         this.startItemCooldown(var1.item, this.attackTime, var7);
      }

   }

   public void startItemCooldown(Item var1, long var2, int var4) {
      ItemCooldown var5 = new ItemCooldown(var2, var4);
      this.itemCooldowns.compute(var1.getStringID(), (var2x, var3) -> {
         if (var3 == null) {
            return var5;
         } else {
            long var4 = this.getWorldEntity().getTime();
            return var5.getTimeRemaining(var4) >= var3.getTimeRemaining(var4) ? var5 : var3;
         }
      });
   }

   public void startItemCooldown(Item var1, int var2) {
      this.startItemCooldown(var1, this.getWorldEntity().getTime(), var2);
   }

   public boolean isItemOnCooldown(Item var1) {
      ItemCooldown var2 = this.getItemCooldown(var1);
      return var2 != null && var2.getTimeRemaining(this.getWorldEntity().getTime()) > 0;
   }

   public ItemCooldown getItemCooldown(Item var1) {
      return (ItemCooldown)this.itemCooldowns.get(var1.getStringID());
   }

   public void stopAttack() {
      this.forceEndAttack();
      this.onCooldown = false;
      this.attackTime = 0L;
   }

   public AttackHandler getAttackHandler() {
      return this.attackHandler;
   }

   public boolean isAttackHandlerFrom(InventoryItem var1, PlayerInventorySlot var2) {
      return this.attackHandler != null && this.attackHandler.isFrom(var1, var2);
   }

   public void startAttackHandler(AttackHandler var1) {
      if (this.attackHandler != var1) {
         if (this.attackHandler != null) {
            this.attackHandler.onEndAttack(false);
         }

         this.attackHandler = var1;
      }
   }

   public void endAttackHandler(boolean var1) {
      if (this.attackHandler != null) {
         this.attackHandler.onEndAttack(var1);
      }

      this.attackHandler = null;
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      int var3 = (Integer)var2.stream().filter((var0) -> {
         return !var0.removed();
      }).map(Attacker::getRespawnTime).max(Comparator.comparingInt(Integer::intValue)).orElse(5000);
      this.getServerClient().die(var3);
      GameMessage var4 = DeathMessageTable.getDeathMessage(var1, this.getLocalization());
      this.getLevel().getServer().network.sendToAllClients(new PacketChatMessage((new GameMessageBuilder()).append("\u00a76").append(var4)));
      System.out.println(var4.translate());
   }

   public GameMessage getAttackerName() {
      return new StaticMessage(this.getDisplayName());
   }

   public int getUniqueID() {
      return this.getRealUniqueID();
   }

   public void spawnDeathParticles(float var1, float var2) {
      GameSkin var3 = this.look.getGameSkin(false);

      for(int var4 = 0; var4 < 4; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), var3.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void setSelectedSlot(int var1) {
      if (var1 >= 0 && var1 <= 9) {
         if (this.selectedSlot != var1) {
            this.inventoryActionUpdate = true;
         }

         this.selectedSlot = var1;
      } else {
         throw new IllegalArgumentException("Slot must be in range [0 - 9]");
      }
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void setInventoryExtended(boolean var1) {
      if (this.inventoryExtended != var1) {
         this.inventoryActionUpdate = true;
      }

      if (this.getLevel() != null && this.isServer() && this.inventoryExtended && !var1) {
         InventoryItem var2 = this.getDraggingItem();
         if (var2 != null) {
            if (!var2.isLocked()) {
               this.dropDraggingItem(var2.getAmount());
            } else {
               this.getInv().addItem(var2, true, "addback", (InventoryAddConsumer)null);
               if (var2.getAmount() <= 0) {
                  this.setDraggingItem((InventoryItem)null);
               } else {
                  this.dropDraggingItem(var2.getAmount());
               }
            }
         }
      }

      this.inventoryExtended = var1;
   }

   public boolean isInventoryExtended() {
      return this.inventoryExtended;
   }

   public boolean shouldUpdateInventoryAction() {
      return this.inventoryActionUpdate;
   }

   public void resetUpdateInventoryAction() {
      this.inventoryActionUpdate = false;
   }

   public void resetInv() {
      if (this.inv == null) {
         this.inv = new PlayerInventoryManager(this);
      } else {
         this.inv.clearInventories();
      }

   }

   public NetworkClient getNetworkClient() {
      return this.networkClient;
   }

   public PlayerInventoryManager getInv() {
      return this.inv;
   }

   public void tickInventory() {
      this.getInv().tick();
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

   public float getHungerLevel() {
      return this.hungerLevel;
   }

   public void useHunger(float var1, boolean var2) {
      if (var2 || !this.buffManager.hasBuff((Buff)BuffRegistry.FOOD_BUFF)) {
         this.hungerLevel = Math.max(0.0F, this.hungerLevel - var1);
      }
   }

   public void addHunger(float var1) {
      this.hungerLevel = Math.min(this.hungerLevel + var1, 1.0F + var1);
   }

   public boolean useFoodItem(FoodConsumableItem var1, boolean var2) {
      FoodBuff var3 = var1.isDebuff ? BuffRegistry.FOOD_DEBUFF : BuffRegistry.FOOD_BUFF;
      ActiveBuff var4 = this.buffManager.getBuff((Buff)var3);
      boolean var5 = this.getWorldSettings() == null || this.getWorldSettings().playerHunger();
      if (var4 != null && (!var5 || this.hungerLevel >= 1.0F)) {
         FoodConsumableItem var6 = FoodBuff.getFoodItem(var4);
         if (var6 != null && var6.getID() == this.getID()) {
            return false;
         }
      }

      HungerMob.super.useFoodItem(var1, var2);
      if (this.isServerClient()) {
         ServerClient var7 = this.getServerClient();
         var7.newStats.food_consumed.add((Item)var1);
      }

      this.sendHungerPacket();
      return true;
   }

   public void tickHunger() {
      WorldSettings var1 = this.getLevel().getWorldSettings();
      if (var1.playerHunger()) {
         float var2 = 0.0F;
         var2 += 50.0F / (1000.0F * (float)secondsToPassAtFullHunger);
         double var3 = (double)((int)this.getDistanceRan()) - this.lastDistanceRan;
         if (var3 > 0.0) {
            float var5 = Math.min(1.0F, 35.0F / this.getSpeed());
            var2 = (float)((double)var2 + var3 / (double)distanceToRunAtFullHunger * (double)var5);
         }

         this.useHunger(var2, false);
      }

      this.lastDistanceRan = this.getDistanceRan();
      if (var1.playerHunger()) {
         if (this.hungerLevel <= 0.0F) {
            if (this.isServer() && var1.survivalMode && this.canTakeDamage() && !this.isOnGenericCooldown("starvationdamage")) {
               int var6 = this.getMaxHealth();
               float var7 = Math.max((float)Math.pow((double)var6, 0.5) + (float)var6 / 20.0F, 20.0F);
               var7 /= 2.0F;
               this.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, var7), 0.0F, 0.0F, 0.0F, STARVING_ATTACKER);
               this.startGenericCooldown("starvationdamage", 5000L);
            }

            if (!this.buffManager.hasBuff(BuffRegistry.STARVING_BUFF)) {
               this.buffManager.addBuff(new ActiveBuff(BuffRegistry.STARVING_BUFF, this, 0, (Attacker)null), false);
            }

            this.buffManager.removeBuff(BuffRegistry.HUNGRY_BUFF, false);
         } else if (this.hungerLevel <= 0.1F) {
            if (!this.buffManager.hasBuff(BuffRegistry.HUNGRY_BUFF)) {
               this.buffManager.addBuff(new ActiveBuff(BuffRegistry.HUNGRY_BUFF, this, 0, (Attacker)null), false);
            }

            this.buffManager.removeBuff(BuffRegistry.STARVING_BUFF, false);
         } else {
            this.buffManager.removeBuff(BuffRegistry.HUNGRY_BUFF, false);
            this.buffManager.removeBuff(BuffRegistry.STARVING_BUFF, false);
         }
      } else {
         this.buffManager.removeBuff(BuffRegistry.HUNGRY_BUFF, false);
         this.buffManager.removeBuff(BuffRegistry.STARVING_BUFF, false);
      }

   }

   public ServerClient getServerClient() {
      return (ServerClient)this.networkClient;
   }

   public boolean isServerClient() {
      return this.networkClient instanceof ServerClient;
   }

   public ClientClient getClientClient() {
      return (ClientClient)this.networkClient;
   }

   public boolean isClientClient() {
      return this.networkClient instanceof ClientClient;
   }

   public void sendHungerPacket() {
      if (this.isServer()) {
         this.getLevel().getServer().network.sendToClientsAt(new PacketPlayerHunger(this.getUniqueID(), this.hungerLevel), (Level)this.getLevel());
      }

   }

   public float getAttackingMovementModifier() {
      return this.attackingItem != null ? this.attackingItem.item.getFinalAttackMovementMod(this.attackingItem, this) : super.getAttackingMovementModifier();
   }

   public boolean usesMana() {
      return true;
   }

   public boolean isManaBarVisible() {
      if (this.usesMana()) {
         float var1 = this.getMana();
         int var2 = this.getMaxMana();
         if (var1 < (float)var2) {
            this.maxManaReached = false;
            return true;
         } else if (this.timeOfMaxMana + 2000L < this.getWorldEntity().getTime() && this.maxManaReached) {
            return false;
         } else {
            if (!this.maxManaReached) {
               this.timeOfMaxMana = this.getWorldEntity().getTime();
               this.maxManaReached = true;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      if (this.activeSetBuffAbility != null) {
         this.activeSetBuffAbility.onStopped(this);
         this.activeSetBuffAbility = null;
      }

      if (this.activeTrinketBuffAbility != null) {
         this.activeTrinketBuffAbility.onStopped(this);
         this.activeTrinketBuffAbility = null;
      }

      if (this.attackHandler != null) {
         this.attackHandler.onEndAttack(false);
      }

      this.attackHandler = null;
   }

   public void giveBaitBack(BaitItem var1) {
      ItemPickupEntity var2 = this.inv.addItemsDropRemaining(new InventoryItem(var1), "addback", this, false, false);
      if (var2 != null) {
         var2.pickupCooldown = 0;
      }

   }

   public void stopFishing() {
      this.stopAttack();
   }

   public void showFishingWaitAnimation(FishingRodItem var1, int var2, int var3) {
      this.showAttack(FishingPoleHolding.setGNDData(new InventoryItem("fishinghold"), var1), var2, var3, 0, new Packet());
   }

   public boolean isFishingSwingDone() {
      this.getAttackAnimProgress();
      return !this.isAttacking;
   }

   public FishingLootTable getFishingLootTable(FishingSpot var1) {
      ServerClient var2 = this.getServerClient();
      return var2 == null ? var1.tile.level.biome.getFishingLootTable(var1) : var2.getFishingLoot(var1);
   }

   public void restore() {
      super.restore();
      this.equipmentBuffManager.updateArmorBuffs();
      this.equipmentBuffManager.updateCosmeticSetBonus();
      this.equipmentBuffManager.updateTrinketBuffs();
   }

   public void dispose() {
      super.dispose();
      this.getInv().dispose();
      if (this.activeSetBuffAbility != null) {
         this.activeSetBuffAbility.onStopped(this);
         this.activeSetBuffAbility = null;
      }

      if (this.activeTrinketBuffAbility != null) {
         this.activeTrinketBuffAbility.onStopped(this);
         this.activeTrinketBuffAbility = null;
      }

      if (this.attackHandler != null) {
         this.attackHandler.onEndAttack(false);
      }

   }
}
