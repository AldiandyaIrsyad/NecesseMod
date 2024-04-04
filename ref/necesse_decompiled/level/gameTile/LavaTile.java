package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;

public class LavaTile extends LiquidTile {
   public GameTextureSection texture;
   protected final GameRandom drawRandom;

   public LavaTile() {
      super(new Color(150, 0, 0));
      this.lightLevel = 100;
      this.lightHue = 0.0F;
      this.lightSat = 0.6F;
      this.drawRandom = new GameRandom();
   }

   protected void loadTextures() {
      super.loadTextures();
      this.texture = tileTextures.addTexture(GameTexture.fromFile("tiles/lava"));
   }

   public double getPathCost(Level var1, int var2, int var3, Mob var4) {
      return !var4.isLavaImmune() ? 1000.0 : super.getPathCost(var1, var2, var3, var4);
   }

   public float getItemSinkingRate(float var1) {
      return TickManager.getTickDelta(60.0F);
   }

   public float getItemMaxSinking() {
      return 1.0F;
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      if (var1.canLevelInteract() && !var1.isFlying() && !var1.isWaterWalking() && var2.inLiquid(var1.getX(), var1.getY()) && var2.isServer() && var1.canTakeDamage() && !var1.isLavaImmune() && !var1.isOnGenericCooldown("lavadamage")) {
         int var5 = var1.getMaxHealth();
         float var6 = Math.max((float)Math.pow((double)var5, 0.5) + (float)var5 / 20.0F, 20.0F);
         var6 *= (Float)var1.buffManager.getModifier(BuffModifiers.FIRE_DAMAGE);
         if (var6 != 0.0F) {
            var1.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, var6), 0.0F, 0.0F, 0.0F, LAVA_ATTACKER);
         }

         var1.startGenericCooldown("lavadamage", 500L);
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE, var1, 10.0F, (Attacker)null), true);
      }

   }

   public Color getLiquidColor(Level var1, int var2, int var3) {
      return this.getLiquidColor(1);
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(200) && var1.getObjectID(var2, var3) == 0) {
         byte var4 = 12;
         Color var5 = GameUtils.getBrighterColor(this.getLiquidColor(var1, var2, var3), 0.85F);
         var1.entityManager.addParticle(ParticleOption.base((float)(var2 * 32 + GameRandom.globalRandom.nextInt(32 - var4)), (float)(var3 * 32 + GameRandom.globalRandom.nextInt(32 - var4))), Particle.GType.COSMETIC).lifeTime(1000).sprite((var1x, var2x, var3x, var4x) -> {
            int var5 = GameResources.liquidBlobParticle.getWidth() / var4;
            return var1x.add(GameResources.liquidBlobParticle.sprite((int)(var4x * (float)var5), 0, var4));
         }).color(var5);
      }

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
         synchronized(this.drawRandom) {
            var12 = this.drawRandom.seeded(this.getTileSeed(var4, var5)).nextInt(this.texture.getHeight() / 32);
         }

         var1.add(this.texture.sprite(0, var12, 32)).color(GameUtils.getBrighterColor(this.getLiquidColor(var3, var4, var5), 0.85F)).pos(var9, var10 + var11 - 2);
      }

   }

   public int getLiquidBobbing(Level var1, int var2, int var3) {
      return super.getLiquidBobbing(var1, var2, var3) / 2;
   }
}
