package necesse.engine.commands.parameterHandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MultiParameterHandler extends ParameterHandler<Object[]> {
   private ParameterHandler[] handlers;
   private int argsUsed;

   public MultiParameterHandler(ParameterHandler... var1) {
      this.handlers = var1;
      this.argsUsed = var1[0].getArgsUsed();
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      ArrayList var5 = new ArrayList();
      ParameterHandler[] var6 = this.handlers;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ParameterHandler var9 = var6[var8];
         Iterator var10 = var9.autocomplete(var1, var2, var3, var4).iterator();

         while(var10.hasNext()) {
            AutoComplete var11 = (AutoComplete)var10.next();
            if (var5.stream().noneMatch((var1x) -> {
               return var1x.equals(var11);
            })) {
               var5.add(var11);
            }
         }
      }

      return var5;
   }

   public Object[] parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      IllegalArgumentException var6 = null;
      Object[] var7 = new Object[this.handlers.length];
      boolean var8 = false;

      for(int var9 = 0; var9 < this.handlers.length; ++var9) {
         try {
            var7[var9] = this.handlers[var9].parse(var1, var2, var3, var4, var5);
            var8 = true;
         } catch (IllegalArgumentException var11) {
            if (var6 == null) {
               var6 = var11;
            }

            var7[var9] = this.handlers[var9].getDefault(var1, var2, var3, var5);
         }
      }

      if (!var8) {
         throw var6;
      } else {
         return var7;
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      ParameterHandler[] var6 = this.handlers;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ParameterHandler var9 = var6[var8];
         if (var9.tryParse(var1, var2, var3, var4, var5)) {
            return true;
         }
      }

      return false;
   }

   public Object[] getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      Object[] var5 = new Object[this.handlers.length];

      for(int var6 = 0; var6 < this.handlers.length; ++var6) {
         var5[var6] = this.handlers[var6].getDefault(var1, var2, var3, var4);
      }

      return var5;
   }

   public int getArgsUsed() {
      return this.argsUsed;
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
