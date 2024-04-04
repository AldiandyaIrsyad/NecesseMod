package necesse.engine.commands.parameterHandlers;

import java.util.Iterator;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.biomes.Biome;

public class BiomeParameterHandler extends ParameterHandler<Biome> {
   private Biome defaultValue;

   public BiomeParameterHandler() {
      this.defaultValue = null;
   }

   public BiomeParameterHandler(Biome var1) {
      this.defaultValue = var1;
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromList(BiomeRegistry.getBiomes(), (var0) -> {
         return var0 != BiomeRegistry.UNKNOWN;
      }, Biome::getStringID, var4);
   }

   public Biome parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      Iterator var6 = BiomeRegistry.getBiomes().iterator();

      Biome var7;
      do {
         if (!var6.hasNext()) {
            throw new IllegalArgumentException("Could not find biome \"" + var4 + "\" for <" + var5.name + ">");
         }

         var7 = (Biome)var6.next();
      } while(var7 == BiomeRegistry.UNKNOWN || !var4.equalsIgnoreCase(var7.getStringID()));

      return var7;
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public Biome getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.defaultValue;
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
