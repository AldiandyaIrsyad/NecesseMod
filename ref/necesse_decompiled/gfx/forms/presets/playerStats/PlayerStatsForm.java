package necesse.gfx.forms.presets.playerStats;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.playerStats.stats.BiomesVisitedStat;
import necesse.engine.playerStats.stats.IncIntPlayerStat;
import necesse.engine.playerStats.stats.ItemCountStat;
import necesse.engine.playerStats.stats.MobKillsStat;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemDisplayComponent;
import necesse.gfx.forms.components.FormPlayerStatComponent;
import necesse.gfx.forms.components.FormPlayerStatIntComponent;
import necesse.gfx.forms.components.FormPlayerStatLongComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.DisabledPreForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.item.Item;

public class PlayerStatsForm extends Form implements PlayerStatsSelected {
   public FormSwitcher switcher;
   public PlayerStatsContentBox generalStats;
   public PlayerStatsContentBox damageTypes;
   public PlayerStatsContentBox mobKills;
   public PlayerStatsContentBox foodConsumed;
   public PlayerStatsContentBox potionsConsumed;
   public PlayerStatsContentBox biomesDiscovered;
   public PlayerStatsContentBox trinketsWorn;
   public Runnable damageTypesUpdate;
   public Runnable mobKillsUpdate;
   public Runnable foodConsumedUpdate;
   public Runnable potionsConsumedUpdate;
   public Runnable biomesDiscoveredUpdate;
   public Runnable trinketsWornUpdate;
   public PlayerStatsObtainedItemsForm itemsObtained;
   public LinkedList<PlayerStatsSubForm> subForms = new LinkedList();
   public Runnable subMenuBackPressed;
   private Form disabledContent;

   public PlayerStatsForm(int var1, int var2, int var3, int var4, PlayerStats var5) {
      super(var3, var4);
      this.setPosition(var1, var2);
      this.drawBase = false;
      this.switcher = (FormSwitcher)this.addComponent(new FormSwitcher());
      byte var6 = 8;
      int var7 = var3 - var6 * 2;
      this.generalStats = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this));
      int var8 = this.generalStats.getScrollBarWidth();
      this.subForms.add(this.generalStats);
      FormFlow var9 = new FormFlow();
      this.generalStats.addComponent((FormPlayerStatComponent)var9.nextY(new FormPlayerStatComponent(0, 0, var7 - var8, new LocalMessage("stats", "time_played"), () -> {
         return GameUtils.formatSeconds((long)var5.time_played.get());
      })));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var9.nextY(new FormPlayerStatLongComponent(0, 0, var7 - var8, new LocalMessage("stats", "distance_ran"), () -> {
         return (long)GameMath.pixelsToMeters((float)var5.distance_ran.get());
      }, "m")));
      this.generalStats.addComponent((FormPlayerStatLongComponent)var9.nextY(new FormPlayerStatLongComponent(0, 0, var7 - var8, new LocalMessage("stats", "distance_ridden"), () -> {
         return (long)GameMath.pixelsToMeters((float)var5.distance_ridden.get());
      }, "m")));
      PlayerStatsContentBox var10000 = this.generalStats;
      int var10006 = var7 - var8;
      LocalMessage var10007 = new LocalMessage("stats", "damage_dealt");
      IncIntPlayerStat var10008 = var5.damage_dealt;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_types", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.damageTypes);
      });
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "damage_taken");
      var10008 = var5.damage_taken;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "deaths");
      var10008 = var5.deaths;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "mob_kills");
      MobKillsStat var10 = var5.mob_kills;
      Objects.requireNonNull(var10);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10::getTotalKills)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "boss_kills");
      var10 = var5.mob_kills;
      Objects.requireNonNull(var10);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10::getBossKills)));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_mobs", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.mobKills);
      });
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "island_travels");
      var10008 = var5.island_travels;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "islands_visited");
      BiomesVisitedStat var11 = var5.biomes_visited;
      Objects.requireNonNull(var11);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var11::getTotalBiomes)));
      this.generalStats.addComponent((FormPlayerStatComponent)var9.nextY(new FormPlayerStatComponent(0, 0, var7 - var8, new LocalMessage("stats", "biomes_visited"), () -> {
         return var5.biomes_visited.getTotalBiomeTypes() + "/" + BiomeRegistry.getTotalDiscoverableBiomesTypes();
      })));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_biomes", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.biomesDiscovered);
      });
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "objects_mined");
      var10008 = var5.objects_mined;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "objects_placed");
      var10008 = var5.objects_placed;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "tiles_mined");
      var10008 = var5.tiles_mined;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "tiles_placed");
      var10008 = var5.tiles_placed;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "food_consumed");
      ItemCountStat var12 = var5.food_consumed;
      Objects.requireNonNull(var12);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var12::getTotal)));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_food", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.foodConsumed);
      });
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "potions_consumed");
      var12 = var5.potions_consumed;
      Objects.requireNonNull(var12);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var12::getTotal)));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_potions", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.potionsConsumed);
      });
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "fish_caught");
      var10008 = var5.fish_caught;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "quests_completed");
      var10008 = var5.quests_completed;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "money_earned");
      var10008 = var5.money_earned;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "items_sold");
      var10008 = var5.items_sold;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "money_spent");
      var10008 = var5.money_spent;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "items_bought");
      var10008 = var5.items_bought;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "items_enchanted");
      var10008 = var5.items_enchanted;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "items_upgraded");
      var10008 = var5.items_upgraded;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "items_salvaged");
      var10008 = var5.items_salvaged;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      this.generalStats.addComponent((FormPlayerStatComponent)var9.nextY(new FormPlayerStatComponent(0, 0, var7 - var8, new LocalMessage("stats", "items_obtained"), () -> {
         return var5.items_obtained.getTotalStatItems() + "/" + ItemRegistry.getTotalStatItemsObtainable();
      })));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_item_list", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.itemsObtained);
      });
      this.generalStats.addComponent((FormPlayerStatComponent)var9.nextY(new FormPlayerStatComponent(0, 0, var7 - var8, new LocalMessage("stats", "trinkets_worn"), () -> {
         return var5.trinkets_worn.getTotalTrinketsWorn() + "/" + ItemRegistry.getTotalTrinkets();
      })));
      ((FormLocalTextButton)this.generalStats.addComponent((FormLocalTextButton)var9.nextY(new FormLocalTextButton("stats", "show_missing_trinkets", 0, 0, var7 - var8, FormInputSize.SIZE_20, ButtonColor.BASE)))).onClicked((var1x) -> {
         this.switcher.makeCurrent(this.trinketsWorn);
      });
      this.generalStats.addComponent((FormPlayerStatComponent)var9.nextY(new FormPlayerStatComponent(0, 0, var7 - var8, new LocalMessage("stats", "set_bonuses_worn"), () -> {
         return var5.set_bonuses_worn.getTotalSetBonusesWorn() + "/" + BuffRegistry.getTotalSetBonuses();
      })));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "ladders_used");
      var10008 = var5.ladders_used;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "doors_used");
      var10008 = var5.doors_used;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "plates_triggered");
      var10008 = var5.plates_triggered;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "levers_flicked");
      var10008 = var5.levers_flicked;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "homestones_used");
      var10008 = var5.homestones_used;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "waystones_used");
      var10008 = var5.waystones_used;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "crafted_items");
      var10008 = var5.crafted_items;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      var10000 = this.generalStats;
      var10006 = var7 - var8;
      var10007 = new LocalMessage("stats", "crates_broken");
      var10008 = var5.crates_broken;
      Objects.requireNonNull(var10008);
      var10000.addComponent((FormPlayerStatIntComponent)var9.nextY(new FormPlayerStatIntComponent(0, 0, var10006, var10007, var10008::get)));
      this.generalStats.fitContentBoxToComponents(var6);
      this.damageTypes = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this) {
         public boolean backPressed() {
            if (PlayerStatsForm.this.subMenuBackPressed != null) {
               PlayerStatsForm.this.subMenuBackPressed.run();
               PlayerStatsForm.this.subMenuBackPressed = null;
            } else {
               PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
            }

            return true;
         }
      }, (var1x, var2x) -> {
         if (var2x) {
            this.damageTypesUpdate.run();
         }

      });
      var8 = this.damageTypes.getScrollBarWidth();
      this.subForms.add(this.damageTypes);
      this.damageTypesUpdate = () -> {
         this.damageTypes.clearComponents();
         FormFlow var5x = new FormFlow();
         Iterator var6x = DamageTypeRegistry.getDamageTypes().iterator();

         while(var6x.hasNext()) {
            DamageType var7x = (DamageType)var6x.next();
            this.damageTypes.addComponent((FormPlayerStatIntComponent)var5x.nextY(new FormPlayerStatIntComponent(0, 0, var7 - var8, var7x.getStatsText(), () -> {
               return var5.type_damage_dealt.getDamage(var7x);
            })));
         }

         this.damageTypes.fitContentBoxToComponents(var6);
      };
      this.mobKills = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this) {
         public boolean backPressed() {
            if (PlayerStatsForm.this.subMenuBackPressed != null) {
               PlayerStatsForm.this.subMenuBackPressed.run();
               PlayerStatsForm.this.subMenuBackPressed = null;
            } else {
               PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
            }

            return true;
         }
      }, (var1x, var2x) -> {
         if (var2x) {
            this.mobKillsUpdate.run();
         }

      });
      var8 = this.mobKills.getScrollBarWidth();
      this.subForms.add(this.mobKills);
      this.mobKillsUpdate = () -> {
         this.mobKills.clearComponents();
         FormFlow var5x = new FormFlow();
         LinkedList var6x = new LinkedList();
         var5.mob_kills.forEach((var3, var4) -> {
            var6x.add(new FormPlayerStatIntComponent(0, 0, var7 - var8, MobRegistry.getLocalization(var3), () -> {
               return var4;
            }));
         });
         var6x.sort(Comparator.comparing((var0) -> {
            return var0.getDisplayName().translate();
         }));
         var6x.forEach((var2) -> {
            this.mobKills.addComponent((FormPlayerStatComponent)var5x.nextY(var2));
         });
         this.mobKills.fitContentBoxToComponents(var6);
      };
      this.foodConsumed = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this) {
         public boolean backPressed() {
            if (PlayerStatsForm.this.subMenuBackPressed != null) {
               PlayerStatsForm.this.subMenuBackPressed.run();
               PlayerStatsForm.this.subMenuBackPressed = null;
            } else {
               PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
            }

            return true;
         }
      }, (var1x, var2x) -> {
         if (var2x) {
            this.foodConsumedUpdate.run();
         }

      });
      var8 = this.foodConsumed.getScrollBarWidth();
      this.subForms.add(this.foodConsumed);
      this.foodConsumedUpdate = () -> {
         this.foodConsumed.clearComponents();
         FormFlow var5x = new FormFlow();
         LinkedList var6x = new LinkedList();
         var5.food_consumed.forEach((var3, var4) -> {
            var6x.add(new FormPlayerStatIntComponent(0, 0, var7 - var8, ItemRegistry.getLocalization(ItemRegistry.getItemID(var3)), () -> {
               return var4;
            }));
         });
         var6x.sort(Comparator.comparing((var0) -> {
            return var0.getDisplayName().translate();
         }));
         var6x.forEach((var2) -> {
            this.foodConsumed.addComponent((FormPlayerStatComponent)var5x.nextY(var2));
         });
         this.foodConsumed.fitContentBoxToComponents(var6);
      };
      this.potionsConsumed = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this) {
         public boolean backPressed() {
            if (PlayerStatsForm.this.subMenuBackPressed != null) {
               PlayerStatsForm.this.subMenuBackPressed.run();
               PlayerStatsForm.this.subMenuBackPressed = null;
            } else {
               PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
            }

            return true;
         }
      }, (var1x, var2x) -> {
         if (var2x) {
            this.potionsConsumedUpdate.run();
         }

      });
      var8 = this.potionsConsumed.getScrollBarWidth();
      this.subForms.add(this.potionsConsumed);
      this.potionsConsumedUpdate = () -> {
         this.potionsConsumed.clearComponents();
         FormFlow var5x = new FormFlow();
         LinkedList var6x = new LinkedList();
         var5.potions_consumed.forEach((var3, var4) -> {
            var6x.add(new FormPlayerStatIntComponent(0, 0, var7 - var8, ItemRegistry.getLocalization(ItemRegistry.getItemID(var3)), () -> {
               return var4;
            }));
         });
         var6x.sort(Comparator.comparing((var0) -> {
            return var0.getDisplayName().translate();
         }));
         var6x.forEach((var2) -> {
            this.potionsConsumed.addComponent((FormPlayerStatComponent)var5x.nextY(var2));
         });
         this.potionsConsumed.fitContentBoxToComponents(var6);
      };
      this.biomesDiscovered = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this) {
         public boolean backPressed() {
            if (PlayerStatsForm.this.subMenuBackPressed != null) {
               PlayerStatsForm.this.subMenuBackPressed.run();
               PlayerStatsForm.this.subMenuBackPressed = null;
            } else {
               PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
            }

            return true;
         }
      }, (var1x, var2x) -> {
         if (var2x) {
            this.biomesDiscoveredUpdate.run();
         }

      });
      var8 = this.biomesDiscovered.getScrollBarWidth();
      this.subForms.add(this.biomesDiscovered);
      this.biomesDiscoveredUpdate = () -> {
         this.biomesDiscovered.clearComponents();
         FormFlow var5x = new FormFlow();
         LinkedList var6x = new LinkedList();
         var5.biomes_visited.forEach((var3, var4) -> {
            var6x.add(new FormPlayerStatIntComponent(0, 0, var7 - var8, BiomeRegistry.getBiome(var3).getLocalization(), () -> {
               return var4;
            }));
         });
         var6x.sort(Comparator.comparing((var0) -> {
            return var0.getDisplayName().translate();
         }));
         var6x.forEach((var2) -> {
            this.biomesDiscovered.addComponent((FormPlayerStatComponent)var5x.nextY(var2));
         });
         this.biomesDiscovered.fitContentBoxToComponents(var6);
      };
      this.itemsObtained = (PlayerStatsObtainedItemsForm)this.switcher.addComponent(new PlayerStatsObtainedItemsForm(this, var5, var6, () -> {
         if (this.subMenuBackPressed != null) {
            this.subMenuBackPressed.run();
            this.subMenuBackPressed = null;
         } else {
            this.switcher.makeCurrent(this.generalStats);
         }

      }), (var1x, var2x) -> {
         if (var2x) {
            var1x.updateList(var5);
         }

      });
      this.subForms.add(this.itemsObtained);
      this.trinketsWorn = (PlayerStatsContentBox)this.switcher.addComponent(new PlayerStatsContentBox(this) {
         public boolean backPressed() {
            if (PlayerStatsForm.this.subMenuBackPressed != null) {
               PlayerStatsForm.this.subMenuBackPressed.run();
               PlayerStatsForm.this.subMenuBackPressed = null;
            } else {
               PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
            }

            return true;
         }
      }, (var1x, var2x) -> {
         if (var2x) {
            this.trinketsWornUpdate.run();
         }

      });
      this.subForms.add(this.trinketsWorn);
      this.trinketsWornUpdate = () -> {
         this.trinketsWorn.clearComponents();
         FormFlow var3 = new FormFlow();
         LinkedList var4 = new LinkedList();
         Iterator var5x = var5.trinkets_worn.getTrinketsWorn().iterator();

         while(var5x.hasNext()) {
            String var6x = (String)var5x.next();
            var4.add(var6x);
         }

         List var16 = ItemRegistry.getItems();
         var16.removeIf((var1) -> {
            return !ItemRegistry.countsInStats(var1.getID()) || !var1.isTrinketItem() || var4.contains(var1.getStringID());
         });
         if (var16.isEmpty()) {
            this.trinketsWorn.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("stats", "have_all_trinkets", new FontOptions(20), 0, this.trinketsWorn.getWidth() / 2, 0, this.trinketsWorn.getWidth())));
            this.trinketsWorn.fitContentBoxToComponents(var6);
            this.trinketsWorn.centerContentHorizontal();
         } else {
            byte var17 = 32;
            byte var7 = 2;
            int var8 = (this.trinketsWorn.getWidth() - var7) / (var17 + var7);
            int var9 = (this.trinketsWorn.getWidth() - var7) % (var17 + var7) / 2;
            this.trinketsWorn.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("stats", "missing_trinkets", new FontOptions(20), -1, var9, 0, this.trinketsWorn.getWidth())));
            int var10 = var3.next();
            int var11 = 0;

            for(Iterator var12 = var16.iterator(); var12.hasNext(); ++var11) {
               Item var13 = (Item)var12.next();
               int var14 = var11 % var8;
               int var15 = var11 / var8;
               this.trinketsWorn.addComponent(new FormItemDisplayComponent(var14 * (var17 + var7), var15 * (var17 + var7) + var10, var13.getDefaultItem((PlayerMob)null, 1)));
            }

            this.trinketsWorn.fitContentBoxToComponents(var9, 0, var7, var7);
         }

      };
      this.switcher.makeCurrent(this.generalStats);
   }

   public void onSelected() {
      this.switcher.makeCurrent(this.generalStats);
   }

   public boolean backPressed() {
      FormComponent var1 = this.switcher.getCurrent();
      return var1 instanceof PlayerStatsSubForm ? ((PlayerStatsSubForm)var1).backPressed() : false;
   }

   public void removeDisabledTip() {
      if (this.disabledContent != null) {
         this.removeComponent(this.disabledContent);
      }

      Iterator var1 = this.subForms.iterator();

      while(var1.hasNext()) {
         PlayerStatsSubForm var2 = (PlayerStatsSubForm)var1.next();
         var2.updateDisabled(0);
      }

   }

   public void setDisabledTip(GameMessage var1, GameMessage var2) {
      if (this.disabledContent != null) {
         this.removeComponent(this.disabledContent);
      }

      this.disabledContent = (Form)this.addComponent(new DisabledPreForm(this.getWidth(), var1, var2));
      Iterator var3 = this.subForms.iterator();

      while(var3.hasNext()) {
         PlayerStatsSubForm var4 = (PlayerStatsSubForm)var3.next();
         var4.updateDisabled(this.disabledContent.getHeight());
      }

   }
}
