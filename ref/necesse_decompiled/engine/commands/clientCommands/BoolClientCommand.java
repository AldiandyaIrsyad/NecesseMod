package necesse.engine.commands.clientCommands;

import java.lang.reflect.Field;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;

public class BoolClientCommand extends ModularChatCommand {
   private final BoolCommandLogic logic;

   public BoolClientCommand(String var1, String var2, PermissionLevel var3, String var4, BoolCommandLogic var5) {
      super(var1, var2, var3, false, new CmdParameter(var4, new PresetStringParameterHandler(true, new String[]{"1", "0", "default"}), true, new CmdParameter[0]));
      this.logic = var5;
   }

   public BoolClientCommand(String var1, String var2, PermissionLevel var3, String var4, Field var5, Object var6) {
      this(var1, var2, var3, var4, (var2x, var3x, var4x) -> {
         try {
            Object var5x = var6 == null ? var2x : var6;
            var5.setBoolean(var5x, var4x.result(var5.getBoolean(var5x)));
            var3x.add(var5.getName() + ": " + var5.getBoolean(var5x));
         } catch (IllegalAccessException var6x) {
            var3x.add("Cannot access field");
         }

      });
   }

   public BoolClientCommand(String var1, String var2, PermissionLevel var3, BoolCommandLogic var4) {
      this(var1, var2, var3, "1/0", var4);
   }

   public BoolClientCommand(String var1, String var2, PermissionLevel var3, Field var4, Object var5) {
      this(var1, var2, var3, "1/0", var4, var5);
   }

   public static BoolClientCommand create(String var0, String var1, PermissionLevel var2, String var3, Class var4, String var5, Object var6) {
      try {
         return new BoolClientCommand(var0, var1, var2, var3, var4.getField(var5), var6);
      } catch (NoSuchFieldException var8) {
         var8.printStackTrace();
         return null;
      }
   }

   public static BoolClientCommand create(String var0, String var1, PermissionLevel var2, Class var3, String var4, Object var5) {
      return create(var0, var1, var2, "1/0", var3, var4, var5);
   }

   public static BoolClientCommand create(String var0, String var1, PermissionLevel var2, String var3, GameObject var4, String var5) {
      try {
         return new BoolClientCommand(var0, var1, var2, var3, var4.getClass().getField(var5), var4);
      } catch (NoSuchFieldException var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public static BoolClientCommand create(String var0, String var1, PermissionLevel var2, GameObject var3, String var4) {
      return create(var0, var1, var2, "1/0", var3, var4);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      BoolCommandResult var8;
      if (var7.equals("default")) {
         var8 = new BoolCommandResult(false, false);
      } else {
         var8 = new BoolCommandResult(true, var7.equals("1"));
      }

      this.logic.apply(var1, var6, var8);
   }

   @FunctionalInterface
   public interface BoolCommandLogic {
      void apply(Client var1, CommandLog var2, BoolCommandResult var3);
   }

   public static class BoolCommandResult {
      public final boolean resultGiven;
      public final boolean result;

      private BoolCommandResult(boolean var1, boolean var2) {
         this.resultGiven = var1;
         this.result = var2;
      }

      public boolean result(boolean var1) {
         if (!this.resultGiven) {
            return !var1;
         } else {
            return this.result;
         }
      }

      // $FF: synthetic method
      BoolCommandResult(boolean var1, boolean var2, Object var3) {
         this(var1, var2);
      }
   }
}
