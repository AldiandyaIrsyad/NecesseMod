package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.multiTile.MultiTile;

public class SettlementAssignWorkToolHandler implements SettlementToolHandler {
   public final Level level;
   public final SettlementContainer container;
   public final SettlementAssignWorkForm<?> workForm;

   public SettlementAssignWorkToolHandler(Client var1, SettlementContainer var2, SettlementAssignWorkForm<?> var3) {
      this.level = var1.getLevel();
      this.container = var2;
      this.workForm = var3;
   }

   public boolean onLeftClick(Point var1) {
      ArrayList var2 = this.addOptionsAndTooltips(var1, (Consumer)null, (Consumer)null);
      if (var2.isEmpty()) {
         return false;
      } else if (var2.size() == 1) {
         ConfigureOption var6 = (ConfigureOption)var2.get(0);
         this.workForm.playTickSound();
         var6.onClicked.run();
         return true;
      } else {
         this.workForm.playTickSound();
         SelectionFloatMenu var3 = new SelectionFloatMenu(this.workForm);
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            ConfigureOption var5 = (ConfigureOption)var4.next();
            var3.add(var5.str, () -> {
               var5.onClicked.run();
               var3.remove();
            });
         }

         this.workForm.getManager().openFloatMenu(var3, -5, -5);
         return true;
      }
   }

   public boolean onHover(Point var1, Consumer<ListGameTooltips> var2, Consumer<Screen.CURSOR> var3) {
      this.addOptionsAndTooltips(var1, var2, var3);
      return false;
   }

   public ArrayList<ConfigureOption> addOptionsAndTooltips(Point var1, Consumer<ListGameTooltips> var2, Consumer<Screen.CURSOR> var3) {
      int var4 = var1.x / 32;
      int var5 = var1.y / 32;
      ArrayList var6 = new ArrayList();
      ListGameTooltips var7 = new ListGameTooltips();
      if (var2 != null) {
         var2.accept(var7);
      }

      Iterator var8;
      Point var9;
      MultiTile var10;
      if (!(Boolean)Settings.hideSettlementStorage.get()) {
         var8 = this.workForm.storagePositions.iterator();

         while(var8.hasNext()) {
            var9 = (Point)var8.next();
            var10 = this.level.getObject(var9.x, var9.y).getMultiTile(this.level, var9.x, var9.y);
            if (var10.getTileRectangle(var9.x, var9.y).contains(var4, var5)) {
               var6.add(new ConfigureOption(Localization.translate("ui", "settlementconfigurestorage"), () -> {
                  this.workForm.openStorageConfig(var9.x, var9.y);
               }));
            }
         }
      }

      if (!(Boolean)Settings.hideSettlementWorkstations.get()) {
         var8 = this.workForm.workstationPositions.iterator();

         while(var8.hasNext()) {
            var9 = (Point)var8.next();
            var10 = this.level.getObject(var9.x, var9.y).getMultiTile(this.level, var9.x, var9.y);
            if (var10.getTileRectangle(var9.x, var9.y).contains(var4, var5)) {
               var6.add(new ConfigureOption(Localization.translate("ui", "settlementconfigureworkstation"), () -> {
                  this.workForm.openWorkstationConfig(var9.x, var9.y);
               }));
            }
         }
      }

      var8 = this.workForm.settlementWorkZones.values().iterator();

      while(var8.hasNext()) {
         SettlementWorkZone var12 = (SettlementWorkZone)var8.next();
         if (!var12.isHiddenSetting() && var12.containsTile(var4, var5)) {
            GameMessage var13 = var12.getName();
            if (var13 != null) {
               var7.add(var13);
               if (var12.canConfigure()) {
                  var6.add(new ConfigureOption(Localization.translate("ui", "settlementconfigurezone", "name", var13.translate()), () -> {
                     this.workForm.openWorkZoneConfig(var12);
                  }));
               }
            }
         }
      }

      if (!var6.isEmpty()) {
         if (var3 != null) {
            var3.accept(Screen.CURSOR.INTERACT);
         }

         if (var6.size() == 1) {
            ConfigureOption var11 = (ConfigureOption)var6.get(0);
            if (Input.lastInputIsController) {
               var7.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, var11.str)));
            } else {
               var7.add((Object)(new InputTooltip(-100, var11.str)));
            }
         } else if (Input.lastInputIsController) {
            var7.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "configurebutton"))));
         } else {
            var7.add((Object)(new InputTooltip(-100, Localization.translate("ui", "configurebutton"))));
         }
      }

      return var6;
   }

   private static class ConfigureOption {
      public final String str;
      public final Runnable onClicked;

      public ConfigureOption(String var1, Runnable var2) {
         this.str = var1;
         this.onClicked = var2;
      }
   }
}
