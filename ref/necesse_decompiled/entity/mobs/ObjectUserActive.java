package necesse.entity.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;

public abstract class ObjectUserActive {
   private Mob mob;
   public final Level level;
   public final int tileX;
   public final int tileY;
   public final ObjectUsersObject object;

   public ObjectUserActive(Level var1, int var2, int var3, ObjectUsersObject var4) {
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
      this.object = var4;
   }

   public void init(Mob var1) {
      if (this.mob != null) {
         throw new IllegalStateException("Cannot initiate ObjectUserActive twice");
      } else {
         this.mob = var1;
      }
   }

   public final Mob mob() {
      return this.mob;
   }

   public void tick() {
      if (this.isValid()) {
         this.object.tickUser(this.level, this.tileX, this.tileY, this.mob);
      } else {
         this.stopUsing();
      }

   }

   public abstract void keepUsing();

   public void stopUsing() {
      this.object.stopUsing(this.level, this.tileX, this.tileY, this.mob);
   }

   public boolean isValid() {
      return this.object.isValidUser(this.level, this.tileX, this.tileY, this.mob);
   }

   public Rectangle getUserSelectBox() {
      return this.object.getUserSelectBox(this.level, this.tileX, this.tileY, this.mob);
   }

   public Point getUserAppearancePos() {
      return this.object.getUserAppearancePos(this.level, this.tileX, this.tileY, this.mob);
   }
}
