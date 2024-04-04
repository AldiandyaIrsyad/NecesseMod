package necesse.gfx;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.util.GameMath;

public enum GameColor {
   NO_COLOR('0', (Supplier)null),
   WHITE('1', new Color(250, 250, 250)),
   BLACK('2', new Color(0, 0, 0)),
   LIGHT_GRAY('3', new Color(200, 200, 200)),
   DARK_GRAY('4', new Color(50, 50, 50)),
   GRAY('5', new Color(150, 150, 150)),
   RED('6', new Color(200, 50, 50)),
   GREEN('7', new Color(50, 200, 50)),
   BLUE('8', new Color(50, 50, 200)),
   YELLOW('9', new Color(200, 200, 50)),
   CYAN('a', new Color(50, 200, 200)),
   PURPLE('b', new Color(200, 50, 200)),
   ITEM_NORMAL('c', new Color(250, 250, 250)),
   ITEM_COMMON('d', new Color(25, 230, 27)),
   ITEM_UNCOMMON('e', new Color(65, 96, 224)),
   ITEM_RARE('f', new Color(229, 230, 25)),
   ITEM_EPIC('g', new Color(216, 39, 211)),
   ITEM_LEGENDARY('h', new Color(220, 60, 60)),
   ITEM_QUEST('i', new Color(242, 128, 13)),
   ITEM_UNIQUE('j', () -> {
      float var0 = 0.8472222F;
      float var1 = 0.9444444F;
      float var2 = (float)(System.currentTimeMillis() % 3000L) / 3000.0F;
      float var3 = GameMath.sin(var2 * 360.0F);
      return new Color(Color.HSBtoRGB(var0 + (var1 - var0) * var3, 1.0F, 1.0F));
   }),
   CUSTOM('#', (var0) -> {
      if (var0 == null) {
         return () -> {
            return Color.WHITE;
         };
      } else {
         Pattern var1 = Pattern.compile("[0-9a-fA-F]{6}");
         Matcher var2 = var1.matcher(var0);
         if (var2.find() && var2.start() == 0) {
            return () -> {
               return new Color(Integer.valueOf(var0.substring(0, 2), 16), Integer.valueOf(var0.substring(2, 4), 16), Integer.valueOf(var0.substring(4, 6), 16));
            };
         } else {
            var1 = Pattern.compile("[0-9a-fA-F]{3}");
            var2 = var1.matcher(var0);
            return var2.find() && var2.start() == 0 ? () -> {
               return new Color(Integer.valueOf(var0.substring(0, 1), 16) * 17, Integer.valueOf(var0.substring(1, 2), 16) * 17, Integer.valueOf(var0.substring(2, 3), 16) * 17);
            } : null;
         }
      }
   }),
   RAINBOW('!', () -> {
      long var0 = System.currentTimeMillis() % 2500L;
      return new Color(Color.HSBtoRGB((float)var0 / 2500.0F, 1.0F, 1.0F));
   });

   public static final Supplier<Color> DEFAULT_COLOR = () -> {
      return Color.WHITE;
   };
   public static final char CODE_PREFIX = '\u00a7';
   public static final String CODE_REGEX = "\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))";
   public final char codeChar;
   public final Supplier<Color> color;
   private final Function<String, Supplier<Color>> colorParser;

   private GameColor(char var3, Color var4) {
      this.codeChar = var3;
      this.colorParser = (var1x) -> {
         return () -> {
            return var4;
         };
      };
      this.color = () -> {
         return var4;
      };
   }

   private GameColor(char var3, Function var4) {
      this.codeChar = var3;
      this.colorParser = var4;
      this.color = () -> {
         return (Color)((Supplier)var4.apply((Object)null)).get();
      };
   }

   private GameColor(char var3, Supplier var4) {
      this.codeChar = var3;
      this.color = var4;
      this.colorParser = (var1x) -> {
         return var4;
      };
   }

   public String getColorCode() {
      return "\u00a7" + this.codeChar;
   }

   public int getID() {
      return this.ordinal();
   }

   public static GameColor getGameColor(int var0) {
      GameColor[] var1 = values();
      return var0 >= 0 && var0 < var1.length ? var1[var0] : WHITE;
   }

   public static Supplier<Color> parseColorSupplierString(String var0) {
      if (var0.length() < 2) {
         return null;
      } else {
         if (var0.charAt(0) == 167) {
            char var1 = var0.charAt(1);
            GameColor[] var2 = values();
            GameColor[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               GameColor var6 = var3[var5];
               if (var6.codeChar == var1) {
                  return (Supplier)var6.colorParser.apply(var0.substring(2));
               }
            }
         }

         return null;
      }
   }

   public static String getCustomColorCode(Color var0) {
      if (var0 == null) {
         return NO_COLOR.getColorCode();
      } else {
         StringBuilder var1 = new StringBuilder(Integer.toHexString(var0.getRed()));

         while(var1.length() < 2) {
            var1.insert(0, "0");
         }

         StringBuilder var2 = new StringBuilder(Integer.toHexString(var0.getGreen()));

         while(var2.length() < 2) {
            var2.insert(0, "0");
         }

         StringBuilder var3 = new StringBuilder(Integer.toHexString(var0.getBlue()));

         while(var3.length() < 2) {
            var3.insert(0, "0");
         }

         return "\u00a7#" + var1 + var2 + var3;
      }
   }

   public static Color parseColorString(String var0) {
      Supplier var1 = parseColorSupplierString(var0);
      return var1 == null ? null : (Color)var1.get();
   }

   public static GameColor getGameColor(char var0) {
      GameColor[] var1 = values();
      GameColor[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameColor var5 = var2[var4];
         if (var5 != null && var5.codeChar == var0) {
            return var5;
         }
      }

      return null;
   }

   public static String stripCodes(String var0) {
      return var0.replaceAll("\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))", "");
   }

   // $FF: synthetic method
   private static GameColor[] $values() {
      return new GameColor[]{NO_COLOR, WHITE, BLACK, LIGHT_GRAY, DARK_GRAY, GRAY, RED, GREEN, BLUE, YELLOW, CYAN, PURPLE, ITEM_NORMAL, ITEM_COMMON, ITEM_UNCOMMON, ITEM_RARE, ITEM_EPIC, ITEM_LEGENDARY, ITEM_QUEST, ITEM_UNIQUE, CUSTOM, RAINBOW};
   }
}
