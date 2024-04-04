package necesse.engine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GameRandom extends Random {
   public final boolean canSeed;
   private boolean isSeeded;
   private static GameRandom uniqueRandom = new GameRandom();
   public static GameRandom globalRandom = new GameRandom(false);
   private static int[] primes = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013, 1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087, 1091, 1093, 1097, 1103, 1109, 1117, 1123, 1129, 1151, 1153, 1163, 1171, 1181, 1187, 1193, 1201, 1213, 1217, 1223, 1229, 1231, 1237, 1249, 1259, 1277, 1279, 1283, 1289, 1291, 1297, 1301, 1303, 1307, 1319, 1321, 1327, 1361, 1367, 1373, 1381, 1399, 1409, 1423, 1427, 1429, 1433, 1439, 1447, 1451, 1453, 1459, 1471, 1481, 1483, 1487, 1489, 1493, 1499, 1511, 1523, 1531, 1543, 1549, 1553, 1559, 1567, 1571, 1579, 1583, 1597, 1601, 1607, 1609, 1613, 1619, 1621, 1627, 1637, 1657, 1663, 1667, 1669, 1693, 1697, 1699, 1709, 1721, 1723, 1733, 1741, 1747, 1753, 1759, 1777, 1783, 1787, 1789, 1801, 1811, 1823, 1831, 1847, 1861, 1867, 1871, 1873, 1877, 1879, 1889, 1901, 1907, 1913, 1931, 1933, 1949, 1951, 1973, 1979, 1987, 1993, 1997, 1999, 2003, 2011, 2017, 2027, 2029, 2039, 2053, 2063, 2069, 2081, 2083, 2087, 2089, 2099, 2111, 2113, 2129, 2131, 2137, 2141, 2143, 2153, 2161, 2179, 2203, 2207, 2213, 2221, 2237, 2239, 2243, 2251, 2267, 2269, 2273, 2281, 2287, 2293, 2297, 2309, 2311, 2333, 2339, 2341, 2347, 2351, 2357, 2371, 2377, 2381, 2383, 2389, 2393, 2399, 2411, 2417, 2423, 2437, 2441, 2447, 2459, 2467, 2473, 2477, 2503, 2521, 2531, 2539, 2543, 2549, 2551, 2557, 2579, 2591, 2593, 2609, 2617, 2621, 2633, 2647, 2657, 2659, 2663, 2671, 2677, 2683, 2687, 2689, 2693, 2699, 2707, 2711, 2713, 2719, 2729, 2731, 2741};

   public GameRandom(boolean var1) {
      this.canSeed = var1;
   }

   public GameRandom() {
      this(true);
   }

   public GameRandom(long var1, boolean var3) {
      super(var1);
      this.canSeed = var3;
   }

   public GameRandom(long var1) {
      this(var1, true);
   }

   public synchronized void setSeed(long var1) {
      if (!this.canSeed && this.isSeeded) {
         throw new IllegalArgumentException("GameRandom already initialized.");
      } else {
         super.setSeed(var1);
         this.isSeeded = true;
      }
   }

   public synchronized GameRandom seeded(long var1) {
      this.setSeed(var1);
      return this;
   }

   public synchronized GameRandom appendSeed(long var1) {
      this.setSeed(this.nextLong() * var1);
      return this;
   }

   public GameRandom nextSeeded() {
      return nextSeeded(this);
   }

   public GameRandom nextSeeded(int var1) {
      return nextSeeded(this, var1);
   }

   @SafeVarargs
   public final <T> T getOneOf(Supplier<T>... var1) {
      return getOneOf(this, (Supplier[])var1);
   }

   @SafeVarargs
   public final <T> T getOneOf(T... var1) {
      return getOneOf(this, (Object[])var1);
   }

   public final <T> T getOneOf(List<T> var1) {
      return getOneOf(this, (List)var1);
   }

   public <T> T getOneOfWeighted(Class<T> var1, Object... var2) {
      return getOneOfWeighted(this, var1, var2);
   }

   @SafeVarargs
   public final <T> ArrayList<T> getCountOf(int var1, Supplier<T>... var2) {
      return getCountOf(this, var1, (Supplier[])var2);
   }

   @SafeVarargs
   public final <T> ArrayList<T> getCountOf(int var1, T... var2) {
      return getCountOf(this, var1, (Object[])var2);
   }

   public final <T> ArrayList<T> getCountOf(int var1, List<T> var2) {
      return getCountOf(this, var1, (List)var2);
   }

   public <T> ArrayList<T> getCountOfWeighted(int var1, Class<T> var2, Object... var3) {
      return getCountOfWeighted(this, var1, var2, var3);
   }

   public void runOneOf(Runnable... var1) {
      runOneOf(this, var1);
   }

   public float floatGaussian() {
      return floatGaussian(this);
   }

   public boolean getChance(double var1) {
      return getChance(this, var1);
   }

   public boolean getChance(float var1) {
      return getChance(this, var1);
   }

   public boolean getChance(int var1) {
      return getChance(this, var1);
   }

   public int getIntBetween(int var1, int var2) {
      return getIntBetween(this, var1, var2);
   }

   public int getIntOffset(int var1, int var2) {
      return getIntOffset(this, var1, var2);
   }

   public float getFloatBetween(float var1, float var2) {
      return getFloatBetween(this, var1, var2);
   }

   public float getFloatOffset(float var1, float var2) {
      return getFloatOffset(this, var1, var2);
   }

   public double getDoubleBetween(double var1, double var3) {
      return getDoubleBetween(this, var1, var3);
   }

   public double getDoubleOffset(double var1, double var3) {
      return getDoubleOffset(this, var1, var3);
   }

   public static int prime(int var0) {
      return primes[Math.floorMod(var0, primes.length)];
   }

   public static GameRandom nextSeeded(GameRandom var0) {
      return new GameRandom(var0.nextLong());
   }

   public static GameRandom nextSeeded(GameRandom var0, int var1) {
      return new GameRandom(var0.nextLong() * (long)prime(var1));
   }

   @SafeVarargs
   public static <T> T getOneOf(Random var0, Supplier<T>... var1) {
      if (var1.length == 0) {
         return null;
      } else {
         int var2 = var0.nextInt(var1.length);
         return var1[var2].get();
      }
   }

   @SafeVarargs
   public static <T> T getOneOf(Random var0, T... var1) {
      return var1.length == 0 ? null : var1[var0.nextInt(var1.length)];
   }

   public static <T> T getOneOf(Random var0, List<T> var1) {
      if (var1.isEmpty()) {
         return null;
      } else {
         int var2 = var0.nextInt(var1.size());
         return var1.get(var2);
      }
   }

   public static <T> T getOneOfWeighted(Random var0, Class<T> var1, Object... var2) {
      return parseItems(var1, var2).getRandomObject(var0);
   }

   public static void runOneOf(Random var0, Runnable... var1) {
      if (var1.length != 0) {
         int var2 = var0.nextInt(var1.length);
         var1[var2].run();
      }
   }

   @SafeVarargs
   public static <T> ArrayList<T> getCountOf(Random var0, int var1, Supplier<T>... var2) {
      ArrayList var3 = new ArrayList(var1);
      ArrayList var4 = new ArrayList(Arrays.asList(var2));

      for(int var5 = 0; var5 < var1 && !var4.isEmpty(); ++var5) {
         int var6 = var0.nextInt(var4.size());
         var3.add(((Supplier)var4.remove(var6)).get());
      }

      return var3;
   }

   @SafeVarargs
   public static <T> ArrayList<T> getCountOf(Random var0, int var1, T... var2) {
      return getCountOf(var0, var1, Arrays.asList(var2));
   }

   public static <T> ArrayList<T> getCountOf(Random var0, int var1, List<T> var2) {
      ArrayList var3 = new ArrayList(var1);
      ArrayList var4 = new ArrayList(var2);

      for(int var5 = 0; var5 < var1 && !var4.isEmpty(); ++var5) {
         int var6 = var0.nextInt(var4.size());
         var3.add(var4.remove(var6));
      }

      return var3;
   }

   public static <T> ArrayList<T> getCountOfWeighted(Random var0, int var1, Class<T> var2, Object... var3) {
      TicketSystemList var4 = parseItems(var2, var3);
      ArrayList var5 = new ArrayList(var1);

      for(int var6 = 0; var6 < var1; ++var6) {
         var5.add(var4.getAndRemoveRandomObject(var0));
      }

      return var5;
   }

   private static <T> TicketSystemList<T> parseItems(Class<T> var0, Object... var1) {
      TicketSystemList var2 = new TicketSystemList();
      boolean var3 = var0 == Integer.class;

      for(int var4 = 0; var4 < var1.length; var4 += 2) {
         Object var5 = var1[var4];
         int var6;
         Object var7;
         if (var3) {
            if (!(var5 instanceof Integer)) {
               throw new IllegalArgumentException("Could not cast " + var5 + " into an int or " + var0.getName());
            }

            var6 = (Integer)var5;
            if (var4 + 1 < var1.length && var0.isInstance(var1[var4 + 1])) {
               var7 = var0.cast(var1[var4 + 1]);
               var2.addObject(var6, var7);
            } else {
               --var4;
            }
         } else if (var0.isInstance(var5)) {
            Object var8 = var0.cast(var5);
            var2.addObject(100, var8);
            --var4;
         } else {
            if (!(var5 instanceof Integer)) {
               throw new IllegalArgumentException("Could not cast " + var5 + " into an int or " + var0.getName());
            }

            var6 = (Integer)var5;
            if (var4 + 1 < var1.length && var0.isInstance(var1[var4 + 1])) {
               var7 = var0.cast(var1[var4 + 1]);
               var2.addObject(var6, var7);
            } else {
               --var4;
            }
         }
      }

      return var2;
   }

   public static float floatGaussian(Random var0) {
      return (float)var0.nextGaussian();
   }

   public static boolean getChance(Random var0, double var1) {
      if (var1 >= 1.0) {
         return true;
      } else {
         return var0.nextDouble() < var1;
      }
   }

   public static boolean getChance(Random var0, float var1) {
      if (var1 >= 1.0F) {
         return true;
      } else {
         return var0.nextFloat() < var1;
      }
   }

   public static boolean getChance(Random var0, int var1) {
      return var1 == 0 ? getChance(var0, 1.0F) : getChance(var0, 1.0F / (float)var1);
   }

   public static int getIntBetween(Random var0, int var1, int var2) {
      if (var1 == var2) {
         return var1;
      } else {
         int var3 = Math.min(var1, var2);
         int var4 = Math.max(var1, var2);
         return var0.nextInt(var4 - var3 + 1) + var3;
      }
   }

   public static int getIntOffset(Random var0, int var1, int var2) {
      return getIntBetween(var0, var1 - var2, var1 + var2);
   }

   public static float getFloatBetween(Random var0, float var1, float var2) {
      if (var1 == var2) {
         return var1;
      } else {
         float var3 = Math.min(var1, var2);
         float var4 = Math.max(var1, var2);
         return var0.nextFloat() * (var4 - var3) + var3;
      }
   }

   public static float getFloatOffset(Random var0, float var1, float var2) {
      return getFloatBetween(var0, var1 - var2, var1 + var2);
   }

   public static double getDoubleBetween(Random var0, double var1, double var3) {
      if (var1 == var3) {
         return var1;
      } else {
         double var5 = Math.min(var1, var3);
         double var7 = Math.max(var1, var3);
         return var0.nextDouble() * (var7 - var5) + var5;
      }
   }

   public static double getDoubleOffset(Random var0, double var1, double var3) {
      return getDoubleBetween(var0, var1 - var3, var1 + var3);
   }

   public static int getNewUniqueID(Random var0) {
      if (var0 == null) {
         var0 = uniqueRandom;
      }

      return ((Random)var0).nextInt();
   }

   public static int getNewUniqueID() {
      return getNewUniqueID((Random)null);
   }
}
