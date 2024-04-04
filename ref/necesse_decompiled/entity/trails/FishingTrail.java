package necesse.entity.trails;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.FishingHookProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.Level;

public class FishingTrail extends Trail {
   private Entity hook;
   private boolean isHookProjectile;
   private Mob mob;
   private FishingRodItem fishingRod;

   public FishingTrail(Mob var1, Level var2, Entity var3, FishingRodItem var4) {
      super(new TrailVector(var1.x, var1.y, var1.x - var3.x, var1.y - var3.y, 16.0F, 0.0F), var2, new Color(255, 255, 255), 5000);
      this.mob = var1;
      this.hook = var3;
      this.fishingRod = var4;
      this.sprite = var4.getTrailSprite();
      this.update();
   }

   public FishingTrail(Mob var1, Level var2, FishingHookProjectile var3, FishingRodItem var4) {
      this(var1, var2, (Entity)var3, var4);
      this.isHookProjectile = true;
   }

   public synchronized void update() {
      Point var1 = this.fishingRod.getTipPos(this.mob);
      int var2 = this.fishingRod.getTipHeight(this.mob);
      float var3 = (float)var1.x;
      float var4 = (float)var1.y;
      float var5 = this.hook.x;
      float var6 = this.hook.y;
      float var7 = 0.0F;
      if (this.isHookProjectile) {
         var7 = ((FishingHookProjectile)this.hook).getHeight();
      }

      this.reset(new TrailVector(var3, var4, var3 - var5, var4 - var6, this.thickness, (float)var2));
      float var8 = (float)(new Point2D.Float(var3, var4)).distance((double)var5, (double)var6);
      float var9 = (var3 - var5) / 2.0F + var5;
      float var10 = (var4 - var6) / 2.0F + var6;
      float var11 = ((float)var2 - var7) / 2.0F + var7 - var8 / 10.0F;
      this.addPoints(3, new TrailVector[]{new TrailVector(var9, var10, var3 - var9, var4 - var10, this.thickness, var11), new TrailVector(var5, var6, var5 - var9, var6 - var10, this.thickness, var7)});
   }

   protected synchronized DrawOptions getDrawSection(TrailDrawSection var1, GameCamera var2) {
      return var1.getSpriteTrailsDraw(this.sprite, var2, TrailDrawSection.lightColorSetter(this.level, this.col));
   }

   protected synchronized DrawOptions getDrawNextSection(TrailDrawSection var1, TrailPointList var2, float var3, GameCamera var4) {
      return TrailDrawSection.getSpriteTrailsDraw(this.sprite, var2, 0, var2.size() - 1, var4, TrailDrawSection.lightColorSetter(this.level, this.col));
   }
}
