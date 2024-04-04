package necesse.entity.levelEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.TileDamageType;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;

public class TempleEntranceEvent extends LevelEvent {
   public static int ANIMATION_TIME = 10000;
   public long startTime;
   public int tileX;
   public int tileY;
   protected SoundPlayer secondStageRumble;
   protected TerrainSplatterTile[][] tiles;

   public TempleEntranceEvent() {
   }

   public TempleEntranceEvent(int var1, int var2) {
      this.tileX = var1;
      this.tileY = var2;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
   }

   public void init() {
      super.init();
      int var2;
      if (this.isServer()) {
         for(int var1 = this.tileX - 1; var1 <= this.tileX + 1; ++var1) {
            for(var2 = this.tileY; var2 <= this.tileY + 1; ++var2) {
               this.level.entityManager.doDamage(var1, var2, 1000000, TileDamageType.Object, 1000000, (ServerClient)null);
            }
         }
      }

      ObjectRegistry.getObject("templeentrance").placeObject(this.level, this.tileX - 1, this.tileY, 0);
      this.startTime = this.level.getWorldEntity().getTime();
      if (this.isClient()) {
         CameraShake var8 = this.level.getClient().startCameraShake((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16), ANIMATION_TIME, 40, 5.0F, 5.0F, true);
         var8.minDistance = 200;
         var8.listenDistance = 2000;
         this.tiles = new TerrainSplatterTile[3][2];

         for(var2 = 0; var2 < 3; ++var2) {
            int var3 = this.tileX - 1 + var2;

            for(int var4 = 0; var4 < 2; ++var4) {
               int var5 = this.tileY + var4;
               GameTile var6 = this.level.getTile(var3, var5);
               TerrainSplatterTile var7;
               if (var6 instanceof TerrainSplatterTile) {
                  var7 = (TerrainSplatterTile)var6;
               } else {
                  var7 = (TerrainSplatterTile)TileRegistry.getTile("sandbrick");
               }

               this.tiles[var2][var4] = var7;
            }
         }
      }

      if (this.isServer()) {
         this.over();
      }

   }

   public void clientTick() {
      super.clientTick();
      long var1 = this.level.getWorldEntity().getTime() - this.startTime;
      if (var1 > (long)ANIMATION_TIME) {
         this.over();
      } else {
         if (this.secondStageRumble == null || this.secondStageRumble.isDone()) {
            this.secondStageRumble = Screen.playSound(GameResources.rumble, SoundEffect.effect((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16)).volume(4.0F).falloffDistance(2000));
         }

         if (this.secondStageRumble != null) {
            this.secondStageRumble.refreshLooping(1.0F);
         }

         float var3 = Math.abs(GameMath.limit((float)var1 / (float)ANIMATION_TIME, 0.0F, 1.0F) - 1.0F);
         int var4 = (int)(var3 * 32.0F * 3.0F);

         for(int var5 = 0; var5 < 4; ++var5) {
            this.level.entityManager.addParticle((float)(this.tileX * 32 - 32 + var4) + GameRandom.globalRandom.floatGaussian() * 5.0F, (float)(this.tileY * 32) + GameRandom.globalRandom.nextFloat() * 32.0F * 2.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 3.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(0.0F, GameRandom.globalRandom.getFloatBetween(20.0F, 30.0F)).lifeTime(1000);
         }

      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, LevelDrawUtils.DrawArea var4, Level var5, TickManager var6, GameCamera var7) {
      if (!this.isOver()) {
         long var8 = var5.getWorldEntity().getTime() - this.startTime;
         float var10 = Math.abs(GameMath.limit((float)var8 / (float)ANIMATION_TIME, 0.0F, 1.0F) - 1.0F);
         int var11 = (int)(var10 * 32.0F * 3.0F);
         if (var11 > 0) {
            LevelTileDrawOptions var12 = new LevelTileDrawOptions();

            for(int var13 = 0; var13 < this.tiles.length; ++var13) {
               int var14 = this.tileX - 1 + var13;
               int var15 = Math.min(var11 - var13 * 32, 32);
               if (var15 <= 0) {
                  break;
               }

               int var16 = var7.getTileDrawX(var14);

               for(int var17 = 0; var17 < this.tiles[var13].length; ++var17) {
                  int var18 = this.tileY + var17;
                  int var19 = var7.getTileDrawY(var18);
                  GameTextureSection var20 = this.tiles[var13][var17].getTerrainTexture(var5, var14, var18).section(0, var15, 0, 32);
                  var12.add(var20).light(var5.getLightLevel(var14, var18)).pos(var16, var19);
               }
            }

            var2.add((var1x) -> {
               var12.draw();
            });
         }
      }
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile(this.tileX, this.tileY));
   }
}
