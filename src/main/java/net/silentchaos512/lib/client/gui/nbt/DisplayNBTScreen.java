package net.silentchaos512.lib.client.gui.nbt;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.silentchaos512.lib.util.NBTToJson;
import net.silentchaos512.lib.util.TextRenderUtils;

import java.util.ArrayList;
import java.util.List;

public class DisplayNBTScreen extends Screen {
    final List<String> lines;
    private final CompoundTag nbtCompound;
    private DisplayNBTList displayList;
    private Component header;

    public DisplayNBTScreen(CompoundTag nbt, Component titleIn) {
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
        Button exportButton = Button.builder(Component.literal("Export to JSON"), b -> {
            JsonObject json = NBTToJson.toJsonObject(this.nbtCompound);
            String message = NBTToJson.writeFile(json);
            this.header = Component.literal(message);
        }).bounds(scaledWidth - width - 2, scaledHeight - height - 2, width, height).build();
        this.addRenderableWidget(exportButton);

        this.displayList = new DisplayNBTList(this, minecraft, scaledWidth, this.height, 12, this.height - 12, 11);
        this.addWidget(displayList);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;
        this.displayList.render(matrix, mouseX, mouseY, partialTicks);
        String titleStr = this.header.getString();
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        TextRenderUtils.renderScaled(matrix, font, Component.literal(titleStr).getVisualOrderText(), (scaledWidth - font.width(titleStr)) / 2, 2, 1f, 0xFFFFFF, true);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private static List<String> formatNbt(CompoundTag nbt, int depth) {
        List<String> list = new ArrayList<>();

        for (String key : nbt.getAllKeys()) {
            Tag inbt = nbt.get(key);
            list.addAll(formatNbt(key, inbt, depth + 1));
        }

        return list;
    }

    private static List<String> formatNbt(String key, Tag nbt, int depth) {
        List<String> list = new ArrayList<>();
        String indentStr = indent(depth);

        //noinspection ChainOfInstanceofChecks
        if (nbt instanceof CompoundTag) {
            formatCompound(key, (CompoundTag) nbt, depth, list, indentStr);
        } else if (nbt instanceof CollectionTag) {
            formatList(key, (CollectionTag) nbt, depth, list, indentStr);
        } else if (nbt instanceof NumericTag) {
            formatNumber(key, (NumericTag) nbt, list, indentStr);
        } else if (nbt instanceof StringTag) {
            String value = nbt.getAsString();
            list.add(indentStr + format(key, value, ChatFormatting.GREEN));
        }

        return list;
    }

    private static void formatCompound(String key, CompoundTag nbt, int depth, List<String> list, String indentStr) {
        if (nbt.isEmpty()) {
            list.add(indentStr + format(key, "{}", ChatFormatting.RESET));
        } else {
            list.add(indentStr + format(key, "{", ChatFormatting.RESET));
            list.addAll(formatNbt(nbt, depth + 1));
            list.add(indentStr + "}" + (key.isEmpty() ? "" : ChatFormatting.DARK_GRAY + " #" + key));
        }
    }

    private static void formatList(String key, CollectionTag nbt, int depth, List<String> list, String indentStr) {
        if (nbt.isEmpty()) {
            list.add(indentStr + format(key, "[]", ChatFormatting.RESET));
        } else {
            list.add(indentStr + format(key, "[", ChatFormatting.RESET));
            for (Tag element : (CollectionTag<?>) nbt) {
                list.addAll(formatNbt("", element, depth + 1));
            }
            list.add(indentStr + "]" + (key.isEmpty() ? "" : ChatFormatting.DARK_GRAY + " #" + key));
        }
    }

    private static void formatNumber(String key, NumericTag nbt, List<String> list, String indentStr) {
        Number value = nbt.getAsNumber();
        String line = indentStr + format(key, value, ChatFormatting.LIGHT_PURPLE);
        if (value instanceof Integer) {
            line += ChatFormatting.GRAY + String.format(" (0x%X)", value.intValue());
        }
        list.add(line);
    }

    private static String format(String key, Object value, ChatFormatting valueFormat) {
        if (key.isEmpty()) {
            return valueFormat + value.toString();
        } else {
            return ChatFormatting.GOLD + key + ChatFormatting.RESET + ": " + valueFormat + value;
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
