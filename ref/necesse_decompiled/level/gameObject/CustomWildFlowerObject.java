package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
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
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CustomWildFlowerObject extends GameObject {
   protected String textureName;
   public String seedStringID;
   public String productStringID;
   public GameTexture texture;
   public int textureSpriteX;
   public int maxProductAmount;
   public String[] growTiles;
   protected final GameRandom drawRandom;

   public CustomWildFlowerObject(String var1, int var2, String var3, String var4, int var5, Color var6, String... var7) {
      super(new Rectangle(0, 0));
      this.textureName = var1;
      this.textureSpriteX = var2;
      this.seedStringID = var3;
      this.productStringID = var4;
      this.maxProductAmount = var5;
      this.growTiles = var7;
      this.mapColor = var6;
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.attackThrough = true;
      this.drawRandom = new GameRandom();
   }

   public CustomWildFlowerObject(String var1, int var2, String var3, String var4, int var5, Color var6) {
      this(var1, var2, var3, var4, var5, var6, "grasstile");
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName);
      this.texture = new GameTexture("objects/" + this.textureName + " weave", 64, var1.getHeight());
      this.texture.copy(var1, 16, 0, this.textureSpriteX * 32, 0, 32, var1.getHeight());
      this.texture.resetTexture();
      var1.makeFinal();
      this.texture.makeFinal();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{LootItem.between(this.productStringID, 1, this.maxProductAmount), LootItem.between(this.seedStringID, 1, 2)});
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      super.tick(var1, var2, var3, var4);
      if (Settings.wavyGrass && var1.getFlyingHeight() < 10 && (var1.dx != 0.0F || var1.dy != 0.0F)) {
         var2.makeGrassWeave(var3, var4, 1000, false);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      double var9;
      double var11;
      boolean var13;
      synchronized(this.drawRandom) {
         var9 = this.drawRandom.seeded(this.getTileSeed(var4, var5)).nextGaussian();
         var11 = this.drawRandom.nextGaussian();
         var13 = this.drawRandom.nextBoolean();
      }

      GameLight var14 = var3.getLightLevel(var4, var5);
      int var15 = var7.getTileDrawX(var4);
      int var16 = var7.getTileDrawY(var5);
      final WaveShader.WaveState var17 = GameResources.waveShader.setupGrassWave(var3, var4, var5, 1000L, 0.07F, 1.0F, this.drawRandom, this.getTileSeed(var4, var5));
      int var18 = 16 + (int)(var9 * 4.0);
      final TextureDrawOptionsEnd var19 = this.texture.initDraw().light(var14).mirror(var13, false).pos(var15 - 16 + (int)(var11 * 4.0), var16 - this.texture.getHeight() + var18);
      final int var20 = var18 - 6;
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var20;
         }

         public void draw(TickManager var1) {
            if (var17 != null) {
               var17.start();
            }

            var19.draw();
            if (var17 != null) {
               var17.end();
            }

         }
      });
   }

   public boolean isValid(Level var1, int var2, int var3) {
      int var4 = var1.getTileID(var2, var3);
      return Arrays.stream(this.growTiles).anyMatch((var1x) -> {
         return var4 == TileRegistry.getTileID(var1x);
      });
   }
}
