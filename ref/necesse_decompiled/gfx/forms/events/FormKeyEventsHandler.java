package necesse.gfx.forms.events;

import java.util.ArrayList;

public class FormKeyEventsHandler<T extends FormEvent> {
   private ArrayList<FormKeyEventsHandler<T>.StringEventListener<T>> listeners = new ArrayList();

   public FormKeyEventsHandler() {
   }

   public void addListener(String var1, FormEventListener<T> var2) {
      this.listeners.add(new StringEventListener(var1, var2));
   }

   public void clearListeners() {
      this.listeners.clear();
   }

   public void onEvent(String var1, T var2) {
      this.listeners.stream().filter((var1x) -> {
         return var1x.key.equals(var1);
      }).forEach((var1x) -> {
         var1x.listener.onEvent(var2);
      });
   }

   private class StringEventListener<S extends FormEvent> {
      public final String key;
      public final FormEventListener<S> listener;

      public StringEventListener(String var2, FormEventListener<S> var3) {
         this.key = var2;
         this.listener = var3;
      }
   }
}
