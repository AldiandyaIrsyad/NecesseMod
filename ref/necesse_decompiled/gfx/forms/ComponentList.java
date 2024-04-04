package necesse.gfx.forms;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public abstract class ComponentList<T extends FormComponent> implements Iterable<T>, ComponentPriorityManager {
   private ArrayList<T> components;
   private boolean appendPriority;
   private long priorityCounter;

   public ComponentList(boolean var1) {
      this.priorityCounter = 0L;
      this.components = new ArrayList();
      this.appendPriority = var1;
   }

   public ComponentList() {
      this(true);
   }

   public void submitInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (!var1.isUsed()) {
         var1 = this.offsetEvent(var1, false);
         this.components.sort((Comparator)null);
         FormComponent[] var4 = (FormComponent[])this.components.toArray(new FormComponent[0]);

         for(int var5 = var4.length - 1; var5 >= 0; --var5) {
            FormComponent var6 = var4[var5];
            if (var6 != null && var6.shouldDraw()) {
               boolean var7 = var1.isMouseClickEvent();
               var6.handleInputEvent(var1, var2, var3);
               if (this.appendPriority && var7 && var6.isMouseOver(var1)) {
                  var6.tryPutOnTop();
                  if (var6.shouldUseMouseEvents()) {
                     break;
                  }
               }

               if (var1.isMouseMoveEvent() && var6.shouldUseMouseEvents() && var6.isMouseOver(var1)) {
                  var1.useMove();
               }
            }

            if (var1.isUsed()) {
               break;
            }
         }

      }
   }

   public void submitControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (!var1.isUsed()) {
         this.components.sort((Comparator)null);
         FormComponent[] var4 = (FormComponent[])this.components.toArray(new FormComponent[0]);

         for(int var5 = var4.length - 1; var5 >= 0; --var5) {
            FormComponent var6 = var4[var5];
            if (var6 != null && var6.shouldDraw()) {
               boolean var7 = var1.getState() == ControllerInput.MENU_SELECT;
               var6.handleControllerEvent(var1, var2, var3);
               if (this.appendPriority && var7) {
                  FormManager var8 = this.getManager();
                  if (var8 != null && var8.isControllerFocus(var6)) {
                     var6.tryPutOnTop();
                  }
               }
            }

            if (var1.isUsed()) {
               break;
            }
         }

      }
   }

   public void addNextControllerComponents(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      this.components.sort((Comparator)null);
      Iterator var7 = this.components.iterator();

      while(var7.hasNext()) {
         FormComponent var8 = (FormComponent)var7.next();
         if (var8.shouldDraw()) {
            var8.addNextControllerFocus(var1, var2, var3, var4, var5, var6);
         }
      }

   }

   public abstract InputEvent offsetEvent(InputEvent var1, boolean var2);

   public abstract FormManager getManager();

   public void init() {
      this.components.forEach((var1) -> {
         var1.setManager(this.getManager());
      });
   }

   public void removeComponent(T var1) {
      this.removeComponent(var1, true);
   }

   private void removeComponent(T var1, boolean var2) {
      if (this.components.remove(var1)) {
         var1.dispose();
         if (var2) {
            this.onChange();
         }
      }

   }

   public void onChange() {
   }

   public boolean hasComponent(T var1) {
      return this.components.contains(var1);
   }

   public void clearComponents() {
      this.removeComponentsIf((var0) -> {
         return true;
      });
   }

   public boolean removeComponentsIf(Predicate<T> var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.components.size(); ++var3) {
         FormComponent var4 = (FormComponent)this.components.get(var3);
         if (var1.test(var4)) {
            this.removeComponent(var4, false);
            var2 = true;
            --var3;
         }
      }

      if (var2) {
         this.onChange();
      }

      return var2;
   }

   public Collection<T> getComponents() {
      return this.components;
   }

   public <M extends T> M addComponent(M var1) {
      return this.addComponent(var1, 0);
   }

   public <M extends T> M addComponent(M var1, int var2) {
      if (this.hasComponent(var1)) {
         throw new IllegalArgumentException("Cannot add the same component twice");
      } else {
         var1.zIndex = var2;
         this.components.add(var1);
         var1.setPriorityManager(this);
         var1.setManager(this.getManager());
         this.onChange();
         return var1;
      }
   }

   public void sort() {
      this.components.sort((Comparator)null);
   }

   public void drawComponents(TickManager var1, PlayerMob var2, Rectangle var3) {
      Performance.record(var1, "sort", (Runnable)(this::sort));
      Stream var4 = this.components.stream().filter(FormComponent::shouldDraw);
      if (var3 != null) {
         var4 = var4.filter((var1x) -> {
            return var3.intersects(var1x.getBoundingBox());
         });
      }

      var4.forEach((var3x) -> {
         var3x.draw(var1, var2, var3);
      });
   }

   public void onWindowResized() {
      this.components.forEach(FormComponent::onWindowResized);
   }

   public void disposeComponents() {
      while(!this.components.isEmpty()) {
         this.removeComponent((FormComponent)this.components.get(0), false);
      }

   }

   public Iterator<T> iterator() {
      return this.components.iterator();
   }

   public T[] toArray(T[] var1) {
      return (FormComponent[])this.components.toArray(var1);
   }

   public Stream<T> stream() {
      return this.components.stream();
   }

   public List<Rectangle> getHitboxes() {
      return (List)this.stream().filter(FormComponent::shouldDraw).flatMap((var0) -> {
         return var0.getHitboxes().stream();
      }).collect(Collectors.toList());
   }

   public int size() {
      return this.components.size();
   }

   public long getNextPriorityKey() {
      return ++this.priorityCounter;
   }
}
