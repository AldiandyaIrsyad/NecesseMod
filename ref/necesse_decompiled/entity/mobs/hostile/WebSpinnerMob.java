package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SmallGroundWebEvent;
import necesse.entity.mobs.GameDamage;
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

public class WebSpinnerMob extends HostileMob {
   public static GameDamage collisionDamage = new GameDamage(45.0F);
   private long sinceLastWeb;
   Point lastWebPos;

   public WebSpinnerMob() {
      super(300);
      this.setArmor(30);
      this.setSpeed(50.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-18, -24, 36, 36);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 384, collisionDamage, 25, 5000));
      this.sinceLastWeb = this.getLevel().getWorldEntity().getLocalTime();
      this.lastWebPos = this.getPositionPoint();
   }

   public void serverTick() {
      super.serverTick();
      if (this.getLevel().getWorldEntity().getLocalTime() - this.sinceLastWeb > 125L && 25.0 < this.getPositionPoint().distance(this.lastWebPos)) {
         SmallGroundWebEvent var1 = new SmallGroundWebEvent(this, (int)this.x, (int)this.y, GameRandom.globalRandom);
         this.getLevel().entityManager.addLevelEvent(var1);
         this.sinceLastWeb = this.getLevel().getWorldEntity().getLocalTime();
         this.lastWebPos = this.getPositionPoint();
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.webSpinner.body, var3, 4, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 16;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      if (this.inLiquid(var5, var6)) {
         var12 -= 6;
      }

      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.webSpinner.body.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.webSpinner.shadow.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var15.draw();
      });
   }
}
