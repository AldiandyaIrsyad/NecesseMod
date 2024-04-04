package necesse.entity.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import necesse.engine.util.GameUtils;

public class EntityComponentManager {
   private HashMap<Class<? extends EntityComponent>, HashMap<Integer, Object>> list = new HashMap();

   public EntityComponentManager() {
   }

   public void add(int var1, Object var2) {
      Class[] var3 = var2.getClass().getInterfaces();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Class var6 = var3[var5];
         if (EntityComponent.class.isAssignableFrom(var6)) {
            this.add(var6, var1, var2);
         }
      }

   }

   private void add(Class<? extends EntityComponent> var1, int var2, Object var3) {
      this.list.compute(var1, (var2x, var3x) -> {
         if (var3x == null) {
            var3x = new HashMap();
         }

         var3x.put(var2, var3);
         return var3x;
      });
   }

   public boolean remove(int var1, Object var2) {
      boolean var3 = false;
      Class[] var4 = var2.getClass().getInterfaces();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Class var7 = var4[var6];
         if (EntityComponent.class.isAssignableFrom(var7)) {
            var3 = this.remove(var7, var1) || var3;
         }
      }

      return var3;
   }

   private boolean remove(Class<? extends EntityComponent> var1, int var2) {
      AtomicBoolean var3 = new AtomicBoolean();
      this.list.compute(var1, (var2x, var3x) -> {
         if (var3x == null) {
            var3x = new HashMap();
         }

         if (var3x.remove(var2) != null) {
            var3.set(true);
         }

         return var3x;
      });
      return var3.get();
   }

   public <C extends Class<? extends EntityComponent>> int getCount(C var1) {
      HashMap var2 = (HashMap)this.list.get(var1);
      return var2 == null ? 0 : var2.size();
   }

   public <R extends EntityComponent, C extends Class<? extends R>> Iterable<R> getAll(C var1) {
      HashMap var2 = (HashMap)this.list.get(var1);
      return (Iterable)(var2 != null ? GameUtils.mapIterable(var2.values().iterator(), (var0) -> {
         return (EntityComponent)var0;
      }) : Collections.emptyList());
   }

   public <R extends EntityComponent, C extends Class<? extends R>> Stream<R> streamAll(C var1) {
      HashMap var2 = (HashMap)this.list.get(var1);
      return var2 != null ? var2.values().stream().map((var0) -> {
         return (EntityComponent)var0;
      }) : Stream.empty();
   }
}
