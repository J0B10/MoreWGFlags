# MoreWGFlags

This plugin is a addon for
[WorldGuard](https://github.com/sk89q/worldguard) that adds some custom
flags for convinience.

Tested on `spigot 1.12.2` with `WorldGaurd 6.2.1`.

-----------------

### Added custom region flags:

* **`cross-border`** Control if a player can walk through the border of a region.  
If the flag is set to `deny` he/she can't cross the border, the only way to get in and out is then via teleportation.  
Players with op rights can always pass the border for convinience.  
*default: `allow`*  

* **`direct-teleport`** Set this flag to a location and any player entering the region will be teleported directly to the specified loaction.  
Used properly this can make things a lot easier but be cautious as you can get completly stuck if you create a loop that teleports you between two loactions.  
A good rule of thumb to prevent this kind of trouble is to make sure the target location dosn't have this flag set.  
*default: not set*

----------------

**[>> Download release](https://github.com/joblo2213/MoreWGFlags/releases/download/v1.0/MoreWGFlags.jar)**
