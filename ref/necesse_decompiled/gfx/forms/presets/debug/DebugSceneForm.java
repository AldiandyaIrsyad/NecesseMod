package necesse.gfx.forms.presets.debug;

import necesse.engine.util.EventVariable;
import necesse.gfx.GameResources;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;

public class DebugSceneForm extends Form {
   public final DebugForm parent;
   public static EventVariable<Float> brightness = new EventVariable(1.0F);
   public static EventVariable<Float> contrast = new EventVariable(1.0F);
   public static EventVariable<Float> gamma = new EventVariable(1.0F);
   public static EventVariable<Float> vibrance = new EventVariable(0.0F);
   public static EventVariable<Float> vibranceRedBalance = new EventVariable(1.0F);
   public static EventVariable<Float> vibranceGreenBalance = new EventVariable(1.0F);
   public static EventVariable<Float> vibranceBlueBalance = new EventVariable(1.0F);
   public static EventVariable<Float> red = new EventVariable(1.0F);
   public static EventVariable<Float> green = new EventVariable(1.0F);
   public static EventVariable<Float> blue = new EventVariable(1.0F);

   public DebugSceneForm(String var1, DebugForm var2) {
      super((String)var1, 240, 400);
      this.parent = var2;
      FormFlow var3 = new FormFlow(10);
      this.addComponent(new FormLabel("Scene", new FontOptions(20), 0, this.getWidth() / 2, var3.next(20)));
      final byte var4 = 20;
      FormSlider var5 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Brightness", 5, 10, (int)((Float)brightness.get() * (float)var4), 0, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var5.onChanged((var1x) -> {
         brightness.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      brightness.addChangeListener((var2x) -> {
         var5.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var6 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Contrast", 5, 10, (int)((Float)contrast.get() * (float)var4), 0, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var6.onChanged((var1x) -> {
         contrast.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      contrast.addChangeListener((var2x) -> {
         var6.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var7 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Gamma", 5, 10, (int)((Float)gamma.get() * (float)var4), 0, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var7.onChanged((var1x) -> {
         gamma.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      gamma.addChangeListener((var2x) -> {
         var7.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var8 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Vibrance", 5, 10, (int)((Float)vibrance.get() * (float)var4), -2 * var4, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var8.onChanged((var1x) -> {
         vibrance.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      vibrance.addChangeListener((var2x) -> {
         var8.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var9 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Vibrance Red", 5, 10, (int)((Float)vibranceRedBalance.get() * (float)var4), 0, 1 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var9.onChanged((var1x) -> {
         vibranceRedBalance.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      vibranceRedBalance.addChangeListener((var2x) -> {
         var9.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var10 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Vibrance Green", 5, 10, (int)((Float)vibranceGreenBalance.get() * (float)var4), 0, 1 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var10.onChanged((var1x) -> {
         vibranceGreenBalance.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      vibranceGreenBalance.addChangeListener((var2x) -> {
         var10.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var11 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Vibrance Blue", 5, 10, (int)((Float)vibranceBlueBalance.get() * (float)var4), 0, 1 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var11.onChanged((var1x) -> {
         vibranceBlueBalance.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      vibranceBlueBalance.addChangeListener((var2x) -> {
         var11.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      var3.next(10);
      FormSlider var12 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Red color", 5, 10, (int)((Float)red.get() * (float)var4), 0, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var12.onChanged((var1x) -> {
         red.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      red.addChangeListener((var2x) -> {
         var12.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var13 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Green color", 5, 10, (int)((Float)green.get() * (float)var4), 0, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var13.onChanged((var1x) -> {
         green.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      green.addChangeListener((var2x) -> {
         var13.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      FormSlider var14 = (FormSlider)this.addComponent((<undefinedtype>)var3.nextY(new FormSlider("Blue color", 5, 10, (int)((Float)blue.get() * (float)var4), 0, 2 * var4, this.getWidth() - 10) {
         public String getValueText() {
            return (int)((float)this.getValue() / (float)var4 * 100.0F) + " %";
         }
      }, 5));
      var14.onChanged((var1x) -> {
         blue.set((float)((FormSlider)var1x.from).getValue() / (float)var4);
      });
      blue.addChangeListener((var2x) -> {
         var14.setValue((int)(var2x * (float)var4));
      }, this::isDisposed);
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, var3.next(40), this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.mainMenu);
      });
      this.setHeight(var3.next());
   }

   static {
      brightness.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("brightness", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      contrast.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("contrast", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      gamma.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("gamma", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      vibrance.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("vibrance", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      vibranceRedBalance.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("vibranceRedBalance", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      vibranceGreenBalance.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("vibranceGreenBalance", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      vibranceBlueBalance.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("vibranceBlueBalance", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      red.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("red", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      green.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("green", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
      blue.addChangeListener((var0) -> {
         GameResources.debugColorShader.use();
         GameResources.debugColorShader.pass1f("blue", var0);
         GameResources.debugColorShader.stop();
      }, () -> {
         return false;
      });
   }
}
