package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.ApiaryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.ApiaryFramePlaceableItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.FillApiaryFrameLevelJob;
import necesse.level.maps.levelData.jobs.HarvestApiaryLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class ApiaryObject extends GameObject {
   public GameTexture texture;

   public ApiaryObject() {
      super(new Rectangle(2, 8, 28, 22));
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.mapColor = new Color(150, 119, 70);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.objectHealth = 50;
      this.rarity = Item.Rarity.COMMON;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      AbstractBeeHiveObjectEntity var4 = this.getApiaryObjectEntity(var1, var2, var3);
      if (var4 != null) {
         ArrayList var5 = new ArrayList(2);
         if (var4.getHoneyAmount() > 0) {
            var5.add(new HarvestApiaryLevelJob(var2, var3));
         }

         if (var4.canAddFrame()) {
            var5.add(new FillApiaryFrameLevelJob(var2, var3));
         }

         return var5;
      } else {
         return super.getLevelJobs(var1, var2, var3);
      }
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/apiary");
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      int var12 = var3.getObjectRotation(var4, var5) % 4;
      int var13 = this.texture.getHeight() / 4;
      int var14 = 0;
      if (var8 != null) {
         InventoryItem var15 = var8.getSelectedItem();
         if (var15 != null && var15.item instanceof ApiaryFramePlaceableItem) {
            var14 = 1;
            AbstractBeeHiveObjectEntity var16 = this.getApiaryObjectEntity(var3, var4, var5);
            if (var16 != null) {
               var14 = Math.min(1 + var16.getFrameAmount(), this.texture.getWidth() / 32);
            }
         }
      }

      final TextureDrawOptionsEnd var17 = this.texture.initDraw().sprite(var14, var12, 32, var13).light(var9).pos(var10, var11 - var13 + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var17.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      int var10 = this.texture.getHeight() / 4;
      this.texture.initDraw().sprite(0, var4 % 4, 32, var10).alpha(var5).draw(var8, var9 - var10 + 32);
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      AbstractBeeHiveObjectEntity var4 = this.getApiaryObjectEntity(var1, var2, var3);
      if (var4 != null && GameRandom.globalRandom.nextInt(10) == 0) {
         int var5;
         if (var4.getHoneyAmount() > 0) {
            var5 = 16 + GameRandom.globalRandom.nextInt(16);
            var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(var3 * 32 + 32), Particle.GType.IMPORTANT_COSMETIC).color(new Color(200, 200, 0)).heightMoves((float)var5, (float)(var5 + 20)).lifeTime(1000);
         } else if (var4.hasQueen() || var4.getBeeAmount() > 0) {
            var5 = 16 + GameRandom.globalRandom.nextInt(16);
            var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(var3 * 32 + 32), Particle.GType.IMPORTANT_COSMETIC).color(new Color(200, 200, 200)).heightMoves((float)var5, (float)(var5 + 20)).lifeTime(1000);
         }
      }

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
      return new ApiaryObjectEntity(var1, var2, var3);
   }

   public boolean onDamaged(Level var1, int var2, int var3, int var4, ServerClient var5, boolean var6, int var7, int var8) {
      boolean var9 = super.onDamaged(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var4 > 0 && !var1.isClient()) {
         AbstractBeeHiveObjectEntity var10 = this.getApiaryObjectEntity(var1, var2, var3);
         if (var10.hasQueen()) {
            var10.removeQueen(var5 == null ? null : var5.playerMob);
            return false;
         }
      }

      return var9;
   }
}
