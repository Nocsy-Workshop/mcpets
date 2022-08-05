# 🧪 Upcoming evolutions

⏳ In progress ✅ Done 🧪 To test ❌ Abandonned ❓ To discuss

### ⏳ Small changes & new features

* ❌ Show skill on cooldown
  * Requires a skill to be triggered from MM coz of possible delays and cancel etc...
  * So it requires vendors to edit all their products to adapt it to the new system, or it won't show any cooldown
* ❌ Add a flat SQL support
* Add multi-server support to keep pets spawned between servers
* ✅ \[issue] Skins are not loaded if it's a middle file (get crushed by the others)
* ⏳ \[issue] "setname" mechanic doesn't update the pet's name
* ⏳ \[issue] 8 pets in the category menu ain't showing, while 9 does due to the pager being here
* ⏳ Add an option to despawn the pet on dismount
* ⏳ Save skin choice for each pet
* Add support for factions plugin for flags

### ⏳ Inventory system

* ✅ Possibility to store items in the pet inventory if enabled
  * ✅ Define default inventory size for each pet
    * ⏳ make it work with the evolving system in which a pet can eventually add up space in this inventory

### Taming system

* Define Foods in a specific file (Food file)
  * command to give oneself food
  * command to add an item to the food list
* Taming the pet with a list of different foods
  * Taming progress bar
  * Taming causes the mob to naturaly follow the owner if set enabled
    * create a follow up mechanic in MM (pretty much like the usual integration for MCPets)
    * create an event \~onTame (triggered when interacted with the right food item)
    * create trigger skill when fully tamed ?

### Capture system

* Possibility to capture the pet into a certain item and stock it for later to be spawn
  * Define a list of Capture Items
    * In game command to give a capture item
    * In game command to stock a capture item
  * Define to what Capture Items the pet is affected by
  * Define chances of capture
    * Depending on taming/health ?
  * Play MM skill on capture ? (I'm thinking of Pokeball animation eventually, I'll leave that to the vendors)
  * Capturing a pet stocks it has "living pet" for the player
  * ❓ There can not be multiple capture of the same pet at once
    * technical aspect : becomes more complicated and doesn't suit the old system
    * logical aspect : what is the fundamental interest of having the same pet multiple times
    * logical aspect of MCPets : makes it look like "collectioning pets", feels better to me

### Living pets system

* Define a pet as "living" or "cosmetic"
  * In game command to add/remove a pet from the "living" or "cosmetic" group
  * Cosmetic is the current system (pet is invincible, following owner, TP, etc...)
  * Living system makes the pet vulnerable, adds a respawn cooldown, adds a revoke cooldown, adds regeneration items, adds experience...
    * Stock pet's health
      * can not summon a dead pet
        * Possibility to lose the pet permanently on death
      * Stock last summon from player
    * Define a delay before the pet is available after death
    * Define amount of HP when it's available again
      * Stock last revoke from player
    * Define delay before resummoning the pet
      * Define Food Items that are capable of regenerating the pet
    * Define regeneration amount
      * Define pet's experience
    * Set calculation (linear, power, exponential)
    * Define experience gained when owner kills an entity
    * Define experience gained when pet kills an entity
    * Define MM skill to give experience to the pet (so it can be given by any other specific event in MM)
      * Define progression stats (Levels, Health, Attack, Power -to use in skills like healing for instance-, Inventory Size)
    * Define MM skill on level up (animations, particles, etc...)
      * Define evolving into another pet triggered at a certain level (swapping permissions, reseting stats)
      * ❓ Can not evolve if there is already the evolved version within the owner's pet inventory, coz of what was explained in "capture system"
  * Show all the previous bullet points in the MCPets Menu when hovering the pet, in its description
