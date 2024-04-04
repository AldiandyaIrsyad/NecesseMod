package necesse.level.maps.hudManager.floatText;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class HungryFloatText extends FloatText {
   private Mob mob;
   private long removeTime;

   public HungryFloatText(Mob var1) {
      this.mob = var1;
   }

   public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
      if (!this.isRemoved() && !this.mob.removed()) {
         if (this.getTime() >= this.removeTime) {
            this.remove();
         } else if (var2.getBounds().intersects(this.getCollision())) {
            final DrawOptionsList var4 = new DrawOptionsList();
            int var5 = var2.getDrawX(this.getX());
            int var6 = var2.getDrawY(this.getY());
            int var7 = (Settings.UI.food_outline.getWidth() - Settings.UI.food_fill.getWidth()) / 2;
            int var8 = (Settings.UI.food_outline.getHeight() - Settings.UI.food_fill.getHeight()) / 2;
            float var9 = GameMath.lerp(GameUtils.getAnimFloatContinuous(this.removeTime - this.getTime(), 1000), 0.2F, 1.0F);
            var4.add(Settings.UI.food_outline.initDraw().alpha(var9).pos(var5, var6));
            var4.add(Settings.UI.food_fill.initDraw().alpha(var9).pos(var5 + var7, var6 + var8));
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return 0;
               }

               public void draw(TickManager var1) {
                  var4.draw();
               }
            });
         }
      }
   }

   public int getX() {
      return this.mob.getDrawX() - Settings.UI.food_outline.getWidth() / 2;
   }

   public int getY() {
      return this.mob.getDrawY() - (this.mob.isHealthBarVisible() ? 65 : 50) - Settings.UI.food_outline.getHeight() / 2;
   }

   public int getWidth() {
      return Settings.UI.food_outline.getWidth();
   }

   public int getHeight() {
      return Settings.UI.food_outline.getHeight();
   }

   public void addThis(Level var1, ArrayList<HudDrawElement> var2) {
      super.addThis(var1, var2);
      long var3 = 6000L;
      this.removeTime = this.getTime() + var3;
   }
}
