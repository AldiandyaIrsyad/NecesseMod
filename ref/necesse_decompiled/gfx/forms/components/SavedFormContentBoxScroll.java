package necesse.gfx.forms.components;

public class SavedFormContentBoxScroll {
   protected boolean set;
   protected int scrollX;
   protected int scrollY;

   public SavedFormContentBoxScroll() {
   }

   public void save(FormContentBox var1) {
      this.scrollX = var1.getScrollX();
      this.scrollY = var1.getScrollY();
      this.set = true;
   }

   public void load(FormContentBox var1) {
      if (this.set) {
         var1.setScrollX(this.scrollX);
         var1.setScrollY(this.scrollY);
         this.set = false;
      }

   }
}
