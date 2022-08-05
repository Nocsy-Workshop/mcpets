# My pet can't be mounted

Several mistakes can be causing this issue:

* Make sure the "model" mechanic of the pet enables driving. Get into "./plugins/MythicMobs/" and locate the mob config file, then open it. Within the various "Skills" of the mob, you should find one called "model". Make sure it has the argument "drive=true" enabled. Refer to [Model Engine Wiki](https://github.com/Ticxo/Model-Engine-Wiki/wiki/Mechanics) if you can't find it.
* Make sure the model has a "mount" bone. Open the model of the pet in Blockbench, and make sure it has a bone called "mount".
