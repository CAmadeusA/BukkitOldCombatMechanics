package gvlfm78.plugin.OldCombatMechanics.module;

import java.util.Collection;
import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.util.Vector;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;

/**
 * Created by Rayzr522 on 6/27/16.
 */
public class ModuleFishingKnockback extends Module {

	public ModuleFishingKnockback(OCMMain plugin) {
		super(plugin, "old-fishing-knockback");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRodLand(ProjectileHitEvent e) {

		Entity hookEntity = e.getEntity();
		World world = hookEntity.getWorld();

		if (!isEnabled(world))
			return;

		if (e.getEntityType() != EntityType.FISHING_HOOK)
			return;

		Entity hitent = null;

		try {
			hitent = e.getHitEntity();
		} catch (NoSuchMethodError e1) { // For older version that don't have such method
			Collection<Entity> entities = world.getNearbyEntities(hookEntity.getLocation(), 0.25, 0.25, 0.25);

			for (Entity entity : entities) {
				if (entity instanceof Player)
					hitent = entity;
				break;

			}
		}

		if (hitent == null)
			return;
		if (!(hitent instanceof LivingEntity))
			return;

		FishHook hook = (FishHook) hookEntity;
		Player rodder = (Player) hook.getShooter();
		LivingEntity living = (LivingEntity) hitent;

		if (living.getUniqueId().equals(rodder.getUniqueId()))
			return;

		if (living instanceof Player) {
			if (((Player) living).getGameMode() == GameMode.CREATIVE)
				return;
		}

		double damage = 0.0005;

		EntityDamageEvent event = makeEvent(rodder, living, damage);
		Bukkit.getPluginManager().callEvent(event);

		if (module().getBoolean("checkCancelled") && event.isCancelled()) {

			// This is to check what plugins are listening to the event
			if (plugin.getConfig().getBoolean("debug.enabled")) {
				debug("You can't do that here!", rodder);
				HandlerList hl = event.getHandlers();

				for (RegisteredListener rl : hl.getRegisteredListeners())
					debug("Plugin Listening: " + rl.getPlugin().getName(), rodder);
			}

			return;
		}
		// Because entities can take damage when less than 10 nodamageticks, for some weird reason. #BlameMojang.
		if (living.getNoDamageTicks() <= 10) {			
			living.damage(damage);
			Location loc = living.getLocation();
			float yamount = (((rodder.getLocation().getPitch() * -1) + 180));
			Vector yvec = new Vector(0, yamount, 0).multiply(0.00555555555).multiply(2.5);
			if (yvec.getY() > 1.95) {
				yvec = new Vector(0, 1.95, 0);
			}
			if (yvec.getY() < 1.45) {
				yvec = new Vector(0, 1.45, 0);
			}
			living.setNoDamageTicks(living.getMaximumNoDamageTicks());
			living.setVelocity(loc.subtract(rodder.getLocation()).toVector().add(yvec).normalize().multiply(0.65));
		
		}

	}

	@EventHandler
	public void playerFishEvent(PlayerFishEvent event) {
		if (event.getCaught() instanceof LivingEntity) {
			event.getHook().remove();
			event.setCancelled(true);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EntityDamageEvent makeEvent(Player rodder, LivingEntity rodded, double damage) {

		if (module().getBoolean("useEntityDamageEvent"))
			return new EntityDamageEvent(rodded, EntityDamageEvent.DamageCause.PROJECTILE,
					new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damage)),
					new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(damage))));
		else
			return new EntityDamageByEntityEvent(rodder, rodded, EntityDamageEvent.DamageCause.PROJECTILE,
					new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damage)),
					new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(damage))));
	}
}