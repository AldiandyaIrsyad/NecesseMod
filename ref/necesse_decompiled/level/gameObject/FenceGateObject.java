package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class FenceGateObject extends DoorObject implements FenceObjectInterface {
   protected String textureName;
   protected GameTexture texture;
   protected int collisionWidth;
   protected int collisionHeight;
   protected LinkedList<Integer> connections;

   private FenceGateObject(int var1, boolean var2, String var3, Color var4, int var5, int var6) {
      super(new Rectangle(32, 32), var1, var2);
      this.connections = new LinkedList();
      this.setItemCategory(new String[]{"objects", "fencesandgates"});
      this.textureName = var3;
      this.mapColor = var4;
      this.collisionWidth = var5;
      this.collisionHeight = var6;
      this.isFence = true;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.canPlaceOnShore = true;
      this.regionType = SemiRegion.RegionType.FENCE_GATE;
      this.replaceCategories.add("fencegate");
      this.canReplaceCategories.add("fencegate");
      this.canReplaceCategories.add("fence");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("door");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public boolean attachesToObject(GameObject var1, Level var2, int var3, int var4, LevelObject var5) {
      return var5.object.isWall || var5.object.getID() == var1.getID() || this.connections.contains(var5.object.getID());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - this.texture.getHeight() + 32;
      LevelObject var12 = var3.getLevelObject(var4, var5 - 1);
      LevelObject var13 = var3.getLevelObject(var4, var5 + 1);
      boolean var14 = this.attachesToObject(this, var3, var4, var5, var12);
      boolean var15 = this.attachesToObject(this, var3, var4, var5, var13);
      if (!var14 && !var15) {
         final TextureDrawOptionsEnd var18 = this.texture.initDraw().sprite(1, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 14;
            }

            public void draw(TickManager var1) {
               var18.draw();
            }
         });
      } else {
         final SharedTextureDrawOptions var16 = new SharedTextureDrawOptions(this.texture);
         final SharedTextureDrawOptions var17 = new SharedTextureDrawOptions(this.texture);
         var16.addSprite(2, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 + 14);
         if (var15) {
            var16.addSprite(3, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 + 14);
         }

         var17.addSprite(2, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - 14);
         var17.addSprite(4, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - 14);
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 26;
            }

            public void draw(TickManager var1) {
               var16.draw();
            }
         });
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 6;
            }

            public void draw(TickManager var1) {
               var17.draw();
            }
         });
      }

   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3) - this.texture.getHeight() + 32;
      LevelObject var10 = var1.getLevelObject(var2, var3 - 1);
      LevelObject var11 = var1.getLevelObject(var2, var3 + 1);
      boolean var12 = this.attachesToObject(this, var1, var2, var3, var10);
      boolean var13 = this.attachesToObject(this, var1, var2, var3, var11);
      if (!var12 && !var13) {
         this.texture.initDraw().sprite(1, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
      } else {
         this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - 14);
         this.texture.initDraw().sprite(4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - 14);
         this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 + 14);
         if (var13) {
            this.texture.initDraw().sprite(3, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 + 14);
         }
      }

   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      LevelObject var5 = var1.getLevelObject(var2, var3 - 1);
      LevelObject var6 = var1.getLevelObject(var2, var3 + 1);
      boolean var7 = this.attachesToObject(this, var1, var2, var3, var5);
      boolean var8 = this.attachesToObject(this, var1, var2, var3, var6);
      return !var7 && !var8 ? new Rectangle(var2 * 32, var3 * 32 + (32 - this.collisionHeight) / 2, 32, this.collisionHeight) : new Rectangle(var2 * 32 + (32 - this.collisionWidth) / 2, var3 * 32, this.collisionWidth, 32);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }

   public void playSwitchSound(Level var1, int var2, int var3) {
      if (var1.isClient()) {
         Screen.playSound(this.isSwitched ? GameResources.doorclose : GameResources.dooropen, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
      }

   }

   public static int[] registerGatePair(int var0, String var1, String var2, Color var3, int var4, int var5, float var6) {
      FenceGateObject var7 = new FenceGateObject(0, false, var2, var3, var4, var5);
      int var8 = ObjectRegistry.registerObject(var1, var7, var6, true);
      FenceGateOpenObject var9 = new FenceGateOpenObject(var8, true, var2, var3, var4, var5);
      int var10 = ObjectRegistry.registerObject(var1 + "open", var9, 0.0F, false);
      var7.counterID = var10;
      var7.connections.add(var10);
      var7.connections.add(var0);
      var9.connections.add(var8);
      var9.connections.add(var0);
      GameObject var11 = ObjectRegistry.getObject(var0);
      if (var11 instanceof FenceObject) {
         ((FenceObject)var11).connections.add(var8);
         ((FenceObject)var11).connections.add(var10);
      }

      return new int[]{var8, var10};
   }

   // $FF: synthetic method
   FenceGateObject(int var1, boolean var2, String var3, Color var4, int var5, int var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   private static class FenceGateOpenObject extends FenceGateObject {
      private FenceGateOpenObject(int var1, boolean var2, String var3, Color var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6, null);
      }

      public LootTable getLootTable(Level var1, int var2, int var3) {
         return ObjectRegistry.getObject(this.counterID).getLootTable(var1, var2, var3);
      }

      public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
         GameLight var9 = var3.getLightLevel(var4, var5);
         int var10 = var7.getTileDrawX(var4);
         int var11 = var7.getTileDrawY(var5) - this.texture.getHeight() + 32;
         LevelObject var12 = var3.getLevelObject(var4, var5 - 1);
         LevelObject var13 = var3.getLevelObject(var4, var5 + 1);
         boolean var14 = this.attachesToObject(this, var3, var4, var5, var12);
         boolean var15 = this.attachesToObject(this, var3, var4, var5, var13);
         final ArrayList var16;
         if (!var14 && !var15) {
            var16 = new ArrayList();
            var16.add(this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11));
            var1.add(new LevelSortedDrawable(this, var4, var5) {
               public int getSortY() {
                  return 14;
               }

               public void draw(TickManager var1) {
                  var16.forEach(TextureDrawOptions::draw);
               }
            });
         } else {
            var16 = new ArrayList();
            final ArrayList var17 = new ArrayList();
            var16.add(this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 + 14));
            if (var15) {
               var16.add(this.texture.initDraw().sprite(3, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 + 14));
            }

            var17.add(this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - 14));
            var16.add(this.texture.initDraw().sprite(5, 0, 32, this.texture.getHeight()).light(var9).pos(var10 - 16, var11 + 14));
            var1.add(new LevelSortedDrawable(this, var4, var5) {
               public int getSortY() {
                  return 26;
               }

               public void draw(TickManager var1) {
                  var16.forEach(TextureDrawOptions::draw);
               }
            });
            var1.add(new LevelSortedDrawable(this, var4, var5) {
               public int getSortY() {
                  return 6;
               }

               public void draw(TickManager var1) {
                  var17.forEach(TextureDrawOptions::draw);
               }
            });
         }

      }

      public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
         return new Rectangle();
      }

      public boolean shouldSnapSmartMining(Level var1, int var2, int var3) {
         return true;
      }

      public boolean isSolid(Level var1, int var2, int var3) {
         return false;
      }

      // $FF: synthetic method
      FenceGateOpenObject(int var1, boolean var2, String var3, Color var4, int var5, int var6, Object var7) {
         this(var1, var2, var3, var4, var5, var6);
      }
   }
}
