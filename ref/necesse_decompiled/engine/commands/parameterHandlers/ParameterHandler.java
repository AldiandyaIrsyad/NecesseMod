package necesse.engine.commands.parameterHandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class ParameterHandler<T> {
   public ParameterHandler() {
   }

   public abstract List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4);

   public abstract T parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException;

   public abstract boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5);

   public abstract T getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4);

   public int getArgsUsed() {
      return 1;
   }

   public static <T> List<AutoComplete> autocompleteFromList(List<T> var0, Function<T, Boolean> var1, Function<T, String> var2, CmdArgument var3) {
      return autocompleteFromCollection(var0, var1, var2, var3);
   }

   public static <T> List<AutoComplete> autocompleteFromSet(Set<T> var0, Function<T, Boolean> var1, Function<T, String> var2, CmdArgument var3) {
      return autocompleteFromCollection(var0, var1, var2, var3);
   }

   public static <T> List<AutoComplete> autocompleteFromCollection(Collection<T> var0, Function<T, Boolean> var1, Function<T, String> var2, CmdArgument var3) {
      return autocompleteFromArray(var0.toArray(), var1, var2, var3);
   }

   public static <T> List<AutoComplete> autocompleteFromArray(T[] var0, Function<T, Boolean> var1, Function<T, String> var2, CmdArgument var3) {
      if (var1 == null) {
         var1 = (var0x) -> {
            return true;
         };
      }

      if (var2 == null) {
         var2 = Object::toString;
      }

      ArrayList var4;
      Object[] var5;
      int var6;
      int var7;
      Object var8;
      if (var3.arg.length() == 0) {
         var4 = new ArrayList();
         var5 = var0;
         var6 = var0.length;

         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            if (var8 != null && (Boolean)var1.apply(var8)) {
               AutoComplete var11 = new AutoComplete(var3.argCount, (String)var2.apply(var8));
               if (!var4.contains(var11)) {
                  var4.add(var11);
               }
            }
         }

         return var4;
      } else {
         var4 = new ArrayList();
         var5 = var0;
         var6 = var0.length;

         String var9;
         AutoComplete var10;
         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            if (var8 != null && (Boolean)var1.apply(var8)) {
               var9 = (String)var2.apply(var8);
               if (var9.toLowerCase().startsWith(var3.arg.toLowerCase())) {
                  var10 = new AutoComplete(var3.argCount, var9);
                  if (!var4.contains(var10)) {
                     var4.add(var10);
                  }
               }
            }
         }

         var5 = var0;
         var6 = var0.length;

         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            if (var8 != null && (Boolean)var1.apply(var8)) {
               var9 = (String)var2.apply(var8);
               var10 = new AutoComplete(var3.argCount, var9);
               if (!var4.contains(var10) && var9.toLowerCase().contains(var3.arg.toLowerCase())) {
                  var4.add(var10);
               }
            }
         }

         return var4;
      }
   }
}
