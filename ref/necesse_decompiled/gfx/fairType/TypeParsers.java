package necesse.gfx.fairType;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import necesse.engine.control.Control;
import necesse.engine.control.Input;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.parsers.TypeItemParser;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserMatcherResult;
import necesse.gfx.fairType.parsers.TypeParserResult;
import necesse.gfx.fairType.parsers.TypeRegexParser;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;

public class TypeParsers {
   public static final TypeRegexParser GAME_COLOR = new TypeRegexParser("\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))") {
      public FairGlyph[] parse(TypeParserMatcherResult var1, FairGlyph[] var2) {
         String var3 = var1.matcher.group();
         Supplier var4 = GameColor.parseColorSupplierString(var3);
         return new FairGlyph[]{new FairColorChangeGlyph(var3, var4)};
      }

      // $FF: synthetic method
      // $FF: bridge method
      public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
         return this.parse((TypeParserMatcherResult)var1, var2);
      }
   };
   public static final TypeRegexParser STRIP_GAME_COLOR = new TypeRegexParser("\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))") {
      public FairGlyph[] parse(TypeParserMatcherResult var1, FairGlyph[] var2) {
         return new FairGlyph[0];
      }

      // $FF: synthetic method
      // $FF: bridge method
      public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
         return this.parse((TypeParserMatcherResult)var1, var2);
      }
   };
   private static final Pattern URL_PATTERN = Pattern.compile("(?i)\\b((?:https?|ftp)://)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?\\b");
   public static final TypeRegexParser URL_OPEN;
   public static final TypeParser<TypeParserResult> REMOVE_URL;
   private static final Pattern MARKDOWN_URL_PATTERN;
   public static final TypeRegexParser MARKDOWN_URL;
   public static final Pattern INPUT_PATTERN;

   public TypeParsers() {
   }

   public static TypeItemParser ItemIcon(int var0) {
      return new TypeItemParser(var0);
   }

   public static TypeItemParser ItemIcon(int var0, boolean var1) {
      return new TypeItemParser(var0, var1);
   }

   public static String getItemParseString(InventoryItem var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append("[item=").append(var0.item.getStringID()).append("]");
      if (var0.getGndData().getMapSize() > 0) {
         SaveData var2 = new SaveData("");
         var0.getGndData().addSaveData(var2);
         var1.append(var2.getScript(true));
      }

      return var1.toString();
   }

   public static String getItemsParseString(List<InventoryItem> var0) {
      String var1 = GameUtils.join((InventoryItem[])var0.toArray(new InventoryItem[0]), (Function)((var0x) -> {
         String var1 = null;
         if (var0x.getGndData().getMapSize() > 0) {
            SaveData var2 = new SaveData("");
            var0x.getGndData().addSaveData(var2);
            var1 = var2.getScript(true);
         }

         return var0x.item.getStringID() + (var1 == null ? "" : var1);
      }), ",");
      return "[items=" + var1 + "]";
   }

   public static TypeRegexParser InputIcon(final FontOptions var0) {
      return new TypeRegexParser(INPUT_PATTERN) {
         public FairGlyph[] parse(TypeParserMatcherResult var1, FairGlyph[] var2) {
            String var3 = var1.matcher.group(1);
            if (var3 != null) {
               try {
                  int var7 = Integer.parseInt(var3);
                  String var5 = Input.getName(var7);
                  return var5.equals("N/A") ? var2 : new FairGlyph[]{new FairInputKeyGlyph(var0, var7, var5, (String)null)};
               } catch (NumberFormatException var6) {
                  return var2;
               }
            } else {
               Control var4 = Control.getControl(var1.matcher.group(2));
               return var4 != null ? new FairGlyph[]{new FairControlKeyGlyph(var0, var4, (String)null)} : var2;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
            return this.parse((TypeParserMatcherResult)var1, var2);
         }
      };
   }

   public static String getInputParseString(int var0) {
      return "[input=" + var0 + "]";
   }

   public static String getInputParseString(Control var0) {
      return "[input=" + var0.id + "]";
   }

   public static TypeParser<TypeParserResult> replaceParser(final String var0, final FairGlyph... var1) {
      return new TypeParser<TypeParserResult>() {
         public TypeParserResult getMatchResult(FairGlyph[] var1x, int var2) {
            StringBuilder var3 = new StringBuilder();
            FairGlyph[] var4 = var1x;
            int var5 = var1x.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               FairGlyph var7 = var4[var6];
               var3.append(var7.getCharacter());
            }

            int var8 = var3.toString().indexOf(var0);
            return var8 == -1 ? null : new TypeParserResult(var8, var8 + var0.length());
         }

         public FairGlyph[] parse(TypeParserResult var1x, FairGlyph[] var2) {
            return var1;
         }
      };
   }

   public static TypeParser<TypeParserResult> headerParser(final String var0, final String var1, final boolean var2, final FontOptions var3) {
      return new TypeParser<TypeParserResult>() {
         public TypeParserResult getMatchResult(FairGlyph[] var1x, int var2x) {
            int var3x = TypeParsers.getIndexOf(var1x, var0, var2x);
            if (var3x == -1) {
               return null;
            } else {
               int var4 = TypeParsers.getIndexOf(var1x, var1, var3x + var0.length());
               return var4 == -1 ? new TypeParserResult(var3x, var1x.length) : new TypeParserResult(var3x, var4 + var1.length());
            }
         }

         public FairGlyph[] parse(TypeParserResult var1x, FairGlyph[] var2x) {
            FairGlyph[] var3x = new FairGlyph[var2x.length - var0.length() - (var2 ? var1.length() : 0)];

            for(int var4 = 0; var4 < var3x.length; ++var4) {
               var3x[var4] = new FairCharacterGlyph(var3, var2x[var4 + var0.length()].getCharacter());
            }

            return var3x;
         }
      };
   }

   private static int getIndexOf(FairGlyph[] var0, String var1, int var2) {
      for(int var3 = var2; var3 < var0.length; ++var3) {
         boolean var4 = true;

         for(int var5 = 0; var5 < var1.length(); ++var5) {
            if (var3 + var5 >= var0.length || var0[var3 + var5].getCharacter() != var1.charAt(var5)) {
               var4 = false;
               break;
            }
         }

         if (var4) {
            return var3;
         }
      }

      return -1;
   }

   static {
      URL_OPEN = new TypeRegexParser(URL_PATTERN) {
         public FairGlyph[] parse(TypeParserMatcherResult var1, FairGlyph[] var2) {
            String var3 = var1.matcher.group();
            if (var1.matcher.groupCount() > 0 && var1.matcher.group(1) == null) {
               var3 = "http://" + var3;
            }

            try {
               URI var4 = URI.create(var3);
               FairGlyph[] var5 = new FairGlyph[var2.length];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  if (var2[var6] instanceof FairURLLinkGlyph) {
                     FairURLLinkGlyph var7 = (FairURLLinkGlyph)var2[var6];
                     var5[var6] = new FairURLLinkGlyph(var7.glyph, var4, true);
                  } else {
                     var5[var6] = new FairURLLinkGlyph(var2[var6], var4, true);
                  }
               }

               return var5;
            } catch (Exception var8) {
               var8.printStackTrace();
               return var2;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
            return this.parse((TypeParserMatcherResult)var1, var2);
         }
      };
      REMOVE_URL = new TypeParser<TypeParserResult>() {
         public TypeParserResult getMatchResult(FairGlyph[] var1, int var2) {
            for(int var3 = var2; var3 < var1.length; ++var3) {
               if (var1[var3] instanceof FairURLLinkGlyph) {
                  int var4;
                  for(var4 = 1; var3 + var4 < var1.length && var1[var3 + var4] instanceof FairURLLinkGlyph && ((FairURLLinkGlyph)var1[var3 + var4]).parsedGlyph; ++var4) {
                  }

                  return new TypeParserResult(var3, var3 + var4);
               }
            }

            return null;
         }

         public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
            FairGlyph[] var3 = new FairGlyph[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
               var3[var4] = ((FairURLLinkGlyph)var2[var4]).glyph;
            }

            return var3;
         }
      };
      MARKDOWN_URL_PATTERN = Pattern.compile("\\[(.+)]\\((" + URL_PATTERN.toString() + ")\\)");
      MARKDOWN_URL = new TypeRegexParser(MARKDOWN_URL_PATTERN) {
         public FairGlyph[] parse(TypeParserMatcherResult var1, FairGlyph[] var2) {
            String var3 = var1.matcher.group(1);
            String var4 = var1.matcher.group(2);
            if (var1.matcher.groupCount() > 2 && var1.matcher.group(3) == null) {
               var4 = "http://" + var4;
            }

            try {
               int var5 = var1.matcher.start(1) - var1.start;
               URI var6 = URI.create(var4);
               FairGlyph[] var7 = new FairGlyph[var3.length()];

               for(int var8 = 0; var8 < var7.length; ++var8) {
                  FairGlyph var9 = var2[var8 + var5];
                  if (var9 instanceof FairURLLinkGlyph) {
                     FairURLLinkGlyph var10 = (FairURLLinkGlyph)var9;
                     var7[var8] = new FairURLLinkGlyph(var10.glyph, var6, false);
                  } else {
                     var7[var8] = new FairURLLinkGlyph(var9, var6, false);
                  }
               }

               return var7;
            } catch (Exception var11) {
               var11.printStackTrace();
               return var2;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
            return this.parse((TypeParserMatcherResult)var1, var2);
         }
      };
      INPUT_PATTERN = Pattern.compile("\\[input=(?:(-?\\d+)|(\\w+))]");
   }
}
