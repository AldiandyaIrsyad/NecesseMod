package necesse.entity.particle;

import java.awt.Point;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class NinjaShadowParticle extends Particle {
   private Point sprite;
   private int dir;
   private int xOffset;
   private int yOffset;
   private PlayerMob player;
   private boolean isCosmetic;
   private InventoryItem head;
   private InventoryItem chest;
   private InventoryItem feet;

   public NinjaShadowParticle(Level var1, PlayerMob var2, boolean var3) {
      super(var1, (float)var2.getX(), (float)var2.getY(), 700L);
      this.player = var2;
      this.isCosmetic = var3;
      this.sprite = var2.getAnimSprite();
      this.dir = var2.dir;
      this.xOffset = -32;
      this.yOffset = -51;
      this.yOffset += var2.getBobbing();
      this.yOffset += var2.getLevel().getTile(var2.getX() / 32, var2.getY() / 32).getMobSinkingAmount(var2);
      this.head = this.getArmor(var2, 0);
      this.chest = this.getArmor(var2, 1);
      this.feet = this.getArmor(var2, 2);
   }

   private InventoryItem getArmor(PlayerMob var1, int var2) {
      InventoryItem var3 = (this.isCosmetic ? var1.getInv().cosmetic : var1.getInv().armor).getItem(var2);
      return var3 != null && var3.item.isArmorItem() ? var3 : null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9 = this.getLifeCyclePercent();
      if (!this.removed()) {
         GameLight var10 = var5.getLightLevel(this);
         int var11 = this.getX() - var7.getX() + this.xOffset;
         int var12 = this.getY() - var7.getY() + this.yOffset;
         float var13 = Math.max(0.0F, 1.0F - var9);
         final DrawOptions var14 = (new HumanDrawOptions(var5)).player(this.player).helmet(this.head).chestplate(this.chest).boots(this.feet).light(var10).alpha(var13).sprite(this.sprite).dir(this.dir).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var14.draw();
            }
         });
      }
   }
}
