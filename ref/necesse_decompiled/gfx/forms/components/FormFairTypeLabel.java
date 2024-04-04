package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;

public class FormFairTypeLabel extends FormComponent implements FormPositionContainer {
   protected FormPosition position;
   protected boolean isHovering;
   public boolean useHoverMoveEvents;
   protected GameMessage text;
   protected FontOptions fontOptions;
   protected FairType.TextAlign textAlign;
   protected FairType customFairType;
   public final FairTypeDrawOptionsContainer drawOptions;
   protected int maxWidth;
   protected int maxLines;
   protected boolean cutLastLineWord;
   protected boolean addEllipsis;
   protected Supplier<Color> color;
   protected TypeParser[] parsers;
   protected FormEventsHandler<FormEvent<FormFairTypeLabel>> updateEvents;

   public FormFairTypeLabel(GameMessage var1, FontOptions var2, FairType.TextAlign var3, int var4, int var5) {
      this.useHoverMoveEvents = true;
      this.maxWidth = -1;
      this.maxLines = -1;
      this.color = () -> {
         return Settings.UI.activeTextColor;
      };
      this.parsers = new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL};
      this.updateEvents = new FormEventsHandler();
      this.fontOptions = var2;
      this.textAlign = var3;
      this.drawOptions = new FairTypeDrawOptionsContainer(() -> {
         FairType var1 = this.customFairType != null ? this.customFairType : (new FairType()).append(this.fontOptions, this.getText().translate());
         if (this.getParsers() != null) {
            var1.applyParsers(this.getParsers());
         }

         return var1.getDrawOptions(this.getTextAlign(), this.getMaxWidth(), true, this.getMaxLines(), this.cutLastLineWord, this.addEllipsis ? this.fontOptions : null, true);
      });
      this.drawOptions.onUpdate(() -> {
         this.updateEvents.onEvent(new FormEvent(this));
      });
      this.setPosition(new FormFixedPosition(var4, var5));
      this.setText(var1);
   }

   public FormFairTypeLabel(GameMessage var1, int var2, int var3) {
      this(var1, new FontOptions(16), FairType.TextAlign.LEFT, var2, var3);
   }

   public FormFairTypeLabel(String var1, int var2, int var3) {
      this((GameMessage)(new StaticMessage(var1)), var2, var3);
   }

   public FormFairTypeLabel onUpdated(FormEventListener<FormEvent<FormFairTypeLabel>> var1) {
      this.updateEvents.addListener(var1);
      return this;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      FairTypeDrawOptions var4 = this.drawOptions.get();
      boolean var5 = false;
      if (var1.isMouseMoveEvent()) {
         var5 = this.isMouseOver(var1);
         if (this.isHovering != var5) {
            this.isHovering = var5;
         }
      }

      if (var4 != null) {
         var4.handleInputEvent(this.getX(), this.getY(), var1);
      }

      if (var5 && this.useHoverMoveEvents) {
         var1.useMove();
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.getText().hasUpdated()) {
         this.drawOptions.reset();
      }

      FairTypeDrawOptions var4 = this.drawOptions.get();
      if (var4 != null) {
         var4.draw(this.getX(), this.getY(), (Color)this.getColor().get());
      }

   }

   public boolean isHovering() {
      return this.isHovering;
   }

   public boolean displaysFullText() {
      FairTypeDrawOptions var1 = this.drawOptions.get();
      if (var1 != null) {
         var1.displaysEverything();
      }

      return true;
   }

   public void setText(GameMessage var1, TypeParser... var2) {
      this.text = var1;
      this.parsers = var2;
   }

   public List<Rectangle> getHitboxes() {
      FairTypeDrawOptions var1 = this.drawOptions.get();
      return var1 != null ? singleBox(var1.getBoundingBox(this.getX(), this.getY())) : singleBox(new Rectangle());
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public FormFairTypeLabel setCustomFairType(FairType var1) {
      this.customFairType = var1;
      this.drawOptions.reset();
      return this;
   }

   public GameMessage getText() {
      return this.text;
   }

   public FormFairTypeLabel setText(GameMessage var1) {
      if (var1 == null) {
         var1 = new StaticMessage("");
      }

      this.text = (GameMessage)var1;
      this.customFairType = null;
      this.drawOptions.reset();
      return this;
   }

   public FormFairTypeLabel setText(String var1) {
      return this.setText((GameMessage)(new StaticMessage(var1)));
   }

   public FontOptions getFontOptions() {
      return this.fontOptions;
   }

   public FormFairTypeLabel setFontOptions(FontOptions var1) {
      this.fontOptions = var1;
      this.drawOptions.reset();
      return this;
   }

   public FairType.TextAlign getTextAlign() {
      return this.textAlign;
   }

   public FormFairTypeLabel setTextAlign(FairType.TextAlign var1) {
      this.textAlign = var1;
      this.drawOptions.reset();
      return this;
   }

   public int getMaxWidth() {
      return this.maxWidth;
   }

   public FormFairTypeLabel setMaxWidth(int var1) {
      this.maxWidth = var1;
      this.drawOptions.reset();
      return this;
   }

   public int getMaxLines() {
      return this.maxLines;
   }

   public FormFairTypeLabel setMaxLines(int var1, boolean var2, boolean var3) {
      this.maxLines = var1;
      this.cutLastLineWord = var2;
      this.addEllipsis = var3;
      this.drawOptions.reset();
      return this;
   }

   public FormFairTypeLabel setMaxLines(int var1, boolean var2) {
      return this.setMaxLines(var1, var2, var2);
   }

   public FormFairTypeLabel setMax(int var1, int var2, boolean var3, boolean var4) {
      this.setMaxWidth(var1);
      return this.setMaxLines(var2, var3, var4);
   }

   public FormFairTypeLabel setMax(int var1, int var2, boolean var3) {
      return this.setMax(var1, var2, var3, var3);
   }

   public Supplier<Color> getColor() {
      return this.color;
   }

   public FormFairTypeLabel setColor(Supplier<Color> var1) {
      this.color = var1;
      this.drawOptions.reset();
      return this;
   }

   public FormFairTypeLabel setColor(Color var1) {
      return this.setColor(() -> {
         return var1;
      });
   }

   public TypeParser[] getParsers() {
      return this.parsers;
   }

   public FormFairTypeLabel setParsers(TypeParser... var1) {
      this.parsers = var1;
      this.drawOptions.reset();
      return this;
   }
}
