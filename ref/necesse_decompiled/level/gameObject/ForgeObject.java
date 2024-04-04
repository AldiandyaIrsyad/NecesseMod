package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AnyLogFueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ForgeObject extends FueledCraftingStationObject {
   public GameTexture texture;

   public ForgeObject() {
      super(new Rectangle(32, 32));
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.roomProperties.add("metalwork");
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      FueledInventoryObjectEntity var4 = this.getFueledObjectEntity(var1, var2, var3);
      return var4 != null && var4.isFueled() ? 100 : 0;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      if (GameRandom.globalRandom.nextInt(10) == 0) {
         ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
         if (var4 instanceof FueledInventoryObjectEntity && ((FueledInventoryObjectEntity)var4).isFueled()) {
            int var5 = 16 + GameRandom.globalRandom.nextInt(16);
            var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(var3 * 32 + 32), Particle.GType.COSMETIC).smokeColor().heightMoves((float)var5, (float)(var5 + 20)).lifeTime(1000);
         }
      }

   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/forge");
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return var4 % 2 == 0 ? new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 28, 20) : new Rectangle(var2 * 32 + 6, var3 * 32 + 2, 20, 28);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      boolean var13 = false;
      ObjectEntity var14 = var3.entityManager.getObjectEntity(var4, var5);
      if (var14 instanceof FueledInventoryObjectEntity) {
         var13 = ((FueledInventoryObjectEntity)var14).isFueled();
      }

      int var15 = this.texture.getHeight() - 32;
      final TextureDrawOptionsEnd var16 = this.texture.initDraw().sprite(var12 % 4, 0, 32, var15).light(var9).pos(var10, var11 - (var15 - 32));
      final TextureDrawOptionsEnd var17;
      if (var13 && var12 == 2) {
         int var18 = (int)(var3.getWorldEntity().getWorldTime() % 1200L / 300L);
         var17 = this.texture.initDraw().sprite(var18, var15 / 32, 32).light(var9).pos(var10, var11);
      } else {
         var17 = null;
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var16.draw();
            if (var17 != null) {
               var17.draw();
            }

         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      int var10 = this.texture.getHeight() - 32;
      this.texture.initDraw().sprite(var4 % 4, 0, 32, var10).alpha(var5).draw(var8, var9 - (var10 - 32));
   }

   public Tech[] getCraftingTechs() {
      return new Tech[]{RecipeTechRegistry.FORGE};
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new AnyLogFueledInventoryObjectEntity(var1, "forge", var2, var3, false);
   }
}
