package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ModularTableObject extends TableObject {
   protected String textureName;
   public GameTexture texture;

   public ModularTableObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(4, 4, 24, 24), var3);
      this.textureName = var1;
      this.toolType = var2;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public ModularTableObject(String var1, Color var2) {
      this(var1, ToolType.ALL, var2);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - 14;
      GameObject[] var12 = var3.getAdjacentObjects(var4, var5);
      final ArrayList var13 = new ArrayList();
      if (this.same(var12[1]) && this.same(var12[3])) {
         if (!this.same(var12[0])) {
            var13.add(this.texture.initDraw().sprite(2, 0, 16).light(var9).pos(var10, var11));
         } else {
            var13.add(this.texture.initDraw().sprite(0, 2, 16).light(var9).pos(var10, var11));
         }
      } else if (!this.same(var12[1]) && this.same(var12[3])) {
         var13.add(this.texture.initDraw().sprite(2, 2, 16).light(var9).pos(var10, var11));
      } else if (this.same(var12[1]) && !this.same(var12[3])) {
         if (this.same(var12[0])) {
            var13.add(this.texture.initDraw().sprite(4, 0, 16).light(var9).pos(var10, var11));
         } else {
            var13.add(this.texture.initDraw().sprite(4, 1, 16).light(var9).pos(var10, var11));
         }
      } else {
         var13.add(this.texture.initDraw().sprite(0, 0, 16).light(var9).pos(var10, var11));
      }

      if (this.same(var12[1]) && this.same(var12[4])) {
         if (!this.same(var12[2])) {
            var13.add(this.texture.initDraw().sprite(3, 0, 16).light(var9).pos(var10 + 16, var11));
         } else {
            var13.add(this.texture.initDraw().sprite(1, 2, 16).light(var9).pos(var10 + 16, var11));
         }
      } else if (!this.same(var12[1]) && this.same(var12[4])) {
         var13.add(this.texture.initDraw().sprite(3, 2, 16).light(var9).pos(var10 + 16, var11));
      } else if (this.same(var12[1]) && !this.same(var12[4])) {
         if (this.same(var12[2])) {
            var13.add(this.texture.initDraw().sprite(5, 0, 16).light(var9).pos(var10 + 16, var11));
         } else {
            var13.add(this.texture.initDraw().sprite(5, 1, 16).light(var9).pos(var10 + 16, var11));
         }
      } else {
         var13.add(this.texture.initDraw().sprite(1, 0, 16).light(var9).pos(var10 + 16, var11));
      }

      if (this.same(var12[6]) && this.same(var12[3])) {
         if (!this.same(var12[5])) {
            var13.add(this.texture.initDraw().sprite(2, 1, 16).light(var9).pos(var10, var11 + 16));
         } else {
            var13.add(this.texture.initDraw().sprite(0, 3, 16).light(var9).pos(var10, var11 + 16));
         }
      } else if (!this.same(var12[6]) && this.same(var12[3])) {
         var13.add(this.texture.initDraw().sprite(2, 3, 16).light(var9).pos(var10, var11 + 16));
      } else if (this.same(var12[6]) && !this.same(var12[3])) {
         var13.add(this.texture.initDraw().sprite(4, 1, 16).light(var9).pos(var10, var11 + 16));
      } else {
         var13.add(this.texture.initDraw().sprite(0, 1, 16).light(var9).pos(var10, var11 + 16));
      }

      if (this.same(var12[6]) && this.same(var12[4])) {
         if (!this.same(var12[7])) {
            var13.add(this.texture.initDraw().sprite(3, 1, 16).light(var9).pos(var10 + 16, var11 + 16));
         } else {
            var13.add(this.texture.initDraw().sprite(1, 3, 16).light(var9).pos(var10 + 16, var11 + 16));
         }
      } else if (!this.same(var12[6]) && this.same(var12[4])) {
         var13.add(this.texture.initDraw().sprite(3, 3, 16).light(var9).pos(var10 + 16, var11 + 16));
      } else if (this.same(var12[6]) && !this.same(var12[4])) {
         var13.add(this.texture.initDraw().sprite(5, 1, 16).light(var9).pos(var10 + 16, var11 + 16));
      } else {
         var13.add(this.texture.initDraw().sprite(1, 1, 16).light(var9).pos(var10 + 16, var11 + 16));
      }

      if (!this.same(var12[6])) {
         boolean var14 = this.same(var12[3]);
         boolean var15 = this.same(var12[4]);
         if (var14 && var15) {
            var13.add(this.texture.initDraw().sprite(2, 3, 32, 16).light(var9).pos(var10, var11 + 16 + 10));
         } else if (var14) {
            var13.add(this.texture.initDraw().sprite(4, 3, 16).light(var9).pos(var10, var11 + 16 + 10));
            var13.add(this.texture.initDraw().sprite(5, 2, 16).light(var9).pos(var10 + 16, var11 + 16 + 10));
         } else if (var15) {
            var13.add(this.texture.initDraw().sprite(4, 2, 16).light(var9).pos(var10, var11 + 16 + 10));
            var13.add(this.texture.initDraw().sprite(5, 3, 16).light(var9).pos(var10 + 16, var11 + 16 + 10));
         } else {
            var13.add(this.texture.initDraw().sprite(2, 2, 32, 16).light(var9).pos(var10, var11 + 16 + 10));
         }
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var13.forEach(TextureDrawOptions::draw);
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3) - 14;
      GameObject[] var10 = var1.getAdjacentObjects(var2, var3);
      if (this.same(var10[1]) && this.same(var10[3])) {
         if (!this.same(var10[0])) {
            this.texture.initDraw().sprite(2, 0, 16).alpha(var5).draw(var8, var9);
         } else {
            this.texture.initDraw().sprite(0, 2, 16).alpha(var5).draw(var8, var9);
         }
      } else if (!this.same(var10[1]) && this.same(var10[3])) {
         this.texture.initDraw().sprite(2, 2, 16).alpha(var5).draw(var8, var9);
      } else if (this.same(var10[1]) && !this.same(var10[3])) {
         if (this.same(var10[0])) {
            this.texture.initDraw().sprite(4, 0, 16).alpha(var5).draw(var8, var9);
         } else {
            this.texture.initDraw().sprite(4, 1, 16).alpha(var5).draw(var8, var9);
         }
      } else {
         this.texture.initDraw().sprite(0, 0, 16).alpha(var5).draw(var8, var9);
      }

      if (this.same(var10[1]) && this.same(var10[4])) {
         if (!this.same(var10[2])) {
            this.texture.initDraw().sprite(3, 0, 16).alpha(var5).draw(var8 + 16, var9);
         } else {
            this.texture.initDraw().sprite(1, 2, 16).alpha(var5).draw(var8 + 16, var9);
         }
      } else if (!this.same(var10[1]) && this.same(var10[4])) {
         this.texture.initDraw().sprite(3, 2, 16).alpha(var5).draw(var8 + 16, var9);
      } else if (this.same(var10[1]) && !this.same(var10[4])) {
         if (this.same(var10[2])) {
            this.texture.initDraw().sprite(5, 0, 16).alpha(var5).draw(var8 + 16, var9);
         } else {
            this.texture.initDraw().sprite(5, 1, 16).alpha(var5).draw(var8 + 16, var9);
         }
      } else {
         this.texture.initDraw().sprite(1, 0, 16).alpha(var5).draw(var8 + 16, var9);
      }

      if (this.same(var10[6]) && this.same(var10[3])) {
         if (!this.same(var10[5])) {
            this.texture.initDraw().sprite(2, 1, 16).alpha(var5).draw(var8, var9 + 16);
         } else {
            this.texture.initDraw().sprite(0, 3, 16).alpha(var5).draw(var8, var9 + 16);
         }
      } else if (!this.same(var10[6]) && this.same(var10[3])) {
         this.texture.initDraw().sprite(2, 3, 16).alpha(var5).draw(var8, var9 + 16);
      } else if (this.same(var10[6]) && !this.same(var10[3])) {
         this.texture.initDraw().sprite(4, 1, 16).alpha(var5).draw(var8, var9 + 16);
      } else {
         this.texture.initDraw().sprite(0, 1, 16).alpha(var5).draw(var8, var9 + 16);
      }

      if (this.same(var10[6]) && this.same(var10[4])) {
         if (!this.same(var10[7])) {
            this.texture.initDraw().sprite(3, 1, 16).alpha(var5).draw(var8 + 16, var9 + 16);
         } else {
            this.texture.initDraw().sprite(1, 3, 16).alpha(var5).draw(var8 + 16, var9 + 16);
         }
      } else if (!this.same(var10[6]) && this.same(var10[4])) {
         this.texture.initDraw().sprite(3, 3, 16).alpha(var5).draw(var8 + 16, var9 + 16);
      } else if (this.same(var10[6]) && !this.same(var10[4])) {
         this.texture.initDraw().sprite(5, 1, 16).alpha(var5).draw(var8 + 16, var9 + 16);
      } else {
         this.texture.initDraw().sprite(1, 1, 16).alpha(var5).draw(var8 + 16, var9 + 16);
      }

      if (!this.same(var10[6])) {
         boolean var11 = this.same(var10[3]);
         boolean var12 = this.same(var10[4]);
         if (var11 && var12) {
            this.texture.initDraw().sprite(2, 3, 32, 16).alpha(var5).draw(var8, var9 + 16 + 10);
         } else if (var11) {
            this.texture.initDraw().sprite(4, 3, 16).alpha(var5).draw(var8, var9 + 16 + 10);
            this.texture.initDraw().sprite(5, 2, 16).alpha(var5).draw(var8 + 16, var9 + 16 + 10);
         } else if (var12) {
            this.texture.initDraw().sprite(4, 2, 16).alpha(var5).draw(var8, var9 + 16 + 10);
            this.texture.initDraw().sprite(5, 3, 16).alpha(var5).draw(var8 + 16, var9 + 16 + 10);
         } else {
            this.texture.initDraw().sprite(2, 2, 32, 16).alpha(var5).draw(var8, var9 + 16 + 10);
         }
      }

   }

   private boolean same(GameObject var1) {
      return var1.getStringID().equals(this.getStringID());
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      Rectangle var5 = super.getCollision(var1, var2, var3, var4);
      if (this.same(var1.getObject(var2 - 1, var3))) {
         var5.x -= 4;
         var5.width += 4;
      }

      if (this.same(var1.getObject(var2, var3 - 1))) {
         var5.y -= 4;
         var5.height += 4;
      }

      if (this.same(var1.getObject(var2 + 1, var3))) {
         var5.width += 4;
      }

      if (this.same(var1.getObject(var2, var3 + 1))) {
         var5.height += 4;
      }

      return var5;
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }
}
