package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.boomerangProjectile.ReaperScytheProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "halodromevinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("shadowbeam"), new LootItem("reaperscall"), new LootItem("deathripper"), new LootItem("reaperscythe"));
   public static LootTable privateLootTable;
   public static MaxHealthGetter BASE_MAX_HEALTH;
   public static MaxHealthGetter INCURSION_MAX_HEALTH;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public LinkedList<Mob> spawnedMobs = new LinkedList();
   private long appearTime;
   private boolean hasScythe;
   private boolean isHiding;
   public final BooleanMobAbility hasScytheAbility;
   public final BooleanMobAbility hidingAbility;
   public final CoordinateMobAbility appearAbility;
   public final EmptyMobAbility magicSoundAbility;
   public final EmptyMobAbility roarSoundAbility;
   public GameDamage collisionDamage;
   public GameDamage scytheDamage;
   public static GameDamage baseCollisionDamage;
   public static GameDamage baseScytheDamage;
   public static GameDamage incursionCollisionDamage;
   public static GameDamage incursionScytheDamage;

   public ReaperMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
      this.moveAccuracy = 60;
      this.hasScythe = true;
      this.setSpeed(80.0F);
      this.setArmor(30);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-40, -70, 80, 90);
      this.hitBox = new Rectangle(-40, -70, 80, 90);
      this.selectBox = new Rectangle(-40, -80, 80, 110);
      this.hasScytheAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            ReaperMob.this.hasScythe = var1;
            if (ReaperMob.this.isClient() && !ReaperMob.this.hasScythe) {
               Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(ReaperMob.this).pitch(0.8F));
            }

         }
      });
      this.hidingAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            ReaperMob.this.isHiding = var1;
            if (!ReaperMob.this.isHiding) {
               ReaperMob.this.appearTime = ReaperMob.this.getWorldEntity().getTime();
            } else if (ReaperMob.this.isClient()) {
               ReaperMob.this.spawnFadingParticle(500);
            }

         }
      });
      this.appearAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            if (ReaperMob.this.isClient() && !ReaperMob.this.isHiding) {
               ReaperMob.this.spawnFadingParticle(500);
            }

            ReaperMob.this.setPos((float)var1, (float)var2, true);
            ReaperMob.this.isHiding = false;
            ReaperMob.this.appearTime = ReaperMob.this.getWorldEntity().getTime();
         }
      });
      this.magicSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (ReaperMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(ReaperMob.this));
            }

         }
      });
      this.roarSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (ReaperMob.this.isClient()) {
               Screen.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7F).pitch(1.3F));
            }

         }
      });
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.hasScythe = var1.getNextBoolean();
      this.isHiding = var1.getNextBoolean();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.hasScythe);
      var1.putNextBoolean(this.isHiding);
   }

   public void setupHealthPacket(PacketWriter var1, boolean var2) {
      this.scaling.setupHealthPacket(var1, var2);
      super.setupHealthPacket(var1, var2);
   }

   public void applyHealthPacket(PacketReader var1, boolean var2) {
      this.scaling.applyHealthPacket(var1, var2);
      super.applyHealthPacket(var1, var2);
   }

   public void setMaxHealth(int var1) {
      super.setMaxHealth(var1);
      if (this.scaling != null) {
         this.scaling.updatedMaxHealth();
      }

   }

   public void init() {
      if (this.getLevel() instanceof IncursionLevel) {
         this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
         this.setHealth(this.getMaxHealth());
         this.collisionDamage = incursionCollisionDamage;
         this.scytheDamage = incursionScytheDamage;
      } else {
         this.collisionDamage = baseCollisionDamage;
         this.scytheDamage = baseScytheDamage;
      }

      super.init();
      this.ai = new BehaviourTreeAI(this, new DeepReaperAI());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect());
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public boolean canTakeDamage() {
      return !this.isHiding;
   }

   public boolean canLevelInteract() {
      return !this.isHiding;
   }

   public boolean canPushMob(Mob var1) {
      return !this.isHiding;
   }

   public boolean isVisible() {
      return !this.isHiding;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public boolean canCollisionHit(Mob var1) {
      return !this.isHiding && super.canCollisionHit(var1);
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void setFacingDir(float var1, float var2) {
      if (var1 < 0.0F) {
         this.dir = 0;
      } else if (var1 > 0.0F) {
         this.dir = 1;
      }

   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.Halodrome, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(85.0F + var1 * 65.0F);
      if (this.isVisible()) {
         for(int var2 = 0; var2 < 3; ++var2) {
            this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 12.0) + (float)(this.dir == 0 ? 18 : -18), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0) + 10.0F, Particle.GType.IMPORTANT_COSMETIC).lifeTime(1000).movesConstant(this.dx / 10.0F, this.dy / 10.0F).color(new Color(24, 27, 27)).height(0.0F);
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(85.0F + var1 * 65.0F);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 8; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.reaper, 4 + var3, 16, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void spawnRemoveParticles(float var1, float var2) {
      this.spawnFadingParticle(1000);
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (!this.isHiding) {
         GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
         float var11 = 1.0F;
         long var12 = this.getWorldEntity().getTime();
         if (var12 - this.appearTime < 500L) {
            var11 = (float)(var12 - this.appearTime) / 500.0F;
         }

         DrawOptions var14 = this.getBodyDrawOptions(var4, var5, var6, var7, var8, this.hasScythe, this.dir, var11);
         TextureDrawOptions var15 = this.getShadowDrawOptions(var5, var6, var10, var8);
         var3.add((var2x) -> {
            var15.draw();
            var14.draw();
         });
      }

   }

   public DrawOptions getBodyDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, boolean var6, int var7, float var8) {
      GameLight var9 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var10 = var5.getDrawX(var2) - 75;
      int var11 = var5.getDrawY(var3) - 100;
      long var12 = var1.getWorldEntity().getTime();
      int var14 = this.isSecondStage() ? 2 : 0;
      if (var6) {
         ++var14;
      }

      int var15 = GameUtils.getAnim(var12, 3, 300);
      TextureDrawOptionsEnd var16 = MobRegistry.Textures.reaper.initDraw().sprite(var15, var14, 128).size(128, 128).light(var9).alpha(var8).mirror(var7 != 0, false).pos(var10, var11);
      byte var17 = 100;
      TextureDrawOptionsEnd var18 = MobRegistry.Textures.reaperGlow.initDraw().sprite(var15, var14, 128).size(128, 128).light(var9.minLevelCopy((float)var17)).alpha(var8).mirror(var7 != 0, false).pos(var10, var11);
      return () -> {
         var16.draw();
         var18.draw();
      };
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.reaper_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2 - 10;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2 + 10;
      return var5.initDraw().light(var3).pos(var6, var7);
   }

   public boolean shouldDrawOnMap() {
      return !this.isHiding;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 16;
      int var5 = var3 - 16;
      MobRegistry.Textures.reaper.initDraw().sprite(2 + (this.isSecondStage() ? 1 : 0), 9, 64).size(32, 32).mirror(this.dir != 0, false).draw(var4, var5);
      MobRegistry.Textures.reaperGlow.initDraw().sprite(2 + (this.isSecondStage() ? 1 : 0), 9, 64).size(32, 32).mirror(this.dir != 0, false).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-16, -16, 32, 32);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F));
   }

   public void setHasScythe(boolean var1) {
      if (this.hasScythe != var1) {
         this.hasScytheAbility.runAndSend(var1);
      }

   }

   public void setIsHiding(boolean var1) {
      if (this.isHiding != var1) {
         this.hidingAbility.runAndSend(var1);
      }

   }

   public boolean isSecondStage() {
      float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
      return var1 < 0.5F;
   }

   public void spawnFadingParticle(int var1) {
      final int var2 = this.dir;
      final boolean var3 = this.hasScythe;
      float var4 = this.dx;
      float var5 = this.dy;
      this.getLevel().entityManager.addParticle(new Particle(this.getLevel(), (float)this.getX(), (float)this.getY(), var4, var5, (long)var1) {
         public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2x, OrderableDrawables var3x, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
            float var9 = Math.abs(this.getLifeCyclePercent() - 1.0F);
            DrawOptions var10 = ReaperMob.this.getBodyDrawOptions(var5, this.getX(), this.getY(), var6, var7, var3, var2, var9);
            var3x.add((var1x) -> {
               var10.draw();
            });
         }
      }, Particle.GType.CRITICAL);
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), (ServerClient)((PlayerMob)var1x).getServerClient());
      });
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((var0) -> {
         return var0.getLevel() == null || !var0.getLevel().isIncursionLevel;
      }, new LootItemInterface[]{uniqueDrops})});
      BASE_MAX_HEALTH = new MaxHealthGetter(6000, 9000, 11000, 13000, 18000);
      INCURSION_MAX_HEALTH = new MaxHealthGetter(12000, 17000, 20000, 23000, 28000);
      baseCollisionDamage = new GameDamage(65.0F);
      baseScytheDamage = new GameDamage(65.0F);
      incursionCollisionDamage = new GameDamage(85.0F);
      incursionScytheDamage = new GameDamage(95.0F);
   }

   public class DeepReaperAI<T extends ReaperMob> extends AINode<T> {
      private int removeCounter;
      private Mob currentTarget;
      private long findNewTargetTime;
      private ArrayList<DeepReaperAI<T>.AttackRotation> attackRotations = new ArrayList();
      private int currentRotation;

      public DeepReaperAI() {
         this.attackRotations.add(new FadeChargeAttackRotation());
         this.attackRotations.add(new SpawnSpiritsAttackRotation());
         this.attackRotations.add(new CirclingTargetAttackRotation());
         this.currentRotation = 0;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         var3.onEvent("refreshBossDespawn", (var1x) -> {
            this.removeCounter = 0;
         });
         if (var2.isServer()) {
            ((AttackRotation)this.attackRotations.get(this.currentRotation)).start();
         }

         var3.onRemoved((var1x) -> {
            ReaperMob.this.spawnedMobs.forEach(Mob::remove);
         });
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.tickTargets();
         if (this.currentTarget != null) {
            this.removeCounter = 0;
            if (((AttackRotation)this.attackRotations.get(this.currentRotation)).isOver()) {
               ((AttackRotation)this.attackRotations.get(this.currentRotation)).end();
               this.currentRotation = (this.currentRotation + 1) % this.attackRotations.size();
               ((AttackRotation)this.attackRotations.get(this.currentRotation)).start();
            }

            ((AttackRotation)this.attackRotations.get(this.currentRotation)).tick();
         } else {
            ReaperMob.this.stopMoving();
            ++this.removeCounter;
            if (this.removeCounter > 100) {
               ReaperMob.this.remove();
            }
         }

         return AINodeResult.SUCCESS;
      }

      public void tickTargets() {
         if (this.currentTarget != null) {
            if (this.currentTarget.isSamePlace(this.mob()) && !this.currentTarget.removed()) {
               if (ReaperMob.this.getWorldEntity().getTime() <= this.findNewTargetTime) {
                  this.refreshTarget();
               }
            } else {
               this.refreshTarget();
            }
         } else {
            this.refreshTarget();
         }

      }

      public void refreshTarget() {
         this.currentTarget = (Mob)GameUtils.streamServerClients(ReaperMob.this.getLevel()).map((var0) -> {
            return var0.playerMob;
         }).min(Comparator.comparingInt((var1) -> {
            return (int)ReaperMob.this.getDistance(var1);
         })).orElse((Object)null);
         this.findNewTargetTime = ReaperMob.this.getWorldEntity().getTime() + 5000L;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((ReaperMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((ReaperMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (ReaperMob)var2, var3);
      }

      private class FadeChargeAttackRotation extends DeepReaperAI<T>.AttackRotation {
         private int chargeTimes;
         private boolean isCharging;
         private int currentCharge;
         private boolean isDone;
         private int timer;
         private List<Integer> currentAttackAngles;

         private FadeChargeAttackRotation() {
            super(null);
            this.timer = 0;
         }

         public void start() {
            this.currentAttackAngles = new ArrayList(Arrays.asList(0, 45, 90, 135, 180, 225, 270, 315));
            this.chargeTimes = 4;
            this.currentCharge = 0;
            this.isCharging = false;
            this.timer = 0;
            ReaperMob.this.stopMoving();
            ReaperMob.this.setIsHiding(true);
            this.isDone = false;
         }

         public void tick() {
            if (!this.isCharging) {
               ++this.timer;
               float var1 = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
               int var2 = 20;
               if (var1 < 0.25F) {
                  var2 = (int)((float)var2 * 1.0F / 4.0F);
               } else if (var1 < 0.5F) {
                  var2 = (int)((float)var2 * 1.0F / 3.0F);
               } else if (var1 < 0.7F) {
                  var2 = (int)((float)var2 * 1.0F / 2.0F);
               }

               if (this.timer > var2) {
                  this.timer = 0;
                  int var3 = GameRandom.globalRandom.nextInt(this.currentAttackAngles.size());
                  int var4 = (Integer)this.currentAttackAngles.remove(var3);
                  float var5 = 200.0F + ReaperMob.this.getSpeed() / 2.0F;
                  int var6 = (int)(Math.cos(Math.toRadians((double)var4)) * (double)var5);
                  int var7 = (int)(Math.sin(Math.toRadians((double)var4)) * (double)var5);
                  ReaperMob.this.appearAbility.runAndSend(DeepReaperAI.this.currentTarget.getX() + var6, DeepReaperAI.this.currentTarget.getY() + var7);
                  ReaperMob.this.setMovement(new MobMovementLevelPos((float)(DeepReaperAI.this.currentTarget.getX() - var6), (float)(DeepReaperAI.this.currentTarget.getY() - var7)));
                  this.isCharging = true;
                  ReaperMob.this.roarSoundAbility.runAndSend();
                  ++this.currentCharge;
               }
            } else if (ReaperMob.this.hasArrivedAtTarget()) {
               if (this.currentCharge < this.chargeTimes) {
                  this.isCharging = false;
                  ReaperMob.this.setIsHiding(true);
               } else {
                  this.isDone = true;
               }
            }

         }

         public boolean isOver() {
            return this.isDone;
         }

         public void end() {
         }

         // $FF: synthetic method
         FadeChargeAttackRotation(Object var2) {
            this();
         }
      }

      private class SpawnSpiritsAttackRotation extends DeepReaperAI<T>.AttackRotation {
         private float spawnCounter;
         private int timer;
         private int currentPortal;
         private ArrayList<EmptyMobAbility> portals;

         private SpawnSpiritsAttackRotation() {
            super(null);
            this.portals = new ArrayList();
         }

         public void start() {
            this.timer = 0;
            this.spawnCounter = 0.0F;
            this.currentPortal = 0;
            this.portals.clear();
            ReaperMob.this.stopMoving();
            int var1 = ReaperMob.this.getLevel().presentPlayers;
            int var2 = GameMath.limit((var1 - 1) / 2, 0, 10);
            if (ReaperMob.this.isSecondStage()) {
               var2 += 2;
               ReaperMob.this.setIsHiding(true);
            } else {
               this.portals.add(ReaperMob.this.magicSoundAbility);
            }

            if (var2 > 0) {
               Point2D.Float var3 = GameMath.normalize((float)(DeepReaperAI.this.currentTarget.getX() - ReaperMob.this.getX()), (float)(DeepReaperAI.this.currentTarget.getY() - ReaperMob.this.getY()));
               float var4 = (float)Math.toDegrees(Math.atan2((double)var3.y, (double)var3.x));
               int var5 = 360 / var2;
               int var6 = var5 / 8;

               for(int var7 = 0; var7 < var2; ++var7) {
                  this.spawnPortal(var4 + 90.0F + (float)(var5 * var7) + (float)GameRandom.globalRandom.getIntOffset(0, var6));
               }
            }

         }

         public void spawnPortal(float var1) {
            int var2 = (int)(Math.cos(Math.toRadians((double)var1)) * 300.0);
            int var3 = (int)(Math.sin(Math.toRadians((double)var1)) * 300.0);
            ReaperSpiritPortalMob var4 = (ReaperSpiritPortalMob)MobRegistry.getMob("reaperspiritportal", ReaperMob.this.getLevel());
            var4.owner = ReaperMob.this;
            ReaperMob.this.getLevel().entityManager.addMob(var4, (float)(DeepReaperAI.this.currentTarget.getX() + var2), (float)(DeepReaperAI.this.currentTarget.getY() + var3));
            ReaperMob.this.spawnedMobs.add(var4);
            this.portals.add(var4.magicSoundAbility);
         }

         public void tick() {
            float var1 = Math.max((float)this.portals.size() / 2.0F, 1.0F);
            ++this.timer;
            float var2 = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
            float var3;
            if (var2 < 0.25F) {
               var3 = 0.25F;
            } else if (var2 < 0.5F) {
               var3 = 0.2F;
            } else if (var2 < 0.7F) {
               var3 = 0.15F;
            } else {
               var3 = 0.1F;
            }

            for(this.spawnCounter += var3 * var1; this.spawnCounter > 1.0F; this.currentPortal = (this.currentPortal + 1) % this.portals.size()) {
               --this.spawnCounter;
               EmptyMobAbility var4 = (EmptyMobAbility)this.portals.get(this.currentPortal);
               Mob var5 = var4.getMob();
               var4.runAndSend();
               ReaperSpiritMob var6 = (ReaperSpiritMob)MobRegistry.getMob("reaperspirit", ReaperMob.this.getLevel());
               var6.owner = ReaperMob.this;
               ReaperMob.this.getLevel().entityManager.addMob(var6, (float)var5.getX(), (float)var5.getY());
               ReaperMob.this.spawnedMobs.add(var6);
            }

         }

         public boolean isOver() {
            float var1 = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
            return (float)this.timer > 20.0F * (2.0F + var1 * 3.0F);
         }

         public void end() {
            Iterator var1 = this.portals.iterator();

            while(var1.hasNext()) {
               EmptyMobAbility var2 = (EmptyMobAbility)var1.next();
               Mob var3 = var2.getMob();
               if (var3 != DeepReaperAI.this.mob()) {
                  var3.remove();
               }
            }

            ReaperMob.this.setIsHiding(false);
            this.portals.clear();
         }

         // $FF: synthetic method
         SpawnSpiritsAttackRotation(Object var2) {
            this();
         }
      }

      private class CirclingTargetAttackRotation extends DeepReaperAI<T>.AttackRotation {
         private int timer;
         private int cooldown;
         private Mob circlingTarget;
         private boolean reverse;

         private CirclingTargetAttackRotation() {
            super(null);
            this.reverse = false;
         }

         public void start() {
            this.timer = 0;
            this.cooldown = 0;
            this.circlingTarget = null;
            this.reverse = GameRandom.globalRandom.nextBoolean();
         }

         public void tick() {
            ++this.timer;
            if (this.circlingTarget != DeepReaperAI.this.currentTarget) {
               this.circlingTarget = DeepReaperAI.this.currentTarget;
               ReaperMob.this.setMovement(new MobMovementCircleRelative(DeepReaperAI.this.mob(), this.circlingTarget, 350, 2.0F, this.reverse));
            }

            if (ReaperMob.this.hasScythe) {
               ++this.cooldown;
               if (this.cooldown > 40) {
                  this.cooldown = 0;
                  ReaperMob.this.setHasScythe(false);
                  ReaperMob.this.getLevel().entityManager.projectiles.add(new ReaperScytheProjectile(ReaperMob.this, ReaperMob.this.getX(), ReaperMob.this.getY(), DeepReaperAI.this.currentTarget.getX(), DeepReaperAI.this.currentTarget.getY(), ReaperMob.this.scytheDamage, (int)(ReaperMob.this.getSpeed() * 1.1F), ReaperMob.this.isSecondStage() ? 400 : 650));
                  if (ReaperMob.this.isSecondStage()) {
                     int var1 = DeepReaperAI.this.currentTarget.getX() - ReaperMob.this.getX();
                     int var2 = DeepReaperAI.this.currentTarget.getY() - ReaperMob.this.getY();
                     ReaperMob.this.appearAbility.runAndSend(DeepReaperAI.this.currentTarget.getX() + var1, DeepReaperAI.this.currentTarget.getY() + var2);
                     this.reverse = !this.reverse;
                     ReaperMob.this.setMovement(new MobMovementCircleRelative(DeepReaperAI.this.mob(), this.circlingTarget, 350, 2.0F, this.reverse));
                  }
               }
            }

         }

         public boolean isOver() {
            float var1 = (float)ReaperMob.this.getHealth() / (float)ReaperMob.this.getMaxHealth();
            return ReaperMob.this.hasScythe && (float)this.timer > 20.0F * (5.0F + var1 * 10.0F);
         }

         public void end() {
         }

         // $FF: synthetic method
         CirclingTargetAttackRotation(Object var2) {
            this();
         }
      }

      private abstract class AttackRotation {
         private AttackRotation() {
         }

         public abstract void start();

         public abstract void tick();

         public abstract boolean isOver();

         public abstract void end();

         // $FF: synthetic method
         AttackRotation(Object var2) {
            this();
         }
      }
   }
}
