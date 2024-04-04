package necesse.level.maps;

import java.awt.Rectangle;
import necesse.engine.control.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;

public abstract class MapDrawElement {
   private Level level;
   private boolean removed;
   protected int x;
   protected int y;
   protected Rectangle boundingBox;

   public MapDrawElement(int var1, int var2, Rectangle var3) {
      this.x = var1;
      this.y = var2;
      this.boundingBox = var3;
   }

   public void init(Level var1) {
      if (this.level != var1) {
         if (this.level != null) {
            throw new IllegalStateException("Cannot add the same map draw element to different levels");
         } else {
            this.level = var1;
         }
      }
   }

   protected final Level getLevel() {
      return this.level;
   }

   public final void remove() {
      if (!this.removed) {
         this.removed = true;
      }
   }

   public final boolean isRemoved() {
      return this.removed;
   }

   public boolean shouldRemove() {
      return false;
   }

   public void onRemove() {
   }

   public final long getTime() {
      return this.level.getWorldEntity().getLocalTime();
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public Rectangle getBoundingBox() {
      return this.boundingBox;
   }

   public abstract void draw(int var1, int var2, PlayerMob var3);

   public abstract GameTooltips getTooltips(int var1, int var2, PlayerMob var3);

   public String getMapInteractTooltip() {
      return null;
   }

   public void onMapInteract(InputEvent var1, PlayerMob var2) {
   }
}
