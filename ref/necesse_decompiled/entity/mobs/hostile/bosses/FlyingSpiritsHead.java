package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.TempleEntranceEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FireDanceLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementConstant;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
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
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FlyingSpiritsHead extends BossWormMobHead<FlyingSpiritsBody, FlyingSpiritsHead> {
   private SoundPlayer sound;
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "kandiruvinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation((var0, var1) -> {
      return var1.characterStats().mob_kills.getKills("sageandgrit");
   }, new LootItem("dragonsrebound"), new LootItem("dragonlance"), new LootItem("bowofdualism"), new LootItem("skeletonstaff"));
   public static LootTable privateLootTable;
   public static float lengthPerBodyPart;
   public static float waveLength;
   public static final int totalBodyParts = 13;
   public static MaxHealthGetter BASE_MAX_HEALTH;
   public static MaxHealthGetter INCURSION_MAX_HEALTH;
   public GameDamage collisionDamage;
   public GameDamage fireDamage;
   public static GameDamage baseHeadCollisionDamage;
   public static GameDamage baseBodyCollisionDamage;
   public static GameDamage baseFireDamage;
   public static GameDamage incursionHeadCollisionDamage;
   public static GameDamage incursionBodyCollisionDamage;
   public static GameDamage incursionFireDamage;
   public final LevelMob<FlyingSpiritsHead> friend = new LevelMob();
   public Point pedestalPosition;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   protected Variant variant;
   protected boolean isEnraged;
   protected boolean isEscaping;

   public FlyingSpiritsHead(Variant var1) {
      super(100, waveLength, 100.0F, 13, 25.0F, -5.0F);
      this.variant = var1;
      this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
      this.moveAccuracy = 100;
      this.movementUpdateCooldown = 2000;
      this.movePosTolerance = 700.0F;
      this.setSpeed(180.0F);
      this.setArmor(35);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.collision = new Rectangle(-25, -20, 50, 40);
      this.hitBox = new Rectangle(-30, -25, 60, 50);
      this.selectBox = new Rectangle(-40, -60, 80, 64);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.friend.uniqueID = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.friend.uniqueID);
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isEnraged);
      var1.putNextBoolean(this.isEscaping);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isEnraged = var1.getNextBoolean();
      this.isEscaping = var1.getNextBoolean();
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

   protected float getDistToBodyPart(FlyingSpiritsBody var1, int var2, float var3) {
      return var2 == 1 ? lengthPerBodyPart + 10.0F : lengthPerBodyPart;
   }

   protected FlyingSpiritsBody createNewBodyPart(int var1) {
      FlyingSpiritsBody var2 = new FlyingSpiritsBody();
      byte var3 = 4;
      if (var1 == 0) {
         var2.spriteY = 1;
      } else if (var1 == 2) {
         var2.spriteY = 3;
      } else if (var1 == 13 - var3 - 2) {
         var2.spriteY = 3;
      } else if (var1 >= 13 - var3) {
         var2.spawnsParticles = true;
         int var4 = Math.abs(13 - var1 - var3);
         var2.spriteY = 4 + var4;
      } else {
         var2.spriteY = 1;
      }

      return var2;
   }

   protected void playMoveSound() {
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public boolean dropsLoot() {
      return this.friend.get(this.getLevel()) != null ? false : super.dropsLoot();
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return baseHeadCollisionDamage;
   }

   public boolean canCollisionHit(Mob var1) {
      return this.height < 45.0F && super.canCollisionHit(var1);
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void init() {
      if (this.getLevel() instanceof IncursionLevel) {
         this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
         this.setHealth(this.getMaxHealth());
         this.collisionDamage = incursionHeadCollisionDamage;
         this.fireDamage = incursionFireDamage;
      } else {
         this.collisionDamage = baseHeadCollisionDamage;
         this.fireDamage = baseFireDamage;
      }

      super.init();
      this.ai = new BehaviourTreeAI(this, new FlyingSpiritsHeadAI(), new FlyingAIMover());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
      }

      this.streamBodyParts().forEach((var1) -> {
         var1.variant = this.variant;
      });
   }

   public float getTurnSpeed(float var1) {
      return super.getTurnSpeed(var1) * (this.isEnraged ? 2.0F : 1.0F);
   }

   public void clientTick() {
      super.clientTick();
      if (this.sound == null || this.sound.isDone()) {
         this.sound = Screen.playSound(GameResources.wind, SoundEffect.effect(this).falloffDistance(1400).volume(0.4F));
      }

      if (this.sound != null) {
         this.sound.refreshLooping(1.0F);
      }

      if (!this.isEscaping) {
         Screen.setMusic(MusicRegistry.Kandiru, Screen.MusicPriority.EVENT, 1.5F);
         Screen.registerMobHealthStatusBar(this);
      }

      BossNearbyBuff.applyAround(this);
      this.setSpeed(!this.isEscaping && !this.isEnraged ? 180.0F : 250.0F);
      this.accelerationMod = this.isEnraged ? 1.3F : 1.0F;
      this.decelerationMod = this.isEnraged ? 1.3F : 1.0F;
   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      if (!this.isEnraged) {
         FlyingSpiritsHead var1 = (FlyingSpiritsHead)this.friend.get(this.getLevel());
         if (var1 == null || this.getDistance(var1) > 3840.0F) {
            this.isEnraged = true;
            this.sendMovementPacket(false);
         }
      }

      this.setSpeed(!this.isEscaping && !this.isEnraged ? 180.0F : 280.0F);
      this.accelerationMod = this.isEnraged ? 1.3F : 1.0F;
      this.decelerationMod = this.isEnraged ? 1.3F : 1.0F;
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.flyingSpirits, 4 + this.variant.spriteX, GameRandom.globalRandom.nextInt(6), 64, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(this.x) - 64;
         int var12 = var8.getDrawY(this.y);
         float var13 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
         final MobDrawable var14 = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.flyingSpirits, this.variant.spriteX, 0, 128), (GameTexture)null, var10, (int)this.height, var13, var11, var12, 96);
         new ComputedObjectValue((Object)null, () -> {
            return 0.0;
         });
         ComputedObjectValue var16 = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 70.0);
         final MobDrawable var15;
         if (var16.object != null) {
            Point2D.Double var17 = WormMobHead.linePos(var16);
            GameLight var18 = var4.getLightLevel((int)(var17.x / 32.0), (int)(var17.y / 32.0));
            int var19 = var8.getDrawX((float)var17.x) - 64;
            int var20 = var8.getDrawY((float)var17.y);
            float var21 = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)var16.object).object).movedDist + ((Double)var16.get()).floatValue());
            float var22 = GameMath.fixAngle((float)GameMath.getAngle(new Point2D.Double((double)this.x - var17.x, (double)(this.y - this.height) - (var17.y - (double)var21))));
            var15 = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.flyingSpirits, this.variant.spriteX, 1, 128), (GameTexture)null, var18, (int)var21, var22, var19, var20, 96);
         } else {
            var15 = null;
         }

         var3.add(new MobDrawable() {
            public void draw(TickManager var1) {
               if (var15 != null) {
                  var15.draw(var1);
               }

               var14.draw(var1);
            }
         });
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
      MobRegistry.Textures.flyingSpirits.initDraw().sprite(this.variant.spriteX, 0, 128).rotate(var6 + 90.0F, 24, 24).size(48, 48).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-15, -15, 30, 30);
   }

   public GameTooltips getMapTooltips() {
      return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.POISON_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FROST_DAMAGE_FLAT, 0.0F)).max(0.0F));
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      boolean var3 = this.friend.get(this.getLevel()) == null;
      boolean var4 = false;
      if (var3 && this.isServer() && this.pedestalPosition != null && !(this.getLevel() instanceof IncursionLevel)) {
         Point var5 = new Point(this.pedestalPosition.x, this.pedestalPosition.y + 2);
         if (!this.getLevel().getLevelObject(var5.x, var5.y).getMultiTile().getMasterObject().getStringID().equals("templeentrance")) {
            this.getLevel().entityManager.addLevelEvent(new TempleEntranceEvent(this.pedestalPosition.x, this.pedestalPosition.y + 2));
            var4 = true;
         }
      }

      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var3x) -> {
         ServerClient var4x = ((PlayerMob)var3x).getServerClient();
         if (var3) {
            var4x.newStats.mob_kills.addKill("sageandgrit");
         }

         this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), (ServerClient)var4x);
         if (var4) {
            this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "staircaseopening")), (ServerClient)var4x);
         }

      });
   }

   public boolean controlsAI() {
      return this.variant == FlyingSpiritsHead.Variant.GRIT || this.isEnraged();
   }

   public boolean isEnraged() {
      return this.isEnraged;
   }

   public boolean isEscaping() {
      return this.isEscaping;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected WormMobBody createNewBodyPart(int var1) {
      return this.createNewBodyPart(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float getDistToBodyPart(WormMobBody var1, int var2, float var3) {
      return this.getDistToBodyPart((FlyingSpiritsBody)var1, var2, var3);
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((var0) -> {
         return var0.getLevel() == null || !var0.getLevel().isIncursionLevel;
      }, new LootItemInterface[]{uniqueDrops})});
      lengthPerBodyPart = 50.0F;
      waveLength = 800.0F;
      BASE_MAX_HEALTH = new MaxHealthGetter(10000, 20000, 25000, 28000, 33000);
      INCURSION_MAX_HEALTH = new MaxHealthGetter(14000, 24000, 28000, 32000, 37000);
      baseHeadCollisionDamage = new GameDamage(85.0F);
      baseBodyCollisionDamage = new GameDamage(60.0F);
      baseFireDamage = new GameDamage(75.0F);
      incursionHeadCollisionDamage = new GameDamage(95.0F);
      incursionBodyCollisionDamage = new GameDamage(70.0F);
      incursionFireDamage = new GameDamage(85.0F);
   }

   public static enum Variant {
      GRIT(0, 20.0F, new Color(184, 102, 40)),
      SAGE(1, 200.0F, new Color(65, 105, 151));

      public final int spriteX;
      public final float particleHue;
      public final Color fireColor;

      private Variant(int var3, float var4, Color var5) {
         this.spriteX = var3;
         this.particleHue = var4;
         this.fireColor = var5;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{GRIT, SAGE};
      }
   }

   public static class FlyingSpiritsHeadAI<T extends FlyingSpiritsHead> extends SequenceAINode<T> {
      public FlyingSpiritsHeadAINode<T> node;

      public FlyingSpiritsHeadAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         TargetFinderAINode var1;
         this.addChild(var1 = new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((FlyingSpiritsHead)var1, var2, var3);
            }
         });
         var1.moveToAttacker = false;
         this.addChild(this.node = new FlyingSpiritsHeadAINode());
      }
   }

   public static class FlyingSpiritsHeadAINode<T extends FlyingSpiritsHead> extends AINode<T> {
      public String targetKey = "currentTarget";
      private int startMoveAccuracy;
      protected MState state;
      protected Mob chargingTarget;
      protected float circlingAngleOffset;
      protected float circlingSpeed;
      protected boolean circlingReversed;
      protected boolean lastWasCharge;
      protected float fireDanceTargetX;
      protected float fireDanceTargetY;
      protected long fireDanceStateTime;
      protected boolean fireDanceActive;
      protected FireDanceLevelEvent fireDanceLevelEvent;
      public long nextStateTime;
      public int circlingRange = 500;
      public int fireDanceRange = 300;

      public FlyingSpiritsHeadAINode() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         this.startMoveAccuracy = var2.moveAccuracy;
      }

      public void init(T var1, Blackboard<T> var2) {
         if (this.nextStateTime == 0L) {
            this.nextStateTime = var1.getWorldEntity().getTime() + 11500L;
         }

      }

      public float getHealthPercent() {
         float var1 = GameMath.limit((float)((FlyingSpiritsHead)this.mob()).getHealth() / (float)((FlyingSpiritsHead)this.mob()).getMaxHealth(), 0.0F, 1.0F);
         FlyingSpiritsHead var2 = (FlyingSpiritsHead)((FlyingSpiritsHead)this.mob()).friend.get(((FlyingSpiritsHead)this.mob()).getLevel());
         if (var2 != null) {
            float var3 = GameMath.limit((float)var2.getHealth() / (float)var2.getMaxHealth(), 0.0F, 1.0F);
            return (var1 + var3) / 2.0F;
         } else {
            return var1;
         }
      }

      public int getRandomCooldownBasedOnMissingHealth(int var1, int var2, int var3) {
         float var4 = this.getHealthPercent();
         int var5 = var2 - var1;
         var3 = GameMath.limit(var3, 1, Math.abs(var5));
         if (var5 < 0) {
            var3 = -var3;
         }

         int var6 = (int)(var4 * (float)(var5 - var3));
         int var7 = var3 < 0 ? -GameRandom.globalRandom.nextInt(-var3 + 1) : GameRandom.globalRandom.nextInt(var3 + 1);
         return var1 + var6 + var7;
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (var1.isEscaping()) {
            var2.mover.setCustomMovement(this, new MobMovementConstant(var1.dx, var1.dy));
            return AINodeResult.SUCCESS;
         } else {
            if (this.state == null) {
               this.startCircling();
            } else if (this.state == FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.Charging) {
               if (this.chargingTarget != null && !this.chargingTarget.removed()) {
                  float var3 = var1.getDistance(this.chargingTarget);
                  if (var3 < (float)this.circlingRange / 4.0F) {
                     float var4 = GameMath.getAngle(new Point2D.Float(var1.dx, var1.dy));
                     float var5 = GameMath.getAngle(new Point2D.Float(this.chargingTarget.x - var1.x, this.chargingTarget.y - var1.y));
                     float var6 = GameMath.getAngleDifference(var4, var5);
                     float var7 = var1.isEnraged() ? 20.0F : 40.0F;
                     if (Math.abs(var6) >= var7 && var3 > 75.0F || var1.dx == 0.0F && var1.dy == 0.0F) {
                        var1.moveAccuracy = this.startMoveAccuracy;
                        this.startCircling();
                     }
                  }
               } else {
                  this.startCircling();
               }
            } else if (this.state == FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.FireDance) {
               if (this.fireDanceStateTime < var1.getWorldEntity().getTime()) {
                  if (this.fireDanceActive) {
                     this.startCircling();
                  } else {
                     this.startFireDanceLaser();
                  }
               } else if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
                  boolean var8 = var1.getLevel().entityManager.players.streamArea(this.fireDanceTargetX, this.fireDanceTargetY, this.fireDanceRange).anyMatch((var1x) -> {
                     if (var1x != null && !var1x.removed() && var1x.isVisible()) {
                        return var1x.getDistance(this.fireDanceTargetX, this.fireDanceTargetY) <= (float)this.fireDanceRange;
                     } else {
                        return false;
                     }
                  });
                  if (!var8) {
                     this.startCircling();
                     this.friendAI(FlyingSpiritsHeadAINode::startCircling);
                  }
               }
            }

            if (var1.controlsAI()) {
               FlyingSpiritsHeadAINode var9 = this.getFriendAI();
               if (this.state == FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.Circling && (var9 == null || var9.state == FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.Circling) && this.nextStateTime < var1.getWorldEntity().getTime()) {
                  if (!var1.isEnraged() && this.lastWasCharge && !GameRandom.globalRandom.nextBoolean()) {
                     this.lastWasCharge = false;
                     this.startFireDance();
                  } else {
                     this.lastWasCharge = true;
                     this.startCharging();
                  }

                  this.nextStateTime = var1.getWorldEntity().getTime() + 4000L;
               }
            }

            return AINodeResult.SUCCESS;
         }
      }

      public Mob getTarget() {
         return (Mob)this.getBlackboard().getObject(Mob.class, this.targetKey);
      }

      public void friendAI(Consumer<FlyingSpiritsHeadAINode<?>> var1) {
         FlyingSpiritsHeadAINode var2 = this.getFriendAI();
         if (var2 != null) {
            var1.accept(var2);
         }

      }

      public FlyingSpiritsHeadAINode<?> getFriendAI() {
         FlyingSpiritsHead var1 = (FlyingSpiritsHead)((FlyingSpiritsHead)this.mob()).friend.get(((FlyingSpiritsHead)this.mob()).getLevel());
         return var1 != null && !var1.isEnraged() ? ((FlyingSpiritsHeadAI)var1.ai.tree).node : null;
      }

      public void startCircling() {
         this.stopExistingFireDance();
         Mob var1 = (Mob)this.getBlackboard().getObject(Mob.class, this.targetKey);
         ((FlyingSpiritsHead)this.mob()).moveAccuracy = this.startMoveAccuracy;
         this.state = FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.Circling;
         if (((FlyingSpiritsHead)this.mob()).isEnraged()) {
            this.nextStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + (long)this.getRandomCooldownBasedOnMissingHealth(1000, 4000, 1000);
         } else {
            this.nextStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + (long)this.getRandomCooldownBasedOnMissingHealth(2000, 6000, 1000);
         }

         FlyingSpiritsHeadAINode var2 = this.getFriendAI();
         if (var2 != null && var2.state == FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.Circling) {
            this.circlingReversed = var2.circlingReversed;
            this.circlingAngleOffset = var2.circlingAngleOffset + 180.0F;
            this.circlingSpeed = var2.circlingSpeed;
            if (var1 != null) {
               this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleRelative(this.mob(), var1, this.circlingRange, this.circlingSpeed, this.circlingAngleOffset, this.circlingReversed));
            } else {
               this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleLevelPos(this.mob(), ((FlyingSpiritsHead)this.mob()).x, ((FlyingSpiritsHead)this.mob()).y, this.circlingRange, this.circlingSpeed, this.circlingAngleOffset, this.circlingReversed));
            }
         } else {
            this.circlingSpeed = MobMovementCircle.convertToRotSpeed(this.circlingRange, ((FlyingSpiritsHead)this.mob()).getSpeed()) * 1.1F;
            Object var3;
            if (var1 != null) {
               var3 = new MobMovementCircleRelative(this.mob(), var1, this.circlingRange, this.circlingSpeed, GameRandom.globalRandom.nextBoolean());
            } else {
               var3 = new MobMovementCircleLevelPos(this.mob(), ((FlyingSpiritsHead)this.mob()).x, ((FlyingSpiritsHead)this.mob()).y, this.circlingRange, this.circlingSpeed, GameRandom.globalRandom.nextBoolean());
            }

            this.circlingReversed = ((MobMovementCircle)var3).reversed;
            this.circlingAngleOffset = ((MobMovementCircle)var3).angleOffset;
            this.getBlackboard().mover.setCustomMovement(this, (MobMovement)var3);
         }

      }

      public void startCharging() {
         Mob var1 = this.getTarget();
         if (var1 != null) {
            ((FlyingSpiritsHead)this.mob()).moveAccuracy = 5;
            this.state = FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.Charging;
            this.chargingTarget = var1;
            this.getBlackboard().mover.setCustomMovement(this, new MobMovementRelative(var1, 0.0F, 0.0F));
         }

         if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
            this.friendAI(FlyingSpiritsHeadAINode::startCharging);
         }

      }

      public void startFireDance() {
         Mob var1 = this.getTarget();
         if (var1 != null) {
            float var2 = MobMovementCircle.convertToRotSpeed(this.fireDanceRange, ((FlyingSpiritsHead)this.mob()).getSpeed()) * 1.1F;
            float var3 = (new MobMovementCircleLevelPos(this.mob(), var1.x, var1.y, this.fireDanceRange, var2, this.circlingReversed)).angleOffset;
            this.startFireDance(var1.x, var1.y, this.fireDanceRange, var2, var3, this.circlingReversed);
            if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
               this.friendAI((var4) -> {
                  var4.startFireDance(var1.x, var1.y, this.fireDanceRange, var2, var3 + 180.0F, this.circlingReversed);
               });
            }
         }

      }

      public void startFireDanceLaser() {
         if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
            int var1 = this.getRandomCooldownBasedOnMissingHealth(3000, 6000, 1000);
            this.fireDanceStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + (long)var1;
            ((FlyingSpiritsHead)this.mob()).buffManager.addBuff(new ActiveBuff(BuffRegistry.HARDENED, this.mob(), var1, (Attacker)null), true, true);
            this.stopExistingFireDance();
            ((FlyingSpiritsHead)this.mob()).getLevel().entityManager.addLevelEvent(this.fireDanceLevelEvent = new FireDanceLevelEvent(this.mob(), new GameRandom(), this.fireDanceTargetX, this.fireDanceTargetY, ((FlyingSpiritsHead)this.mob()).fireDamage, 100, ((FlyingSpiritsHead)this.mob()).variant.fireColor, var1));
            this.fireDanceActive = true;
            this.friendAI((var2) -> {
               var2.fireDanceStateTime = this.fireDanceStateTime;
               var2.fireDanceActive = true;
               ((FlyingSpiritsHead)var2.mob()).buffManager.addBuff(new ActiveBuff(BuffRegistry.HARDENED, var2.mob(), var1, (Attacker)null), true, true);
               var2.stopExistingFireDance();
               ((FlyingSpiritsHead)var2.mob()).getLevel().entityManager.addLevelEvent(var2.fireDanceLevelEvent = new FireDanceLevelEvent(var2.mob(), new GameRandom(), this.fireDanceTargetX, this.fireDanceTargetY, ((FlyingSpiritsHead)this.mob()).fireDamage, 100, ((FlyingSpiritsHead)var2.mob()).variant.fireColor, var1));
            });
         } else {
            this.fireDanceStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + 2000L;
            this.fireDanceActive = true;
         }

      }

      public void stopExistingFireDance() {
         if (this.fireDanceLevelEvent != null) {
            this.fireDanceLevelEvent.over();
            if (((FlyingSpiritsHead)this.mob()).isServer()) {
               ((FlyingSpiritsHead)this.mob()).getLevel().getServer().network.sendToClientsAt(new PacketLevelEventOver(this.fireDanceLevelEvent.getUniqueID()), (Level)((FlyingSpiritsHead)this.mob()).getLevel());
            }

            this.fireDanceLevelEvent = null;
         }

      }

      public void startFireDance(float var1, float var2, int var3, float var4, float var5, boolean var6) {
         this.fireDanceTargetX = var1;
         this.fireDanceTargetY = var2;
         this.state = FlyingSpiritsHead.FlyingSpiritsHeadAINode.MState.FireDance;
         this.fireDanceActive = false;
         this.fireDanceStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + 3500L;
         this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleLevelPos(this.mob(), var1, var2, var3, var4, var5, var6));
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((FlyingSpiritsHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((FlyingSpiritsHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (FlyingSpiritsHead)var2, var3);
      }

      private static enum MState {
         Circling,
         Charging,
         FireDance;

         private MState() {
         }

         // $FF: synthetic method
         private static MState[] $values() {
            return new MState[]{Circling, Charging, FireDance};
         }
      }
   }
}
