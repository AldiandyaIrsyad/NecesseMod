package necesse.level.gameObject;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SeedObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.SeedObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class SeedObject extends GameObject implements PollinateObject {
   protected String textureName;
   public String productID;
   public String seedID;
   public GameTexture texture;
   public GameTexture fertilizedTexture;
   public GameTexture itemTexture;
   public int drawOffset;
   public int minGrowTime;
   public int maxGrowTime;
   public int maxProductAmount;
   public int[] stageIDs = new int[0];
   public int thisStage;
   public boolean canBePlacedAsFlower;
   public int flowerID;
   public int itemSpoilDurationMinutes;
   private final GameRandom drawRandom;

   public SeedObject(String var1, int var2, int var3, String var4, int var5, String var6, int var7, int var8, boolean var9, int var10, Color var11, Item.Rarity var12, int var13) {
      this.textureName = var1;
      this.drawOffset = var2;
      this.thisStage = var3;
      this.productID = var4;
      this.maxProductAmount = var5;
      this.seedID = var6;
      this.minGrowTime = var7;
      this.maxGrowTime = var8;
      this.mapColor = var11;
      this.canBePlacedAsFlower = var9;
      this.flowerID = var10;
      this.rarity = var12;
      this.itemSpoilDurationMinutes = var13;
      this.displayMapTooltip = true;
      this.setItemCategory(new String[]{"objects", "seeds"});
      this.isSeed = true;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.attackThrough = true;
      this.drawRandom = new GameRandom();
      this.replaceCategories.add("seed");
      this.canReplaceCategories.add("seed");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName, true);
      this.fertilizedTexture = GameTexture.fromFile("objects/fertilizer");
      this.itemTexture = new GameTexture(var1, 0, 1, 32);
      this.itemTexture.makeFinal();
      this.texture = new GameTexture("objects/" + this.textureName + " stage" + this.thisStage, 64, var1.getHeight());
      this.texture.copy(var1, 16, 0, 32 + this.thisStage * 32, 0, 32, var1.getHeight());
      this.texture.resetTexture();
      this.texture.makeFinal();
      if (this.thisStage == 0) {
         var1.makeFinal();
      }

   }

   public GameMessage getNewLocalization() {
      return this.thisStage == 0 ? super.getNewLocalization() : ObjectRegistry.getObject(ObjectRegistry.getObjectID(this.seedID)).getNewLocalization();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = new LootTable(new LootItemInterface[]{this.isLastStage() ? new LootItem(this.seedID, 2) : (new LootItem(this.seedID)).preventLootMultiplier()});
      if (this.isLastStage() && this.maxProductAmount > 0) {
         var4.items.add(LootItem.between(this.productID, 1, this.maxProductAmount));
      }

      return var4;
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      super.tick(var1, var2, var3, var4);
      if (Settings.wavyGrass && var1.getFlyingHeight() < 10 && (var1.dx != 0.0F || var1.dy != 0.0F)) {
         var2.makeGrassWeave(var3, var4, 1000, false);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9;
      boolean var10;
      boolean var11;
      synchronized(this.drawRandom) {
         var9 = (this.drawRandom.seeded(this.getTileSeed(var4, var5)).nextFloat() - 0.5F) * 1.5F;
         var10 = this.drawRandom.nextBoolean();
         var11 = this.drawRandom.nextBoolean();
      }

      GameLight var12 = var3.getLightLevel(var4, var5);
      int var13 = var7.getTileDrawX(var4);
      int var14 = var7.getTileDrawY(var5);
      final WaveShader.WaveState var15 = GameResources.waveShader.setupGrassWave(var3, var4, var5, 1000L, 0.07F, 1.0F, this.drawRandom, this.getTileSeed(var4, var5));
      int var16 = 12 + (int)(var9 * 4.0F) + this.drawOffset;
      final TextureDrawOptionsEnd var17 = this.texture.initDraw().light(var12).mirror(var10, false).pos(var13 - 16 + 4, var14 - this.texture.getHeight() + var16);
      int var18 = 24 + (int)(var9 * 4.0F) + this.drawOffset;
      final TextureDrawOptionsEnd var19 = this.texture.initDraw().light(var12).mirror(var11, false).pos(var13 - 16 - 4, var14 - this.texture.getHeight() + var18);
      ObjectEntity var20 = var3.entityManager.getObjectEntity(var4, var5);
      if (var20 instanceof SeedObjectEntity) {
         TextureDrawOptionsEnd var21 = this.fertilizedTexture.initDraw().pos(var13, var14);
         if (((SeedObjectEntity)var20).isFertilized()) {
            var2.add((var1x) -> {
               var21.draw();
            });
         }
      }

      final int var24 = var16 - 5;
      final int var22 = var18 - 5;
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var24;
         }

         public void draw(TickManager var1) {
            if (var15 != null) {
               var15.start();
            }

            var17.draw();
            if (var15 != null) {
               var15.end();
            }

         }
      });
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var22;
         }

         public void draw(TickManager var1) {
            if (var15 != null) {
               var15.start();
            }

            var19.draw();
            if (var15 != null) {
               var15.end();
            }

         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      float var8;
      boolean var9;
      boolean var10;
      synchronized(this.drawRandom) {
         var8 = (this.drawRandom.seeded(this.getTileSeed(var2, var3)).nextFloat() - 0.5F) * 1.5F;
         var9 = this.drawRandom.nextBoolean();
         var10 = this.drawRandom.nextBoolean();
      }

      int var11 = var7.getTileDrawX(var2);
      int var12 = var7.getTileDrawY(var3);
      int var13 = 12 + (int)(var8 * 4.0F) + this.drawOffset;
      this.texture.initDraw().alpha(var5).mirror(var9, false).draw(var11 - 16 + 4, var12 - this.texture.getHeight() + var13);
      int var14 = 24 + (int)(var8 * 4.0F) + this.drawOffset;
      this.texture.initDraw().alpha(var5).mirror(var10, false).draw(var11 - 16 - 4, var12 - this.texture.getHeight() + var14);
   }

   public Item generateNewObjectItem() {
      return (new SeedObjectItem(this, () -> {
         return this.itemTexture;
      })).spoilDuration(this.itemSpoilDurationMinutes);
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.getTileID("farmland");
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         return !var1.getTile(var2, var3).getStringID().equals("farmland") ? "notfarmland" : null;
      }
   }

   public boolean canReplace(Level var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < this.stageIDs.length - 1; ++var5) {
         if (var1.getObjectID(var2, var3) == this.stageIDs[var5]) {
            return false;
         }
      }

      return super.canReplace(var1, var2, var3, var4);
   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      return this.isLastStage() ? Collections.singletonList(new HarvestCropLevelJob(var2, var3)) : super.getLevelJobs(var1, var2, var3);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return this.isLastStage() ? null : new SeedObjectEntity(var1, var2, var3, this.minGrowTime, this.maxGrowTime);
   }

   public PollinateObjectHandler getPollinateHandler(Level var1, int var2, int var3) {
      final SeedObjectEntity var4 = (SeedObjectEntity)this.getCurrentObjectEntity(var1, var2, var3, SeedObjectEntity.class);
      return var4 != null ? new PollinateObjectHandler(var2, var3, var4.fertilizeReservable) {
         public boolean canPollinate() {
            return !var4.isFertilized();
         }

         public void pollinate() {
            var4.fertilize();
         }

         public boolean isValid() {
            return !var4.removed();
         }
      } : null;
   }

   public boolean isLastStage() {
      return this.thisStage >= this.stageIDs.length - 1;
   }

   public int getFirstStageID() {
      return this.stageIDs.length > 0 ? this.stageIDs[0] : -1;
   }

   public int getNextStageID() {
      return this.thisStage < this.stageIDs.length - 1 ? this.stageIDs[this.thisStage + 1] : -1;
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4, Attacker var5) {
      var1.getServer().network.sendToClientsWithTile(new PacketHitObject(var1, var2, var3, this, var4), var1, var2, var3);
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      var1.makeGrassWeave(var2, var3, 1000, false);
   }

   public void onMouseHover(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, boolean var6) {
      String var7 = null;
      int var8;
      if (this.productID != null && this.maxProductAmount > 0) {
         var8 = ItemRegistry.getItemID(this.productID);
         if (var8 != -1) {
            var7 = ItemRegistry.getDisplayName(var8);
         }
      }

      if (var7 == null && this.seedID != null) {
         var8 = ItemRegistry.getItemID(this.seedID);
         if (var8 != -1) {
            var7 = ItemRegistry.getDisplayName(var8);
         }
      }

      if (var7 != null) {
         Screen.addTooltip(new StringTooltips(var7), TooltipLocation.INTERACT_FOCUS);
      }

      super.onMouseHover(var1, var2, var3, var4, var5, var6);
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   public static int[] registerSeedObjects(String var0, String var1, String var2, int var3, int var4, int var5, float var6, float var7, Color var8, float var9) {
      return registerSeedObjects(var0, var1, var2, var3, var4, var5, var6, var7, var8, Item.Rarity.NORMAL, 0, var9);
   }

   public static int[] registerSeedObjects(String var0, String var1, String var2, int var3, int var4, int var5, float var6, float var7, Color var8, Item.Rarity var9, int var10, float var11) {
      return registerSeedObjects(var0, var1, var2, var3, var4, var5, var6, var7, false, 0, var8, var9, var10, var11);
   }

   public static int[] registerSeedObjects(String var0, String var1, String var2, int var3, int var4, int var5, float var6, float var7, boolean var8, int var9, Color var10, Item.Rarity var11, int var12, float var13) {
      int var14 = (int)(var6 * 1000.0F) / var5;
      int var15 = (int)(var7 * 1000.0F) / var5;
      int[] var16 = new int[var5];
      SeedObject[] var17 = new SeedObject[var5];

      int var20;
      for(int var18 = var5 - 1; var18 >= 0; --var18) {
         SeedObject var19 = new SeedObject(var1, var4, var18, var2, var3, var0, var14, var15, var8, var9, var10, var11, var12);
         var20 = ObjectRegistry.registerObject(var0 + (var18 == 0 ? "" : var18), var19, var13, var18 <= 0);
         var17[var18] = var19;
         var16[var18] = var20;
      }

      SeedObject[] var22 = var17;
      int var23 = var17.length;

      for(var20 = 0; var20 < var23; ++var20) {
         SeedObject var21 = var22[var20];
         var21.stageIDs = var16;
      }

      return var16;
   }
}
