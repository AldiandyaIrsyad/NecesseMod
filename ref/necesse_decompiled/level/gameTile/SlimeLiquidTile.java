package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;

public class SlimeLiquidTile extends LiquidTile {
   public GameTextureSection deepTexture;
   public GameTextureSection shallowTexture;
   protected final GameRandom drawRandom;

   public SlimeLiquidTile() {
      super(new Color(50, 200, 50));
      this.lightLevel = 50;
      this.lightHue = 130.0F;
      this.lightSat = 0.6F;
      this.drawRandom = new GameRandom();
   }

   protected void loadTextures() {
      super.loadTextures();
      this.deepTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/waterdeep"));
      this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/watershallow"));
   }

   public double getPathCost(Level var1, int var2, int var3, Mob var4) {
      return !var4.isLavaImmune() ? 1000.0 : super.getPathCost(var1, var2, var3, var4);
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(200) && var1.getObjectID(var2, var3) == 0) {
         byte var4 = 12;
         Color var5 = this.getLiquidColor(var1, var2, var3).brighter();
         var1.entityManager.addParticle(ParticleOption.base((float)(var2 * 32 + GameRandom.globalRandom.nextInt(32 - var4)), (float)(var3 * 32 + GameRandom.globalRandom.nextInt(32 - var4))), Particle.GType.COSMETIC).lifeTime(1000).sprite((var1x, var2x, var3x, var4x) -> {
            int var5 = GameResources.liquidBlobParticle.getWidth() / var4;
            return var1x.add(GameResources.liquidBlobParticle.sprite((int)(var4x * (float)var5), 0, var4));
         }).color(var5);
      }

   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      if (var1.canLevelInteract() && !var1.isFlying() && !var1.isWaterWalking() && var2.inLiquid(var1.getX(), var1.getY()) && var2.isServer() && var1.canTakeDamage() && !var1.isSlimeImmune() && !var1.isOnGenericCooldown("slimedamage")) {
         var1.startGenericCooldown("slimedamage", 500L);
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_POISON, var1, 10.0F, (Attacker)null), true);
      }

   }

   public Color getLiquidColor(Level var1, int var2, int var3) {
      return this.getLiquidColor(4);
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
