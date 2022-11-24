package fr.nocsy.mcpets.data.serializer;

import com.google.gson.Gson;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.utils.PetTimer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class PetStatsSerializer {

    @Getter
    // Reference to the actual pet
    private String petId;
    @Getter
    private UUID petOwner;
    @Getter
    // Handles the health of the Pet
    private double currentHealth;
    @Getter
    // Handles the experience of the pet
    private double experience;
    @Getter
    // Handles the levels
    private String levelId;

    /**
     * Constructor
     * @param petId
     * @param petOwner
     * @param currentHealth
     * @param experience
     * @param levelId
     */
    private PetStatsSerializer(String petId,
                              UUID petOwner,
                              double currentHealth,
                              double experience,
                              String levelId)
    {
        this.petId = petId;
        this.petOwner = petOwner;
        this.currentHealth = currentHealth;
        this.experience = experience;
        this.levelId = levelId;
    }

    /**
     * Build the serializer from the pet stats instance
     * @param stats
     * @return
     */
    public static PetStatsSerializer build(@NotNull PetStats stats)
    {
        return new PetStatsSerializer(stats.getPet().getId(),
                                    stats.getPet().getOwner(),
                                    stats.getCurrentHealth(),
                                    stats.getExperience(),
                                    stats.getCurrentLevel().getLevelId());
    }

    /**
     * Rebuild the PetStats from the serialized
     * @return
     */
    public PetStats buildStats()
    {
        Pet pet = Pet.getFromId(petId);
        if(pet == null)
            return null;

        pet.setOwner(petOwner);

        PetLevel currentLevel = pet.getPetLevels().stream()
                                                .filter(level -> level.getLevelId().equals(levelId))
                                                .findFirst()
                                                .orElse(null);
        if(currentLevel == null)
            return null;

        return new PetStats(pet, experience, currentHealth, currentLevel);
    }

    /**
     * Get the JSON representation of the pet stats
     * @return
     */
    public String JSONformatted()
    {
        return new Gson().toJson(this);
    }

    /**
     * Return a string representation of the object
     * @return
     */
    public String serialize()
    {
        String jsonStr = JSONformatted();
        jsonStr = Base64.getEncoder().encodeToString(jsonStr.getBytes());
        return jsonStr;
    }

    /**
     * Returns the object unserialized from the given serialized string
     * @return
     */
    public static PetStatsSerializer unserialize(String serialized)
    {
        String decoded = new String(Base64.getDecoder().decode(serialized.getBytes()));
        if(decoded == null || decoded.isEmpty())
            return null;
        return new Gson().fromJson(decoded, PetStatsSerializer.class);
    }

}
