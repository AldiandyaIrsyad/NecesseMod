package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
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
import necesse.engine.util.GroundPillar;
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
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoQuakeProjectile;
import necesse.entity.projectile.CryoShardProjectile;
import necesse.entity.projectile.CryoVolleyProjectile;
import necesse.entity.projectile.CryoWarningProjectile;
import necesse.entity.projectile.CryoWaveProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.pathProjectile.CryoQuakeCirclingProjectile;
import necesse.entity.projectile.pathProjectile.CryoWarningCirclingProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
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

public class CryoQueenMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "milleniumvinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("cryoquake"), new LootItem("cryospear"), new LootItem("cryoblaster"), new LootItem("cryoglaive"));
   public static LootTable privateLootTable;
   public static MaxHealthGetter BASE_MAX_HEALTH;
   public static MaxHealthGetter INCURSION_MAX_HEALTH;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public LinkedList<Mob> spawnedMobs = new LinkedList();
   public boolean attackingAnimation;
   public final EmptyMobAbility magicSoundAbility;
   public final EmptyMobAbility jingleSoundAbility;
   public final EmptyMobAbility roarSoundAbility;
   public final BooleanMobAbility attackingAnimationAbility;
   public GameDamage collisionDamage = new GameDamage(80.0F);
   public GameDamage cryoQuakeDamage = new GameDamage(70.0F);
   public GameDamage cryoShardDamage = new GameDamage(60.0F);
   public GameDamage cryoWaveDamage = new GameDamage(70.0F);
   public GameDamage cryoVolleyDamage = new GameDamage(70.0F);
   public static GameDamage baseCollisionDamage;
   public static GameDamage baseCryoQuakeDamage;
   public static GameDamage baseCryoShardDamage;
   public static GameDamage baseCryoWaveDamage;
   public static GameDamage baseCryoVolleyDamage;
   public static GameDamage incursionCollisionDamage;
   public static GameDamage incursionCryoQuakeDamage;
   public static GameDamage incursionCryoShardDamage;
   public static GameDamage incursionCryoWaveDamage;
   public static GameDamage incursionCryoVolleyDamage;

   public CryoQueenMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
      this.moveAccuracy = 60;
      this.setSpeed(110.0F);
      this.setArmor(35);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-40, -80, 80, 105);
      this.hitBox = new Rectangle(-40, -80, 80, 105);
      this.selectBox = new Rectangle(-40, -90, 80, 120);
      this.magicSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (CryoQueenMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(CryoQueenMob.this));
            }

         }
      });
      this.jingleSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (CryoQueenMob.this.isClient()) {
               float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(1.0F, 1.05F));
               Screen.playSound(GameResources.jingle, SoundEffect.effect(CryoQueenMob.this).pitch(var1));
            }

         }
      });
      this.roarSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (CryoQueenMob.this.isClient()) {
               Screen.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7F).pitch(1.3F));
            }

         }
      });
      this.attackingAnimationAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            CryoQueenMob.this.attackingAnimation = var1;
         }
      });
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.attackingAnimation);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.attackingAnimation = var1.getNextBoolean();
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
         this.cryoQuakeDamage = incursionCryoQuakeDamage;
         this.cryoShardDamage = incursionCryoShardDamage;
         this.cryoWaveDamage = incursionCryoWaveDamage;
         this.cryoVolleyDamage = incursionCryoVolleyDamage;
      } else {
         this.collisionDamage = baseCollisionDamage;
         this.cryoQuakeDamage = baseCryoQuakeDamage;
         this.cryoShardDamage = baseCryoShardDamage;
         this.cryoWaveDamage = baseCryoWaveDamage;
         this.cryoVolleyDamage = baseCryoVolleyDamage;
      }

      super.init();
      this.ai = new BehaviourTreeAI(this, new CryoQueenAI(), new FlyingAIMover());
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

   public boolean canBePushed(Mob var1) {
      return false;
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
         this.dir = 1;
      } else if (var1 > 0.0F) {
         this.dir = 0;
      }

   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.Millenium, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(110.0F + var1 * 70.0F);
   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(110.0F + var1 * 70.0F);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 6; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.cryoQueen, var3, 17, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void playHitSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.jinglehit, SoundEffect.effect(this).pitch(var1));
   }

   protected void playDeathSound() {
      this.playHitSound();
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 64;
      int var12 = var8.getDrawY(var6) - 100;
      int var13 = GameUtils.getAnim(this.getWorldEntity().getTime(), 5, 750);
      TextureDrawOptionsEnd var14 = MobRegistry.Textures.cryoQueen.initDraw().sprite(var13, this.attackingAnimation ? 0 : 1, 128).size(128, 128).light(var10).mirror(this.dir != 0, false).pos(var11, var12);
      TextureDrawOptions var15 = this.getShadowDrawOptions(var5, var6, var10, var8);
      var3.add((var2x) -> {
         var15.draw();
         var14.draw();
      });
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.reaper_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2 + 20;
      return var5.initDraw().light(var3).pos(var6, var7);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 16;
      int var5 = var3 - 16;
      MobRegistry.Textures.cryoQueen.initDraw().sprite(0, 7, 64).size(32, 32).mirror(this.dir != 0, false).draw(var4, var5);
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

   public boolean isSecondStage() {
      float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
      return var1 < 0.5F;
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
      BASE_MAX_HEALTH = new MaxHealthGetter(8000, 14000, 18000, 21000, 26000);
      INCURSION_MAX_HEALTH = new MaxHealthGetter(17000, 24000, 28000, 32000, 38000);
      baseCollisionDamage = new GameDamage(80.0F);
      baseCryoQuakeDamage = new GameDamage(70.0F);
      baseCryoShardDamage = new GameDamage(60.0F);
      baseCryoWaveDamage = new GameDamage(70.0F);
      baseCryoVolleyDamage = new GameDamage(70.0F);
      incursionCollisionDamage = new GameDamage(95.0F);
      incursionCryoQuakeDamage = new GameDamage(85.0F);
      incursionCryoShardDamage = new GameDamage(80.0F);
      incursionCryoWaveDamage = new GameDamage(90.0F);
      incursionCryoVolleyDamage = new GameDamage(80.0F);
   }

   public static class CryoQueenAI<T extends CryoQueenMob> extends SequenceAINode<T> {
      public CryoQueenAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((CryoQueenMob)var1, var2, var3);
            }
         });
         SequenceAINode var1 = new SequenceAINode();
         var1.addChild(new MoveToRandomPosition(true, 300));
         var1.addChild(new IdleTime(100));
         var1.addChild(new CryoQuakeRotation());
         var1.addChild(new MoveToRandomPosition(true, 300));
         var1.addChild(new IdleTime(100));
         var1.addChild(new CryoShardRotation());
         var1.addChild(new MoveToRandomPosition(true, 300));
         var1.addChild(new IdleTime(100));
         var1.addChild(new CryoWaveRotation());

         for(int var2 = 0; var2 < 3; ++var2) {
            var1.addChild(new MoveToRandomPosition(false, 300));
            var1.addChild(new IdleTime(1000));
            var1.addChild(new CryoVolleyRotation());
         }

         this.addChild(new IsolateRunningAINode(var1));
      }

      public void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         super.onRootSet(var1, var2, var3);
         var3.onRemoved((var1x) -> {
            var2.spawnedMobs.forEach(Mob::remove);
         });
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (CryoQueenMob)var2, var3);
      }
   }

   public static class CryoPillar extends GroundPillar {
      public GameTextureSection texture;
      public boolean mirror;

      public CryoPillar(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         this.mirror = GameRandom.globalRandom.nextBoolean();
         this.texture = MobRegistry.Textures.cryoQueen == null ? null : (GameTextureSection)GameRandom.globalRandom.getOneOf((Object[])((new GameTextureSection(MobRegistry.Textures.cryoQueen)).sprite(0, 6, 64), (new GameTextureSection(MobRegistry.Textures.cryoQueen)).sprite(1, 6, 64), (new GameTextureSection(MobRegistry.Textures.cryoQueen)).sprite(2, 6, 64)));
         this.behaviour = new GroundPillar.TimedBehaviour(300, 200, 800);
      }

      public DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6) {
         GameLight var7 = var1.getLightLevel(this.x / 32, this.y / 32);
         int var8 = var6.getDrawX(this.x);
         int var9 = var6.getDrawY(this.y);
         double var10 = this.getHeight(var2, var4);
         int var12 = (int)(var10 * (double)this.texture.getHeight());
         return this.texture.section(0, this.texture.getWidth(), 0, var12).initDraw().mirror(this.mirror, false).light(var7).pos(var8 - this.texture.getWidth() / 2, var9 - var12);
      }
   }

   public static class CryoVolleyRotation<T extends CryoQueenMob> extends RunningAINode<T> {
      private int ticker;

      public CryoVolleyRotation() {
      }

      public void start(T var1, Blackboard<T> var2) {
         var2.mover.stopMoving(var1);
         var1.attackingAnimationAbility.runAndSend(true);
         this.ticker = 0;
      }

      public AINodeResult tickRunning(T var1, Blackboard<T> var2) {
         ++this.ticker;
         float var3 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         int var4 = 10 + (int)(20.0F * var3 * 1.5F);
         return this.ticker < var4 ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
      }

      public void end(T var1, Blackboard<T> var2) {
         var1.attackingAnimationAbility.runAndSend(false);
         LinkedList var3 = new LinkedList();
         float var4 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         int var5 = 150 + (int)(Math.abs(var4 - 1.0F) * 170.0F);
         GameUtils.streamServerClients(var1.getLevel()).map((var0) -> {
            return var0.playerMob;
         }).filter((var1x) -> {
            return var1.getDistance(var1x) < 1120.0F;
         }).forEach((var3x) -> {
            float var4 = Projectile.getAngleToTarget(var1.x, var1.y, (float)var3x.getX(), (float)var3x.getY());
            if (var3.stream().noneMatch((var1x) -> {
               return Math.abs(GameMath.getAngleDifference(var4, var1x)) < 35.0F;
            })) {
               Point2D.Float var5x = GameMath.getAngleDir(var4 + 90.0F);
               byte var6 = 6;
               short var7 = 150;
               int var8 = var7 / var6;

               for(int var9 = 0; var9 < var6; ++var9) {
                  int var10 = GameRandom.globalRandom.getIntBetween(50, 100);
                  Point2D.Float var11 = new Point2D.Float(var5x.x * (float)var10, var5x.y * (float)var10);
                  int var12 = var9 * var8 - var7 / 2;
                  var11 = GameMath.getPerpendicularPoint(var11, (float)var12, var5x);
                  Point2D.Float var13 = new Point2D.Float(var3x.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), var3x.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50));
                  float var10003 = var1.x + var11.x;
                  var1.getLevel().entityManager.projectiles.add(new CryoVolleyProjectile(var10003, var1.y + var11.y - 30.0F, var13.x, var13.y, (float)var5, 1440, var1.cryoVolleyDamage, 100, var1));
               }

               var3.add(var4);
            }

         });
         var1.jingleSoundAbility.runAndSend();
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void end(Mob var1, Blackboard var2) {
         this.end((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tickRunning(Mob var1, Blackboard var2) {
         return this.tickRunning((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void start(Mob var1, Blackboard var2) {
         this.start((CryoQueenMob)var1, var2);
      }
   }

   public static class CryoWaveRotation<T extends CryoQueenMob> extends RunningAINode<T> {
      private Point2D.Float attackDir = new Point2D.Float(1.0F, 0.0F);
      private boolean reverseDir;
      private int timerBuffer;

      public CryoWaveRotation() {
      }

      public void start(T var1, Blackboard<T> var2) {
         var1.attackingAnimationAbility.runAndSend(true);
         this.reverseDir = !this.reverseDir;
         this.timerBuffer = 0;
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            float var4 = var1.getDistance(var3);
            float var5 = GameMath.limit((var4 - 500.0F) / 500.0F, 0.0F, 1.0F);
            float var6 = 45.0F + var5 * 35.0F;
            float var7 = GameMath.cos(var6) * var4 * 1.6F;
            float var8 = (float)Math.toDegrees(Math.atan2((double)(var3.y - var1.y), (double)(var3.x - var1.x))) + (this.reverseDir ? var6 : -var6);
            Point2D.Float var9 = GameMath.getAngleDir(var8);
            this.attackDir = GameMath.getAngleDir(var8 + (float)(this.reverseDir ? -90 : 90));
            var2.mover.directMoveTo(this, (int)(var1.x + var9.x * var7), (int)(var1.y + var9.y * var7));
         }

      }

      public AINodeResult tickRunning(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            this.timerBuffer += 50;
            float var4 = (float)var1.getHealth() / (float)var1.getMaxHealth();
            int var5 = 250 + (int)(var4 * 230.0F);
            if (this.timerBuffer > var5) {
               var1.magicSoundAbility.runAndSend();
            }

            do {
               if (this.timerBuffer <= var5) {
                  return AINodeResult.RUNNING;
               }

               this.timerBuffer -= var5;
               int var6 = 80 + (int)(Math.abs(var4 - 1.0F) * 100.0F);
               float var10005 = var1.x + this.attackDir.x * 100.0F;
               var1.getLevel().entityManager.projectiles.add(new CryoWaveProjectile(var1.x, var1.y, var10005, var1.y + this.attackDir.y * 100.0F, (float)var6, 1280, var1.cryoWaveDamage, 100, var1));
               if (var1.getHealth() < var1.getMaxHealth() / 2) {
                  var10005 = var1.x - this.attackDir.x * 100.0F;
                  var1.getLevel().entityManager.projectiles.add(new CryoWaveProjectile(var1.x, var1.y, var10005, var1.y - this.attackDir.y * 100.0F, (float)var6, 1280, var1.cryoWaveDamage, 100, var1));
               }
            } while(var2.mover.isMoving());

            return AINodeResult.SUCCESS;
         } else {
            return AINodeResult.SUCCESS;
         }
      }

      public void end(T var1, Blackboard<T> var2) {
         var1.attackingAnimationAbility.runAndSend(false);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void end(Mob var1, Blackboard var2) {
         this.end((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tickRunning(Mob var1, Blackboard var2) {
         return this.tickRunning((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void start(Mob var1, Blackboard var2) {
         this.start((CryoQueenMob)var1, var2);
      }
   }

   public static class CryoShardRotation<T extends CryoQueenMob> extends RunningAINode<T> {
      private int ticker;
      private int timerBuffer;
      private boolean reversed;

      public CryoShardRotation() {
      }

      public void start(T var1, Blackboard<T> var2) {
         var2.mover.stopMoving(var1);
         var1.attackingAnimationAbility.runAndSend(true);
         this.ticker = 0;
         this.timerBuffer = 0;
         this.reversed = !this.reversed;
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            int var4 = GameMath.limit((int)var1.getDistance(var3), 100, 500);
            var1.setMovement(new MobMovementCircleLevelPos(var1, var3.x, var3.y, var4, 2.0F, this.reversed));
         }

      }

      public AINodeResult tickRunning(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            this.timerBuffer += 50;
            float var4 = (float)var1.getHealth() / (float)var1.getMaxHealth();
            int var5 = 150 + (int)(var4 * 200.0F);
            if (this.timerBuffer > var5) {
               var1.jingleSoundAbility.runAndSend();
            }

            while(this.timerBuffer > var5) {
               int var6 = 100 + (int)(Math.abs(var4 - 1.0F) * 160.0F);
               this.timerBuffer -= var5;
               var1.getLevel().entityManager.projectiles.add(new CryoShardProjectile(var1.x, var1.y, var3.x + (float)GameRandom.globalRandom.getIntBetween(-20, 20), var3.y + (float)GameRandom.globalRandom.getIntBetween(-20, 20), (float)var6, 1440, var1.cryoShardDamage, 100, var1));
            }
         }

         ++this.ticker;
         return this.ticker <= 100 ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
      }

      public void end(T var1, Blackboard<T> var2) {
         var1.stopMoving();
         var1.attackingAnimationAbility.runAndSend(false);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void end(Mob var1, Blackboard var2) {
         this.end((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tickRunning(Mob var1, Blackboard var2) {
         return this.tickRunning((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void start(Mob var1, Blackboard var2) {
         this.start((CryoQueenMob)var1, var2);
      }
   }

   public static class CryoQuakeRotation<T extends CryoQueenMob> extends RunningAINode<T> {
      private int timer;
      private int index;
      private float startAngle;
      private boolean circling;
      private boolean clockwise;
      private Point2D.Float startPos;
      private boolean playedWarningSound;
      private boolean playedQuakeSound;
      public final int msPerProjectile = 30;
      public final int totalProjectiles = 18;
      public final int anglePerProjectile = 20;

      public CryoQuakeRotation() {
      }

      public void start(T var1, Blackboard<T> var2) {
         var1.attackingAnimationAbility.runAndSend(true);
         this.circling = GameRandom.globalRandom.nextBoolean();
         this.clockwise = GameRandom.globalRandom.nextBoolean();
         this.timer = 0;
         this.index = 0;
         this.playedWarningSound = false;
         this.playedQuakeSound = false;
         this.startPos = new Point2D.Float(var1.x, var1.y + 15.0F);
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            this.startAngle = (float)Math.toDegrees(Math.atan2((double)(var3.y - this.startPos.y), (double)(var3.x - this.startPos.x))) + 90.0F - 40.0F;
         }

         this.startAngle += (float)GameRandom.globalRandom.getIntBetween(-5, 5);
      }

      public AINodeResult tickRunning(T var1, Blackboard<T> var2) {
         for(this.timer += 50; this.timer >= 30; ++this.index) {
            this.timer -= 30;
            float var3 = this.startAngle + (float)(this.index * 20);
            float var4;
            if (this.index < 18) {
               if (!this.playedWarningSound) {
                  var1.magicSoundAbility.runAndSend();
                  this.playedWarningSound = true;
               }

               var4 = this.getProjectileSpeed(var1);
               if (this.circling) {
                  var1.getLevel().entityManager.projectiles.add(new CryoWarningCirclingProjectile(this.startPos.x, this.startPos.y, 20.0F, var3, this.clockwise, var4, 960));
               } else {
                  var1.getLevel().entityManager.projectiles.add(new CryoWarningProjectile(this.startPos.x, this.startPos.y, var3, var4, 1120));
               }
            } else {
               if (this.index >= 36) {
                  return AINodeResult.SUCCESS;
               }

               if (!this.playedQuakeSound) {
                  var1.magicSoundAbility.runAndSend();
                  this.playedQuakeSound = true;
               }

               var4 = this.getProjectileSpeed(var1);
               if (this.circling) {
                  var1.getLevel().entityManager.projectiles.add(new CryoQuakeCirclingProjectile(this.startPos.x, this.startPos.y, 20.0F, var3, this.clockwise, var4, 960, var1.cryoQuakeDamage, 100, var1));
               } else {
                  var1.getLevel().entityManager.projectiles.add(new CryoQuakeProjectile(this.startPos.x, this.startPos.y, var3, var4, 1120, var1.cryoQuakeDamage, 100, var1));
               }
            }
         }

         return AINodeResult.RUNNING;
      }

      public void end(T var1, Blackboard<T> var2) {
         var1.attackingAnimationAbility.runAndSend(false);
      }

      protected float getProjectileSpeed(T var1) {
         float var2 = Math.abs((float)var1.getHealth() / (float)var1.getMaxHealth() - 1.0F);
         return this.circling ? 80.0F + var2 * 140.0F : 100.0F + var2 * 180.0F;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void end(Mob var1, Blackboard var2) {
         this.end((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tickRunning(Mob var1, Blackboard var2) {
         return this.tickRunning((CryoQueenMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void start(Mob var1, Blackboard var2) {
         this.start((CryoQueenMob)var1, var2);
      }
   }

   public static class MoveToRandomPosition<T extends Mob> extends RunningAINode<T> {
      public int baseDistance;
      public boolean isRunningWhileMoving;

      public MoveToRandomPosition(boolean var1, int var2) {
         this.isRunningWhileMoving = var1;
         this.baseDistance = var2;
      }

      public void start(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         Point2D.Float var4 = new Point2D.Float(var1.x, var1.y);
         if (var3 != null) {
            var4 = new Point2D.Float(var3.x, var3.y);
         }

         Point2D.Float var5 = new Point2D.Float(var1.x, var1.y);

         for(int var6 = 0; var6 < 10; ++var6) {
            int var7 = GameRandom.globalRandom.nextInt(360);
            Point2D.Float var8 = GameMath.getAngleDir((float)var7);
            var5 = new Point2D.Float(var4.x + var8.x * (float)this.baseDistance, var4.y + var8.y * (float)this.baseDistance);
            if (var1.getDistance(var5.x, var5.y) >= (float)this.baseDistance / 4.0F) {
               break;
            }
         }

         var2.mover.directMoveTo(this, (int)var5.x, (int)var5.y);
      }

      public AINodeResult tickRunning(T var1, Blackboard<T> var2) {
         return this.isRunningWhileMoving && var2.mover.isMoving() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
      }

      public void end(T var1, Blackboard<T> var2) {
      }
   }

   public static class IdleTime<T extends Mob> extends RunningAINode<T> {
      public int msToIdle;
      private int timer;

      public IdleTime(int var1) {
         this.msToIdle = var1;
      }

      public void start(T var1, Blackboard<T> var2) {
         this.timer = 0;
      }

      public AINodeResult tickRunning(T var1, Blackboard<T> var2) {
         this.timer += 50;
         return this.timer <= this.msToIdle ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
      }

      public void end(T var1, Blackboard<T> var2) {
      }
   }
}
