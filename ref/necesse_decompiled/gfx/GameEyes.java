package necesse.gfx;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.gameTexture.GameTexture;

public class GameEyes {
   public static GameTexture eyesTexture;
   private static ArrayList<EyeColor> colors;

   public GameEyes() {
   }

   public static void loadTextures() {
      colors = new ArrayList();
      eyesTexture = GameTexture.fromFile("player/eyes/eyes");
      String var0 = "player/eyes/eyecolors";

      try {
         GameTexture var1 = GameTexture.fromFileRaw(var0, true);

         for(int var2 = 0; var2 < var1.getHeight(); ++var2) {
            Color var3 = var1.getColor(0, var2);
            int var4 = (GameMath.max(var3.getRed(), var3.getGreen(), var3.getBlue()) + GameMath.min(var3.getRed(), var3.getGreen(), var3.getBlue())) / 2;
            colors.add(new EyeColor(var4, var1.getColor(1, var2)));
         }

      } catch (FileNotFoundException var5) {
         throw new RuntimeException("Could not find eye colors texture file at " + var0);
      }
   }

   public static int getTotalColors() {
      return colors.size();
   }

   public static Color getEyeColor(int var0) {
      int var1 = Math.floorMod(var0, colors.size());
      return ((EyeColor)colors.get(var1)).color;
   }

   public static int getRandomEyeColor(GameRandom var0) {
      if (colors != null && !colors.isEmpty()) {
         TicketSystemList var1 = new TicketSystemList();

         for(int var2 = 0; var2 < colors.size(); ++var2) {
            var1.addObject(((EyeColor)colors.get(var2)).weight, var2);
         }

         return (Integer)var1.getRandomObject(var0);
      } else {
         return var0.nextInt();
      }
   }

   private static class EyeColor {
      public final int weight;
      public final Color color;

      public EyeColor(int var1, Color var2) {
         this.weight = var1;
         this.color = var2;
      }
   }
}
