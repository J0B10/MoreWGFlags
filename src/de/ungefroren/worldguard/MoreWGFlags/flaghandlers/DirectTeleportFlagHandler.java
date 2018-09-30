/**
 * Created on 15.04.2018.
 *
 * @author Jonas Blocher
 */
package de.ungefroren.worldguard.MoreWGFlags.flaghandlers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import de.ungefroren.worldguard.MoreWGFlags.MoreWGFlagsPlugin;

/**
 * Created on 15.04.2018.
 *
 * @author Jonas Blocher
 */
public class DirectTeleportFlagHandler extends Handler {

    public static final Factory FACTORY = new Factory();

    public DirectTeleportFlagHandler(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(Player player,
                                   Location from,
                                   Location to,
                                   ApplicableRegionSet toSet,
                                   Set<ProtectedRegion> entered,
                                   Set<ProtectedRegion> exited,
                                   MoveType moveType) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return true;
        LocalPlayer localPlayer = getPlugin().wrapPlayer(player);
        com.sk89q.worldedit.Location location = toSet.queryValue(localPlayer, MoreWGFlagsPlugin.DIRECT_TELEPORT_FLAG);
        if (location == null) return true;
        Location l = new Location(Bukkit.getWorld(location.getWorld().getName()),
                                  location.getPosition().getX(),
                                  location.getPosition().getY(),
                                  location.getPosition().getZ(),
                                  location.getYaw(),
                                  location.getPitch());
        RegionManager regionManager = WorldGuardPlugin.inst().getRegionManager(Bukkit.getWorld(location.getWorld().getName()));
        ApplicableRegionSet destRegions = regionManager.getApplicableRegions(l);
        if (destRegions.queryValue(localPlayer, MoreWGFlagsPlugin.DIRECT_TELEPORT_FLAG) != null) {
            System.out.println("[MoreWGFlags] Could not teleport " + player.getName() + " to [" + location.toString() + "]: Target " +
                                       "region also has the flag direct-teleport!");
            return false;
        }
        player.teleport(l, PlayerTeleportEvent.TeleportCause.PLUGIN);
        return true;
    }

    public static class Factory extends Handler.Factory<DirectTeleportFlagHandler> {

        @Override
        public DirectTeleportFlagHandler create(Session session) {
            return new DirectTeleportFlagHandler(session);
        }
    }
}
