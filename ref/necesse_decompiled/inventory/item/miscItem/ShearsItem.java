package necesse.inventory.item.miscItem;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

public class ShearsItem extends Item implements ItemInteractAction {
   public ShearsItem() {
      super(1);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.attackXOffset = 8;
      this.attackYOffset = 20;
      this.dropsAsMatDeathPenalty = false;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)(new StringTooltips(Localization.translate("itemtooltip", "shearstip"), 300)));
      return var4;
   }

   public boolean canMobInteract(Level var1, Mob var2, PlayerMob var3, InventoryItem var4) {
      return var2 instanceof HusbandryMob && ((HusbandryMob)var2).canShear(var4) && var2.inInteractRange(var3);
   }

   public InventoryItem onMobInteract(Level var1, Mob var2, PlayerMob var3, int var4, InventoryItem var5, PlayerInventorySlot var6, int var7, PacketReader var8) {
      if (var2 instanceof HusbandryMob) {
         HusbandryMob var9 = (HusbandryMob)var2;
         if (var9.canShear(var5)) {
            ArrayList var10 = new ArrayList();
            InventoryItem var11 = var9.onShear(var5, var10);
            if (!var1.isClient()) {
               Iterator var12 = var10.iterator();

               while(var12.hasNext()) {
                  InventoryItem var13 = (InventoryItem)var12.next();
                  var1.entityManager.pickups.add(var13.getPickupEntity(var1, var9.x, var9.y));
               }
            }

            return var11;
         }
      }

      return var5;
   }

   public boolean getConstantInteract(InventoryItem var1) {
      return true;
   }

   public boolean onMouseHoverMob(InventoryItem var1, GameCamera var2, PlayerMob var3, Mob var4, boolean var5) {
      boolean var6 = super.onMouseHoverMob(var1, var2, var3, var4, var5);
      if (var4 instanceof HusbandryMob && ((HusbandryMob)var4).canShear(var1)) {
         if (var4.inInteractRange(var3)) {
            Screen.setCursor(Screen.CURSOR.INTERACT);
            Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "sheartip")), TooltipLocation.INTERACT_FOCUS);
         } else {
            Screen.setCursor(Screen.CURSOR.INTERACT);
            Screen.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "sheartip"), 0.5F), TooltipLocation.INTERACT_FOCUS);
         }

         return true;
      } else {
         return var6;
      }
   }
}
