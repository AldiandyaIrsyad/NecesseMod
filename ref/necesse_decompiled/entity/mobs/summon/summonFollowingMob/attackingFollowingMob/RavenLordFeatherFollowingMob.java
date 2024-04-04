package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerValidTargetCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.RavenlordsHeaddressSetBonusBuff;
import necesse.entity.mobs.networkField.BooleanNetworkField;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RavenLordFeatherFollowingMob extends FlyingAttackingFollowingMob {
   public Trail trail;
   public float moveAngle;
   private float toMove;
   public BooleanNetworkField hasTarget;
   public float baseDamage = 50.0F;

   public RavenLordFeatherFollowingMob() {
      super(10);
      this.moveAccuracy = 15;
      this.setFriction(2.0F);
      this.collision = new Rectangle(-8, -8, 16, 16);
      this.hitBox = new Rectangle(-8, -8, 16, 16);
      this.selectBox = new Rectangle();
      this.hasTarget = (BooleanNetworkField)this.registerNetworkField(new BooleanNetworkField(false));
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.damage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 15;
   }

   public void handleCollisionHit(Mob var1, GameDamage var2, int var3) {
      Mob var4 = this.getAttackOwner();
      if (var4 != null && var1 != null) {
         var1.isServerHit(var2, var1.x - var4.x, var1.y - var4.y, (float)var3, this);
         this.collisionHitCooldowns.startCooldown(var1);
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerValidTargetCollisionChaserAI<RavenLordFeatherFollowingMob>(448, (GameDamage)null, 15, 500, 640, 64) {
         public boolean isValidTarget(RavenLordFeatherFollowingMob var1, ServerClient var2, Mob var3) {
            if (var2 == null) {
               return false;
            } else {
               Object var4 = GameUtils.castRayFirstHit(new Line2D.Float(var2.playerMob.x, var2.playerMob.y, var3.x, var3.y), 100.0, (var1x) -> {
                  return var1.getLevel().collides(var1x, (new CollisionFilter()).projectileCollision()) ? new Object() : null;
               });
               return var4 == null;
            }
         }

         public AINodeResult tick(RavenLordFeatherFollowingMob var1, Blackboard<RavenLordFeatherFollowingMob> var2) {
            AINodeResult var3 = super.tick(var1, var2);
            Mob var4 = (Mob)var2.getObject(Mob.class, "chaserTarget");
            RavenLordFeatherFollowingMob.this.hasTarget.set(var4 != null);
            return var3;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isValidTarget(Mob var1, ServerClient var2, Mob var3) {
            return this.isValidTarget((RavenLordFeatherFollowingMob)var1, var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((RavenLordFeatherFollowingMob)var1, var2);
         }
      }, new FlyingAIMover());
      if (this.isClient()) {
         this.trail = new Trail(this, this.getLevel(), new Color(231, 212, 243, 89), 14.0F, 1000, 0.0F);
         this.trail.drawOnTop = true;
         this.trail.removeOnFadeOut = false;
         this.getLevel().entityManager.addTrail(this.trail);
      }

   }

   public void tickMovement(float var1) {
      if (this.getAttackOwner() != null) {
         float var2 = ((Float)this.getAttackOwner().buffManager.getModifier(BuffModifiers.SPEED) - 1.0F) * 100.0F / 2.0F;
         if ((Boolean)this.hasTarget.get()) {
            this.setSpeed(250.0F + var2);
            this.moveAngle -= var1;
         } else {
            this.setSpeed(120.0F + var2);
            this.moveAngle -= 0.1F * var1;
         }

         this.toMove += var1;

         while(this.toMove > 4.0F) {
            float var3 = this.x;
            float var4 = this.y;
            super.tickMovement(4.0F);
            this.toMove -= 4.0F;
            Point2D.Float var5 = GameMath.normalize(var3 - this.x, var4 - this.y);
            if (this.trail != null) {
               float var6 = 0.0F;
               this.trail.addPoint(new TrailVector(this.x + var5.x * var6, this.y + var5.y * var6, -var5.x, -var5.y, this.trail.thickness, 0.0F));
            }
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      Mob var1 = this.getAttackOwner();
      if (var1 != null) {
         this.damage = new GameDamage(DamageTypeRegistry.SUMMON, RavenlordsHeaddressSetBonusBuff.getFinalDamage(var1, this.baseDamage));
         if (!var1.buffManager.hasBuff((Buff)BuffRegistry.SetBonuses.RAVENLORDS_HEADDRESS)) {
            this.remove(0.0F, 0.0F, (Attacker)null, true);
         }
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 20; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle((float)GameRandom.globalRandom.nextInt(360), (float)GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(239, 235, 255));
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.spit, SoundEffect.effect(this).volume(0.5F));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 16;
      TextureDrawOptionsEnd var13 = MobRegistry.Textures.ravenlords_set_feather.initDraw().light(var10).rotate(this.moveAngle, 16, 16).pos(var11, var12);
      var3.add((var1x) -> {
         var13.draw();
      });
   }

   public void dispose() {
      super.dispose();
      if (this.trail != null) {
         this.trail.removeOnFadeOut = true;
      }

   }
}
