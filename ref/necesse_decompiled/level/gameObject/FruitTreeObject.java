package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
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
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class FruitTreeObject extends GameObject implements ForestryJobObject {
   public int weaveTime = 250;
   public float weaveAmount = 0.02F;
   public float weaveHeight = 1.0F;
   public float waveHeightOffset = 0.1F;
   public int leavesCenterWidth;
   public int leavesMinHeight;
   public int leavesMaxHeight;
   public String leavesTextureName;
   public Supplier<GameTextureSection> leavesTexture;
   public float minGrowTimeSeconds;
   public float maxGrowTimeSeconds;
   public float fruitPerStage;
   public int maxStage;
   public String logStringID;
   public String seedStringID;
   public String fruitStringID;
   protected String textureName;
   protected GameTexture[][] textures;
   protected final GameRandom drawRandom;

   public FruitTreeObject(String var1, String var2, String var3, float var4, float var5, String var6, float var7, int var8, Color var9, int var10, int var11, int var12, String var13) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.logStringID = var2;
      this.seedStringID = var3;
      this.minGrowTimeSeconds = var4;
      this.maxGrowTimeSeconds = var5;
      this.fruitStringID = var6;
      this.fruitPerStage = var7;
      this.maxStage = var8;
      this.mapColor = var9;
      this.leavesCenterWidth = var10;
      this.leavesMinHeight = var11;
      this.leavesMaxHeight = var12;
      this.leavesTextureName = var13;
      this.debrisColor = new Color(86, 69, 40);
      this.displayMapTooltip = true;
      this.isTree = true;
      this.drawDamage = false;
      this.toolType = ToolType.AXE;
      this.drawRandom = new GameRandom();
      this.replaceCategories.add("tree");
      this.replaceRotations = false;
   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      return this.getFruitStage(var1, var2, var3) > 0 ? Collections.singletonList(new HarvestFruitLevelJob(var2, var3)) : super.getLevelJobs(var1, var2, var3);
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName);
      this.textures = new GameTexture[var1.getWidth() / 128][var1.getHeight() / 128];

      int var3;
      for(int var2 = 0; var2 < this.textures.length; ++var2) {
         for(var3 = 0; var3 < this.textures[var2].length; ++var3) {
            this.textures[var2][var3] = new GameTexture(var1, 128 * var2, 128 * var3, 128, 128);
         }
      }

      try {
         GameTexture var7 = GameTexture.fromFileRaw("particles/" + this.leavesTextureName);
         var3 = var7.getHeight();
         int var4 = var7.getWidth() / var3;
         GameTextureSection var5 = GameResources.particlesTextureGenerator.addTexture(var7);
         this.leavesTexture = () -> {
            return var5.sprite(GameRandom.globalRandom.nextInt(var4), 0, var3);
         };
      } catch (FileNotFoundException var6) {
         this.leavesTexture = null;
      }

   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = new LootTable();
      if (this.seedStringID != null) {
         var4.items.add((new LootItem(this.seedStringID)).preventLootMultiplier());
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

         final WaveShader.WaveState var17 = GameResources.waveShader.setupGrassWave(var3, var4, var5, (long)this.weaveTime, this.weaveAmount, this.weaveHeight, 2, this.waveHeightOffset, this.drawRandom, this.getTileSeed(var4, var5, 0));
         byte var12 = 0;
         if (this.textures.length > 1 && var3.getTileID(var4, var5) == TileRegistry.snowID) {
            var12 = 1;
         }

         boolean var13;
         synchronized(this.drawRandom) {
            this.drawRandom.setSeed(this.getTileSeed(var4, var5));
            var13 = this.drawRandom.nextBoolean();
         }

         int var14 = Math.min(this.getFruitStage(var3, var4, var5), this.textures[var12].length - 1);
         final TextureDrawOptionsEnd var15 = this.textures[var12][var14].initDraw().alpha(var10).light(var7x).mirror(var13, false).pos(var8x - 48, var9 - 96);
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 16;
            }

            public void draw(TickManager var1) {
               Performance.record(var1, "treeDraw", (Runnable)(() -> {
                  if (var17 != null) {
                     var17.start();
                  }

                  var15.draw();
                  if (var17 != null) {
                     var17.end();
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
         TreeObject.spawnLeafParticles(var1, var2, var3, this.leavesCenterWidth, var4, this.leavesMaxHeight, var5, this.leavesTexture);
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

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return this.getFruitStage(var1, var2, var3) > 0 ? Localization.translate("controls", "harvesttip") : null;
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      this.harvest(var1, var2, var3, var4);
   }

   public void harvest(Level var1, int var2, int var3, PlayerMob var4) {
      FruitGrowerObjectEntity var5 = this.getFruitObjectEntity(var1, var2, var3);
      if (var5 != null) {
         var5.harvest(var4);
      }

      if (!var1.isServer() && !var1.isGrassWeaving(var2, var3)) {
         Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
         var1.makeGrassWeave(var2, var3, this.weaveTime, false);
      }

   }

   public static Point getItemDropPos(int var0, int var1, Entity var2) {
      int var3 = 0;
      int var4 = 0;
      Point2D.Float var5 = var2 == null ? new Point2D.Float(0.0F, 1.0F) : GameMath.normalize((float)(var2.getX() - var0 * 32 - 16), (float)(var2.getY() - var1 * 32 - 16));
      if (Math.abs(var5.x) - Math.abs(var5.y) <= 0.0F) {
         var4 = var5.y < 0.0F ? -1 : 1;
      } else {
         var3 = var5.x < 0.0F ? -1 : 1;
      }

      return new Point(var0 * 32 + 16 + var3 * 32, var1 * 32 + 16 + var4 * 32);
   }

   public FruitGrowerObjectEntity getFruitObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof FruitGrowerObjectEntity ? (FruitGrowerObjectEntity)var4 : null;
   }

   public int getFruitStage(Level var1, int var2, int var3) {
      FruitGrowerObjectEntity var4 = this.getFruitObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getStage() : 0;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new FruitGrowerObjectEntity(var1, var2, var3, (int)(this.minGrowTimeSeconds * 1000.0F), (int)(this.maxGrowTimeSeconds * 1000.0F), this.maxStage, this.fruitStringID, this.fruitPerStage);
   }

   public String getSaplingStringID() {
      return this.seedStringID;
   }
}
