package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.objectEntity.TempleExitObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class TempleExitObject extends StaticMultiObject {
   private TempleExitObject(int var1, int var2, int var3, int var4, int[] var5, Rectangle var6) {
      super(var1, var2, var3, var4, var5, var6, "templeexit");
      this.mapColor = new Color(122, 102, 60);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.lightLevel = 100;
      this.toolType = ToolType.UNBREAKABLE;
      this.isLightTransparent = true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer() && var4.isServerClient()) {
         LevelObject var5 = (LevelObject)this.getMultiTile(var1, var2, var3).getMasterLevelObject(var1, var2, var3).orElse((Object)null);
         if (var5 != null) {
            ObjectEntity var6 = var1.entityManager.getObjectEntity(var5.tileX, var5.tileY);
            if (var6 instanceof PortalObjectEntity) {
               ((PortalObjectEntity)var6).use(var1.getServer(), var4.getServerClient());
            }
         }
      }

      super.interact(var1, var2, var3, var4);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return (ObjectEntity)(this.isMultiTileMaster() ? new TempleExitObjectEntity(var1, var2, var3, 10, 10) : super.getNewObjectEntity(var1, var2, var3));
   }

   public static int[] registerTempleExit() {
      int[] var0 = new int[6];
      Rectangle var1 = new Rectangle(8, 8, 82, 56);
      var0[0] = ObjectRegistry.registerObject("templeexit", new TempleExitObject(0, 0, 3, 2, var0, var1), 0.0F, false);
      var0[1] = ObjectRegistry.registerObject("templeexit2", new TempleExitObject(1, 0, 3, 2, var0, var1), 0.0F, false);
      var0[2] = ObjectRegistry.registerObject("templeexit3", new TempleExitObject(2, 0, 3, 2, var0, var1), 0.0F, false);
      var0[3] = ObjectRegistry.registerObject("templeexit4", new TempleExitObject(0, 1, 3, 2, var0, var1), 0.0F, false);
      var0[4] = ObjectRegistry.registerObject("templeexit5", new TempleExitObject(1, 1, 3, 2, var0, var1), 0.0F, false);
      var0[5] = ObjectRegistry.registerObject("templeexit6", new TempleExitObject(2, 1, 3, 2, var0, var1), 0.0F, false);
      return var0;
   }
}
