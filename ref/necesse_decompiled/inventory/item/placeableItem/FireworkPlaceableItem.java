package necesse.inventory.item.placeableItem;

import java.awt.Color;
import java.util.function.Consumer;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnFirework;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.entity.particle.fireworks.FireworksRocketParticle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FireworkPlaceableItem extends PlaceableItem {
   public FireworkPlaceableItem() {
      super(100, true);
      this.rarity = Item.Rarity.COMMON;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "fireworkrockettip"));
      GNDItemMap var5 = var1.getGndData();
      FireworksShape var6 = getShape(var5);
      var4.add(Localization.translate("itemtooltip", "fireworkshape", "shape", var6 == null ? Localization.translate("itemtooltip", "fireworkrandom") : var6.displayName.translate()));
      FireworkColor var7 = getColor(var5);
      var4.add(Localization.translate("itemtooltip", "fireworkcolor", "color", var7 == null ? Localization.translate("itemtooltip", "fireworkrandom") : var7.displayName.translate()));
      FireworkCrackle var8 = getCrackle(var5);
      var4.add(var8 == null ? Localization.translate("itemtooltip", "fireworkrandomcrackle") : var8.displayName.translate());
      return var4;
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "shape", "color", "crackle");
   }

   public static void spawnFireworks(GNDItemMap var0, Level var1, float var2, float var3, int var4, float var5, int var6) {
      GameRandom var7 = new GameRandom((long)var6);
      var1.entityManager.addParticle(new FireworksRocketParticle(var1, var2, var3, 1200L, var4, getExplosion(var0, var5, var7), var7), true, Particle.GType.CRITICAL);
   }

   public static FireworksExplosion getExplosion(GNDItemMap var0, float var1, GameRandom var2) {
      FireworksShape var3 = getShape(var0);
      if (var3 == null) {
         var3 = (FireworksShape)var2.getOneOf((Object[])FireworkPlaceableItem.FireworksShape.values());
      }

      FireworkColor var4 = getColor(var0);
      if (var4 == null) {
         var4 = (FireworkColor)var2.getOneOf((Object[])FireworkPlaceableItem.FireworkColor.values());
      }

      FireworkCrackle var5 = getCrackle(var0);
      if (var5 == null) {
         var5 = (FireworkCrackle)var2.getOneOf((Object[])FireworkPlaceableItem.FireworkCrackle.values());
      }

      FireworksExplosion var6 = new FireworksExplosion((FireworksRocketParticle.ParticleGetter)null);
      var3.explosionModifier.play(var6, var1, var2);
      var4.explosionModifier.accept(var6);
      var5.explosionModifier.accept(var6);
      return var6;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithTile(new PacketSpawnFirework(var1, (float)var2, (float)var3, GameRandom.globalRandom.getIntBetween(300, 400), (float)GameRandom.globalRandom.getIntBetween(150, 250), var5.getGndData(), GameRandom.globalRandom.nextInt()), var1, var2 / 32, var3 / 32);
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public static FireworksShape getShape(GNDItemMap var0) {
      if (var0.hasKey("shape")) {
         FireworksShape[] var1 = FireworkPlaceableItem.FireworksShape.values();
         return var1[Math.abs(var0.getByte("shape") - 1) % var1.length];
      } else {
         return null;
      }
   }

   public static FireworkColor getColor(GNDItemMap var0) {
      if (var0.hasKey("color")) {
         FireworkColor[] var1 = FireworkPlaceableItem.FireworkColor.values();
         return var1[Math.abs(var0.getByte("color") - 1) % var1.length];
      } else {
         return null;
      }
   }

   public static FireworkCrackle getCrackle(GNDItemMap var0) {
      if (var0.hasKey("crackle")) {
         FireworkCrackle[] var1 = FireworkPlaceableItem.FireworkCrackle.values();
         return var1[Math.abs(var0.getByte("crackle") - 1) % var1.length];
      } else {
         return null;
      }
   }

   public static enum FireworksShape {
      Sphere(new LocalMessage("itemtooltip", "fireworksphere"), (var0, var1, var2) -> {
         var0.pathGetter = FireworksPath.sphere(var1);
      }),
      Splash(new LocalMessage("itemtooltip", "fireworksplash"), (var0, var1, var2) -> {
         var0.pathGetter = FireworksPath.splash((float)var2.getIntBetween(0, 360), var1);
      }),
      Disc(new LocalMessage("itemtooltip", "fireworkdisc"), (var0, var1, var2) -> {
         var0.pathGetter = FireworksPath.disc(var1);
         var0.minSize = 20;
         var0.maxSize = 30;
         var0.trailSize = 15.0F;
         var0.trailFadeTime = 1000;
         var0.particles = 50;
         var0.trailChance = 1.0F;
      }),
      Star(new LocalMessage("itemtooltip", "fireworkstar"), (var0, var1, var2) -> {
         var0.pathGetter = FireworksPath.shape(FireworksPath.star, var1, (var0x) -> {
            return Math.min(1.0F, var0x.nextFloat() * 1.2F);
         });
      }),
      Heart(new LocalMessage("itemtooltip", "fireworkheart"), (var0, var1, var2) -> {
         var0.pathGetter = FireworksPath.shape(FireworksPath.heart, var1, (var0x) -> {
            return Math.min(1.0F, var0x.nextFloat() * 1.2F);
         });
      });

      public final GameMessage displayName;
      public final FireworksRocketParticle.ExplosionModifier explosionModifier;

      private FireworksShape(GameMessage var3, FireworksRocketParticle.ExplosionModifier var4) {
         this.displayName = var3;
         this.explosionModifier = var4;
      }

      // $FF: synthetic method
      private static FireworksShape[] $values() {
         return new FireworksShape[]{Sphere, Splash, Disc, Star, Heart};
      }
   }

   public static enum FireworkColor {
      Confetti(new LocalMessage("itemtooltip", "fireworkconfetti"), (var0) -> {
         var0.colorGetter = (var0x, var1, var2) -> {
            return Color.getHSBColor(var2.nextFloat(), 1.0F, 1.0F);
         };
      }),
      Flame(new LocalMessage("itemtooltip", "fireworkflame"), (var0) -> {
         var0.colorGetter = (var0x, var1, var2) -> {
            return ParticleOption.randomFlameColor(var2);
         };
      }),
      Red(new LocalMessage("itemtooltip", "fireworkred"), (var0) -> {
         var0.colorGetter = (var0x, var1, var2) -> {
            return ParticleOption.randomFlameColor(var2, 0.0F);
         };
      }),
      Green(new LocalMessage("itemtooltip", "fireworkgreen"), (var0) -> {
         var0.colorGetter = (var0x, var1, var2) -> {
            return ParticleOption.randomFlameColor(var2, 110.0F);
         };
      }),
      Blue(new LocalMessage("itemtooltip", "fireworkblue"), (var0) -> {
         var0.colorGetter = (var0x, var1, var2) -> {
            return ParticleOption.randomFlameColor(var2, 240.0F);
         };
      }),
      Pink(new LocalMessage("itemtooltip", "fireworkpink"), (var0) -> {
         var0.colorGetter = (var0x, var1, var2) -> {
            return ParticleOption.randomFlameColor(var2, 310.0F);
         };
      });

      public final GameMessage displayName;
      public final Consumer<FireworksExplosion> explosionModifier;

      private FireworkColor(GameMessage var3, Consumer var4) {
         this.displayName = var3;
         this.explosionModifier = var4;
      }

      // $FF: synthetic method
      private static FireworkColor[] $values() {
         return new FireworkColor[]{Confetti, Flame, Red, Green, Blue, Pink};
      }
   }

   public static enum FireworkCrackle {
      NoCrackle(new LocalMessage("itemtooltip", "fireworknocrackle"), (var0) -> {
         var0.popChance = 0.0F;
      }),
      Crackle(new LocalMessage("itemtooltip", "fireworkyescrackle"), (var0) -> {
         var0.popChance = 0.5F;
      });

      public final GameMessage displayName;
      public Consumer<FireworksExplosion> explosionModifier;

      private FireworkCrackle(GameMessage var3, Consumer var4) {
         this.displayName = var3;
         this.explosionModifier = var4;
      }

      // $FF: synthetic method
      private static FireworkCrackle[] $values() {
         return new FireworkCrackle[]{NoCrackle, Crackle};
      }
   }

   public static class FireworkItemCreator {
      FireworksShape shape;
      FireworkColor color;
      FireworkCrackle crackle;

      public FireworkItemCreator() {
      }

      public GNDItemMap getGNDData() {
         GNDItemMap var1 = new GNDItemMap();
         this.applyToData(var1);
         return var1;
      }

      public FireworkItemCreator applyToData(GNDItemMap var1) {
         if (this.shape != null) {
            var1.setByte("shape", (byte)(this.shape.ordinal() + 1));
         }

         if (this.color != null) {
            var1.setByte("color", (byte)(this.color.ordinal() + 1));
         }

         if (this.crackle != null) {
            var1.setByte("crackle", (byte)(this.crackle.ordinal() + 1));
         }

         return this;
      }

      public FireworkItemCreator shape(FireworksShape var1) {
         this.shape = var1;
         return this;
      }

      public FireworkItemCreator color(FireworkColor var1) {
         this.color = var1;
         return this;
      }

      public FireworkItemCreator crackle(FireworkCrackle var1) {
         this.crackle = var1;
         return this;
      }

      public FireworkItemCreator applyToItem(InventoryItem var1) {
         return this.applyToData(var1.getGndData());
      }

      public InventoryItem getNewItem() {
         InventoryItem var1 = new InventoryItem("fireworkrocket");
         this.applyToItem(var1);
         return var1;
      }
   }
}
