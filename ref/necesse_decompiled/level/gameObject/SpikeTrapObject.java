package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SpikeTrapObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpikeTrapObject extends GameObject {
   public GameTexture texture;
   public GameTexture spikeTexture;
   public int animationDuration = 1000;

   public SpikeTrapObject() {
      super(new Rectangle(0, 0, 0, 0));
      this.showsWire = true;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/spiketrap");
      this.spikeTexture = GameTexture.fromFile("objects/spiketrapspikes");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32, 64).light(var9).pos(var10, var11 - 32);
      var2.add((var1x) -> {
         var12.draw();
      });
      ObjectEntity var13 = var3.entityManager.getObjectEntity(var4, var5);
      if (var13 instanceof TrapObjectEntity) {
         long var14 = ((TrapObjectEntity)var13).getTimeSinceActivated();
         if (var14 < (long)this.animationDuration) {
            int var16 = this.animationDuration / 9;
            int var17 = (int)(var14 / (long)var16);
            final TextureDrawOptionsEnd var18 = this.spikeTexture.initDraw().sprite(var17, 0, 32, 64).light(var9).pos(var10, var11 - 32);
            var1.add(new LevelSortedDrawable(this, var4, var5) {
               public int getSortY() {
                  return -4;
               }

               public void draw(TickManager var1) {
                  var18.draw();
               }
            });
         }
      }

   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 0, 32, 64).alpha(var5).draw(var8, var9 - 32);
   }

   public Color getMapColor(Level var1, int var2, int var3) {
      return var1.getTile(var2, var3).getMapColor(var1, var2, var3).darker();
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var5 && var1.isServer()) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 != null) {
            ((TrapObjectEntity)var6).triggerTrap(var4, var1.getObjectRotation(var2, var3));
         }
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new SpikeTrapObjectEntity(var1, var2, var3, this.animationDuration);
   }
}
