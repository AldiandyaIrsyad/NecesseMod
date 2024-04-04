package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WallTorchObject extends GameObject {
   public GameTexture texture;

   public WallTorchObject() {
      this.mapColor = new Color(240, 200, 10);
      this.displayMapTooltip = true;
      this.lightLevel = 150;
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.roomProperties.add("lights");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/walltorch");
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(40) && this.isActive(var1, var2, var3)) {
         byte var4 = var1.getObjectRotation(var2, var3);
         int var5 = this.getSprite(var1, var2, var3, var4);
         int var6 = 10;
         if (var5 == 0) {
            var6 += 32;
         } else if (var5 == 1 || var5 == 3) {
            var6 += 14;
         }

         BombProjectile.spawnFuseParticle(var1, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16 + 2), (float)var6);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "torchSetup", (Runnable)(() -> {
         GameLight var6 = var3.getLightLevel(var4, var5);
         int var7x = var7.getTileDrawX(var4);
         int var8 = var7.getTileDrawY(var5);
         byte var9 = var3.getObjectRotation(var4, var5);
         int var10 = this.getSprite(var3, var4, var5, var9);
         boolean var11 = this.isActive(var3, var4, var5);
         if (var10 == 0) {
            var8 -= 16;
         } else if (var10 == 2) {
            var8 += 16;
         }

         final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(var11 ? 0 : 1, var10, 32).light(var6).pos(var7x, var8 - 16);
         final byte var13;
         if (var10 == 0) {
            var13 = 0;
         } else if (var10 == 2) {
            var13 = 32;
         } else {
            var13 = 16;
         }

         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return var13;
            }

            public void draw(TickManager var1) {
               TextureDrawOptions var10002 = var12;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "torchDraw", (Runnable)(var10002::draw));
            }
         });
      }));
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      int var10 = this.getSprite(var1, var2, var3, var4);
      if (var10 == 0) {
         var9 -= 16;
      } else if (var10 == 2) {
         var9 += 16;
      }

      this.texture.initDraw().sprite(0, var10, 32).alpha(var5).draw(var8, var9 - 16);
   }

   protected int getSprite(Level var1, int var2, int var3, int var4) {
      GameObject var5 = var1.getObject(var2, var3 - 1);
      GameObject var6 = var1.getObject(var2 + 1, var3);
      GameObject var7 = var1.getObject(var2, var3 + 1);
      GameObject var8 = var1.getObject(var2 - 1, var3);
      boolean var9 = var5.isWall && !var5.isDoor;
      boolean var10 = var6.isWall && !var6.isDoor;
      boolean var11 = var7.isWall && !var7.isDoor;
      boolean var12 = var8.isWall && !var8.isDoor;
      if (var9) {
         if (var4 == 0 && var11) {
            return 2;
         } else if (var4 == 1 && var10) {
            return 1;
         } else {
            return var4 == 3 && var12 ? 3 : 0;
         }
      } else if (var11) {
         if (var4 == 1 && var10) {
            return 1;
         } else {
            return var4 == 3 && var12 ? 3 : 2;
         }
      } else if (var10) {
         return var4 == 3 && var12 ? 3 : 1;
      } else {
         return var12 ? 3 : 0;
      }
   }

   public int getPlaceRotation(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      if (var5 == 1) {
         return 3;
      } else {
         return var5 == 3 ? 1 : super.getPlaceRotation(var1, var2, var3, var4, var5);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      byte var5 = var1.getObjectRotation(var2, var3);
      int var6 = this.getSprite(var1, var2, var3, var5);
      if (var6 == 0) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32, 0));
      } else if (var6 == 1 || var6 == 3) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16, 0));
      }

      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      if (var1.getObjectID(var2, var3) != 0 && !var1.getObject(var2, var3).isGrass) {
         return "occupied";
      } else {
         boolean var5 = false;
         if (var1.getObject(var2 - 1, var3).isWall && !var1.getObject(var2 - 1, var3).isDoor) {
            var5 = true;
         } else if (var1.getObject(var2 + 1, var3).isWall && !var1.getObject(var2 + 1, var3).isDoor) {
            var5 = true;
         } else if (var1.getObject(var2, var3 - 1).isWall && !var1.getObject(var2, var3 - 1).isDoor) {
            var5 = true;
         } else if (var1.getObject(var2, var3 + 1).isWall && !var1.getObject(var2, var3 + 1).isDoor) {
            var5 = true;
         }

         return !var5 ? "nowall" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      boolean var4 = false;
      if (var1.getObject(var2 - 1, var3).isWall && !var1.getObject(var2 - 1, var3).isDoor) {
         var4 = true;
      } else if (var1.getObject(var2 + 1, var3).isWall && !var1.getObject(var2 + 1, var3).isDoor) {
         var4 = true;
      } else if (var1.getObject(var2, var3 - 1).isWall && !var1.getObject(var2, var3 - 1).isDoor) {
         var4 = true;
      } else if (var1.getObject(var2, var3 + 1).isWall && !var1.getObject(var2, var3 + 1).isDoor) {
         var4 = true;
      }

      return var4;
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.isActive(var1, var2, var3) ? 150 : 0;
   }

   public boolean isActive(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      return this.getMultiTile(var4).streamIDs(var2, var3).noneMatch((var1x) -> {
         return var1.wireManager.isWireActiveAny(var1x.tileX, var1x.tileY);
      });
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      byte var6 = var1.getObjectRotation(var2, var3);
      Rectangle var7 = this.getMultiTile(var6).getTileRectangle(var2, var3);
      var1.lightManager.updateStaticLight(var7.x, var7.y, var7.x + var7.width - 1, var7.y + var7.height - 1, true);
   }
}
