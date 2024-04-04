package necesse.engine.save;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SaveSerialize {
   public SaveSerialize() {
   }

   public static String serializeEnum(Enum var0) {
      return var0.toString();
   }

   public static <T extends Enum<T>> T unserializeEnum(Class<T> var0, String var1) {
      Enum[] var2 = (Enum[])var0.getEnumConstants();
      Enum[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Enum var6 = var3[var5];
         if (var6.toString().equals(var1)) {
            return var6;
         }
      }

      throw new IllegalArgumentException("No enum constant " + var0.getName() + "." + var1);
   }

   public static String serializePoint(Point var0) {
      return "[" + var0.x + ", " + var0.y + "]";
   }

   public static Point unserializePoint(String var0) {
      try {
         var0 = var0.substring(1, var0.length() - 1);
         if (var0.isEmpty()) {
            return new Point();
         } else {
            String[] var1 = var0.split(",");
            return new Point(Integer.parseInt(var1[0].trim()), Integer.parseInt(var1[1].trim()));
         }
      } catch (Exception var2) {
         System.err.println("Could not unserialize point: " + var0);
         throw new NullPointerException();
      }
   }

   public static String serializeDimension(Dimension var0) {
      return "[" + var0.width + ", " + var0.height + "]";
   }

   public static Dimension unserializeDimension(String var0) {
      try {
         var0 = var0.substring(1, var0.length() - 1);
         if (var0.isEmpty()) {
            return new Dimension();
         } else {
            String[] var1 = var0.split(",");
            return new Dimension(Integer.parseInt(var1[0].trim()), Integer.parseInt(var1[1].trim()));
         }
      } catch (Exception var2) {
         System.err.println("Could not unserialize point: " + var0);
         throw new NullPointerException();
      }
   }

   public static String serializeColor(Color var0) {
      return "[" + var0.getRed() + ", " + var0.getGreen() + ", " + var0.getBlue() + ", " + var0.getAlpha() + "]";
   }

   public static Color unserializeColor(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      String[] var1 = var0.split(",");

      try {
         return new Color(Integer.parseInt(var1[0].trim()), Integer.parseInt(var1[1].trim()), Integer.parseInt(var1[2].trim()), Integer.parseInt(var1[3].trim()));
      } catch (Exception var3) {
         System.err.println("Could not unserialize color: " + var0);
         throw new NullPointerException();
      }
   }

   public static <T> String serializeCollect(Collection<T> var0, Function<T, String> var1) {
      StringBuilder var2 = new StringBuilder("[");
      Iterator var3 = var0.iterator();

      for(boolean var4 = false; var3.hasNext(); var4 = true) {
         Object var5 = var3.next();
         if (var4) {
            var2.append(",");
         }

         var2.append((String)var1.apply(var5));
      }

      return var2.append("]").toString();
   }

   public static <T, V, L> L unserializeCollect(String var0, Function<String, T> var1, Collector<? super T, V, L> var2) {
      var0 = var0.substring(1, var0.length() - 1);
      Object var3 = var2.supplier().get();
      int var4 = 0;

      while(true) {
         int var5 = SaveComponent.indexOf(var0, ',', var4);
         if (var5 == -1) {
            var2.accumulator().accept(var3, var1.apply(var0.substring(var4)));
            return var2.finisher().apply(var3);
         }

         var2.accumulator().accept(var3, var1.apply(var0.substring(var4, var5)));
         var4 = var5 + 1;
      }
   }

   public static String serializeSafeStringCollection(Collection<String> var0) {
      return serializeCollect(var0, SaveComponent::toSafeData);
   }

   public static List<String> unserializeSafeStringCollection(String var0) {
      return (List)unserializeCollect(var0, SaveComponent::fromSafeData, Collectors.toList());
   }

   public static String serializeStringList(List<String> var0) {
      return Arrays.toString(var0.toArray());
   }

   public static List<String> unserializeStringList(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      if (var0.isEmpty()) {
         return new ArrayList();
      } else {
         String[] var1 = var0.split(", ");
         return new ArrayList(Arrays.asList(var1));
      }
   }

   public static String serializeStringHashSet(HashSet<String> var0) {
      return Arrays.toString(var0.toArray());
   }

   public static HashSet<String> unserializeStringHashSet(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      if (var0.isEmpty()) {
         return new HashSet();
      } else {
         String[] var1 = var0.split(", ");
         HashSet var2 = new HashSet();
         Collections.addAll(var2, var1);
         return var2;
      }
   }

   public static String serializeStringArray(String[] var0) {
      return Arrays.toString(var0);
   }

   public static String[] unserializeStringArray(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      return var0.split(", ");
   }

   public static String serializeShortArray(short[] var0) {
      return Arrays.toString(var0);
   }

   public static short[] unserializeShortArray(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      String[] var1 = var0.split(", ");
      short[] var2 = new short[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         try {
            var2[var3] = Short.parseShort(var1[var3]);
         } catch (Exception var5) {
            System.err.println("Could not parse short: " + var1[var3] + "  in string array.");
         }
      }

      return var2;
   }

   public static String serializeIntArray(int[] var0) {
      return Arrays.toString(var0);
   }

   public static int[] unserializeIntArray(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      if (var0.isEmpty()) {
         return new int[0];
      } else {
         String[] var1 = var0.split(", ");
         int[] var2 = new int[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
               var2[var3] = Integer.parseInt(var1[var3]);
            } catch (Exception var5) {
               System.err.println("Could not parse integer: " + var1[var3] + "  in string array.");
            }
         }

         return var2;
      }
   }

   public static String serializeByteArray(byte[] var0) {
      return Arrays.toString(var0);
   }

   public static byte[] unserializeByteArray(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      if (var0.isEmpty()) {
         return new byte[0];
      } else {
         String[] var1 = var0.split(", ");
         byte[] var2 = new byte[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
               var2[var3] = Byte.parseByte(var1[var3]);
            } catch (Exception var5) {
               System.err.println("Could not parse byte: " + var1[var3] + " in string array.");
            }
         }

         return var2;
      }
   }

   public static String serializeLongArray(long[] var0) {
      return Arrays.toString(var0);
   }

   public static long[] unserializeLongArray(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      if (var0.isEmpty()) {
         return new long[0];
      } else {
         String[] var1 = var0.split(", ");
         long[] var2 = new long[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
               var2[var3] = Long.parseLong(var1[var3]);
            } catch (Exception var5) {
               System.err.println("Could not parse long: " + var1[var3] + "  in string array.");
            }
         }

         return var2;
      }
   }

   public static String serializeBooleanArray(boolean[] var0) {
      return Arrays.toString(var0);
   }

   public static boolean[] unserializeBooleanArray(String var0) {
      var0 = var0.substring(1, var0.length() - 1);
      if (var0.isEmpty()) {
         return new boolean[0];
      } else {
         String[] var1 = var0.split(", ");
         boolean[] var2 = new boolean[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
               var2[var3] = Boolean.parseBoolean(var1[var3]);
            } catch (Exception var5) {
               System.err.println("Could not parse boolean: " + var1[var3] + "  in string array.");
            }
         }

         return var2;
      }
   }

   public static String serializeSmallBooleanArray(boolean[] var0) {
      StringBuilder var1 = new StringBuilder();
      boolean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         boolean var5 = var2[var4];
         var1.append(var5 ? "1" : "0");
      }

      return var1.toString();
   }

   public static boolean[] unserializeSmallBooleanArray(String var0) {
      boolean[] var1 = new boolean[var0.length()];

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         var1[var2] = var0.charAt(var2) != '0';
      }

      return var1;
   }
}
