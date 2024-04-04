package necesse.engine.commands.parameterHandlers;

import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.IDDataContainer;
import necesse.inventory.enchants.ItemEnchantment;

public class EnchantmentParameterHandler extends ParameterHandler<ItemEnchantment> {
   public EnchantmentParameterHandler() {
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromList(EnchantmentRegistry.getEnchantments(), (var0) -> {
         return var0.getID() != 0;
      }, IDDataContainer::getStringID, var4);
   }

   public ItemEnchantment parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      ItemEnchantment var6 = EnchantmentRegistry.getEnchantment(var4);
      if (var6 == null) {
         throw new IllegalArgumentException("Could not find enchantment with stringID \"" + var4 + "\" for <" + var5.name + ">");
      } else {
         return var6;
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public ItemEnchantment getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
