package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.SupernovaExplosionEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SunlightOrbEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToOppositeDirectionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.mobs.networkField.BooleanNetworkField;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SunlightChampionMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "sunlightsexamvinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("dawnhelmet", 1, (new GNDItemMap()).setInt("upgradeLevel", 100)), new LootItem("dawnchestplate", 1, (new GNDItemMap()).setInt("upgradeLevel", 100)), new LootItem("dawnboots", 1, (new GNDItemMap()).setInt("upgradeLevel", 100)));
   public static LootTable privateLootTable;
   public float currentHeight;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
   public ArrayList<SunlightGauntletMob> gauntlets = new ArrayList();
   public boolean[] gauntletAttackStatus;
   public long superNovaEndTime;
   public SoundPlayer superNovaRumbleSound;
   public ParticleTypeSwitcher superNovaParticleTypeSwitcher;
   public static GameDamage collisionDamage;
   public static GameDamage gauntletCollisionDamage;
   public static GameDamage sunlightOrbDamage;
   public static GameDamage supernovaDamage;
   protected final IntMobAbility startSupernovaCharge;
   protected final EmptyMobAbility supernovaSoundAbility;
   protected final EmptyMobAbility rocketPunchSoundAbility;
   public static MaxHealthGetter MAX_HEALTH;

   public SunlightChampionMob() {
      super(40000);
      this.superNovaParticleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.setSpeed(80.0F);
      this.setArmor(40);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-30, -20, 60, 70);
      this.hitBox = new Rectangle(-25, -60, 50, 160);
      this.selectBox = new Rectangle(-70, -85, 140, 210);
      this.startSupernovaCharge = (IntMobAbility)this.registerAbility(new IntMobAbility() {
         protected void run(int var1) {
            SunlightChampionMob.this.superNovaEndTime = SunlightChampionMob.this.getTime() + (long)var1;
            if (SunlightChampionMob.this.isClient()) {
               SunlightChampionMob.this.getClient().startCameraShake((PrimitiveSoundEmitter)null, var1, 60, 3.0F, 3.0F, false);
            }

         }
      });
      this.supernovaSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SunlightChampionMob.this.isClient()) {
               Screen.playSound(GameResources.explosionHeavy, SoundEffect.globalEffect().volume(2.0F).pitch(0.5F));
            }
         }
      });
      this.rocketPunchSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SunlightChampionMob.this.isClient()) {
               Screen.playSound(GameResources.firespell1, SoundEffect.globalEffect().volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 2.5F)));
            }
         }
      });
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      Iterator var2 = this.gauntlets.iterator();

      while(var2.hasNext()) {
         SunlightGauntletMob var3 = (SunlightGauntletMob)var2.next();
         var1.putNextBoolean((Boolean)var3.isAttacking.get());
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.gauntletAttackStatus = new boolean[2];

      for(int var2 = 0; var2 < 2; ++var2) {
         this.gauntletAttackStatus[var2] = var1.getNextBoolean();
      }

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

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new SunlightChampionAI());
      GameRandom var1 = new GameRandom();

      for(int var2 = 0; var2 < 2; ++var2) {
         var1.setSeed((long)(this.getUniqueID() + var2));
         SunlightGauntletMob var3 = new SunlightGauntletMob();
         var3.leftHanded = var2 == 0;
         var3.setLevel(this.getLevel());
         var3.setUniqueID(getNewUniqueID(this.getLevel(), var1));
         var3.setPos(this.x, this.y, true);
         var3.setMaxHealth(this.getMaxHealth());
         var3.setHealthHidden(this.getHealth());
         var3.master.uniqueID = this.getUniqueID();
         if (this.gauntletAttackStatus != null) {
            var3.isAttacking.set(this.gauntletAttackStatus[var2]);
         }

         this.getLevel().entityManager.mobs.addHidden(var3);
         this.gauntlets.add(var3);
      }

      if (this.isClient()) {
         Screen.playSound(GameResources.firespell1, SoundEffect.globalEffect().volume(10.0F).pitch(0.5F));
      }

   }

   public void tickMovement(float var1) {
      float var2 = this.getDesiredHeight();
      float var3 = var2 - this.currentHeight;
      float var4 = Math.abs(var3) * 2.0F + 10.0F;
      float var5 = var4 * var1 / 250.0F;
      if (Math.abs(var3) < var5) {
         this.currentHeight = var2;
      } else {
         this.currentHeight += Math.signum(var3) * var5;
      }

      super.tickMovement(var1);
   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.SunlightsExam, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      int var1 = GameRandom.globalRandom.getIntBetween(-25, 25);
      int var2 = GameRandom.globalRandom.getIntBetween(-1, -10);
      int var3 = GameRandom.globalRandom.getIntBetween(0, 30);
      this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).color(new Color(130 + var3, 120 + var3, 110 + var3)).sizeFades(50, 75).movesConstant((float)var1, (float)var2).height(-80.0F).lifeTime(1000);
      if (this.getTime() < this.superNovaEndTime) {
         this.spawnChargeSupernovaParticles();
         if (this.superNovaRumbleSound != null && !this.superNovaRumbleSound.isDone()) {
            this.superNovaRumbleSound.refreshLooping(1.0F);
         } else {
            this.superNovaRumbleSound = Screen.playSound(GameResources.rumble, SoundEffect.globalEffect().pitch(1.2F).volume(1.5F));
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
   }

   public int getFlyingHeight() {
      return (int)this.currentHeight;
   }

   public float getDesiredHeight() {
      float var1 = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
      float var2 = GameMath.sin(var1 * 360.0F) * 5.0F;
      return (float)((int)var2);
   }

   public int getRespawnTime() {
      return this.isSummoned ? BossMob.getBossRespawnTime(this) : super.getRespawnTime();
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
   }

   private void spawnChargeSupernovaParticles() {
      for(int var1 = 0; var1 < 5; ++var1) {
         int var2 = GameRandom.globalRandom.nextInt(360);
         Point2D.Float var3 = GameMath.getAngleDir((float)var2);
         float var4 = GameRandom.globalRandom.getFloatBetween(25.0F, 40.0F);
         float var5 = this.x + var3.x * var4;
         float var6 = this.y + 20.0F;
         float var7 = 29.0F;
         float var8 = var7 + var3.y * var4;
         int var9 = GameRandom.globalRandom.getIntBetween(200, 500);
         float var10 = var3.x * var4 * 250.0F / (float)var9;
         this.getLevel().entityManager.addTopParticle(var5, var6, this.superNovaParticleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(15, 30).rotates().height(this.currentHeight).heightMoves(var8, var7).movesConstant(-var10, 0.0F).color(new Color(249, 155, 78)).givesLight(35.0F, 1.0F).fadesAlphaTime(100, 50).lifeTime(var9);
         this.getLevel().entityManager.addTopParticle(var5, var6, this.superNovaParticleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(30, 40).rotates().height(this.currentHeight).heightMoves(var8, var7).movesConstant(var10 * 2.0F, 0.0F).color(new Color(255, 233, 73)).givesLight(50.0F, 1.0F).fadesAlphaTime(100, 50).lifeTime(var9);
      }

      AtomicReference var11 = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
      float var12 = 100.0F;
      this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin((Float)var11.get()) * var12, this.y + GameMath.cos((Float)var11.get()) * var12 * 0.75F, this.superNovaParticleTypeSwitcher.next()).color(new Color(249, 155, 78)).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).height(0.0F).moves((var3x, var4x, var5x, var6x, var7x) -> {
         float var8 = (Float)var11.accumulateAndGet(var4x * 150.0F / 250.0F, Float::sum);
         var3x.x = this.x + GameMath.sin(var8) * var12 * var7x;
         var3x.y = this.y + GameMath.cos(var8) * var12 * var7x;
      }).lifeTime(1000).sizeFades(50, 50);
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5);
      int var12 = var8.getDrawY(var6);
      var12 -= this.getFlyingHeight();
      float var13 = GameMath.limit(this.dx / 10.0F, -10.0F, 10.0F);
      byte var14 = 100;
      int var15 = (int)(this.getWorldEntity().getTime() / (long)var14) % 4;
      final TextureDrawOptionsEnd var16 = MobRegistry.Textures.sunlightChampionEye.initDraw().sprite(var15, 0, 64).size(64, 64).rotate(var13).pos(var11 - 32 + (int)var13, var12 - 78);
      final TextureDrawOptionsEnd var17 = MobRegistry.Textures.sunlightChampionChestplate.initDraw().sprite(0, 0, 84, 64).size(84, 64).rotate(var13).light(var10).pos(var11 - 42, var12 - 32);
      final TextureDrawOptionsEnd var18 = MobRegistry.Textures.sunlightChampionJet.initDraw().sprite(var15 % 2, 0, 56, 60).size(56, 60).rotate(var13 * 4.0F).pos(var11 - 28 - (int)(var13 * 2.0F), var12 + 15 + (int)(var13 <= 0.0F ? var13 : -var13));
      var3.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var18.draw();
            var17.draw();
            var16.draw();
         }
      });
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 24;
      int var5 = var3 - 42;
      MobRegistry.Textures.sunlightChampionEye.initDraw().sprite(0, 0, 64).size(48, 48).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-24, -34, 48, 34);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      this.gauntlets.forEach(Mob::remove);
      this.gauntlets.clear();
      this.spawnedProjectiles.forEach(Projectile::remove);
      this.spawnedProjectiles.clear();
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.0F));
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.sunlightChampionChestplate, 1, 0, 84, 64, this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 15.0F, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
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
      privateLootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(uniqueDrops)});
      collisionDamage = new GameDamage(80.0F);
      gauntletCollisionDamage = new GameDamage(100.0F);
      sunlightOrbDamage = new GameDamage(90.0F);
      supernovaDamage = new GameDamage(300.0F);
      MAX_HEALTH = new MaxHealthGetter(30000, 37500, 40000, 42500, 50000);
   }

   public static class SunlightGauntletMob extends FlyingBossMob {
      public final LevelMob<SunlightChampionMob> master = new LevelMob();
      public boolean leftHanded;
      public BooleanNetworkField isAttacking;
      protected final EmptyMobAbility feintAttackAbility;

      public SunlightGauntletMob() {
         super(10);
         this.isSummoned = true;
         this.dropsLoot = false;
         this.collision = new Rectangle(-20, -20, 40, 40);
         this.hitBox = new Rectangle(-30, -30, 60, 60);
         this.selectBox = new Rectangle(-50, -50, 100, 100);
         this.setKnockbackModifier(0.0F);
         this.setRegen(0.0F);
         this.setSpeed(600.0F);
         this.isAttacking = (BooleanNetworkField)this.registerNetworkField(new BooleanNetworkField(false));
         this.feintAttackAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
            protected void run() {
               SunlightGauntletMob.this.dx = 0.0F;
               SunlightGauntletMob.this.dy = 0.0F;
               if (SunlightGauntletMob.this.isClient()) {
                  Screen.playSound(GameResources.explosionLight, SoundEffect.globalEffect().volume(2.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.8F)));
                  SunlightGauntletMob.this.spawnFeintingAttackParticles();
               }
            }
         });
      }

      public boolean shouldSendSpawnPacket() {
         return false;
      }

      public Mob getSpawnPacketMaster() {
         return this.master.get(this.getLevel());
      }

      public void init() {
         super.init();
         this.countStats = false;
      }

      public void clientTick() {
         super.clientTick();
         this.tickMaster();
         int var1 = GameRandom.globalRandom.getIntBetween(0, 30);
         float var10002 = this.y - 20.0F;
         this.getLevel().entityManager.addParticle(this.x, var10002, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).color(new Color(130 + var1, 120 + var1, 110 + var1)).sizeFades(50, 75).height(-20.0F).movesConstantAngle((float)GameRandom.globalRandom.getIntBetween(0, 360), 15.0F).lifeTime(500);
      }

      public void serverTick() {
         super.serverTick();
         this.movementUpdateTime = this.getWorldEntity().getTime();
         this.healthUpdateTime = this.getWorldEntity().getTime();
         this.tickMaster();
      }

      public int getHealth() {
         if (this.master != null) {
            SunlightChampionMob var1 = (SunlightChampionMob)this.master.get(this.getLevel());
            if (var1 != null) {
               return var1.getHealth();
            }
         }

         return super.getHealth();
      }

      public int getMaxHealth() {
         if (this.master != null) {
            SunlightChampionMob var1 = (SunlightChampionMob)this.master.get(this.getLevel());
            if (var1 != null) {
               return var1.getMaxHealth();
            }
         }

         return super.getMaxHealth();
      }

      public void requestServerUpdate() {
      }

      public void tickMaster() {
         if (!this.removed()) {
            if (this.master.get(this.getLevel()) == null) {
               this.remove();
            }

            this.master.computeIfPresent(this.getLevel(), (var1) -> {
               this.setMaxHealth(var1.getMaxHealth());
               this.setHealthHidden(var1.getHealth(), 0.0F, 0.0F, (Attacker)null);
               this.setArmor(var1.getArmorFlat() * 2);
            });
         }
      }

      public int stoppingDistance(float var1, float var2) {
         return 0;
      }

      public void setHealthHidden(int var1, float var2, float var3, Attacker var4, boolean var5) {
         if (this.master != null) {
            this.master.computeIfPresent(this.getLevel(), (var5x) -> {
               var5x.setHealthHidden(var1, var2, var3, var4, var5);
            });
         }

         super.setHealthHidden(var1, var2, var3, var4, var5);
      }

      public boolean isHealthBarVisible() {
         return false;
      }

      public float getIncomingDamageModifier() {
         SunlightChampionMob var1 = (SunlightChampionMob)this.master.get(this.getLevel());
         return var1 == null ? super.getIncomingDamageModifier() : var1.getIncomingDamageModifier();
      }

      public GameDamage getCollisionDamage(Mob var1) {
         return SunlightChampionMob.gauntletCollisionDamage;
      }

      public int getCollisionKnockback(Mob var1) {
         return 350;
      }

      public void spawnFeintingAttackParticles() {
         ParticleTypeSwitcher var1 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
         float var2 = 18.0F;

         for(int var3 = 0; var3 < 20; ++var3) {
            int var4 = (int)((float)var3 * var2 + GameRandom.globalRandom.nextFloat() * var2);
            float var5 = (float)Math.sin(Math.toRadians((double)var4)) * 50.0F;
            float var6 = (float)Math.cos(Math.toRadians((double)var4)) * 50.0F;
            this.getLevel().entityManager.addTopParticle((Entity)this, var1.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).color((var0, var1x, var2x, var3x) -> {
               var0.color(new Color((int)(255.0F - 55.0F * var3x), (int)(225.0F - 200.0F * var3x), (int)(155.0F - 125.0F * var3x)));
            }).movesFriction(var5, var6, 0.8F).heightMoves(0.0F, 10.0F).lifeTime(1000);
         }

      }

      public void spawnDeathParticles(float var1, float var2) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.sunlightGauntlet, 1, 0, 64, 76, this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 15.0F, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

      protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
         super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
         int var11 = var8.getDrawX(var5);
         int var12 = var8.getDrawY(var6);
         var12 -= this.getFlyingHeight();
         float var13 = (float)Math.toDegrees(Math.atan2((double)this.dy, (double)this.dx)) - 90.0F;
         byte var14 = 100;
         int var15 = (int)(this.getWorldEntity().getTime() / (long)var14 % 4L);
         final TextureDrawOptionsEnd var16 = MobRegistry.Textures.sunlightGauntlet.initDraw().sprite(0, 0, 64, 76).size(64, 76).mirror(this.leftHanded, false).rotate(var13).light(var10).pos(var11 - 32, var12);
         final TextureDrawOptionsEnd var17 = MobRegistry.Textures.sunlightGauntletFire.initDraw().sprite(var15, 0, 64, 76).size(64, 76).mirror(this.leftHanded, false).rotate(var13).pos(var11 - 32, var12);
         final TextureDrawOptionsEnd var18 = MobRegistry.Textures.sunlightGauntletJet.initDraw().sprite(var15 % 2, 0, 38, 81).size(38, 81).rotate(var13, 19, 81).posMiddle(var11, var12);
         var3.add(new MobDrawable() {
            public void draw(TickManager var1) {
               if ((Boolean)SunlightGauntletMob.this.isAttacking.get()) {
                  var18.draw();
               } else {
                  var17.draw();
               }

               var16.draw();
            }
         });
      }
   }

   public static class SunlightChampionAI<T extends SunlightChampionMob> extends SequenceAINode<T> {
      public SunlightChampionAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((SunlightChampionMob)var1, var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         this.addChild(new IsolateRunningAINode(var1));
         var1.addChild(new GatherGauntletsStage());
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new SunlightOrbStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new ChargeTargetStage());
         var1.addChild(new RocketPunchStage());
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new RocketPunchStage());
         var1.addChild(new FeintingAttackStage());
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new GatherGauntletsStage());

         for(int var2 = 0; var2 < 2; ++var2) {
            var1.addChild(new ChargeTargetStage());
            var1.addChild(new RocketPunchStage());
            var1.addChild(new ChargeTargetStage());
         }

         var1.addChild(new FeintingAttackStage());
         var1.addChild(new SupernovaStage(3000));
         var1.addChild(new GatherGauntletsStage());
         var1.addChild(new RocketPunchStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 2000);
         }));
         var1.addChild(new SunlightOrbStage());
         var1.addChild(new FeintingAttackStage());
         var1.addChild(new ChargeTargetStage());
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new RocketPunchStage());
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new FeintingAttackStage());
         var1.addChild(new ChargeTargetStage());
      }

      private int getIdleTime(T var1, int var2) {
         float var3 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         return (int)((float)var2 * var3);
      }
   }

   public static class ChargeTargetStage<T extends SunlightChampionMob> extends FlyToOppositeDirectionAttackStage<T> {
      public ChargeTargetStage() {
         super(true, 250.0F, 0.0F);
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         super.onStarted(var1, var2);
         if (var2.mover.isMoving()) {
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, var1, 5.0F, (Attacker)null), true);
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
         super.onEnded(var1, var2);
         var1.buffManager.removeBuff(BuffRegistry.MOVE_SPEED_BURST, true);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SunlightChampionMob)var1, var2);
      }
   }

   public static class SupernovaStage<T extends SunlightChampionMob> extends AINode<T> implements AttackStageInterface<T> {
      private final int attackDuration;
      private long explosionTime;

      public SupernovaStage(int var1) {
         this.attackDuration = var1;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (var1.getTime() < this.explosionTime) {
            return AINodeResult.RUNNING;
         } else {
            SupernovaExplosionEvent var3 = new SupernovaExplosionEvent(var1.x, var1.y, 650, SunlightChampionMob.supernovaDamage, 0, var1);
            var1.getLevel().entityManager.addLevelEvent(var3);
            var1.supernovaSoundAbility.runAndSend();
            return AINodeResult.SUCCESS;
         }
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.explosionTime = var1.getTime() + (long)this.attackDuration;
         var1.startSupernovaCharge.runAndSend(this.attackDuration);
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SunlightChampionMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SunlightChampionMob)var1, var2);
      }
   }

   public static class SunlightOrbStage<T extends SunlightChampionMob> extends AINode<T> implements AttackStageInterface<T> {
      public SunlightOrbStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         return AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         Iterator var3 = var1.getLevel().getServer().getClients().iterator();

         while(var3.hasNext()) {
            ServerClient var4 = (ServerClient)var3.next();
            PlayerMob var5 = var4.playerMob;
            ArrayList var6 = new ArrayList();
            byte var7 = 5;

            int var8;
            for(var8 = -var7; var8 <= var7; ++var8) {
               int var9 = var5.getTileX() + var8;

               for(int var10 = -var7; var10 <= var7; ++var10) {
                  int var11 = var5.getTileY() + var10;
                  if (!var1.getLevel().isSolidTile(var9, var11)) {
                     var6.add(new Point(var9 * 32 + 16, var11 * 32 + 16));
                  }
               }
            }

            for(var8 = 0; var8 < 2; ++var8) {
               if (!var6.isEmpty()) {
                  Point var12 = (Point)var6.remove(GameRandom.globalRandom.nextInt(var6.size()));
                  SunlightOrbEvent var13 = new SunlightOrbEvent(var1, var12.x, var12.y, GameRandom.globalRandom, SunlightChampionMob.sunlightOrbDamage, 1000L);
                  var1.getLevel().entityManager.addLevelEvent(var13);
               }
            }
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SunlightChampionMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SunlightChampionMob)var1, var2);
      }
   }

   public static class FeintingAttackStage<T extends SunlightChampionMob> extends AINode<T> implements AttackStageInterface<T> {
      public FeintingAttackStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         return AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         Iterator var4 = var1.gauntlets.iterator();

         while(var4.hasNext()) {
            SunlightGauntletMob var5 = (SunlightGauntletMob)var4.next();
            if ((Boolean)var5.isAttacking.get()) {
               var1.rocketPunchSoundAbility.runAndSend();
               var5.feintAttackAbility.runAndSend();
               var5.setMovement(new MobMovementRelative(var3, 0.0F, 0.0F));
               break;
            }
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SunlightChampionMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SunlightChampionMob)var1, var2);
      }
   }

   public static class RocketPunchStage<T extends SunlightChampionMob> extends AINode<T> implements AttackStageInterface<T> {
      public RocketPunchStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         return AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         Iterator var4 = var1.gauntlets.iterator();

         while(var4.hasNext()) {
            SunlightGauntletMob var5 = (SunlightGauntletMob)var4.next();
            if (!(Boolean)var5.isAttacking.get()) {
               var1.rocketPunchSoundAbility.runAndSend();
               var5.isAttacking.set(true);
               var5.setMovement(new MobMovementRelative(var3, 0.0F, 0.0F));
               break;
            }
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SunlightChampionMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SunlightChampionMob)var1, var2);
      }
   }

   public static class GatherGauntletsStage<T extends SunlightChampionMob> extends AINode<T> implements AttackStageInterface<T> {
      public GatherGauntletsStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         return AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         Iterator var3 = var1.gauntlets.iterator();

         while(var3.hasNext()) {
            SunlightGauntletMob var4 = (SunlightGauntletMob)var3.next();
            var4.isAttacking.set(false);
            var4.setMovement(new MobMovementRelative(var1, var4.leftHanded ? 100.0F : -100.0F, 0.0F));
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SunlightChampionMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SunlightChampionMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SunlightChampionMob)var1, var2);
      }
   }
}
