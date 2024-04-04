package necesse.gfx.forms;

import java.util.Collection;
import java.util.function.Predicate;
import necesse.gfx.forms.components.FormComponent;

public interface ComponentListContainer<T extends FormComponent> {
   ComponentList<T> getComponentList();

   default void clearComponents() {
      this.getComponentList().clearComponents();
   }

   default boolean removeComponentsIf(Predicate<T> var1) {
      return this.getComponentList().removeComponentsIf(var1);
   }

   default void removeComponent(T var1) {
      this.getComponentList().removeComponent(var1);
   }

   default boolean hasComponent(T var1) {
      return this.getComponentList().hasComponent(var1);
   }

   default Collection<T> getComponents() {
      return this.getComponentList().getComponents();
   }

   default <M extends T> M addComponent(M var1) {
      return this.getComponentList().addComponent(var1);
   }

   default <M extends T> M addComponent(M var1, int var2) {
      return this.getComponentList().addComponent(var1, var2);
   }
}
