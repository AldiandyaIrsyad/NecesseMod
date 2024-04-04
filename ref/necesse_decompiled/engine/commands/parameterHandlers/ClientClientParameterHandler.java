package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ClientClientParameterHandler extends ParameterHandler<ClientClient> {
   public final boolean returnDefaultSelf;

   public ClientClientParameterHandler(boolean var1) {
      this.returnDefaultSelf = var1;
   }

   public ClientClientParameterHandler() {
      this(false);
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      if (var1 != null) {
         return autocompleteFromList((List)var1.streamClients().collect(Collectors.toList()), (var0) -> {
            return true;
         }, ClientClient::getName, var4);
      } else {
         return var2 != null ? autocompleteFromList((List)var2.streamClients().collect(Collectors.toList()), (var0) -> {
            return true;
         }, ServerClient::getName, var4) : Collections.emptyList();
      }
   }

   public ClientClient parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      ClientClient var6 = (ClientClient)var1.streamClients().filter((var1x) -> {
         return var1x.getName().toLowerCase().equals(var4.toLowerCase());
      }).findFirst().orElse((Object)null);
      if (var6 == null) {
         var6 = (ClientClient)var1.streamClients().filter((var1x) -> {
            return var1x.getName().toLowerCase().contains(var4.toLowerCase());
         }).findFirst().orElse((Object)null);
      }

      if (var6 == null) {
         throw new IllegalArgumentException("Could not find player with name \"" + var4 + "\" for <" + var5.name + ">");
      } else {
         return var6;
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public ClientClient getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.returnDefaultSelf ? var1.getClient() : null;
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
