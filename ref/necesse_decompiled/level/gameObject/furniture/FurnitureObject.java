package necesse.level.gameObject.furniture;

import java.awt.Rectangle;
import necesse.level.gameObject.GameObject;

public class FurnitureObject extends GameObject implements RoomFurniture {
   public String furnitureType = null;

   public FurnitureObject() {
      this.roomProperties.add("furniture");
      this.setItemCategory(new String[]{"objects", "furniture"});
      this.displayMapTooltip = true;
      this.replaceCategories.add("furniture");
      this.canReplaceCategories.add("furniture");
      this.canReplaceCategories.add("column");
      this.canReplaceCategories.add("torch");
   }

   public FurnitureObject(Rectangle var1) {
      super(var1);
      this.roomProperties.add("furniture");
      this.setItemCategory(new String[]{"objects", "furniture"});
      this.displayMapTooltip = true;
      this.replaceCategories.add("furniture");
      this.canReplaceCategories.add("furniture");
      this.canReplaceCategories.add("column");
      this.canReplaceCategories.add("torch");
   }

   public String getFurnitureType() {
      return this.furnitureType;
   }
}
