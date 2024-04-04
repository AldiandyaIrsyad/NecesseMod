package necesse.gfx.fairType;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;

public interface FairGlyph {
   FloatDimension getDimensions();

   void updateDimensions();

   void handleInputEvent(float var1, float var2, InputEvent var3);

   void draw(float var1, float var2, Color var3);

   FairGlyph getTextBoxCharacter();

   default boolean isWhiteSpaceGlyph() {
      return false;
   }

   default boolean isNewLineGlyph() {
      return false;
   }

   default Supplier<Supplier<Color>> getDefaultColor() {
      return null;
   }

   default char getCharacter() {
      return '\ufffe';
   }

   default String getParseString() {
      return String.valueOf(this.getCharacter());
   }

   default boolean canBeParsed() {
      return false;
   }
}
