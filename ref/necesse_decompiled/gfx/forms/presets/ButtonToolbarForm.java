package necesse.gfx.forms.presets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class ButtonToolbarForm extends Form {
   private ArrayList<ToolbarButton> buttons;

   public ButtonToolbarForm(String var1) {
      super((String)var1, 16, 40);
      this.buttons = new ArrayList();
   }

   public ButtonToolbarForm() {
      this((String)null);
   }

   public void addButton(FormComponent var1, BiConsumer<Integer, Integer> var2) {
      this.addComponent(var1);
      this.buttons.add(new ToolbarButton(var1, var2));
      this.updateButtons();
   }

   public void addButton(FormPositionContainer var1) {
      FormComponent var10001 = (FormComponent)var1;
      Objects.requireNonNull(var1);
      this.addButton(var10001, var1::setPosition);
   }

   public void addButton(FormContentIconButton var1) {
      Objects.requireNonNull(var1);
      this.addButton(var1, var1::setPosition);
   }

   public FormContentIconButton addButton(String var1, ButtonIcon var2, FormEventListener<FormInputEvent<FormButton>> var3, GameMessage... var4) {
      FormContentIconButton var5 = new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, var2, var4);
      var5.controllerFocusHashcode = var1;
      var5.onClicked(var3);
      this.addButton(var5);
      return var5;
   }

   public void updateButtons() {
      FormFlow var1 = new FormFlow(4);
      Iterator var2 = this.buttons.iterator();

      while(var2.hasNext()) {
         ToolbarButton var3 = (ToolbarButton)var2.next();
         var3.positionSetter.accept(var1.next(var3.component.getBoundingBox().width), 4);
      }

      this.setWidth(var1.next() + 4);
   }

   private static class ToolbarButton {
      public final FormComponent component;
      public final BiConsumer<Integer, Integer> positionSetter;

      public ToolbarButton(FormComponent var1, BiConsumer<Integer, Integer> var2) {
         this.component = var1;
         this.positionSetter = var2;
      }
   }
}
