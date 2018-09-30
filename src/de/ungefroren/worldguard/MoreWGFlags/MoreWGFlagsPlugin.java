/**
 * Created on 15.04.2018.
 *
 * @author Jonas Blocher
 */
package de.ungefroren.worldguard.MoreWGFlags;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import de.ungefroren.worldguard.MoreWGFlags.flaghandlers.CrossBorderFlagHandler;
import de.ungefroren.worldguard.MoreWGFlags.flaghandlers.DirectTeleportFlagHandler;

/**
 * Created on 15.04.2018.
 *
 * @author Jonas Blocher
 */
public class MoreWGFlagsPlugin extends JavaPlugin {

    public static final StateFlag CROSS_BRODER_FLAG = new StateFlag("cross-border", true);
    public static final LocationFlag DIRECT_TELEPORT_FLAG = new LocationFlag("direct-teleport");
    boolean error = false;

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuardPlugin.inst().getFlagRegistry();
        final Flag[] flags = {
                CROSS_BRODER_FLAG,
                DIRECT_TELEPORT_FLAG
        };
        for (Flag flag : flags) {
            try {
                registry.register(flag);
                getLogger().log(Level.INFO, "Registered flag " + flag.getName());
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not register flag " + flag.getName() + ": " + e.getMessage());
                error = true;
                return;
            }
        }
        super.onLoad();
    }

    @Override
    public void onEnable() {
        if (error) {
            setEnabled(false);
            return;
        }
        SessionManager sessionManager = WorldGuardPlugin.inst().getSessionManager();
        final Handler.Factory[] handlerFactories = {
                CrossBorderFlagHandler.FACTORY,
                DirectTeleportFlagHandler.FACTORY
        };
        for (Handler.Factory factory : handlerFactories) {
            String[] args = factory.getClass().getName().split("[.$]");
            final String handlername = args[args.length > 1 ? args.length - 2 : 0];
            try {
                sessionManager.registerHandler(factory, null);
                getLogger().log(Level.SEVERE, "Registered flag handler " + handlername);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not register flag handler " + handlername + ": " + e.getMessage());
                setEnabled(false);
                return;
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null && worldGuard.isEnabled()) {
            final Handler.Factory[] handlerFactories = {
                    CrossBorderFlagHandler.FACTORY,
                    DirectTeleportFlagHandler.FACTORY
            };
            SessionManager sessionManager = WorldGuardPlugin.inst().getSessionManager();
            for (Handler.Factory factory : handlerFactories) {
                String[] args = factory.getClass().getName().split("[.$]");
                final String handlername = args[args.length > 1 ? args.length - 2 : 0];
                sessionManager.unregisterHandler(factory);
                getLogger().log(Level.INFO, "Flag handler " + handlername + "  unregistered!");
            }
            WorldGuardPlugin.inst().getSessionManager().unregisterHandler(CrossBorderFlagHandler.FACTORY);
            getLogger().log(Level.INFO, "Flag handler unregistered!");
        }
        super.onDisable();
    }
}
