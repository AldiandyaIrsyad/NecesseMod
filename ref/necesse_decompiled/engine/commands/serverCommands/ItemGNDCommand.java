package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDItemParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class ItemGNDCommand extends ModularChatCommand {
   public ItemGNDCommand() {
      super("itemgnd", "Gets or sets item GND data", PermissionLevel.OWNER, true, new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("set/get/clear", new PresetStringParameterHandler(new String[]{"set", "get", "clear"})), new CmdParameter("key", new StringParameterHandler(), true, new CmdParameter[]{new CmdParameter("value", GNDItemParameterHandler.getMultiParameterHandler(), true, new CmdParameter[0])}));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Cannot run inspect item command from server");
      } else {
         int var7 = (Integer)var4[0];
         PlayerInventorySlot var8 = var7 < 0 ? var3.playerMob.getSelectedItemSlot() : new PlayerInventorySlot(var3.playerMob.getInv().main, var7);
         if (var8.slot >= var3.playerMob.getInv().main.getSize()) {
            var6.add("Slot must be below " + var3.playerMob.getInv().main.getSize());
         } else {
            InventoryItem var9 = var3.playerMob.getInv().getItem(var8);
            if (var9 == null) {
               var6.add("Empty item selected");
            } else {
               GNDItemMap var10 = var9.getGndData();
               String var11 = (String)var4[1];
               String var12 = (String)var4[2];
               GNDItem var13 = GNDItemParameterHandler.getReturnedItem(var4[3]);
               switch (var11) {
                  case "get":
                     if (var12 == null) {
                        if (var10.getMapSize() == 0) {
                           var6.add(var9.getItemDisplayName() + " has no GND data");
                        } else {
                           var6.add(var9.getItemDisplayName() + " GND data:");
                           Iterator var16 = var10.getKeyStringSet().iterator();

                           while(var16.hasNext()) {
                              String var17 = (String)var16.next();
                              var6.add(var17 + ": " + var10.getItem(var17).toString());
                           }

                           return;
                        }
                     } else {
                        GNDItem var18 = var10.getItem(var12);
                        if (var18 != null) {
                           var6.add(var12 + ": " + var10.getItem(var12).toString());
                        } else {
                           var6.add(var9.getItemDisplayName() + " has no GND data with key " + var12);
                        }
                     }
                     break;
                  case "clear":
                     if (var12 == null) {
                        var9.setGndData(new GNDItemMap());
                        var6.add("Cleared all GND data on " + var9.getItemDisplayName());
                        var8.markDirty(var3.playerMob.getInv());
                        return;
                     }

                     var10.setItem(var12, (GNDItem)null);
                     var6.add("Cleared GND key " + var12 + " on " + var9.getItemDisplayName());
                     var8.markDirty(var3.playerMob.getInv());
                     break;
                  case "set":
                     if (var12 == null) {
                        var6.add("Must supply key for set action");
                        return;
                     }

                     var10.setItem(var12, var13);
                     var6.add("Set key " + var12 + " to " + (var13 == null ? "null" : var13.getStringID()) + " " + var13 + " on " + var9.getItemDisplayName());
                     var8.markDirty(var3.playerMob.getInv());
               }

            }
         }
      }
   }
}
