package necesse.gfx.camera;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.entity.Entity;
import necesse.level.maps.Level;

public class GameCamera {
   protected int x;
   protected int y;
   protected int width;
   protected int height;

   public GameCamera(int var1, int var2, int var3, int var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public GameCamera(int var1, int var2) {
      this(var1, var2, Screen.getSceneWidth(), Screen.getSceneHeight());
   }

   public GameCamera() {
      this(0, 0);
   }

   public void updateToSceneDimensions() {
      this.setDimensions(Screen.getSceneWidth(), Screen.getSceneHeight());
   }

   public void setPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void setDimensions(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public void limitToLevel(Level var1) {
      if (this.width <= var1.width * 32) {
         if (this.x < 0) {
            this.x = 0;
         } else if (this.x > var1.width * 32 - this.width) {
            this.x = var1.width * 32 - this.width;
         }
      }

      if (this.height <= var1.height * 32) {
         if (this.y < 0) {
            this.y = 0;
         } else if (this.y > var1.height * 32 - this.height) {
            this.y = var1.height * 32 - this.height;
         }
      }

   }

   public int getDrawX(int var1) {
      return var1 - this.getX();
   }

   public int getDrawY(int var1) {
      return var1 - this.getY();
   }

   public int getDrawX(float var1) {
      return this.getDrawX((int)var1);
   }

   public int getDrawY(float var1) {
      return this.getDrawY((int)var1);
   }

   public int getTileDrawX(int var1) {
      return this.getDrawX(var1 * 32);
   }

   public int getTileDrawY(int var1) {
      return this.getDrawY(var1 * 32);
   }

   public int getMouseLevelPosX(InputPosition var1) {
      return var1.sceneX + this.getX();
   }

   public int getMouseLevelPosX(InputEvent var1) {
      return this.getMouseLevelPosX(var1.pos);
   }

   public int getMouseLevelPosX() {
      return this.getMouseLevelPosX(Screen.mousePos());
   }

   public int getMouseLevelPosY(InputPosition var1) {
      return var1.sceneY + this.getY();
   }

   public int getMouseLevelPosY(InputEvent var1) {
      return this.getMouseLevelPosY(var1.pos);
   }

   public int getMouseLevelPosY() {
      return this.getMouseLevelPosY(Screen.mousePos());
   }

   public int getMouseLevelTilePosX(InputPosition var1) {
      return this.getMouseLevelPosX(var1) / 32;
   }

   public int getMouseLevelTilePosX(InputEvent var1) {
      return this.getMouseLevelPosX(var1) / 32;
   }

   public int getMouseLevelTilePosX() {
      return this.getMouseLevelPosX() / 32;
   }

   public int getMouseLevelTilePosY(InputPosition var1) {
      return this.getMouseLevelPosY(var1) / 32;
   }

   public int getMouseLevelTilePosY(InputEvent var1) {
      return this.getMouseLevelPosY(var1) / 32;
   }

   public int getMouseLevelTilePosY() {
      return this.getMouseLevelPosY() / 32;
   }

   public Point getMouseLevelPos(InputPosition var1) {
      return new Point(this.getMouseLevelPosX(var1), this.getMouseLevelPosY(var1));
   }

   public Point getMouseLevelPos(InputEvent var1) {
      return new Point(this.getMouseLevelPosX(var1), this.getMouseLevelPosY(var1));
   }

   public Point getMouseLevelPos() {
      return new Point(this.getMouseLevelPosX(), this.getMouseLevelPosY());
   }

   public Point getMouseLevelTilePos(InputPosition var1) {
      return new Point(this.getMouseLevelTilePosX(var1), this.getMouseLevelTilePosY(var1));
   }

   public Point getMouseLevelTilePos(InputEvent var1) {
      return new Point(this.getMouseLevelTilePosX(var1), this.getMouseLevelTilePosY(var1));
   }

   public Point getMouseLevelTilePos() {
      return new Point(this.getMouseLevelTilePosX(), this.getMouseLevelTilePosY());
   }

   public void centerCamera(int var1, int var2) {
      this.setPosition(var1 - this.width / 2, var2 - this.height / 2);
   }

   public void centerCamera(Point var1) {
      this.centerCamera(var1.x, var1.y);
   }

   public void centerCamera(Entity var1) {
      this.centerCamera(var1.getX(), var1.getY());
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Rectangle getBounds() {
      return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }
}
