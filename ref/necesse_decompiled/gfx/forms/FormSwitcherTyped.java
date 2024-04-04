package necesse.gfx.forms;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public class FormSwitcherTyped<C extends FormComponent> extends FormComponent {
   private LinkedList<SwitchComponent> components = new LinkedList();
   private SwitchComponent currentComponent = null;

   public FormSwitcherTyped() {
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.currentComponent != null) {
         this.currentComponent.component.handleInputEvent(var1, var2, var3);
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.currentComponent != null) {
         this.currentComponent.component.handleControllerEvent(var1, var2, var3);
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.currentComponent != null) {
         this.currentComponent.component.addNextControllerFocus(var1, var2, var3, var4, var5, var6);
      }

   }

   protected void init() {
      super.init();
      this.components.forEach((var1) -> {
         var1.component.setManager(this.getManager());
      });
   }

   public <T extends C> T addComponent(T var1, BiConsumer<T, Boolean> var2) {
      this.components.add(new SwitchComponent(var1, var2));
      var1.setManager(this.getManager());
      return var1;
   }

   public <T extends C> T addComponent(T var1) {
      return this.addComponent(var1, (BiConsumer)null);
   }

   public boolean hasComponent(C var1) {
      return this.components.stream().anyMatch((var1x) -> {
         return var1x.component == var1;
      });
   }

   public void removeComponent(C var1) {
      if (this.hasComponent(var1)) {
         var1.dispose();
         this.components.removeIf((var1x) -> {
            return var1x.component == var1;
         });
      }

   }

   public <T extends C> FormSwitcherTyped<C>.SwitchComponent<T> getSwitch(T var1) {
      SwitchComponent var2 = (SwitchComponent)this.components.stream().filter((var1x) -> {
         return var1x.component == var1;
      }).findFirst().orElse((Object)null);
      if (var2 == null) {
         throw new IllegalArgumentException("Component not added to switcher");
      } else {
         return var2;
      }
   }

   public void clearCurrent() {
      if (this.currentComponent != null) {
         if (this.currentComponent.onSwitch != null) {
            this.currentComponent.onSwitch.accept(this.currentComponent.component, false);
         }

         if (this.currentComponent.component instanceof FormSwitchedComponent) {
            ((FormSwitchedComponent)this.currentComponent.component).onSwitched(false);
         }
      }

      this.currentComponent = null;
      Screen.submitNextMoveEvent();
   }

   public void makeCurrent(C var1) {
      this.getSwitch(var1).makeCurrent();
   }

   public <T extends C> T addAndMakeCurrentTemporary(T var1) {
      return this.addAndMakeCurrentTemporary(var1, (Runnable)null);
   }

   public <T extends C> T addAndMakeCurrentTemporary(T var1, Runnable var2) {
      this.addComponent(var1, (var2x, var3) -> {
         if (!var3) {
            this.removeComponent(var2x);
            if (var2 != null) {
               var2.run();
            }
         }

      });
      this.makeCurrent(var1);
      return var1;
   }

   public boolean isCurrent(C var1) {
      return this.getSwitch(var1).isCurrent();
   }

   public C getCurrent() {
      return this.currentComponent == null ? null : this.currentComponent.component;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.currentComponent != null) {
         this.currentComponent.component.draw(var1, var2, var3);
      }

   }

   public List<Rectangle> getHitboxes() {
      return this.currentComponent == null ? Collections.emptyList() : this.currentComponent.component.getHitboxes();
   }

   public boolean shouldDraw() {
      return this.currentComponent != null && this.currentComponent.component.shouldDraw();
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.components.stream().map((var0) -> {
         return var0.component;
      }).forEach(FormComponent::onWindowResized);
   }

   public void dispose() {
      super.dispose();
      this.components.stream().map((var0) -> {
         return var0.component;
      }).forEach(FormComponent::dispose);
   }

   public class SwitchComponent<T extends C> {
      public final T component;
      private final BiConsumer<T, Boolean> onSwitch;

      private SwitchComponent(T var2, BiConsumer<T, Boolean> var3) {
         this.component = var2;
         this.onSwitch = var3;
      }

      public boolean isCurrent() {
         return FormSwitcherTyped.this.currentComponent == this;
      }

      public void makeCurrent() {
         if (FormSwitcherTyped.this.currentComponent != this) {
            SwitchComponent var1 = FormSwitcherTyped.this.currentComponent;
            if (var1 != null) {
               if (var1.onSwitch != null) {
                  var1.onSwitch.accept(var1.component, false);
               }

               if (var1.component instanceof FormSwitchedComponent) {
                  ((FormSwitchedComponent)var1.component).onSwitched(false);
               }
            }

            FormSwitcherTyped.this.currentComponent = this;
            if (this.onSwitch != null) {
               this.onSwitch.accept(this.component, true);
            }

            if (this.component instanceof FormSwitchedComponent) {
               ((FormSwitchedComponent)this.component).onSwitched(true);
            }

            Screen.submitNextMoveEvent();
         }

      }

      // $FF: synthetic method
      SwitchComponent(FormComponent var2, BiConsumer var3, Object var4) {
         this(var2, var3);
      }
   }
}
