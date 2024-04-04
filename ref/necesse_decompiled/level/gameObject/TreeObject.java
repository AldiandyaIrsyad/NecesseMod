package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TreeObject extends GameObject implements ForestryJobObject {
   public int weaveTime = 250;
   public float weaveAmount = 0.02F;
   public float weaveHeight = 1.0F;
   public float waveHeightOffset = 0.1F;
   public int leavesCenterWidth;
   public int leavesMinHeight;
   public int leavesMaxHeight;
   public String leavesTextureName;
   public Supplier<GameTextureSection> leavesTexture;
   public String logStringID;
   public String saplingStringID;
   protected String textureName;
   protected GameTexture[][] textures;
   protected final GameRandom drawRandom;

   public TreeObject(String var1, String var2, String var3, Color var4, int var5, int var6, int var7, String var8) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.logStringID = var2;
      this.saplingStringID = var3;
      this.mapColor = var4;
      this.leavesCenterWidth = var5;
      this.leavesMinHeight = var6;
      this.leavesMaxHeight = var7;
      this.leavesTextureName = var8;
      this.hoverHitbox = new Rectangle(0, -16, 32, 48);
      this.displayMapTooltip = true;
      this.isTree = true;
      this.drawDamage = false;
      this.toolType = ToolType.AXE;
      this.drawRandom = new GameRandom();
      this.replaceCategories.add("tree");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName, true);
      this.textures = new GameTexture[var1.getWidth() / 128][var1.getHeight() / 128];

      int var3;
      for(int var2 = 0; var2 < this.textures.length; ++var2) {
         for(var3 = 0; var3 < this.textures[var2].length; ++var3) {
            GameTexture var4 = new GameTexture(var1, 128 * var2, 128 * var3, 128, 128);
            var4.makeFinal();
            this.textures[var2][var3] = var4;
         }
      }

      var1.makeFinal();
      if (this.leavesTextureName != null) {
         try {
            GameTexture var7 = GameTexture.fromFileRaw("particles/" + this.leavesTextureName);
            var3 = var7.getHeight();
            int var8 = var7.getWidth() / var3;
            GameTextureSection var5 = GameResources.particlesTextureGenerator.addTexture(var7);
            this.leavesTexture = () -> {
               return var5.sprite(GameRandom.globalRandom.nextInt(var8), 0, var3);
            };
         } catch (FileNotFoundException var6) {
            this.leavesTexture = null;
         }
      }

   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = new LootTable();
      if (this.saplingStringID != null) {
         var4.items.add(LootItem.between(this.saplingStringID, 1, 2).splitItems(5));
      }

      if (this.logStringID != null) {
         var4.items.add(LootItem.between(this.logStringID, 4, 5).splitItems(5));
      }

      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "treeSetup", (Runnable)(() -> {
         GameLight var7x = var3.getLightLevel(var4, var5);
         int var8x = var7.getTileDrawX(var4);
         int var9 = var7.getTileDrawY(var5);
         float var10 = 1.0F;
         if (var8 != null && !Settings.hideUI) {
            Rectangle var11 = new Rectangle(var4 * 32 - 48, var5 * 32 - 100, 128, 100);
            if (var8.getCollision().intersects(var11)) {
               var10 = 0.5F;
            } else if (var11.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
               var10 = 0.5F;
            }
         }

         byte var18 = 0;
         if (this.textures.length > 1 && var3.getTileID(var4, var5) == TileRegistry.snowID) {
            var18 = 1;
         }

         final WaveShader.WaveState var12 = GameResources.waveShader.setupGrassWave(var3, var4, var5, (long)this.weaveTime, this.weaveAmount, this.weaveHeight, 2, this.waveHeightOffset, this.drawRandom, this.getTileSeed(var4, var5, 0));
         int var13 = 0;
         boolean var14;
         synchronized(this.drawRandom) {
            this.drawRandom.setSeed(this.getTileSeed(var4, var5));
            if (this.textures.length > 1) {
               var13 = this.drawRandom.nextInt(this.textures[var18].length);
            }

            var14 = this.drawRandom.nextBoolean();
         }

         final TextureDrawOptionsEnd var15 = this.textures[var18][var13].initDraw().alpha(var10).light(var7x).mirror(var14, false).pos(var8x - 48, var9 - 96);
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 16;
            }

            public void draw(TickManager var1) {
               Performance.record(var1, "treeDraw", (Runnable)(() -> {
                  if (var12 != null) {
                     var12.start();
                  }

                  var15.draw();
                  if (var12 != null) {
                     var12.end();
                  }

               }));
            }
         });
      }));
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      super.onDestroyed(var1, var2, var3, var4, var5);
      if (!var1.isServer()) {
         int var6 = GameRandom.globalRandom.getIntBetween(15, 20);
         this.spawnLeafParticles(var1, var2, var3, 20, var6);
      }

   }

   public boolean onDamaged(Level var1, int var2, int var3, int var4, ServerClient var5, boolean var6, int var7, int var8) {
      boolean var9 = super.onDamaged(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var6) {
         var1.makeGrassWeave(var2, var3, this.weaveTime, true);
         if (!var1.isServer()) {
            int var10 = GameRandom.globalRandom.getIntBetween(0, 2);
            this.spawnLeafParticles(var1, var2, var3, this.leavesMinHeight, var10);
         }
      }

      return var9;
   }

   public void spawnLeafParticles(Level var1, int var2, int var3, int var4, int var5) {
      if (this.leavesTexture != null) {
         spawnLeafParticles(var1, var2, var3, this.leavesCenterWidth, var4, this.leavesMaxHeight, var5, this.leavesTexture);
      }
   }

   public static void spawnLeafParticles(Level var0, int var1, int var2, int var3, int var4, int var5, int var6, Supplier<GameTextureSection> var7) {
      boolean var8 = GameRandom.globalRandom.nextBoolean();

      for(int var9 = 0; var9 < var6; ++var9) {
         float var10 = (float)(var1 * 32 + 16) + (var8 ? GameRandom.globalRandom.getFloatBetween(-1.0F, 0.0F) : GameRandom.globalRandom.getFloatBetween(0.0F, 1.0F)) * (float)var3;
         var8 = !var8;
         float var11 = (float)(var2 * 32 + 32);
         float var12 = GameRandom.globalRandom.getFloatBetween((float)var4, (float)var5);
         float var13 = GameRandom.globalRandom.getFloatBetween(0.0F, 60.0F);
         float var14 = GameRandom.globalRandom.getFloatBetween(-10.0F, -5.0F);
         float var15 = GameRandom.globalRandom.getFloatBetween(8.0F, 20.0F);
         boolean var16 = GameRandom.globalRandom.nextBoolean();
         float var17 = GameRandom.globalRandom.getFloatBetween(-100.0F, 100.0F);
         float var18 = GameRandom.globalRandom.floatGaussian() * 5.0F;
         float var19 = GameRandom.globalRandom.floatGaussian() * 2.0F;
         int var20 = GameRandom.globalRandom.getIntBetween(3000, 8000);
         int var21 = GameRandom.globalRandom.getIntBetween(1000, 2000);
         int var22 = var20 + var21;
         ParticleOption.HeightMover var23 = new ParticleOption.HeightMover(var12, var13, var15, 2.0F, var14, 0.0F);
         AtomicReference var24 = new AtomicReference(0.0F);
         var0.entityManager.addParticle(var10, var11, Particle.GType.COSMETIC).sprite((GameTextureSection)var7.get()).fadesAlphaTime(0, var21).sizeFadesInAndOut(15, 20, 100, 0).height((ParticleOption.HeightGetter)var23).onMoveTick((var3x, var4x, var5x, var6x) -> {
            if (var23.currentHeight > var14) {
               var24.set((Float)var24.get() + var3x);
            }

         }).modify((var2x, var3x, var4x, var5x) -> {
            float var6 = GameMath.sin((Float)var24.get() / 5.0F) * var17;
            var2x.rotate(var6, 10, -4);
         }).moves((var4x, var5x, var6x, var7x, var8x) -> {
            if (var23.currentHeight > var14) {
               float var9 = var5x / 250.0F;
               var4x.x += var18 * var9;
               var4x.y += var19 * var9;
            }

         }).modify((var1x, var2x, var3x, var4x) -> {
            var1x.mirror(var16, false);
         }).lifeTime(var22);
      }

   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         GameObject[] var6 = var1.getAdjacentObjects(var2, var3);
         GameObject[] var7 = var6;
         int var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            GameObject var10 = var7[var9];
            if (var10.isTree) {
               return "treenear";
            }
         }

         return null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (super.isValid(var1, var2, var3)) {
         GameObject[] var4 = var1.getAdjacentObjects(var2, var3);
         GameObject[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            GameObject var8 = var5[var7];
            if (var8.isTree) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public String getSaplingStringID() {
      return this.saplingStringID;
   }
}
