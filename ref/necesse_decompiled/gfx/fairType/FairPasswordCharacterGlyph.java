package necesse.gfx.fairType;

import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.control.InputEvent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;

public class FairPasswordCharacterGlyph extends FairCharacterGlyph {
   private final char actualCharacter;

   public FairPasswordCharacterGlyph(FontOptions var1, char var2, Function<InputEvent, Boolean> var3, Supplier<GameTooltips> var4) {
      super(var1, '*', var3, var4);
      this.actualCharacter = var2;
   }

   public FairPasswordCharacterGlyph(FontOptions var1, char var2, Function<InputEvent, Boolean> var3) {
      super(var1, '*', var3);
      this.actualCharacter = var2;
   }

   public FairPasswordCharacterGlyph(FontOptions var1, char var2, Supplier<GameTooltips> var3) {
      super(var1, '*', var3);
      this.actualCharacter = var2;
   }

   public FairPasswordCharacterGlyph(FontOptions var1, char var2) {
      super(var1, '*');
      this.actualCharacter = var2;
   }

   public char getCharacter() {
      return this.actualCharacter;
   }

   public String toString() {
      return String.valueOf(this.actualCharacter);
   }
}
