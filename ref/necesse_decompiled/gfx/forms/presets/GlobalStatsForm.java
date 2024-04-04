package necesse.gfx.forms.presets;

import java.util.Iterator;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.steam.SteamData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPlayerStatComponent;
import necesse.gfx.forms.components.FormPlayerStatLongComponent;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.ui.ButtonColor;

public class GlobalStatsForm extends Form {
   private FormSwitcher switcher;
   private FormContentBox generalStats;
   private FormContentBox damageTypes;

   public GlobalStatsForm(int var1, int var2, int var3, int var4) {
      super(var3, var4);
      this.setPosition(var1, var2);
      this.drawBase = false;
      this.switcher = (FormSwitcher)this.addComponent(new FormSwitcher());
      byte var5 = 8;
      int var6 = var3 - var5 * 2;
      this.generalStats = (FormContentBox)this.switcher.addComponent(new FormContentBox(0, 0, var3, var4));
      int var7 = this.generalStats.getScrollBarWidth();
      FormFlow var8 = new FormFlow();
      this.generalStats.addComponent((FormPlayerStatComponent)var8.nextY(new FormPlayerStatComponent(0, 0, var6 - var7, new LocalMessage("stats", "time_played"), () -> {
         return GameUtils.formatSeconds(SteamData.globalStats().time_played);
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "distance_ran"), () -> {
         return (long)GameMath.pixelsToMeters((float)SteamData.globalStats().distance_ran);
      }, "m")));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "distance_ridden"), () -> {
         return (long)GameMath.pixelsToMeters((float)SteamData.globalStats().distance_ridden);
      }, "m")));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "damage_dealt"), () -> {
         return SteamData.globalStats().damage_dealt;
      })));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var8.nextY(new FormLocalTextButton("stats", "show_types", 0, 0, var6 - var7, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.damageTypes);
      });
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "damage_taken"), () -> {
         return SteamData.globalStats().damage_taken;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "deaths"), () -> {
         return SteamData.globalStats().deaths;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "mob_kills"), () -> {
         return SteamData.globalStats().mob_kills;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "boss_kills"), () -> {
         return SteamData.globalStats().boss_kills;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "island_travels"), () -> {
         return SteamData.globalStats().island_travels;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "islands_visited"), () -> {
         return SteamData.globalStats().islands_discovered + SteamData.globalStats().islands_visited;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "objects_mined"), () -> {
         return SteamData.globalStats().objects_mined;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "objects_placed"), () -> {
         return SteamData.globalStats().objects_placed;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "tiles_mined"), () -> {
         return SteamData.globalStats().tiles_mined;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "tiles_placed"), () -> {
         return SteamData.globalStats().tiles_placed;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "food_consumed"), () -> {
         return SteamData.globalStats().food_consumed;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "potions_consumed"), () -> {
         return SteamData.globalStats().potions_consumed;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "fish_caught"), () -> {
         return SteamData.globalStats().fish_caught;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "quests_completed"), () -> {
         return SteamData.globalStats().quests_completed;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "money_earned"), () -> {
         return SteamData.globalStats().money_earned;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "items_sold"), () -> {
         return SteamData.globalStats().items_sold;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "money_spent"), () -> {
         return SteamData.globalStats().money_spent;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "items_bought"), () -> {
         return SteamData.globalStats().items_bought;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "items_enchanted"), () -> {
         return SteamData.globalStats().items_enchanted;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "items_upgraded"), () -> {
         return SteamData.globalStats().items_upgraded;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "items_salvaged"), () -> {
         return SteamData.globalStats().items_salvaged;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "ladders_used"), () -> {
         return SteamData.globalStats().ladders_used;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "doors_used"), () -> {
         return SteamData.globalStats().doors_used;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "plates_triggered"), () -> {
         return SteamData.globalStats().plates_triggered;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "levers_flicked"), () -> {
         return SteamData.globalStats().levers_flicked;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "homestones_used"), () -> {
         return SteamData.globalStats().homestones_used;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "waystones_used"), () -> {
         return SteamData.globalStats().waystones_used;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "crafted_items"), () -> {
         return SteamData.globalStats().crafted_items;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "crates_broken"), () -> {
         return SteamData.globalStats().crates_broken;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "opened_incursions"), () -> {
         return SteamData.globalStats().opened_incursions;
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, new LocalMessage("stats", "completed_incursions"), () -> {
         return SteamData.globalStats().completed_incursions;
      })));
      this.generalStats.fitContentBoxToComponents(var5);
      this.damageTypes = (FormContentBox)this.switcher.addComponent(new FormContentBox(0, 0, var3, var4));
      var7 = this.damageTypes.getScrollBarWidth();
      var8 = new FormFlow();
      ((FormLocalTextButton)this.damageTypes.addComponent((FormLocalTextButton)var8.nextY(new FormLocalTextButton("ui", "backbutton", 0, 0, var6 - var7, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.generalStats);
      });
      Iterator var9 = DamageTypeRegistry.getDamageTypes().iterator();

      while(var9.hasNext()) {
         DamageType var10 = (DamageType)var9.next();
         String var11 = var10.getSteamStatKey();
         if (var11 != null) {
            this.damageTypes.addComponent((FormPlayerStatLongComponent)var8.nextY(new FormPlayerStatLongComponent(0, 0, var6 - var7, var10.getStatsText(), () -> {
               return SteamData.globalStats().getStatByName(var11, 0L);
            })));
         }
      }

      this.damageTypes.fitContentBoxToComponents(var5);
      this.switcher.makeCurrent(this.generalStats);
   }
}
