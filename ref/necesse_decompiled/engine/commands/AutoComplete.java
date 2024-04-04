package necesse.engine.commands;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class AutoComplete {
   public final int argsUsed;
   public final String newArgs;
   public final boolean ignoreWrap;

   public AutoComplete(int var1, String var2, boolean var3) {
      this.argsUsed = var1;
      this.newArgs = var2;
      this.ignoreWrap = var3;
   }

   public AutoComplete(int var1, String var2) {
      this(var1, var2, false);
   }

   public AutoComplete(PacketReader var1) {
      this.argsUsed = var1.getNextShort();
      this.newArgs = var1.getNextString();
      this.ignoreWrap = var1.getNextBoolean();
   }

   public Packet getContentPacket() {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextShort((short)this.argsUsed);
      var2.putNextString(this.newArgs);
      var2.putNextBoolean(this.ignoreWrap);
      return var1;
   }

   public String getFullCommand(String var1) {
      String[] var2 = ParsedCommand.parseArgs(var1);
      if (this.argsUsed > var2.length) {
         System.err.println("Tried to autocomplete command with too few arguments: \"" + var1 + "\" -" + this.argsUsed + " +" + this.newArgs);
      }

      String[] var3 = (String[])Arrays.copyOfRange(var2, 0, Math.max(0, var2.length - this.argsUsed));
      ArrayList var4 = new ArrayList(Arrays.asList(var3));
      var4.add(this.newArgs);
      StringBuilder var5 = new StringBuilder();

      for(int var6 = 0; var6 < var4.size(); ++var6) {
         if (var6 >= var4.size() - this.argsUsed && this.ignoreWrap) {
            var5.append((String)var4.get(var6));
         } else {
            var5.append(ParsedCommand.wrapArgument((String)var4.get(var6)));
         }

         if (var6 < var2.length - 1) {
            var5.append(" ");
         }
      }

      return var5.toString();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AutoComplete)) {
         return super.equals(var1);
      } else {
         AutoComplete var2 = (AutoComplete)var1;
         return this.argsUsed == var2.argsUsed && this.newArgs.equals(var2.newArgs) && this.ignoreWrap == var2.ignoreWrap;
      }
   }
}
