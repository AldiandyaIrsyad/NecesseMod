package necesse.entity.particle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapArrayList;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class ParticleOptions {
   private static final int sortYAccuracy = 4;
   private static final int sortYHalf = 2;
   private final Level level;
   private HashMapArrayList<Integer, OptionContainer> sorted = new HashMapArrayList();
   private final GameLinkedList<OptionContainer> newSorted = new GameLinkedList();
   private final ArrayList<OptionContainer> top = new ArrayList();
   private final LinkedList<OptionContainer> newTop = new LinkedList();
   private int count;

   public ParticleOptions(Level var1) {
      this.level = var1;
   }

   public void add(ParticleOption var1) {
      if (!this.level.isServer()) {
         OptionContainer var2 = new OptionContainer(this.level.getWorldEntity().getLocalTime(), var1);
         synchronized(this.newSorted) {
            this.newSorted.addLast(var2);
         }
      }
   }

   public void addTop(ParticleOption var1) {
      if (!this.level.isServer()) {
         synchronized(this.newTop) {
            this.newTop.add(new OptionContainer(this.level.getWorldEntity().getLocalTime(), var1));
         }
      }
   }

   private boolean tickSorted(OptionContainer var1, long var2, float var4, HashMapArrayList<Integer, OptionContainer> var5) {
      int var6 = (int)(var2 - var1.spawnTime);
      float var7 = (float)var6 / (float)var1.option.lifeTime;
      if (var1.option.removed) {
         return false;
      } else if (var6 > var1.option.lifeTime) {
         var1.option.tickProgress(1.0F);
         var1.option.remove();
         return false;
      } else {
         if (var1.option.mover != null) {
            var1.option.mover.tick(var1.option.pos, var4, var1.option.lifeTime, var6, var7);
         }

         if (var1.option.height != null) {
            var1.option.currentHeight = var1.option.height.tick(var4, var1.option.lifeTime, var6, var7);
         }

         var1.option.tickEvents.forEach((var4x) -> {
            var4x.tick(var4, var1.option.lifeTime, var6, var7);
         });
         var1.option.tickProgress(var7);
         var5.add((int)(var1.option.getLevelPos().y / 4.0F), var1);
         return true;
      }
   }

   public void tickMovement(float var1) {
      int var2 = 0;
      long var3 = this.level.getWorldEntity().getLocalTime();
      HashMapArrayList var5 = new HashMapArrayList(100);
      OptionContainer var8;
      synchronized(this.newSorted) {
         Iterator var7 = this.newSorted.iterator();

         while(true) {
            if (!var7.hasNext()) {
               this.newSorted.clear();
               break;
            }

            var8 = (OptionContainer)var7.next();
            this.tickSorted(var8, var3, var1, var5);
         }
      }

      Iterator var6 = this.sorted.values().iterator();

      while(var6.hasNext()) {
         ArrayList var17 = (ArrayList)var6.next();
         Iterator var19 = var17.iterator();

         while(var19.hasNext()) {
            OptionContainer var9 = (OptionContainer)var19.next();
            if (this.tickSorted(var9, var3, var1, var5)) {
               ++var2;
            }
         }
      }

      synchronized(this) {
         this.sorted = var5;
      }

      synchronized(this.top) {
         synchronized(this.newTop) {
            this.top.addAll(this.newTop);
            this.newTop.clear();
         }

         for(int var18 = 0; var18 < this.top.size(); ++var18) {
            var8 = (OptionContainer)this.top.get(var18);
            int var20 = (int)(var3 - var8.spawnTime);
            float var10 = (float)var20 / (float)var8.option.lifeTime;
            if (var8.option.removed) {
               this.top.remove(var18);
               --var18;
            } else if (var20 > var8.option.lifeTime) {
               var8.option.tickProgress(1.0F);
               var8.option.remove();
               this.top.remove(var18);
               --var18;
            } else {
               if (var8.option.mover != null) {
                  var8.option.mover.tick(var8.option.pos, var1, var8.option.lifeTime, var20, var10);
               }

               if (var8.option.height != null) {
                  var8.option.currentHeight = var8.option.height.tick(var1, var8.option.lifeTime, var20, var10);
               }

               var8.option.tickEvents.forEach((var4) -> {
                  var4.tick(var1, var8.option.lifeTime, var20, var10);
               });
               var8.option.tickProgress(var10);
               ++var2;
            }
         }
      }

      this.count = var2;
   }

   public void tick() {
      synchronized(this) {
         this.sorted.values().stream().flatMap(Collection::stream).filter((var0) -> {
            return var0.option.lightLevel > 0;
         }).forEach((var1) -> {
            Point2D.Float var2 = var1.option.getLevelPos();
            this.level.lightManager.refreshParticleLightFloat(var2.x, var2.y, var1.option.lightHue, var1.option.lightSat, var1.option.lightLevel);
         });
      }

      synchronized(this.top) {
         this.top.stream().filter((var0) -> {
            return var0.option.lightLevel > 0;
         }).forEach((var1) -> {
            Point2D.Float var2 = var1.option.getLevelPos();
            this.level.lightManager.refreshParticleLightFloat(var2.x, var2.y, var1.option.lightHue, var1.option.lightSat, var1.option.lightLevel);
         });
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, TickManager var5, GameCamera var6, PlayerMob var7) {
      long var8 = var4.getWorldEntity().getLocalTime();
      synchronized(this) {
         Iterator var11 = this.sorted.entrySet().iterator();

         while(true) {
            if (!var11.hasNext()) {
               break;
            }

            Map.Entry var12 = (Map.Entry)var11.next();
            final int var13 = (Integer)var12.getKey() * 4 + 2;
            final SharedTextureDrawOptions var14 = new SharedTextureDrawOptions(GameResources.generatedParticlesTexture);
            ArrayList var15 = (ArrayList)var12.getValue();
            Iterator var16 = var15.iterator();

            while(var16.hasNext()) {
               OptionContainer var17 = (OptionContainer)var16.next();
               int var18 = (int)(var8 - var17.spawnTime);
               float var19 = Math.min((float)var18 / (float)var17.option.lifeTime, 1.0F);
               var17.option.addDrawOptions(var14, var4, var17.option.lifeTime, var18, var19, var6);
            }

            var1.add(new LevelSortedDrawable(var14) {
               public int getSortY() {
                  return var13;
               }

               public void draw(TickManager var1) {
                  var14.draw();
               }
            });
         }
      }

      synchronized(this.top) {
         SharedTextureDrawOptions var24 = new SharedTextureDrawOptions(GameResources.generatedParticlesTexture);
         Iterator var25 = this.top.iterator();

         while(var25.hasNext()) {
            OptionContainer var26 = (OptionContainer)var25.next();
            int var27 = (int)(var8 - var26.spawnTime);
            float var28 = (float)var27 / (float)var26.option.lifeTime;
            var26.option.addDrawOptions(var24, var4, var26.option.lifeTime, var27, var28, var6);
         }

         var3.add((var1x) -> {
            var24.draw();
         });
      }
   }

   public int count() {
      return this.count;
   }

   protected static class OptionContainer {
      protected final long spawnTime;
      protected final ParticleOption option;

      public OptionContainer(long var1, ParticleOption var3) {
         this.spawnTime = var1;
         this.option = var3;
      }
   }
}
