package necesse.engine.commands;

public class CmdArgument {
   public final CmdParameter param;
   public final String arg;
   public final int argCount;

   public CmdArgument(CmdParameter var1, String var2, int var3) {
      this.param = var1;
      this.arg = var2;
      this.argCount = var3;
   }
}
