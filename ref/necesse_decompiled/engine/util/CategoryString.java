package necesse.engine.util;

public class CategoryString implements Comparable<CategoryString> {
   public static final String splitterRegex = "-";
   public final String[] categories;
   public final String name;

   public CategoryString(String[] var1, String var2) {
      this.categories = var1;
      this.name = var2;
   }

   public CategoryString(String var1) {
      String[] var2 = var1.split("-");
      this.categories = new String[var2.length - 1];
      System.arraycopy(var2, 0, this.categories, 0, this.categories.length);
      this.name = var2[var2.length - 1];
   }

   public CategoryString(String var1, String var2) {
      this.categories = getCategories(var1);
      this.name = var2;
   }

   public int compareTo(CategoryString var1) {
      int var3 = compareCategories(this.categories, var1.categories);
      return var3 == 0 ? this.name.compareTo(var1.name) : var3;
   }

   public String toString() {
      return getCategoryString(this.categories) + "-" + this.name;
   }

   public static int compareCategories(String[] var0, String[] var1) {
      for(int var2 = 0; var2 < var0.length || var2 >= var1.length; ++var2) {
         if (var2 >= var1.length && var2 < var0.length) {
            return 1;
         }

         if (var2 >= var0.length && var2 >= var1.length) {
            return 0;
         }

         String var3 = var0[var2];
         String var4 = var1[var2];
         int var5 = var3.compareTo(var4);
         if (var5 != 0) {
            return var5;
         }
      }

      return -1;
   }

   public static String[] getCategories(String var0) {
      return var0.split("-");
   }

   public static String getCategoryString(String[] var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append(var0[var2]);
         if (var2 < var0.length - 1) {
            var1.append("-");
         }
      }

      return var1.toString();
   }

   public static CategoryString[] toCategoryStrings(String[] var0) {
      CategoryString[] var1 = new CategoryString[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = new CategoryString(var0[var2]);
      }

      return var1;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((CategoryString)var1);
   }
}
