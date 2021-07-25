package net.silentchaos512.lib.client.gui.nbt;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.lib.util.NBTToJson;
import net.silentchaos512.lib.util.TextRenderUtils;

import java.util.ArrayList;
import java.util.List;

public class DisplayNBTScreen extends Screen {
    final List<String> lines;
    private final CompoundNBT nbtCompound;
    private DisplayNBTList displayList;
    private ITextComponent header;

    public DisplayNBTScreen(CompoundNBT nbt, ITextComponent titleIn) {
        super(titleIn);
        this.lines = formatNbt(nbt, 0);
        this.nbtCompound = nbt;
        this.header = title;
    }

    @Override
    protected void init() {
        if (minecraft == null) minecraft = Minecraft.getInstance();

        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        int width = 100;
        int height = 20;
        this.addButton(new Button(scaledWidth - width - 2, scaledHeight - height - 2, width, height, new StringTextComponent("Export to JSON"), b -> {
            JsonObject json = NBTToJson.toJsonObject(this.nbtCompound);
            String message = NBTToJson.writeFile(json);
            this.header = new StringTextComponent(message);
        }));

        this.displayList = new DisplayNBTList(this, minecraft, scaledWidth, this.height, 12, this.height - 12, 11);
        this.children.add(this.displayList);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;
        this.displayList.render(matrix, mouseX, mouseY, partialTicks);
        String titleStr = this.header.getString();
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        TextRenderUtils.renderScaled(matrix, font, new StringTextComponent(titleStr).getVisualOrderText(), (scaledWidth - font.width(titleStr)) / 2, 2, 1f, 0xFFFFFF, true);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private static List<String> formatNbt(CompoundNBT nbt, int depth) {
        List<String> list = new ArrayList<>();

        for (String key : nbt.getAllKeys()) {
            INBT inbt = nbt.get(key);
            list.addAll(formatNbt(key, inbt, depth + 1));
        }

        return list;
    }

    private static List<String> formatNbt(String key, INBT nbt, int depth) {
        List<String> list = new ArrayList<>();
        String indentStr = indent(depth);

        //noinspection ChainOfInstanceofChecks
        if (nbt instanceof CompoundNBT) {
            formatCompound(key, (CompoundNBT) nbt, depth, list, indentStr);
        } else if (nbt instanceof CollectionNBT) {
            formatList(key, (CollectionNBT) nbt, depth, list, indentStr);
        } else if (nbt instanceof NumberNBT) {
            formatNumber(key, (NumberNBT) nbt, list, indentStr);
        } else if (nbt instanceof StringNBT) {
            String value = nbt.getAsString();
            list.add(indentStr + format(key, value, TextFormatting.GREEN));
        }

        return list;
    }

    private static void formatCompound(String key, CompoundNBT nbt, int depth, List<String> list, String indentStr) {
        if (nbt.isEmpty()) {
            list.add(indentStr + format(key, "{}", TextFormatting.RESET));
        } else {
            list.add(indentStr + format(key, "{", TextFormatting.RESET));
            list.addAll(formatNbt(nbt, depth + 1));
            list.add(indentStr + "}" + (key.isEmpty() ? "" : TextFormatting.DARK_GRAY + " #" + key));
        }
    }

    private static void formatList(String key, CollectionNBT nbt, int depth, List<String> list, String indentStr) {
        if (nbt.isEmpty()) {
            list.add(indentStr + format(key, "[]", TextFormatting.RESET));
        } else {
            list.add(indentStr + format(key, "[", TextFormatting.RESET));
            for (INBT element : (CollectionNBT<?>) nbt) {
                list.addAll(formatNbt("", element, depth + 1));
            }
            list.add(indentStr + "]" + (key.isEmpty() ? "" : TextFormatting.DARK_GRAY + " #" + key));
        }
    }

    private static void formatNumber(String key, NumberNBT nbt, List<String> list, String indentStr) {
        Number value = nbt.getAsNumber();
        String line = indentStr + format(key, value, TextFormatting.LIGHT_PURPLE);
        if (value instanceof Integer) {
            line += TextFormatting.GRAY + String.format(" (0x%X)", value.intValue());
        }
        list.add(line);
    }

    private static String format(String key, Object value, TextFormatting valueFormat) {
        if (key.isEmpty()) {
            return valueFormat + value.toString();
        } else {
            return TextFormatting.GOLD + key + TextFormatting.RESET + ": " + valueFormat + value;
        }
    }

    private static String indent(int depth) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; ++i) {
            builder.append("  ");
        }
        return builder.toString();
    }
}
