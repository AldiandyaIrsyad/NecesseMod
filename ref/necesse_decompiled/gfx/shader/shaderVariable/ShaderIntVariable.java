package necesse.gfx.shader.shaderVariable;

import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.util.EventVariable;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.GameShader;

public class ShaderIntVariable extends ShaderVariable<Integer> {
   protected boolean hasSlider;
   protected int min;
   protected int max;

   public ShaderIntVariable(String var1) {
      super(var1);
   }

   public ShaderIntVariable(String var1, int var2, int var3) {
      this(var1);
      this.hasSlider = true;
      this.min = var2;
      this.max = var3;
   }

   public void addInputs(GameShader var1, ComponentListContainer<? super FormComponent> var2, int var3, FormFlow var4, int var5) {
      EventVariable var6 = new EventVariable(var1.get1i(this.varName));
      var2.addComponent(new FormLabel(this.varName, new FontOptions(16), -1, var3, var4.next(18), var5));
      FormTextInput var7 = (FormTextInput)var2.addComponent(new FormTextInput(var3, var4.next(22), FormInputSize.SIZE_20, var5, 100));
      var7.setText(Integer.toString((Integer)var6.get()));
      var7.onChange((var2x) -> {
         try {
            int var3 = Integer.parseInt(var7.getText());
            var6.set(var3);
         } catch (NumberFormatException var4) {
         }

      });
      Consumer var10001 = (var1x) -> {
         var7.setText(Integer.toString(var1x));
      };
      Objects.requireNonNull(var7);
      var6.addChangeListener(var10001, var7::isDisposed);
      if (this.hasSlider) {
         FormSlider var8 = (FormSlider)var2.addComponent((FormSlider)var4.nextY(new FormSlider("", var3, 0, (Integer)var6.get(), this.min, this.max, var5), 5));
         var8.drawValue = false;
         var8.onChanged((var1x) -> {
            var6.set(((FormSlider)var1x.from).getValue());
         });
         var10001 = (var2x) -> {
            var8.setValue((Integer)var6.get());
         };
         Objects.requireNonNull(var8);
         var6.addChangeListener(var10001, var8::isDisposed);
      }

      var6.addChangeListener((var2x) -> {
         var1.use();
         var1.pass1i(this.varName, var2x);
         var1.stop();
      }, () -> {
         return false;
      });
   }
}
