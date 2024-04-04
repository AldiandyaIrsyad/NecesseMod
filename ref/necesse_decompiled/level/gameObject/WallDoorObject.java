package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class WallDoorObject extends DoorObject {
   protected WallObject wallObject;

   private WallDoorObject(WallObject var1, int var2, boolean var3) {
      super(new Rectangle(), var2, var3);
      this.wallObject = var1;
      this.toolTier = var1.toolTier;
      this.toolType = var1.toolType;
      this.setItemCategory(new String[]{"objects", "wallsanddoors"});
      this.mapColor = var1.mapColor;
      this.isWall = true;
      this.replaceCategories.add("door");
      this.canReplaceCategories.add("door");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("fence");
      this.canReplaceCategories.add("fencegate");
   }

   public boolean shouldMirror(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return var1.getObject(var2 + 1, var3) instanceof WallDoorObject;
      } else if (var4 == 1) {
         return var1.getObject(var2, var3 - 1) instanceof WallDoorObject;
      } else if (var4 == 2) {
         return var1.getObject(var2 - 1, var3) instanceof WallDoorObject;
      } else {
         return var4 == 3 ? var1.getObject(var2, var3 + 1) instanceof WallDoorObject : false;
      }
   }

   private boolean testSolid(Level var1, int var2, int var3) {
      GameObject var4 = var1.getObject(var2, var3);
      return var4.isWall || var4.isDoor || var4.isFence || var4.isRock || var4.isSolid;
   }

   public boolean isOpen(Level var1, int var2, int var3, int var4) {
      if (var4 != 0 && var4 != 2) {
         if (this.isSwitched && this.testSolid(var1, var2 - 1, var3) && this.testSolid(var1, var2 + 1, var3)) {
            return false;
         }

         if (!this.isSwitched && this.testSolid(var1, var2, var3 - 1) && this.testSolid(var1, var2, var3 + 1)) {
            return false;
         }
      } else {
         if (!this.isSwitched && this.testSolid(var1, var2 - 1, var3) && this.testSolid(var1, var2 + 1, var3)) {
            return false;
         }

         if (this.isSwitched && this.testSolid(var1, var2, var3 - 1) && this.testSolid(var1, var2, var3 + 1)) {
            return false;
         }
      }

      return true;
   }

   private boolean isWall(Level var1, int var2, int var3) {
      GameObject var4 = var1.getObject(var2, var3);
      return var4.isWall || var4.isDoor || var4.isFence || var4.isRock;
   }

   public int getPlaceRotation(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      boolean var6 = this.isWall(var1, var2, var3 - 1) || this.isWall(var1, var2, var3 + 1);
      boolean var7 = !var6 && (this.isWall(var1, var2 - 1, var3) || this.isWall(var1, var2 + 1, var3));
      if (var6) {
         if (var5 == 0) {
            return 1;
         }

         if (var5 == 2) {
            return 3;
         }
      } else if (var7) {
         if (var5 == 1) {
            return 0;
         }

         if (var5 == 3) {
            return 2;
         }
      }

      return super.getPlaceRotation(var1, var2, var3, var4, var5);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      int var9 = var7.getTileDrawX(var4);
      int var10 = var7.getTileDrawY(var5);
      GameLight var11 = var3.getLightLevelWall(var4, var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      final SharedTextureDrawOptions var13 = new SharedTextureDrawOptions(WallObject.generatedWallTexture);
      float var14 = 1.0F;
      if (var8 != null && !Settings.hideUI) {
         Rectangle var15 = new Rectangle(var4 * 32 - 16, var5 * 32 - 48, 64, 80);
         if (var12 == 0) {
            var15.height -= 26;
         } else if (var12 == 2) {
            var15.y += 28;
            var15.height -= 28;
         }

         if (var8.getCollision().intersects(var15)) {
            var14 = 0.5F;
         } else if (var15.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
            var14 = 0.5F;
         }
      }

      boolean var17 = this.shouldMirror(var3, var4, var5, var12);
      final byte var16;
      if (var12 == 0) {
         var13.add(this.wallObject.wallTexture.sprite(2, 0, 32, 64)).mirror(var17, false).alpha(var14).light(var11).pos(var9, var10 - 32);
         var13.add(this.wallObject.outlineTexture.sprite(2, 0, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
         var16 = 4;
      } else if (var12 == 1) {
         var13.add(this.wallObject.wallTexture.sprite(4, 1, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
         var13.add(this.wallObject.outlineTexture.sprite(4, 1, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
         var16 = 20;
      } else if (var12 == 2) {
         var13.add(this.wallObject.wallTexture.sprite(2, 1, 32, 64)).mirror(var17, false).alpha(var14).light(var11).pos(var9, var10 - 32);
         var13.add(this.wallObject.outlineTexture.sprite(2, 1, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
         var16 = 28;
      } else {
         var13.add(this.wallObject.wallTexture.sprite(4, 0, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
         var13.add(this.wallObject.outlineTexture.sprite(4, 0, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
         var16 = 20;
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var16;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      boolean var10 = this.shouldMirror(var1, var2, var3, var4);
      if (var4 == 0) {
         this.wallObject.wallTexture.sprite(2, 0, 32, 64).initDraw().mirror(var10, false).alpha(var5).draw(var8, var9 - 32);
         this.wallObject.outlineTexture.sprite(2, 0, 32, 64).initDraw().alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 1) {
         this.wallObject.wallTexture.sprite(4, 1, 32, 64).initDraw().alpha(var5).draw(var8, var9 - 32);
         this.wallObject.outlineTexture.sprite(4, 1, 32, 64).initDraw().alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 2) {
         this.wallObject.wallTexture.sprite(2, 1, 32, 64).initDraw().mirror(var10, false).alpha(var5).draw(var8, var9 - 32);
         this.wallObject.outlineTexture.sprite(2, 1, 32, 64).initDraw().alpha(var5).draw(var8, var9 - 32);
      } else {
         this.wallObject.wallTexture.sprite(4, 0, 32, 64).initDraw().alpha(var5).draw(var8, var9 - 32);
         this.wallObject.outlineTexture.sprite(4, 0, 32, 64).initDraw().alpha(var5).draw(var8, var9 - 32);
      }

   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32, var3 * 32, 32, 12);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 28, var3 * 32, 4, 32);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32, var3 * 32 + 28, 32, 4) : new Rectangle(var2 * 32, var3 * 32, 4, 32);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      byte var5 = var1.getObjectRotation(var2, var3);
      if (var5 == 0) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      } else if (var5 == 1) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -24, 32, 24));
      } else {
         if (var5 == 2) {
            return var4;
         }

         var4.add(new ObjectHoverHitbox(var2, var3, 0, -24, 32, 24));
      }

      return var4;
   }

   public boolean shouldSnapSmartMining(Level var1, int var2, int var3) {
      return true;
   }

   public boolean isSolid(Level var1, int var2, int var3) {
      return !this.isOpen(var1, var2, var3, var1.getObjectRotation(var2, var3));
   }

   public int getLightLevelMod(Level var1, int var2, int var3) {
      return this.isSolid(var1, var2, var3) && !this.isLightTransparent(var1, var2, var3) && !this.isOpen(var1, var2, var3, var1.getObjectRotation(var2, var3)) ? 40 : 10;
   }

   public boolean stopsTerrainSplatting() {
      return true;
   }

   public void playSwitchSound(Level var1, int var2, int var3) {
      if (var1.isClient()) {
         Screen.playSound(this.isSwitched ? GameResources.doorclose : GameResources.dooropen, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
      }

   }

   public static int[] registerDoorPair(String var0, WallObject var1, float var2, boolean var3, boolean var4) {
      WallDoorObject var5 = new WallDoorObject(var1, 0, false);
      int var6 = ObjectRegistry.registerObject(var0, var5, var2, var3, var4);
      int var7 = ObjectRegistry.registerObject(var0 + "open", new WallDoorOpenObject(var1, var6), 0.0F, false);
      WallDoorLockedObject var8 = new WallDoorLockedObject(var1, var5, 1, false);
      int var9 = ObjectRegistry.registerObject(var0 + "locked", var8, var2, false);
      int var10 = ObjectRegistry.registerObject(var0 + "unlocked", new WallDoorUnlockedObject(var1, var9), 0.0F, false);
      var5.counterID = var7;
      var8.counterID = var10;
      return new int[]{var6, var7, var9, var10};
   }

   // $FF: synthetic method
   WallDoorObject(WallObject var1, int var2, boolean var3, Object var4) {
      this(var1, var2, var3);
   }

   private static class WallDoorOpenObject extends WallDoorObject {
      private WallDoorOpenObject(WallObject var1, int var2) {
         super(var1, var2, true, null);
      }

      public LootTable getLootTable(Level var1, int var2, int var3) {
         return ObjectRegistry.getObject(this.counterID).getLootTable(var1, var2, var3);
      }

      public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
         int var9 = var7.getTileDrawX(var4);
         int var10 = var7.getTileDrawY(var5);
         GameLight var11 = var3.getLightLevelWall(var4, var5);
         byte var12 = var3.getObjectRotation(var4, var5);
         final SharedTextureDrawOptions var13 = new SharedTextureDrawOptions(WallObject.generatedWallTexture);
         float var14 = 1.0F;
         if (var8 != null) {
            Rectangle var15 = new Rectangle(var4 * 32 - 16, var5 * 32 - 48, 64, 80);
            if (var8.getCollision().intersects(var15)) {
               var14 = 0.5F;
            } else if (var15.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
               var14 = 0.5F;
            }
         }

         boolean var17 = this.shouldMirror(var3, var4, var5, var12);
         final byte var16;
         if (var12 == 0) {
            var13.add(this.wallObject.wallTexture.sprite(3, 1, 32, 64)).mirror(var17, false).alpha(var14).light(var11).pos(var9, var10 - 32);
            var13.add(this.wallObject.outlineTexture.sprite(3, 1, 32, 64)).mirror(var17, false).alpha(var14).light(var11).pos(var9, var10 - 32);
            var16 = 20;
         } else if (var12 == 1) {
            if (var17) {
               if (var3.getObject(var4, var5 + 1).isWall && !var3.getObject(var4, var5 + 1).isDoor) {
                  var10 += 8;
               }

               var10 += 26;
            }

            var13.add(this.wallObject.wallTexture.sprite(5, 1, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
            var13.add(this.wallObject.outlineTexture.sprite(5, 1, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
            if (var17) {
               var16 = 28;
            } else {
               var16 = 4;
            }
         } else if (var12 == 2) {
            var13.add(this.wallObject.wallTexture.sprite(3, 0, 32, 64)).mirror(var17, false).alpha(var14).light(var11).pos(var9, var10 - 32);
            var13.add(this.wallObject.outlineTexture.sprite(3, 0, 32, 64)).mirror(var17, false).alpha(var14).light(var11).pos(var9, var10 - 32);
            var16 = 20;
         } else {
            if (var3.getObject(var4, var5 + 1).isWall && !var3.getObject(var4, var5 + 1).isDoor) {
               var10 += 8;
            }

            if (var17) {
               var10 -= 26;
            }

            var13.add(this.wallObject.wallTexture.sprite(5, 0, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
            var13.add(this.wallObject.outlineTexture.sprite(5, 0, 32, 64)).alpha(var14).light(var11).pos(var9, var10 - 32);
            if (var17) {
               var16 = 4;
            } else {
               var16 = 28;
            }
         }

         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return var16;
            }

            public void draw(TickManager var1) {
               var13.draw();
            }
         });
      }

      public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
         boolean var5 = this.shouldMirror(var1, var2, var3, var4);
         if (var4 == 0) {
            return var5 ? new Rectangle(var2 * 32, var3 * 32, 4, 32) : new Rectangle(var2 * 32 + 28, var3 * 32, 4, 32);
         } else if (var4 == 1) {
            return var5 ? new Rectangle(var2 * 32, var3 * 32 + 28, 32, 4) : new Rectangle(var2 * 32, var3 * 32, 32, 4);
         } else if (var4 == 2) {
            return var5 ? new Rectangle(var2 * 32 + 28, var3 * 32, 4, 32) : new Rectangle(var2 * 32, var3 * 32, 4, 32);
         } else {
            return var5 ? new Rectangle(var2 * 32, var3 * 32, 32, 4) : new Rectangle(var2 * 32, var3 * 32 + 28, 32, 4);
         }
      }

      public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
         List var4 = super.getHoverHitboxes(var1, var2, var3);
         byte var5 = var1.getObjectRotation(var2, var3);
         if (var5 == 2) {
            var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
         }

         return var4;
      }

      // $FF: synthetic method
      WallDoorOpenObject(WallObject var1, int var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class WallDoorLockedObject extends WallDoorObject {
      public WallDoorObject normalDoor;

      private WallDoorLockedObject(WallObject var1, WallDoorObject var2, int var3, boolean var4) {
         super(var1, var3, var4, null);
         this.regionType = SemiRegion.RegionType.WALL;
         this.normalDoor = var2;
      }

      public LootTable getLootTable(Level var1, int var2, int var3) {
         return this.normalDoor.getLootTable(var1, var2, var3);
      }

      public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
         return false;
      }

      public void onPathOpened(Level var1, int var2, int var3) {
         var1.entityManager.doDamage(var2, var3, this.objectHealth, (TileDamageType)TileDamageType.Object, this.toolTier, (ServerClient)null, true, var2 * 32 + 16, var3 * 32 + 16);
      }

      public void onPathBreakDown(Level var1, int var2, int var3, int var4, int var5, int var6) {
         var1.entityManager.doDamage(var2, var3, this.objectHealth, (TileDamageType)TileDamageType.Object, this.toolTier, (ServerClient)null, true, var5, var6);
      }

      public boolean pathCollidesIfOpen(Level var1, int var2, int var3, CollisionFilter var4, Rectangle var5) {
         return true;
      }

      // $FF: synthetic method
      WallDoorLockedObject(WallObject var1, WallDoorObject var2, int var3, boolean var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   private static class WallDoorUnlockedObject extends WallDoorOpenObject {
      private WallDoorUnlockedObject(WallObject var1, int var2) {
         super(var1, var2, null);
         this.regionType = SemiRegion.RegionType.OPEN;
      }

      public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
         return false;
      }

      public void onPathOpened(Level var1, int var2, int var3) {
         var1.entityManager.doDamage(var2, var3, this.objectHealth, (TileDamageType)TileDamageType.Object, this.toolTier, (ServerClient)null, true, var2 * 32 + 16, var3 * 32 + 16);
      }

      public void onPathBreakDown(Level var1, int var2, int var3, int var4, int var5, int var6) {
         var1.entityManager.doDamage(var2, var3, this.objectHealth, (TileDamageType)TileDamageType.Object, this.toolTier, (ServerClient)null, true, var5, var6);
      }

      public boolean pathCollidesIfOpen(Level var1, int var2, int var3, CollisionFilter var4, Rectangle var5) {
         return true;
      }

      // $FF: synthetic method
      WallDoorUnlockedObject(WallObject var1, int var2, Object var3) {
         this(var1, var2);
      }
   }
}
