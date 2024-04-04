package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.ui.ButtonColor;

public class TestCrashReportForm extends Form {
   public TestCrashReportForm() {
      super((String)"crashTest", 200, FormInputSize.SIZE_24.height + 40);
      this.setDraggingBox(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
      ((FormTextButton)this.addComponent(new FormTextButton("Perform crash", 20, 20, this.getWidth() - 40, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var0) -> {
         throw new RuntimeException("Test crash");
      });
      this.setPosition(50, 50);
   }
}
