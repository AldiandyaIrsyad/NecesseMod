package necesse.engine.util;

import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class LevelIdentifier {
   public static final Pattern levelIdentifierPattern = Pattern.compile("[a-z0-9-+_]{1,50}");
   public static final Pattern islandStringPattern = Pattern.compile("(-?(?:\\d)+)x(-?(?:\\d)+)d(-?(?:\\d)+)");
   public final String stringID;
   private boolean calculatedIsland;
   private boolean isIslandPosition;
   private int islandX;
   private int islandY;
   private int dimension;

   public static String getIslandIdentifier(int var0, int var1, int var2) {
      return var0 + "x" + var1 + "d" + var2;
   }

   public LevelIdentifier(String var1) {
      if (var1 != null && levelIdentifierPattern.matcher(var1).matches()) {
         this.stringID = var1;
      } else {
         throw new InvalidLevelIdentifierException("Invalid level identifier string \"" + var1 + "\". Must match regex: " + levelIdentifierPattern.pattern());
      }
   }

   public LevelIdentifier(int var1, int var2, int var3) {
      this.islandX = var1;
      this.islandY = var2;
      this.dimension = var3;
      this.stringID = getIslandIdentifier(var1, var2, var3);
      this.calculatedIsland = true;
      this.isIslandPosition = true;
   }

   public LevelIdentifier(Point var1, int var2) {
      this(var1.x, var1.y, var2);
   }

   public LevelIdentifier(PacketReader var1) {
      if (var1.getNextBoolean()) {
         this.islandX = var1.getNextInt();
         this.islandY = var1.getNextInt();
         this.dimension = var1.getNextInt();
         this.stringID = getIslandIdentifier(this.islandX, this.islandY, this.dimension);
         this.calculatedIsland = true;
         this.isIslandPosition = true;
      } else {
         this.stringID = var1.getNextString();
         this.calculatedIsland = true;
         this.isIslandPosition = false;
      }

   }

   public void writePacket(PacketWriter var1) {
      if (this.isIslandPosition()) {
         var1.putNextBoolean(true);
         var1.putNextInt(this.islandX);
         var1.putNextInt(this.islandY);
         var1.putNextInt(this.dimension);
      } else {
         var1.putNextBoolean(false);
         var1.putNextString(this.stringID);
      }

   }

   private void calculateIsland() {
      if (!this.calculatedIsland) {
         this.calculatedIsland = true;
         Matcher var1 = islandStringPattern.matcher(this.stringID);
         if (var1.matches()) {
            this.isIslandPosition = true;
            this.islandX = Integer.parseInt(var1.group(1));
            this.islandY = Integer.parseInt(var1.group(2));
            this.dimension = Integer.parseInt(var1.group(3));
         } else {
            this.isIslandPosition = false;
         }

      }
   }

   public boolean isIslandPosition() {
      this.calculateIsland();
      return this.isIslandPosition;
   }

   public int getIslandX() {
      this.calculateIsland();
      return this.islandX;
   }

   public int getIslandY() {
      this.calculateIsland();
      return this.islandY;
   }

   public int getIslandDimension() {
      this.calculateIsland();
      return this.dimension;
   }

   public boolean equals(String var1) {
      return this.stringID.equals(var1);
   }

   public boolean equals(LevelIdentifier var1) {
      if (var1 == this) {
         return true;
      } else if (this.isIslandPosition() && var1.isIslandPosition()) {
         return this.islandX == var1.islandX && this.islandY == var1.islandY && this.dimension == var1.dimension;
      } else {
         return this.stringID.equals(var1.stringID);
      }
   }

   public boolean equals(int var1, int var2, int var3) {
      if (!this.isIslandPosition()) {
         return false;
      } else {
         return this.islandX == var1 && this.islandY == var2 && this.dimension == var3;
      }
   }

   public boolean equals(Point var1, int var2) {
      return this.equals(var1.x, var1.y, var2);
   }

   public boolean isSameIsland(LevelIdentifier var1) {
      return !var1.isIslandPosition() ? false : this.isSameIsland(var1.getIslandX(), var1.getIslandY());
   }

   public boolean isSameIsland(int var1, int var2) {
      if (!this.isIslandPosition()) {
         return false;
      } else {
         return this.islandX == var1 && this.islandY == var2;
      }
   }

   public boolean isSameIsland(Point var1) {
      return this.isSameIsland(var1.x, var1.y);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof LevelIdentifier) {
         return this.equals((LevelIdentifier)var1);
      } else {
         return var1 instanceof String ? this.equals((String)var1) : false;
      }
   }

   public int hashCode() {
      return this.stringID.hashCode();
   }

   public String toString() {
      return this.stringID;
   }
}
