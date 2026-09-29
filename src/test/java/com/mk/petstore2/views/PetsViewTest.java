package com.mk.petstore2.views;

import com.mk.petstore2.model.Pet;
import com.mk.petstore2.model.PetStatus;
import com.mk.petstore2.service.PetService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;

/**
 * Verifies the view wires up against the service without requiring a browser.
 */
class PetsViewTest {

    private static final List<Pet> PETS = List.of(
            new Pet("Luna", "Dog", PetStatus.AVAILABLE, new BigDecimal("450.00"), "Border collie"),
            new Pet("Milo", "Cat", PetStatus.SOLD, new BigDecimal("180.00"), "Tabby kitten")
    );

    private PetService service;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(PetService.class);
        Mockito.when(service.search(any(), any())).thenReturn(PETS);
        Mockito.when(service.search(isNull(), isNull())).thenReturn(PETS);
        Mockito.when(service.count()).thenReturn((long) PETS.size());
        Mockito.when(service.findCategories()).thenReturn(List.of("Cat", "Dog"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void viewLoadsPetsIntoGridOnConstruction() {
        PetsView view = new PetsView(service);

        Grid<Pet> grid = findChild(view, Grid.class);
        assertThat(((ListDataProvider<Pet>) grid.getDataProvider()).getItems()).hasSize(2);
        assertThat(grid.getColumns()).hasSize(6);
    }

    @Test
    void changingTheFilterTriggersANewSearch() {
        PetsView view = new PetsView(service);

        TextField filter = findChild(view, TextField.class);
        filter.setValue("Luna");

        Mockito.verify(service).search("Luna", null);
    }

    @Test
    void editorBindsAnExistingPetIntoTheFormFields() {
        PetEditorDialog dialog = new PetEditorDialog(List.of("Cat", "Dog"), pet -> {
        });
        Pet luna = new Pet("Luna", "Dog", PetStatus.AVAILABLE, new BigDecimal("450.00"), "Border collie");
        luna.setId(1L);

        dialog.bind(luna);

        assertThat(dialog.getHeaderTitle()).isEqualTo("Edit Luna");
        assertThat(dialog.getNameField().getValue()).isEqualTo("Luna");
        assertThat(dialog.getCategoryField().getValue()).isEqualTo("Dog");
        assertThat(dialog.getStatusField().getValue()).isEqualTo(PetStatus.AVAILABLE);
        assertThat(dialog.getPriceField().getValue()).isEqualByComparingTo("450.00");
    }

    @Test
    void editorCommitsEditedValuesToTheCallback() {
        List<Pet> saved = new java.util.ArrayList<>();
        PetEditorDialog dialog = new PetEditorDialog(List.of("Cat", "Dog"), saved::add);

        Pet blank = new Pet();
        dialog.bind(blank);
        assertThat(dialog.getHeaderTitle()).isEqualTo("New pet");

        dialog.getNameField().setValue("Kiwi");
        dialog.getCategoryField().setValue("Bird");
        dialog.getStatusField().setValue(PetStatus.PENDING);
        dialog.getPriceField().setValue(new BigDecimal("95.50"));

        assertThat(dialog.commit()).isTrue();
        assertThat(saved).containsExactly(blank);
        assertThat(blank.getName()).isEqualTo("Kiwi");
        assertThat(blank.getCategory()).isEqualTo("Bird");
        assertThat(blank.getStatus()).isEqualTo(PetStatus.PENDING);
        assertThat(blank.getPrice()).isEqualByComparingTo("95.50");
    }

    @Test
    void editorRejectsInvalidInput() {
        List<Pet> saved = new java.util.ArrayList<>();
        PetEditorDialog dialog = new PetEditorDialog(List.of("Cat", "Dog"), saved::add);

        dialog.bind(new Pet());

        assertThat(dialog.commit()).isFalse();
        assertThat(saved).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Component> T findChild(Component root, Class<T> type) {
        return (T) root.getChildren()
                .<Component>mapMulti((component, consumer) -> {
                    consumer.accept(component);
                    component.getChildren().forEach(consumer);
                })
                .filter(type::isInstance)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No " + type.getSimpleName() + " found"));
    }
}
