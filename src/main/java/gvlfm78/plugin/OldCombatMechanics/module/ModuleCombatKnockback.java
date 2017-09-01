package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;

public class ModuleCombatKnockback extends Module {

	public ModuleCombatKnockback(OCMMain plugin) {
		super(plugin, "old-combat-knockback");
	}
	
	@EventHandler
	public void onCombat(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			damaged.getVelocity().multiply(new Vector(1.22, 1, 1.22));
			event.getDamager().sendMessage("Native Y Velocity: " + damaged.getVelocity().getY());
			if (damaged.getVelocity().getY() < 0) {
				damaged.setVelocity(new Vector(damaged.getVelocity().getX(), 0, damaged.getVelocity().getZ()));
			}
			event.getDamager().sendMessage("Adjusted: " + damaged.getVelocity().getY());
		}
	}

}
