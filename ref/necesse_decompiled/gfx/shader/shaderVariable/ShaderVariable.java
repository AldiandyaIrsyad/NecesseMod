package necesse.gfx.shader.shaderVariable;

import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.shader.GameShader;

public abstract class ShaderVariable<T> {
   public final String varName;

   public ShaderVariable(String var1) {
      this.varName = var1;
   }

   public abstract void addInputs(GameShader var1, ComponentListContainer<? super FormComponent> var2, int var3, FormFlow var4, int var5);
}
