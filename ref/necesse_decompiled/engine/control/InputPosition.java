package necesse.engine.control;

import java.awt.Point;
import java.util.function.Function;
import necesse.engine.GameWindow;

public class InputPosition {
   public final int windowX;
   public final int windowY;
   public final int sceneX;
   public final int sceneY;
   public final int hudX;
   public final int hudY;

   InputPosition(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.windowX = var1;
      this.windowY = var2;
      this.sceneX = var3;
      this.sceneY = var4;
      this.hudX = var5;
      this.hudY = var6;
   }

   public static InputPosition dummyPos() {
      return new InputPosition(0, 0, 0, 0, 0, 0);
   }

   public static InputPosition fromWindowPos(Input var0, int var1, int var2) {
      return fromWindowPos(var0.window, var1, var2);
   }

   public static InputPosition fromWindowPos(GameWindow var0, int var1, int var2) {
      int var3 = (int)((float)var1 * (float)var0.getSceneWidth() / (float)var0.getWidth());
      int var4 = (int)((float)var2 * (float)var0.getSceneHeight() / (float)var0.getHeight());
      int var5 = (int)((float)var1 * (float)var0.getHudWidth() / (float)var0.getWidth());
      int var6 = (int)((float)var2 * (float)var0.getHudHeight() / (float)var0.getHeight());
      return new InputPosition(var1, var2, var3, var4, var5, var6);
   }

   public static InputPosition fromScenePos(Input var0, int var1, int var2) {
      return fromScenePos(var0.window, var1, var2);
   }

   public static InputPosition fromScenePos(GameWindow var0, int var1, int var2) {
      int var3 = (int)((float)var1 / ((float)var0.getSceneWidth() / (float)var0.getWidth()));
      int var4 = (int)((float)var2 / ((float)var0.getSceneHeight() / (float)var0.getHeight()));
      int var5 = (int)((float)var3 * (float)var0.getHudWidth() / (float)var0.getWidth());
      int var6 = (int)((float)var4 * (float)var0.getHudHeight() / (float)var0.getHeight());
      return new InputPosition(var3, var4, var1, var2, var5, var6);
   }

   public static InputPosition fromHudPos(Input var0, int var1, int var2) {
      return fromHudPos(var0.window, var1, var2);
   }

   public static InputPosition fromHudPos(GameWindow var0, int var1, int var2) {
      int var3 = (int)((float)var1 / ((float)var0.getHudWidth() / (float)var0.getWidth()));
      int var4 = (int)((float)var2 / ((float)var0.getHudHeight() / (float)var0.getHeight()));
      int var5 = (int)((float)var3 * (float)var0.getSceneWidth() / (float)var0.getWidth());
      int var6 = (int)((float)var4 * (float)var0.getSceneHeight() / (float)var0.getHeight());
      return new InputPosition(var3, var4, var5, var6, var1, var2);
   }

   public static enum InputPositionGetter {
      WINDOW((var0) -> {
         return new Point(var0.windowX, var0.windowY);
      }),
      SCENE((var0) -> {
         return new Point(var0.sceneX, var0.sceneY);
      }),
      HUD((var0) -> {
         return new Point(var0.hudX, var0.hudY);
      });

      private final Function<InputPosition, Point> getter;

      private InputPositionGetter(Function var3) {
         this.getter = var3;
      }

      public Point get(InputPosition var1) {
         return (Point)this.getter.apply(var1);
      }

      public Point get(InputEvent var1) {
         return this.get(var1.pos);
      }

      // $FF: synthetic method
      private static InputPositionGetter[] $values() {
         return new InputPositionGetter[]{WINDOW, SCENE, HUD};
      }
   }
}
