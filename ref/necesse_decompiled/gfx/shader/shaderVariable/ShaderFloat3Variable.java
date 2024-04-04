package necesse.gfx.shader.shaderVariable;

import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.GameShader;

public class ShaderFloat3Variable extends ShaderVariable<Float[]> {
   public ShaderFloat3Variable(String var1) {
      super(var1);
   }

   public void addInputs(GameShader var1, ComponentListContainer<? super FormComponent> var2, int var3, FormFlow var4, int var5) {
      float[] var6 = var1.get3f(this.varName);
      var2.addComponent(new FormLabel(this.varName, new FontOptions(16), -1, var3, var4.next(16), var5));
      this.addTextInput(var1, var2, var3, var4, var5, var6, 0);
      this.addTextInput(var1, var2, var3, var4, var5, var6, 1);
      this.addTextInput(var1, var2, var3, var4, var5, var6, 2);
   }

   protected void addTextInput(GameShader var1, ComponentListContainer<? super FormComponent> var2, int var3, FormFlow var4, int var5, float[] var6, int var7) {
      FormTextInput var8 = (FormTextInput)var2.addComponent(new FormTextInput(var3, var4.next(22), FormInputSize.SIZE_20, var5, 100));
      var8.setText(Float.toString(var6[var7]));
      var8.onChange((var5x) -> {
         try {
            var6[var7] = Float.parseFloat(var8.getText());
            this.apply(var1, var6);
         } catch (NumberFormatException var7x) {
         }

      });
   }

   protected void apply(GameShader var1, float[] var2) {
      var1.use();
      var1.pass3f(this.varName, var2[0], var2[1], var2[2]);
      var1.stop();
   }
}
