package necesse.level.gameObject;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.light.GameLight;

public class CattailObject extends GrassObject {
   public static double spreadChance = GameMath.getAverageSuccessRuns(900.0);

   public CattailObject(String var1, int var2, Color var3) {
      super(var1, var2, 2);
      this.mapColor = var3;
      this.canPlaceOnLiquid = true;
      this.weaveTime = 1000;
      this.randomXOffset = 5.0F;
      this.randomYOffset = 4.0F;
   }

   public void tick(Level var1, int var2, int var3) {
      super.tick(var1, var2, var3);
      if (var1.isServer() && GameRandom.globalRandom.getChance(spreadChance)) {
         this.tickSpread(var1, var2, var3, 5, 9, 2);
      }

   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      super.addSimulateLogic(var1, var2, var3, var4, var6, var7);
      this.addSimulateSpread(var1, var2, var3, 5, 9, 2, spreadChance, var4, var6, var7);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      int var9 = var7.getTileDrawX(var4);
      int var10 = var7.getTileDrawY(var5);
      GameLight var11 = var3.getLightLevel(var4, var5);
      this.addGrassDrawable(var1, var2, var3, var4, var5, var9, var10, var11, 14, -7, 0);
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      GameLight var10 = var1.getLightLevel(var2, var3);
      LinkedList var11 = new LinkedList();
      OrderableDrawables var12 = new OrderableDrawables(new TreeMap());
      this.addGrassDrawable(var11, var12, var1, var2, var3, var8, var9, var10, 14, -7, 0, 0.5F);
      var12.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
      var11.forEach((var1x) -> {
         var1x.draw(var1.tickManager());
      });
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else if (var1.getTileID(var2, var3) != TileRegistry.waterID) {
         return "wrongtile";
      } else {
         return var1.liquidManager.getHeight(var2, var3) < -6 ? "toodeep" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (!super.isValid(var1, var2, var3)) {
         return false;
      } else {
         return var1.getTileID(var2, var3) == TileRegistry.waterID;
      }
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4, Attacker var5) {
      var1.getServer().network.sendToClientsAt(new PacketHitObject(var1, var2, var3, this, var4), (Level)var1);
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      var1.makeGrassWeave(var2, var3, 1000, false);
   }
}
