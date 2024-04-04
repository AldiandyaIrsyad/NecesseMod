package necesse.gfx.forms.components.chat;

import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.gameFont.FontOptions;

public class ChatMessage {
   public static final int textMaxLength = 500;
   public static final int textFadeTime = 10000;
   public static final FontOptions fontOptions = (new FontOptions(16)).outline();
   public final FairType type;
   public final FairTypeDrawOptionsContainer drawOptions;
   public final long fadeTime;

   public static TypeParser<?>[] getParsers(FontOptions var0) {
      return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(var0.getSize()), TypeParsers.InputIcon(var0)};
   }

   public ChatMessage(FairType var1) {
      this.type = var1;
      this.drawOptions = new FairTypeDrawOptionsContainer(() -> {
         var1.updateGlyphDimensions();
         return var1.getDrawOptions(FairType.TextAlign.LEFT, 500, true, true);
      });
      this.fadeTime = System.currentTimeMillis() + 10000L;
   }

   public boolean shouldDraw() {
      return System.currentTimeMillis() < this.fadeTime;
   }
}
