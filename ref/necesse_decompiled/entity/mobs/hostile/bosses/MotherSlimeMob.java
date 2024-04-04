package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.CameraShake;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeWarningEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SlimeEggProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MotherSlimeMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable();
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation();
   public static LootTable privateLootTable;
   public static MaxHealthGetter MAX_HEALTH;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   protected ParticleTypeSwitcher particleTypeSwitcher;
   public TicksPerSecond particleTicks;
   protected float height;
   protected long wobbleTimerOffset;
   protected long jumpStartTime;
   protected int jumpAnimationTime;
   protected float jumpStartX;
   protected float jumpStartY;
   protected float jumpTargetX;
   protected float jumpTargetY;
   public final SlimeBossJumpMobAbility jumpAbility;
   protected long squishStartTime;
   protected int squishAnimationTime;
   public CameraShake squishShake;
   public final IntMobAbility squishLaunchAbility;
   public static GameDamage collisionDamage;
   public static GameDamage quakeDamage;
   public final EmptyMobAbility flickSoundAbility;
   public final EmptyMobAbility popSoundAbility;

   public MotherSlimeMob() {
      super(100);
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC});
      this.particleTicks = TicksPerSecond.ticksPerSecond(20);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.moveAccuracy = 60;
      this.setSpeed(150.0F);
      this.setArmor(40);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-100, -100, 200, 100);
      this.hitBox = new Rectangle(-100, -100, 200, 100);
      this.selectBox = new Rectangle(-110, -140, 220, 150);
      this.jumpAbility = (SlimeBossJumpMobAbility)this.registerAbility(new SlimeBossJumpMobAbility());
      this.squishLaunchAbility = (IntMobAbility)this.registerAbility(new IntMobAbility() {
         protected void run(int var1) {
            MotherSlimeMob.this.squishStartTime = MotherSlimeMob.this.getLocalTime();
            MotherSlimeMob.this.squishAnimationTime = var1;
            MotherSlimeMob.this.squishShake = new CameraShake(MotherSlimeMob.this.getLocalTime(), MotherSlimeMob.this.squishAnimationTime, 50, 2.0F, 2.0F, true);
         }
      });
      this.flickSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (MotherSlimeMob.this.isClient()) {
               Screen.playSound(GameResources.slimesplash, SoundEffect.effect(MotherSlimeMob.this).pitch(1.0F));
               Screen.playSound(GameResources.flick, SoundEffect.effect(MotherSlimeMob.this).pitch(0.8F));
            }

         }
      });
      this.popSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (MotherSlimeMob.this.isClient()) {
               Screen.playSound(GameResources.pop, SoundEffect.effect(MotherSlimeMob.this).volume(0.3F).pitch(0.5F).falloffDistance(1400));
            }

         }
      });
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      if (this.jumpStartTime != 0L) {
         var1.putNextBoolean(true);
         int var2 = (int)(this.getLocalTime() - this.jumpStartTime);
         var1.putNextInt(var2);
         var1.putNextFloat(this.jumpStartX);
         var1.putNextFloat(this.jumpStartY);
         var1.putNextFloat(this.jumpTargetX);
         var1.putNextFloat(this.jumpTargetY);
         var1.putNextInt(this.jumpAnimationTime);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      if (var1.getNextBoolean()) {
         int var2 = var1.getNextInt();
         this.jumpStartTime = this.getLocalTime() - (long)var2;
         this.jumpStartX = var1.getNextFloat();
         this.jumpStartY = var1.getNextFloat();
         this.jumpTargetX = var1.getNextFloat();
         this.jumpTargetY = var1.getNextFloat();
         this.jumpAnimationTime = var1.getNextInt();
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

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new SlimeBossAI());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.3F));
      }

   }

   public void tickMovement(float var1) {
      long var2;
      if (this.jumpStartTime != 0L) {
         var2 = this.getLocalTime() - this.jumpStartTime;
         float var4 = GameMath.limit((float)var2 / (float)this.jumpAnimationTime, 0.0F, 1.0F);
         if (var2 <= (long)this.jumpAnimationTime) {
            float var5 = (float)Math.pow((double)var4, 1.5);
            float var6 = (float)Math.pow((double)Math.min(var4 * 1.2F, 1.0F), 0.800000011920929);
            this.x = GameMath.lerp(var6, this.jumpStartX, this.jumpTargetX);
            this.y = GameMath.lerp(var6, this.jumpStartY, this.jumpTargetY);
            this.height = (float)Math.sin(Math.PI * (double)var5) * 140.0F;
         } else {
            this.spawnLandParticles();
            this.height = 0.0F;
            this.jumpStartTime = 0L;
            this.wobbleTimerOffset = this.getLocalTime() - 300L;
         }
      } else if (this.squishStartTime != 0L) {
         var2 = this.getLocalTime() - this.squishStartTime;
         if (var2 > (long)this.squishAnimationTime) {
            this.squishStartTime = 0L;
            this.wobbleTimerOffset = this.getLocalTime();
         }

         this.height = 0.0F;
      } else {
         this.height = 0.0F;
      }

      super.tickMovement(var1);
   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.ElekTrak, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      this.particleTicks.gameTick();

      while(this.particleTicks.shouldTick()) {
         int var1 = this.getX() + GameRandom.globalRandom.getIntBetween(-90, 90);
         int var2 = this.getY() + 10;
         float var3 = GameRandom.globalRandom.getFloatBetween(-50.0F, 50.0F);
         float var4 = this.height + (float)GameRandom.globalRandom.getIntBetween(30, 90);
         float var5 = var4 + 300.0F;
         int var6 = GameRandom.globalRandom.getIntBetween(4000, 6000);
         this.getLevel().entityManager.addParticle((float)var1, (float)var2, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).movesFriction(var3, 0.0F, 0.5F).sizeFades(14, 20).rotates().heightMoves(var4, var5).colorRandom(36.0F, 0.7F, 0.6F, 10.0F, 0.1F, 0.1F).fadesAlphaTime(500, 1000).lifeTime(var6);
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
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

   public boolean canBeHit(Attacker var1) {
      return this.height > 50.0F ? false : super.canBeHit(var1);
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
   }

   public boolean canCollisionHit(Mob var1) {
      return this.height < 20.0F && super.canCollisionHit(var1);
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

   public void spawnLandParticles() {
      if (!this.isServer()) {
         short var1 = 200;
         float var2 = 360.0F / (float)var1;

         for(int var3 = 0; var3 < var1; ++var3) {
            int var4 = (int)((float)var3 * var2 + GameRandom.globalRandom.nextFloat() * var2);
            int var5 = GameRandom.globalRandom.getIntBetween(50, 80);
            float var6 = this.x + (float)Math.sin(Math.toRadians((double)var4)) * (float)var5;
            float var7 = this.y + (float)Math.cos(Math.toRadians((double)var4)) * (float)var5 * 0.6F;
            float var8 = (float)Math.sin(Math.toRadians((double)var4)) * (float)GameRandom.globalRandom.getIntBetween(50, 100);
            float var9 = (float)Math.cos(Math.toRadians((double)var4)) * (float)GameRandom.globalRandom.getIntBetween(50, 100) * 0.6F;
            this.getLevel().entityManager.addParticle(var6, var7 - 40.0F, var3 % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(var8, var9, 0.8F).colorRandom(36.0F, 0.7F, 0.6F, 10.0F, 0.1F, 0.1F).heightMoves(0.0F, 50.0F).lifeTime(1500);
         }

         Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(this).volume(0.7F).pitch(0.8F));
         Screen.playSound(GameResources.slimesplash, SoundEffect.effect(this).pitch(0.8F));
         this.getLevel().getClient().startCameraShake(this, 400, 40, 8.0F, 8.0F, true);
      }
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.motherSlime.body, var3 % 5, 4, 64, this.x, this.y - 40.0F, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      float var11 = GameUtils.getAnimFloatContinuous(this.getWorldEntity().getLocalTime() - this.wobbleTimerOffset, 600);
      float var12 = GameUtils.getAnimFloatContinuous(this.getWorldEntity().getLocalTime() - this.wobbleTimerOffset + 200L, 600);
      long var13;
      float var15;
      float var16;
      if (this.jumpStartTime != 0L) {
         var13 = this.getLocalTime() - this.jumpStartTime;
         var15 = GameMath.limit((float)var13 / (float)this.jumpAnimationTime, 0.0F, 1.0F);
         if (var15 < 0.4F) {
            var16 = var15 / 0.4F;
            var11 = 1.0F - var16;
            var12 = 1.0F;
         } else if (var15 < 0.8F) {
            var16 = GameMath.limit((var15 - 0.4F) / 0.4F, 0.0F, 1.0F);
            var11 = var16;
            var12 = 1.0F - var16;
         } else {
            var16 = GameMath.limit((var15 - 0.8F) / 0.2F, 0.0F, 1.0F);
            var11 = 1.0F;
            var12 = var16;
         }
      } else if (this.squishStartTime != 0L) {
         var13 = this.getLocalTime() - this.squishStartTime;
         int var27 = Math.min(150, this.squishAnimationTime / 2);
         if (var13 < (long)(this.squishAnimationTime - var27)) {
            if (!this.squishShake.isOver(this.getLocalTime())) {
               Point2D.Float var28 = this.squishShake.getCurrentShake(this.getLocalTime());
               var5 = (int)((float)var5 + var28.x);
               var6 = (int)((float)var6 + var28.y);
            }

            var16 = GameMath.limit((float)var13 / (float)(this.squishAnimationTime - var27), 0.0F, 1.0F);
            var12 = GameMath.lerp(var16, 1.0F, -1.0F);
            var11 = 1.0F;
         } else {
            var16 = GameMath.limit((float)(var13 - (long)(this.squishAnimationTime - var27)) / (float)var27, 0.0F, 1.0F);
            var12 = GameMath.lerp(var16, -1.0F, 1.0F);
            var11 = GameMath.lerp(var16, 1.0F, 0.0F);
         }
      }

      float var26 = GameMath.lerp(var11, 0.9F, 1.0F);
      int var14 = (int)(256.0F * var26);
      var15 = GameMath.lerp(var12, 0.9F, 1.0F);
      int var29 = (int)(256.0F * var15);
      int var17 = var8.getDrawX(var5) - var14 / 2;
      int var18 = var8.getDrawY(var6) - 215 + (256 - var29);
      final TextureDrawOptionsEnd var19 = MobRegistry.Textures.motherSlime.body.initDraw().sprite(0, 0, 256).light(var10).size(var14, var29).pos(var17, var18 - (int)this.height);
      float var20 = 1.0F - GameMath.limit(this.height / 700.0F, 0.0F, 0.8F);
      int var21 = (int)((float)var14 * var20);
      int var22 = (int)((float)var29 * var20);
      int var23 = var8.getDrawX(var5) - var21 / 2;
      int var24 = var8.getDrawY(var6) - 215 + (256 - var22);
      TextureDrawOptionsEnd var25 = MobRegistry.Textures.motherSlime.shadow.initDraw().sprite(0, 0, 256).light(var10).size(var21, var22).pos(var23, var24);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var19.draw();
         }
      });
      var2.add((var1x) -> {
         var25.draw();
      });
   }

   public int getFlyingHeight() {
      return (int)this.height;
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
      MobRegistry.Textures.motherSlime.body.initDraw().sprite(0, 0, 256).size(48, 48).draw(var4, var5 - (int)(this.height / 10.0F));
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-24, -34, 48, 34);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F));
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
      MAX_HEALTH = new MaxHealthGetter(18000, 28000, 35000, 38000, 45000);
      collisionDamage = new GameDamage(90.0F);
      quakeDamage = new GameDamage(90.0F);
   }

   public class SlimeBossJumpMobAbility extends MobAbility {
      public SlimeBossJumpMobAbility() {
      }

      public void runAndSend(float var1, float var2, int var3) {
         Packet var4 = new Packet();
         PacketWriter var5 = new PacketWriter(var4);
         var5.putNextFloat(var1);
         var5.putNextFloat(var2);
         var5.putNextInt(var3);
         this.runAndSendAbility(var4);
      }

      public void executePacket(PacketReader var1) {
         MotherSlimeMob.this.jumpStartTime = MotherSlimeMob.this.getLocalTime();
         MotherSlimeMob.this.jumpStartX = MotherSlimeMob.this.x;
         MotherSlimeMob.this.jumpStartY = MotherSlimeMob.this.y;
         MotherSlimeMob.this.jumpTargetX = var1.getNextFloat();
         MotherSlimeMob.this.jumpTargetY = var1.getNextFloat();
         MotherSlimeMob.this.jumpAnimationTime = var1.getNextInt();
         if (!MotherSlimeMob.this.isServer()) {
            Screen.playSound(GameResources.slimesplash, SoundEffect.effect(MotherSlimeMob.this).pitch(0.6F));
         }

      }
   }

   public static class SlimeBossAI<T extends MotherSlimeMob> extends SequenceAINode<T> {
      public SlimeBossAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((MotherSlimeMob)var1, var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         this.addChild(new IsolateRunningAINode(var1));
         var1.addChild(new WaitForJumpDoneStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000, 2000);
         }));
         var1.addChild(new JumpSlamStage());
         var1.addChild(new WaitForJumpDoneStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500, 1000);
         }));
         var1.addChild(new JumpQuakeStage(300, 0));
         var1.addChild(new WaitForJumpDoneStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500, 1000);
         }));
         var1.addChild(new JumpQuakeStage(300, 200));
         var1.addChild(new WaitForJumpDoneStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500, 1000);
         }));
         var1.addChild(new JumpQuakeStage(300, 300));
         var1.addChild(new WaitForJumpDoneStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500, 1000);
         }));
         var1.addChild(new SquishLaunchStage());
      }

      public int getIdleTime(T var1, int var2, int var3) {
         return GameMath.lerp(var1.getHealthPercent(), var2, var3);
      }

      public Mob getCurrentTarget() {
         return (Mob)this.getBlackboard().getObject(Mob.class, "currentTarget");
      }

      public class WaitForJumpDoneStage extends AINode<T> {
         public WaitForJumpDoneStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            return var1.jumpStartTime == 0L ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (MotherSlimeMob)var2, var3);
         }
      }

      public class JumpSlamStage extends AINode<T> implements AttackStageInterface<T> {
         public long nextJumpTime;
         public long endTime;

         public JumpSlamStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.endTime = var1.getTime() + (long)GameMath.lerp(var1.getHealthPercent(), 5000, 8000);
            this.nextJumpTime = 0L;
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.endTime <= var1.getTime()) {
               return AINodeResult.SUCCESS;
            } else {
               if (this.nextJumpTime <= var1.getTime() && var1.jumpStartTime == 0L) {
                  Mob var3 = SlimeBossAI.this.getCurrentTarget();
                  if (var3 != null) {
                     float var4 = GameMath.limit(var3.getDistance(var1), 200.0F, 500.0F);
                     Point2D.Float var5 = GameMath.normalize(var3.x - var1.x, var3.y - var1.y + 40.0F);
                     int var6 = GameRandom.globalRandom.getIntBetween(-40, 40);
                     int var7 = GameRandom.globalRandom.getIntBetween(-40, 40);
                     float var8 = GameMath.expSmooth(var1.getHealthPercent(), 1.0F, 0.3F);
                     int var9 = GameMath.lerp(var8, 400, 1200);
                     var1.jumpAbility.runAndSend(var1.x + var5.x * var4 + (float)var6, var1.y + var5.y * var4 + (float)var7, var9);
                     int var10 = GameMath.lerp(var8, 100, 1000);
                     this.nextJumpTime = var1.getTime() + (long)var9 + (long)var10;
                  } else {
                     this.nextJumpTime = var1.getTime() + 500L;
                  }
               }

               return AINodeResult.RUNNING;
            }
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (MotherSlimeMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((MotherSlimeMob)var1, var2);
         }
      }

      public class JumpQuakeStage extends AINode<T> implements AttackStageInterface<T> {
         public int desiredDistanceFromTarget;
         public int jumpDistance;
         public int targetX;
         public int targetY;
         public int jumpAnimationTime;
         public long warningTime;
         public long quakeTime;
         public int offset;
         public float velocity;

         public JumpQuakeStage(int var2, int var3) {
            this.desiredDistanceFromTarget = var2;
            this.jumpDistance = var3;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            if (this.jumpDistance != 0) {
               Mob var3 = SlimeBossAI.this.getCurrentTarget();
               Point2D.Float var5;
               if (var3 != null) {
                  float var4 = var1.getDistance(var3);
                  var5 = GameMath.normalize(var3.x - var1.x, var3.y - var1.y);
                  float var6 = GameMath.limit(((float)this.desiredDistanceFromTarget - var4) / (float)this.jumpDistance, -1.0F, 1.0F);
                  float var7;
                  if (var6 < 0.0F) {
                     var7 = (float)GameMath.lerp(Math.abs(var6), 90, 0);
                  } else {
                     var7 = (float)GameMath.lerp(Math.abs(var6), 90, 180);
                  }

                  if (GameRandom.globalRandom.nextBoolean()) {
                     var7 = -var7;
                  }

                  float var8 = GameMath.fixAngle(GameMath.getAngle(var5) + var7);
                  Point2D.Float var9 = GameMath.getAngleDir(var8);
                  this.targetX = (int)(var1.x + var9.x * (float)this.jumpDistance);
                  this.targetY = (int)(var1.y + var9.y * (float)this.jumpDistance);
               } else {
                  int var11 = GameRandom.globalRandom.nextInt(360);
                  var5 = GameMath.getAngleDir((float)var11);
                  this.targetX = (int)(var1.x + var5.x * (float)this.jumpDistance);
                  this.targetY = (int)(var1.y + var5.y * (float)this.jumpDistance);
               }
            } else {
               this.targetX = var1.getX();
               this.targetY = var1.getY();
            }

            float var10 = GameMath.expSmooth(var1.getHealthPercent(), 1.0F, 0.3F);
            this.jumpAnimationTime = GameMath.lerp(var10, 700, 1500);
            this.warningTime = var1.getTime() + (long)this.jumpAnimationTime - 400L;
            this.quakeTime = this.warningTime + (long)GameMath.lerp(var10, 750, 1500);
            this.offset = GameRandom.globalRandom.nextInt(15000);
            this.velocity = (float)GameMath.lerp(var10, 1000, 250);
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.jumpAnimationTime != 0) {
               var1.jumpAbility.runAndSend((float)this.targetX, (float)this.targetY, this.jumpAnimationTime);
               this.jumpAnimationTime = 0;
            }

            if (this.warningTime != 0L && this.warningTime <= var1.getTime()) {
               int var3 = (int)(this.quakeTime - var1.getTime());
               var1.getLevel().entityManager.addLevelEvent(new SlimeQuakeWarningEvent(var1, this.targetX, this.targetY - 40, new GameRandom(), 0.0F, this.velocity, 1200.0F, var3, (float)this.offset));
               this.warningTime = 0L;
               return AINodeResult.RUNNING;
            } else if (this.quakeTime != 0L && this.quakeTime <= var1.getTime()) {
               var1.getLevel().entityManager.addLevelEvent(new SlimeQuakeEvent(var1, this.targetX, this.targetY - 40, new GameRandom(), 0.0F, MotherSlimeMob.quakeDamage, this.velocity, 50.0F, 1200.0F, (float)this.offset));
               this.quakeTime = 0L;
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
            return this.tick((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (MotherSlimeMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((MotherSlimeMob)var1, var2);
         }
      }

      public class SquishLaunchStage extends AINode<T> implements AttackStageInterface<T> {
         public boolean hasSentSoundEffect;

         public SquishLaunchStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            float var3 = GameMath.expSmooth(var1.getHealthPercent(), 1.0F, 0.3F);
            int var4 = GameMath.lerp(var3, 1000, 3000);
            var1.squishLaunchAbility.runAndSend(var4);
            this.hasSentSoundEffect = false;
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (!this.hasSentSoundEffect) {
               long var3 = var1.getLocalTime() - var1.squishStartTime;
               int var5 = Math.min(150, var1.squishAnimationTime / 2);
               if (var3 >= (long)(var1.squishAnimationTime - var5)) {
                  var1.flickSoundAbility.runAndSend();
                  this.hasSentSoundEffect = true;
               }
            }

            return var1.squishStartTime == 0L ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
            int var3 = GameMath.lerp(var1.getHealthPercent(), 5, 15);

            for(int var4 = 0; var4 < var3; ++var4) {
               float var5 = GameRandom.globalRandom.getFloatBetween(0.7F, 1.0F);
               int var6 = (int)(300.0F * var5);
               float var7 = (float)GameRandom.globalRandom.nextInt(360);
               Point2D.Float var8 = GameMath.getAngleDir(var7);
               Point2D.Float var9 = new Point2D.Float(var1.x + var8.x * (float)var6, var1.y + var8.y * (float)var6);
               float var10 = var1.x + var8.x * 70.0F;
               float var11 = var1.y - 40.0F + var8.y * 30.0F;
               var1.getLevel().entityManager.projectiles.add(new SlimeEggProjectile(var1.getLevel(), var1, var10, var11, var9.x, var9.y, 30.0F, var6, new GameDamage(0.0F), 50));
            }

         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (MotherSlimeMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((MotherSlimeMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((MotherSlimeMob)var1, var2);
         }
      }
   }
}
