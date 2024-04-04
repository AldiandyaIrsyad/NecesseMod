package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CactusObject extends GameObject implements ForestryJobObject {
   public int weaveTime = 250;
   public float weaveAmount = 0.02F;
   public float weaveHeight = 1.0F;
   public float waveHeightOffset = 0.1F;
   protected GameTexture[][] textures;
   protected final GameRandom drawRandom;
   protected String saplingStringID;

   public CactusObject(String var1) {
      super(new Rectangle(8, 16, 16, 8));
      this.saplingStringID = var1;
      this.mapColor = new Color(16, 147, 30);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.toolType = ToolType.AXE;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/cactus");
      this.textures = new GameTexture[var1.getWidth() / 64][var1.getHeight() / 64];

      for(int var2 = 0; var2 < this.textures.length; ++var2) {
         for(int var3 = 0; var3 < this.textures[var2].length; ++var3) {
            this.textures[var2][var3] = new GameTexture(var1, 64 * var2, 64 * var3, 64, 64);
         }
      }

   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{LootItem.between("cactussapling", 3, 5)});
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = 0;
      if (this.textures.length > 1 && var3.getTileID(var4, var5) == TileRegistry.snowID) {
         var12 = 1;
      }

      final WaveShader.WaveState var13 = GameResources.waveShader.setupGrassWave(var3, var4, var5, (long)this.weaveTime, this.weaveAmount, this.weaveHeight, 2, this.waveHeightOffset, this.drawRandom, this.getTileSeed(var4, var5, 0));
      int var14 = 0;
      boolean var15;
      synchronized(this.drawRandom) {
         this.drawRandom.setSeed(this.getTileSeed(var4, var5));
         if (this.textures.length > 1) {
            var14 = this.drawRandom.nextInt(this.textures[var12].length);
         }

         var15 = this.drawRandom.nextBoolean();
      }

      final TextureDrawOptionsEnd var16 = this.textures[var12][var14].initDraw().light(var9).mirror(var15, false).pos(var10 - 16, var11 - 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            if (var13 != null) {
               var13.start();
            }

            var16.draw();
            if (var13 != null) {
               var13.end();
            }

         }
      });
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -24, 32, 24));
      return var4;
   }

   public boolean onDamaged(Level var1, int var2, int var3, int var4, ServerClient var5, boolean var6, int var7, int var8) {
      boolean var9 = super.onDamaged(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var6) {
         var1.makeGrassWeave(var2, var3, this.weaveTime, true);
      }

      return var9;
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.getTileID("sandtile");
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         return !var1.getTile(var2, var3).getStringID().equals("sandtile") ? "notsand" : null;
      }
   }

   public String getSaplingStringID() {
      return this.saplingStringID;
   }
}
