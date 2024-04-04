package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.GrainMillObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

abstract class GrainMillExtraObject extends GameObject {
   public GrainMillExtraObject() {
      super(new Rectangle(32, 32));
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.mapColor = new Color(150, 119, 70);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.objectHealth = 100;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.replaceCategories.add("workstation");
      this.canReplaceCategories.add("workstation");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("furniture");
   }

   protected abstract void setCounterIDs(int var1, int var2, int var3, int var4);

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (this.isMultiTileMaster()) {
         if (var1.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.PROCESSING_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
         }
      } else {
         this.getMultiTile(var1.getObjectRotation(var2, var3)).getMasterLevelObject(var1, var2, var3).ifPresent((var1x) -> {
            var1x.interact(var4);
         });
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return this.isMultiTileMaster() ? new GrainMillObjectEntity(var1, var2, var3) : null;
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "grainmilltip"));
      return var3;
   }
}
