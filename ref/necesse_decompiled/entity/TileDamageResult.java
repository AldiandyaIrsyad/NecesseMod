package necesse.entity;

import java.util.ArrayList;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;

public class TileDamageResult {
   public final DamagedObjectEntity damagedObjectEntity;
   public final LevelTile levelTile;
   public final LevelObject levelObject;
   public final TileDamageType type;
   public final int addedDamage;
   public final boolean showEffects;
   public final int mouseX;
   public final int mouseY;
   public final boolean destroyed;
   public final ArrayList<ItemPickupEntity> itemsDropped;

   public TileDamageResult(DamagedObjectEntity var1, LevelTile var2, LevelObject var3, TileDamageType var4, int var5, boolean var6, ArrayList<ItemPickupEntity> var7, boolean var8, int var9, int var10) {
      this.damagedObjectEntity = var1;
      this.levelTile = var2;
      this.levelObject = var3;
      this.type = var4;
      this.addedDamage = var5;
      this.destroyed = var6;
      this.itemsDropped = var7;
      this.showEffects = var8;
      this.mouseX = var9;
      this.mouseY = var10;
   }

   public int getTileX() {
      return this.damagedObjectEntity.getX();
   }

   public int getTileY() {
      return this.damagedObjectEntity.getY();
   }
}
