package necesse.engine.commands.parameterHandlers;

import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;

public class ItemParameterHandler extends ParameterHandler<Item> {
   public ItemParameterHandler() {
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromArray((Item[])ItemRegistry.getItems().toArray(new Item[0]), (var0) -> {
         return ItemRegistry.isObtainable(var0.getID());
      }, Item::getStringID, var4);
   }

   public Item parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      Item var6 = ItemRegistry.getItem(var4);
      if (var6 == null) {
         throw new IllegalArgumentException("Could not find item with stringID \"" + var4 + "\" for <" + var5.name + ">");
      } else {
         return var6;
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public Item getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return null;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.getDefault(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      return this.parse(var1, var2, var3, var4, var5);
   }
}
