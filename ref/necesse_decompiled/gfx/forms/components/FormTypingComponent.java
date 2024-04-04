package necesse.gfx.forms.components;

import necesse.engine.GameLog;
import necesse.engine.control.Control;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.inventory.InventoryItem;
import org.lwjgl.system.Platform;

public abstract class FormTypingComponent extends FormComponent {
   private static FormTypingComponent currentTypingComponent;
   protected FormEventsHandler<FormInputEvent<FormTypingComponent>> inputEvents = new FormEventsHandler();
   protected FormEventsHandler<FormEvent<FormTypingComponent>> changeEvents = new FormEventsHandler();
   protected GameMessage controllerTypingHeader = null;

   public FormTypingComponent() {
   }

   public static boolean isCurrentlyTyping() {
      return currentTypingComponent != null && !currentTypingComponent.isDisposed();
   }

   public static boolean appendItemToTyping(InventoryItem var0) {
      if (var0 == null) {
         return false;
      } else {
         return isCurrentlyTyping() ? currentTypingComponent.appendItem(var0) : false;
      }
   }

   public static boolean appendTextToTyping(String var0) {
      if (var0 == null) {
         return false;
      } else {
         return isCurrentlyTyping() ? currentTypingComponent.appendText(var0) : false;
      }
   }

   public static boolean submitBackspaceToTyping() {
      return isCurrentlyTyping() ? currentTypingComponent.submitBackspace() : false;
   }

   public static FormTypingComponent getCurrentTypingComponent() {
      return currentTypingComponent;
   }

   private static boolean isCurrentlyTyping(FormTypingComponent var0) {
      return currentTypingComponent == var0;
   }

   private static void stopCurrentlyTyping(FormTypingComponent var0) {
      if (var0 == null) {
         throw new NullPointerException("Stop typing component null");
      } else {
         if (currentTypingComponent == var0) {
            var0.changedTyping(false);
            currentTypingComponent = null;
         }

      }
   }

   private static void setCurrentlyTyping(FormTypingComponent var0) {
      if (var0 == null) {
         throw new NullPointerException("Typing component null");
      } else {
         if (currentTypingComponent != var0) {
            if (currentTypingComponent != null) {
               currentTypingComponent.changedTyping(false);
            }

            var0.changedTyping(true);
            if (Input.lastInputIsController) {
               var0.playTickSound();
               var0.getManager().openControllerKeyboard(var0);
            }
         }

         Control.resetControls();
         currentTypingComponent = var0;
      }
   }

   public FormTypingComponent onInputEvent(FormEventListener<FormInputEvent<FormTypingComponent>> var1) {
      this.inputEvents.addListener(var1);
      return this;
   }

   public FormTypingComponent onChange(FormEventListener<FormEvent<FormTypingComponent>> var1) {
      this.changeEvents.addListener(var1);
      return this;
   }

   public void changedTyping(boolean var1) {
   }

   public final void setTyping(boolean var1) {
      if (var1) {
         FormManager var2 = this.getManager();
         if (var2 == null) {
            GameLog.warn.println("Cannot set typing on component not yet added to form manager");
         } else {
            setCurrentlyTyping(this);
            var2.setNextControllerFocus(this);
         }
      } else {
         stopCurrentlyTyping(this);
      }

   }

   public boolean appendItem(InventoryItem var1) {
      return false;
   }

   public boolean isValidAppendText(String var1) {
      return true;
   }

   public boolean appendText(String var1) {
      return false;
   }

   public boolean submitBackspace() {
      return false;
   }

   public boolean submitControllerEnter() {
      return false;
   }

   public FairTypeDrawOptions getDrawOptions() {
      return null;
   }

   public FairTypeDrawOptions getTextBoxDrawOptions() {
      return null;
   }

   public FormTypingComponent setControllerTypingHeader(GameMessage var1) {
      this.controllerTypingHeader = var1;
      return this;
   }

   public GameMessage getControllerTypingHeader() {
      return this.controllerTypingHeader;
   }

   public boolean submitTypingEvent(InputEvent var1, boolean var2) {
      return false;
   }

   public final boolean isTyping() {
      return isCurrentlyTyping(this);
   }

   public boolean isControllerTyping() {
      FormManager var1 = this.getManager();
      return var1 != null ? var1.isControllerTyping(this) : false;
   }

   protected void submitChangeEvent() {
      this.changeEvents.onEvent(new FormEvent(this));
   }

   public abstract void submitUsedInputEvent(InputEvent var1);

   public void dispose() {
      if (this.isTyping()) {
         stopCurrentlyTyping(this);
      }

      super.dispose();
   }

   public static int getSystemPasteKey() {
      switch (Platform.get()) {
         case WINDOWS:
            return 341;
         case MACOSX:
            return 341;
         default:
            return 341;
      }
   }

   public static int getSystemShiftWordKey() {
      switch (Platform.get()) {
         case WINDOWS:
            return 341;
         case MACOSX:
            return 342;
         default:
            return 341;
      }
   }
}
