# My pet is removed on spawn

This can be caused by several plugins or world configuration :

* Make sure your world isn't on peaceful mode, as some pets are using agressive anchors.
* Make sure the region in which you spawn the pet allows animals/agressive mob spawning.
* Try first to spawn the pet from MythicMobs by giving you an egg (/mm e get ).
  * If the pet doesn't spawn still, get into "./plugins/MCPets/Pets/" and find the YAML config of your pet. Open it, and make sure the "MythicMobs" field corresponds to the MythicMob you're trying to invoke.

If the pet still doesn't spawn after this, then it's most likely an issue with MythicMobs. So make sure to have a look at their wiki/support.
