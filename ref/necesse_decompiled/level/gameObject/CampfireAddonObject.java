package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CampfireObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.CampfireAddonObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class CampfireAddonObject extends FueledCraftingStationObject {
   public CampfireAddonObject() {
      super(new Rectangle(4, 6, 24, 20));
      this.drawDamage = false;
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.lightHue = 50.0F;
      this.lightSat = 0.5F;
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      FueledInventoryObjectEntity var4 = this.getFueledObjectEntity(var1, var2, var3);
      return var4 != null && var4.isFueled() ? 100 : 0;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      FueledInventoryObjectEntity var4 = this.getFueledObjectEntity(var1, var2, var3);
      if (var4 != null && var4.isFueled()) {
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

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new CampfireObjectEntity(var1, this.getStringID(), var2, var3, false, false);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{super.getLootTable(var1, var2, var3), ObjectRegistry.getObject("campfire").getLootTable(var1, var2, var3)});
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      return !var1.getObject(var2, var3).getStringID().equals("campfire") ? "notcampfire" : null;
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "campfireaddontip"));
      return var3;
   }

   public Item generateNewObjectItem() {
      return new CampfireAddonObjectItem(this);
   }
}
