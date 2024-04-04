package necesse.gfx.forms.components.lists;

import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormIndexEvent;

public abstract class FormSelectedList<E extends FormSelectedElement> extends FormGeneralList<E> {
   private FormEventsHandler<FormIndexEvent<FormSelectedList<E>>> onSelected = new FormEventsHandler();
   private int selected;

   public FormSelectedList(int var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   public FormSelectedList<E> onSelected(FormEventListener<FormIndexEvent<FormSelectedList<E>>> var1) {
      this.onSelected.addListener(var1);
      return this;
   }

   protected E getSelectedElement() {
      return this.selected >= 0 && this.selected < this.elements.size() ? (FormSelectedElement)this.elements.get(this.selected) : null;
   }

   public void reset() {
      super.reset();
      this.clearSelected();
   }

   public int getSelectedIndex() {
      return this.selected;
   }

   public void setSelected(int var1) {
      int var2 = this.selected;
      this.clearSelected();
      this.selected = var1;
      if (var1 >= 0 && var1 < this.elements.size()) {
         FormIndexEvent var3 = new FormIndexEvent(this, var1);
         if (this.onSelected != null) {
            this.onSelected.onEvent(var3);
         }

         if (var3.hasPreventedDefault()) {
            this.selected = var2;
         } else {
            ((FormSelectedElement)this.elements.get(var1)).makeSelected(this);
         }

      }
   }

   public void clearSelected() {
      this.selected = -1;
      this.elements.forEach(FormSelectedElement::clearSelected);
   }
}
