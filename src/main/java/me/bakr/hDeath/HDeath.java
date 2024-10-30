package me.bakr.hDeath;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class HDeath extends JavaPlugin implements Listener {

    private final String name = "bakR"; // تعريف المتغير كمتغير فئة

    @Override
    public void onEnable() {
        String releaseDate = "10/30/2024";
        getLogger().info("\u001B[36m" + "Plugin activated by: " + name + " | Release Date: " + releaseDate + "\u001B[0m");
        getServer().getPluginManager().registerEvents(this, this); // تسجيل الأحداث
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();

        // رسالة في الدردشة مع سبب الموت
        String deathCause = event.getDeathMessage(); // للحصول على سبب الموت
        String deathMessage = ChatColor.RED + player.getName() + " الله يرحمه! مات في الهاردكور في الموقع: " +
                "X: " + location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ() +
                " بسبب: " + deathCause;
        Bukkit.broadcastMessage(deathMessage); // بث الرسالة لجميع اللاعبين

        // عرض عنوان عند الموت
        player.sendTitle(ChatColor.RED + "الله يرحمه!", ChatColor.GRAY + "في الموقع: " +
                "X: " + location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ(), 10, 70, 20);

        // توليد زومبي يحمل رأس اللاعب فقط
        new BukkitRunnable() {
            @Override
            public void run() {
                Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);

                // زيادة نقاط الحياة (HP) للزومبي إلى 3 أضعاف
                zombie.setMaxHealth(60.0); // جعل الصحة 3 أضعاف (الصحة الافتراضية 20)
                zombie.setHealth(60.0); // تعيين الصحة إلى 60 عند التولد

                // إعداد رأس اللاعب على الزومبي
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
                skullMeta.setOwningPlayer(player); // تعيين اللاعب كصاحب الرأس
                playerHead.setItemMeta(skullMeta);

                // وضع الرأس على الزومبي
                zombie.getEquipment().setHelmet(playerHead);

                // وضع اسم اللاعب فوق الزومبي
                zombie.setCustomName(ChatColor.RED + player.getName());
                zombie.setCustomNameVisible(true);
            }
        }.runTaskLater(this, 20L); // تنفيذ المهمة بعد 20 تكتس (تقريباً ثانية واحدة)
    }

    @EventHandler
    public void onZombieDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();

            // الحصول على الرأس الذي كان على الزومبي
            ItemStack helmet = zombie.getEquipment().getHelmet();

            if (helmet != null && helmet.getType() == Material.PLAYER_HEAD) {
                // إذا كان الرأس هو رأس لاعب، قم بإسقاطه
                zombie.getWorld().dropItem(zombie.getLocation(), helmet);
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("\u001B[36m" + "Plugin disabled by: " + name + "\u001B[0m");
    }
}
