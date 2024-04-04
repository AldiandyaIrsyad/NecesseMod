package necesse.level.maps.incursion;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public abstract class IncursionBiome {
   public final IDData idData = new IDData();
   public final int minimumTierLevel = 1;
   public final int maximumTierLevel = 3;
   public GameMessage displayName;
   protected GameTexture tabletTexture;
   public String bossMobStringID;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public IncursionBiome(String var1) {
      if (IncursionBiomeRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct IncursionBiome objects when registry is closed, since they are a static registered objects. Use IncursionBiome.getBiome(...) to get incursion biomes.");
      } else {
         this.bossMobStringID = var1;
      }
   }

   public void onIncursionBiomeRegistryClosed() {
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("biome", this.getStringID());
   }

   public void updateLocalDisplayName() {
      this.displayName = this.getNewLocalization();
   }

   public GameMessage getLocalization() {
      return this.displayName;
   }

   public void loadTextures() {
      try {
         this.tabletTexture = GameTexture.fromFileRaw("items/" + this.getStringID() + "tablet");
      } catch (FileNotFoundException var2) {
         this.tabletTexture = null;
      }

   }

   public GameSprite getTabletSprite() {
      return this.tabletTexture == null ? null : new GameSprite(this.tabletTexture);
   }

   public int increaseTabletTierByX(int var1, int var2) {
      int var3 = var1 + var2;
      return GameMath.limit(var3, 1, 3);
   }

   public abstract Collection<Item> getExtractionItems(IncursionData var1);

   public abstract LootTable getHuntDrop(IncursionData var1);

   public abstract LootTable getBossDrop(IncursionData var1);

   public abstract TicketSystemList<Supplier<IncursionData>> getAvailableIncursions(int var1);

   public ArrayList<FairType> getPrivateDropsDisplay(FontOptions var1) {
      return null;
   }

   public IncursionData getRandomIncursion(GameRandom var1, int var2) {
      return (IncursionData)((Supplier)this.getAvailableIncursions(var2).getRandomObject(var1)).get();
   }

   public int getUniqueModifierTickets(UniqueIncursionModifier var1) {
      return 100;
   }

   public abstract IncursionLevel getNewIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, Server var3, WorldEntity var4);

   public abstract ArrayList<Color> getFallenAltarGatewayColorsForBiome();

   public static void addReturnPortalOnTile(Level var0, int var1, int var2) {
      addReturnPortal(var0, (float)(var1 * 32 + 16), (float)(var2 * 32 + 16));
   }

   public static void addReturnPortal(Level var0, float var1, float var2) {
      var0.entityManager.addMob(MobRegistry.getMob("returnportal", var0), var1, var2);
   }

   public static Point generateEntrance(Level var0, GameRandom var1, int var2, int var3, String var4, String var5, String var6) {
      byte var7 = 40;
      int var8 = var1.getIntOffset(var0.width / 2, var0.width / 2 - var7 - var2 / 2);
      int var9 = var1.getIntOffset(var0.height / 2, var0.height / 2 - var7 - var2 / 2);
      generateEntrance(var0, var1, var8, var9, var2, var3, var4, var5, var6);
      return new Point(var8, var9);
   }

   public static void generateEntrance(Level var0, GameRandom var1, int var2, int var3, int var4, int var5, String var6, String var7, String var8) {
      int var9 = var6 == null ? -1 : TileRegistry.getTileID(var6);
      int var10 = var7 == null ? -1 : TileRegistry.getTileID(var7);
      Function var11;
      if (var9 != -1) {
         if (var10 != -1) {
            var11 = (var2x) -> {
               return var2x.getChance(0.75F) ? var9 : var10;
            };
         } else {
            var11 = (var1x) -> {
               return var9;
            };
         }
      } else if (var10 != -1) {
         var11 = (var1x) -> {
            return var10;
         };
      } else {
         var11 = null;
      }

      float var12 = (float)var4 / 2.0F * 32.0F;

      int var13;
      float var15;
      for(var13 = var2 - var4; var13 <= var2 + var4; ++var13) {
         for(int var14 = var3 - var4; var14 < var3 + var4; ++var14) {
            var15 = (float)(new Point(var2 * 32 + 16, var3 * 32 + 16)).distance((double)(var13 * 32 + 16), (double)(var14 * 32 + 16));
            float var16 = var15 / var12;
            if (var16 < 0.5F) {
               if (var11 != null) {
                  var0.setTile(var13, var14, (Integer)var11.apply(var1));
               }

               var0.setObject(var13, var14, 0);
            } else if (var16 <= 1.0F) {
               float var17 = Math.abs((var16 - 0.5F) * 2.0F - 1.0F) * 2.0F;
               if (var1.getChance(var17)) {
                  if (var1.getChance(0.75F)) {
                     if (var11 != null) {
                        var0.setTile(var13, var14, (Integer)var11.apply(var1));
                     }
                  } else if (var5 != -1) {
                     var0.setTile(var13, var14, var5);
                  }

                  var0.setObject(var13, var14, 0);
               }
            }
         }
      }

      if (var8 != null) {
         var13 = var1.getIntBetween(6, 8);
         float var22 = (float)var1.nextInt(360);
         var15 = 360.0F / (float)var13;
         int var23 = ObjectRegistry.getObjectID(var8);

         for(int var24 = 0; var24 < var13; ++var24) {
            var22 += var1.getFloatOffset(var15, var15 / 10.0F);
            Point2D.Float var18 = GameMath.getAngleDir(var22);
            float var19 = (float)var4 / 3.5F * 32.0F;
            int var20 = (int)((float)(var2 * 32 + 16) + var18.x * var19) / 32;
            int var21 = (int)((float)(var3 * 32 + 16) + var18.y * var19) / 32;
            var0.setObject(var20, var21, var23);
         }
      }

      addReturnPortalOnTile(var0, var2, var3);
   }

   public static HashMap<Integer, Float> saveMobHealths(Level var0) {
      HashMap var1 = new HashMap();
      Iterator var2 = var0.entityManager.mobs.iterator();

      while(var2.hasNext()) {
         Mob var3 = (Mob)var2.next();
         var1.put(var3.getUniqueID(), (float)var3.getHealth() / (float)var3.getMaxHealth());
      }

      return var1;
   }

   public static void applyMobHealths(Level var0, HashMap<Integer, Float> var1) {
      Iterator var2 = var0.entityManager.mobs.iterator();

      while(var2.hasNext()) {
         Mob var3 = (Mob)var2.next();
         var1.computeIfPresent(var3.getUniqueID(), (var1x, var2x) -> {
            var3.setHealthHidden((int)((float)var3.getMaxHealth() * var2x));
            return var2x;
         });
      }

   }
}
