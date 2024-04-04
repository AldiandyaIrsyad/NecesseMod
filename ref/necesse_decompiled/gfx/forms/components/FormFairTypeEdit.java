package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.GlyphContainer;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormCursorMoveEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;

public abstract class FormFairTypeEdit extends FormTypingComponent {
   private Caret caret;
   private FairType text;
   private FairTypeDrawOptions drawOptions;
   private FairTypeDrawOptions textBoxDrawOptions;
   private ArrayList<GlyphContainer> drawOptionsArrayList;
   private LinkedList<CaretPosition> possibleCaretPositions;
   public final FairType.TextAlign align;
   public final FontOptions fontOptions;
   public final Color textColor;
   private Pattern regexPattern;
   public final int maxLines;
   public final int maxWidth;
   public final int maxLength;
   public boolean allowTyping = true;
   public boolean allowCaretSelect = true;
   public boolean allowCaretSetTyping = true;
   public boolean allowCaretRemoveTyping = true;
   public boolean allowUsedMouseClickStopTyping = false;
   public boolean allowTextSelect = true;
   public boolean allowItemAppend = false;
   protected Rectangle textSelectEmptySpace = null;
   protected boolean isHovering;
   private CaretPosition currentMouseCaretPosition = null;
   private boolean currentlySelecting = false;
   private CaretPosition startSelection;
   private CaretPosition endSelection;
   private Point startSelectionMousePos;
   protected TypeParser[] parsers;
   protected InputEvent setTypingEvent;
   protected FormEventsHandler<FormCursorMoveEvent<FormFairTypeEdit>> caretMoveEvents = new FormEventsHandler();
   protected FormEventsHandler<FormInputEvent<FormFairTypeEdit>> onMouseChangedTyping = new FormEventsHandler();
   private static final Pattern nextNonWordChar = Pattern.compile("[^\\w\\d]");

   public FormFairTypeEdit(FontOptions var1, FairType.TextAlign var2, Color var3, int var4, int var5, int var6) {
      this.fontOptions = var1;
      this.align = var2;
      this.textColor = var3;
      this.maxWidth = var4;
      this.maxLines = var5;
      this.maxLength = var6;
      this.parsers = new TypeParser[0];
      this.setText("");
   }

   public FormFairTypeEdit onCaretMove(FormEventListener<FormCursorMoveEvent<FormFairTypeEdit>> var1) {
      this.caretMoveEvents.addListener(var1);
      return this;
   }

   public FormFairTypeEdit onMouseChangedTyping(FormEventListener<FormInputEvent<FormFairTypeEdit>> var1) {
      this.onMouseChangedTyping.addListener(var1);
      return this;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getID() != -100 || !this.currentlySelecting) {
         this.getDrawOptions().handleInputEvent(this.getTextX(), this.getTextY(), var1);
      }

      if (this.isTyping()) {
         this.submitTypingEvent(var1, true);
      }

      if (!var1.isUsed()) {
         if (var1.isMouseMoveEvent()) {
            if (!var1.isMoveUsed()) {
               this.isHovering = this.isMouseOver(var1);
               this.updateCurrentCaretPosition(var1);
               if (this.currentlySelecting && this.currentMouseCaretPosition != null) {
                  this.caret.typeIndex = this.currentMouseCaretPosition.index;
                  this.caret.updateProps(this.currentMouseCaretPosition, true, new FormCursorMoveEvent(this, true));
               }

               if (this.isHovering) {
                  var1.useMove();
               }
            } else {
               this.currentMouseCaretPosition = null;
            }
         } else if (this.allowCaretSelect && var1.getID() == -100) {
            this.updateCurrentCaretPosition(var1);
            if (this.currentMouseCaretPosition != null) {
               this.setTypingEvent = var1;
               if (!this.isTyping()) {
                  this.setTyping(true);
                  this.onMouseChangedTyping.onEvent(new FormInputEvent(this, var1));
               }

               if (this.allowTextSelect) {
                  if (var1.state) {
                     this.startSelection = this.currentMouseCaretPosition;
                     this.startSelectionMousePos = new Point(Screen.mousePos().hudX, Screen.mousePos().hudY);
                     this.currentlySelecting = true;
                     this.endSelection = null;
                  }

                  if (!var1.state) {
                     this.endSelection = this.currentMouseCaretPosition;
                     this.startSelectionMousePos = null;
                     this.currentlySelecting = false;
                  }
               } else {
                  this.startSelection = null;
                  this.endSelection = null;
               }

               this.caret.typeIndex = this.currentMouseCaretPosition.index;
               this.caret.updateProps(this.currentMouseCaretPosition, true, new FormCursorMoveEvent(this, true));
               var1.use();
            } else if (this.allowCaretRemoveTyping && (!this.allowItemAppend || !Screen.isKeyDown(340))) {
               this.startSelection = null;
               this.endSelection = null;
               this.currentlySelecting = false;
               if (this.isTyping()) {
                  this.setTyping(false);
                  this.onMouseChangedTyping.onEvent(new FormInputEvent(this, var1));
               }
            }
         } else if (this.allowTextSelect && this.isTyping() && var1.state && var1.getID() == 340 && this.startSelection == null) {
            this.startSelection = this.caret.getCurrentPosition();
            this.endSelection = this.caret.getCurrentPosition();
         } else if (this.isTyping() && var1.getID() == 256 && !var1.state) {
            this.setTyping(false);
            var1.use();
         }

      }
   }

   public boolean submitTypingEvent(InputEvent var1, boolean var2) {
      if (this.handleCaretInput(var1, var2)) {
         this.text.applyParsers((var0) -> {
            return true;
         }, (var1x) -> {
            if (this.caret.typeIndex >= var1x.start && this.caret.typeIndex < var1x.end) {
               this.caret.typeIndex = Math.min(this.caret.typeIndex, var1x.start + var1x.newGlyphs.length - 1);
            } else if (this.caret.typeIndex >= var1x.end) {
               Caret var10000 = this.caret;
               var10000.typeIndex += var1x.newGlyphs.length - var1x.oldGlyphs.length;
            }

            this.resetDrawOptions();
            this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
         }, this.parsers);
         this.submitChangeEvent();
         return true;
      } else {
         return false;
      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getState() == ControllerInput.MENU_SELECT) {
         if (this.isControllerFocus() && var1.buttonState) {
            this.setTyping(true);
            var1.use();
         }
      } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && this.isTyping() && var1.buttonState) {
         this.setTyping(false);
         var1.use();
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      super.onControllerUnfocused(var1);
      this.setTyping(false);
   }

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      return this.isTyping() ? true : super.handleControllerNavigate(var1, var2, var3, var4);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.textSelectEmptySpace != null) {
         Rectangle var7 = new Rectangle(this.textSelectEmptySpace);
         var7.x += this.getTextX();
         var7.y += this.getTextY();
         ControllerFocus.add(var1, var5, this, var7, var2 - var7.x, var3 - var7.y, this.controllerInitialFocusPriority, var4);
      } else {
         ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
      }

   }

   public void submitUsedInputEvent(InputEvent var1) {
      if (this.isTyping() && !InputEvent.isFromSameEvent(this.setTypingEvent, var1) && var1.wasMouseClickEvent() && this.allowUsedMouseClickStopTyping) {
         this.setTyping(false);
         this.onMouseChangedTyping.onEvent(new FormInputEvent(this, var1));
      }

   }

   private void updateCurrentCaretPosition(InputEvent var1) {
      CaretPosition var2 = null;
      if (this.allowCaretSelect) {
         if (!this.isTyping() && !this.allowCaretSetTyping) {
            return;
         }

         Rectangle var3 = this.getTextBoundingBox();
         Rectangle var4 = new Rectangle(var3);
         if (this.textSelectEmptySpace != null) {
            Rectangle var5 = new Rectangle(this.textSelectEmptySpace);
            var5.x += this.getTextX();
            var5.y += this.getTextY();
            var4 = var4.union(var5);
         }

         if (var4.contains(var1.pos.hudX, var1.pos.hudY) || this.currentlySelecting && this.startSelectionMousePos != null) {
            InputEvent var14 = InputEvent.OffsetHudEvent(Screen.input(), var1, -this.getTextX(), -this.getTextY());
            int var6 = Integer.MAX_VALUE;
            LinkedList var7 = this.getPossibleCaretPositions();
            int var8 = ((CaretPosition)var7.getFirst()).line;
            int var9 = ((CaretPosition)var7.getLast()).line;
            Iterator var10 = var7.iterator();

            label49:
            while(true) {
               CaretPosition var11;
               do {
                  do {
                     if (!var10.hasNext()) {
                        if (var2 == null) {
                           CaretPosition var13 = (CaretPosition)var7.getFirst();
                           if (var1.pos.hudY <= var13.y) {
                              var2 = var13;
                           } else {
                              var2 = (CaretPosition)var7.getLast();
                           }
                        }
                        break label49;
                     }

                     var11 = (CaretPosition)var10.next();
                  } while(var14.pos.hudY > var11.y && var11.line != var9);
               } while(var14.pos.hudY <= var11.y - var11.lineHeight && var11.line != var8);

               int var12 = Math.abs(var11.x - var14.pos.hudX);
               if (var12 <= var6) {
                  var2 = var11;
                  var6 = var12;
               }
            }
         }
      }

      this.currentMouseCaretPosition = var2;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.startSelection != null && (this.endSelection != null || this.currentMouseCaretPosition != null)) {
         CaretPosition var4 = this.endSelection == null ? this.currentMouseCaretPosition : this.endSelection;
         int var5 = Integer.MIN_VALUE;
         int var6 = 0;
         if (this.startSelection.index != var4.index) {
            int var7 = Math.min(this.startSelection.index, var4.index);
            int var8 = Math.max(this.startSelection.index, var4.index);
            ArrayList var9 = this.getDrawOptionsArrayList();

            for(int var10 = var7 + 1; var10 <= var8; ++var10) {
               GlyphContainer var11 = (GlyphContainer)var9.get(var10);
               if (var11.line > var6) {
                  var6 = var11.line;
                  var5 = Integer.MIN_VALUE;
               }

               Dimension var12 = var11.glyph.getDimensions().toInt();
               int var13 = this.getTextX() + (int)var11.x;
               int var14 = var12.width;
               int var15 = Math.max(var13, var5);
               if (var13 < var15) {
                  var14 -= var15 - var13;
               }

               Screen.initQuadDraw(var14, (int)var11.lineHeight).color(0.0F, 0.0F, 1.0F, 0.5F).draw(var15, this.getTextY() + (int)(var11.y - var11.lineHeight));
               var5 = var13 + var14;
            }
         }
      }

      if (this.currentMouseCaretPosition != null) {
         Screen.setCursor(Screen.CURSOR.CARET);
      }

      this.getDrawOptions().draw(this.getTextX(), this.getTextY(), this.textColor);
      this.caret.draw();
   }

   public void drawControllerFocus(ControllerFocus var1) {
      if (this.isTyping()) {
         Rectangle var2 = var1.boundingBox;
         byte var3 = 5;
         var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var2).draw();
      } else {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

   }

   public FairTypeDrawOptions getDrawOptions() {
      if (this.drawOptions == null) {
         this.drawOptions = this.text.getDrawOptions(this.align, this.maxWidth, true, false);
      }

      return this.drawOptions;
   }

   public FairTypeDrawOptions getTextBoxDrawOptions() {
      if (this.textBoxDrawOptions == null) {
         this.textBoxDrawOptions = this.text.getTextBoxCopy().getDrawOptions(FairType.TextAlign.LEFT, 400, true, false);
      }

      return this.textBoxDrawOptions;
   }

   public void resetDrawOptions() {
      this.drawOptions = null;
      this.textBoxDrawOptions = null;
      this.drawOptionsArrayList = null;
      this.possibleCaretPositions = null;
      this.clearSelection();
   }

   public int getTextLength() {
      return this.text.getLength();
   }

   public void clearSelection() {
      this.startSelectionMousePos = null;
      this.startSelection = null;
      this.endSelection = null;
      this.currentlySelecting = false;
   }

   public void selectAll() {
      LinkedList var1 = this.getPossibleCaretPositions();
      this.startSelection = (CaretPosition)var1.getFirst();
      this.endSelection = (CaretPosition)var1.getLast();
      this.currentMouseCaretPosition = (CaretPosition)var1.getLast();
   }

   protected ArrayList<GlyphContainer> getDrawOptionsArrayList() {
      if (this.drawOptionsArrayList == null) {
         this.drawOptionsArrayList = new ArrayList(this.getDrawOptions().getDrawList());
      }

      return this.drawOptionsArrayList;
   }

   protected LinkedList<CaretPosition> getPossibleCaretPositions() {
      if (this.possibleCaretPositions == null) {
         LinkedList var1 = this.getDrawOptions().getDrawList();
         LinkedList var2 = new LinkedList();
         if (var1.isEmpty()) {
            var2.add(new CaretPosition((GlyphContainer)null, -1, 0, this.fontOptions.getSize(), 0, this.fontOptions.getSize()));
         }

         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            GlyphContainer var4 = (GlyphContainer)var3.next();
            var2.add(new CaretPosition(var4, var4.index - 1, (int)var4.x, (int)var4.y));
            Dimension var5 = var4.glyph.getDimensions().toInt();
            var2.add(new CaretPosition(var4, var4.index, GameMath.ceil((double)(var4.x + (float)var5.width)), GameMath.ceil((double)var4.y)));
         }

         this.possibleCaretPositions = var2;
      }

      return this.possibleCaretPositions;
   }

   protected LinkedList<CaretPosition> getPossibleCaretPositions(int var1) {
      LinkedList var2 = new LinkedList();
      Iterator var3 = this.getPossibleCaretPositions().iterator();

      while(var3.hasNext()) {
         CaretPosition var4 = (CaretPosition)var3.next();
         if (var4.line == var1) {
            var2.add(var4);
         }
      }

      return var2;
   }

   protected Rectangle getTextBoundingBox() {
      return this.text.getLength() == 0 ? new Rectangle(this.getTextX(), this.getTextY(), 2, this.fontOptions.getSize()) : this.getDrawOptions().getBoundingBox(this.getTextX(), this.getTextY());
   }

   public void setText(String var1, boolean var2) {
      FairType var3 = new FairType();
      char[] var4 = var1.toCharArray();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var4[var6];
         var3.append(this.getCharacterGlyph(var7));
      }

      this.setFairType(var3);
      if (var2) {
         this.submitChangeEvent();
      }

   }

   public final void setText(String var1) {
      this.setText(var1, true);
   }

   public String getText() {
      return this.text.getParseString();
   }

   public String getTextCharString() {
      return this.text.getCharString();
   }

   protected final FairType getFairType() {
      return this.text;
   }

   public void setParsers(TypeParser... var1) {
      this.parsers = var1;
   }

   protected final void setFairType(FairType var1) {
      this.text = var1;
      this.text.applyParsers(this.parsers);
      if (this.maxLength > 0) {
         while(this.text.getLength() > this.maxLength) {
            this.text.remove(this.text.getLength() - 1);
         }
      }

      this.caret = new Caret();
      this.caret.typeIndex = this.text.getLength() - 1;
      this.resetDrawOptions();
      this.caret.updateProps((CaretPosition)null, true, (FormCursorMoveEvent)null);
   }

   public boolean appendAtCaret(String var1) {
      if (var1 == null) {
         return this.appendAtCaret((FairGlyph[])null);
      } else {
         FairGlyph[] var2 = new FairGlyph[var1.length()];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = this.getCharacterGlyph(var1.charAt(var3));
         }

         return this.appendAtCaret(var2);
      }
   }

   public boolean appendAtCaret(FairGlyph... var1) {
      boolean var2 = false;
      if (this.deleteSelection()) {
         this.resetDrawOptions();
         this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
         var2 = true;
      }

      if (var1 != null) {
         FairGlyph[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            FairGlyph var6 = var3[var5];
            if (!this.acceptInput(this.caret.typeIndex + 1, var6)) {
               break;
            }

            ++this.caret.typeIndex;
            var2 = true;
         }

         this.resetDrawOptions();
         this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
      }

      return var2;
   }

   protected Caret getCaret() {
      return this.caret;
   }

   protected abstract int getTextX();

   protected abstract int getTextY();

   public boolean appendItem(InventoryItem var1) {
      if (this.appendAtCaret(new FairItemGlyph(this.fontOptions.getSize(), var1))) {
         this.submitChangeEvent();
         return true;
      } else {
         return false;
      }
   }

   public boolean isValidAppendText(String var1) {
      return this.validInput(var1);
   }

   public boolean appendText(String var1) {
      if (this.validInput(var1)) {
         if (this.appendAtCaret(var1)) {
            this.submitChangeEvent();
            return true;
         } else {
            return false;
         }
      } else {
         return this.appendAtCaret((String)null);
      }
   }

   public boolean submitBackspace() {
      this.handleBackspace();
      this.submitChangeEvent();
      return true;
   }

   public final int getCaretIndex() {
      return this.caret.typeIndex;
   }

   public Point getCaretPos() {
      return new Point(this.caret.x, this.caret.y);
   }

   public Rectangle getCaretBoundingBox() {
      Point var1 = this.getCaretPos();
      return new Rectangle(this.getTextX() + var1.x, this.getTextY() + var1.y - this.caret.height, 2, this.caret.height);
   }

   protected boolean acceptInput(int var1, FairGlyph var2) {
      if (this.maxLength > 0 && this.text.getLength() >= this.maxLength) {
         return false;
      } else if (this.maxLines > 0) {
         FairType var3 = new FairType(this.text);
         this.text.insert(var1, var2);
         this.resetDrawOptions();
         if (this.getDrawOptions().getLineCount() > this.maxLines) {
            this.text = var3;
            return false;
         } else {
            return true;
         }
      } else {
         this.text.insert(var1, var2);
         return true;
      }
   }

   public void setRegexMatchFull(String var1) {
      this.setRegexMatchFull(Pattern.compile(var1));
   }

   public void setRegexMatchFull(Pattern var1) {
      this.regexPattern = var1;
   }

   protected boolean validInput(String var1) {
      return this.regexPattern == null ? true : this.regexPattern.matcher(var1).matches();
   }

   protected FairGlyph getCharacterGlyph(char var1) {
      return new FairCharacterGlyph(this.fontOptions, var1);
   }

   protected boolean deleteSelection() {
      if (this.startSelection != null && this.endSelection != null && this.startSelection.index != this.endSelection.index) {
         int var1 = Math.min(this.startSelection.index + 1, this.endSelection.index + 1);
         int var2 = Math.max(this.startSelection.index, this.endSelection.index);

         for(int var3 = var1; var3 <= var2; ++var3) {
            this.text.remove(var1);
         }

         this.caret.typeIndex = var1 - 1;
         this.startSelection = null;
         this.endSelection = null;
         this.startSelectionMousePos = null;
         this.currentlySelecting = false;
         return true;
      } else {
         return false;
      }
   }

   protected boolean handleCaretInput(InputEvent var1, boolean var2) {
      FormInputEvent var3 = new FormInputEvent(this, var1);
      this.inputEvents.onEvent(var3);
      if (var3.hasPreventedDefault()) {
         return false;
      } else {
         String var4;
         if (this.allowTyping && var1.isCharacterEvent() && !var1.getChar().equals("")) {
            var1.use();
            var4 = var1.getChar();
            return !this.validInput(var4) ? this.appendAtCaret((String)null) : this.appendAtCaret(var4);
         } else {
            if (var1.state && var1.isKeyboardEvent()) {
               if (this.allowTyping && (var1.getID() == 257 || var1.getID() == 335)) {
                  var1.use();
                  if (!this.validInput("\n")) {
                     return this.appendAtCaret((String)null);
                  }

                  return this.appendAtCaret("\n");
               }

               int var11;
               if (var2 && var1.getID() == 263) {
                  var1.use();
                  if (this.caret.typeIndex >= 0) {
                     if (Screen.isKeyDown(getSystemShiftWordKey())) {
                        for(var11 = this.caret.typeIndex - 1; var11 >= -1; --var11) {
                           this.caret.typeIndex = var11;
                           if (var11 >= 0 && nextNonWordChar.matcher(Character.toString(this.text.get(var11).getCharacter())).matches()) {
                              break;
                           }
                        }
                     } else {
                        --this.caret.typeIndex;
                     }

                     this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
                     if (!Screen.isKeyDown(340)) {
                        this.clearSelection();
                     } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                     }
                  } else if (!Screen.isKeyDown(340)) {
                     this.clearSelection();
                  }

                  return false;
               }

               if (var2 && var1.getID() == 262) {
                  var1.use();
                  if (this.caret.typeIndex < this.text.getLength() - 1) {
                     if (Screen.isKeyDown(getSystemShiftWordKey())) {
                        for(var11 = this.caret.typeIndex + 1; var11 < this.text.getLength(); ++var11) {
                           this.caret.typeIndex = var11;
                           if (nextNonWordChar.matcher(Character.toString(this.text.get(var11).getCharacter())).matches()) {
                              break;
                           }
                        }
                     } else {
                        ++this.caret.typeIndex;
                     }

                     this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
                     if (!Screen.isKeyDown(340)) {
                        this.clearSelection();
                     } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                     }
                  } else if (!Screen.isKeyDown(340)) {
                     this.clearSelection();
                  }

                  return false;
               }

               int var6;
               CaretPosition var8;
               int var9;
               CaretPosition var13;
               Iterator var16;
               if (var2 && var1.getID() == 265) {
                  var1.use();
                  var11 = this.caret.prevLine;
                  if (var11 > 0) {
                     var13 = null;
                     var6 = Integer.MAX_VALUE;
                     var16 = this.getPossibleCaretPositions(var11 - 1).iterator();

                     while(true) {
                        do {
                           if (!var16.hasNext()) {
                              if (var13 != null) {
                                 this.caret.typeIndex = var13.index;
                                 this.caret.updateProps(var13, false, new FormCursorMoveEvent(this, false));
                                 if (!Screen.isKeyDown(340)) {
                                    this.clearSelection();
                                 } else if (this.startSelection != null) {
                                    this.endSelection = this.caret.getCurrentPosition();
                                    return false;
                                 }

                                 return false;
                              }

                              return false;
                           }

                           var8 = (CaretPosition)var16.next();
                           var9 = Math.abs(var8.x - this.caret.prevX);
                        } while(var13 != null && var9 > var6);

                        var13 = var8;
                        var6 = var9;
                     }
                  } else if (!Screen.isKeyDown(340)) {
                     this.clearSelection();
                  }

                  return false;
               }

               if (var2 && var1.getID() == 264) {
                  var1.use();
                  var11 = this.caret.prevLine;
                  var13 = null;
                  var6 = Integer.MAX_VALUE;
                  var16 = this.getPossibleCaretPositions(var11 + 1).iterator();

                  while(true) {
                     do {
                        if (!var16.hasNext()) {
                           if (var13 != null) {
                              this.caret.typeIndex = var13.index;
                              this.caret.updateProps(var13, false, new FormCursorMoveEvent(this, false));
                              if (!Screen.isKeyDown(340)) {
                                 this.clearSelection();
                              } else if (this.startSelection != null) {
                                 this.endSelection = this.caret.getCurrentPosition();
                              }
                           } else if (!Screen.isKeyDown(340)) {
                              this.clearSelection();
                           }

                           return false;
                        }

                        var8 = (CaretPosition)var16.next();
                        var9 = Math.abs(var8.x - this.caret.prevX);
                     } while(var13 != null && var9 > var6);

                     var13 = var8;
                     var6 = var9;
                  }
               }

               Iterator var14;
               CaretPosition var15;
               if (var2 && var1.getID() == 269) {
                  var1.use();
                  var11 = this.caret.prevLine;
                  var13 = null;
                  var14 = this.getPossibleCaretPositions(var11).iterator();

                  while(true) {
                     do {
                        if (!var14.hasNext()) {
                           if (var13 != null) {
                              this.caret.typeIndex = var13.index;
                              this.caret.updateProps(var13, true, new FormCursorMoveEvent(this, false));
                              if (!Screen.isKeyDown(340)) {
                                 this.clearSelection();
                              } else if (this.startSelection != null) {
                                 this.endSelection = this.caret.getCurrentPosition();
                              }
                           } else if (!Screen.isKeyDown(340)) {
                              this.clearSelection();
                           }

                           return false;
                        }

                        var15 = (CaretPosition)var14.next();
                     } while(var13 != null && var13.x > var15.x);

                     var13 = var15;
                  }
               }

               if (var2 && var1.getID() == 268) {
                  var1.use();
                  var11 = this.caret.prevLine;
                  var13 = null;
                  var14 = this.getPossibleCaretPositions(var11).iterator();

                  while(true) {
                     do {
                        if (!var14.hasNext()) {
                           if (var13 != null) {
                              this.caret.typeIndex = var13.index;
                              this.caret.updateProps(var13, true, new FormCursorMoveEvent(this, false));
                              if (!Screen.isKeyDown(340)) {
                                 this.clearSelection();
                              } else if (this.startSelection != null) {
                                 this.endSelection = this.caret.getCurrentPosition();
                              }
                           } else if (!Screen.isKeyDown(340)) {
                              this.clearSelection();
                           }

                           return false;
                        }

                        var15 = (CaretPosition)var14.next();
                     } while(var13 != null && var13.x < var15.x);

                     var13 = var15;
                  }
               }

               if (Screen.isKeyDown(getSystemPasteKey()) && var1.getID() == 67) {
                  var1.use();
                  if (this.startSelection != null && this.endSelection != null && this.startSelection.index != this.endSelection.index) {
                     StringBuilder var10 = new StringBuilder();
                     int var12 = Math.min(this.startSelection.index + 1, this.endSelection.index + 1);
                     var6 = Math.max(this.startSelection.index, this.endSelection.index);

                     for(int var7 = var12; var7 <= var6; ++var7) {
                        var10.append(this.text.get(var7).getParseString());
                     }

                     Screen.putClipboard(var10.toString());
                  }

                  return false;
               }

               if (this.allowTyping && Screen.isKeyDown(getSystemPasteKey()) && var1.getID() == 86) {
                  var1.use();
                  var4 = Screen.getClipboard();
                  if (var4 == null) {
                     return false;
                  }

                  var4 = var4.replace("\r", "");
                  if (!this.validInput(var4)) {
                     return this.appendAtCaret((String)null);
                  }

                  FairType var5 = (new FairType()).append(this.fontOptions, var4).applyParsers(this.parsers);
                  return this.appendAtCaret(var5.getGlyphsArray());
               }

               if (this.allowTyping && var1.getID() == 259) {
                  var1.use();
                  this.handleBackspace();
                  return true;
               }

               if (this.allowTyping && var1.getID() == 261) {
                  var1.use();
                  this.handleDelete();
                  return true;
               }

               if (var2 && Screen.isKeyDown(341) && var1.getID() == 65) {
                  var1.use();
                  this.selectAll();
                  return false;
               }
            }

            return false;
         }
      }
   }

   public void handleBackspace() {
      if (this.deleteSelection()) {
         this.resetDrawOptions();
         this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
      } else if (this.caret.typeIndex >= 0) {
         if (Screen.isKeyDown(getSystemShiftWordKey())) {
            for(int var1 = this.caret.typeIndex - 1; var1 >= -1; --var1) {
               this.text.remove(this.caret.typeIndex--);
               if (this.caret.typeIndex < 0 || nextNonWordChar.matcher(Character.toString(this.text.get(this.caret.typeIndex).getCharacter())).matches()) {
                  break;
               }
            }
         } else {
            this.text.remove(this.caret.typeIndex--);
         }

         this.resetDrawOptions();
         this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
      }

   }

   public void handleDelete() {
      if (this.deleteSelection()) {
         this.resetDrawOptions();
         this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
      } else if (this.caret.typeIndex < this.text.getLength() - 1) {
         if (Screen.isKeyDown(getSystemPasteKey())) {
            int var1 = this.text.getLength() - 1;

            for(int var2 = this.caret.typeIndex; var2 < var1; ++var2) {
               this.text.remove(this.caret.typeIndex + 1);
               if (this.caret.typeIndex + 1 >= this.text.getLength() || nextNonWordChar.matcher(Character.toString(this.text.get(this.caret.typeIndex + 1).getCharacter())).matches()) {
                  break;
               }
            }
         } else {
            this.text.remove(this.caret.typeIndex + 1);
         }

         this.resetDrawOptions();
         this.caret.updateProps((CaretPosition)null, true, new FormCursorMoveEvent(this, false));
      }

   }

   protected class CaretPosition {
      public final GlyphContainer container;
      public final int index;
      public final int x;
      public final int y;
      public final int line;
      public final int lineHeight;

      public CaretPosition(GlyphContainer var2, int var3, int var4, int var5, int var6, int var7) {
         this.container = var2;
         this.index = var3;
         this.x = var2 == null ? var4 : (int)var2.x;
         this.y = var2 == null ? var5 : (int)var2.y;
         this.line = var2 == null ? var6 : var2.line;
         this.lineHeight = var2 == null ? var7 : (int)var2.lineHeight;
      }

      public CaretPosition(GlyphContainer var2, int var3, int var4, int var5) {
         this.container = var2;
         this.index = var3;
         this.x = var4;
         this.y = var5;
         this.line = var2.line;
         this.lineHeight = (int)var2.lineHeight;
      }
   }

   protected class Caret {
      public int typeIndex = 0;
      public int x;
      public int prevX;
      public int y;
      public CaretPosition prevPosition;
      public int prevLine = -1;
      public int height;
      public Supplier<Color> colorSupplier;

      protected Caret() {
      }

      public void updateProps(CaretPosition var1, boolean var2, FormCursorMoveEvent<FormFairTypeEdit> var3) {
         int var4 = this.x;
         int var5 = this.y;
         this.typeIndex = Math.min(Math.max(this.typeIndex, -1), FormFairTypeEdit.this.text.getLength() - 1);
         if (var1 == null) {
            LinkedList var6 = FormFairTypeEdit.this.getDrawOptions().getDrawList();
            GlyphContainer var7;
            if (this.typeIndex == -1) {
               var7 = var6.isEmpty() ? null : (GlyphContainer)var6.get(0);
               var1 = FormFairTypeEdit.this.new CaretPosition(var7, this.typeIndex, 0, FormFairTypeEdit.this.fontOptions.getSize(), 0, FormFairTypeEdit.this.fontOptions.getSize());
            } else {
               var7 = (GlyphContainer)var6.get(this.typeIndex);
               Dimension var8 = var7.glyph.getDimensions().toInt();
               var1 = FormFairTypeEdit.this.new CaretPosition(var7, this.typeIndex, GameMath.ceil((double)(var7.x + (float)var8.width)), GameMath.ceil((double)var7.y));
            }
         }

         this.prevPosition = var1;
         if (var1.container != null) {
            this.colorSupplier = var1.container.currentColor;
            if (this.colorSupplier == null) {
               this.colorSupplier = () -> {
                  return FormFairTypeEdit.this.textColor;
               };
            }

            this.height = GameMath.ceil((double)var1.container.glyph.getDimensions().height);
            if (this.height <= 0) {
               this.height = FormFairTypeEdit.this.fontOptions.getSize();
            }
         } else {
            this.colorSupplier = () -> {
               return FormFairTypeEdit.this.textColor;
            };
            this.height = FormFairTypeEdit.this.fontOptions.getSize();
         }

         this.x = var1.x;
         this.y = var1.y;
         this.prevLine = var1.line;
         if (var2) {
            this.prevX = this.x;
         }

         if (var3 != null && (this.x != var4 || this.y != var5)) {
            FormFairTypeEdit.this.caretMoveEvents.onEvent(var3);
         }

      }

      public CaretPosition getCurrentPosition() {
         GlyphContainer var1;
         if (this.typeIndex < 0) {
            var1 = null;
         } else {
            var1 = (GlyphContainer)FormFairTypeEdit.this.getDrawOptions().getDrawList().get(this.typeIndex);
         }

         return FormFairTypeEdit.this.new CaretPosition(var1, this.typeIndex, this.x, this.y, this.prevLine, this.height);
      }

      public void draw() {
         if (FormFairTypeEdit.this.isTyping() && FormFairTypeEdit.this.allowTyping) {
            boolean var1 = System.currentTimeMillis() / 500L % 2L == 0L;
            if (var1) {
               Screen.initQuadDraw(2, this.height).color((Color)this.colorSupplier.get()).draw(FormFairTypeEdit.this.getTextX() + this.x, FormFairTypeEdit.this.getTextY() + this.y - this.height);
            }

         }
      }
   }
}
