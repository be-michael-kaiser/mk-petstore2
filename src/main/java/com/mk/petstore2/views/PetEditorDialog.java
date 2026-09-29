package com.mk.petstore2.views;

import com.mk.petstore2.model.Pet;
import com.mk.petstore2.model.PetStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal editor used for both creating and updating a {@link Pet}.
 */
public class PetEditorDialog extends Dialog {

    private final Binder<Pet> binder = new BeanValidationBinder<>(Pet.class);
    private final Consumer<Pet> onSave;

    private final TextField name = new TextField("Name");
    private final ComboBox<String> category = new ComboBox<>("Category");
    private final ComboBox<PetStatus> status = new ComboBox<>("Status");
    private final BigDecimalField price = new BigDecimalField("Price");
    private final TextArea description = new TextArea("Description");

    private Pet pet;

    public PetEditorDialog(List<String> knownCategories, Consumer<Pet> onSave) {
        this.onSave = onSave;

        setWidth("32rem");
        setCloseOnOutsideClick(false);

        category.setItems(knownCategories);
        category.setAllowCustomValue(true);
        category.addCustomValueSetListener(event -> category.setValue(event.getDetail()));

        status.setItems(PetStatus.values());
        status.setItemLabelGenerator(PetStatus::getLabel);

        price.setPrefixComponent(new Span("€"));
        description.setMaxLength(500);
        description.setHeight("7rem");

        binder.bindInstanceFields(this);

        FormLayout form = new FormLayout(name, category, status, price, description);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("24rem", 2));
        form.setColspan(description, 2);
        add(form);

        Button save = new Button("Save", event -> {
            if (commit()) {
                close();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", event -> close());

        getFooter().add(cancel, save);
    }

    /**
     * Opens the dialog for the given pet. Pass a fresh {@link Pet} to create a new entry.
     */
    public void open(Pet value) {
        bind(value);
        open();
        name.focus();
    }

    /**
     * Loads {@code value} into the form fields without showing the dialog.
     */
    void bind(Pet value) {
        this.pet = value;
        setHeaderTitle(value.isNew() ? "New pet" : "Edit " + value.getName());
        if (value.getPrice() == null) {
            value.setPrice(BigDecimal.ZERO);
        }
        binder.readBean(value);
    }

    /**
     * Writes the form back into the bound pet and notifies the save callback.
     *
     * @return {@code false} when validation failed and nothing was saved
     */
    boolean commit() {
        if (pet == null) {
            return false;
        }
        try {
            binder.writeBean(pet);
        } catch (ValidationException e) {
            return false;
        }
        onSave.accept(pet);
        return true;
    }

    TextField getNameField() {
        return name;
    }

    ComboBox<String> getCategoryField() {
        return category;
    }

    ComboBox<PetStatus> getStatusField() {
        return status;
    }

    BigDecimalField getPriceField() {
        return price;
    }
}
