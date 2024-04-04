package necesse.inventory;

public class ItemCombineResult {
   public boolean success;
   public String error;

   private ItemCombineResult(boolean var1, String var2) {
      this.success = var1;
      this.error = var2;
   }

   public static ItemCombineResult success() {
      return new ItemCombineResult(true, (String)null);
   }

   public static ItemCombineResult failure(String var0) {
      return new ItemCombineResult(false, var0);
   }

   public static ItemCombineResult failure() {
      return failure((String)null);
   }
}
