package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class FireChaliceObject4 extends FireChaliceAbstractObject {
   protected int counterIDTopLeft;
   protected int counterIDTopRight;
   protected int counterIDBotLeft;

   public FireChaliceObject4(String var1, Color var2) {
      super(var1, var2);
   }

   protected void setCounterIDs(int var1, int var2, int var3, int var4) {
      this.counterIDTopLeft = var1;
      this.counterIDTopRight = var2;
      this.counterIDBotLeft = var3;
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32, var3 * 32, 28, 24);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 4, var3 * 32, 28, 24);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 4, var3 * 32 + 5, 28, 27) : new Rectangle(var2 * 32, var3 * 32 + 5, 28, 27);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(1, 1, 2, 2, var1, false, new int[]{this.counterIDTopLeft, this.counterIDTopRight, this.counterIDBotLeft, this.getID()});
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      byte var10 = var3.getObjectRotation(var4, var5);
      int var11 = var7.getTileDrawX(var4);
      int var12 = var7.getTileDrawY(var5);
      boolean var14 = this.isActive(var3, var4, var5);
      int var15 = var14 ? 64 : 0;
      int var13;
      synchronized(this.drawRandom) {
         long var17 = (Long)this.getMultiTile(var10).getMasterLevelObject(var3, var4, var5).map((var1x) -> {
            return this.getTileSeed(var1x.tileX, var1x.tileY);
         }).orElseGet(() -> {
            return this.getTileSeed(var4, var5);
         });
         int var19 = GameUtils.getAnim((long)this.drawRandom.seeded(var17).nextInt(800) + var3.getWorldEntity().getWorldTime(), 4, 800);
         var13 = 128 + var19 * 64;
      }

      final TextureDrawOptionsEnd var16;
      final Object var22;
      if (var10 == 2) {
         var16 = this.texture.initDraw().section(var15, 32 + var15, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         if (var14) {
            var22 = this.texture.initDraw().section(var13, var13 + 32, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         } else {
            var22 = () -> {
            };
         }
      } else if (var10 == 3) {
         var16 = this.texture.initDraw().section(32 + var15, 64 + var15, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         if (var14) {
            var22 = this.texture.initDraw().section(var13 + 32, var13 + 64, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         } else {
            var22 = () -> {
            };
         }
      } else if (var10 == 0) {
         var16 = this.texture.initDraw().section(32 + var15, 64 + var15, this.texture.getHeight() - 32, this.texture.getHeight()).light(var9).pos(var11, var12);
         if (var14) {
            var22 = this.texture.initDraw().section(var13 + 32, var13 + 64, this.texture.getHeight() - 32, this.texture.getHeight()).light(var9).pos(var11, var12);
         } else {
            var22 = () -> {
            };
         }
      } else {
         var16 = this.texture.initDraw().section(var15, 32 + var15, this.texture.getHeight() - 32, this.texture.getHeight()).light(var9).pos(var11, var12);
         if (var14) {
            var22 = this.texture.initDraw().section(var13, var13 + 32, this.texture.getHeight() - 32, this.texture.getHeight()).light(var9).pos(var11, var12);
         } else {
            var22 = () -> {
            };
         }
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var16.draw();
            ((DrawOptions)var22).draw();
         }
      });
   }
}
