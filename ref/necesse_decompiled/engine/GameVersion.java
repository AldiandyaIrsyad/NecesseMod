package necesse.engine;

import java.util.Arrays;
import java.util.HashSet;

public class GameVersion {
   private static final HashSet<Character> ints = new HashSet(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
   public final String versionString;
   private final int[] versions;

   public GameVersion(String var1) {
      this.versionString = var1;
      String[] var2 = var1.split("\\.");
      this.versions = new int[var2.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         try {
            int var4;
            for(var4 = 0; var4 < var2[var3].length() && ints.contains(var2[var3].charAt(var4)); ++var4) {
            }

            this.versions[var3] = Integer.parseInt(var2[var3].substring(0, var4));
         } catch (NumberFormatException var5) {
            this.versions[var3] = Integer.MIN_VALUE;
         }
      }

   }

   public boolean isLaterThan(GameVersion var1) {
      int var2 = Math.max(this.versions.length, var1.versions.length);

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var3 >= this.versions.length) {
            return false;
         }

         if (var3 >= var1.versions.length) {
            return true;
         }

         if (this.versions[var3] < var1.versions[var3]) {
            return false;
         }

         if (this.versions[var3] > var1.versions[var3]) {
            return true;
         }
      }

      return false;
   }

   public boolean isLaterThan(String var1) {
      return this.isLaterThan(new GameVersion(var1));
   }

   public boolean isEarlierThan(GameVersion var1) {
      int var2 = Math.max(this.versions.length, var1.versions.length);

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var3 >= this.versions.length) {
            return true;
         }

         if (var3 >= var1.versions.length) {
            return false;
         }

         if (this.versions[var3] < var1.versions[var3]) {
            return true;
         }

         if (this.versions[var3] > var1.versions[var3]) {
            return false;
         }
      }

      return false;
   }

   public boolean isEarlierThan(String var1) {
      return this.isEarlierThan(new GameVersion(var1));
   }

   public String toString() {
      return this.versionString;
   }
}
