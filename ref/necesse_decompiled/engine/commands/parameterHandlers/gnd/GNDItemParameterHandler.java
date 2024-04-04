package necesse.engine.commands.parameterHandlers.gnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class GNDItemParameterHandler<T extends GNDItem> extends ParameterHandler<GNDItem> {
   public static ArrayList<GNDItemParameterHandler<?>> itemParameterHandlers = new ArrayList();
   protected String prefix;

   public static MultiParameterHandler getMultiParameterHandler() {
      return new MultiParameterHandler((ParameterHandler[])itemParameterHandlers.toArray(new GNDItemParameterHandler[0]));
   }

   public static GNDItem getReturnedItem(Object var0) {
      Object[] var1 = (Object[])var0;
      Object[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 != null) {
            return (GNDItem)var5;
         }
      }

      return null;
   }

   public GNDItemParameterHandler(String var1) {
      if (var1.contains(":")) {
         throw new IllegalArgumentException("Prefix cannot contain semicolon");
      } else {
         this.prefix = var1;
      }
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      String var5 = this.getSecondArg(var4.arg);
      return var5 == null ? autocompleteFromCollection(Collections.singleton(this.prefix + ":"), (Function)null, (Function)null, var4) : this.autocompleteSecondArg(new CmdArgument(var4.param, var5, var4.argCount));
   }

   protected abstract List<AutoComplete> autocompleteSecondArg(CmdArgument var1);

   public T parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      String var6 = this.getSecondArg(var4);
      if (var6 == null) {
         throw new IllegalArgumentException("Invalid argument for GND " + this.prefix + " arg \"" + var4 + "\" for <" + var5.name + ">");
      } else if (var6.isEmpty()) {
         throw new IllegalArgumentException("Missing value GND " + this.prefix + " argument for <" + var5.name + ">");
      } else {
         return this.parseSecondArg(var6, var5);
      }
   }

   protected abstract T parseSecondArg(String var1, CmdParameter var2);

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      if (var4.isEmpty()) {
         return true;
      } else {
         String var6 = this.getSecondArg(var4);
         if (var6 == null) {
            return !autocompleteFromCollection(Collections.singleton(this.prefix), (Function)null, (Function)null, new CmdArgument(var5, var4, 1)).isEmpty();
         } else if (var6.isEmpty()) {
            return true;
         } else {
            try {
               this.parseSecondArg(var6, var5);
               return true;
            } catch (Exception var8) {
               return false;
            }
         }
      }
   }

   public abstract T getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4);

   protected String getSecondArg(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 == -1) {
         return null;
      } else {
         return !var1.substring(0, var2).equals(this.prefix) ? null : var1.substring(var2 + 1);
      }
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

   static {
      itemParameterHandlers.add(new GNDBooleanParameterHandler());
      itemParameterHandlers.add(new GNDByteParameterHandler());
      itemParameterHandlers.add(new GNDShortParameterHandler());
      itemParameterHandlers.add(new GNDIntParameterHandler());
      itemParameterHandlers.add(new GNDLongParameterHandler());
      itemParameterHandlers.add(new GNDFloatParameterHandler());
      itemParameterHandlers.add(new GNDDoubleParameterHandler());
      itemParameterHandlers.add(new GNDStringParameterHandler());
   }
}
