package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.FloatMobAbility;
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
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.StarBarrierPickupEntity;
import necesse.entity.projectile.CrescentDiscFollowingProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.StarVeilProjectile;
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

public class MoonlightDancerMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "moonlightsrehearsalvinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("duskhelmet", 1, (new GNDItemMap()).setInt("upgradeLevel", 100)), new LootItem("duskchestplate", 1, (new GNDItemMap()).setInt("upgradeLevel", 100)), new LootItem("duskboots", 1, (new GNDItemMap()).setInt("upgradeLevel", 100)));
   public static LootTable privateLootTable;
   public float currentHeight;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
   public final int defaultSpeed = 50;
   public final int invincibleSpeed = 200;
   public static GameDamage collisionDamage;
   public static GameDamage starVeilDamage;
   public static GameDamage crescentDiscDamage;
   public static GameDamage astralShotgunDamage;
   public static GameDamage crushingDarknessDamage;
   protected final BooleanMobAbility setInvincibilityAbility;
   protected final FloatMobAbility setInvincibilityAlphaAbility;
   public boolean isInvincible;
   public float invincibilityAlpha = 0.0F;
   protected final EmptyMobAbility chargeAstralShotgunParticleAbility;
   protected final EmptyMobAbility starVeilSoundAbility;
   protected final EmptyMobAbility crescentDiscSoundAbility;
   public static MaxHealthGetter MAX_HEALTH;

   public MoonlightDancerMob() {
      super(35000);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.setSpeed(50.0F);
      this.setArmor(20);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-40, -30, 80, 80);
      this.hitBox = new Rectangle(-25, -60, 50, 160);
      this.selectBox = new Rectangle(-70, -85, 140, 210);
      this.setInvincibilityAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            Screen.playSound(GameResources.jingle, SoundEffect.globalEffect().volume(0.5F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 0.3F)));
            MoonlightDancerMob.this.setSpeed(var1 ? 200.0F : 50.0F);
            MoonlightDancerMob.this.isInvincible = var1;
         }
      });
      this.setInvincibilityAlphaAbility = (FloatMobAbility)this.registerAbility(new FloatMobAbility() {
         protected void run(float var1) {
            MoonlightDancerMob.this.invincibilityAlpha = var1;
         }
      });
      this.chargeAstralShotgunParticleAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (MoonlightDancerMob.this.isClient()) {
               MoonlightDancerMob.this.spawnChargingParticles();
            }
         }
      });
      this.starVeilSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (MoonlightDancerMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt1, SoundEffect.globalEffect().volume(0.75F).pitch(GameRandom.globalRandom.getFloatBetween(0.75F, 0.8F)));
            }
         }
      });
      this.crescentDiscSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (MoonlightDancerMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt2, SoundEffect.globalEffect().volume(2.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.75F, 0.8F)));
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
      var1.putNextBoolean(this.isInvincible);
      var1.putNextFloat(this.invincibilityAlpha);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.setSpeed((float)var1.getNextInt());
      this.isInvincible = var1.getNextBoolean();
      this.setSpeed(this.isInvincible ? 200.0F : 50.0F);
      this.invincibilityAlpha = var1.getNextFloat();
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
      this.ai = new BehaviourTreeAI(this, new MoonlightDancerAI());
      if (this.isClient()) {
         Screen.playSound(GameResources.firespell1, SoundEffect.globalEffect().pitch(1.0F));
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
      Screen.setMusic(MusicRegistry.MoonlightsRehearsal, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
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

   public boolean canCollisionHit(Mob var1) {
      return this.isInvincible ? false : super.canCollisionHit(var1);
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
   }

   public boolean canBeHit(Attacker var1) {
      return this.isInvincible ? false : super.canBeHit(var1);
   }

   public void spawnChargingParticles() {
      GameRandom var1 = GameRandom.globalRandom;
      AtomicReference var2 = new AtomicReference(var1.nextFloat() * 360.0F);
      float var3 = 50.0F;
      this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin((Float)var2.get()) * var3, this.y + GameMath.cos((Float)var2.get()) * var3, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(var1.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).givesLight(247.0F, 0.3F).moves((var3x, var4, var5, var6, var7) -> {
         float var8 = (Float)var2.accumulateAndGet(var4 * 50.0F / 250.0F, Float::sum);
         var3x.x = this.x + GameMath.sin(var8) * var3;
         var3x.y = this.y + 50.0F + (this.x - var3x.x) - var8 / 10.0F + GameMath.cos(var8) * var3;
      }).lifeTime(1000).sizeFades(22, 44);
      this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin((Float)var2.get()) * var3, this.y + GameMath.cos((Float)var2.get()) * var3, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(var1.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).givesLight(247.0F, 0.3F).moves((var3x, var4, var5, var6, var7) -> {
         float var8 = (Float)var2.accumulateAndGet(var4 * 50.0F / 250.0F, Float::sum);
         var3x.x = this.x + GameMath.sin(var8) * var3;
         var3x.y = this.y + 50.0F - (this.x - var3x.x) - var8 / 10.0F + GameMath.cos(var8) * var3;
      }).lifeTime(1000).sizeFades(22, 44);
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      float var11 = GameMath.limit(this.dx / 10.0F, -10.0F, 10.0F);
      int var12 = var8.getDrawX(var5) - 64;
      int var13 = var8.getDrawY(var6) - 80;
      var13 -= this.getFlyingHeight();
      short var16 = 150;
      int var17 = (int)(this.getWorldEntity().getTime() / (long)var16) % 8;
      final TextureDrawOptionsEnd var15 = MobRegistry.Textures.moonlightDancer.initDraw().sprite(var17, 0, 128, 178).size(128, 178).rotate(var11).light(var10).pos(var12, var13).alpha(1.0F - this.invincibilityAlpha / 2.0F);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.moonlightDancerInvincible.initDraw().sprite(var17, 0, 128, 178).size(128, 178).rotate(var11).light(var10).pos(var12, var13).alpha(this.invincibilityAlpha);
      var3.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
            var14.draw();
         }
      });
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 24;
      int var5 = var3 - 23;
      MobRegistry.Textures.moonlightDancerHead.initDraw().sprite(0, 0, 48, 46).size(48, 46).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-24, -34, 48, 34);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      this.spawnedProjectiles.forEach(Projectile::remove);
      this.spawnedProjectiles.clear();
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.0F));
   }

   public void spawnDeathParticles(float var1, float var2) {
      GameRandom var3 = GameRandom.globalRandom;

      int var4;
      for(var4 = 0; var4 < 5; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.moonlightDancerDebris, var4, 0, 32, this.x + var3.floatGaussian() * 15.0F, this.y + var3.floatGaussian() * 15.0F, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

      for(var4 = 0; var4 < 50; ++var4) {
         this.getLevel().entityManager.addTopParticle(this.x + var3.floatGaussian() * 16.0F, this.y + var3.floatGaussian() * 12.0F, Particle.GType.IMPORTANT_COSMETIC).sizeFades(22, 11).sprite(GameResources.magicSparkParticles.sprite(var3.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).movesFrictionAngle((float)var3.getIntBetween(0, 360), 150.0F, 0.5F).lifeTime(5000).givesLight(75.0F, 0.5F);
      }

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
      collisionDamage = new GameDamage(50.0F);
      starVeilDamage = new GameDamage(85.0F);
      crescentDiscDamage = new GameDamage(100.0F);
      astralShotgunDamage = new GameDamage(100.0F);
      crushingDarknessDamage = new GameDamage(300.0F);
      MAX_HEALTH = new MaxHealthGetter(27500, 32500, 35000, 37500, 42500);
   }

   public static class MoonlightDancerAI<T extends MoonlightDancerMob> extends SequenceAINode<T> {
      public MoonlightDancerAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((MoonlightDancerMob)var1, var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         this.addChild(new IsolateRunningAINode(var1));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 200));
         var1.addChild(new StartInvincibilityStage());
         var1.addChild(new CrushingDarknessStage(7500, 10000));
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new AstralShotgun(20, 3000));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 500));
         var1.addChild(new AstralShotgun(20, 3000));
         var1.addChild(new EndInvincibilityStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 2000);
         }));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 200));
         var1.addChild(new CrescentDiscStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 200));
         var1.addChild(new StarVeil());
      }

      private int getIdleTime(T var1, int var2) {
         float var3 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         return (int)((float)var2 * var3);
      }
   }

   public static class AstralShotgun<T extends MoonlightDancerMob> extends AINode<T> implements AttackStageInterface<T> {
      private final int totalProjectiles;
      private final int attackDuration;
      private int projectilesRemaining;
      private float attackBuffer;
      private float elapsedTime;

      public AstralShotgun(int var1, int var2) {
         this.totalProjectiles = var1;
         this.attackDuration = var2;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (this.elapsedTime < (float)this.attackDuration / 2.0F) {
            var1.chargeAstralShotgunParticleAbility.runAndSend();
            this.elapsedTime += 50.0F;
         } else {
            this.attackBuffer += 50.0F;
            if (this.attackBuffer > (float)(this.attackDuration / 2) / (float)this.totalProjectiles) {
               GameRandom var3 = GameRandom.globalRandom;
               Mob var4 = (Mob)var2.getObject(Mob.class, "currentTarget");
               float var5 = var4.x + (float)var3.getIntBetween(-200, 200);
               float var6 = var4.y + (float)var3.getIntBetween(-200, 200);
               StarVeilProjectile var7 = new StarVeilProjectile(var1.x, var1.y, var5, var6, MoonlightDancerMob.astralShotgunDamage, 200.0F, var1);
               var1.getLevel().entityManager.projectiles.add(var7);
               var1.spawnedProjectiles.add(var7);
               var1.starVeilSoundAbility.runAndSend();
               --this.projectilesRemaining;
            }
         }

         return this.projectilesRemaining > 0 ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.elapsedTime = 0.0F;
         this.projectilesRemaining = this.totalProjectiles;
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (MoonlightDancerMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((MoonlightDancerMob)var1, var2);
      }
   }

   public static class CrescentDiscStage<T extends MoonlightDancerMob> extends AINode<T> implements AttackStageInterface<T> {
      public CrescentDiscStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         CrescentDiscFollowingProjectile var4 = new CrescentDiscFollowingProjectile(var1.getLevel(), var1.x, var1.y, var3.x, var3.y, 25.0F, 3000, MoonlightDancerMob.crescentDiscDamage, var1);
         var1.getLevel().entityManager.projectiles.add(var4);
         var1.spawnedProjectiles.add(var4);
         var1.crescentDiscSoundAbility.runAndSend();
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         return AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (MoonlightDancerMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((MoonlightDancerMob)var1, var2);
      }
   }

   public static class EndInvincibilityStage<T extends MoonlightDancerMob> extends AINode<T> implements AttackStageInterface<T> {
      float alpha;

      public EndInvincibilityStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.alpha -= 0.016666668F;
         var1.setInvincibilityAlphaAbility.runAndSend(this.alpha);
         return this.alpha <= 0.0F ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.alpha = 1.0F;
         if (var1 != null) {
            var1.setInvincibilityAbility.runAndSend(false);
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (MoonlightDancerMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((MoonlightDancerMob)var1, var2);
      }
   }

   public static class StartInvincibilityStage<T extends MoonlightDancerMob> extends AINode<T> implements AttackStageInterface<T> {
      float alpha;

      public StartInvincibilityStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.alpha += 0.016666668F;
         var1.setInvincibilityAlphaAbility.runAndSend(this.alpha);
         return this.alpha >= 1.0F ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.alpha = 0.0F;
         if (var1 != null) {
            var1.setInvincibilityAbility.runAndSend(true);
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (MoonlightDancerMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((MoonlightDancerMob)var1, var2);
      }
   }

   public static class StarVeil<T extends MoonlightDancerMob> extends AINode<T> implements AttackStageInterface<T> {
      public int direction;
      private float angleBuffer;
      private float remainingAngle;
      private float currentAngle;
      private final float totalAngle = 1080.0F;

      public StarVeil() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.angleBuffer += 18.0F;
         float var3 = 13.0F;

         while(this.angleBuffer >= var3) {
            this.currentAngle -= var3;
            StarVeilProjectile var4 = new StarVeilProjectile(var1.x, var1.y, this.currentAngle, MoonlightDancerMob.starVeilDamage, 25.0F, var1);
            var1.getLevel().entityManager.projectiles.add(var4);
            var1.spawnedProjectiles.add(var4);
            var1.starVeilSoundAbility.runAndSend();
            this.angleBuffer -= var3;
            this.remainingAngle -= var3;
            if (this.remainingAngle < 1.0F) {
               break;
            }
         }

         return this.angleBuffer >= this.remainingAngle ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.remainingAngle = 1080.0F;
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (MoonlightDancerMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((MoonlightDancerMob)var1, var2);
      }
   }

   public static class CrushingDarknessStage<T extends MoonlightDancerMob> extends AINode<T> implements AttackStageInterface<T> {
      private final Function<T, Integer> attackDuration;

      public CrushingDarknessStage(Function<T, Integer> var1) {
         this.attackDuration = var1;
      }

      public CrushingDarknessStage(int var1, int var2) {
         this((var2x) -> {
            int var3 = var2 - var1;
            float var4 = (float)var2x.getHealth() / (float)var2x.getMaxHealth();
            return var1 + (int)((float)var3 * var4);
         });
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         return AINodeResult.SUCCESS;
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         GameUtils.streamServerClients(var1.getServer(), var1.getLevel()).forEach((var2x) -> {
            PlayerMob var3 = var2x.playerMob;
            ActiveBuff var4 = new ActiveBuff(BuffRegistry.Debuffs.CRUSHING_DARKNESS, var3, (Integer)this.attackDuration.apply(var1), var1);
            var4.getGndData().setInt("uniqueID", var1.getUniqueID());
            var3.buffManager.addBuff(var4, true);
            ArrayList var5 = new ArrayList();
            byte var6 = 15;

            int var7;
            for(var7 = -var6; var7 <= var6; ++var7) {
               int var8 = var3.getTileX() + var7;

               for(int var9 = -var6; var9 <= var6; ++var9) {
                  int var10 = var3.getTileY() + var9;
                  if (!var1.getLevel().isSolidTile(var8, var10)) {
                     var5.add(new Point(var8 * 32 + 16, var10 * 32 + 16));
                  }
               }
            }

            for(var7 = 0; var7 < 4; ++var7) {
               if (!var5.isEmpty()) {
                  Point var11 = (Point)var5.remove(GameRandom.globalRandom.nextInt(var5.size()));
                  StarBarrierPickupEntity var12 = new StarBarrierPickupEntity(var1.getLevel(), (float)var11.x, (float)var11.y, 0.0F, 0.0F);
                  var1.getLevel().entityManager.pickups.add(var12);
               } else {
                  var3.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAR_BARRIER_BUFF, var3, 20000, (Attacker)null), true);
               }
            }

         });
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (MoonlightDancerMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((MoonlightDancerMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((MoonlightDancerMob)var1, var2);
      }
   }
}
