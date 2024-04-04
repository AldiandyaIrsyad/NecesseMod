package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.WorldFile;

public class StoredPlayerParameterHandler extends ParameterHandler<StoredPlayer> {
   public StoredPlayerParameterHandler() {
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      if (var2 != null) {
         List var5 = autocompleteFromCollection(var2.usedNames.values(), (var0) -> {
            return true;
         }, String::valueOf, var4);
         var5.addAll(autocompleteFromSet(var2.usedNames.keySet(), (var0) -> {
            return true;
         }, String::valueOf, var4));
         return var5;
      } else {
         return Collections.emptyList();
      }
   }

   public StoredPlayer parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      try {
         long var6 = Long.parseLong(var4);
         if (var2.usedNames.containsKey(var6)) {
            return new StoredPlayer(var2, (String)var2.usedNames.get(var6), var6);
         }
      } catch (NumberFormatException var10) {
      }

      Iterator var11 = var2.usedNames.keySet().iterator();

      long var7;
      String var9;
      do {
         if (!var11.hasNext()) {
            throw new IllegalArgumentException("Could not find player with name/auth \"" + var4 + "\"");
         }

         var7 = (Long)var11.next();
         var9 = (String)var2.usedNames.get(var7);
      } while(!var9.equalsIgnoreCase(var4) && !String.valueOf(var7).equals(var4));

      return new StoredPlayer(var2, var9, var7);
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public StoredPlayer getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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

   public static class StoredPlayer {
      public long authentication;
      public String name;
      public WorldFile file;
      public WorldFile mapFile;

      public StoredPlayer(Server var1, String var2, long var3) {
         this.name = var2;
         this.authentication = var3;
         this.file = var1.world.fileSystem.getPlayerFile(var3);
         this.mapFile = var1.world.fileSystem.getMapPlayerFile(var3);
      }
   }
}
