package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class WallObject extends GameObject {
   public static SharedGameTexture wallTextures;
   public static HashMap<String, GameTextureSection> outlineTextures = new HashMap();
   public static GameTexture generatedWallTexture;
   public String textureName;
   public String outlineTextureName;
   public GameTextureSection wallTexture;
   public GameTextureSection outlineTexture;
   public ArrayList<GameObject> connectedWalls = new ArrayList();

   public static void setupWallTextures() {
      wallTextures = new SharedGameTexture("wallsShared");
   }

   public static void generateWallTextures() {
      generatedWallTexture = wallTextures.generate();
      wallTextures.close();
   }

   public WallObject(String var1, String var2, Color var3, int var4, ToolType var5) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.outlineTextureName = var2;
      this.mapColor = var3;
      this.toolTier = var4;
      this.toolType = var5;
      this.regionType = SemiRegion.RegionType.WALL;
      this.setItemCategory(new String[]{"objects", "wallsanddoors"});
      this.isWall = true;
      this.replaceCategories.add("wall");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("door");
      this.canReplaceCategories.add("fence");
      this.canReplaceCategories.add("fencegate");
      this.replaceRotations = false;
   }

   public GameMessage getNewTrapLocalization(GameMessage var1) {
      return new LocalMessage("object", this.textureName + "trap", "trap", var1);
   }

   public void loadTextures() {
      super.loadTextures();

      GameTexture var1;
      try {
         var1 = GameTexture.fromFileRaw("objects/" + this.textureName + "_short");
      } catch (FileNotFoundException var5) {
         var1 = GameTexture.fromFile("objects/" + this.textureName);
      }

      this.wallTexture = wallTextures.addTexture(var1);
      if (outlineTextures.containsKey(this.outlineTextureName)) {
         this.outlineTexture = (GameTextureSection)outlineTextures.get(this.outlineTextureName);
      } else {
         GameTexture var2;
         try {
            var2 = GameTexture.fromFileRaw("objects/" + this.outlineTextureName + "_short");
         } catch (FileNotFoundException var4) {
            var2 = GameTexture.fromFile("objects/" + this.outlineTextureName);
         }

         this.outlineTexture = wallTextures.addTexture(var2);
         outlineTextures.put(this.outlineTextureName, this.outlineTexture);
      }

   }

   protected void addWallDrawOptions(SharedTextureDrawOptions var1, GameTextureSection var2, GameLight var3, float var4, int var5, int var6, GameLight var7, GameLight var8, GameLight var9, boolean[] var10, boolean[] var11, boolean[] var12) {
      boolean var13 = var10[1];
      boolean var14 = var10[3];
      boolean var15 = var10[4];
      boolean var16 = var10[5];
      boolean var17 = var10[6];
      boolean var18 = var10[7];
      boolean var19 = var12[1];
      boolean var20;
      if (!var13) {
         if (var19) {
            var4 = 1.0F;
         }

         if (var14) {
            var1.add(var2.sprite(2, 0, 16)).alpha(var4).light(var8).pos(var5, var6 - 16);
         } else {
            var1.add(var2.sprite(0, 0, 16)).alpha(var4).light(var8).pos(var5, var6 - 16);
         }

         if (var15) {
            var1.add(var2.sprite(1, 0, 16)).alpha(var4).light(var8).pos(var5 + 16, var6 - 16);
         } else {
            var1.add(var2.sprite(3, 0, 16)).alpha(var4).light(var8).pos(var5 + 16, var6 - 16);
         }
      } else {
         var20 = var10[0];
         boolean var21 = var10[2];
         if (var12[1] && !var11[1]) {
            if (!var14) {
               var1.add(var2.sprite(0, 1, 16)).light(var8).pos(var5, var6 - 16);
            }

            if (!var20) {
               var1.add(var2.sprite(0, 7, 16)).light(var8).pos(var5, var6 - 16);
            }

            if (!var15) {
               var1.add(var2.sprite(3, 1, 16)).light(var8).pos(var5 + 16, var6 - 16);
            }

            if (!var21) {
               var1.add(var2.sprite(1, 7, 16)).light(var8).pos(var5 + 16, var6 - 16);
            }
         }

         if (var14 && (!var17 || !var16) && var20) {
            if (var15 && var21) {
               var1.add(var2.sprite(2, 1, 16)).light(var8).pos(var5 + 16, var6 - 16);
            }

            var1.add(var2.sprite(1, 1, 16)).light(var8).pos(var5, var6 - 16);
         }

         if (var15 && (!var17 || !var18) && var21) {
            var1.add(var2.sprite(2, 1, 16)).light(var8).pos(var5 + 16, var6 - 16);
            if (var14 && var20) {
               var1.add(var2.sprite(1, 1, 16)).light(var8).pos(var5, var6 - 16);
            }
         }
      }

      if (var17) {
         if (var14) {
            if (var16) {
               var1.add(var2.sprite(1, 2, 16)).light(var8).pos(var5, var6);
               var1.add(var2.sprite(1, 1, 16)).light(var9).pos(var5, var6 + 16);
            } else {
               var20 = var12[5];
               if (var20) {
                  var1.add(var2.sprite(3, 7, 16)).light(var8).pos(var5, var6);
               } else {
                  var1.add(var2.sprite(0, 5, 16)).light(var8).pos(var5, var6);
               }

               var1.add(var2.sprite(0, 6, 16)).light(var9).pos(var5, var6 + 16);
            }
         } else if (var16) {
            var1.add(var2.sprite(0, 2, 16)).light(var8).pos(var5, var6);
            var1.add(var2.sprite(0, 7, 16)).light(var9).pos(var5, var6 + 16);
         } else {
            var1.add(var2.sprite(0, 2, 16)).light(var8).pos(var5, var6);
            var1.add(var2.sprite(0, 1, 16)).light(var9).pos(var5, var6 + 16);
         }

         if (var15) {
            if (var18) {
               var1.add(var2.sprite(2, 2, 16)).light(var8).pos(var5 + 16, var6);
               var1.add(var2.sprite(2, 1, 16)).light(var9).pos(var5 + 16, var6 + 16);
            } else {
               var20 = var12[7];
               if (var20) {
                  var1.add(var2.sprite(2, 7, 16)).light(var8).pos(var5 + 16, var6);
               } else {
                  var1.add(var2.sprite(1, 5, 16)).light(var8).pos(var5 + 16, var6);
               }

               var1.add(var2.sprite(1, 6, 16)).light(var9).pos(var5 + 16, var6 + 16);
            }
         } else if (var18) {
            var1.add(var2.sprite(3, 2, 16)).light(var8).pos(var5 + 16, var6);
            var1.add(var2.sprite(1, 7, 16)).light(var9).pos(var5 + 16, var6 + 16);
         } else {
            var1.add(var2.sprite(3, 2, 16)).light(var8).pos(var5 + 16, var6);
            var1.add(var2.sprite(3, 1, 16)).light(var9).pos(var5 + 16, var6 + 16);
         }
      } else {
         var20 = var12[6];
         if (var14) {
            if (var20) {
               var1.add(var2.sprite(3, 5, 16)).light(var8).pos(var5, var6);
            } else {
               var1.add(var2.sprite(2, 3, 16)).light(var8).pos(var5, var6);
               var1.add(var2.sprite(2, 4, 16)).light(var8).pos(var5, var6 + 16);
            }
         } else if (var20) {
            var1.add(var2.sprite(2, 5, 16)).light(var8).pos(var5, var6);
         } else {
            var1.add(var2.sprite(0, 3, 16)).light(var8).pos(var5, var6);
            var1.add(var2.sprite(0, 4, 16)).light(var8).pos(var5, var6 + 16);
         }

         if (var15) {
            if (var20) {
               var1.add(var2.sprite(2, 6, 16)).light(var8).pos(var5 + 16, var6);
            } else {
               var1.add(var2.sprite(1, 3, 16)).light(var8).pos(var5 + 16, var6);
               var1.add(var2.sprite(1, 4, 16)).light(var8).pos(var5 + 16, var6 + 16);
            }
         } else if (var20) {
            var1.add(var2.sprite(3, 6, 16)).light(var8).pos(var5 + 16, var6);
         } else {
            var1.add(var2.sprite(3, 3, 16)).light(var8).pos(var5 + 16, var6);
            var1.add(var2.sprite(3, 4, 16)).light(var8).pos(var5 + 16, var6 + 16);
         }
      }

   }

   public void addWallDrawOptions(SharedTextureDrawOptions var1, Level var2, int var3, int var4, GameLight var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "wallSetup", (Runnable)(() -> {
         int var8x = var7.getTileDrawX(var3);
         int var9 = var7.getTileDrawY(var4);
         GameObject[] var10 = var2.getAdjacentObjects(var3, var4);
         boolean var11 = true;
         boolean[] var12 = new boolean[var10.length];
         boolean[] var13 = new boolean[var10.length];

         for(int var14 = 0; var14 < var10.length; ++var14) {
            var12[var14] = var10[var14].isWall && !var10[var14].isDoor;
            var13[var14] = var10[var14] == this || this.connectedWalls.contains(var10[var14]);
            var11 = var11 && var13[var14];
         }

         float var18 = 1.0F;
         if (var8 != null && !Settings.hideUI) {
            Rectangle var15 = new Rectangle(var3 * 32 - 16, var4 * 32 - 32, 64, 48);
            if (var8.getCollision().intersects(var15)) {
               var18 = 0.5F;
            } else if (var15.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
               var18 = 0.5F;
            }
         }

         GameLight var19;
         if (var11) {
            var19 = var5 == null ? var2.getLightLevelWall(var3, var4) : var5;
            var1.add(this.wallTexture.section(16, 48, 16, 48)).light(var19).pos(var8x, var9 - 16);
         } else {
            var19 = var5 == null ? var2.getLightLevelWall(var3, var4 - 1) : var5;
            GameLight var16 = var5 == null ? var2.getLightLevelWall(var3, var4) : var5;
            GameLight var17 = var5 == null ? var2.getLightLevelWall(var3, var4 + 1) : var5;
            this.addWallDrawOptions(var1, this.wallTexture, var5, var18, var8x, var9, var19, var16, var17, var13, var13, var12);
            this.addWallDrawOptions(var1, this.outlineTexture, var5, var18, var8x, var9, var19, var16, var17, var12, var13, var12);
         }

      }));
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      final SharedTextureDrawOptions var9 = new SharedTextureDrawOptions(generatedWallTexture);
      this.addWallDrawOptions(var9, var3, var4, var5, (GameLight)null, var6, var7, var8);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            SharedTextureDrawOptions var10002 = var9;
            Objects.requireNonNull(var10002);
            Performance.record(var1, "wallDraw", (Runnable)(var10002::draw));
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      SharedTextureDrawOptions var8 = new SharedTextureDrawOptions(generatedWallTexture);
      this.addWallDrawOptions(var8, var1, var2, var3, var1.lightManager.newLight(150.0F), (TickManager)null, var7, var6);
      var8.forEachDraw((var0) -> {
         var0.alpha(0.5F);
      }).draw();
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      return Collections.singletonList(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
   }

   public boolean stopsTerrainSplatting() {
      return true;
   }

   public boolean drawsFullTile() {
      return true;
   }

   public static int[] registerWallObjects(String var0, String var1, String var2, int var3, Color var4, ToolType var5, float var6, float var7, boolean var8, boolean var9) {
      WallObject var10 = new WallObject(var1, var2, var4, var3, var5);
      int var11 = ObjectRegistry.registerObject(var0 + "wall", var10, var6, var8, var9);
      int[] var12 = WallDoorObject.registerDoorPair(var0 + "door", var10, var7, var8, var9);
      return new int[]{var11, var12[0], var12[1]};
   }

   public static int[] registerWallObjects(String var0, String var1, String var2, int var3, Color var4, ToolType var5, float var6, float var7, boolean var8) {
      return registerWallObjects(var0, var1, var2, var3, var4, var5, var6, var7, var8, var8);
   }

   public static int[] registerWallObjects(String var0, String var1, String var2, int var3, Color var4, ToolType var5, float var6, float var7) {
      return registerWallObjects(var0, var1, var2, var3, var4, var5, var6, var7, true);
   }

   public static int[] registerWallObjects(String var0, String var1, int var2, Color var3, ToolType var4, float var5, float var6, boolean var7, boolean var8) {
      return registerWallObjects(var0, var1, "walloutlines", var2, var3, var4, var5, var6, var7, var8);
   }

   public static int[] registerWallObjects(String var0, String var1, int var2, Color var3, ToolType var4, float var5, float var6, boolean var7) {
      return registerWallObjects(var0, var1, var2, var3, var4, var5, var6, var7, var7);
   }

   public static int[] registerWallObjects(String var0, String var1, int var2, Color var3, ToolType var4, float var5, float var6) {
      return registerWallObjects(var0, var1, var2, var3, var4, var5, var6, true);
   }

   public static int[] registerWallObjects(String var0, String var1, String var2, int var3, Color var4, float var5, float var6) {
      return registerWallObjects(var0, var1, var2, var3, var4, ToolType.PICKAXE, var5, var6);
   }

   public static int[] registerWallObjects(String var0, String var1, int var2, Color var3, float var4, float var5) {
      return registerWallObjects(var0, var1, var2, var3, ToolType.PICKAXE, var4, var5);
   }
}
