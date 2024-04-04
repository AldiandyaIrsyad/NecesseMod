package necesse.gfx.fairType.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.gfx.fairType.FairGlyph;

public class TypeParserMatcherResult extends TypeParserResult {
   public final Matcher matcher;

   private TypeParserMatcherResult(Matcher var1, int var2, int var3) {
      super(var2, var3);
      this.matcher = var1;
   }

   public static TypeParserMatcherResult regexResult(FairGlyph[] var0, int var1, Pattern var2) {
      StringBuilder var3 = new StringBuilder();
      FairGlyph[] var4 = var0;
      int var5 = var0.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         FairGlyph var7 = var4[var6];
         if (var7.canBeParsed()) {
            var3.append(var7.getCharacter());
         } else {
            var3.append('\ufffe');
         }
      }

      Matcher var8 = var2.matcher(var3.toString());
      if (var8.find(var1)) {
         return new TypeParserMatcherResult(var8, var8.start(), var8.end());
      } else {
         return null;
      }
   }
}
