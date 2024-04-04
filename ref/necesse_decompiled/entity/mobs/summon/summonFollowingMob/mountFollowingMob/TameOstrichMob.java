package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TameOstrichMob extends MountFollowingMob {
   public TameOstrichMob() {
      super(50);
      this.setSpeed(65.0F);
      this.setFriction(3.0F);
      this.setSwimSpeed(0.4F);
      this.moveAccuracy = 10;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-16, -12, 32, 24);
      this.selectBox = new Rectangle(-18, -51, 36, 58);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFollowerAINode(480, 64));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ostrich, GameRandom.globalRandom.nextInt(5), 12, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 11;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var15 = MobRegistry.Textures.ostrich.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      final Object var14;
      if (!this.isMounted()) {
         var14 = () -> {
         };
      } else {
         var14 = MobRegistry.Textures.ostrichMount.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      }

      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            ((DrawOptions)var14).draw();
         }

         public void drawBehindRider(TickManager var1) {
            var15.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public int getRockSpeed() {
      return 10;
   }

   public Point getSpriteOffset(int var1, int var2) {
      Point var3 = new Point(0, 0);
      if (var1 == 1 || var1 == 2) {
         var3.y = 2;
      }

      var3.x += this.getRiderDrawXOffset();
      var3.y += this.getRiderDrawYOffset();
      return var3;
   }

   public int getRiderDrawYOffset() {
      return this.inLiquid() ? 0 : -24;
   }

   public GameTexture getRiderMask() {
      return MobRegistry.Textures.mountmask;
   }
}
