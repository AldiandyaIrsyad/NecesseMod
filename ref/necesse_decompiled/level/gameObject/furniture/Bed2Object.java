package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class Bed2Object extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;
   protected int counterID;

   public Bed2Object(String var1, ToolType var2, Color var3) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 0, 1, 2, var1, false, new int[]{this.getID(), this.counterID});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 28, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32 + 6, 30, 24);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 2, var3 * 32, 28, 30) : new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 30, 24);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      final DrawOptionsList var13 = new DrawOptionsList();
      if (var12 == 0) {
         var13.add(this.texture.initDraw().sprite(3, 1, 32).light(var9).pos(var10, var11 - 32));
         var13.add(this.texture.initDraw().sprite(3, 2, 32).light(var9).pos(var10, var11));
      } else if (var12 == 1) {
         var13.add(this.texture.initDraw().sprite(1, 1, 32, 64).light(var9).pos(var10, var11 - 32));
      } else if (var12 == 2) {
         var13.add(this.texture.initDraw().sprite(2, 3, 32).light(var9).pos(var10, var11));
      } else {
         var13.add(this.texture.initDraw().sprite(0, 0, 32, 64).light(var9).pos(var10, var11 - 32));
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      super.interact(var1, var2, var3, var4);
      this.getMultiTile(var1.getObjectRotation(var2, var3)).getMasterLevelObject(var1, var2, var3).ifPresent((var1x) -> {
         var1x.interact(var4);
      });
   }
}
