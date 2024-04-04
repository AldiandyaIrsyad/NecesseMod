package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.server.ServerClient;
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
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerValidTargetCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
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

public class FrostPiercerFollowingMob extends FlyingAttackingFollowingMob {
   public Trail trail;
   public float moveAngle;
   private float toMove;

   public FrostPiercerFollowingMob() {
      super(10);
      this.moveAccuracy = 15;
      this.setSpeed(160.0F);
      this.setFriction(2.0F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
      this.selectBox = new Rectangle();
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
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerValidTargetCollisionChaserAI<FrostPiercerFollowingMob>(448, (GameDamage)null, 15, 500, 640, 64) {
         public boolean isValidTarget(FrostPiercerFollowingMob var1, ServerClient var2, Mob var3) {
            if (var2 == null) {
               return false;
            } else {
               Object var4 = GameUtils.castRayFirstHit(new Line2D.Float(var2.playerMob.x, var2.playerMob.y, var3.x, var3.y), 100.0, (var1x) -> {
                  return var1.getLevel().collides(var1x, (new CollisionFilter()).projectileCollision()) ? new Object() : null;
               });
               return var4 == null;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isValidTarget(Mob var1, ServerClient var2, Mob var3) {
            return this.isValidTarget((FrostPiercerFollowingMob)var1, var2, var3);
         }
      }, new FlyingAIMover());
      if (this.isClient()) {
         this.trail = new Trail(this, this.getLevel(), new Color(48, 123, 158), 16.0F, 200, 0.0F);
         this.trail.drawOnTop = true;
         this.trail.removeOnFadeOut = false;
         this.getLevel().entityManager.addTrail(this.trail);
      }

   }

   public void tickMovement(float var1) {
      this.toMove += var1;

      while(this.toMove > 4.0F) {
         float var2 = this.x;
         float var3 = this.y;
         super.tickMovement(4.0F);
         this.toMove -= 4.0F;
         Point2D.Float var4 = GameMath.normalize(var2 - this.x, var3 - this.y);
         this.moveAngle = (float)Math.toDegrees(Math.atan2((double)var4.y, (double)var4.x)) - 90.0F;
         if (this.trail != null) {
            float var5 = 5.0F;
            this.trail.addPoint(new TrailVector(this.x + var4.x * var5, this.y + var4.y * var5, -var4.x, -var4.y, this.trail.thickness, 0.0F));
         }
      }

   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.dx / 10.0F, this.dy / 10.0F).color(new Color(48, 123, 158));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle((float)GameRandom.globalRandom.nextInt(360), (float)GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(48, 123, 158));
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.jinglehit, SoundEffect.effect(this).volume(0.5F));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 20;
      TextureDrawOptionsEnd var13 = MobRegistry.Textures.frostPiercer.initDraw().light(var10).rotate(this.moveAngle, 16, 20).pos(var11, var12);
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
