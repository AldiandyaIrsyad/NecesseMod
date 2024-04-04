package necesse.gfx.forms.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class FormEventsHandler<T extends FormEvent> {
   private ArrayList<FormEventListener<T>> listeners = new ArrayList();

   public FormEventsHandler() {
   }

   public void addListener(FormEventListener<T> var1) {
      Objects.requireNonNull(var1);
      this.listeners.add(var1);
   }

   public boolean hasListeners() {
      return !this.listeners.isEmpty();
   }

   public void clearListeners() {
      this.listeners.clear();
   }

   public void onEvent(T var1) {
      this.listeners.removeIf(FormEventListener::disposed);
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         FormEventListener var3 = (FormEventListener)var2.next();
         var3.onEvent(var1);
      }

   }
}
