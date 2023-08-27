package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.flags.PetDamageableByPlayerFlag;
import fr.nocsy.mcpets.events.PetDamagedByEntityEvent;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PetDamageFlagListener implements Listener {

    @EventHandler
    public void damaged_pvp(PetDamagedByEntityEvent e)
    {
        Player p = null;
        if (e.getDamager() instanceof Player)
            p = (Player) e.getDamager();

        if(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
            p = (Player)((Projectile)e.getDamager()).getShooter();

        if(p == null)
            return;

        if(FlagsManager.getFlag(PetDamageableByPlayerFlag.NAME).testState(BukkitAdapter.adapt(e.getPet().getActiveMob().getLocation())))
        {
            e.setCancelled(true);
            e.setOriginalDamages(0);
        }

    }

}
