package fr.nocsy.mcpets.data;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerSignal {

    private static final HashMap<UUID, SignalWrapper> orderMap = new HashMap<>();

    /**
     * Set the active signal for the player
     *
     * @param owner
     * @param signal
     */
    public static void setSignal(UUID owner, String signal) {
        Pet pet = Pet.fromOwner(owner);

        if (pet == null)
            return;

        int indexSignal = getSignalIndex(pet, signal);
        if (indexSignal == -1)
            return;

        SignalWrapper wrapper = new SignalWrapper();
        wrapper.setActiveSignal(signal);
        wrapper.setIndexSignal(indexSignal);
        wrapper.setPet(pet);

        orderMap.put(owner, wrapper);
    }

    /**
     * Get the active signal of the player
     *
     * @param owner
     * @return
     */
    public static String getSignalTag(UUID owner) {
        if (orderMap.containsKey(owner)) {
            return orderMap.get(owner).getActiveSignal();
        }
        return null;
    }

    /**
     * Says whether or not the signal is compatible with the pet of the player
     *
     * @param owner
     * @param signal
     * @return
     */
    public static boolean isCompatible(UUID owner, String signal) {
        Pet pet = Pet.fromOwner(owner);

        if (!pet.isStillHere())
            return false;

        return pet.getSignals().contains(signal);
    }

    /**
     * Get the index of the signal of the pet (-1 if doesn't exists)
     *
     * @param pet
     * @param signal
     * @return
     */
    public static int getSignalIndex(@NotNull Pet pet, String signal) {
        if (pet.getSignals().isEmpty())
            return -1;

        int i = 0;
        for (String s : pet.getSignals()) {
            if (s.equalsIgnoreCase(signal))
                return i;
            i++;
        }
        return -1;
    }

    /**
     * Set the default signal to cast for the pet summoned
     *
     * @param owner
     */
    public static void setDefaultSignal(UUID owner, @NotNull Pet pet) {
        if (pet.getSignals().isEmpty())
            return;

        String signal = pet.getSignals().get(0);
        setSignal(owner, signal);
    }

    public static String getNextSignal(UUID owner) {
        if (orderMap.containsKey(owner)) {
            Pet pet = Pet.fromOwner(owner);
            if (pet == null)
                return null;

            if (pet.getSignals().isEmpty())
                return null;

            SignalWrapper wrapper = orderMap.get(owner);
            int newIndex = wrapper.getIndexSignal() + 1;
            if (pet.getSignals().size() <= newIndex)
                newIndex = 0;

            return pet.getSignals().get(newIndex);
        }

        return null;
    }

}

class SignalWrapper {

    @Getter
    @Setter
    private String activeSignal;

    @Getter
    @Setter
    private int indexSignal;

    @Getter
    @Setter
    private Pet pet;

}
