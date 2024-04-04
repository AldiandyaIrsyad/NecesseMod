package necesse.gfx.forms.components.lists;

import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class FormTestList extends FormGeneralList<TestElement> {
   private int itemCounter = 0;

   public FormTestList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 25);

      for(int var5 = 0; var5 < 10; ++var5) {
         this.addElement();
      }

   }

   public void deleteElement() {
      this.elements.remove(this.elements.size() - 1);
   }

   public void addElement() {
      this.elements.add(new TestElement(this.itemCounter++));
   }

   public class TestElement extends FormListElement<FormTestList> {
      private int index;

      public TestElement(int var2) {
         this.index = var2;
      }

      void draw(FormTestList var1, TickManager var2, PlayerMob var3, int var4) {
         float var5 = this.isMouseOver(var1) ? 1.0F : 0.8F;
         FontOptions var6 = (new FontOptions(16)).colorf(var5, var5, var5);
         FontManager.bit.drawString(4.0F, 2.0F, "Item #" + this.index, var6);
      }

      void onClick(FormTestList var1, int var2, InputEvent var3, PlayerMob var4) {
         System.out.println("Clicked " + var2 + ", " + var3.pos.hudX + ", " + var3.pos.hudY + " with buttton " + var3.getID());
      }

      void onControllerEvent(FormTestList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         System.out.println("Clicked " + var2 + ", with controller " + var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormTestList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormTestList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormTestList)var1, var2, var3, var4);
      }
   }
}
