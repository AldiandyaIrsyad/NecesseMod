package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnchantmentParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;

public class EnchantServerCommand extends ModularChatCommand {
   public EnchantServerCommand() {
      super("enchant", "Clears, sets or gives a random enchant (use -1 slot for selected item)", PermissionLevel.ADMIN, true, new CmdParameter("clear/set/random", new PresetStringParameterHandler(new String[]{"clear", "set", "random"})), new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("enchantID", new EnchantmentParameterHandler(), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Cannot run enchant command from server");
      } else {
         String var7 = (String)var4[0];
         int var8 = (Integer)var4[1] - 1;
         if (var8 == -1) {
            var8 = 9;
         }

         ItemEnchantment var9 = (ItemEnchantment)var4[2];
         PlayerInventorySlot var10 = var8 < 0 ? var3.playerMob.getSelectedItemSlot() : new PlayerInventorySlot(var3.playerMob.getInv().main, var8);
         if (var10.slot >= var3.playerMob.getInv().main.getSize()) {
            var6.add("Slot must be below " + var3.playerMob.getInv().main.getSize());
         } else {
            InventoryItem var11 = var3.playerMob.getInv().getItem(var10);
            if (var11 != null && var11.item.isEnchantable(var11)) {
               Enchantable var12 = (Enchantable)var11.item;
               switch (var7) {
                  case "clear":
                     var12.clearEnchantment(var11);
                     var6.add("Cleared enchant on " + ItemRegistry.getDisplayName(var11.item.getID()));
                     break;
                  case "set":
                     if (var9 == null) {
                        var6.add("Must specify enchantID on set mode");
                        return;
                     }

                     var12.setEnchantment(var11, var9.getID());
                     var6.add("Gave enchantment " + var9.getDisplayName() + " to " + ItemRegistry.getDisplayName(var11.item.getID()));
                     break;
                  case "random":
                     var12.addRandomEnchantment(var11);
                     var6.add(ItemRegistry.getDisplayName(var11.item.getID()) + " got enchantment " + var12.getEnchantName(var11));
               }

               var10.getInv(var3.playerMob.getInv()).markDirty(var10.slot);
            } else {
               var6.add("Invalid item selected");
            }
         }
      }
   }
}
