package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.GameDifficulty;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.light.GameLight;

public class NightSwarmBatMob extends FlyingBossMob {
   public int nightSwarmEventUniqueID;
   public int batIndex;
   public int shareHitCooldownUniqueID;
   public long shareHitCooldownDisabledTime;
   public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(8);
   public ParticleTypeSwitcher particleTypes;
   public final EmptyMobAbility disableShareCooldown;
   public float idleXPos;
   public float idleYPos;
   public int idleDistance;
   public NightSwarmBatStage currentStage;
   public ArrayList<NightSwarmBatStage> stages;
   public static GameDamage COLLISION_DAMAGE = new GameDamage(90.0F);

   public NightSwarmBatMob() {
      super((Integer)NightSwarmLevelEvent.BAT_MAX_HEALTH.get(GameDifficulty.CLASSIC));
      this.particleTypes = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      this.idleDistance = 100;
      this.stages = new ArrayList();
      this.isSummoned = true;
      this.moveAccuracy = 10;
      this.setSpeed(100.0F);
      this.setFriction(2.0F);
      this.setArmor(40);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 40);
      this.disableShareCooldown = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            NightSwarmBatMob.this.shareHitCooldownDisabledTime = NightSwarmBatMob.this.getWorldEntity().getTime() + 5000L;
         }
      });
   }

   public NightSwarmLevelEvent getEvent() {
      if (this.getLevel() != null && this.nightSwarmEventUniqueID != 0) {
         LevelEvent var1 = this.getLevel().entityManager.getLevelEvent(this.nightSwarmEventUniqueID, false);
         return var1 instanceof NightSwarmLevelEvent ? (NightSwarmLevelEvent)var1 : null;
      } else {
         return null;
      }
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.nightSwarmEventUniqueID);
      var1.putNextInt(this.batIndex);
      var1.putNextInt(this.shareHitCooldownUniqueID);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.nightSwarmEventUniqueID = var1.getNextInt();
      this.batIndex = var1.getNextInt();
      this.shareHitCooldownUniqueID = var1.getNextInt();
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new NightSwarmBatAI(), new FlyingAIMover());
   }

   public void serverTick() {
      super.serverTick();
      if (this.currentStage != null) {
         this.currentStage.serverTick(this);
         if (this.currentStage.hasCompleted(this)) {
            this.currentStage.onCompletedOrRemoved(this, false);
            this.currentStage = null;
         }
      }

      if (this.currentStage == null && !this.stages.isEmpty()) {
         this.currentStage = (NightSwarmBatStage)this.stages.remove(0);
         this.currentStage.onStarted(this);
      }

      if ((this.currentStage == null || this.currentStage.idleAllowed) && (this.idleXPos != 0.0F || this.idleYPos != 0.0F) && (this.hasArrivedAtTarget() || !this.hasCurrentMovement())) {
         int var1 = GameRandom.globalRandom.nextInt(360);
         Point2D.Float var2 = GameMath.getAngleDir((float)var1);
         float var3 = (0.7F + GameRandom.globalRandom.floatGaussian() * 0.3F) * (float)this.idleDistance;
         this.setMovement(new MobMovementLevelPos(this.idleXPos + var2.x * var3, this.idleYPos + var2.y * var3));
      }

   }

   public void clientTick() {
      super.clientTick();
      this.particleTicks.gameTick();

      while(this.particleTicks.shouldTick()) {
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0F, this.y + GameRandom.globalRandom.floatGaussian() * 5.0F, this.particleTypes.next()).movesConstant(this.dx / 2.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, this.dy / 2.0F + GameRandom.globalRandom.floatGaussian() * 4.0F).color(new Color(7, 13, 24)).height(20.0F).lifeTime(500);
      }

   }

   public boolean canHitThroughCollision() {
      return true;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return COLLISION_DAMAGE;
   }

   public void handleCollisionHit(Mob var1, GameDamage var2, int var3) {
      super.handleCollisionHit(var1, var2, var3);
      if (this.currentStage != null) {
         this.currentStage.onCollisionHit(this, var1);
      }

   }

   protected void doWasHitLogic(MobWasHitEvent var1) {
      super.doWasHitLogic(var1);
      if (this.currentStage != null) {
         this.currentStage.onWasHit(this, var1);
      }

      this.shareHitCooldownDisabledTime = 0L;
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      if (this.currentStage != null) {
         this.currentStage.onCompletedOrRemoved(this, true);
      }

      Iterator var5 = this.stages.iterator();

      while(var5.hasNext()) {
         NightSwarmBatStage var6 = (NightSwarmBatStage)var5.next();
         var6.onCompletedOrRemoved(this, true);
      }

   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      NightSwarmLevelEvent var3 = this.getEvent();
      if (var3 != null) {
         if (var1 != null) {
            var3.attackers.add(var1);
         }

         var3.attackers.addAll(var2);
      }

   }

   public void clearStages() {
      if (this.currentStage != null) {
         this.currentStage.onCompletedOrRemoved(this, true);
      }

      Iterator var1 = this.stages.iterator();

      while(var1.hasNext()) {
         NightSwarmBatStage var2 = (NightSwarmBatStage)var1.next();
         var2.onCompletedOrRemoved(this, true);
      }

      this.currentStage = null;
      this.stages.clear();
   }

   public int getHitCooldownUniqueID() {
      return this.shareHitCooldownUniqueID != 0 && this.shareHitCooldownDisabledTime < this.getWorldEntity().getTime() ? this.shareHitCooldownUniqueID : super.getHitCooldownUniqueID();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.nightSwarmBat, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public int getFlyingHeight() {
      return 20;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32).minLevelCopy(100.0F);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 55;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      float var14 = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0F;
      var12 = (int)((float)var12 + var14);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var15 = MobRegistry.Textures.nightSwarmBat.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public boolean isVisibleOnMap(Client var1, LevelMap var2) {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      int var4 = var2 - 16;
      int var5 = var3 - 26;
      Point var6 = this.getAnimSprite(var2, var3, this.dir);
      float var7 = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0F;
      var5 = (int)((float)var5 + var7);
      var5 += this.getLevel().getTile(var2 / 32, var3 / 32).getMobSinkingAmount(this);
      MobRegistry.Textures.nightSwarmBat.initDraw().sprite(var6.x, var6.y, 64).size(32).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-10, -24, 20, 28);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      return new Point(GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 300), var3);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), (new ModifierValue(BuffModifiers.POISON_DAMAGE, 1.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 1.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FROST_DAMAGE, 1.0F)).max(0.2F));
   }

   public static class NightSwarmBatAI<T extends NightSwarmBatMob> extends SelectorAINode<T> {
      public NightSwarmBatAI() {
      }

      public void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         var3.onEvent("refreshBossDespawn", (var1x) -> {
            NightSwarmLevelEvent var2x = var2.getEvent();
            if (var2x != null) {
               var2x.despawnTimer = 0;
            }

         });
         super.onRootSet(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (NightSwarmBatMob)var2, var3);
      }
   }
}
