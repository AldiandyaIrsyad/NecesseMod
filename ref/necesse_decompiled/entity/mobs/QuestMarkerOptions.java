package necesse.entity.mobs;

import java.awt.Color;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.level.maps.light.GameLight;

public class QuestMarkerOptions {
   public static Color orangeColor = new Color(200, 200, 50);
   public static Color grayColor = new Color(50, 50, 50);
   public final String icons;
   public final Color color;

   public QuestMarkerOptions(String var1, Color var2) {
      this.icons = var1;
      this.color = var2;
   }

   public QuestMarkerOptions(char var1, Color var2) {
      this(Character.toString(var1), var2);
   }

   public QuestMarkerOptions combine(QuestMarkerOptions var1) {
      StringBuilder var2 = (new StringBuilder()).append(this.icons);

      for(int var3 = 0; var3 < var1.icons.length(); ++var3) {
         char var4 = var1.icons.charAt(var3);
         if (var2.indexOf(String.valueOf(var4)) == -1) {
            var2.append(var4);
         }
      }

      float var6 = (float)this.color.getRed() * 0.2126F + (float)this.color.getGreen() * 0.7152F + (float)this.color.getBlue() * 0.0722F;
      float var7 = (float)var1.color.getRed() * 0.2126F + (float)var1.color.getGreen() * 0.7152F + (float)var1.color.getBlue() * 0.0722F;
      Color var5 = var6 > var7 ? this.color : var1.color;
      return new QuestMarkerOptions(var2.toString(), var5);
   }

   public static QuestMarkerOptions combine(QuestMarkerOptions var0, QuestMarkerOptions var1) {
      if (var0 == null) {
         return var1;
      } else {
         return var1 == null ? var0 : var0.combine(var1);
      }
   }

   public DrawOptions getDrawOptions(int var1, int var2, GameLight var3, GameCamera var4, int var5, int var6) {
      return QuestGiver.getMarkerDrawOptions(this.icons, this.color, var1, var2, var3, var4, var5, var6);
   }
}
