package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;

public class CoolingBoxInventoryObject extends InventoryObject {
   public CoolingBoxInventoryObject(String var1, int var2, ToolType var3, Color var4) {
      super(var1, var2, new Rectangle(32, 32), var3, var4);
   }

   public CoolingBoxInventoryObject(String var1, int var2, Color var3) {
      super(var1, var2, new Rectangle(32, 32), var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "coolingboxtip"), 400);
      return var3;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      FueledRefrigeratorObjectEntity var4 = this.getCoolingBoxObjectEntity(var1, var2, var3);
      if (var4 != null && var4.hasFuel() && GameRandom.globalRandom.nextInt(10) == 0) {
         int var5 = 16 + GameRandom.globalRandom.nextInt(16);
         var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(2, 30)), (float)(var3 * 32 + 32), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).heightMoves((float)var5, (float)(var5 - 12)).lifeTime(3000).fadesAlphaTimeToCustomAlpha(500, 500, 0.25F).size(new ParticleOption.DrawModifier() {
            public void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4) {
               var1.size(24, 24);
            }
         });
      }

   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return var4 % 2 == 0 ? new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 28, 20) : new Rectangle(var2 * 32 + 6, var3 * 32 + 2, 20, 28);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new FueledRefrigeratorObjectEntity(var1, var2, var3, 2, 40, 0.25F);
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.FUELED_REFRIGERATOR_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public FueledRefrigeratorObjectEntity getCoolingBoxObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof FueledRefrigeratorObjectEntity ? (FueledRefrigeratorObjectEntity)var4 : null;
   }
}
