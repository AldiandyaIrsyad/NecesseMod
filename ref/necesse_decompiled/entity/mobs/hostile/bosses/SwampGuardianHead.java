package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
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
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SpawnProjectilesOnHealthLossAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SwampBoulderProjectile;
import necesse.entity.projectile.SwampRazorProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampGuardianHead extends BossWormMobHead<SwampGuardianBody, SwampGuardianHead> {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItem(0.2F, "rumbleoftheswampguardianvinyl")});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("razorbladeboomerang"), new LootItem("guardianshell"), new LootItem("dredgingstaff"));
   public static LootTable privateLootTable;
   public static float lengthPerBodyPart;
   public static float waveLength;
   public static final int totalBodyParts = 70;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public static GameDamage headCollisionDamage;
   public static GameDamage bodyCollisionDamage;
   public static GameDamage razorDamage;
   public static GameDamage boulderExplosionDamage;
   public static int boulderExplosionRange;
   public static int totalRazorProjectiles;
   public static MaxHealthGetter MAX_HEALTH;
   public final CoordinateMobAbility flickSound;
   public final CoordinateMobAbility swingSound;

   public SwampGuardianHead() {
      super(100, waveLength, 80.0F, 70, 36.0F, -8.0F);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.moveAccuracy = 160;
      this.setSpeed(150.0F);
      this.setArmor(15);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.collision = new Rectangle(-20, -15, 40, 30);
      this.hitBox = new Rectangle(-25, -20, 50, 40);
      this.selectBox = new Rectangle(-32, -60, 64, 64);
      this.flickSound = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            if (SwampGuardianHead.this.isClient()) {
               Screen.playSound(GameResources.magicbolt2, SoundEffect.effect((float)var1, (float)var2).pitch(1.5F).volume(0.5F));
            }

         }
      });
      this.swingSound = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            if (SwampGuardianHead.this.isClient()) {
               Screen.playSound(GameResources.swing1, SoundEffect.effect((float)var1, (float)var2).pitch(0.8F).volume(1.0F));
            }

         }
      });
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

   protected void onAppearAbility() {
      super.onAppearAbility();
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
      }

   }

   protected float getDistToBodyPart(SwampGuardianBody var1, int var2, float var3) {
      return lengthPerBodyPart;
   }

   protected SwampGuardianBody createNewBodyPart(int var1) {
      Object var2;
      if (var1 == 69) {
         var2 = new SwampGuardianTail();
      } else {
         var2 = new SwampGuardianBody();
      }

      ((SwampGuardianBody)var2).sharesHitCooldownWithNext = var1 % 3 < 2;
      if (var1 != 0 && var1 != 68) {
         ((SwampGuardianBody)var2).sprite = new Point(var1 % 4, 0);
         ((SwampGuardianBody)var2).shadowSprite = 0;
      } else {
         ((SwampGuardianBody)var2).sprite = new Point(4, 0);
         ((SwampGuardianBody)var2).shadowSprite = 1;
      }

      return (SwampGuardianBody)var2;
   }

   protected void playMoveSound() {
      Screen.playSound(GameResources.shake, SoundEffect.effect(this).falloffDistance(1000));
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new SwampGuardianAI(), new FlyingAIMover());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return headCollisionDamage;
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.RumbleOfTheSwampGuardian, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
      float var2 = Math.abs((float)Math.pow((double)var1, 0.5) - 1.0F);
      this.setSpeed(120.0F + var2 * 90.0F);
   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
      float var2 = Math.abs((float)Math.pow((double)var1, 0.5) - 1.0F);
      this.setSpeed(120.0F + var2 * 90.0F);
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.swampGuardian, GameRandom.globalRandom.nextInt(6), 6, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 48;
         int var12 = var8.getDrawY(var6);
         float var13 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
         WormMobHead.addAngledDrawable(var1, new GameSprite(MobRegistry.Textures.swampGuardian, 0, 1, 96), MobRegistry.Textures.swampGuardian_mask, var10, (int)this.height, var13, var11, var12, 64);
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.swampGuardian_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(2, 0, var6).light(var3).pos(var7, var8);
   }

   public boolean shouldDrawOnMap() {
      return this.isVisible();
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 24;
      int var5 = var3 - 24;
      float var6 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
      MobRegistry.Textures.swampGuardian.initDraw().sprite(2, 2, 96).rotate(var6 + 90.0F, 24, 24).size(48, 48).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-15, -15, 30, 30);
   }

   public GameTooltips getMapTooltips() {
      return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
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

   // $FF: synthetic method
   // $FF: bridge method
   protected WormMobBody createNewBodyPart(int var1) {
      return this.createNewBodyPart(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float getDistToBodyPart(WormMobBody var1, int var2, float var3) {
      return this.getDistToBodyPart((SwampGuardianBody)var1, var2, var3);
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{uniqueDrops});
      lengthPerBodyPart = 25.0F;
      waveLength = 500.0F;
      headCollisionDamage = new GameDamage(46.0F);
      bodyCollisionDamage = new GameDamage(36.0F);
      razorDamage = new GameDamage(32.0F);
      boulderExplosionDamage = new GameDamage(70.0F);
      boulderExplosionRange = 80;
      totalRazorProjectiles = 250;
      MAX_HEALTH = new MaxHealthGetter(6000, 10000, 12000, 15000, 20000);
   }

   public static class SwampGuardianAI<T extends SwampGuardianHead> extends SelectorAINode<T> {
      public SwampGuardianAI() {
         SequenceAINode var1 = new SequenceAINode();
         this.addChild(var1);
         var1.addChild(new RemoveOnNoTargetNode(100));
         final TargetFinderAINode var2;
         var1.addChild(var2 = new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((SwampGuardianHead)var1, var2, var3);
            }
         });
         var2.moveToAttacker = false;
         ChargingCirclingChaserAINode var3;
         var1.addChild(var3 = new ChargingCirclingChaserAINode(500, 40));
         var1.addChild(new SpawnProjectilesOnHealthLossAINode<T>(SwampGuardianHead.totalRazorProjectiles) {
            public void shootProjectile(T var1) {
               WormMobHead.BodyPartTarget var2x = var1.getRandomTargetFromBodyPart(this, var2, (var1x, var2xx) -> {
                  return var1x.getDistance(var2xx) < 500.0F && !var1.getLevel().collides((Line2D)(new Line2D.Float(var1x.x, var1x.y, var2xx.x, var2xx.y)), (CollisionFilter)(new CollisionFilter()).mobCollision());
               });
               if (var2x != null) {
                  var2x.bodyPart.getLevel().entityManager.projectiles.add(new SwampRazorProjectile(var2x.bodyPart.getLevel(), var1, var2x.bodyPart.x, var2x.bodyPart.y, var2x.target.x, var2x.target.y, 70.0F, 1750, SwampGuardianHead.razorDamage, 50));
                  var1.flickSound.runAndSend(var2x.bodyPart.getX(), var2x.bodyPart.getY());
               }

            }

            // $FF: synthetic method
            // $FF: bridge method
            public void shootProjectile(Mob var1) {
               this.shootProjectile((SwampGuardianHead)var1);
            }
         });
         var1.addChild(new SpawnBouldersAI(var2));
         var1.addChild(new DiveChargeRotationAI(var3));
         this.addChild(new WandererAINode(0));
      }
   }

   public static class DiveChargeRotationAI<T extends SwampGuardianHead> extends AINode<T> {
      private int ticker;
      private ChargingCirclingChaserAINode<T> chaserAI;

      public DiveChargeRotationAI(ChargingCirclingChaserAINode<T> var1) {
         this.chaserAI = var1;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         this.ticker = 100;
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            --this.ticker;
            if (this.ticker <= 0) {
               if (!var1.dive && !var1.isUnderground) {
                  var1.diveAbility.runAndSend();
                  this.chaserAI.startCircling(var1, var2, var3, 100);
                  this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(2.0F, 3.0F));
               } else {
                  this.chaserAI.startCharge(var1, var2, var3);
                  float var4 = GameMath.getAngle(new Point2D.Float(var1.x - var3.x, var1.y - var3.y));
                  Point2D.Float var5 = GameMath.getAngleDir(var4);
                  var1.appearAbility.runAndSend(var1.x, var1.y, -var5.x, -var5.y);
                  this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(8.0F, 9.0F));
               }
            }
         }

         return AINodeResult.SUCCESS;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SwampGuardianHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SwampGuardianHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SwampGuardianHead)var2, var3);
      }
   }

   public static class SpawnBouldersAI<T extends SwampGuardianHead> extends AINode<T> {
      public int ticker;
      public TargetFinderAINode<T> targetFinder;

      public SpawnBouldersAI(TargetFinderAINode<T> var1) {
         this.targetFinder = var1;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(0.8F, 2.0F));
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         --this.ticker;
         if (this.ticker <= 0) {
            WormMobHead.BodyPartTarget var3 = var1.getRandomTargetFromBodyPart(this, this.targetFinder, (var0, var1x) -> {
               float var2 = var0.getDistance(var1x);
               return var2 > 350.0F && var2 < 400.0F;
            });
            if (var3 != null) {
               Point2D.Float var4 = new Point2D.Float(var3.target.x + GameRandom.globalRandom.floatGaussian() * 30.0F, var3.target.y + GameRandom.globalRandom.floatGaussian() * 30.0F);
               int var5 = (int)var3.bodyPart.getDistance(var4.x, var4.y);
               var3.bodyPart.getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(var3.bodyPart.getLevel(), var1, var3.bodyPart.x, var3.bodyPart.y, var4.x, var4.y, 40.0F, var5, new GameDamage(0.0F), 50));
               var1.swingSound.runAndSend(var3.bodyPart.getX(), var3.bodyPart.getY());
            }

            this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(0.8F, 2.0F));
         }

         return AINodeResult.SUCCESS;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((SwampGuardianHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((SwampGuardianHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (SwampGuardianHead)var2, var3);
      }
   }
}
