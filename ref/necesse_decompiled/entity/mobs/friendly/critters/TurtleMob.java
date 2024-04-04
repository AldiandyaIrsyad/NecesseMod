package necesse.entity.mobs.friendly.critters;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

public class TurtleMob extends CritterMob {
   public static LootTable lootTable = new LootTable();

   public TurtleMob() {
      this.setSpeed(6.0F);
      this.setFriction(3.0F);
      this.setSwimSpeed(2.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-19, -20, 38, 28);
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public int getTileWanderPriority(TilePosition var1) {
      if (var1.tileID() == TileRegistry.waterID) {
         return 1000;
      } else {
         int var2 = var1.level.liquidManager.getHeight(var1.tileX, var1.tileY);
         return var2 >= 0 && var2 <= 3 ? 1000 : super.getTileWanderPriority(var1);
      }
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.turtle.body, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 30;
      int var12 = var8.getDrawY(var6) - 54;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.turtle.body.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.turtle.shadow.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var15.draw();
      });
   }

   public int getRockSpeed() {
      return 10;
   }

   public MobSpawnLocation checkSpawnLocation(MobSpawnLocation var1) {
      return var1.checkNotLevelCollides().checkTile((var1x, var2) -> {
         int var3 = this.getLevel().getTileID(var1x, var2);
         if (var3 == TileRegistry.waterID) {
            return true;
         } else {
            int var4 = this.getLevel().liquidManager.getHeight(var1x, var2);
            return var4 >= 0 && var4 <= 3;
         }
      });
   }
}
