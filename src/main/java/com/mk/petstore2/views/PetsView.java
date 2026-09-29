package com.mk.petstore2.views;

import com.mk.petstore2.model.Pet;
import com.mk.petstore2.model.PetStatus;
import com.mk.petstore2.service.PetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Pets | mk-petstore2")
public class PetsView extends VerticalLayout {

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    private final PetService service;

    private final Grid<Pet> grid = new Grid<>(Pet.class, false);
    private final TextField filterText = new TextField();
    private final ComboBox<PetStatus> filterStatus = new ComboBox<>();
    private final Span summary = new Span();

    public PetsView(PetService service) {
        this.service = service;

        addClassName("pets-view");
        setSizeFull();

        configureGrid();
        add(createToolbar(), grid, summary);
        refresh();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(Pet::getName).setHeader("Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(Pet::getCategory).setHeader("Category").setSortable(true).setAutoWidth(true);
        grid.addComponentColumn(this::statusBadge).setHeader("Status")
                .setComparator(Pet::getStatus).setAutoWidth(true);
        grid.addColumn(pet -> formatPrice(pet.getPrice())).setHeader("Price")
                .setComparator(Pet::getPrice).setAutoWidth(true).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END);
        grid.addColumn(Pet::getDescription).setHeader("Description").setFlexGrow(1);
        grid.addComponentColumn(this::rowActions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);

        grid.addItemDoubleClickListener(event -> edit(event.getItem()));
    }

    private HorizontalLayout createToolbar() {
        filterText.setPlaceholder("Search name or category…");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> refresh());

        filterStatus.setPlaceholder("All statuses");
        filterStatus.setItems(PetStatus.values());
        filterStatus.setItemLabelGenerator(PetStatus::getLabel);
        filterStatus.setClearButtonVisible(true);
        filterStatus.addValueChangeListener(event -> refresh());

        Button addPet = new Button("Add pet", VaadinIcon.PLUS.create(), event -> edit(new Pet()));
        addPet.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, filterStatus, addPet);
        toolbar.addClassName("pet-toolbar");
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        return toolbar;
    }

    private Span statusBadge(Pet pet) {
        PetStatus status = pet.getStatus();
        Span badge = new Span(status == null ? "" : status.getLabel());
        if (status != null) {
            badge.addClassName("pet-badge-" + status.name().toLowerCase(Locale.ROOT));
        }
        return badge;
    }

    private HorizontalLayout rowActions(Pet pet) {
        Button edit = new Button(VaadinIcon.EDIT.create(), event -> edit(pet));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        edit.getElement().setAttribute("aria-label", "Edit " + pet.getName());

        Button delete = new Button(VaadinIcon.TRASH.create(), event -> confirmDelete(pet));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        delete.getElement().setAttribute("aria-label", "Delete " + pet.getName());

        return new HorizontalLayout(edit, delete);
    }

    private void edit(Pet pet) {
        PetEditorDialog dialog = new PetEditorDialog(service.findCategories(), saved -> {
            service.save(saved);
            notify("Saved " + saved.getName(), NotificationVariant.LUMO_SUCCESS);
            refresh();
        });
        dialog.open(pet);
    }

    private void confirmDelete(Pet pet) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete pet");
        dialog.setText("Delete \"%s\"? This cannot be undone.".formatted(pet.getName()));
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            service.delete(pet);
            notify("Deleted " + pet.getName(), NotificationVariant.LUMO_CONTRAST);
            refresh();
        });
        dialog.open();
    }

    private void refresh() {
        List<Pet> pets = service.search(filterText.getValue(), filterStatus.getValue());
        grid.setItems(pets);
        summary.setText("%d of %d pets shown".formatted(pets.size(), service.count()));
    }

    private void notify(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
    }

    private static String formatPrice(BigDecimal price) {
        return price == null ? "" : CURRENCY.format(price);
    }
}
