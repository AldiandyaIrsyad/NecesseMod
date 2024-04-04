package necesse.inventory.recipe;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemByte;
import necesse.engine.network.gameNetworkData.GNDItemDouble;
import necesse.engine.network.gameNetworkData.GNDItemInt;
import necesse.engine.network.gameNetworkData.GNDItemLong;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.gameNetworkData.GNDItemShort;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;

public class RecipeData {
   public final String resultID;
   public final int resultAmount;
   public final IngredientData[] ingredients;
   public final String tech;
   public final boolean isHidden;
   public final GNDItemMap gndData;
   protected String sortResultID;
   protected boolean sortBefore;

   public RecipeData(String var1, int var2, IngredientData[] var3, String var4, boolean var5, GNDItemMap var6) {
      this.resultID = var1;
      this.resultAmount = var2;
      this.ingredients = var3;
      this.tech = var4;
      this.isHidden = var5;
      this.gndData = var6;
   }

   public RecipeData(Recipe var1) {
      this.resultID = var1.resultID;
      this.resultAmount = var1.resultAmount;
      this.ingredients = new IngredientData[var1.ingredients.length];

      for(int var2 = 0; var2 < this.ingredients.length; ++var2) {
         this.ingredients[var2] = new IngredientData(var1.ingredients[var2]);
      }

      this.tech = var1.tech.getStringID();
      this.isHidden = var1.isHidden;
      this.gndData = var1.getGndData().copy();
      this.sortResultID = var1.sortResultID;
      this.sortBefore = var1.sortBefore;
   }

   public RecipeData(LoadData var1) throws SaveSyntaxException {
      ListIterator var2 = var1.getLoadData().listIterator();
      if (!var2.hasNext()) {
         throw new SaveSyntaxException("Missing recipe resultName");
      } else {
         LoadData var3 = (LoadData)var2.next();
         if (!var3.isData()) {
            throw new SaveSyntaxException("Recipe resultName must be a data component");
         } else {
            this.resultID = LoadData.getUnsafeString(var3);
            if (!var2.hasNext()) {
               throw new SaveSyntaxException("Missing recipe resultAmount for '" + this.resultID + "'");
            } else {
               var3 = (LoadData)var2.next();
               if (!var3.isData()) {
                  throw new SaveSyntaxException("Recipe for '" + this.resultID + "' resultAmount must be a data component");
               } else {
                  try {
                     this.resultAmount = LoadData.getInt(var3);
                  } catch (NumberFormatException var6) {
                     throw new SaveSyntaxException("Recipe resultAmount must be a number");
                  }

                  if (!var2.hasNext()) {
                     throw new SaveSyntaxException("Missing recipe tech for '" + this.resultID + "'");
                  } else {
                     var3 = (LoadData)var2.next();
                     if (!var3.isData()) {
                        throw new SaveSyntaxException("Recipe for '" + this.resultID + "' tech must be a data component");
                     } else {
                        this.tech = LoadData.getUnsafeString(var3);
                        if (!var2.hasNext()) {
                           throw new SaveSyntaxException("Missing recipe ingredients for '" + this.resultID + "'");
                        } else {
                           var3 = (LoadData)var2.next();
                           if (!var3.isArray()) {
                              throw new SaveSyntaxException("Recipe for '" + this.resultID + "' ingredients must be an array component");
                           } else {
                              List var4 = var3.getLoadData();
                              this.ingredients = new IngredientData[var4.size()];

                              for(int var5 = 0; var5 < this.ingredients.length; ++var5) {
                                 this.ingredients[var5] = new IngredientData((LoadData)var4.get(var5), this);
                              }

                              var3 = null;
                              LoadData var7;
                              String var8;
                              if (var2.hasNext()) {
                                 var7 = (LoadData)var2.next();
                                 var3 = null;
                                 if (var7.isData()) {
                                    var8 = LoadData.getUnsafeString(var7);
                                    if (var8.equals("true")) {
                                       this.isHidden = true;
                                    } else if (var8.equals("false")) {
                                       this.isHidden = false;
                                    } else {
                                       this.isHidden = false;
                                       var3 = var7;
                                    }
                                 } else {
                                    this.isHidden = false;
                                    var3 = var7;
                                 }
                              } else {
                                 this.isHidden = false;
                              }

                              if (var3 == null && !var2.hasNext()) {
                                 this.sortResultID = null;
                              } else {
                                 var7 = var3 != null ? var3 : (LoadData)var2.next();
                                 var3 = null;
                                 if (var7.isData()) {
                                    var8 = LoadData.getUnsafeString(var7);
                                    if (var8.startsWith("before:")) {
                                       this.sortResultID = var8.substring("before:".length());
                                       this.sortBefore = true;
                                    } else if (var8.startsWith("after:")) {
                                       this.sortResultID = var8.substring("after:".length());
                                       this.sortBefore = false;
                                    } else {
                                       this.sortResultID = null;
                                       var3 = var7;
                                    }
                                 }
                              }

                              if (var3 == null && !var2.hasNext()) {
                                 this.gndData = new GNDItemMap();
                              } else {
                                 var7 = var3 != null ? var3 : (LoadData)var2.next();
                                 var3 = null;
                                 if (!var7.isArray()) {
                                    throw new SaveSyntaxException("Recipe for '" + this.resultID + "' gndData must be a array component");
                                 }

                                 this.gndData = getRecipeGNDData(this.resultID, var7);
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static GNDItemMap getRecipeGNDData(String var0, LoadData var1) throws SaveSyntaxException {
      GNDItemMap var2 = new GNDItemMap();
      Iterator var3 = var1.getLoadData().iterator();

      while(true) {
         while(true) {
            while(var3.hasNext()) {
               LoadData var4 = (LoadData)var3.next();
               String var5 = var4.getName();
               if (var4.isData()) {
                  String var6 = LoadData.getUnsafeString(var4);
                  if (var6.startsWith("\"")) {
                     var2.setItem(var5, new GNDItemString(SaveComponent.fromSafeData(var6)));
                  } else if (var6.contains(".")) {
                     var2.setItem(var5, new GNDItemDouble(Double.parseDouble(var6)));
                  } else {
                     try {
                        long var7 = LoadData.getLong(var4);
                        Object var9 = new GNDItemLong(var7);
                        if (var7 >= -128L && var7 <= 127L) {
                           var9 = new GNDItemByte((byte)((int)var7));
                        } else if (var7 >= -32768L && var7 <= 32767L) {
                           var9 = new GNDItemShort((short)((int)var7));
                        } else if (var7 >= -2147483648L && var7 <= 2147483647L) {
                           var9 = new GNDItemInt((int)var7);
                        }

                        var2.setItem(var5, (GNDItem)var9);
                     } catch (NumberFormatException var10) {
                        var2.setString(var5, var6);
                     }
                  }
               } else {
                  if (!var4.isArray()) {
                     throw new SaveSyntaxException("Recipe for " + var0 + " gnd had unknown data type");
                  }

                  var2.setItem(var5, getRecipeGNDData(var0, var4));
               }
            }

            return var2;
         }
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("", this.resultID);
      var1.addInt("", this.resultAmount);
      var1.addUnsafeString("", this.tech);
      SaveData var2 = new SaveData("");
      IngredientData[] var3 = this.ingredients;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IngredientData var6 = var3[var5];
         SaveData var7 = new SaveData("");
         var6.addSaveData(var7);
         var2.addSaveData(var7);
      }

      var1.addSaveData(var2);
      if (this.isHidden) {
         var1.addUnsafeString("", "true");
      }

      if (this.sortResultID != null) {
         if (this.sortBefore) {
            var1.addUnsafeString("", "before:" + this.sortResultID);
         } else {
            var1.addUnsafeString("", "after:" + this.sortResultID);
         }
      }

      if (this.gndData.getMapSize() != 0) {
         SaveData var8 = new SaveData("GND");
         Iterator var9 = this.gndData.getKeyStringSet().iterator();

         while(var9.hasNext()) {
            String var10 = (String)var9.next();
            GNDItem var11 = this.gndData.getItem(var10);
            if (var11 instanceof GNDItemString) {
               var8.addSafeString(var10, var11.toString());
            } else {
               var8.addUnsafeString(var10, var11.toString());
            }
         }

         var1.addSaveData(var8);
      }

   }

   public Recipe validate() {
      Ingredient[] var1 = new Ingredient[this.ingredients.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = this.ingredients[var2].validate();
      }

      try {
         Tech var5 = RecipeTechRegistry.getTech(this.tech);
         Recipe var3 = new Recipe(this.resultID, this.resultAmount, var5, var1, this.isHidden, this.gndData);
         if (this.sortResultID != null) {
            if (this.sortBefore) {
               var3.showBefore(this.sortResultID);
            } else {
               var3.showAfter(this.sortResultID);
            }
         }

         return var3;
      } catch (NoSuchElementException var4) {
         throw new SaveSyntaxException("Could not find tech with name " + this.tech);
      }
   }
}
