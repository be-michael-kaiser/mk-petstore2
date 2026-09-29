package com.mk.petstore2.service;

import com.mk.petstore2.model.Pet;
import com.mk.petstore2.model.PetStatus;
import com.mk.petstore2.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "petstore.sample-data", havingValue = "true", matchIfMissing = true)
public class SampleDataLoader {

    private static final Logger log = LoggerFactory.getLogger(SampleDataLoader.class);

    @Bean
    ApplicationRunner seedPets(PetRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            List<Pet> pets = List.of(
                    new Pet("Luna", "Dog", PetStatus.AVAILABLE, new BigDecimal("450.00"),
                            "Two-year-old border collie, house trained and great with children."),
                    new Pet("Milo", "Cat", PetStatus.AVAILABLE, new BigDecimal("180.00"),
                            "Playful tabby kitten, vaccinated and microchipped."),
                    new Pet("Kiwi", "Bird", PetStatus.PENDING, new BigDecimal("95.50"),
                            "Green budgerigar, hand tame and already talking."),
                    new Pet("Shelly", "Reptile", PetStatus.AVAILABLE, new BigDecimal("220.00"),
                            "Hermann's tortoise with certified papers."),
                    new Pet("Nibbles", "Small pet", PetStatus.SOLD, new BigDecimal("35.00"),
                            "Dwarf hamster, sold last week but awaiting pickup."),
                    new Pet("Bubbles", "Fish", PetStatus.AVAILABLE, new BigDecimal("12.75"),
                            "Betta splendens, needs a heated tank of at least 20 litres."),
                    new Pet("Rocky", "Dog", PetStatus.PENDING, new BigDecimal("620.00"),
                            "Four-year-old German shepherd, trained for agility."),
                    new Pet("Sesame", "Cat", PetStatus.AVAILABLE, new BigDecimal("210.00"),
                            "Ginger domestic shorthair, very affectionate.")
            );
            repository.saveAll(pets);
            log.info("Seeded {} sample pets", pets.size());
        };
    }
}
