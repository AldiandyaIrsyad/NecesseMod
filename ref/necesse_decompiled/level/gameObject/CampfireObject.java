package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CampfireObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CampfireObject extends GameObject {
   public GameTexture texture;

   public CampfireObject() {
      super(new Rectangle(4, 6, 24, 20));
      this.mapColor = new Color(90, 71, 41);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.lightHue = 50.0F;
      this.lightSat = 0.5F;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/campfire");
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof FueledInventoryObjectEntity && ((FueledInventoryObjectEntity)var4).isFueled() ? 100 : 0;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      if (var4 instanceof FueledInventoryObjectEntity && ((FueledInventoryObjectEntity)var4).isFueled()) {
         float var5 = 0.5F;

         while(var5 >= 1.0F || GameRandom.globalRandom.getChance(var5)) {
            --var5;
            ParticleOption var6 = var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(11, 21)), (float)(var3 * 32 + GameRandom.globalRandom.getIntBetween(10, 16)), GameRandom.globalRandom.getChance(0.75F) ? Particle.GType.CRITICAL : Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F), GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F)).heightMoves(0.0F, 10.0F).flameColor().sizeFades(10, 14).lifeTime(2000);
            if (GameRandom.globalRandom.nextBoolean()) {
               var6.onProgress(0.5F, (var1x) -> {
                  for(int var2 = 0; var2 < GameRandom.globalRandom.getIntBetween(1, 2); ++var2) {
                     var1.entityManager.addParticle(var1x.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), var1x.y, Particle.GType.COSMETIC).smokeColor().sizeFades(8, 12).heightMoves(6.0F, 20.0F);
                  }

               });
            }
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).pos(var10, var11 - (this.texture.getHeight() - 32));
      var2.add((var1x) -> {
         var12.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 32));
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.FUELED_OE_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new CampfireObjectEntity(var1, "campfire", var2, var3, true, true);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "campfiretip"));
      return var3;
   }
}
