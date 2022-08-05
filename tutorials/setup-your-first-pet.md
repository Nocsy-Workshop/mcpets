# 🦦 Setup your first pet

## Install the mob files

Before you get to add the pet to MCPets, you have to make sure it is already working from both MythicMobs and ModelEngine perspective.

### S**etup the model in ModelEngine** :

* drop the .bbmodel file of your model inside the "ModelEngine/blueprints" folder
* reload ModelEngine with /meg reload
* a new resource pack as been generate by ModelEngine in "ModelEngine/resource pack" folder, so get the files within the folder, zip them and put the zip in your Minecraft resource pack folder

### P**repare the MythicMobs pet** :

* if you bought a pack on the internet, follow the instruction given by your vendor on how to setup the mob in MythicMobs.
* if you're a creator, create the mythicmobs corresponding to the pet, with the skills you want and place the files in your MythicMobs folder at the respective places for your skills and mobs.

At this step, you should be able to spawn the MythicMob corresponding to the pet only using MythicMobs. For instance, give yourself an egg spawner using "_/mm e get \<mobName>"_ and spawn it. The MythicMob have to be working properly and the model should be working as well. If not, please contact the vendor of the pet pack or have a look at the [common mistakes here](../common-issues/common-issues/).

## Installing/Creating the pet files for MCPets

### Installing files or following the basics

After you checked the mob installation was working properly, it is now time to implement it into MCPets !

If you bought a pack from some vendor, just follow the instructions given by the vendor to install the pet file into the "MCPets/Pets" folder. If you're a creator, head in the "MCPets/Pets" folder and create a new .yml file.

At this link you can find a template for your pet config, with all the features you can use and what they are used for. Some are optional so don't feel like you need to fill everything, namely for the Signal Stick or the Skins.

### Adding a name tag to my pet (ModelEngine r2.3.0+)

Since ModelEngine r2.3.0, you have to setup your own bone for name tag. So, to simply the creation process, the bone used for naming pet is preset has **tag\_name**. What it means is that you just have to add a bone named **tag\_name** for the pet's name to show up. Also, remember center its pivot point wherever you want, linking it to another bone if you want, animate it, etc...

Here is a picture example for the Pikachu:

&#x20;<img src="https://i.postimg.cc/wBKkGrGQ/Screenshot-1.png" alt="pikachu name tag positioning" data-size="original">

### Learn how to use signals

MCPets allows you to send orders to your pet. This is achieved through the [Signal Mechanic of MythicMobs](https://www.mythicmobs.net/manual/doku.php/skills/mechanics/signal).

Basically, prepare the order in your MythicMob mob file using the trigger \~onSignal:YOUR\_SIGNAL\_TAG.

When it's done, add "YOUR\_SIGNAL\_TAG" in the list of Signals values in the MCPets file of your pet ([look at the end of the template if you don't know how to do it](https://github.com/AlexandreChaussard/MCPets-Wiki/blob/master/pet\_config\_template.yml)).

If you have several orders to give, just add them to the list. Refer to [The Signal stick](https://github.com/MC-Models/mcpets/wiki/The-Signal-stick) to know how you can navigate through orders in game and cast them.

## Testing the pet functionnalities

Now that everything is ready, you can safely reload MCPets using /mcpets reload. Open the GUI from /mcpets, and summon your new favorite pet.

Depending on how you made it, you should see it follows you when you're being too far. Also try all the options you've enabled like renaming, mounting, and casting orders with the signal stick.
