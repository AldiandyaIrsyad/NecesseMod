package necesse.level.maps.incursion;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public abstract class BiomeMissionIncursionData extends IncursionData {
   public float difficulty;
   public IncursionBiome biome;
   protected int tabletTier;
   public ArrayList<ModifierValue<?>> levelModifiers = new ArrayList();
   private static final GameLootUtils.LootValueMap<IncursionModifier> modifierMap = new GameLootUtils.LootValueMap<IncursionModifier>() {
      public float getValuePerCount(IncursionModifier var1) {
         return var1.value;
      }

      public int getRemainingCount(IncursionModifier var1) {
         return var1.percent;
      }

      public void setRemainingCount(IncursionModifier var1, int var2) {
         var1.percent = var2;
      }

      public boolean canCombine(IncursionModifier var1, IncursionModifier var2) {
         return var1.modifier == var2.modifier;
      }

      public void onCombine(IncursionModifier var1, IncursionModifier var2) {
         var1.percent += var2.percent;
      }

      public IncursionModifier copy(IncursionModifier var1, int var2) {
         return new IncursionModifier(var1.modifier, var1.isLevelModifier, var1.invert, var1.value, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public Object copy(Object var1, int var2) {
         return this.copy((IncursionModifier)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onCombine(Object var1, Object var2) {
         this.onCombine((IncursionModifier)var1, (IncursionModifier)var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public boolean canCombine(Object var1, Object var2) {
         return this.canCombine((IncursionModifier)var1, (IncursionModifier)var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void setRemainingCount(Object var1, int var2) {
         this.setRemainingCount((IncursionModifier)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public int getRemainingCount(Object var1) {
         return this.getRemainingCount((IncursionModifier)var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public float getValuePerCount(Object var1) {
         return this.getValuePerCount((IncursionModifier)var1);
      }
   };

   public BiomeMissionIncursionData() {
   }

   public BiomeMissionIncursionData(float var1, IncursionBiome var2, int var3) {
      this.difficulty = var1;
      this.biome = var2;
      this.tabletTier = var3;
      this.initModifiers();
   }

   protected void initModifiers() {
      this.levelModifiers = new ArrayList();
      this.uniqueIncursionModifiers = new ArrayList();
      this.playerPersonalIncursionCompleteRewards = new ArrayList();
      this.playerSharedIncursionCompleteRewards = new ArrayList();
      float var1 = 25.0F;
      float var2 = 15.0F;
      float var3 = 15.0F;
      IncursionModifier var4 = new IncursionModifier(LevelModifiers.ENEMY_MAX_HEALTH, false, false, var1 * (float)this.tabletTier, 100);
      IncursionModifier var5 = new IncursionModifier(LevelModifiers.ENEMY_DAMAGE, false, false, var2 * (float)this.tabletTier, 100);
      IncursionModifier var6 = new IncursionModifier(LevelModifiers.LOOT, false, false, var3 * (float)this.tabletTier, 100);
      this.levelModifiers.add(new ModifierValue(var4.modifier, var4.value / (float)var4.percent));
      this.levelModifiers.add(new ModifierValue(var5.modifier, var5.value / (float)var5.percent));
      this.levelModifiers.add(new ModifierValue(var6.modifier, var6.value / (float)var5.percent));
      byte var7 = 1;

      for(int var8 = 0; var8 < var7; ++var8) {
         GameRandom var9 = (new GameRandom((long)this.getUniqueID())).nextSeeded(20 * (var8 + 1) * this.getTabletTier());
         TicketSystemList var10 = UniqueIncursionModifierRegistry.getAvailableIncursionModifiers(var9, this, (var1x) -> {
            return this.biome.getUniqueModifierTickets(var1x);
         });
         UniqueIncursionModifier var11 = (UniqueIncursionModifier)var10.getRandomObject(var9);
         if (var11 != null) {
            this.uniqueIncursionModifiers.add(var11);
         }

         UniqueIncursionModifierRegistry.ModifierChallengeLevel var12 = var11 == null ? UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy : var11.challengeLevel;
         this.playerSharedIncursionCompleteRewards.add(UniqueIncursionRewardsRegistry.getSeededRandomRewardBasedOnModifierChallengeLevel(var9, var12, this.getTabletTier()));
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addFloat("difficulty", this.difficulty);
      var1.addUnsafeString("biome", this.biome.getStringID());
      var1.addInt("tabletTier", this.tabletTier);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.difficulty = var1.getFloat("difficulty");
      String var2 = var1.getUnsafeString("biome");
      this.biome = IncursionBiomeRegistry.getBiome(var2);
      if (this.biome == null) {
         throw new LoadDataException("Could not find incursion biome with stringID " + var2);
      } else {
         this.tabletTier = var1.getInt("tabletTier");
         this.initModifiers();
      }
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);
      var1.putNextFloat(this.difficulty);
      var1.putNextShortUnsigned(this.biome.getID());
      var1.putNextInt(this.tabletTier);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);
      this.difficulty = var1.getNextFloat();
      int var2 = var1.getNextShortUnsigned();
      this.biome = IncursionBiomeRegistry.getBiome(var2);
      if (this.biome == null) {
         (new IllegalArgumentException("Could not find incursion biome with id " + var2)).printStackTrace();
      }

      this.tabletTier = var1.getNextInt();
      this.initModifiers();
   }

   public IncursionBiome getIncursionBiome() {
      return this.biome;
   }

   public GameSprite getTabletSprite() {
      return this.biome.getTabletSprite();
   }

   public void setTabletTier(int var1) {
      this.tabletTier = var1;
      this.initModifiers();
   }

   public int getTabletTier() {
      return this.tabletTier;
   }

   public boolean isSameIncursion(IncursionData var1) {
      if (var1.getID() != this.getID()) {
         return false;
      } else {
         BiomeMissionIncursionData var2 = (BiomeMissionIncursionData)var1;
         if (var2.getUniqueID() != this.getUniqueID()) {
            return false;
         } else if (var2.difficulty != this.difficulty) {
            return false;
         } else {
            return var2.biome.getID() == this.biome.getID();
         }
      }
   }

   public void setUpDetails(FallenAltarContainer var1, FallenAltarContainerForm var2, FormContentBox var3, boolean var4) {
      FormFlow var5 = new FormFlow(12);
      byte var6 = 16;
      byte var7 = 20;
      var3.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel(this.biome.getLocalization(), new FontOptions(var7), 0, var3.getMinContentWidth() / 2, 0, var3.getMinContentWidth() - 10), 4));
      LocalMessage var8 = new LocalMessage("item", "tierandincursiontype", new Object[]{"tiernumber", this.tabletTier, "incursiontype", this.getIncursionMissionTypeName()});
      ((FormLocalLabel)var3.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel(var8, new FontOptions(var6), 0, var3.getMinContentWidth() / 2, 0, var3.getMinContentWidth() - 10), 4))).setColor(Settings.UI.incursionTierPurple);
      var5.next(14);
      var3.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel("ui", "incursionloot", new FontOptions(var7), 0, var3.getMinContentWidth() / 2, 0, var3.getMinContentWidth() - 10), 2));
      Iterator var9 = this.getLoot(this, new FontOptions(var6)).iterator();

      while(var9.hasNext()) {
         FairType var10 = (FairType)var9.next();
         FormFairTypeLabel var11 = new FormFairTypeLabel("", var3.getMinContentWidth() / 2, 0);
         var11.setMaxWidth(var3.getMinContentWidth() - 10);
         var11.setCustomFairType(var10);
         var11.setTextAlign(FairType.TextAlign.CENTER);
         var3.addComponent((FormFairTypeLabel)var5.nextY(var11, 2));
      }

      ArrayList var14 = this.biome.getPrivateDropsDisplay(new FontOptions(var6));
      Iterator var15;
      if (var14 != null) {
         var15 = var14.iterator();

         while(var15.hasNext()) {
            FairType var16 = (FairType)var15.next();
            FormFairTypeLabel var12 = new FormFairTypeLabel("", var3.getMinContentWidth() / 2, 0);
            var12.setCustomFairType(var16);
            var12.setMaxWidth(var3.getMinContentWidth() - 10);
            var12.setTextAlign(FairType.TextAlign.CENTER);
            var3.addComponent((FormFairTypeLabel)var5.nextY(var12, 2));
         }
      }

      this.getAndSetupRewardLabels(this.playerPersonalIncursionCompleteRewards, var6, var3, var5);
      this.getAndSetupRewardLabels(this.playerSharedIncursionCompleteRewards, var6, var3, var5);
      var5.next(14);
      var3.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel("ui", "incursionmodifiers", new FontOptions(var7), 0, var3.getMinContentWidth() / 2, 0, var3.getMinContentWidth() - 10), 2));
      if (!this.levelModifiers.isEmpty()) {
         ((Stream)this.levelModifiers.stream().flatMap((var0) -> {
            return var0.getAllTooltips().stream();
         }).sequential()).sorted(Comparator.comparingInt((var0) -> {
            return -var0.sign;
         })).forEach((var3x) -> {
            FormFairTypeLabel var4 = new FormFairTypeLabel("", var3.getMinContentWidth() / 2, 0);
            var4.setFontOptions(new FontOptions(var6));
            var4.setMaxWidth(var3.getMinContentWidth() - 10);
            var4.setCustomFairType(var3x.toFairType(var4.getFontOptions(), false));
            var4.setTextAlign(FairType.TextAlign.CENTER);
            var4.setColor(var3x.getTextColor());
            var3.addComponent((FormFairTypeLabel)var5.nextY(var4, 2));
         });
      }

      var5.next(4);
      var3.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, var5.next(), 384, true));
      var5.next(4);
      var15 = this.uniqueIncursionModifiers.iterator();

      while(var15.hasNext()) {
         UniqueIncursionModifier var17 = (UniqueIncursionModifier)var15.next();
         GameMessageBuilder var18 = new GameMessageBuilder();
         FormFairTypeLabel var13 = new FormFairTypeLabel(var18, var3.getMinContentWidth() / 2, 0);
         var13.setFontOptions(new FontOptions(var6));
         var13.setMaxWidth(var3.getMinContentWidth() - 10);
         var13.setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(var13.getFontOptions()), TypeParsers.ItemIcon(var13.getFontOptions().getSize()));
         var13.setTextAlign(FairType.TextAlign.CENTER);
         var13.setText((GameMessage)var17.getModifierDescription());
         var13.setColor(Settings.UI.incursionModifierOrange);
         var3.addComponent((FormFairTypeLabel)var5.nextY(var13, 4));
      }

      if (GlobalData.isDevMode() && GlobalData.debugCheatActive()) {
         var5.next(14);
         var3.addComponent((FormLabel)var5.nextY(new FormLabel("Debug info:", new FontOptions(16), 0, var3.getMinContentWidth() / 2, 0), 4));
         var3.addComponent((FormLabel)var5.nextY(new FormLabel("UniqueID: " + this.getUniqueID(), new FontOptions(12), 0, var3.getMinContentWidth() / 2, 0), 4));
         var3.addComponent((FormLabel)var5.nextY(new FormLabel("Difficulty: " + this.difficulty, new FontOptions(12), 0, var3.getMinContentWidth() / 2, 0), 4));
      }

      var5.next(2);
      var3.setContentBox(new Rectangle(var3.getWidth(), var5.next()));
      var3.setScroll(0, 0);
   }

   protected abstract Iterable<FairType> getLoot(IncursionData var1, FontOptions var2);

   public void getAndSetupRewardLabels(ArrayList<ArrayList<Supplier<InventoryItem>>> var1, int var2, FormContentBox var3, FormFlow var4) {
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         ArrayList var6 = (ArrayList)var5.next();
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            Supplier var8 = (Supplier)var7.next();
            FormFairTypeLabel var9 = new FormFairTypeLabel("", var3.getMinContentWidth() / 2, 0);
            var9.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(var2));
            var9.setFontOptions(new FontOptions(var2));
            var9.setMaxWidth(var3.getMinContentWidth() - 10);
            InventoryItem var10 = (InventoryItem)var8.get();
            String var11;
            if (var10.item instanceof GatewayTabletItem) {
               var11 = Localization.translate("item", "tier", "tiernumber", (Object)GatewayTabletItem.getIncursionData(var10).getTabletTier());
               var9.setText(TypeParsers.getItemParseString(var10) + " " + var11 + " " + GatewayTabletItem.getIncursionData(var10).getIncursionBiome().displayName.translate());
            } else {
               var11 = Localization.translate("item", "tier", "tiernumber", (Object)((int)var10.item.getUpgradeTier(var10)));
               var9.setText(TypeParsers.getItemParseString(var10) + " " + var11 + " " + Localization.translate("item", var10.item.getStringID()));
            }

            var9.setTextAlign(FairType.TextAlign.CENTER);
            var3.addComponent((FormFairTypeLabel)var4.nextY(var9, 2));
            var9.setColor(Settings.UI.incursionModifierOrange);
         }
      }

   }

   public int getLootCount() {
      int var1 = 0;

      for(Iterator var2 = this.getLoot(this, new FontOptions(0)).iterator(); var2.hasNext(); ++var1) {
         FairType var3 = (FairType)var2.next();
      }

      ArrayList var4 = this.biome.getPrivateDropsDisplay(new FontOptions(0));
      if (var4 != null) {
         var1 += var4.size();
      }

      return var1;
   }

   protected FairType getItemMessage(InventoryItem var1, FontOptions var2) {
      FairType var3 = new FairType();
      var3.append(new FairItemGlyph(var2.getSize(), var1));
      var3.append(var2, var1.getItemDisplayName());
      return var3;
   }

   protected FairType getItemMessage(Item var1, FontOptions var2) {
      return this.getItemMessage(new InventoryItem(var1), var2);
   }

   public GameTooltips getOpenButtonTooltips(FallenAltarContainer var1) {
      return null;
   }

   public boolean canOpen(FallenAltarContainer var1) {
      return true;
   }

   public void onOpened(FallenAltarContainer var1, ServerClient var2) {
      if (var2 != null) {
         var2.newStats.opened_incursions.add(this);
      }

   }

   public void onCompleted(FallenAltarObjectEntity var1, ServerClient var2) {
      if (var2 != null) {
         var2.newStats.completed_incursions.add(this);
      }

   }

   public void onClosed(FallenAltarObjectEntity var1, ServerClient var2) {
   }

   public IncursionLevel getNewIncursionLevel(LevelIdentifier var1, Server var2, WorldEntity var3) {
      return this.biome.getNewIncursionLevel(var1, this, var2, var3);
   }

   public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
      return this.levelModifiers.stream();
   }

   public ArrayList<UniqueIncursionModifier> getUniqueIncursionModifiers() {
      return this.uniqueIncursionModifiers;
   }

   private static class IncursionModifier {
      public final Modifier<Float> modifier;
      public boolean isLevelModifier;
      public boolean invert;
      public float value;
      public int percent;

      public IncursionModifier(Modifier<Float> var1, boolean var2, boolean var3, float var4, int var5) {
         this.modifier = var1;
         this.isLevelModifier = var2;
         this.invert = var3;
         this.value = var4;
         this.percent = var5;
      }

      public void apply(Collection<ModifierValue<?>> var1, Collection<ModifierValue<?>> var2) {
         float var3 = (float)this.percent / 100.0F;
         if (this.invert) {
            var3 = -var3;
         }

         if (this.isLevelModifier) {
            var1.add(new ModifierValue(this.modifier, var3));
         } else {
            var2.add(new ModifierValue(this.modifier, var3));
         }

      }
   }
}
