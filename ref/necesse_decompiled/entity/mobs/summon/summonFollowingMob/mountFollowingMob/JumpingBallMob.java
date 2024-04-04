package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class JumpingBallMob extends MountFollowingJumpingMob {
   private static final int[] frameOffsets = new int[]{0, -8, -18, -30, -18, -8};

   public JumpingBallMob() {
      super(50);
      this.setSpeed(20.0F);
      this.setFriction(2.0F);
      this.setJumpStrength(100.0F);
      this.setJumpCooldown(100);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -24, 32, 32);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 54;
      boolean var13 = this.inLiquid(var5, var6);
      int var15 = 0;
      int var14;
      if (var13) {
         var14 = 0;
      } else {
         var14 = this.getJumpAnimationFrame(6);
         var15 = frameOffsets[var14];
      }

      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var16 = MobRegistry.Textures.jumpingBall.initDraw().sprite(var14, var13 ? 1 : 0, 64).light(var10).pos(var11, var12);
      final int var17 = this.dir;
      final TextureDrawOptionsEnd var18 = MobRegistry.Textures.jumpingBall.initDraw().sprite(2 + var17, 1, 64).light(var10).pos(var11, var12 + var15);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            if (var17 != 0) {
               var18.draw();
            }

         }

         public void drawBehindRider(TickManager var1) {
            if (var17 == 0) {
               var18.draw();
            }

            var16.draw();
         }
      });
      if (!this.inLiquid(var5, var6)) {
         TextureDrawOptionsEnd var19 = MobRegistry.Textures.jumpingBall_shadow.initDraw().sprite(var14, 0, 64).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var19.draw();
         });
      }

   }

   public Point getSpriteOffset(int var1, int var2) {
      return new Point(this.getRiderDrawXOffset(), this.getRiderDrawYOffset());
   }

   public int getRiderDrawYOffset() {
      return this.inLiquid() ? 4 : frameOffsets[this.getJumpAnimationFrame(6)] - 8;
   }

   public GameTexture getRiderMask() {
      return MobRegistry.Textures.mountmask;
   }
}
