package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.placeableItem.PlaceableItem;

public class ConsumableItem extends PlaceableItem {
   public boolean allowRightClickToConsume;

   public ConsumableItem(int var1, boolean var2) {
      super(var1, var2);
      this.setItemCategory(new String[]{"consumable"});
      this.keyWords.add("consumeable");
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return this.allowRightClickToConsume && var4.getInventory() instanceof PlayerInventory ? () -> {
         if (var1.getClient().isClient()) {
            PlayerMob var3 = var1.getClient().playerMob;
            Point var2;
            switch (var3.dir) {
               case 0:
                  var2 = new Point(0, -1);
                  break;
               case 1:
                  var2 = new Point(1, 0);
                  break;
               case 2:
                  var2 = new Point(0, 1);
                  break;
               default:
                  var2 = new Point(-1, 0);
            }

            PlayerInventorySlot var4x = new PlayerInventorySlot((PlayerInventory)var4.getInventory(), var4.getInventorySlot());
            if (var3.tryAttack(var4x, var3.getX() + var2.x * 100, var3.getY() + var2.y * 100) && var4x.getItem(var3.getInv()) == null) {
               return new ContainerActionResult((String)null);
            }
         }

         return new ContainerActionResult(645719);
      } : super.getInventoryRightClickAction(var1, var2, var3, var4);
   }

   public static GameMessage getDurationMessage(int var0, boolean var1) {
      int var2 = 0;
      int var3 = 0;
      if (var0 >= 60) {
         var2 = var0 / 60;
         var0 -= var2 * 60;
         if (var2 >= 60) {
            var3 = var2 / 60;
            var2 -= var3 * 60;
         }
      }

      GameMessageBuilder var4 = new GameMessageBuilder();
      if (var3 > 0) {
         if (var3 != 1) {
            var4.append((GameMessage)(new LocalMessage("itemtooltip", "hoursduration", new Object[]{"value", var3})));
         } else {
            var4.append((GameMessage)(new LocalMessage("itemtooltip", "hourduration", new Object[]{"value", var3})));
         }

         if (!var1 && var3 >= 10) {
            return var4;
         }
      }

      if (var2 > 0) {
         if (var4.size() != 0) {
            var4.append(" ");
         }

         if (var2 != 1) {
            var4.append((GameMessage)(new LocalMessage("itemtooltip", "minsduration", new Object[]{"value", var2})));
         } else {
            var4.append((GameMessage)(new LocalMessage("itemtooltip", "minduration", new Object[]{"value", var2})));
         }

         if (!var1 && (var3 > 0 || var2 >= 10)) {
            return var4;
         }
      }

      if (var0 > 0 || var4.size() == 0) {
         if (var4.size() != 0) {
            var4.append(" ");
         }

         if (var0 != 1) {
            var4.append((GameMessage)(new LocalMessage("itemtooltip", "secsduration", new Object[]{"value", var0})));
         } else {
            var4.append((GameMessage)(new LocalMessage("itemtooltip", "secduration", new Object[]{"value", var0})));
         }
      }

      return var4;
   }

   public static GameMessage getSpoilsTimeWithRateMessage(int var0, float var1) {
      if (var1 > 0.0F) {
         String var2 = GameUtils.formatNumber((double)(var1 * 100.0F));
         return new LocalMessage("itemtooltip", "spoilstimewithrate", new Object[]{"time", getDurationMessage(var0, false), "rate", var2 + "%"});
      } else {
         return getSpoilsTimeMessage(var0);
      }
   }

   public static GameMessage getSpoilsTimeMessage(int var0) {
      return new LocalMessage("itemtooltip", "spoilstime", "time", getDurationMessage(var0, false));
   }

   public static GameMessage getSpoilStoppedTimeMessage(int var0) {
      return new LocalMessage("itemtooltip", "spoilingstopped", "time", getDurationMessage(var0, false));
   }

   public static GameMessage getBuffDurationMessage(int var0) {
      return new LocalMessage("itemtooltip", "buffduration", "time", getDurationMessage(var0, true));
   }
}
