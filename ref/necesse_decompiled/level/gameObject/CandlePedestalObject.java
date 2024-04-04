package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CandlePedestalObject extends GameObject {
   public float flameHue;
   public float smokeHue;
   public GameTexture texture;
   public int particleStartHeight;

   public CandlePedestalObject() {
      super(new Rectangle(7, 7, 18, 18));
      this.flameHue = ParticleOption.defaultFlameHue;
      this.smokeHue = ParticleOption.defaultSmokeHue;
      this.particleStartHeight = 40;
      this.mapColor = new Color(240, 200, 10);
      this.displayMapTooltip = true;
      this.lightLevel = 100;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.stackSize = 100;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.roomProperties.add("lights");
      this.canPlaceOnShore = true;
      this.replaceCategories.add("torch");
      this.canReplaceCategories.add("torch");
      this.canReplaceCategories.add("furniture");
      this.canReplaceCategories.add("column");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.getStringID());
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(40) && this.isActive(var1, var2, var3)) {
         int var4 = this.particleStartHeight + (int)(GameRandom.globalRandom.nextGaussian() * 2.0);
         BombProjectile.spawnFuseParticle(var1, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16 + 2), (float)var4, this.flameHue, this.smokeHue);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "candlepedestalSetup", (Runnable)(() -> {
         GameLight var6 = var3.getLightLevel(var4, var5);
         int var7x = var7.getTileDrawX(var4);
         int var8 = var7.getTileDrawY(var5);
         boolean var9 = this.isActive(var3, var4, var5);
         final TextureDrawOptionsEnd var10 = this.texture.initDraw().sprite(0, var9 ? 0 : 1, 32, this.texture.getHeight() / 2).light(var6).pos(var7x, var8 - this.texture.getHeight() / 2 + 32);
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return 16;
            }

            public void draw(TickManager var1) {
               TextureDrawOptions var10002 = var10;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "candlepedestalDraw", (Runnable)(var10002::draw));
            }
         });
      }));
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight() / 2).alpha(var5).draw(var8, var9 - this.texture.getHeight() / 2 + 32);
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.isActive(var1, var2, var3) ? this.lightLevel : 0;
   }

   public boolean isActive(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      return this.getMultiTile(var4).streamIDs(var2, var3).noneMatch((var1x) -> {
         return var1.wireManager.isWireActiveAny(var1x.tileX, var1x.tileY);
      });
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      byte var6 = var1.getObjectRotation(var2, var3);
      Rectangle var7 = this.getMultiTile(var6).getTileRectangle(var2, var3);
      var1.lightManager.updateStaticLight(var7.x, var7.y, var7.x + var7.width - 1, var7.y + var7.height - 1, true);
   }
}
