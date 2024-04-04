package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitchedComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.gameFont.FontOptions;

public class DialogueForm extends Form implements FormSwitchedComponent {
   public FormContentBox content;
   public FormFlow flow;
   public int optionCounter;
   private ArrayList<FormDialogueOption> dialogueOptions = new ArrayList();

   public DialogueForm(String var1, int var2, int var3, GameMessage var4, GameMessage var5) {
      super(var1, var2, var3);
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 0, var2, var3));
      this.reset(var4, var5);
   }

   public void reset(BiConsumer<FormContentBox, FormFlow> var1) {
      this.dialogueOptions.clear();
      this.content.clearComponents();
      this.optionCounter = 1;
      this.flow = new FormFlow(4);
      var1.accept(this.content, this.flow);
      this.flow.next(10);
      this.updateContent();
   }

   public void reset(GameMessage var1, BiConsumer<FormContentBox, FormFlow> var2) {
      this.reset((var3, var4) -> {
         if (var1 != null) {
            String var5 = GameUtils.maxString(var1.translate(), new FontOptions(20), this.getWidth() - 10);
            var3.addComponent((FormLabel)var4.nextY(new FormLabel(var5, new FontOptions(20), -1, 4, 0), 5));
         }

         if (var2 != null) {
            var2.accept(var3, var4);
         }

      });
   }

   public void reset(GameMessage var1) {
      this.reset(var1, (BiConsumer)null);
   }

   public void reset(GameMessage var1, GameMessage var2, BiConsumer<FormContentBox, FormFlow> var3) {
      this.reset(var1, (var2x, var3x) -> {
         if (var2 != null) {
            addText(var2x, var3x, var2);
         }

         if (var3 != null) {
            var3.accept(var2x, var3x);
         }

      });
   }

   public void reset(GameMessage var1, GameMessage var2) {
      this.reset(var1, var2, (BiConsumer)null);
   }

   public static FormFairTypeLabel addText(FormContentBox var0, FormFlow var1, GameMessage var2, int var3, int var4) {
      FormFairTypeLabel var5 = (FormFairTypeLabel)var0.addComponent(new FormFairTypeLabel(var2, var3, 0));
      var5.setMaxWidth(var4);
      FontOptions var6 = new FontOptions(16);
      var5.setFontOptions(var6);
      var5.setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(var6), TypeParsers.ItemIcon(16));
      var1.nextY(var5);
      return var5;
   }

   public static FormFairTypeLabel addText(FormContentBox var0, FormFlow var1, GameMessage var2) {
      return addText(var0, var1, var2, 5, var0.getWidth() - 10);
   }

   public FormDialogueOption addDialogueOption(GameMessage var1, Runnable var2) {
      return this.addDialogueOption(this.optionCounter++, var1, var2);
   }

   public FormDialogueOption addDialogueOption(int var1, GameMessage var2, Runnable var3) {
      FormDialogueOption var4 = this.addDialogueOption(new FormDialogueOption(var1, var2, new FontOptions(16), 0, 0, 0));
      var4.onClicked((var1x) -> {
         var3.run();
      });
      return var4;
   }

   public FormDialogueOption addDialogueOption(FormDialogueOption var1) {
      var1.setPosition(20, 0);
      var1.setMaxWidth(this.getWidth() - 30);
      this.content.addComponent((FormDialogueOption)this.flow.nextY(var1, 6));
      this.dialogueOptions.add(var1);
      this.updateContent();
      return var1;
   }

   public void updateContent() {
      this.content.setContentBox(new Rectangle(0, 0, this.getWidth(), this.flow.next()));
   }

   public int getContentHeight() {
      return this.flow.next();
   }

   public void setHeight(int var1) {
      super.setHeight(var1);
      this.content.setHeight(var1);
   }

   public void onSwitched(boolean var1) {
      if (var1) {
         this.setNextControllerFocus((ControllerFocusHandler[])this.dialogueOptions.toArray(new FormDialogueOption[0]));
      }

   }
}
