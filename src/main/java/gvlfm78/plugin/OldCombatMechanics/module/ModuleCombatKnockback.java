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
	
	// Restores knockback to (from what im told) the PVP ideal knockback in 1.6.4->1.7 Change.
	@EventHandler
	public void onCombat(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			damaged.getVelocity().multiply(1.22);
		}
	}

}
