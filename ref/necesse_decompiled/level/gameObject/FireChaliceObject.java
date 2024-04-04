package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
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

public class FireChaliceObject extends FireChaliceAbstractObject {
   protected int counterIDTopRight;
   protected int counterIDBotLeft;
   protected int counterIDBotRight;

   private FireChaliceObject(String var1, Color var2) {
      super(var1, var2);
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 4, var3 * 32 + 5, 28, 27);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32 + 5, 28, 27);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32, var3 * 32, 28, 24) : new Rectangle(var2 * 32 + 4, var3 * 32, 27, 24);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   protected void setCounterIDs(int var1, int var2, int var3, int var4) {
      this.counterIDTopRight = var2;
      this.counterIDBotLeft = var3;
      this.counterIDBotRight = var4;
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(0, 0, 2, 2, var1, true, new int[]{this.getID(), this.counterIDTopRight, this.counterIDBotLeft, this.counterIDBotRight});
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
      if (var10 == 0) {
         var16 = this.texture.initDraw().section(var15, 32 + var15, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         if (var14) {
            var22 = this.texture.initDraw().section(var13, var13 + 32, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         } else {
            var22 = () -> {
            };
         }
      } else if (var10 == 1) {
         var16 = this.texture.initDraw().section(32 + var15, 64 + var15, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         if (var14) {
            var22 = this.texture.initDraw().section(var13 + 32, var13 + 64, 0, this.texture.getHeight() - 32).light(var9).pos(var11, var12 - (this.texture.getHeight() - 64));
         } else {
            var22 = () -> {
            };
         }
      } else if (var10 == 2) {
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

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      if (var4 == 1) {
         --var2;
      } else if (var4 == 2) {
         --var2;
         --var3;
      } else if (var4 == 3) {
         --var3;
      }

      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(1, 0, 64, this.texture.getHeight()).alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 64));
   }

   public static int[] registerFireChalice(String var0, String var1, Color var2, boolean var3, boolean var4) {
      FireChaliceObject var5;
      int var9 = ObjectRegistry.registerObject(var0, var5 = new FireChaliceObject(var1, var2), 5.0F, var3, var4);
      FireChaliceObject2 var6;
      int var10 = ObjectRegistry.registerObject(var0 + "2", var6 = new FireChaliceObject2(var1, var2), 0.0F, false);
      FireChaliceObject3 var7;
      int var11 = ObjectRegistry.registerObject(var0 + "3", var7 = new FireChaliceObject3(var1, var2), 0.0F, false);
      FireChaliceObject4 var8;
      int var12 = ObjectRegistry.registerObject(var0 + "4", var8 = new FireChaliceObject4(var1, var2), 0.0F, false);
      var5.setCounterIDs(var9, var10, var11, var12);
      var6.setCounterIDs(var9, var10, var11, var12);
      var7.setCounterIDs(var9, var10, var11, var12);
      var8.setCounterIDs(var9, var10, var11, var12);
      return new int[]{var9, var10, var11, var12};
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      super.onWireUpdate(var1, var2, var3, var4, var5);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean isActive(Level var1, int var2, int var3) {
      return super.isActive(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int getLightLevel(Level var1, int var2, int var3) {
      return super.getLightLevel(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void loadTextures() {
      super.loadTextures();
   }
}
