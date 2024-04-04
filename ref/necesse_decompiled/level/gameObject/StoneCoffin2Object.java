package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class StoneCoffin2Object extends StoneCoffinObject {
   public StoneCoffin2Object(String var1, String var2, ToolType var3, Color var4) {
      super(var1, var2, var3, var4);
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 0, 1, 2, var1, false, new int[]{this.getID(), this.counterID});
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 20, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32 + 6, 26, 20);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 6, var3 * 32, 20, 26) : new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 26, 20);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      boolean var13 = var8 != null && (Boolean)var8.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
      GameTexture var14 = this.texture;
      if (this.openTexture != null) {
         LevelObject var15 = (LevelObject)this.getMultiTile(var12).getMasterLevelObject(var3, var4, var5).orElse((Object)null);
         if (var15 != null) {
            ObjectEntity var16 = var3.entityManager.getObjectEntity(var15.tileX, var15.tileY);
            if (var16 != null && var16.implementsOEUsers() && ((OEUsers)var16).isInUse()) {
               var14 = this.openTexture;
            }
         }
      }

      final SharedTextureDrawOptions var17 = new SharedTextureDrawOptions(var14);
      if (var12 == 0) {
         var17.addSprite(0, 0, 32, var14.getHeight() - 32).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var14.getHeight() + 64);
      } else if (var12 == 1) {
         var17.addSprite(2, 0, 32, var14.getHeight()).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var14.getHeight() + 32);
      } else if (var12 == 2) {
         var17.addSprite(3, var14.getHeight() / 32 - 1, 32).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11);
      } else {
         var17.addSprite(4, 0, 32, var14.getHeight()).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var14.getHeight() + 32);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var17.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      this.getMultiTile(var1.getObjectRotation(var2, var3)).getMasterLevelObject(var1, var2, var3).ifPresent((var1x) -> {
         var1x.interact(var4);
      });
   }
}
