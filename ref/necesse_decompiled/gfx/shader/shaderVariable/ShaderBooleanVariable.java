package necesse.gfx.shader.shaderVariable;

import java.util.function.Function;
import java.util.function.Predicate;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.shader.GameShader;

public class ShaderBooleanVariable extends ShaderVariable<Integer> {
   public Predicate<Integer> toActive;
   public Function<Boolean, Integer> toValue;

   public ShaderBooleanVariable(String var1, Predicate<Integer> var2, Function<Boolean, Integer> var3) {
      super(var1);
      this.toActive = var2;
      this.toValue = var3;
   }

   public ShaderBooleanVariable(String var1) {
      this(var1, (var0) -> {
         return var0 > 0;
      }, (var0) -> {
         return var0 ? 1 : 0;
      });
   }

   public void addInputs(GameShader var1, ComponentListContainer<? super FormComponent> var2, int var3, FormFlow var4, int var5) {
      int var6 = var1.get1i(this.varName);
      FormCheckBox var7 = (FormCheckBox)var2.addComponent(new FormCheckBox(this.varName, var3, var4.next(16), this.toActive.test(var6)));
      var7.onClicked((var2x) -> {
         var1.use();
         var1.pass1i(this.varName, (Integer)this.toValue.apply(((FormCheckBox)var2x.from).checked));
         var1.stop();
      });
   }
}
