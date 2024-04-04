package necesse.engine.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;

public class CmdParameter {
   public final String name;
   public final ParameterHandler<?> param;
   public final boolean optional;
   public final boolean partOfUsage;
   public final CmdParameter[] extraParams;

   public CmdParameter(String var1, ParameterHandler<?> var2, boolean var3, boolean var4, CmdParameter... var5) {
      this.name = var1;
      this.param = var2;
      this.optional = var3;
      this.partOfUsage = var4;
      this.extraParams = var5;
   }

   public CmdParameter(String var1, ParameterHandler<?> var2, CmdParameter... var3) {
      this(var1, var2, true, true, var3);
   }

   public CmdParameter(String var1, ParameterHandler<?> var2, boolean var3, CmdParameter... var4) {
      this(var1, var2, var3, true, var4);
   }

   public CmdParameter(String var1, ParameterHandler<?> var2) {
      this(var1, var2, false);
   }

   public String getUsage() {
      if (!this.partOfUsage) {
         return null;
      } else {
         String var1 = "";
         if (this.optional) {
            var1 = var1 + "[";
         }

         var1 = var1 + "<" + this.name + ">";
         if (this.extraParams.length > 0) {
            String var2 = getUsage(this.extraParams);
            if (!var2.isEmpty()) {
               var1 = var1 + " " + var2;
            }
         }

         if (this.optional) {
            var1 = var1 + "]";
         }

         return var1;
      }
   }

   public boolean parse(Client var1, Server var2, ServerClient var3, ArrayList<String> var4, ArrayList<Object> var5, ArrayList<String> var6, ArgCounter var7, CommandLog var8) {
      try {
         if (var4.isEmpty()) {
            var6.add((Object)null);
            if (this.optional) {
               this.addDefaults(var1, var2, var3, var5, var6);
               return true;
            } else {
               var8.add("Missing argument <" + this.name + ">");
               return false;
            }
         } else {
            String var9 = "";
            int var10 = this.param.getArgsUsed();

            for(int var11 = 0; var11 < var10; ++var11) {
               if (var11 >= var4.size()) {
                  var10 = var11;
                  break;
               }

               var9 = var9 + (String)var4.get(var11);
               if (var11 < var10 - 1) {
                  var9 = var9 + " ";
               }
            }

            Object var17 = this.param.parse(var1, var2, var3, var9, this);
            var7.currentArg += var10;
            var7.currentParam += this.countParameters();

            for(int var12 = 0; var12 < var10; ++var12) {
               var4.remove(0);
            }

            var5.add(var17);
            var6.add((Object)null);
            CmdParameter[] var18 = this.extraParams;
            int var13 = var18.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               CmdParameter var15 = var18[var14];
               if (!var15.parse(var1, var2, var3, var4, var5, var6, var7, var8)) {
                  return false;
               }
            }

            return true;
         }
      } catch (IllegalArgumentException var16) {
         var6.add(var16.getMessage());
         if (this.optional) {
            if (var7.params - var7.currentParam - this.countParameters(true) <= var7.totalArgs - var7.currentArg) {
               var8.add(var16.getMessage());
               return false;
            } else {
               var7.currentParam += this.countParameters();
               this.addDefaults(var1, var2, var3, var5, var6);
               return true;
            }
         } else {
            var8.add(var16.getMessage());
            return false;
         }
      }
   }

   private void addDefaults(Client var1, Server var2, ServerClient var3, ArrayList<Object> var4, ArrayList<String> var5) {
      var4.add(this.param.getDefault(var1, var2, var3, this));
      CmdParameter[] var6 = this.extraParams;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         CmdParameter var9 = var6[var8];
         var5.add((Object)null);
         var9.addDefaults(var1, var2, var3, var4, var5);
      }

   }

   public ArrayList<CmdArgument> getTypingParameters(Client var1, Server var2, ServerClient var3, String[] var4, UsageCounter var5) {
      ArrayList var6 = new ArrayList();
      boolean var7 = false;

      for(int var8 = var5.optionalArgs; var8 >= 0; --var8) {
         int var9 = var5.currentArg - var8;
         int var10 = this.param.getArgsUsed();
         if (var9 < var4.length) {
            String var11 = "";

            for(int var12 = 0; var12 < var10 + var8; ++var12) {
               int var13 = var9 + var12;
               if (var13 >= var4.length) {
                  var10 = var12;
                  break;
               }

               var11 = var11 + var4[var13] + " ";
            }

            if (var11.endsWith(" ")) {
               var11 = var11.substring(0, var11.length() - 1);
            }

            if (this.param.tryParse(var1, var2, var3, var11, this)) {
               var5.currentArg += var10;
               if (this.optional) {
                  var5.optionalArgs += var10;
               }

               if (var5.currentArg < var4.length) {
                  var6.addAll(getCurrentArguments(var1, var2, var3, this.extraParams, var4, var5));
                  var7 = false;
               } else {
                  if (!this.optional) {
                     var7 = true;
                  }

                  var6.add(new CmdArgument(this, var11, var10));
               }
               break;
            }
         }
      }

      if (var7) {
         var5.currentArg = 1000000;
      }

      return var6;
   }

   public int countParameters() {
      return this.countParameters(false);
   }

   public int countParameters(boolean var1) {
      if (var1 && this.optional) {
         return 0;
      } else {
         int var2 = this.param.getArgsUsed();
         CmdParameter[] var3 = this.extraParams;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CmdParameter var6 = var3[var5];
            var2 += var6.countParameters(var1);
         }

         return var2;
      }
   }

   public static String getUsage(CmdParameter[] var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         String var3 = var0[var2].getUsage();
         if (var3 != null && var3.length() > 0) {
            if (var2 > 0) {
               var1.append(" ");
            }

            var1.append(var3);
         }
      }

      return var1.toString();
   }

   public static ArrayList<CmdArgument> getCurrentArguments(Client var0, Server var1, ServerClient var2, CmdParameter[] var3, String[] var4, UsageCounter var5) {
      ArrayList var6 = new ArrayList();
      CmdParameter[] var7 = var3;
      int var8 = var3.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         CmdParameter var10 = var7[var9];
         var6.addAll(var10.getTypingParameters(var0, var1, var2, var4, var5));
      }

      return var6;
   }

   public static List<AutoComplete> autoComplete(Client var0, Server var1, ServerClient var2, CmdParameter[] var3, String[] var4) {
      UsageCounter var5 = new UsageCounter();
      ArrayList var6 = new ArrayList();
      Iterator var7 = getCurrentArguments(var0, var1, var2, var3, var4, var5).iterator();

      while(var7.hasNext()) {
         CmdArgument var8 = (CmdArgument)var7.next();
         var6.addAll(var8.param.param.autocomplete(var0, var1, var2, var8));
      }

      return var6;
   }

   public static String getCurrentUsage(ChatCommand var0, Client var1, Server var2, ServerClient var3, CmdParameter[] var4, String[] var5) {
      UsageCounter var6 = new UsageCounter();
      LinkedList var7 = new LinkedList();
      Iterator var8 = getCurrentArguments(var1, var2, var3, var4, var5, var6).iterator();

      while(var8.hasNext()) {
         CmdArgument var9 = (CmdArgument)var8.next();
         var7.add(var9.param);
      }

      return "Usage: /" + var0.name + getCurrentUsage(var4, var7);
   }

   public static String getCurrentUsage(CmdParameter[] var0, Collection<CmdParameter> var1) {
      StringBuilder var2 = new StringBuilder();
      CmdParameter[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         CmdParameter var6 = var3[var5];
         StringBuilder var7 = new StringBuilder();
         if (var1 != null && var1.contains(var6)) {
            var7.append(GameColor.YELLOW.getColorCode()).append(var6.name).append(GameColor.NO_COLOR.getColorCode());
         } else {
            var7.append(var6.name);
         }

         var7.append(getCurrentUsage(var6.extraParams, var1));
         if (!var2.toString().endsWith(" ")) {
            var2.append(" ");
         }

         if (var6.optional) {
            var2.append("[").append(var7).append("]");
         } else {
            var2.append(var7);
         }
      }

      return var2.toString();
   }

   public static class ArgCounter {
      public final int params;
      public final int totalArgs;
      public int currentArg;
      public int currentParam;

      public ArgCounter(int var1, int var2) {
         this.params = var1;
         this.totalArgs = var2;
         this.currentArg = 0;
      }
   }

   public static class UsageCounter {
      public int currentArg;
      public int optionalArgs;

      public UsageCounter() {
      }
   }
}
