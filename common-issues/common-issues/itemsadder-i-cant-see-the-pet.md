# ItemsAdder: I can't see the pet

Most likely you didn't import the file from Model Engine into your ItemsAdder resource pack, or partially or not correctly.

An easy fix is to take the folder "assets" into "./plugins/ModelEngine/resource pack/" and drop it where your "assets" folder is located in ItemsAdder or the other resource pack manager you use. Merge the two "assets" folder (the one from ModelEngine with ItemsAdder).

In case there is any conflict, overwrite the files from ItemsAdder if you want to prioritize the models. However conflicts indicate that "leather\_horse\_armor.json" is most likely already used by ItemsAdder for another item model, so I would suggest you to change that in your Items Adder resource pack.
