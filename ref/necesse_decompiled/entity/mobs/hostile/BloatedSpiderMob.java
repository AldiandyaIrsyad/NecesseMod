package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.levelEvent.explosionEvent.BloatedSpiderExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BloatedSpiderMob extends HostileMob {
   public static GameDamage collisionDamage = new GameDamage(250.0F);
   public static GameDamage explosionDamage = new GameDamage(250.0F);

   public BloatedSpiderMob() {
      super(250);
      this.setArmor(30);
      this.setSpeed(45.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-13, -13, 26, 26);
      this.hitBox = new Rectangle(-16, -16, 32, 32);
      this.selectBox = new Rectangle(-16, -16, 32, 32);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 512, (GameDamage)null, 100, 40000));
   }

   public void handleCollisionHit(Mob var1, GameDamage var2, int var3) {
      super.handleCollisionHit(var1, var2, var3);
      if (!var1.isCritter) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public GameDamage getCollisionDamage(Mob var1) {
      return collisionDamage;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.bloatedSpider.body, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      if (this.isServer()) {
         BloatedSpiderExplosionEvent var3 = new BloatedSpiderExplosionEvent(this.x, this.y, 150, explosionDamage, false, 0, this);
         this.getLevel().entityManager.addLevelEvent(var3);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 32;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      if (this.inLiquid(var5, var6)) {
         var12 -= 6;
      }

      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.bloatedSpider.body.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.bloatedSpider.shadow.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var15.draw();
      });
   }
}
