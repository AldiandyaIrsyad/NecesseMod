package necesse.gfx.forms.presets.debug;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.lists.FormShaderList;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderVariable;

public class DebugShadersForm extends Form {
   public FormShaderList shaderList;
   public FormContentBox inputBox;
   public FormTextButton shadersBack;
   public final DebugForm parent;

   public DebugShadersForm(String var1, DebugForm var2) {
      super((String)var1, 320, 400);
      this.parent = var2;
      this.addComponent(new FormLabel("Shaders", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.shaderList = (FormShaderList)this.addComponent(new FormShaderList(0, 40, this.getWidth(), 150));
      this.shaderList.onShaderSelect((var1x) -> {
         this.updateShaderInputs(var1x.shader);
      });
      this.inputBox = (FormContentBox)this.addComponent(new FormContentBox(0, 190, this.getWidth(), this.getHeight() - 150 - 80));
      this.shadersBack = (FormTextButton)this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth()));
      this.shadersBack.onClicked((var1x) -> {
         var2.makeCurrent(var2.mainMenu);
      });
   }

   private void updateShaderInputs(GameShader var1) {
      this.inputBox.clearComponents();
      FormFlow var2 = new FormFlow(5);
      ArrayList var3 = var1.getVariables();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         ShaderVariable var5 = (ShaderVariable)var4.next();
         var5.addInputs(var1, this.inputBox, 5, var2, this.inputBox.getWidth() - 18);
      }

      this.inputBox.setContentBox(new Rectangle(this.getWidth(), var2.next() + 5));
   }
}
