package necesse.gfx.forms;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFairTypeDraw;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class ControllerKeyboardForm extends FormComponentList {
   public static ArrayList<String[]> keyboards = new ArrayList(Arrays.asList(new String[]{"1234567890", "1234567890", "qwertyuiop", "QWERTYUIOP", "asdfghjkl'", "ASDFGHJKL\"", "zxcvbnm,.?", "ZXCVBNM;:!"}, new String[]{"!#%&/\\`\u00b4~^", "!#%&/\\`\u00b4~^", "@_-+=;:<>|", "@_-+=;:<>|", "()[]{}*\u20ac$\u00a3", "()[]{}*\u00e2\u00ea\u00e3", "\u00e1\u00e9\u00ed\u00fa\u00fc\u00a1\u00bf\u00e6\u00f8\u00e5", "\u00e3\u00ea\u0457\u00f1\u00f6\u0161\u00e7\u00c6\u00d8\u00c5"}));
   private FormTypingComponent typingComponent;
   private FormLocalLabel header;
   private FormFairTypeDraw label;
   private FormSwitcher keyboardSwitcher;
   private ArrayList<Form> normalForms;
   private ArrayList<Form> capitalizedForms;
   private Object repeatSpacebar;
   private Object repeatBackspace;
   private int currentForm;
   private boolean capitalized;

   public ControllerKeyboardForm(FormTypingComponent var1) {
      this.normalForms = new ArrayList(keyboards.size());
      this.capitalizedForms = new ArrayList(keyboards.size());
      this.repeatSpacebar = new Object();
      this.repeatBackspace = new Object();
      this.typingComponent = var1;
      this.keyboardSwitcher = (FormSwitcher)this.addComponent(new FormSwitcher());
      GameMessage var2 = var1.getControllerTypingHeader();
      if (var2 != null) {
         this.header = (FormLocalLabel)this.addComponent(new FormLocalLabel(var2, (new FontOptions(20)).color(Color.WHITE).outline(), 0, 0, 0, 400));
      }

      this.label = (FormFairTypeDraw)this.addComponent(new FormFairTypeDraw(0, 0));
      this.label.drawOptions = var1.getTextBoxDrawOptions();

      for(int var3 = 0; var3 < keyboards.size(); ++var3) {
         String[] var4 = (String[])keyboards.get(var3);
         HashSet var5 = new HashSet();
         int var6 = 8;

         int var7;
         int var10;
         int var11;
         for(var7 = 0; var7 < var4.length; var7 += 2) {
            String var8 = var4[var7];
            String var9 = var4[var7 + 1];
            var10 = Math.max(var8.length(), var9.length());
            var6 = Math.max(var10, var6);

            for(var11 = 0; var11 < var10; ++var11) {
               var5.add(new Point(var11, var7 / 2));
            }
         }

         var7 = var4.length / 2;

         for(int var23 = 0; var23 < var6; ++var23) {
            var5.add(new Point(var23, var7));
         }

         byte var24 = 1;
         FormInputSize var25 = FormInputSize.SIZE_32;
         var10 = var25.height + var24 * 2;
         var11 = var25.height;
         Form var12 = (Form)this.keyboardSwitcher.addComponent(new Form("keyboardNormal" + var3, var5, var11 + var24 * 2, 4));
         this.normalForms.add(var12);
         Form var13 = (Form)this.keyboardSwitcher.addComponent(new Form("keyboardCap" + var3, var5, var11 + var24 * 2, 4));
         this.capitalizedForms.add(var13);

         int var14;
         for(var14 = 0; var14 < var4.length; var14 += 2) {
            String var15 = var4[var14];
            String var16 = var4[var14 + 1];
            int var17 = Math.max(var15.length(), var16.length());

            for(int var18 = 0; var18 < var17; ++var18) {
               char var19 = var18 < var15.length() ? var15.charAt(var18) : 32;
               char var20 = var18 < var16.length() ? var16.charAt(var18) : 32;
               FormTextButton var21 = (FormTextButton)var12.addComponent(new FormTextButton(Character.toString(var19), var18 * var10 + var24, var14 / 2 * var10 + var24, var11, var25, ButtonColor.BASE));
               var21.onClicked((var2x) -> {
                  this.submitText(String.valueOf(var19));
               });
               var21.controllerFocusHashcode = "keyboardKey" + var14 + "x" + var18;
               var21.acceptMouseRepeatEvents = true;
               var21.submitControllerPressEvent = true;
               var21.setActive(var1.isValidAppendText(String.valueOf(var19)));
               FormTextButton var22 = (FormTextButton)var13.addComponent(new FormTextButton(Character.toString(var20), var18 * var10 + var24, var14 / 2 * var10 + var24, var11, var25, ButtonColor.BASE));
               var22.onClicked((var2x) -> {
                  this.submitText(String.valueOf(var20));
               });
               var22.controllerFocusHashcode = "keyboardKey" + var14 + "x" + var18;
               var22.acceptMouseRepeatEvents = true;
               var21.submitControllerPressEvent = true;
               var22.setActive(var1.isValidAppendText(String.valueOf(var20)));
            }
         }

         var14 = var6 - 5;
         ((<undefinedtype>)var12.addComponent(new FormContentIconToggleButton(var24, var7 * var10 + var24, var11, var25, ButtonColor.YELLOW, Settings.UI.keyboard_shift, new GameMessage[0]) {
            public boolean isToggled() {
               return ControllerKeyboardForm.this.capitalized;
            }
         })).onClicked((var1x) -> {
            this.capitalized = !this.capitalized;
            this.refreshCurrentKeyboard();
         }).controllerFocusHashcode = "keyboardCap";
         ((FormContentIconButton)var12.addComponent(new FormContentIconButton(var10 + var24, var7 * var10 + var24, var11, var25, ButtonColor.YELLOW, Settings.UI.keyboard_next, new GameMessage[0]))).onClicked((var1x) -> {
            this.currentForm = (this.currentForm + 1) % this.normalForms.size();
            this.refreshCurrentKeyboard();
         }).controllerFocusHashcode = "keyboardChange";
         FormContentIconButton var26 = (FormContentIconButton)var12.addComponent(new FormContentIconButton(var10 * 2 + var24, var7 * var10 + var24, var14 * var11 + var24 * (var14 - 1) * 2, var25, ButtonColor.YELLOW, Settings.UI.keyboard_spacebar, new GameMessage[0]));
         var26.onClicked((var1x) -> {
            this.submitText(" ");
         });
         var26.controllerFocusHashcode = "keyboardSpace";
         var26.acceptMouseRepeatEvents = true;
         var26.setActive(var1.isValidAppendText(" "));
         FormContentIconButton var27 = (FormContentIconButton)var12.addComponent(new FormContentIconButton(var10 * 2 + var14 * var10 + var24, var7 * var10 + var24, var11 * 2 + var24 * 2, var25, ButtonColor.YELLOW, Settings.UI.keyboard_backspace, new GameMessage[0]));
         var27.onClicked((var1x) -> {
            this.submitBackspace();
         });
         var27.controllerFocusHashcode = "keyboardBack";
         var27.acceptMouseRepeatEvents = true;
         ((FormContentIconButton)var12.addComponent(new FormContentIconButton(var10 * 4 + var14 * var10 + var24, var7 * var10 + var24, var11, var25, ButtonColor.GREEN, Settings.UI.keyboard_return, new GameMessage[0]))).onClicked((var1x) -> {
            this.submitEnter();
         }).controllerFocusHashcode = "keyboardEnter";
         ((<undefinedtype>)var13.addComponent(new FormContentIconToggleButton(var24, var7 * var10 + var24, var11, var25, ButtonColor.YELLOW, Settings.UI.keyboard_shift, new GameMessage[0]) {
            public boolean isToggled() {
               return ControllerKeyboardForm.this.capitalized;
            }
         })).onClicked((var1x) -> {
            this.capitalized = !this.capitalized;
            this.refreshCurrentKeyboard();
         }).controllerFocusHashcode = "keyboardCap";
         ((FormContentIconButton)var13.addComponent(new FormContentIconButton(var10 + var24, var7 * var10 + var24, var11, var25, ButtonColor.YELLOW, Settings.UI.keyboard_next, new GameMessage[0]))).onClicked((var1x) -> {
            this.currentForm = (this.currentForm + 1) % this.normalForms.size();
            this.refreshCurrentKeyboard();
         }).controllerFocusHashcode = "keyboardChange";
         FormContentIconButton var28 = (FormContentIconButton)var13.addComponent(new FormContentIconButton(var10 * 2 + var24, var7 * var10 + var24, var14 * var11 + var24 * (var14 - 1) * 2, var25, ButtonColor.YELLOW, Settings.UI.keyboard_spacebar, new GameMessage[0]));
         var28.onClicked((var1x) -> {
            this.submitText(" ");
         });
         var28.controllerFocusHashcode = "keyboardSpace";
         var28.acceptMouseRepeatEvents = true;
         var28.setActive(var1.isValidAppendText(" "));
         FormContentIconButton var29 = (FormContentIconButton)var13.addComponent(new FormContentIconButton(var10 * 2 + var14 * var10 + var24, var7 * var10 + var24, var11 * 2 + var24 * 2, var25, ButtonColor.YELLOW, Settings.UI.keyboard_backspace, new GameMessage[0]));
         var29.onClicked((var1x) -> {
            this.submitBackspace();
         });
         var29.controllerFocusHashcode = "keyboardBack";
         var29.acceptMouseRepeatEvents = true;
         ((FormContentIconButton)var13.addComponent(new FormContentIconButton(var10 * 4 + var14 * var10 + var24, var7 * var10 + var24, var11, var25, ButtonColor.GREEN, Settings.UI.keyboard_return, new GameMessage[0]))).onClicked((var1x) -> {
            this.submitEnter();
         }).controllerFocusHashcode = "keyboardEnter";
      }

      this.refreshCurrentKeyboard();
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (!var1.isUsed()) {
         if (this.typingComponent.submitTypingEvent(var1, false)) {
            this.label.drawOptions = this.typingComponent.getTextBoxDrawOptions();
         }

      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      super.handleControllerEvent(var1, var2, var3);
      if (!var1.isUsed()) {
         if ((!var1.buttonState || var1.getState() != ControllerInput.MENU_ITEM_ACTIONS_MENU) && !var1.isRepeatEvent(this.repeatSpacebar)) {
            if ((!var1.buttonState || var1.getState() != ControllerInput.MENU_NEXT) && !var1.isRepeatEvent(this.repeatBackspace)) {
               if (var1.buttonState && var1.getState() == ControllerInput.MENU_PREV) {
                  this.capitalized = !this.capitalized;
                  this.refreshCurrentKeyboard();
                  if (var1.shouldSubmitSound()) {
                     this.playTickSound();
                  }

                  var1.use();
               }
            } else {
               var1.startRepeatEvents(this.repeatBackspace);
               this.submitBackspace();
               if (var1.shouldSubmitSound()) {
                  this.playTickSound();
               }

               var1.use();
            }
         } else {
            if (this.typingComponent.isValidAppendText(" ")) {
               var1.startRepeatEvents(this.repeatSpacebar);
               this.submitText(" ");
               if (var1.shouldSubmitSound()) {
                  this.playTickSound();
               }
            }

            var1.use();
         }
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Form var4 = this.capitalized ? (Form)this.capitalizedForms.get(this.currentForm) : (Form)this.normalForms.get(this.currentForm);
      Rectangle var5 = this.label.getBoundingBox();
      if (this.label.drawOptions != null) {
         switch (this.label.drawOptions.align) {
            case LEFT:
               this.label.setPosition(Screen.getHudWidth() / 2 - var5.width / 2, var4.getY() - var5.height - 30);
               break;
            case CENTER:
               this.label.setPosition(Screen.getHudWidth() / 2, var4.getY() - var5.height - 30);
               break;
            case RIGHT:
               this.label.setPosition(Screen.getHudWidth() / 2 + var5.width / 2, var4.getY() - var5.height - 30);
         }
      } else {
         this.label.setPosition(Screen.getHudWidth() / 2, var4.getY() - var5.height - 30);
      }

      if (this.header != null) {
         this.header.setPosition(Screen.getHudWidth() / 2, this.label.getY() - this.header.getHeight() - 15);
      }

      GameBackground.textBox.getDrawOptions(var5.x - 8, var5.y - 8, var5.width + 16, var5.height + 16).draw();
      super.draw(var1, var2, var3);
      GameBackground.textBox.getEdgeDrawOptions(var5.x - 8, var5.y - 8, var5.width + 16, var5.height + 16).draw();
      Screen.addControllerGlyph(Localization.translate("controls", "spacebartip"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
      Screen.addControllerGlyph(Localization.translate("controls", "backspacetip"), ControllerInput.MENU_NEXT);
      Screen.addControllerGlyph(Localization.translate("controls", "capitalizetip"), ControllerInput.MENU_PREV);
   }

   public void refreshCurrentKeyboard() {
      Form var1 = this.capitalized ? (Form)this.capitalizedForms.get(this.currentForm) : (Form)this.normalForms.get(this.currentForm);
      var1.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.keyboardSwitcher.makeCurrent(var1);
   }

   public void submitText(String var1) {
      this.typingComponent.appendText(var1);
      this.label.drawOptions = this.typingComponent.getTextBoxDrawOptions();
   }

   public void submitBackspace() {
      this.typingComponent.submitBackspace();
      this.label.drawOptions = this.typingComponent.getTextBoxDrawOptions();
   }

   public abstract void submitEnter();
}
