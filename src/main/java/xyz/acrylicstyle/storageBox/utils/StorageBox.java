package xyz.acrylicstyle.storageBox.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;
import xyz.acrylicstyle.storageBox.StorageBoxPlugin;

import java.util.Arrays;
import java.util.UUID;

public class StorageBox {
    private final UUID uuid;
    private boolean autoCollect = true;
    private Material type;
    private int amount;

    public StorageBox(@NotNull UUID uuid, @Nullable Material type, int amount) {
        this.uuid = uuid;
        this.type = type;
        this.amount = amount;
    }

    public StorageBox(@NotNull UUID uuid, @Nullable Material type, int amount, boolean autoCollect) {
        this.uuid = uuid;
        this.type = type;
        this.amount = amount;
        this.autoCollect = autoCollect;
    }

    @Nullable
    public static StorageBox getStorageBox(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;
        try {
            String s = Paper.itemStack(itemStack).getOrCreateTag().getString("uuid");
            try {
                return StorageBox.loadStorageBox(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return null;
            }
        } catch (RuntimeException e) {
            return null;
        }
    }

    @NotNull
    @Contract("-> new")
    public static StorageBox getNewStorageBox() {
        return getNewStorageBox(null);
    }

    @NotNull
    @Contract("_ -> new")
    public static StorageBox getNewStorageBox(@Nullable Material type) {
        return getNewStorageBox(type, 0);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static StorageBox getNewStorageBox(@Nullable Material type, int amount) {
        UUID uuid = UUID.randomUUID();
        String prefix = "boxes." + uuid.toString() + ".";
        StorageBoxPlugin.config.set(prefix + "autoCollect", true);
        StorageBoxPlugin.config.set(prefix + "type", type);
        StorageBoxPlugin.config.set(prefix + "amount", amount);
        return new StorageBox(uuid, type, amount);
    }

    public void delete() {
        StorageBoxPlugin.config.set("boxes." + uuid.toString(), null);
    }

    @NotNull
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        String name;
        if (type == null) {
            name = "空";
        } else {
            name = type.name().replaceAll("_", " ").toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Storage Box " + ChatColor.YELLOW + "[" + ChatColor.WHITE + name + ChatColor.YELLOW + "]");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Amount: " + amount, ChatColor.GRAY + "AutoCollect: " + autoCollect));
        item.setItemMeta(meta);
        ItemStackUtils is = Paper.itemStack(item);
        NBTTagCompound tag = is.getOrCreateTag();
        tag.setString("uuid", uuid.toString());
        is.setTag(tag);
        item = is.getItemStack();
        return item;
    }

    @NotNull
    public UUID getUniqueId() { return uuid; }

    public void setAmount(int amount) {
        StorageBoxPlugin.config.set("boxes." + uuid.toString() + ".amount", amount);
        this.amount = amount;
    }

    public void increaseAmount() {
        setAmount(amount + 1);
    }

    public void decreaseAmount() {
        setAmount(amount - 1);
    }

    /**
     * Get material of this storage box.
     * @return Null if undefined, material otherwise.
     */
    @Nullable
    public Material getType() {
        return type;
    }

    /**
     * Set material of this storage box.
     * @param type Null if undefined, material otherwise.
     */
    public void setType(@Nullable Material type) {
        StorageBoxPlugin.config.set("boxes." + uuid.toString() + ".type", type == null ? null : type.name());
        this.type = type;
    }

    public int getAmount() { return amount; }

    public boolean isEmpty() { return amount <= 0; }

    public boolean isAutoCollect() {
        return autoCollect;
    }

    public void setAutoCollect(boolean autoCollect) {
        StorageBoxPlugin.config.set("boxes." + uuid.toString() + ".autoCollect", autoCollect);
        this.autoCollect = autoCollect;
    }
}
