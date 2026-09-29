package com.mk.petstore2.service;

import com.mk.petstore2.model.Pet;
import com.mk.petstore2.model.PetStatus;
import com.mk.petstore2.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public List<Pet> findAll() {
        return search(null, null);
    }

    /**
     * Finds pets whose name or category contains {@code term} and that match {@code status}.
     * Both arguments are optional; {@code null} or blank means "no restriction".
     */
    public List<Pet> search(String term, PetStatus status) {
        String normalized = (term == null || term.isBlank()) ? null : term.trim();
        return repository.search(normalized, status);
    }

    public Optional<Pet> findById(Long id) {
        return repository.findById(id);
    }

    public List<String> findCategories() {
        return repository.findDistinctCategories();
    }

    public long count() {
        return repository.count();
    }

    @Transactional
    public Pet save(Pet pet) {
        return repository.save(pet);
    }

    @Transactional
    public void delete(Pet pet) {
        if (pet.getId() != null) {
            repository.deleteById(pet.getId());
        }
    }
}
