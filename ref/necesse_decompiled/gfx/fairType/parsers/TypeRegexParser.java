package necesse.gfx.fairType.parsers;

import java.util.regex.Pattern;
import necesse.gfx.fairType.FairGlyph;

public abstract class TypeRegexParser extends TypeParser<TypeParserMatcherResult> {
   public final Pattern regex;

   public TypeRegexParser(Pattern var1) {
      this.regex = var1;
   }

   public TypeRegexParser(String var1) {
      this(Pattern.compile(var1));
   }

   public TypeParserMatcherResult getMatchResult(FairGlyph[] var1, int var2) {
      return TypeParserMatcherResult.regexResult(var1, var2, this.regex);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TypeParserResult getMatchResult(FairGlyph[] var1, int var2) {
      return this.getMatchResult(var1, var2);
   }
}
