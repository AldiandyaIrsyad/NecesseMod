package necesse.gfx.drawables;

import necesse.entity.Entity;

public abstract class EntityDrawable extends LevelSortedDrawable {
   private final Entity entity;

   public EntityDrawable(Entity var1) {
      super(var1, false);
      this.entity = var1;
      this.init();
   }

   public int getSortY() {
      return this.entity.getY();
   }
}
