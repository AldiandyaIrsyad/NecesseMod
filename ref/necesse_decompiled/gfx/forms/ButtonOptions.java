package necesse.gfx.forms;

import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class ButtonOptions {
   public GameMessage text;
   public Runnable pressed;

   public ButtonOptions(GameMessage var1, Runnable var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      this.text = var1;
      this.pressed = var2;
   }

   public ButtonOptions(String var1, String var2, Runnable var3) {
      this(new LocalMessage(var1, var2), var3);
   }

   public static ButtonOptions continueButton(Runnable var0) {
      return new ButtonOptions("ui", "continuebutton", var0);
   }

   public static ButtonOptions confirmButton(Runnable var0) {
      return new ButtonOptions("ui", "confirmbutton", var0);
   }

   public static ButtonOptions backButton(Runnable var0) {
      return new ButtonOptions("ui", "backbutton", var0);
   }

   public static ButtonOptions cancelButton(Runnable var0) {
      return new ButtonOptions("ui", "cancelbutton", var0);
   }

   public static ButtonOptions closeButton(Runnable var0) {
      return new ButtonOptions("ui", "closebutton", var0);
   }
}
