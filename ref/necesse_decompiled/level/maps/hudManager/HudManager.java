package necesse.level.maps.hudManager;

import java.util.ArrayList;
import java.util.List;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.Level;

public class HudManager {
   private final Level level;
   private ArrayList<HudDrawElement> list;

   public HudManager(Level var1) {
      this.level = var1;
      this.list = new ArrayList();
   }

   public void tick() {
      for(int var1 = 0; var1 < this.list.size(); ++var1) {
         HudDrawElement var2 = (HudDrawElement)this.list.get(var1);
         if (var2.isRemoved()) {
            this.list.remove(var1);
            var2.onRemove();
            --var1;
         }
      }

   }

   public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
      HudDrawElement[] var4 = (HudDrawElement[])this.list.toArray(new HudDrawElement[0]);
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         HudDrawElement var7 = var4[var6];
         if (var7 != null && !var7.isRemoved()) {
            var7.addDrawables(var1, var2, var3);
         }
      }

   }

   public <T extends HudDrawElement> T addElement(T var1) {
      if (var1 != null && !this.level.isServer()) {
         var1.addThis(this.level, this.list);
         return var1;
      } else {
         return null;
      }
   }
}
