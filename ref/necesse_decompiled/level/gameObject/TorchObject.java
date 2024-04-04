package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.localization.Localization;
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
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.TorchObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TorchObject extends GameObject {
   public float flameHue;
   public float smokeHue;
   protected String textureName;
   public GameTexture texture;
   public GameTexture texture_off;
   public int particleStartHeight;

   public TorchObject(String var1, ToolType var2, Color var3, float var4, float var5) {
      this.flameHue = ParticleOption.defaultFlameHue;
      this.smokeHue = ParticleOption.defaultSmokeHue;
      this.particleStartHeight = 12;
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.lightHue = var4;
      this.lightSat = var5;
      this.objectHealth = 1;
      this.lightLevel = 150;
      this.stackSize = 500;
      this.drawDamage = false;
      this.displayMapTooltip = true;
      this.isLightTransparent = true;
      this.canPlaceOnShore = true;
      this.roomProperties.add("lights");
      this.replaceCategories.add("torch");
      this.canReplaceCategories.add("torch");
      this.canReplaceCategories.add("furniture");
      this.canReplaceCategories.add("column");
   }

   public TorchObject(String var1, Color var2, float var3, float var4) {
      this(var1, ToolType.ALL, var2, var3, var4);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
      this.texture_off = GameTexture.fromFile("objects/" + this.textureName + "_off");
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("controls", "torchplacetip"));
      return var3;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(40) && this.isActive(var1, var2, var3)) {
         int var4 = this.particleStartHeight + (int)(GameRandom.globalRandom.nextGaussian() * 2.0);
         BombProjectile.spawnFuseParticle(var1, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16 + 2), (float)var4, this.flameHue, this.smokeHue);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      GameTexture var12 = this.isActive(var3, var4, var5) ? this.texture : this.texture_off;
      int var13 = var3.getObjectRotation(var4, var5) % (var12.getWidth() / 32);
      final TextureDrawOptionsEnd var14 = var12.initDraw().sprite(var13, 0, 32, var12.getHeight()).light(var9).pos(var10, var11 - var12.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            TextureDrawOptions var10002 = var14;
            Objects.requireNonNull(var10002);
            Performance.record(var1, "torchDraw", (Runnable)(var10002::draw));
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      byte var10 = (byte)(var4 % (this.texture.getWidth() / 32));
      this.texture.initDraw().sprite(var10, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public Item generateNewObjectItem() {
      return new TorchObjectItem(this, false);
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
