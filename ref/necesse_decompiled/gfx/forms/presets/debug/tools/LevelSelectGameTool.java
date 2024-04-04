package necesse.gfx.forms.presets.debug.tools;

import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.control.InputEvent;
import necesse.engine.gameTool.GameTool;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class LevelSelectGameTool implements GameTool {
   private int startX;
   private int startY;
   private HudDrawElement hudElement;
   private int selectEventID;

   public LevelSelectGameTool(int var1) {
      this.startX = -1;
      this.startY = -1;
      this.selectEventID = var1;
   }

   public LevelSelectGameTool() {
      this(-100);
   }

   public boolean inputEvent(InputEvent var1) {
      if (var1.getID() == this.selectEventID) {
         if (var1.state && !GlobalData.getCurrentState().getFormManager().isMouseOver(var1)) {
            this.startX = this.getMouseX();
            this.startY = this.getMouseY();
            return true;
         }

         if (this.startX != -1 && this.startY != -1) {
            int var2 = Math.min(this.startX, this.getMouseX());
            int var3 = Math.min(this.startY, this.getMouseY());
            int var4 = Math.max(this.startX, this.getMouseX());
            int var5 = Math.max(this.startY, this.getMouseY());
            this.onSelection(var2, var3, var4, var5);
            this.startX = -1;
            this.startY = -1;
            return true;
         }
      }

      return false;
   }

   public void init() {
      this.startX = -1;
      this.startY = -1;
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            if (LevelSelectGameTool.this.startX != -1 && LevelSelectGameTool.this.startY != -1) {
               final int var4 = Math.min(LevelSelectGameTool.this.startX, LevelSelectGameTool.this.getMouseX());
               final int var5 = Math.min(LevelSelectGameTool.this.startY, LevelSelectGameTool.this.getMouseY());
               final int var6 = Math.max(LevelSelectGameTool.this.startX, LevelSelectGameTool.this.getMouseX());
               final int var7 = Math.max(LevelSelectGameTool.this.startY, LevelSelectGameTool.this.getMouseY());
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return -10000;
                  }

                  public void draw(TickManager var1) {
                     LevelSelectGameTool.this.drawSelection(var2, var3, var4, var5, var6, var7);
                  }
               });
            }

         }
      });
   }

   public abstract Level getLevel();

   public abstract int getMouseX();

   public abstract int getMouseY();

   public abstract void onSelection(int var1, int var2, int var3, int var4);

   public abstract void drawSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);

   public void isCancelled() {
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }

   public void isCleared() {
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }
}
