package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class FruitBushObject extends GameObject {
   public int weaveTime;
   public float weaveAmount;
   public float weaveHeight;
   public float waveHeightOffset;
   public float minGrowTimeSeconds;
   public float maxGrowTimeSeconds;
   public float fruitPerStage;
   public int maxStage;
   public String seedStringID;
   public String fruitStringID;
   protected String textureName;
   protected GameTexture[][] textures;
   protected final GameRandom drawRandom;

   public FruitBushObject(String var1, String var2, float var3, float var4, String var5, float var6, int var7, Color var8) {
      this.weaveTime = 250;
      this.weaveAmount = 0.04F;
      this.weaveHeight = 1.0F;
      this.waveHeightOffset = 0.0F;
      this.textureName = var1;
      this.seedStringID = var2;
      this.minGrowTimeSeconds = var3;
      this.maxGrowTimeSeconds = var4;
      this.fruitStringID = var5;
      this.fruitPerStage = var6;
      this.maxStage = var7;
      this.mapColor = var8;
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.drawRandom = new GameRandom();
      this.attackThrough = true;
      this.replaceCategories.add("bush");
      this.replaceRotations = false;
   }

   public FruitBushObject(String var1, String var2, float var3, float var4, String var5, float var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, new Color(86, 69, 40));
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      super.tick(var1, var2, var3, var4);
      if (Settings.wavyGrass && var1.getFlyingHeight() < 10 && (var1.dx != 0.0F || var1.dy != 0.0F)) {
         var2.makeGrassWeave(var3, var4, this.weaveTime, false);
      }

   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      return this.getFruitStage(var1, var2, var3) > 0 ? Collections.singletonList(new HarvestFruitLevelJob(var2, var3)) : super.getLevelJobs(var1, var2, var3);
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName);
      this.textures = new GameTexture[var1.getWidth() / 64][var1.getHeight() / 64];

      for(int var2 = 0; var2 < this.textures.length; ++var2) {
         for(int var3 = 0; var3 < this.textures[var2].length; ++var3) {
            this.textures[var2][var3] = new GameTexture(var1, 64 * var2, 64 * var3, 64, 64);
         }
      }

   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = new LootTable();
      if (this.seedStringID != null) {
         var4.items.add((new LootItem(this.seedStringID)).preventLootMultiplier());
      }

      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      float var12 = 1.0F;
      if (var8 != null && !Settings.hideUI) {
         Rectangle var13 = new Rectangle(var4 * 32 - 12, var5 * 32 - 24, 56, 32);
         if (var8.getCollision().intersects(var13)) {
            var12 = 0.5F;
         } else if (var13.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
            var12 = 0.5F;
         }
      }

      byte var24 = 0;
      if (this.textures.length > 1 && var3.getTileID(var4, var5) == TileRegistry.snowID) {
         var24 = 1;
      }

      final WaveShader.WaveState var14 = GameResources.waveShader.setupGrassWave(var3, var4, var5, 250L, this.weaveAmount, this.weaveHeight, 2, this.waveHeightOffset, this.drawRandom, this.getTileSeed(var4, var5, 0));
      double var15;
      boolean var17;
      synchronized(this.drawRandom) {
         var15 = (double)(this.drawRandom.seeded(this.getTileSeed(var4, var5, 0)).nextFloat() * 2.0F - 1.0F);
         this.drawRandom.setSeed(this.getTileSeed(var4, var5));
         var17 = this.drawRandom.nextBoolean();
      }

      int var18 = Math.min(this.getFruitStage(var3, var4, var5), this.textures[var24].length - 1);
      GameTexture var19 = this.textures[var24][var18];
      int var20 = 28 + (int)(var15 * 4.0);
      final TextureDrawOptionsEnd var21 = var19.initDraw().alpha(var12).light(var9).mirror(var17, false).pos(var10 - 32 + 16, var11 - var19.getHeight() + var20);
      final int var22 = var20 - 8;
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var22;
         }

         public void draw(TickManager var1) {
            if (var14 != null) {
               var14.start();
            }

            var21.draw();
            if (var14 != null) {
               var14.end();
            }

         }
      });
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4, Attacker var5) {
      var1.getServer().network.sendToClientsWithTile(new PacketHitObject(var1, var2, var3, this, var4), var1, var2, var3);
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      var1.makeGrassWeave(var2, var3, 1000, false);
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
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

   public void harvest(Level var1, int var2, int var3, Mob var4) {
      FruitGrowerObjectEntity var5 = this.getFruitObjectEntity(var1, var2, var3);
      if (var5 != null) {
         var5.harvest(var4);
      }

      if (!var1.isServer() && !var1.isGrassWeaving(var2, var3)) {
         Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
         var1.makeGrassWeave(var2, var3, this.weaveTime, false);
      }

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
}
