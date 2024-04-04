package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GamePoint3D;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InverseKinematics;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToOppositeDirectionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.EmpressAcidProjectile;
import necesse.entity.projectile.EmpressSlashProjectile;
import necesse.entity.projectile.EmpressSlashWarningProjectile;
import necesse.entity.projectile.EmpressWebBallProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderEmpressMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "wrathoftheempressvinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("empressmask", 1));
   public static LootTable privateLootTable;
   public static MaxHealthGetter MAX_HEALTH;
   public static float ACID_LINGER_SECONDS;
   public static float ACID_LINGER_SECONDS_RAGE;
   public static GameDamage collisionDamage;
   public static GameDamage rageCollisionDamage;
   public static GameDamage slashDamage;
   public static GameDamage webBallDamage;
   public static GameDamage acidProjectileDamage;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   private ArrayList<SpiderEmpressLeg> frontLegs;
   private ArrayList<SpiderEmpressLeg> backLegs;
   public SpiderEmpressArm leftArm = new SpiderEmpressArm(this, true);
   public SpiderEmpressArm rightArm = new SpiderEmpressArm(this, false);
   public float currentHeight;
   public boolean isRaging = false;
   public float rageProgress = 0.0F;
   public float passiveRageGenerationTime = 60.0F;
   public float passiveRageDecayTime = 30.0F;
   public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
   protected final BooleanMobAbility setRagingAbility;
   protected final EmptyMobAbility setAcidBreathAnimationAbility;
   public boolean isAcidBreathing;
   public int acidBreathingParticleTimer;
   protected final EmptyMobAbility setWebVolleyAnimationAbility;
   protected final EmptyMobAbility setScreenSlashWarningAnimationAbility;
   protected final EmptyMobAbility setScreenSlashAttackAnimationAbility;
   protected final EmptyMobAbility setIdleAttackAnimationAbility;
   protected final EmptyMobAbility roarAbility;
   protected final EmptyMobAbility slashSoundAbility;
   protected final EmptyMobAbility acidSoundAbility;
   protected final EmptyMobAbility chargeSoundAbility;
   protected final EmptyMobAbility webBallSoundAbility;

   public SpiderEmpressMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.setSpeed(120.0F);
      this.setArmor(20);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-40, -30, 80, 80);
      this.hitBox = new Rectangle(-30, -90, 60, 120);
      this.selectBox = new Rectangle(-75, -120, 150, 200);
      this.setRaging(false);
      this.setRagingAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            SpiderEmpressMob.this.setRaging(var1);
         }
      });
      this.setAcidBreathAnimationAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               SpiderEmpressMob.this.isAcidBreathing = true;
               SpiderEmpressMob.this.addAcidBreathParticles();
               SpiderEmpressMob.this.leftArm.doAcidBreathAnimation();
               SpiderEmpressMob.this.rightArm.doAcidBreathAnimation();
            }
         }
      });
      this.setWebVolleyAnimationAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               SpiderEmpressMob.this.leftArm.doWebVolleyAnimation();
               SpiderEmpressMob.this.rightArm.doWebVolleyAnimation();
            }
         }
      });
      this.setScreenSlashWarningAnimationAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               SpiderEmpressMob.this.leftArm.doScreenSlashWarningAnimation();
               SpiderEmpressMob.this.rightArm.doScreenSlashWarningAnimation();
            }
         }
      });
      this.setScreenSlashAttackAnimationAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               SpiderEmpressMob.this.leftArm.doScreenSlashAttackAnimation();
               SpiderEmpressMob.this.rightArm.doScreenSlashAttackAnimation();
            }
         }
      });
      this.setIdleAttackAnimationAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               SpiderEmpressMob.this.isAcidBreathing = false;
               SpiderEmpressMob.this.leftArm.returnToIdleAnimation();
               SpiderEmpressMob.this.rightArm.returnToIdleAnimation();
            }
         }
      });
      this.roarAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               Screen.playSound(GameResources.shears, SoundEffect.globalEffect().pitch(0.2F));
            }
         }
      });
      this.slashSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt4, SoundEffect.globalEffect().volume(0.75F).pitch(GameRandom.globalRandom.getFloatBetween(0.75F, 0.8F)));
            }
         }
      });
      this.acidSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               Screen.playSound(GameResources.spit, SoundEffect.globalEffect().volume(6.0F).pitch(0.45F));
            }
         }
      });
      this.chargeSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               Screen.playSound(GameResources.roar, SoundEffect.globalEffect().volume(2.0F).pitch(3.0F));
            }
         }
      });
      this.webBallSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (SpiderEmpressMob.this.isClient()) {
               Screen.playSound(GameResources.swing1, SoundEffect.globalEffect().volume(0.2F).pitch(GameRandom.globalRandom.getFloatBetween(0.75F, 0.8F)));
            }
         }
      });
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      this.setRaging(this.isRaging);
      var1.putNextBoolean(this.isAcidBreathing);
      this.leftArm.setupSpawnPacket(var1);
      this.rightArm.setupSpawnPacket(var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isRaging = var1.getNextBoolean();
      this.isAcidBreathing = var1.getNextBoolean();
      this.leftArm.applySpawnPacket(var1);
      this.rightArm.applySpawnPacket(var1);
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

   public void setHealthHidden(int var1, float var2, float var3, Attacker var4, boolean var5) {
      int var6 = this.getHealth();
      super.setHealthHidden(var1, var2, var3, var4, var5);
      int var7 = var6 - this.getHealth();
      if (var7 > 0) {
         this.rageProgress += (float)var7 / (float)this.getMaxHealth();
      }

   }

   public void setPos(float var1, float var2, boolean var3) {
      super.setPos(var1, var2, var3);
      if (var3 && this.backLegs != null && this.frontLegs != null) {
         Iterator var4 = this.backLegs.iterator();

         SpiderEmpressLeg var5;
         while(var4.hasNext()) {
            var5 = (SpiderEmpressLeg)var4.next();
            var5.snapToPosition();
         }

         var4 = this.frontLegs.iterator();

         while(var4.hasNext()) {
            var5 = (SpiderEmpressLeg)var4.next();
            var5.snapToPosition();
         }
      }

   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new SpiderCastleBossAI(), new FlyingAIMover());
      if (this.isClient()) {
         Screen.playSound(GameResources.shears, SoundEffect.globalEffect().pitch(0.2F));
      }

      this.currentHeight = this.getDesiredHeight();
      this.frontLegs = new ArrayList();
      this.backLegs = new ArrayList();
      byte var1 = 8;
      float[] var2 = new float[]{65.0F, 95.0F, 125.0F, 155.0F, -65.0F, -95.0F, -125.0F, -155.0F};

      for(int var3 = 0; var3 < var1; ++var3) {
         final float var4 = var2[var3] - 90.0F;
         boolean var5 = var2[var3] > 0.0F;
         float var6 = (float)(var3 + (var3 % 2 == 0 ? 4 : 0)) / (float)var1 % 1.0F;
         final Point2D.Float var7 = GameMath.getAngleDir(var4);
         float var8 = 170.0F;
         float var9 = 170.0F;
         if (var7.x < 0.0F) {
            var9 = 0.0F;
         } else if (var7.x > 0.0F) {
            var8 = 0.0F;
         }

         SpiderEmpressLeg var10 = new SpiderEmpressLeg(this, 80.0F, var6, var8, var9, var5) {
            public GamePoint3D getCenterPosition() {
               byte var1 = 10;
               return new GamePoint3D((float)SpiderEmpressMob.this.getDrawX() + var7.x * (float)var1, (float)SpiderEmpressMob.this.getDrawY() + var7.y * (float)var1 * 0.2F, (float)(SpiderEmpressMob.this.getFlyingHeight() + 30));
            }

            public GamePoint3D getDesiredPosition() {
               Point2D.Float var1 = GameMath.getAngleDir(var4);
               int var2 = SpiderEmpressMob.this.isRaging ? 90 : 60;
               float var3 = Math.min(SpiderEmpressMob.this.getCurrentSpeed() / (SpiderEmpressMob.this.isRaging ? 200.0F : 150.0F), 1.0F);
               Point2D.Float var4x = GameMath.normalize(SpiderEmpressMob.this.dx, SpiderEmpressMob.this.dy);
               if (var4x.y < 0.0F) {
                  var3 *= 1.0F + -var4x.y * 80.0F / SpiderEmpressMob.this.getSpeed();
               }

               return new GamePoint3D((float)SpiderEmpressMob.this.getDrawX() + var1.x * (float)var2 + var4x.x * (float)var2 * var3, (float)SpiderEmpressMob.this.getDrawY() + var1.y * (float)var2 + var4x.y * (float)var2 * var3, 0.0F);
            }

            public float getJumpHeight() {
               return 40.0F;
            }
         };
         if (var7.y < 0.0F) {
            this.backLegs.add(var10);
         } else {
            this.frontLegs.add(var10);
         }
      }

      this.frontLegs.sort(Comparator.comparingDouble((var0) -> {
         return (double)var0.y;
      }));
      this.backLegs.sort(Comparator.comparingDouble((var0) -> {
         return (double)var0.y;
      }));
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
      Iterator var6 = this.backLegs.iterator();

      SpiderEmpressLeg var7;
      while(var6.hasNext()) {
         var7 = (SpiderEmpressLeg)var6.next();
         var7.tickMovement(var1);
      }

      var6 = this.frontLegs.iterator();

      while(var6.hasNext()) {
         var7 = (SpiderEmpressLeg)var6.next();
         var7.tickMovement(var1);
      }

      this.leftArm.tickMovement(var1);
      this.rightArm.tickMovement(var1);
   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.WrathOfTheEmpress, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      if (this.isRaging) {
         this.addRageParticles();
      }

      if (this.isAcidBreathing) {
         for(this.acidBreathingParticleTimer += 50; this.acidBreathingParticleTimer >= 25; this.acidBreathingParticleTimer -= 25) {
            this.addAcidBreathParticles();
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      if (!this.isRaging) {
         this.rageProgress += 1.0F / (this.passiveRageGenerationTime * 20.0F);
      } else {
         this.rageProgress -= 1.0F / (this.passiveRageDecayTime * 20.0F);
      }

   }

   protected void setRaging(boolean var1) {
      this.isRaging = var1;
      if (var1) {
         this.setArmor(60);
      } else {
         this.setArmor(20);
      }

      if (var1 && this.isClient()) {
         Screen.playSound(GameResources.shears, SoundEffect.globalEffect().pitch(0.2F));
         this.addInitialRageParticles();
      }

   }

   public int getFlyingHeight() {
      return (int)this.currentHeight;
   }

   public float getDesiredHeight() {
      float var1 = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
      float var2 = GameMath.sin(var1 * 360.0F) * 5.0F;
      return this.isAcidBreathing ? var2 - 20.0F : (float)(20 + (int)var2 - (this.isRaging ? 20 : 0));
   }

   public int getRespawnTime() {
      return this.isSummoned ? BossMob.getBossRespawnTime(this) : super.getRespawnTime();
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.isRaging ? rageCollisionDamage : collisionDamage;
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

   private void addInitialRageParticles() {
      ParticleTypeSwitcher var1 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      float var2 = 9.0F;

      for(int var3 = 0; var3 < 40; ++var3) {
         int var4 = (int)((float)var3 * var2 + GameRandom.globalRandom.nextFloat() * var2);
         float var5 = (float)Math.sin(Math.toRadians((double)var4)) * (float)GameRandom.globalRandom.getIntBetween(140, 150);
         float var6 = (float)Math.cos(Math.toRadians((double)var4)) * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8F;
         this.getLevel().entityManager.addParticle((Entity)this, var1.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 3), 0, 22)).sizeFades(30, 100).movesFriction(var5, var6, 0.8F).color(new Color(228, 92, 95)).heightMoves(0.0F, 10.0F).lifeTime(1500);
      }

   }

   private void addRageParticles() {
      GameRandom var1 = GameRandom.globalRandom;

      int var2;
      for(var2 = 0; var2 < 5; ++var2) {
         this.getLevel().entityManager.addParticle((Entity)this, (float)var1.getIntBetween(-25, 25), (float)var1.getIntBetween(-20, 20) * 0.75F - 25.0F, Particle.GType.CRITICAL).color(new Color(228, 92, 95)).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).movesConstant(0.0F, -5.0F).height(75.0F).lifeTime(1000).givesLight(0.0F, 75.0F).sizeFades(32, 48);
      }

      for(var2 = 0; var2 < 1; ++var2) {
         this.getLevel().entityManager.addParticle(this.x + (float)var1.getIntBetween(-25, 25), this.y + (float)var1.getIntBetween(-20, 20) * 0.75F, Particle.GType.CRITICAL).color(new Color(228, 92, 95)).movesConstant(0.0F, -5.0F).height(75.0F).lifeTime(1000).givesLight(0.0F, 75.0F).sizeFades(16, 24);
      }

   }

   public void addAcidBreathParticles() {
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
         this.getLevel().entityManager.addTopParticle(var5, var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 26).rotates().height(this.currentHeight).heightMoves(var8, var7).movesConstant(-var10, 0.0F).color(new Color(166, 204, 52)).givesLight(74.0F, 1.0F).fadesAlphaTime(100, 50).lifeTime(var9);
         this.getLevel().entityManager.addTopParticle(var5, var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().height(this.currentHeight).heightMoves(var8, var7).movesConstant(var10 * 2.0F, 0.0F).color(new Color(166, 204, 52)).givesLight(74.0F, 1.0F).fadesAlphaTime(100, 50).lifeTime(var9);
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 7; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.spiderEmpressDebris, var3, 0, 32, this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 15.0F, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5);
      int var12 = var8.getDrawY(var6);
      var12 -= this.getFlyingHeight();
      float var13 = GameMath.limit(this.dx / 10.0F, -10.0F, 10.0F);
      GameTexture var14;
      GameTexture var15;
      GameTexture var16;
      if (this.isRaging) {
         var14 = MobRegistry.Textures.spiderEmpressRageHead;
         var15 = MobRegistry.Textures.spiderEmpressRageTorso;
         var16 = MobRegistry.Textures.spiderEmpressRageDress;
      } else {
         var14 = MobRegistry.Textures.spiderEmpressHead;
         var15 = MobRegistry.Textures.spiderEmpressTorso;
         var16 = MobRegistry.Textures.spiderEmpressDress;
      }

      TextureDrawOptionsEnd var17 = var16.initDraw().light(var10).rotate(var13 * 4.0F, MobRegistry.Textures.spiderEmpressDress.getWidth() / 2, MobRegistry.Textures.spiderEmpressDress.getHeight()).posMiddle(var11 - (int)(var13 * 2.5F), var12 + 20 - 57 + (int)(var13 <= 0.0F ? var13 : -var13));
      TextureDrawOptionsEnd var18 = var15.initDraw().light(var10).rotate(var13 * 2.0F, MobRegistry.Textures.spiderEmpressTorso.getWidth() / 2, MobRegistry.Textures.spiderEmpressTorso.getHeight()).posMiddle(var11, var12 - 18 - 57);
      TextureDrawOptionsEnd var19 = var14.initDraw().light(var10).rotate(var13 * 0.6F, MobRegistry.Textures.spiderEmpressHead.getWidth() / 2, MobRegistry.Textures.spiderEmpressHead.getHeight() + 150).posMiddle(var11, var12 - 54 - 57);
      DrawOptionsList var20 = new DrawOptionsList();
      DrawOptionsList var21 = new DrawOptionsList();
      DrawOptionsList var22 = new DrawOptionsList();
      DrawOptionsList var23 = new DrawOptionsList();
      DrawOptionsList var24 = new DrawOptionsList();
      Iterator var25 = this.backLegs.iterator();

      SpiderEmpressLeg var26;
      while(var25.hasNext()) {
         var26 = (SpiderEmpressLeg)var25.next();
         var26.addDrawOptions(var20, var21, var22, var4, var8, this.isRaging);
      }

      var25 = this.frontLegs.iterator();

      while(var25.hasNext()) {
         var26 = (SpiderEmpressLeg)var25.next();
         var26.addDrawOptions(var20, var23, var24, var4, var8, this.isRaging);
      }

      DrawOptionsList var28 = new DrawOptionsList();
      DrawOptionsList var29 = new DrawOptionsList();
      DrawOptionsList var27 = new DrawOptionsList();
      this.leftArm.addDrawOptions(var28, var29, var27, var4, var5 + (int)var13, var6 + (int)var13, var8, this.isRaging);
      this.rightArm.addDrawOptions(var28, var29, var27, var4, var5 + (int)var13, var6 - (int)var13, var8, this.isRaging);
      var3.add((var11x) -> {
         var20.draw();
         var17.draw();
         var21.draw();
         var22.draw();
         var23.draw();
         var24.draw();
         var27.draw();
         var18.draw();
         var19.draw();
         var29.draw();
         var28.draw();
      });
   }

   public float getSoundPositionY() {
      return super.getSoundPositionY() - 40.0F;
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 24;
      int var5 = var3 - 42;
      MobRegistry.Textures.spiderEmpressHead.initDraw().sprite(0, 0, 62, 54).size(48, 48).draw(var4, var5);
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

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), (ServerClient)((PlayerMob)var1x).getServerClient());
      });
   }

   protected void addHoverTooltips(ListGameTooltips var1, boolean var2) {
      super.addHoverTooltips(var1, var2);
      if (var2) {
         var1.add("Raging: " + this.isRaging);
      }

   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(uniqueDrops)});
      MAX_HEALTH = new MaxHealthGetter(22000, 27000, 30000, 33000, 38000);
      ACID_LINGER_SECONDS = 15.0F;
      ACID_LINGER_SECONDS_RAGE = 10.0F;
      collisionDamage = new GameDamage(60.0F);
      rageCollisionDamage = new GameDamage(70.0F);
      slashDamage = new GameDamage(85.0F);
      webBallDamage = new GameDamage(80.0F);
      acidProjectileDamage = new GameDamage(90.0F);
   }

   public static class SpiderEmpressArm {
      private final SpiderEmpressMob mob;
      private final boolean isLeftHand;
      private boolean targetReached;
      private boolean isAttacking;
      private float handRotation;
      private float targetHandRotation;
      private float moveSpeed = 50.0F;
      private float targetX;
      private float targetY;
      public float currentX;
      public float currentY;
      public InverseKinematics limbs;

      public SpiderEmpressArm(SpiderEmpressMob var1, boolean var2) {
         this.isLeftHand = var2;
         this.mob = var1;
         this.targetX = var2 ? 50.0F : -50.0F;
         this.targetY = -100.0F;
         this.currentX = this.targetX;
         this.currentY = this.targetY;
         this.handRotation = 0.0F;
         this.targetHandRotation = 0.0F;
         this.isAttacking = false;
         if (var2) {
            this.limbs = InverseKinematics.startFromPoints(15.0F, -90.0F, 35.0F, -90.0F, 140.0F, -10.0F);
            this.limbs.addJointPoint(65.0F, -90.0F);
         } else {
            this.limbs = InverseKinematics.startFromPoints(-15.0F, -90.0F, -35.0F, -90.0F, -10.0F, 140.0F);
            this.limbs.addJointPoint(-65.0F, -90.0F);
         }

      }

      public void setupSpawnPacket(PacketWriter var1) {
         var1.putNextFloat(this.currentX);
         var1.putNextFloat(this.currentY);
         var1.putNextFloat(this.targetX);
         var1.putNextFloat(this.targetY);
         var1.putNextFloat(this.handRotation);
         var1.putNextFloat(this.targetHandRotation);
      }

      public void applySpawnPacket(PacketReader var1) {
         this.currentX = var1.getNextFloat();
         this.currentY = var1.getNextFloat();
         this.targetX = var1.getNextFloat();
         this.targetY = var1.getNextFloat();
         this.handRotation = var1.getNextFloat();
         this.targetHandRotation = var1.getNextFloat();
      }

      public void setTarget(float var1, float var2, float var3) {
         this.targetX = var1;
         this.targetY = var2;
         this.targetHandRotation = var3;
         this.targetReached = false;
      }

      public void tickMovement(float var1) {
         double var2 = (double)GameMath.preciseDistance(this.targetX, this.targetY, this.currentX, this.currentY);
         float var4;
         if (var2 == 0.0) {
            this.targetReached = true;
            if (!this.isAttacking) {
               this.doIdleAnimation();
            }
         } else {
            var4 = this.moveSpeed * var1 / 250.0F;
            if (var2 <= (double)var4) {
               this.currentX = this.targetX;
               this.currentY = this.targetY;
               this.targetReached = true;
            } else {
               Point2D.Float var5 = GameMath.normalize(this.targetX - this.currentX, this.targetY - this.currentY);
               this.currentX += var5.x * var4;
               this.currentY += var5.y * var4;
            }

            this.limbs.apply(this.currentX, this.currentY, 0.1F, 0.5F, 100);
         }

         if (this.targetHandRotation != this.handRotation) {
            var4 = this.moveSpeed * var1 / 250.0F;
            float var6 = this.targetHandRotation - this.handRotation;
            if (Math.abs(var6) < var4) {
               this.handRotation = this.targetHandRotation;
            } else {
               this.handRotation += var4 * Math.signum(var6);
            }

         }
      }

      public void doAcidBreathAnimation() {
         this.moveSpeed = 100.0F;
         this.isAttacking = true;
         this.setTarget(this.isLeftHand ? 20.0F : -20.0F, 0.0F, this.isLeftHand ? 20.0F : -20.0F);
      }

      public void doWebVolleyAnimation() {
         this.moveSpeed = 100.0F;
         this.isAttacking = true;
         this.setTarget(this.isLeftHand ? 30.0F : -30.0F, -40.0F, this.isLeftHand ? 90.0F : -90.0F);
      }

      public void doScreenSlashWarningAnimation() {
         this.moveSpeed = 100.0F;
         this.isAttacking = true;
         this.setTarget(this.isLeftHand ? 60.0F : -60.0F, -150.0F, this.isLeftHand ? -130.0F : 130.0F);
      }

      public void doScreenSlashAttackAnimation() {
         this.moveSpeed = 200.0F;
         this.setTarget(this.isLeftHand ? 40.0F : -40.0F, -10.0F, this.isLeftHand ? 20.0F : -20.0F);
      }

      public void returnToIdleAnimation() {
         this.moveSpeed = 30.0F;
         this.isAttacking = false;
         this.setTarget(this.isLeftHand ? 50.0F : -50.0F, -100.0F, 0.0F);
      }

      public void doIdleAnimation() {
         if (this.targetReached) {
            if ((this.currentX == 60.0F || this.currentX == -60.0F) && this.currentY == -75.0F) {
               this.setTarget(this.isLeftHand ? 50.0F : -50.0F, -100.0F, 0.0F);
            } else {
               this.setTarget(this.isLeftHand ? 60.0F : -60.0F, -75.0F, 0.0F);
            }

            this.moveSpeed = 10.0F;
         }

      }

      public void addDrawOptions(DrawOptionsList var1, DrawOptionsList var2, DrawOptionsList var3, Level var4, int var5, int var6, GameCamera var7, boolean var8) {
         if (!this.mob.isServer()) {
            int var9 = (int)this.mob.currentHeight;
            Iterator var10 = this.limbs.limbs.iterator();

            while(var10.hasNext()) {
               InverseKinematics.Limb var11 = (InverseKinematics.Limb)var10.next();
               GameLight var12 = var4.getLightLevel(var5 / 32, var6 / 32);
               GameTexture var13;
               int var14;
               int var15;
               if (this.limbs.limbs.getLast() == var11) {
                  var13 = var8 ? MobRegistry.Textures.spiderEmpressRageArmBottom : MobRegistry.Textures.spiderEmpressArmBottom;
                  var14 = var13.getWidth();
                  var15 = var13.getHeight();
                  GameTexture var16 = var8 ? MobRegistry.Textures.spiderEmpressRageHand : MobRegistry.Textures.spiderEmpressHand;
                  int var17 = var16.getWidth();
                  int var18 = var16.getHeight();
                  TextureDrawOptionsEnd var19;
                  TextureDrawOptionsEnd var20;
                  if (this.isLeftHand) {
                     var19 = var13.initDraw().mirrorX().rotate(var11.angle + 20.0F, 4, var15 - 4).light(var12).size(var14, var15).pos(var7.getDrawX(var11.inboundX - 4.0F) + var5, var7.getDrawY(var11.inboundY - (float)var15 / 2.0F - 4.0F) + var6 - var9);
                     var20 = var16.initDraw().mirrorX().rotate(this.handRotation, 10, 8).light(var12).size(var17, var18).pos(var7.getDrawX(var11.outboundX - 10.0F) + var5, var7.getDrawY(var11.outboundY - 8.0F) + var6 - var9);
                     var1.add(var20);
                  } else {
                     var19 = var13.initDraw().rotate(var11.angle - 200.0F, var14 - 4, var15 - 4).light(var12).size(var14, var15).pos(var7.getDrawX(var11.inboundX - (float)(var14 - 4)) + var5, var7.getDrawY(var11.inboundY - (float)var15 / 2.0F - 4.0F) + var6 - var9);
                     var20 = var16.initDraw().rotate(this.handRotation, var17 - 10, 8).light(var12).size(var17, var18).pos(var7.getDrawX(var11.outboundX - (float)(var17 - 10)) + var5, var7.getDrawY(var11.outboundY - 8.0F) + var6 - var9);
                  }

                  var2.add(var19);
                  var1.add(var20);
               } else {
                  var13 = var8 ? MobRegistry.Textures.spiderEmpressRageArmTop : MobRegistry.Textures.spiderEmpressArmTop;
                  var14 = var13.getWidth();
                  var15 = var13.getHeight();
                  TextureDrawOptionsEnd var22;
                  if (this.isLeftHand) {
                     var22 = var13.initDraw().mirrorX().rotate(var11.angle - 45.0F, 4, 4).light(var12).size(var14, var15).pos(var7.getDrawX(var11.inboundX - 4.0F) + var5, var7.getDrawY(var11.inboundY - 4.0F) + var6 - var9);
                  } else {
                     var22 = var13.initDraw().rotate(var11.angle - 135.0F, var14 - 4, 4).light(var12).size(var14, var15).pos(var7.getDrawX(var11.inboundX - (float)(var14 - 4)) + var5, var7.getDrawY(var11.inboundY - 4.0F) + var6 - var9);
                  }

                  var3.add(var22);
               }
            }

            if (GlobalData.debugActive()) {
               Color var21 = this.isLeftHand ? Color.BLUE : Color.RED;
               var3.add(() -> {
                  this.limbs.drawDebug(var7, var5, var6 - var9, var21, Color.GREEN);
               });
            }

         }
      }
   }

   public abstract static class SpiderEmpressLeg {
      public final Mob mob;
      public float startX;
      public float startY;
      public float x;
      public float y;
      public float nextX;
      public float nextY;
      public boolean isMoving;
      private InverseKinematics ik;
      private List<Line2D.Float> shadowLines;
      public float maxLeftAngle;
      public float maxRightAngle;
      private final float moveAtDist;
      private float checkX;
      private float checkY;
      private float distBuffer;
      private final boolean mirror;

      public SpiderEmpressLeg(Mob var1, float var2, float var3, float var4, float var5, boolean var6) {
         this.mob = var1;
         this.moveAtDist = var2;
         this.distBuffer = var2 * var3;
         this.maxLeftAngle = var4;
         this.maxRightAngle = var5;
         this.mirror = var6;
         this.snapToPosition();
         this.checkX = this.x;
         this.checkY = this.y;
      }

      public void snapToPosition() {
         GamePoint3D var1 = this.getDesiredPosition();
         this.x = var1.x;
         this.y = var1.y;
         this.nextX = var1.x;
         this.nextY = var1.y;
         this.updateIK();
      }

      public void tickMovement(float var1) {
         GamePoint3D var2 = this.getCenterPosition();
         double var3 = (new Point2D.Float(var2.x, var2.y)).distance((double)this.checkX, (double)this.checkY);
         this.distBuffer = (float)((double)this.distBuffer + var3);
         this.checkX = var2.x;
         this.checkY = var2.y;
         if (var3 == 0.0) {
            this.distBuffer += var1 / (this.moveAtDist / 5.0F);
         }

         if (!this.isMoving) {
            GamePoint3D var5 = this.getDesiredPosition();
            double var6 = (new Point2D.Float(var5.x, var5.y)).distance((double)this.x, (double)this.y);
            if (var6 > 175.0) {
               this.distBuffer += this.moveAtDist;
            }

            if (this.distBuffer >= this.moveAtDist) {
               this.distBuffer -= this.moveAtDist;
               if (this.x != var5.x || this.y != var5.y) {
                  this.startX = this.x;
                  this.startY = this.y;
                  this.nextX = var5.x;
                  this.nextY = var5.y;
                  this.isMoving = true;
               }
            }
         }

         if (this.isMoving) {
            double var10 = (new Point2D.Float(this.x, this.y)).distance((double)this.nextX, (double)this.nextY);
            float var7 = (float)var10 * 2.0F + this.mob.getSpeed() * 1.2F;
            float var8 = var7 * var1 / 250.0F;
            if (var10 < (double)var8) {
               if (this.mob.isClient()) {
                  Screen.playSound(GameResources.tap2, SoundEffect.effect(this.x, this.y).volume(0.1F).pitch(GameRandom.globalRandom.getFloatBetween(0.5F, 0.75F)));
               }

               this.x = this.nextX;
               this.y = this.nextY;
               this.isMoving = false;
            } else {
               Point2D.Float var9 = GameMath.normalize(this.nextX - this.x, this.nextY - this.y);
               this.x += var9.x * var8;
               this.y += var9.y * var8;
            }
         }

         this.updateIK();
      }

      public void updateIK() {
         if (!this.mob.isServer()) {
            GamePoint3D var1 = this.getCenterPosition();
            Point2D.Float var2 = GameMath.normalize(this.x - var1.x, this.y - var1.y);
            float var3 = 0.5F;
            float var4 = (float)(new Point2D.Float(var1.x, var1.y)).distance((double)this.x, (double)this.y) * var3;
            float var5 = 20.0F;
            GamePoint3D var6 = var1.dirFromLength(var1.x + var2.x * var4, var1.y + var2.y * var4, var5, 40.0F);
            GamePoint3D var7 = var6.dirFromLength(this.x, this.y, 0.0F, 70.0F);
            float var8 = this.getJumpHeight();
            float var9 = 0.6F;
            synchronized(this) {
               this.shadowLines = Collections.synchronizedList(new LinkedList());
               this.shadowLines.add(new Line2D.Float(var1.x, var1.y + 18.0F, var6.x, var6.y - var6.height * var9));
               this.shadowLines.add(new Line2D.Float(var6.x, var6.y - var6.height * var9, this.x, this.y));
            }

            this.ik = InverseKinematics.startFromPoints(var1.x, var1.y - var1.height * var9 - var8, var6.x, var6.y - var6.height * var9 - var8, this.maxLeftAngle, this.maxRightAngle);
            this.ik.addJointPoint(var7.x, var7.y - var7.height * var9 - var8);
            this.ik.apply(this.x, this.y - this.getCurrentLegLift() - Math.max(var8 - 150.0F, var8 / 4.0F), 0.0F, 2.0F, 500);
         }
      }

      public float getCurrentLegLift() {
         double var1 = (new Point2D.Float(this.startX, this.startY)).distance((double)this.nextX, (double)this.nextY);
         float var3 = Math.min((float)var1 / 40.0F, 1.0F) * 20.0F + 5.0F;
         double var4 = (new Point2D.Float(this.x, this.y)).distance((double)this.nextX, (double)this.nextY);
         double var6 = GameMath.limit(var4 / var1, 0.0, 1.0);
         return GameMath.sin((float)var6 * 180.0F) * var3;
      }

      public abstract float getJumpHeight();

      public abstract GamePoint3D getDesiredPosition();

      public abstract GamePoint3D getCenterPosition();

      public void addDrawOptions(DrawOptionsList var1, DrawOptionsList var2, DrawOptionsList var3, Level var4, GameCamera var5, boolean var6) {
         if (!this.mob.isServer()) {
            int var7 = MobRegistry.Textures.queenSpiderLeg_shadow.getWidth() / 2;
            Iterator var9;
            TextureDrawOptionsEnd var14;
            synchronized(this) {
               var9 = this.shadowLines.iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     break;
                  }

                  Line2D.Float var10 = (Line2D.Float)var9.next();
                  GameLight var11 = var4.getLightLevel((int)((var10.x1 + var10.x2) / 2.0F / 32.0F), (int)((var10.y1 + var10.y2) / 2.0F / 32.0F));
                  float var12 = GameMath.getAngle(new Point2D.Float(var10.x1 - var10.x2, var10.y1 - var10.y2));
                  float var13 = (float)var10.getP1().distance(var10.getP2());
                  var14 = MobRegistry.Textures.queenSpiderLeg_shadow.initDraw().rotate(var12 + 90.0F, var7 / 2, 6).light(var11).size(var7, (int)var13 + 16).pos(var5.getDrawX(var10.x1 - (float)var7 / 2.0F), var5.getDrawY(var10.y1));
                  var1.add(var14);
               }
            }

            float var8 = this.getJumpHeight();
            var9 = this.ik.limbs.iterator();

            while(var9.hasNext()) {
               InverseKinematics.Limb var17 = (InverseKinematics.Limb)var9.next();
               int var18 = MobRegistry.Textures.spiderEmpressLegBottom.getWidth() + 2;
               GameLight var19 = var4.getLightLevel((int)((var17.inboundX + var17.outboundX) / 2.0F / 32.0F), (int)(((var17.inboundY + var17.outboundY) / 2.0F + var8) / 32.0F));
               GameTexture var20;
               if (this.ik.limbs.getLast() == var17) {
                  var20 = var6 ? MobRegistry.Textures.spiderEmpressRageLegBottom : MobRegistry.Textures.spiderEmpressLegBottom;
                  if (this.mirror) {
                     var14 = var20.initDraw().mirrorX().rotate(var17.angle - 90.0F, var18 / 2, 4).light(var19).size(var18, (int)var17.length + 16).pos(var5.getDrawX(var17.inboundX - (float)var18 / 2.0F), var5.getDrawY(var17.inboundY) - 8);
                  } else {
                     var14 = var20.initDraw().rotate(var17.angle - 90.0F, var18 / 2, 4).light(var19).size(var18, (int)var17.length + 16).pos(var5.getDrawX(var17.inboundX - (float)var18 / 2.0F), var5.getDrawY(var17.inboundY) - 8);
                  }

                  var2.add(var14);
               } else {
                  var20 = var6 ? MobRegistry.Textures.spiderEmpressRageLegTop : MobRegistry.Textures.spiderEmpressLegTop;
                  var18 = var20.getWidth();
                  if (this.mirror) {
                     var14 = var20.initDraw().mirrorX().rotate(var17.angle - 90.0F, var18 / 2, 4).light(var19).size(var18, (int)var17.length + 16).pos(var5.getDrawX(var17.inboundX - (float)var18 / 2.0F), var5.getDrawY(var17.inboundY) - 8);
                  } else {
                     var14 = var20.initDraw().rotate(var17.angle - 90.0F, var18 / 2, 4).light(var19).size(var18, (int)var17.length + 16).pos(var5.getDrawX(var17.inboundX - (float)var18 / 2.0F), var5.getDrawY(var17.inboundY) - 8);
                  }

                  var3.add(var14);
               }
            }

            if (GlobalData.debugActive()) {
               var3.add(() -> {
                  Screen.drawCircle(var5.getDrawX(this.x), var5.getDrawY(this.y - this.getCurrentLegLift()), 4, 12, 1.0F, 0.0F, 0.0F, 1.0F, false);
               });
               var3.add(() -> {
                  this.ik.drawDebug(var5, Color.RED, Color.GREEN);
               });
            }

         }
      }
   }

   public static class SpiderCastleBossAI<T extends SpiderEmpressMob> extends SequenceAINode<T> {
      public SpiderCastleBossAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((SpiderEmpressMob)var1, var2, var3);
            }
         });
         short var1 = 500;
         byte var2 = 100;
         AttackStageManagerNode var3 = new AttackStageManagerNode();
         this.addChild(new ConditionAINode(new IsolateRunningAINode(var3), (var0) -> {
            return !var0.isRaging;
         }, AINodeResult.SUCCESS));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new ScreenSlashStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1500);
         }));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var3.addChild(new WebVolleyWindupStage(var2, var1));
         var3.addChild(new WebVolleyStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new ChargeTargetStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new ScreenSlashStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new AcidWaveWindupStage(var2, var1));
         var3.addChild(new AcidWaveStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var3.addChild(new ChargeTargetStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var1 = 400;
         var2 = 50;
         var3 = new AttackStageManagerNode();
         this.addChild(new ConditionAINode(new IsolateRunningAINode(var3), (var0) -> {
            return var0.isRaging;
         }, AINodeResult.SUCCESS));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 400));
         var3.addChild(new IdleTimeAttackStage((var2x) -> {
            return this.getIdleTime(var2x, var1);
         }));

         int var4;
         for(var4 = 0; var4 < 3; ++var4) {
            var3.addChild(new ScreenSlashStage());
         }

         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1500);
         }));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 400));
         var3.addChild(new WebVolleyWindupStage(var2, var1));
         var3.addChild(new WebVolleyStage());
         var3.addChild(new FlyToRandomPositionAttackStage(true, 400));
         var3.addChild(new WebVolleyWindupStage(var2, var1));
         var3.addChild(new WebVolleyStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));

         for(var4 = 0; var4 < 2; ++var4) {
            var3.addChild(new ChargeTargetStage());
         }

         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 400));
         var3.addChild(new AcidWaveWindupStage(var2, var1));
         var3.addChild(new AcidWaveStage());
         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var3.addChild(new FlyToRandomPositionAttackStage(true, 100));
         var3.addChild(new AcidWaveWindupStage(var2, var1));
         var3.addChild(new AcidWaveStage());

         for(var4 = 0; var4 < 2; ++var4) {
            var3.addChild(new ChargeTargetStage());
         }

         var3.addChild(new CheckRageTransitionStage());
         var3.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
      }

      private int getIdleTime(T var1, int var2) {
         float var3 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         return (int)((float)var2 * var3);
      }
   }

   public static class AcidWaveStage<T extends SpiderEmpressMob> extends AINode<T> implements AttackStageInterface<T> {
      private boolean useRageVariant;

      public AcidWaveStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.useRageVariant = var1.isRaging;
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 == null) {
            return AINodeResult.FAILURE;
         } else {
            float var4 = (float)var1.getHealth() / (float)var1.getMaxHealth();
            float var5 = Math.abs(var4 - 1.0F);
            float var6;
            if (this.useRageVariant) {
               var6 = 120.0F + var5 * 60.0F;

               for(int var7 = -1; var7 <= 1; ++var7) {
                  EmpressAcidProjectile var8 = new EmpressAcidProjectile(var1.getLevel(), var1.x, var1.y, var3.x, var3.y, var6, 1000, SpiderEmpressMob.acidProjectileDamage, var1);
                  var8.setAngle(var8.getAngle() + (float)(10 * var7));
                  var1.getLevel().entityManager.projectiles.add(var8);
                  var1.spawnedProjectiles.add(var8);
               }

               var1.acidSoundAbility.runAndSend();
            } else {
               var6 = 120.0F + var5 * 40.0F;
               EmpressAcidProjectile var9 = new EmpressAcidProjectile(var1.getLevel(), var1.x, var1.y, var3.x, var3.y, var6, 1000, SpiderEmpressMob.acidProjectileDamage, var1);
               var1.getLevel().entityManager.projectiles.add(var9);
               var1.spawnedProjectiles.add(var9);
               var1.acidSoundAbility.runAndSend();
            }

            return AINodeResult.SUCCESS;
         }
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SpiderEmpressMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }

   public static class AcidWaveWindupStage<T extends SpiderEmpressMob> extends AINode<T> implements AttackStageInterface<T> {
      private float timer;
      private final Function<T, Integer> duration;

      public AcidWaveWindupStage(Function<T, Integer> var1) {
         this.duration = var1;
      }

      public AcidWaveWindupStage(int var1, int var2) {
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

      public void onStarted(T var1, Blackboard<T> var2) {
         this.timer = 0.0F;
         var1.setAcidBreathAnimationAbility.runAndSend();
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.timer += 50.0F;
         if (this.timer > (float)(Integer)this.duration.apply(var1)) {
            var1.setIdleAttackAnimationAbility.runAndSend();
            return AINodeResult.SUCCESS;
         } else {
            return AINodeResult.RUNNING;
         }
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SpiderEmpressMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }

   public static class ChargeTargetStage<T extends SpiderEmpressMob> extends FlyToOppositeDirectionAttackStage<T> {
      public ChargeTargetStage() {
         super(true, 250.0F, 0.0F);
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         super.onStarted(var1, var2);
         if (var2.mover.isMoving()) {
            var1.chargeSoundAbility.runAndSend();
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, var1, 5.0F, (Attacker)null), true);
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
         super.onEnded(var1, var2);
         var1.buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }

   public static class WebVolleyStage<T extends SpiderEmpressMob> extends AINode<T> implements AttackStageInterface<T> {
      private boolean useRageVariant;
      private boolean initial;
      public int direction;
      private float angleBuffer;
      private float remainingAngle;
      private float totalAngle;
      private float currentAngleLeft;
      private float currentAngleRight;

      public WebVolleyStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.useRageVariant = var1.isRaging;
         this.initial = true;
         this.angleBuffer = 0.0F;
         this.totalAngle = 180.0F;
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (this.initial) {
            Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
            if (var3 == null) {
               return AINodeResult.SUCCESS;
            }

            this.currentAngleLeft = -6.0F;
            this.currentAngleRight = 6.0F;
            this.remainingAngle = this.totalAngle;
            this.initial = false;
         }

         this.angleBuffer += this.totalAngle * 50.0F / 500.0F;
         float var6 = this.useRageVariant ? 10.0F : 15.0F;

         while(this.angleBuffer >= var6) {
            this.currentAngleLeft += var6;
            EmpressWebBallProjectile var4 = new EmpressWebBallProjectile(var1.x, var1.y, this.currentAngleLeft, SpiderEmpressMob.webBallDamage, 120.0F, var1);
            var1.getLevel().entityManager.projectiles.add(var4);
            var1.spawnedProjectiles.add(var4);
            var1.webBallSoundAbility.runAndSend();
            this.currentAngleRight -= var6;
            EmpressWebBallProjectile var5 = new EmpressWebBallProjectile(var1.x, var1.y, this.currentAngleRight, SpiderEmpressMob.webBallDamage, 120.0F, var1);
            var1.getLevel().entityManager.projectiles.add(var5);
            var1.spawnedProjectiles.add(var5);
            this.angleBuffer -= var6;
            this.remainingAngle -= var6;
            var1.webBallSoundAbility.runAndSend();
            if (this.remainingAngle < 1.0F) {
               break;
            }
         }

         return this.angleBuffer >= this.remainingAngle ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SpiderEmpressMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }

   public static class WebVolleyWindupStage<T extends SpiderEmpressMob> extends AINode<T> implements AttackStageInterface<T> {
      private float timer;
      private final Function<T, Integer> duration;

      public WebVolleyWindupStage(Function<T, Integer> var1) {
         this.duration = var1;
      }

      public WebVolleyWindupStage(int var1, int var2) {
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

      public void onStarted(T var1, Blackboard<T> var2) {
         this.timer = 0.0F;
         var1.setWebVolleyAnimationAbility.runAndSend();
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.timer += 50.0F;
         if (this.timer > (float)(Integer)this.duration.apply(var1)) {
            var1.setIdleAttackAnimationAbility.runAndSend();
            return AINodeResult.SUCCESS;
         } else {
            return AINodeResult.RUNNING;
         }
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SpiderEmpressMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }

   public static class ScreenSlashStage<T extends SpiderEmpressMob> extends AINode<T> implements AttackStageInterface<T> {
      private int slashesRemaining;
      private final ArrayList<Float> attackAngles = new ArrayList();
      private final ArrayList<Point> attackPos = new ArrayList();
      private float timeSinceStart = 0.0F;
      private float timeBeforeSlash;
      private float slashBuffer;

      public ScreenSlashStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.slashesRemaining = var1.isRaging ? 20 : 10;
         this.timeSinceStart = 0.0F;
         this.slashBuffer = 0.0F;
         var1.setScreenSlashWarningAnimationAbility.runAndSend();
         this.calculateTimeBeforeSlash(var1);
      }

      private void calculateTimeBeforeSlash(T var1) {
         float var2 = var1.isRaging ? 750.0F : 1000.0F;
         float var3 = Math.abs(var1.getHealthPercent() - 1.0F);
         this.timeBeforeSlash = var2 - var3 * 250.0F;
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 == null) {
            return AINodeResult.RUNNING;
         } else {
            float var4;
            int var6;
            if (this.timeSinceStart >= this.timeBeforeSlash) {
               var1.setScreenSlashAttackAnimationAbility.runAndSend();
               if (this.attackAngles.isEmpty()) {
                  var1.setIdleAttackAnimationAbility.runAndSend();
                  return AINodeResult.SUCCESS;
               }

               var4 = (Float)this.attackAngles.get(0);
               int var5 = ((Point)this.attackPos.get(0)).x;
               var6 = ((Point)this.attackPos.get(0)).y;
               EmpressSlashProjectile var7 = new EmpressSlashProjectile((float)var5, (float)var6, var4, SpiderEmpressMob.slashDamage, var1);
               var1.getLevel().entityManager.projectiles.add(var7);
               var1.spawnedProjectiles.add(var7);
               EmpressSlashProjectile var8 = new EmpressSlashProjectile((float)var5, (float)var6, var4 - 180.0F, SpiderEmpressMob.slashDamage, var1);
               var1.getLevel().entityManager.projectiles.add(var8);
               var1.spawnedProjectiles.add(var8);
               var1.slashSoundAbility.runAndSend();
               this.attackAngles.remove(0);
               this.attackPos.remove(0);
               if (this.slashesRemaining < 1) {
                  return AINodeResult.RUNNING;
               }
            }

            GameRandom var10 = GameRandom.globalRandom;
            this.timeSinceStart += 50.0F;

            for(this.slashBuffer += 50.0F; this.slashesRemaining > 0 && this.slashBuffer > 50.0F; --this.slashesRemaining) {
               var6 = var10.getIntBetween(-200, 200);
               int var11 = var10.getIntBetween(-200, 200);
               var4 = (float)var10.getIntBetween(-180, 180);
               EmpressSlashWarningProjectile var12 = new EmpressSlashWarningProjectile((float)((int)(var3.x + (float)var6)), (float)((int)(var3.y + (float)var11)), var4, SpiderEmpressMob.slashDamage, var1);
               var1.getLevel().entityManager.projectiles.add(var12);
               var1.spawnedProjectiles.add(var12);
               EmpressSlashWarningProjectile var9 = new EmpressSlashWarningProjectile((float)((int)(var3.x + (float)var6)), (float)((int)(var3.y + (float)var11)), var4 - 180.0F, SpiderEmpressMob.slashDamage, var1);
               var1.getLevel().entityManager.projectiles.add(var9);
               var1.spawnedProjectiles.add(var9);
               this.attackAngles.add(var4);
               this.attackPos.add(new Point((int)(var3.x + (float)var6), (int)(var3.y + (float)var11)));
               this.slashBuffer -= 50.0F;
            }

            return AINodeResult.RUNNING;
         }
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SpiderEmpressMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }

   public static class CheckRageTransitionStage<T extends SpiderEmpressMob> extends AINode<T> implements AttackStageInterface<T> {
      public CheckRageTransitionStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public void onStarted(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (0.0F < var1.rageProgress && var1.rageProgress < 1.0F) {
            return AINodeResult.SUCCESS;
         } else {
            if (var1.rageProgress < 0.0F && var1.isRaging) {
               var1.setRagingAbility.runAndSend(false);
            } else if (var1.rageProgress > 1.0F && !var1.isRaging) {
               var1.setRagingAbility.runAndSend(true);
            }

            return AINodeResult.SUCCESS;
         }
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SpiderEmpressMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((SpiderEmpressMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((SpiderEmpressMob)var1, var2);
      }
   }
}
