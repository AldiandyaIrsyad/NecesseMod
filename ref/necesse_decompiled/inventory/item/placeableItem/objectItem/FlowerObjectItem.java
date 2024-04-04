package necesse.inventory.item.placeableItem.objectItem;

import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class FlowerObjectItem extends CustomObjectItem implements ItemInteractAction {
   public FlowerObjectItem(GameObject var1, Supplier<GameTexture> var2) {
      super(var1, (Supplier)var2, 0, 0);
   }

   public boolean canMobInteract(Level var1, Mob var2, PlayerMob var3, InventoryItem var4) {
      return var2 instanceof HusbandryMob && ((HusbandryMob)var2).canFeed(var4) && var2.inInteractRange(var3);
   }

   public InventoryItem onMobInteract(Level var1, Mob var2, PlayerMob var3, int var4, InventoryItem var5, PlayerInventorySlot var6, int var7, PacketReader var8) {
      if (var2 instanceof HusbandryMob) {
         HusbandryMob var9 = (HusbandryMob)var2;
         if (var9.canFeed(var5)) {
            return var9.onFed(var5);
         }
      }

      return var5;
   }

   public boolean getConstantInteract(InventoryItem var1) {
      return true;
   }

   public boolean onMouseHoverMob(InventoryItem var1, GameCamera var2, PlayerMob var3, Mob var4, boolean var5) {
      boolean var6 = super.onMouseHoverMob(var1, var2, var3, var4, var5);
      if (var4 instanceof HusbandryMob && ((HusbandryMob)var4).canFeed(var1)) {
         if (var4.inInteractRange(var3)) {
            Screen.setCursor(Screen.CURSOR.INTERACT);
            Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "feedtip")), TooltipLocation.INTERACT_FOCUS);
         } else {
            Screen.setCursor(Screen.CURSOR.INTERACT);
            Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "feedtip"), 0.5F), TooltipLocation.INTERACT_FOCUS);
         }

         return true;
      } else {
         return var6;
      }
   }
}
