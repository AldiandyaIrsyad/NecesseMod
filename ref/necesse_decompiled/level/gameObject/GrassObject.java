package necesse.level.gameObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.AreaFinder;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.light.GameLight;

public class GrassObject extends GameObject {
   public int weaveTime;
   public float weaveAmount;
   public float weaveHeight;
   public float randomXOffset;
   public float randomYOffset;
   protected String textureName;
   protected GameTexture[] textures;
   protected GameTexture[] underGroundTextures;
   protected int undergroundPixels;
   private final GameRandom drawRandom;
   protected int density;
   protected int extraWeaveSpace;

   public GrassObject(String var1, int var2, int var3) {
      super(new Rectangle(0, 0));
      this.weaveTime = 2000;
      this.weaveAmount = 0.15F;
      this.weaveHeight = 1.0F;
      this.randomXOffset = 8.0F;
      this.randomYOffset = 8.0F;
      this.extraWeaveSpace = 0;
      this.textureName = var1;
      this.undergroundPixels = var2;
      this.density = var3;
      this.drawDamage = false;
      this.isGrass = true;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.attackThrough = true;
      this.drawRandom = new GameRandom();
      this.canPlaceOnProtectedLevels = true;
   }

   public GrassObject(String var1, int var2) {
      this(var1, 0, var2);
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName);
      int var2 = var1.getWidth() / 32;
      int var3 = this.extraWeaveSpace / 2;
      this.textures = new GameTexture[var2];
      this.underGroundTextures = new GameTexture[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         this.textures[var4] = new GameTexture("objects/" + this.textureName + " weave" + var4, 32 + this.extraWeaveSpace, var1.getHeight() - this.undergroundPixels);
         this.textures[var4].copy(var1, var3, 0, var4 * 32, 0, 32, var1.getHeight() - this.undergroundPixels);
         this.textures[var4].makeFinal();
         if (this.undergroundPixels > 0) {
            this.underGroundTextures[var4] = new GameTexture("objects/" + this.textureName + " underground weave" + var4, 32 + this.extraWeaveSpace, this.undergroundPixels);
            this.underGroundTextures[var4].copy(var1, var3, 0, var4 * 32, var1.getHeight() - this.undergroundPixels, 32, this.undergroundPixels);
            this.underGroundTextures[var4].makeFinal();
         }
      }

      var1.makeFinal();
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      this.playDamageSound(var1, var2, var3, true);
   }

   protected void addSimulateSpread(Level var1, int var2, int var3, int var4, int var5, int var6, double var7, long var9, SimulatePriorityList var11, boolean var12) {
      double var13 = Math.max(1.0, GameMath.getAverageRunsForSuccess(var7, GameRandom.globalRandom.nextDouble()));
      long var15 = (long)((double)var9 - var13);
      if (var15 > 0L) {
         var11.add(var2, var3, var15, () -> {
            boolean var13 = this.tickSpread(var1, var2, var3, var4, var5, var6, (var5x) -> {
               if (var12) {
                  var1.sendObjectUpdatePacket(var5x.x, var5x.y);
               }

               var1.getObject(var5x.x, var5x.y).addSimulateLogic(var1, var5x.x, var5x.y, var15, var11, var12);
            });
            if (var13) {
               this.addSimulateSpread(var1, var2, var3, var4, var5, var6, var7, var15, var11, var12);
            }

         });
      }

   }

   protected boolean tickSpread(Level var1, int var2, int var3, int var4, int var5, int var6) {
      return this.tickSpread(var1, var2, var3, var4, var5, var6, (var2x) -> {
         return var1.getObjectID(var2x.x, var2x.y) == this.getID();
      }, (var1x) -> {
         var1.sendObjectUpdatePacket(var1x.x, var1x.y);
      });
   }

   protected boolean tickSpread(Level var1, int var2, int var3, int var4, int var5, int var6, Consumer<Point> var7) {
      return this.tickSpread(var1, var2, var3, var4, var5, var6, (var2x) -> {
         return var1.getObjectID(var2x.x, var2x.y) == this.getID();
      }, var7);
   }

   protected boolean tickSpread(Level var1, int var2, int var3, int var4, int var5, int var6, Predicate<Point> var7) {
      return this.tickSpread(var1, var2, var3, var4, var5, var6, var7, (var1x) -> {
         var1.sendObjectUpdatePacket(var1x.x, var1x.y);
      });
   }

   protected boolean tickSpread(Level var1, int var2, int var3, int var4, final int var5, int var6, final Predicate<Point> var7, Consumer<Point> var8) {
      final AtomicInteger var9 = new AtomicInteger(0);
      AreaFinder var10 = new AreaFinder(var2, var3, var4, true) {
         public boolean checkPoint(int var1, int var2) {
            if (var7.test(new Point(var1, var2))) {
               return var9.incrementAndGet() > var5;
            } else {
               return false;
            }
         }
      };
      var10.runFinder();
      if (!var10.hasFound()) {
         ArrayList var11 = new ArrayList();

         for(int var12 = var2 - var6; var12 <= var2 + var6; ++var12) {
            for(int var13 = var3 - var6; var13 <= var3 + var6; ++var13) {
               if ((var12 != var2 || var13 != var3) && this.canPlace(var1, var12, var13, 0) == null) {
                  var11.add(new Point(var12, var13));
               }
            }
         }

         Point var14 = (Point)GameRandom.globalRandom.getOneOf((List)var11);
         if (var14 != null) {
            this.placeObject(var1, var14.x, var14.y, 0);
            if (var8 != null) {
               var8.accept(var14);
            }

            return true;
         }
      }

      return false;
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      super.tick(var1, var2, var3, var4);
      if (Settings.wavyGrass && var1.getFlyingHeight() < 10 && (var1.dx != 0.0F || var1.dy != 0.0F)) {
         var2.makeGrassWeave(var3, var4, this.weaveTime, false);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "grassSetup", (Runnable)(() -> {
         int var7x = var7.getTileDrawX(var4);
         int var8 = var7.getTileDrawY(var5);
         GameLight var9 = var3.getLightLevel(var4, var5);
         if (Settings.denseGrass) {
            this.addGrassDrawable(var1, var2, var3, var4, var5, var7x, var8, var9, 10, -7, 0);
            this.addGrassDrawable(var1, var2, var3, var4, var5, var7x, var8, var9, 26, -7, 1);
         } else {
            this.addGrassDrawable(var1, var2, var3, var4, var5, var7x, var8, var9, 14, -7, 0);
         }

      }));
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      GameLight var10 = var1.getLightLevel(var2, var3);
      LinkedList var11 = new LinkedList();
      OrderableDrawables var12 = new OrderableDrawables(new TreeMap());
      if (Settings.denseGrass) {
         this.addGrassDrawable(var11, var12, var1, var2, var3, var8, var9, var10, 10, -7, 0, 0.5F);
         this.addGrassDrawable(var11, var12, var1, var2, var3, var8, var9, var10, 26, -7, 1, 0.5F);
      } else {
         this.addGrassDrawable(var11, var12, var1, var2, var3, var8, var9, var10, 14, -7, 0, 0.5F);
      }

      var12.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
      var11.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
   }

   public void addGrassDrawable(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, int var6, int var7, GameLight var8, int var9, int var10, int var11) {
      this.addGrassDrawable(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, 0, this.textures.length - 1);
   }

   public void addGrassDrawable(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, int var6, int var7, GameLight var8, int var9, int var10, int var11, float var12) {
      this.addGrassDrawable(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, 0, this.textures.length - 1, var12);
   }

   public void addGrassDrawable(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, int var6, int var7, GameLight var8, int var9, int var10, int var11, int var12, int var13) {
      this.addGrassDrawable(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, 1.0F);
   }

   public void addGrassDrawable(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, int var6, int var7, GameLight var8, int var9, int var10, int var11, int var12, int var13, float var14) {
      double var15;
      double var17;
      boolean var19;
      int var20;
      synchronized(this.drawRandom) {
         var15 = (double)(this.drawRandom.seeded(this.getTileSeed(var4, var5, var11)).nextFloat() * 2.0F - 1.0F);
         var17 = (double)(this.drawRandom.nextFloat() * 2.0F - 1.0F);
         var19 = this.drawRandom.nextBoolean();
         var20 = this.drawRandom.getIntBetween(var12, var13);
      }

      GameTexture var21 = this.textures[var20];
      final WaveShader.WaveState var22 = GameResources.waveShader.setupGrassWave(var3, var4, var5, (long)this.weaveTime, this.weaveAmount, this.weaveHeight, this.drawRandom, this.getTileSeed(var4, var5, var11));
      int var23 = var9 + (int)(var15 * (double)this.randomYOffset);
      TextureDrawOptionsEnd var24 = var21.initDraw().alpha(var14).mirror(var19, false);
      if (var8 != null) {
         var24 = var24.light(var8);
      }

      final TextureDrawOptionsEnd var25 = var24.pos(var6 + (int)(var17 * (double)this.randomXOffset) - this.extraWeaveSpace / 2, var7 - var21.getHeight() + var23);
      final int var26 = var23 + var10;
      GameTexture var27 = this.underGroundTextures[var20];
      if (var27 != null && this.shouldDrawUnderground(var3, var4, var5)) {
         TextureDrawOptionsEnd var28 = var27.initDraw().alpha(var14).mirror(var19, false);
         if (var8 != null) {
            var28.light(var8);
         }

         TextureDrawOptionsEnd var29 = var28.pos(var6 + (int)(var17 * (double)this.randomXOffset) - this.extraWeaveSpace / 2, var7 + var23);
         var2.add((var1x) -> {
            var29.draw();
         });
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var26;
         }

         public void draw(TickManager var1) {
            Performance.record(var1, "grassDraw", (Runnable)(() -> {
               if (var22 != null) {
                  var22.start();
               }

               var25.draw();
               if (var22 != null) {
                  var22.end();
               }

            }));
         }
      });
   }

   protected boolean shouldDrawUnderground(Level var1, int var2, int var3) {
      return var1.getTile(var2, var3).isLiquid && var1.getTile(var2, var3 + 1).isLiquid;
   }

   public boolean shouldSnapControllerMining(Level var1, int var2, int var3) {
      return !this.attackThrough;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         return !this.densityCheck(var1, var2, var3) ? "adjspace" : null;
      }
   }

   protected boolean densityCheck(Level var1, int var2, int var3) {
      if (this.density > 0) {
         GameObject[] var4 = var1.getAdjacentObjects(var2, var3);
         int var5 = 0;
         GameObject[] var6 = var4;
         int var7 = var4.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            GameObject var9 = var6[var8];
            if (var9.isGrass) {
               ++var5;
            }

            if (var5 > this.density) {
               return false;
            }
         }
      }

      return true;
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }
}
