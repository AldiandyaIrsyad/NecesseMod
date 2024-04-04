package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public class FormComponentListTyped<T extends FormComponent> extends FormComponent implements ComponentListContainer<T> {
   protected boolean isHidden;
   private ComponentList<T> components = new ComponentList<T>() {
      public InputEvent offsetEvent(InputEvent var1, boolean var2) {
         return var1;
      }

      public FormManager getManager() {
         return FormComponentListTyped.this.getManager();
      }
   };

   public FormComponentListTyped() {
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (!this.isHidden()) {
         this.components.submitInputEvent(var1, var2, var3);
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (!this.isHidden()) {
         this.components.submitControllerEvent(var1, var2, var3);
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (!this.isHidden()) {
         if (var6) {
            Screen.drawShape(var5, false, 0.0F, 1.0F, 1.0F, 1.0F);
         }

         this.components.addNextControllerComponents(var1, var2, var3, var4, var5, var6);
      }

   }

   protected void init() {
      super.init();
      this.components.init();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (!this.isHidden()) {
         this.components.drawComponents(var1, var2, var3);
      }

   }

   public List<Rectangle> getHitboxes() {
      return !this.isHidden() ? (List)this.components.stream().flatMap((var0) -> {
         return var0.getHitboxes().stream();
      }).collect(Collectors.toList()) : Collections.emptyList();
   }

   public void setHidden(boolean var1) {
      this.isHidden = var1;
   }

   public boolean isHidden() {
      return this.isHidden;
   }

   public ComponentList<T> getComponentList() {
      return this.components;
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.components.onWindowResized();
   }

   public void dispose() {
      super.dispose();
      this.components.disposeComponents();
   }
}
