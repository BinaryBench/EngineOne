package me.engineone.engine.components.spectate;

import me.engineone.core.holder.CollectionHolder;
import me.engineone.engine.components.base.ParentListenerComponent;
import me.engineone.engine.components.disablers.*;
import me.engineone.engine.utilites.ListUtilities;
import me.engineone.engine.utilites.NumberUtilities;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SpectatorComponent extends ParentListenerComponent implements CollectionHolder<Player> {

    private static final float SPEED_INCREMENT = 0.1f;
    private final Set<Player> spectators = new HashSet<>();
    private final Set<Consumer<Player>> removeListeners = new HashSet<>();
    private final Set<Consumer<Player>> addListeners = new HashSet<>();
    private final List<Vector> spawns;

    public SpectatorComponent(List<Vector> spawns) {
        this.spawns = spawns;
        addChild(new PvPDisabledComponent(this));
        addChild(new PvEDisabledComponent(this));
        addChild(new HungerDisabledComponent(this));
        addChild(new DropItemDisabledComponent(this));
        addChild(new PickUpDisabledComponent(this));

        onAdd(this::addListener);
        onRemove(this::removeListener);
        onDisable(this::clear);
    }

    private void addListener(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(new Vector());
        player.getInventory().setHeldItemSlot(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, true, true));

        player.teleport(player.getLocation().add(0, 10, 0));
        player.getInventory().clear();

        //player.getInventory().setItemInOffHand(new ItemStack(Material.ARROW, 1));
        player.setFlySpeed(0.1f);
    }

    private void removeListener(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setVelocity(new Vector());
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(ListUtilities.getRandom(spawns).toLocation(player.getWorld()));
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());


        player.setFlySpeed(0.1f);
        player.getInventory().clear();
    }

    @EventHandler
    public void onScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (!test(player))
            return;

        event.setCancelled(true);
        if (event.getNewSlot() == 0)
            return;

        float speed = player.getFlySpeed() + (event.getNewSlot() < 5 ? -SPEED_INCREMENT : SPEED_INCREMENT);
        if (speed > 1.05f || speed < SPEED_INCREMENT + 0.05f) {
            player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 10, 2);
            return;
        }

        //player.getInventory().getItemInOffHand().setAmount((int) (speed * 10));
        player.setFlySpeed(NumberUtilities.bound(speed, 1f, 0.1f));
    }


    @Override
    public Set<Consumer<Player>> getRemoveListeners() {
        return removeListeners;
    }

    @Override
    public Set<Consumer<Player>> getAddListeners() {
        return addListeners;
    }

    @Override
    public Collection<Player> getContents() {
        return spectators;
    }
}