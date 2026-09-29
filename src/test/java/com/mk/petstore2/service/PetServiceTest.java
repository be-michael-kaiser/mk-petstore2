package com.mk.petstore2.service;

import com.mk.petstore2.model.Pet;
import com.mk.petstore2.model.PetStatus;
import com.mk.petstore2.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PetService.class)
@TestPropertySource(properties = "petstore.sample-data=false")
class PetServiceTest {

    @Autowired
    private PetService service;

    @Autowired
    private PetRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Pet("Luna", "Dog", PetStatus.AVAILABLE, new BigDecimal("450.00"), "Border collie"),
                new Pet("Milo", "Cat", PetStatus.SOLD, new BigDecimal("180.00"), "Tabby kitten"),
                new Pet("Rocky", "Dog", PetStatus.PENDING, new BigDecimal("620.00"), "German shepherd")
        ));
    }

    @Test
    void findAllReturnsEveryPetSortedByName() {
        assertThat(service.findAll()).extracting(Pet::getName).containsExactly("Luna", "Milo", "Rocky");
    }

    @Test
    void searchWithoutCriteriaReturnsEverything() {
        assertThat(service.search(null, null)).hasSize(3);
        assertThat(service.search("   ", null)).hasSize(3);
    }

    @Test
    void searchMatchesNameCaseInsensitively() {
        assertThat(service.search("luna", null)).extracting(Pet::getName).containsExactly("Luna");
    }

    @Test
    void searchMatchesCategory() {
        assertThat(service.search("dog", null)).extracting(Pet::getName).containsExactly("Luna", "Rocky");
    }

    @Test
    void searchFiltersByStatus() {
        assertThat(service.search(null, PetStatus.SOLD)).extracting(Pet::getName).containsExactly("Milo");
    }

    @Test
    void searchCombinesTermAndStatus() {
        assertThat(service.search("Dog", PetStatus.PENDING)).extracting(Pet::getName).containsExactly("Rocky");
        assertThat(service.search("Dog", PetStatus.SOLD)).isEmpty();
    }

    @Test
    void findCategoriesReturnsDistinctSortedValues() {
        assertThat(service.findCategories()).containsExactly("Cat", "Dog");
    }

    @Test
    void saveCreatesAndUpdatesPets() {
        Pet created = service.save(new Pet("Kiwi", "Bird", PetStatus.AVAILABLE, new BigDecimal("95.50"), "Budgie"));
        assertThat(created.getId()).isNotNull();
        assertThat(service.count()).isEqualTo(4);

        created.setStatus(PetStatus.SOLD);
        service.save(created);
        assertThat(service.findById(created.getId())).get().extracting(Pet::getStatus).isEqualTo(PetStatus.SOLD);
    }

    @Test
    void deleteRemovesPet() {
        Pet milo = service.search("Milo", null).getFirst();
        service.delete(milo);

        assertThat(service.count()).isEqualTo(2);
        assertThat(service.findById(milo.getId())).isEmpty();
    }

    @Test
    void deleteIgnoresTransientPet() {
        service.delete(new Pet());
        assertThat(service.count()).isEqualTo(3);
    }
}
