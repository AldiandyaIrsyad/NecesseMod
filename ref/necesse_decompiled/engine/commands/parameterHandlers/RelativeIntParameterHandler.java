package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class RelativeIntParameterHandler extends ParameterHandler<RelativeInt> {
   private Integer defaultValue;

   public static int handleRelativeInt(Object var0, int var1) {
      if (var0 instanceof RelativeInt) {
         RelativeInt var2 = (RelativeInt)var0;
         return var2.relativeFunction != null ? (Integer)var2.relativeFunction.apply(var1, var2.value) : var2.value;
      } else {
         throw new IllegalArgumentException("Object is not a relative int");
      }
   }

   public RelativeIntParameterHandler() {
      this.defaultValue = 0;
   }

   public RelativeIntParameterHandler(Integer var1) {
      this.defaultValue = var1;
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return Collections.emptyList();
   }

   public RelativeInt parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      try {
         if (!var4.startsWith("%")) {
            return new RelativeInt((BiFunction)null, Integer.parseInt(var4));
         } else {
            var4 = var4.substring(1);
            BiFunction var6 = Integer::sum;
            Operator[] var7 = RelativeIntParameterHandler.Operator.values();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Operator var10 = var7[var9];
               if (var4.startsWith(var10.prefix)) {
                  var6 = var10.calculate;
                  var4 = var4.substring(1);
                  break;
               }
            }

            return var4.isEmpty() ? new RelativeInt(var6, 0) : new RelativeInt(var6, Integer.parseInt(var4));
         }
      } catch (NumberFormatException var11) {
         throw new IllegalArgumentException((var4.isEmpty() ? "Argument" : var4) + " for <" + var5.name + "> is not a number");
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      if (var4.isEmpty()) {
         return true;
      } else {
         try {
            if (var4.startsWith("%")) {
               var4 = var4.substring(1);
               Operator[] var6 = RelativeIntParameterHandler.Operator.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  Operator var9 = var6[var8];
                  if (var4.startsWith(var9.prefix)) {
                     var4 = var4.substring(1);
                     break;
                  }
               }
            }

            if (var4.isEmpty()) {
               return true;
            } else {
               Integer.parseInt(var4);
               return true;
            }
         } catch (NumberFormatException var10) {
            return false;
         }
      }
   }

   public RelativeInt getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return new RelativeInt((BiFunction)null, this.defaultValue);
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

   public static class RelativeInt {
      public final BiFunction<Integer, Integer, Integer> relativeFunction;
      public final int value;

      public RelativeInt(BiFunction<Integer, Integer, Integer> var1, int var2) {
         this.relativeFunction = var1;
         this.value = var2;
      }
   }

   private static enum Operator {
      PLUS("+", Integer::sum),
      MINUS("-", (var0, var1) -> {
         return var0 - var1;
      }),
      MULTIPLY("*", (var0, var1) -> {
         return var0 * var1;
      }),
      DIVIDE("/", (var0, var1) -> {
         return var0 / var1;
      }),
      MODULO("%", (var0, var1) -> {
         return var0 % var1;
      }),
      POWER("^", (var0, var1) -> {
         return (int)Math.pow((double)var0, (double)var1);
      });

      public String prefix;
      public BiFunction<Integer, Integer, Integer> calculate;

      private Operator(String var3, BiFunction var4) {
         this.prefix = var3;
         this.calculate = var4;
      }

      // $FF: synthetic method
      private static Operator[] $values() {
         return new Operator[]{PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, POWER};
      }
   }
}
