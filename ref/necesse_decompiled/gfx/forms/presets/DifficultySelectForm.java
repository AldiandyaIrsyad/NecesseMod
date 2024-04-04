package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.GameDifficulty;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ButtonOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class DifficultySelectForm extends Form {
   public GameDifficulty selectedDifficulty;
   private FormContentIconToggleButton[] difficultyButtons;
   private FormLocalLabel[] difficultyLabels;
   private FormContentBox difficultyContent;
   private FormLocalTextButton difficultyContinueButton;

   public DifficultySelectForm(Runnable var1, Runnable var2, Runnable var3) {
      this(new ButtonOptions("ui", "createworld", var1), ButtonOptions.backButton(var2), new ButtonOptions("ui", "moresworldettings", var3));
   }

   public DifficultySelectForm(ButtonOptions var1, ButtonOptions var2, ButtonOptions var3) {
      super((String)"difficultySelect", 730, 500);
      this.selectedDifficulty = GameDifficulty.CLASSIC;
      GameDifficulty[] var4 = GameDifficulty.values();
      byte var5 = 8;
      short var6 = 128;
      short var7 = 143;
      FormFlow var8 = new FormFlow(10);
      this.addComponent(new FormLocalLabel("ui", "selectdifficulty", new FontOptions(20), 0, this.getWidth() / 2, var8.next(30)));
      this.addComponent((FormLocalLabel)var8.nextY(new FormLocalLabel("ui", "wschangetip", new FontOptions(12), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 20));
      int var9 = var8.next(var7 + 10 + 16);
      this.difficultyButtons = new FormContentIconToggleButton[var4.length];
      this.difficultyLabels = new FormLocalLabel[var4.length];

      for(int var10 = 0; var10 < var4.length; ++var10) {
         final GameDifficulty var11 = var4[var10];
         int var12 = var6 + var5 * 2;
         int var13 = this.getWidth() / 2 - this.difficultyButtons.length * var12 / 2 + var10 * var12 + var5;
         FormLocalLabel var14 = (FormLocalLabel)this.addComponent(new FormLocalLabel(var11.displayName, new FontOptions(20), 0, var13 + var6 / 2, var9 + var7 + 5));
         this.difficultyLabels[var10] = var14;
         FormContentIconToggleButton var15 = (FormContentIconToggleButton)this.addComponent(new FormContentIconToggleButton(var13, var9, var6, FormInputSize.background(var7, GameBackground.form, 20), ButtonColor.BASE, (ButtonIcon)null, new GameMessage[]{var11.displayName}) {
            public Color getContentColor(ButtonIcon var1) {
               if (this.isToggled()) {
                  return Color.WHITE;
               } else {
                  return this.isHovering() ? Color.WHITE : new Color(80, 110, 155);
               }
            }

            protected void drawContent(int var1, int var2, int var3, int var4) {
               ((ButtonIcon)var11.buttonIconBackgroundSupplier.get()).texture.initDraw().color(this.getContentColor((ButtonIcon)null)).draw(-16, -17);
            }

            protected void drawTopContent(int var1, int var2, int var3, int var4) {
               ((ButtonIcon)var11.buttonIconForegroundSupplier.get()).texture.initDraw().color(this.getContentColor((ButtonIcon)null)).draw(var1 - 16, var2 - 17);
            }
         });
         var15.onToggled((var3x) -> {
            var14.setColor(Settings.UI.inactiveTextColor);
            this.selectedDifficulty = var11;
            this.updateDifficultyContent();
         });
         this.difficultyButtons[var10] = var15;
      }

      short var16 = 220;
      this.difficultyContent = (FormContentBox)this.addComponent(new FormContentBox(0, var8.next(var16), this.getWidth(), var16));
      this.updateDifficultyContent();
      int var17;
      if (var3 != null) {
         var8.next(4);
         var17 = Math.min(this.difficultyContent.getWidth() - 20, 300);
         ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var3.text, this.getWidth() / 2 - var17 / 2, var8.next(28), var17, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
            var3.pressed.run();
         });
      }

      if (var1 != null || var2 != null) {
         var17 = var8.next(40);
         if (var1 != null && var2 != null) {
            this.difficultyContinueButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton(var1.text, 4, var17, this.getWidth() / 2 - 6));
            this.difficultyContinueButton.onClicked((var1x) -> {
               var1.pressed.run();
            });
            ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var2.text, this.getWidth() / 2 + 2, var17, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
               var2.pressed.run();
            });
         } else {
            ButtonOptions var18 = var1 == null ? var2 : var1;
            this.difficultyContinueButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton(var18.text, 4, var17, this.getWidth() - 8));
            this.difficultyContinueButton.onClicked((var1x) -> {
               var18.pressed.run();
            });
         }
      }

      this.setHeight(var8.next());
   }

   public void updateDifficultyContent() {
      this.difficultyContent.clearComponents();
      FormContentIconToggleButton[] var1 = this.difficultyButtons;
      int var2 = var1.length;

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         FormContentIconToggleButton var4 = var1[var3];
         var4.setToggled(false);
      }

      FormLocalLabel[] var5 = this.difficultyLabels;
      var2 = var5.length;

      for(var3 = 0; var3 < var2; ++var3) {
         FormLocalLabel var7 = var5[var3];
         var7.setColor(Settings.UI.inactiveTextColor);
      }

      if (this.selectedDifficulty != null) {
         this.difficultyButtons[this.selectedDifficulty.ordinal()].setToggled(true);
         this.difficultyLabels[this.selectedDifficulty.ordinal()].setColor(Settings.UI.activeTextColor);
         FormFlow var6 = new FormFlow(20);
         this.difficultyContent.addComponent(new FormLocalLabel(this.selectedDifficulty.displayName, new FontOptions(20), 0, this.difficultyContent.getWidth() / 2, var6.next(30)));
         var2 = Math.min(600, this.difficultyContent.getWidth() - 20);
         this.difficultyContent.addComponent((FormLocalLabel)var6.nextY(new FormLocalLabel(this.selectedDifficulty.description, new FontOptions(16), 0, this.difficultyContent.getWidth() / 2, 0, var2), 20));
         this.difficultyContent.addComponent(new FormLocalLabel("ui", "difficultyeffects", new FontOptions(20), 0, this.difficultyContent.getWidth() / 2, var6.next(24)));
         FormFairTypeLabel var8 = new FormFairTypeLabel("", this.difficultyContent.getWidth() / 2, var6.next());
         var8.setTextAlign(FairType.TextAlign.CENTER);
         var8.setFontOptions(new FontOptions(16));
         var8.setMaxWidth(var2);
         var8.setText(this.selectedDifficulty.effects);
         this.difficultyContent.addComponent((FormFairTypeLabel)var6.nextY(var8, 10));
         this.difficultyContent.setContentBox(new Rectangle(this.difficultyContent.getWidth(), var6.next()));
      }

   }
}
