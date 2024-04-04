package necesse.level.gameObject;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.light.GameLight;

public class ThornsObject extends GrassObject {
   public static double spreadChance = GameMath.getAverageSuccessRuns(600.0);

   public ThornsObject() {
      super("thorns", -1);
      this.setItemCategory(new String[]{"materials", "flowers"});
      this.canPlaceOnShore = true;
      this.mapColor = new Color(137, 74, 54);
      this.displayMapTooltip = true;
      this.weaveAmount = 0.05F;
      this.extraWeaveSpace = 32;
      this.randomYOffset = 3.0F;
      this.randomXOffset = 10.0F;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      return var1.getTileID(var2, var3) != TileRegistry.mudID ? "notmud" : super.canPlace(var1, var2, var3, var4);
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.mudID;
   }

   public int getLightLevelMod(Level var1, int var2, int var3) {
      return 30;
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
      super.tick(var1, var2, var3, var4);
      if (var2.isServer() && var1.canTakeDamage() && !var1.isBoss() && !var1.isHostile && !var1.isCritter) {
         if (!var1.isOnGenericCooldown("thornsdamage")) {
            int var5 = var1.getMaxHealth();
            float var6 = Math.max((float)Math.pow((double)var5, 0.5) + (float)var5 / 30.0F, 10.0F);
            if (var6 != 0.0F) {
               var1.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, var6), 0.0F, 0.0F, 0.0F, new ThornsAttacker());
               var1.startGenericCooldown("thornsdamage", 500L);
            }
         }

         var2.sendObjectChangePacket(var2.getServer(), var3, var4, 0, 0);
      }

   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      super.addSimulateLogic(var1, var2, var3, var4, var6, var7);
      this.addSimulateSpread(var1, var2, var3, 2, 10, 1, spreadChance, var4, var6, var7);
   }

   public void tick(Level var1, int var2, int var3) {
      super.tick(var1, var2, var3);
      if (var1.isServer() && GameRandom.globalRandom.getChance(spreadChance)) {
         Performance.record(var1.tickManager(), "growThorns", (Runnable)(() -> {
            this.tickSpread(var1, var2, var3, 2, 10, 1);
         }));
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "thornsSetup", (Runnable)(() -> {
         Integer[] var7x = var3.getAdjacentObjectsInt(var4, var5);
         int var8 = 0;
         Integer[] var9 = var7x;
         int var10 = var7x.length;

         int var11;
         for(var11 = 0; var11 < var10; ++var11) {
            Integer var12 = var9[var11];
            if (var12 == this.getID()) {
               ++var8;
            }
         }

         byte var14;
         byte var15;
         if (var8 < 4) {
            var14 = 4;
            var15 = 7;
         } else {
            var14 = 0;
            var15 = 3;
         }

         var11 = var7.getTileDrawX(var4);
         int var16 = var7.getTileDrawY(var5);
         GameLight var13 = var3.getLightLevel(var4, var5);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 6, -5, 0, var14, var15);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 12, -5, 1);
         this.addGrassDrawable(var1, var2, var3, var4, var5, var11, var16, var13, 26, -5, 2, var14, var15);
      }));
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      Integer[] var8 = var1.getAdjacentObjectsInt(var2, var3);
      int var9 = 0;
      Integer[] var10 = var8;
      int var11 = var8.length;

      int var12;
      for(var12 = 0; var12 < var11; ++var12) {
         Integer var13 = var10[var12];
         if (var13 == this.getID()) {
            ++var9;
         }
      }

      byte var16;
      byte var17;
      if (var9 < 4) {
         var16 = 4;
         var17 = 7;
      } else {
         var16 = 0;
         var17 = 3;
      }

      var12 = var7.getTileDrawX(var2);
      int var18 = var7.getTileDrawY(var3);
      LinkedList var14 = new LinkedList();
      OrderableDrawables var15 = new OrderableDrawables(new TreeMap());
      this.addGrassDrawable(var14, var15, var1, var2, var3, var12, var18, (GameLight)null, 6, -5, 0, var16, var17, 0.5F);
      this.addGrassDrawable(var14, var15, var1, var2, var3, var12, var18, (GameLight)null, 12, -5, 1, 0.5F);
      this.addGrassDrawable(var14, var15, var1, var2, var3, var12, var18, (GameLight)null, 26, -5, 2, var16, var17, 0.5F);
      var15.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
      var14.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
   }

   private static class ThornsAttacker implements Attacker {
      public ThornsAttacker() {
      }

      public GameMessage getAttackerName() {
         return new LocalMessage("object", "thorns");
      }

      public DeathMessageTable getDeathMessages() {
         return this.getDeathMessages("thorns", 2);
      }

      public Mob getFirstAttackOwner() {
         return null;
      }
   }
}
