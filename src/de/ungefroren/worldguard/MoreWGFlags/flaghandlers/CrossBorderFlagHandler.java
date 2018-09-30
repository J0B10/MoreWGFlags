/**
 * Created on 15.04.2018.
 *
 * @author Jonas Blocher
 */
package de.ungefroren.worldguard.MoreWGFlags.flaghandlers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
public class CrossBorderFlagHandler extends Handler {

    public static final Factory FACTORY = new Factory();
    private static final long MESSAGE_THRESHOLD = 1000 * 2;
    private long lastMessage;

    public CrossBorderFlagHandler(Session session) {
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
        LocalPlayer localPlayer = getPlugin().wrapPlayer(player);
        if (moveType != MoveType.MOVE && moveType != MoveType.RIDE) return true;
        if (!moveType.isCancellable()) return true;
        if (getSession().getManager().hasBypass(player, to.getWorld())) return true;
        boolean allowed = true;
        for (ProtectedRegion region : entered) {
            if (region.getFlag(MoreWGFlagsPlugin.CROSS_BRODER_FLAG) == StateFlag.State.DENY) {
                allowed = false;
                break;

            }
        }
        if (allowed) for (ProtectedRegion region : exited) {
            if (region.getFlag(MoreWGFlagsPlugin.CROSS_BRODER_FLAG) == StateFlag.State.DENY) {
                allowed = false;
                break;
            }
        }
        //send message
        if (!allowed) {
            String message = toSet.queryValue(localPlayer, DefaultFlag.ENTRY_DENY_MESSAGE);
            long now = System.currentTimeMillis();

            if ((now - lastMessage) > MESSAGE_THRESHOLD && message != null && !message.isEmpty()) {
                player.sendMessage(CommandUtils.replaceColorMacros(message));
                lastMessage = now;
            }
        }
        return allowed;
    }

    public static class Factory extends Handler.Factory<CrossBorderFlagHandler> {

        @Override
        public CrossBorderFlagHandler create(Session session) {
            return new CrossBorderFlagHandler(session);
        }
    }
}
