package necesse.gfx.forms.presets;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.position.FormPositionContainer;

public class ContentBoxListManager {
   public final FormContentBox contentBox;
   private final LinkedList<FormPositionContainer> components = new LinkedList();
   private FormFlow flow;

   public ContentBoxListManager(FormContentBox var1) {
      this.contentBox = var1;
      this.flow = new FormFlow();
   }

   public <T extends FormPositionContainer> T add(T var1) {
      return this.add(var1, 0);
   }

   public <T extends FormPositionContainer> T add(T var1, int var2) {
      this.contentBox.addComponent((FormComponent)var1);
      this.components.add(var1);
      return this.flow.nextY(var1, var2);
   }

   public void clear() {
      Iterator var1 = this.components.iterator();

      while(var1.hasNext()) {
         FormPositionContainer var2 = (FormPositionContainer)var1.next();
         this.contentBox.removeComponent((FormComponent)var2);
      }

      this.components.clear();
      this.flow = new FormFlow();
   }

   public int size() {
      return this.components.size();
   }

   public boolean isEmpty() {
      return this.components.isEmpty();
   }

   public void updatePositions() {
      this.flow = new FormFlow();
      Iterator var1 = this.components.iterator();

      while(var1.hasNext()) {
         FormPositionContainer var2 = (FormPositionContainer)var1.next();
         this.flow.nextY(var2);
      }

   }

   public void fit(int var1) {
      this.contentBox.fitContentBoxToComponents(var1);
   }

   public void fit(int var1, int var2, int var3, int var4) {
      this.contentBox.fitContentBoxToComponents(var1, var2, var3, var4);
   }

   public void fit() {
      this.contentBox.fitContentBoxToComponents();
   }
}
