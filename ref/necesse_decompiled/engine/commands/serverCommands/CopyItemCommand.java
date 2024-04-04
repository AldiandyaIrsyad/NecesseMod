package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class CopyItemCommand extends ModularChatCommand {
   public CopyItemCommand() {
      super("copyitem", "Copies an item and all of its data", PermissionLevel.ADMIN, true, new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Cannot run enchant command from server");
      } else {
         int var7 = (Integer)var4[0];
         PlayerInventorySlot var8 = var7 < 0 ? var3.playerMob.getSelectedItemSlot() : new PlayerInventorySlot(var3.playerMob.getInv().main, var7);
         if (var8.slot >= var3.playerMob.getInv().main.getSize()) {
            var6.add("Slot must be below " + var3.playerMob.getInv().main.getSize());
         } else {
            InventoryItem var9 = var3.playerMob.getInv().getItem(var8);
            if (var9 == null) {
               var6.add("Could not find item in selected slot");
            } else {
               var3.playerMob.getInv().addItemsDropRemaining(var9.copy(), "addloot", var3.playerMob, true, true);
               var6.add("Copied " + var9.getItemDisplayName());
            }
         }
      }
   }
}
