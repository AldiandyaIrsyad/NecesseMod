package necesse.level.maps.presets.modularPresets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class ModularPreset extends Preset {
   public final int sectionWidth;
   public final int sectionHeight;
   public final int sectionRes;
   public final int openingSize;
   public final int openingDepth;
   public int closeObject;
   public int closeTile;
   public int openObject;
   public int openTile;
   protected ArrayList<Opening> openings;
   public boolean overlap = false;

   public ModularPreset(int var1, int var2, int var3, int var4, int var5) {
      super(var1 * var3, var2 * var3);
      this.sectionWidth = var1;
      this.sectionHeight = var2;
      this.sectionRes = var3;
      this.openingSize = var4;
      this.openingDepth = var5;
      this.openings = new ArrayList();
      this.closeObject = -1;
      this.closeTile = -1;
      this.openObject = -1;
      this.openTile = -1;
   }

   protected void open(int var1, int var2, int var3) {
      this.openings.add(new Opening(var1, var2, var3));
   }

   protected ModularPreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return new ModularPreset(var1, var2, var3, var4, var5);
   }

   protected final ModularPreset newObject(int var1, int var2) {
      ModularPreset var3 = this.newModularObject(var1 / this.sectionRes, var2 / this.sectionRes, this.sectionRes, this.openingSize, this.openingDepth);
      var3.closeObject = this.closeObject;
      var3.closeTile = this.closeTile;
      var3.openObject = this.openObject;
      var3.openTile = this.openTile;
      var3.overlap = this.overlap;
      return var3;
   }

   public ModularPreset copy() {
      ModularPreset var1 = (ModularPreset)super.copy();
      var1.openings.addAll(this.openings);
      return var1;
   }

   public ModularPreset mirrorX() throws PresetMirrorException {
      ModularPreset var1 = (ModularPreset)super.mirrorX();

      Opening var3;
      int var4;
      int var5;
      for(Iterator var2 = this.openings.iterator(); var2.hasNext(); var1.openings.add(new Opening(var4, var3.y, var5))) {
         var3 = (Opening)var2.next();
         var4 = this.getMirroredX(var3.x);
         var5 = var3.dir;
         if (var5 == 1) {
            var5 = 3;
         } else if (var5 == 3) {
            var5 = 1;
         }
      }

      return var1;
   }

   public ModularPreset mirrorY() throws PresetMirrorException {
      ModularPreset var1 = (ModularPreset)super.mirrorY();

      Opening var3;
      int var4;
      int var5;
      for(Iterator var2 = this.openings.iterator(); var2.hasNext(); var1.openings.add(new Opening(var3.x, var4, var5))) {
         var3 = (Opening)var2.next();
         var4 = this.getMirroredY(var3.y);
         var5 = var3.dir;
         if (var5 == 0) {
            var5 = 2;
         } else if (var5 == 2) {
            var5 = 0;
         }
      }

      return var1;
   }

   public ModularPreset rotate(PresetRotation var1) throws PresetRotateException {
      ModularPreset var2 = (ModularPreset)super.rotate(var1);
      int var3 = var1 == null ? 0 : var1.dirOffset;
      Iterator var4 = this.openings.iterator();

      while(var4.hasNext()) {
         Opening var5 = (Opening)var4.next();
         Point var6 = PresetUtils.getRotatedPointInSpace(var5.x, var5.y, this.width, this.height, var1);
         var2.openings.add(new Opening(var6.x, var6.y, (var5.dir + var3) % 4));
      }

      return var2;
   }

   public void openLevel(Level var1, int var2, int var3, int var4, int var5, int var6, GameRandom var7, int var8) {
      this.openLevel(var1, var2, var3, var4, var5, var6, var8);
   }

   public void openLevel(Level var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fillOpening(var1, var2, var3, var4, var5, var6, this.openObject, this.openTile, var7);
   }

   public void closeLevel(Level var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fillOpening(var1, var2, var3, var4, var5, var6, this.closeObject, this.closeTile, var7);
   }

   public void fillOpening(Level var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      var6 = Math.abs(var6) % 4;
      int var10 = var9 - this.openingDepth + (this.overlap ? 1 : 0);
      int var11 = var9 / 2 - this.openingSize / 2;
      if (var6 == 0) {
         this.fillOpeningReal(var1, var4 + var2 * var9 + var11, var5 + var3 * var9, var6, var7, var8);
      } else if (var6 == 1) {
         this.fillOpeningReal(var1, var4 + var2 * var9 + var10, var5 + var3 * var9 + var11, var6, var7, var8);
      } else if (var6 == 2) {
         this.fillOpeningReal(var1, var4 + var2 * var9 + var11, var5 + var3 * var9 + var10, var6, var7, var8);
      } else if (var6 == 3) {
         this.fillOpeningReal(var1, var4 + var2 * var9, var5 + var3 * var9 + var11, var6, var7, var8);
      }

   }

   public void fillOpeningReal(Level var1, int var2, int var3, int var4, int var5, int var6) {
      var4 = Math.abs(var4) % 4;
      if (var4 == 0) {
         if (var5 != -1) {
            this.fillObject(var1, var2, var3, this.openingSize, this.openingDepth, var5);
         }

         if (var6 != -1) {
            this.fillTile(var1, var2, var3, this.openingSize, this.openingDepth, var6);
         }
      } else if (var4 == 1) {
         if (var5 != -1) {
            this.fillObject(var1, var2, var3, this.openingDepth, this.openingSize, var5);
         }

         if (var6 != -1) {
            this.fillTile(var1, var2, var3, this.openingDepth, this.openingSize, var6);
         }
      } else if (var4 == 2) {
         if (var5 != -1) {
            this.fillObject(var1, var2, var3, this.openingSize, this.openingDepth, var5);
         }

         if (var6 != -1) {
            this.fillTile(var1, var2, var3, this.openingSize, this.openingDepth, var6);
         }
      } else if (var4 == 3) {
         if (var5 != -1) {
            this.fillObject(var1, var2, var3, this.openingDepth, this.openingSize, var5);
         }

         if (var6 != -1) {
            this.fillTile(var1, var2, var3, this.openingDepth, this.openingSize, var6);
         }
      }

   }

   public void fillObject(Level var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      for(int var8 = var2; var8 < var4 + var2; ++var8) {
         for(int var9 = var3; var9 < var5 + var3; ++var9) {
            var1.setObject(var8, var9, var6);
            var1.setObjectRotation(var8, var9, (byte)var7);
         }
      }

   }

   public void fillObject(Level var1, int var2, int var3, int var4, int var5, int var6) {
      this.fillObject(var1, var2, var3, var4, var5, var6, 0);
   }

   public void fillTile(Level var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var2; var7 < var4 + var2; ++var7) {
         for(int var8 = var3; var8 < var5 + var3; ++var8) {
            var1.setTile(var7, var8, var6);
         }
      }

   }

   public boolean isOpen(int var1, int var2, int var3) {
      Iterator var4 = this.openings.iterator();

      Opening var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (Opening)var4.next();
      } while(var5.x != var1 || var5.y != var2 || var5.dir != var3);

      return true;
   }

   public boolean getSpecificOpenLeft(int var1) {
      Iterator var2 = this.openings.iterator();

      Opening var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Opening)var2.next();
      } while(var3.x != 0 || var3.y != var1 || var3.dir != 3);

      return true;
   }

   public int getOpenLeft(int var1) {
      for(int var2 = 0; var2 < this.openings.size(); ++var2) {
         int var3 = (var2 + Math.abs(var1)) % this.openings.size();
         Opening var4 = (Opening)this.openings.get(var3);
         if (var4.x == 0 && var4.dir == 3) {
            return var4.y;
         }
      }

      return -1;
   }

   public int getOpenLeft() {
      return this.getOpenLeft(0);
   }

   public boolean isOpenLeft() {
      return this.getOpenLeft() != -1;
   }

   public boolean getSpecificOpenRight(int var1) {
      Iterator var2 = this.openings.iterator();

      Opening var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Opening)var2.next();
      } while(var3.x != this.sectionWidth - 1 || var3.y != var1 || var3.dir != 1);

      return true;
   }

   public int getOpenRight(int var1) {
      for(int var2 = 0; var2 < this.openings.size(); ++var2) {
         int var3 = (var2 + Math.abs(var1)) % this.openings.size();
         Opening var4 = (Opening)this.openings.get(var3);
         if (var4.x == this.sectionWidth - 1 && var4.dir == 1) {
            return var4.y;
         }
      }

      return -1;
   }

   public int getOpenRight() {
      return this.getOpenRight(0);
   }

   public boolean isOpenRight() {
      return this.getOpenRight() != -1;
   }

   public boolean getSpecificOpenTop(int var1) {
      Iterator var2 = this.openings.iterator();

      Opening var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Opening)var2.next();
      } while(var3.x != var1 || var3.y != 0 || var3.dir != 0);

      return true;
   }

   public int getOpenTop(int var1) {
      for(int var2 = 0; var2 < this.openings.size(); ++var2) {
         int var3 = (var2 + Math.abs(var1)) % this.openings.size();
         Opening var4 = (Opening)this.openings.get(var3);
         if (var4.y == 0 && var4.dir == 0) {
            return var4.x;
         }
      }

      return -1;
   }

   public int getOpenTop() {
      return this.getOpenTop(0);
   }

   public boolean isOpenTop() {
      return this.getOpenTop() != -1;
   }

   public boolean getSpecificOpenBottom(int var1) {
      Iterator var2 = this.openings.iterator();

      Opening var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Opening)var2.next();
      } while(var3.x != var1 || var3.y != this.sectionHeight - 1 || var3.dir != 2);

      return true;
   }

   public int getOpenBottom(int var1) {
      for(int var2 = 0; var2 < this.openings.size(); ++var2) {
         int var3 = (var2 + Math.abs(var1)) % this.openings.size();
         Opening var4 = (Opening)this.openings.get(var3);
         if (var4.y == this.sectionHeight - 1 && var4.dir == 2) {
            return var4.x;
         }
      }

      return -1;
   }

   public int getOpenBottom() {
      return this.getOpenBottom(0);
   }

   public boolean isOpenBottom() {
      return this.getOpenBottom() != -1;
   }

   public int getRandomOpenDir(int var1, int var2) {
      var2 = Math.abs(var2) % 4;
      if (var2 == 0) {
         return this.getOpenTop(var1);
      } else if (var2 == 1) {
         return this.getOpenRight(var1);
      } else if (var2 == 2) {
         return this.getOpenBottom(var1);
      } else {
         return var2 == 3 ? this.getOpenLeft(var1) : -1;
      }
   }

   public int getOpenDir(int var1) {
      return this.getRandomOpenDir(0, var1);
   }

   public boolean isOpenDir(int var1) {
      return this.getOpenDir(var1) != -1;
   }

   public boolean canPlace(Level var1, int var2, int var3) {
      return true;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset rotate(PresetRotation var1) throws PresetRotateException {
      return this.rotate(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset mirrorY() throws PresetMirrorException {
      return this.mirrorY();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset mirrorX() throws PresetMirrorException {
      return this.mirrorX();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset copy() {
      return this.copy();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Preset newObject(int var1, int var2) {
      return this.newObject(var1, var2);
   }

   protected static class Opening extends Point {
      public int dir;

      public Opening(int var1, int var2, int var3) {
         super(var1, var2);
         this.dir = var3;
      }
   }
}
