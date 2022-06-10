package de.lennox.rainbowify.config.option;

import com.google.gson.JsonObject;
import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.Option;
import de.lennox.rainbowify.config.OptionRepository;
import de.lennox.rainbowify.config.screen.ConfigScreen;
import de.lennox.rainbowify.config.screen.RenderedCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adds the possibility to sort options in a category, this way the option gui will stay more
 * organized
 *
 * @since 2.0.0
 * @author Lennox
 */
@SuppressWarnings("rawtypes")
public class CategoryOption extends Option {
  private final List<Option> children;

  public CategoryOption(String key, Option... children) {
    //noinspection unchecked
    super(key, "rainbowify.category." + key, null);
    this.children = List.of(children);
    // Define all children as category child in their instance
    this.children.forEach(child -> child.child(true));
  }

  @Override
  public JsonObject parseJson() {
    // Cannot be parsed
    return null;
  }

  @Override
  public void fromJson(JsonObject object) {
    // Cannot be parsed
  }

  @Override
  public SimpleOption parseAsOption() {
    // Cannot be parsed
    return null;
  }

  /**
   * Creates the category in the given game option screen, handles option parsing and title
   * insertion
   *
   * @param screen The option screen
   * @param offset The offset of rendering
   * @since 2.0.0
   */
  public RenderedCategory createRenderCategory(ConfigScreen screen, AtomicInteger offset) {
    // Estimate the height
    List<SimpleOption> options = children.stream().map(Option::parseAsOption).toList();
    int estimatedHeight = estimateWidgetHeightOf(options);
    int currentOffset = offset.get();
    // Create the button list widget
    ButtonListWidget listWidget =
        new ButtonListWidget(
            MinecraftClient.getInstance(),
            screen.width,
            screen.height,
            currentOffset,
            currentOffset + estimatedHeight,
            25);
    listWidget.setRenderHorizontalShadows(false);
    listWidget.setRenderBackground(false);
    // Add all options into the widget
    listWidget.addAll(options.toArray(new SimpleOption[0]));
    // Increment the offset
    offset.getAndAdd(estimatedHeight + 20);
    return new RenderedCategory(
        Text.translatable(translationKey),
        options,
        listWidget,
      estimatedHeight + 20);
  }

  /**
   * Estimates the height of given options
   *
   * @param options The options
   * @return The estimated height
   * @since 2.0.0
   */
  private int estimateWidgetHeightOf(List<SimpleOption> options) {
    // Calculate rows and margin
    int rows = Math.round(options.size() / 2f);
    int totalMargin = rows > 1 ? rows * 5 : 0;
    // Calculate total height
    return rows * 20 + 10 + totalMargin;
  }

  /**
   * Creates a new option category
   *
   * @param key The key name
   * @param children The option children
   * @return The option category
   * @since 2.0.0
   */
  public static CategoryOption of(String key, Option... children) {
    // Add all children into the option repository
    OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();
    for (Option child : children) {
      optionRepository.add(child);
    }
    return new CategoryOption(key, children);
  }
}
