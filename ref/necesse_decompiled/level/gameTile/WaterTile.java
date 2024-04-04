package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.desert.DesertBiome;

public class WaterTile extends LiquidTile {
   public GameTextureSection deepTexture;
   public GameTextureSection shallowTexture;
   protected final GameRandom drawRandom = new GameRandom();

   public WaterTile() {
      super(new Color(50, 100, 200));
   }

   protected void loadTextures() {
      super.loadTextures();
      this.deepTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/waterdeep"));
      this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/watershallow"));
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      if (!var1.isFlying() && !var1.isWaterWalking() && var2.inLiquid(var1.getX(), var1.getY())) {
         var1.buffManager.removeBuff("onfire", false);
      }

   }

   public void tickValid(Level var1, int var2, int var3, boolean var4) {
      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            if (var5 != 0 || var6 != 0) {
               GameTile var7 = var1.getTile(var2 + var5, var3 + var6);
               if (var7.isLiquid && var7 instanceof LavaTile) {
                  if (!var4 && var1.isClient()) {
                     for(int var8 = 0; var8 < 10; ++var8) {
                        BombProjectile.spawnFuseParticle(var1, (float)(var2 * 32 + GameRandom.globalRandom.nextInt(33)), (float)(var3 * 32 + GameRandom.globalRandom.nextInt(33)), 1.0F);
                     }

                     var1.lightManager.refreshParticleLight(var2, var3, 0.0F, 0.3F);
                     Screen.playSound(GameResources.fizz, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)).volume(0.5F));
                  }

                  var1.setTile(var2, var3, TileRegistry.getTileID("rocktile"));
               }
            }
         }
      }

   }

   public Color getLiquidColor(Level var1, int var2, int var3) {
      if (var1.biome == BiomeRegistry.SWAMP) {
         return this.getLiquidColor(2);
      } else {
         return var1.biome instanceof DesertBiome && var1.isCave ? this.getLiquidColor(3) : this.getLiquidColor(0);
      }
   }

   public Color getMapColor(Level var1, int var2, int var3) {
      return this.getLiquidColor(var1, var2, var3);
   }

   protected void addLiquidTopDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      boolean var8;
      synchronized(this.drawRandom) {
         var8 = this.drawRandom.seeded(this.getTileSeed(var4, var5)).getChance(0.15F);
      }

      if (var8) {
         int var9 = var6.getTileDrawX(var4);
         int var10 = var6.getTileDrawY(var5);
         int var11 = this.getLiquidBobbing(var3, var4, var5);
         int var12;
         int var13;
         GameTextureSection var14;
         if (var3.liquidManager.getHeight(var4, var5) <= -10) {
            var12 = 0;
            var13 = var11;
            var14 = this.deepTexture;
         } else {
            var12 = var11;
            var13 = 0;
            var14 = this.shallowTexture;
         }

         int var15;
         synchronized(this.drawRandom) {
            var15 = this.drawRandom.seeded(this.getTileSeed(var4, var5)).nextInt(var14.getHeight() / 32);
         }

         var1.add(var14.sprite(0, var15, 32)).color(this.getLiquidColor(var3, var4, var5).brighter()).pos(var9 + var12, var10 + var13 - 2);
      }

   }
}
