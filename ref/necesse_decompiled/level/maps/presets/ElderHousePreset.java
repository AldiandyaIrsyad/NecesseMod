package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.GameLog;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.Waystone;

public class ElderHousePreset extends Preset {
   public ElderHousePreset(GameRandom var1) {
      super(17, 14);
      this.applyScript("PRESET = {\n\twidth = 17,\n\theight = 9,\n\ttileIDs = [9, farmland, 10, woodfloor, 14, stonefloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 129, storagebox, 386, firemone, 35, woodwall, 36, wooddoor, 518, ladderdown, 176, sprucedesk, 183, sprucebed, 184, sprucebed2, 377, sunflowerseed, 26, workstation, 187, sprucecandelabra, 188, sprucedisplay],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 35, 35, 35, 35, 35, 35, 35, 35, 0, 0, 0, 0, 0, 0, 0, 0, 35, 187, 129, 26, 386, 188, 176, 183, 35, 0, 0, 0, 0, 0, 0, 0, 0, 35, 0, 0, 0, 0, 0, 0, 184, 35, 35, 35, 35, 35, 377, 377, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 35, 0, 0, 0, 35, 377, 377, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 36, 0, 518, 0, 35, 377, 377, 0, 0, 35, 0, 0, 0, 0, 0, 0, 187, 35, 0, 0, 0, 35, 377, 377, 0, 0, 35, 35, 35, 35, 36, 35, 35, 35, 35, 35, 35, 35, 35, 377, 377, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 0, 0, 0, 0, 0, 3, 3, 3, 2, 1, 1, 1, 0, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
      this.replaceTile(TileRegistry.grassID, -1);
      this.addInventory(LootTablePresets.startChest, var1, 3, 2, new Object[0]);
      this.addInventory(LootTablePresets.startDisplayStand, var1, 6, 2, new Object[0]);
      this.fillObject(14, 3, 2, 5, ObjectRegistry.getObjectID("sunflowerseed"));
      this.addCustomPreApplyRectEach(0, 0, this.width, 10, 0, (var0, var1x, var2, var3) -> {
         if (var0.getTile(var1x, var2).isLiquid) {
            var0.setTile(var1x, var2, TileRegistry.sandID);
            var0.setObject(var1x, var2, 0);
         }

         return null;
      });
      this.addCustomPreApplyRectEach(0, 9, this.width, 5, 0, (var0, var1x, var2, var3) -> {
         GameObject var4 = var0.getObject(var1x, var2);
         if (!var4.isGrass) {
            var0.setObject(var1x, var2, 0);
         }

         return null;
      });
      this.addCustomApply(7, 9, 0, (var0, var1x, var2, var3) -> {
         var0.setObject(var1x, var2, ObjectRegistry.getObjectID("roastingstation"), var3);
         return null;
      });
      this.addCustomPreApplyRectEach(6, 8, 3, 3, 0, (var0, var1x, var2, var3) -> {
         if (var0.getTile(var1x, var2).isLiquid) {
            var0.setTile(var1x, var2, TileRegistry.sandID);
            var0.setObject(var1x, var2, 0);
         }

         return null;
      });
      this.addCustomApply(8, 2, 0, (var1x, var2, var3, var4) -> {
         HumanMob var5 = (HumanMob)MobRegistry.getMob("elderhuman", var1x);
         var5.setSettlerSeed(var1.nextInt());
         var5.setHome(var2, var3);
         Point var6 = Waystone.findTeleportLocation(var1x, var2, var3, var5);
         var1x.entityManager.addMob(var5, (float)var6.x, (float)var6.y);
         if (var1x.isServer()) {
            SettlementLevelData var7 = SettlementLevelData.getSettlementDataCreateIfNonExist(var1x);
            SettlementBed var8 = var7.addOrValidateBed(var2, var3);
            LevelSettler var9 = new LevelSettler(var7, var5);
            var7.moveSettler(var9, var8, (ServerClient)null);
            if (var8 == null) {
               GameLog.warn.println("Could not find bed for elder house at " + var2 + ", " + var3);
            }
         }

         return (var1xx, var2x, var3x) -> {
            var5.remove();
         };
      });
   }
}
