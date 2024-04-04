package necesse.engine.util;

import java.util.function.Supplier;

public abstract class MultiValueWatcher {
   private final ValueWatcher<?>[] watchers;
   private boolean changed = false;

   public MultiValueWatcher(Supplier<?>... var1) {
      this.watchers = new ValueWatcher[var1.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.watchers[var2] = new ValueWatcher<Object>(var1[var2]) {
            public void onChange(Object var1) {
               MultiValueWatcher.this.changed = true;
            }
         };
      }

   }

   public void update() {
      ValueWatcher[] var1 = this.watchers;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ValueWatcher var4 = var1[var3];
         var4.update();
      }

      if (this.changed) {
         this.changed = false;
         this.onChange();
      }

   }

   public abstract void onChange();
}
