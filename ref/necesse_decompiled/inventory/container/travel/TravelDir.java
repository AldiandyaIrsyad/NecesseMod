package necesse.inventory.container.travel;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;

public enum TravelDir {
   NorthWest("ui", "dirnorthwest"),
   North("ui", "dirnorth"),
   NorthEast("ui", "dirnortheast"),
   West("ui", "dirwest"),
   East("ui", "direast"),
   SouthWest("ui", "dirsouthwest"),
   South("ui", "dirsouth"),
   SouthEast("ui", "dirsoutheast"),
   All(new StaticMessage("DIR_ALL")),
   None(new StaticMessage("DIR_NONE"));

   public final GameMessage dirMessage;
   public final GameMessage travelMessage;

   private TravelDir(GameMessage var3) {
      this.dirMessage = var3;
      this.travelMessage = new LocalMessage("ui", "traveldir", "dir", var3);
   }

   private TravelDir(String var3, String var4) {
      this(new LocalMessage(var3, var4));
   }

   public static TravelDir getDeltaDir(int var0, int var1, int var2, int var3) {
      return getDeltaDir(var0, var1, var2, var3, None);
   }

   public static TravelDir getDeltaDir(int var0, int var1, int var2, int var3, TravelDir var4) {
      if (var3 < var1) {
         if (var2 < var0) {
            return NorthWest;
         } else {
            return var2 > var0 ? NorthEast : North;
         }
      } else if (var3 > var1) {
         if (var2 < var0) {
            return SouthWest;
         } else {
            return var2 > var0 ? SouthEast : South;
         }
      } else if (var2 < var0) {
         return West;
      } else {
         return var2 > var0 ? East : var4;
      }
   }

   // $FF: synthetic method
   private static TravelDir[] $values() {
      return new TravelDir[]{NorthWest, North, NorthEast, West, East, SouthWest, South, SouthEast, All, None};
   }
}
