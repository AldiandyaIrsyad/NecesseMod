package necesse.gfx.drawOptions;

import java.util.ArrayList;
import java.util.List;

public class DrawOptionsList extends ArrayList<DrawOptions> implements DrawOptions {
   public DrawOptionsList(List<DrawOptions> var1) {
      super(var1);
   }

   public DrawOptionsList(int var1) {
      super(var1);
   }

   public DrawOptionsList() {
   }

   public void draw() {
      this.forEach(DrawOptions::draw);
   }
}
