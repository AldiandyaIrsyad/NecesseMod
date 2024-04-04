package necesse.entity.projectile;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class FishingHookProjectile extends Projectile {
   private Mob target;
   private FishingEvent event;
   private InventoryItem item;
   private GameSprite sprite;
   private GameSprite shadowSprite;
   private int startHeight;
   private boolean hitWall;

   public FishingHookProjectile(Level var1, FishingEvent var2) {
      super(false, false);
      this.setLevel(var1);
      this.setDistance(500);
      this.event = var2;
      this.sprite = var2.fishingRod.getHookProjectileSprite();
      this.shadowSprite = var2.fishingRod.getHookShadowSprite();
   }

   public FishingHookProjectile(Level var1, FishingEvent var2, Mob var3, Item var4) {
      super(false, false);
      this.setLevel(var1);
      this.setDistance(5000);
      this.event = var2;
      this.target = var3;
      this.item = var4 == null ? null : new InventoryItem(var4);
      this.sprite = var2.fishingRod.getHookProjectileSprite();
      this.shadowSprite = var2.fishingRod.getHookShadowSprite();
   }

   public void setStartHeight(int var1) {
      this.startHeight = var1;
   }

   public void init() {
      super.init();
   }

   public float tickMovement(float var1) {
      if (this.removed()) {
         return 0.0F;
      } else if (this.target != null) {
         Point var2 = this.event.getPoleTipPos();
         int var3 = this.event.getPoleTipHeight();
         Point var4 = new Point(var2.x, var2.y - var3);
         if (this.target != null) {
            this.setTarget((float)var4.x, (float)var4.y);
            this.isSolid = false;
         }

         float var5 = super.tickMovement(var1);
         if (this.target != null) {
            double var6 = var4.distance((double)this.x, (double)this.y);
            if (var6 < 5.0 || var6 < (double)this.getMoveDist(this.speed, var1)) {
               this.remove();
            }
         }

         return var5;
      } else if (this.hitWall) {
         this.traveledDistance += this.getMoveDist(this.speed, var1);
         this.checkRemoved();
         return 0.0F;
      } else {
         return super.tickMovement(var1);
      }
   }

   public float getHeight() {
      if (this.target != null) {
         return (float)this.startHeight;
      } else {
         float var1 = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F) - 1.0F);
         float var2 = GameMath.sin(var1 * 180.0F);
         float var3 = (float)this.startHeight * var1;
         return (float)((int)(var3 + var2 * (float)this.distance / 2.5F));
      }
   }

   public Trail getTrail() {
      return null;
   }

   public void checkHitCollision(Line2D var1) {
   }

   public void onHit(Mob var1, LevelObjectHit var2, float var3, float var4, boolean var5, ServerClient var6) {
      if (var1 == null && this.target == null) {
         this.hitWall = true;
      } else {
         super.onHit(var1, var2, var3, var4, var5, var6);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x);
         int var11 = var7.getDrawY(this.y);
         int var13 = (int)this.getHeight();
         final Object var12;
         if (this.item != null) {
            var12 = this.item.getWorldDrawOptions(var8, var10, var11 + 16 - var13, var9, 0.0F, 32);
         } else {
            var12 = this.sprite.initDraw().light(var9).pos(var10 - this.sprite.width / 2, var11 - this.sprite.height / 2 - var13);
         }

         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               ((DrawOptions)var12).draw();
            }
         });
         if (this.shadowSprite != null) {
            float var14 = Math.abs(GameMath.limit((float)var13 / 120.0F, 0.0F, 1.0F) - 1.0F);
            int var15 = var10 - this.shadowSprite.width / 2;
            int var16 = var11 - this.shadowSprite.height / 2;
            TextureDrawOptionsEnd var17 = this.shadowSprite.initDraw().light(var9).alpha(var14).pos(var15, var16);
            var2.add((var1x) -> {
               var17.draw();
            });
         }

      }
   }
}
