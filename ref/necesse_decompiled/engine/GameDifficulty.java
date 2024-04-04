package necesse.engine;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.ui.ButtonIcon;

public enum GameDifficulty {
   CASUAL(0.4F, 0.6F, 1.0F, 0.6F, 0.9F, new LocalMessage("ui", "diffcasual"), new LocalMessage("ui", "diffcasualdesc"), new LocalMessage("ui", "diffcasualeffects"), () -> {
      return Settings.UI.difficulty_casual_background;
   }, () -> {
      return Settings.UI.difficulty_casual;
   }),
   ADVENTURE(0.7F, 0.8F, 1.0F, 0.85F, 1.0F, new LocalMessage("ui", "diffadventure"), new LocalMessage("ui", "diffadventuredesc"), new LocalMessage("ui", "diffadventureeffects"), () -> {
      return Settings.UI.difficulty_adventure_background;
   }, () -> {
      return Settings.UI.difficulty_adventure;
   }),
   CLASSIC(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, new LocalMessage("ui", "diffclassic"), new LocalMessage("ui", "diffclassicdesc"), new LocalMessage("ui", "diffclassiceffects"), () -> {
      return Settings.UI.difficulty_classic_background;
   }, () -> {
      return Settings.UI.difficulty_classic;
   }),
   HARD(1.2F, 1.2F, 0.75F, 1.2F, 1.2F, new LocalMessage("ui", "diffhard"), new LocalMessage("ui", "diffharddesc"), new LocalMessage("ui", "diffhardeffects"), () -> {
      return Settings.UI.difficulty_hard_background;
   }, () -> {
      return Settings.UI.difficulty_hard;
   }),
   BRUTAL(1.6F, 1.4F, 0.4F, 1.4F, 1.5F, new LocalMessage("ui", "diffbrutal"), new LocalMessage("ui", "diffbrutaldesc"), new LocalMessage("ui", "diffbrutaleffects"), () -> {
      return Settings.UI.difficulty_brutal_background;
   }, () -> {
      return Settings.UI.difficulty_brutal;
   });

   public final GameMessage displayName;
   public final GameMessage description;
   public final GameMessage effects;
   public final float damageTakenModifier;
   public final float knockbackGivenModifier;
   public final float enemySpawnRateModifier;
   public final float enemySpawnCapModifier;
   public final float raiderDamageModifier;
   public final Supplier<ButtonIcon> buttonIconBackgroundSupplier;
   public final Supplier<ButtonIcon> buttonIconForegroundSupplier;

   private GameDifficulty(float var3, float var4, float var5, float var6, float var7, GameMessage var8, GameMessage var9, GameMessage var10, Supplier var11, Supplier var12) {
      this.damageTakenModifier = var3;
      this.raiderDamageModifier = var4;
      this.knockbackGivenModifier = var5;
      this.enemySpawnRateModifier = var6;
      this.enemySpawnCapModifier = var7;
      this.displayName = var8;
      this.description = var9;
      this.effects = var10;
      this.buttonIconBackgroundSupplier = var11;
      this.buttonIconForegroundSupplier = var12;
   }

   // $FF: synthetic method
   private static GameDifficulty[] $values() {
      return new GameDifficulty[]{CASUAL, ADVENTURE, CLASSIC, HARD, BRUTAL};
   }
}
