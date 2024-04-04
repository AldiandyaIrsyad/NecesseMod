package necesse.level.gameObject;

public class TreeSaplingObject extends SaplingObject implements ForestrySaplingObject {
   public TreeSaplingObject(String var1, String var2, int var3, int var4, boolean var5, String... var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public TreeSaplingObject(String var1, String var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, var5);
   }

   public String getForestryResultObjectStringID() {
      return this.resultObjectStringID;
   }
}
