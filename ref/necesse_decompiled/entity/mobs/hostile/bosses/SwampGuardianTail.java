package necesse.entity.mobs.hostile.bosses;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampGuardianTail extends SwampGuardianBody {
   public SwampGuardianTail() {
   }

   private SwampGuardianBody getNextBodyPart(int var1) {
      SwampGuardianBody var2 = (SwampGuardianBody)this.next;

      for(int var3 = 0; var3 < var1 && var2.next != null; ++var3) {
         var2 = (SwampGuardianBody)var2.next;
      }

      return var2;
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 48;
         int var12 = var8.getDrawY(var6);
         float var13 = 0.0F;
         SwampGuardianBody var14 = this.getNextBodyPart(2);
         if (var14 != null) {
            var13 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(var14.x - (float)var5, var14.y - (float)var6)) + 180.0F);
         }

         WormMobHead.addAngledDrawable(var1, new GameSprite(MobRegistry.Textures.swampGuardian, 1, 1, 96), MobRegistry.Textures.swampGuardian_mask, var10, (int)this.height, var13, var11, var12, 64);
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }
}
