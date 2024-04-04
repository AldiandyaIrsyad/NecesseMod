package necesse.gfx.fairType.parsers;

import necesse.gfx.fairType.FairGlyph;

public abstract class TypeParser<T extends TypeParserResult> {
   public TypeParser() {
   }

   public abstract T getMatchResult(FairGlyph[] var1, int var2);

   public abstract FairGlyph[] parse(T var1, FairGlyph[] var2);
}
