package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.BeeHiveObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BeeHiveObject extends GameObject {
   public GameTexture texture;

   public BeeHiveObject() {
      super(new Rectangle(2, 8, 28, 22));
      this.mapColor = new Color(206, 171, 8);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/beehive");
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      AbstractBeeHiveObjectEntity var10 = this.getApiaryObjectEntity(var3, var4, var5);
      int var11 = var7.getTileDrawX(var4);
      int var12 = var7.getTileDrawY(var5);
      int var13 = 0;
      if (var10 != null) {
         int var14 = Math.max(var10.getMaxBees() - 1, 1);
         int var15 = BeeHiveObjectEntity.maxBees;
         float var16 = (float)var14 / (float)var15;
         int var17 = this.texture.getWidth() / 32;
         var13 = Math.min(Math.round(var16 * (float)var17), var17 - 1);
      }

      final TextureDrawOptionsEnd var18 = this.texture.initDraw().sprite(var13, 0, 32, this.texture.getHeight()).light(var9).pos(var11, var12 - this.texture.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var18.draw();
         }
      });
   }

   public AbstractBeeHiveObjectEntity getApiaryObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof AbstractBeeHiveObjectEntity ? (AbstractBeeHiveObjectEntity)var4 : null;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      AbstractBeeHiveObjectEntity var6 = this.getApiaryObjectEntity(var1, var2, var3);
      return var6 != null ? var6.getInteractTip(var4) : null;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      AbstractBeeHiveObjectEntity var5 = this.getApiaryObjectEntity(var1, var2, var3);
      if (var5 != null) {
         var5.interact(var4);
      }

   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new BeeHiveObjectEntity(var1, var2, var3);
   }
}
