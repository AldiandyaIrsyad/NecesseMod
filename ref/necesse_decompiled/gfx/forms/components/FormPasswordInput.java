package necesse.gfx.forms.components;

import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairPasswordCharacterGlyph;

public class FormPasswordInput extends FormTextInput {
   public FormPasswordInput(int var1, int var2, FormInputSize var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public FormPasswordInput(int var1, int var2, FormInputSize var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected FairGlyph getCharacterGlyph(char var1) {
      return new FairPasswordCharacterGlyph(this.fontOptions, var1);
   }
}
