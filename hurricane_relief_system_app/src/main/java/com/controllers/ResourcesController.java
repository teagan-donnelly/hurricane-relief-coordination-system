package com.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hurricane_relief_system.App;
import com.model.HurricaneReliefApplication;
import com.model.Location;
import com.model.Shelter;
import com.model.ShelterStatus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Resources screen (resources.fxml): emergency contacts, open shelters,
 * preparedness guides and FAQ. Open to everyone, no login needed.
 */
public class ResourcesController {

    /** Widest the centred content column gets, not counting side padding (from the design). */
    private static final double CONTENT_WIDTH = 1200;
    /** Below this window width the nav hides the brand name and tightens up. */
    private static final double COMPACT_NAV_WIDTH = 900;
    /** A shelter at or under this share of free beds is shown as "Almost full". */
    private static final double ALMOST_FULL_SHARE = 0.2;

    // Header photo crop from the design: the 620x180 slot shows x 20%-100%, y 42%-84% of the image.
    private static final double PHOTO_SLOT_WIDTH = 620;
    private static final double PHOTO_X = 0.20, PHOTO_W = 0.80, PHOTO_Y = 0.42, PHOTO_H = 0.42;
    private static final double PHOTO_MIN_WIDTH = 200;

    private static final String IMAGES = "images/resources/";

    @FXML private ScrollPane scroll;
    @FXML private HBox navBar;
    @FXML private VBox brandName;
    @FXML private HBox navLinks;
    @FXML private Button accountButton;

    @FXML private Pane photoPane;
    @FXML private ImageView headerPhoto;
    @FXML private Region photoFade;
    @FXML private VBox headerColumn;
    @FXML private Label headerDescription;

    @FXML private VBox mainContent;
    @FXML private GridPane contactGrid;
    @FXML private GridPane shelterGrid;
    @FXML private GridPane guideGrid;
    @FXML private Label shelterSubtitle;
    @FXML private Label shelterMeta;
    @FXML private Label shelterEmpty;

    @FXML private ToggleGroup guideTabs;
    @FXML private ImageView guideIcon0, guideIcon1, guideIcon2;
    @FXML private Label guideTitle0, guideTitle1, guideTitle2;
    @FXML private Label guideBody0, guideBody1, guideBody2;
    @FXML private Label guideTime0, guideTime1, guideTime2;

    @FXML private HBox footer;

    private Guide[] visibleGuides = GUIDES[0];

    @FXML
    private void initialize() {
        loadShelters();

        // Keep one guide tab selected at all times; clicking the active tab does nothing.
        guideTabs.selectedToggleProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) {
                guideTabs.selectToggle(oldTab);
            } else {
                showGuides(Integer.parseInt((String) newTab.getUserData()));
            }
        });
        Toggle selected = guideTabs.getSelectedToggle();
        showGuides(selected == null ? 0 : Integer.parseInt((String) selected.getUserData()));

        scroll.viewportBoundsProperty().addListener((obs, oldBounds, bounds) -> layoutFor(bounds.getWidth()));
        layoutFor(1440);
    }

    // ------------------------------------------------------------------
    // Responsive layout
    // ------------------------------------------------------------------

    /** Adapts padding, columns and the header photo to the visible width. */
    private void layoutFor(double width) {
        if (width <= 0) {
            return;
        }
        double pad = width >= 1440 ? 120 : width >= 1024 ? 64 : width >= 720 ? 32 : 16;
        double edgePad = width >= 1024 ? 64 : width >= 720 ? 32 : 16;

        mainContent.setPadding(new Insets(48, pad, 80, pad));
        mainContent.setMaxWidth(CONTENT_WIDTH + 2 * pad);
        headerColumn.setPadding(new Insets(36, pad, 36, pad));
        headerColumn.setMaxWidth(CONTENT_WIDTH + 2 * pad);
        footer.setPadding(new Insets(28, edgePad, 28, edgePad));

        boolean compactNav = width < COMPACT_NAV_WIDTH;
        navBar.setPadding(new Insets(0, edgePad, 0, edgePad));
        navLinks.setSpacing(compactNav ? 16 : 40);
        brandName.setVisible(!compactNav);
        brandName.setManaged(!compactNav);
        accountButton.setPrefWidth(compactNav ? Region.USE_COMPUTED_SIZE : 148);

        double columnWidth = Math.min(width, CONTENT_WIDTH + 2 * pad) - 2 * pad;
        reflow(contactGrid, 4, 220, columnWidth);
        reflow(shelterGrid, 3, 300, columnWidth);
        reflow(guideGrid, 3, 300, columnWidth);

        // Header photo: shrink from the left so the text never runs under it.
        double textRight = Math.max(0, (width - (CONTENT_WIDTH + 2 * pad)) / 2) + pad
                + headerDescription.prefWidth(-1) + 40;
        double photoWidth = Math.min(PHOTO_SLOT_WIDTH, width - textRight);
        boolean showPhoto = photoWidth >= PHOTO_MIN_WIDTH;
        photoPane.setVisible(showPhoto);
        photoPane.setManaged(showPhoto);
        if (showPhoto) {
            sizePhoto(photoWidth);
        }
    }

    /** Lays the grid's children out in as many equal columns (up to maxColumns) as fit. */
    private static void reflow(GridPane grid, int maxColumns, double minCellWidth, double width) {
        double gap = grid.getHgap();
        int columns = (int) Math.floor((width + gap) / (minCellWidth + gap));
        columns = Math.max(1, Math.min(maxColumns, columns));

        List<ColumnConstraints> constraints = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(0, 100, Double.MAX_VALUE);
            column.setHgrow(Priority.ALWAYS);
            column.setFillWidth(true);
            constraints.add(column);
        }
        grid.getColumnConstraints().setAll(constraints);

        List<Node> cells = grid.getChildren();
        for (int i = 0; i < cells.size(); i++) {
            GridPane.setConstraints(cells.get(i), i % columns, i / columns);
        }
    }

    private void sizePhoto(double photoWidth) {
        Image image = headerPhoto.getImage();
        if (image == null || image.getWidth() <= 0) {
            return;
        }
        double visibleShare = PHOTO_W * photoWidth / PHOTO_SLOT_WIDTH;
        double x = PHOTO_X + PHOTO_W - visibleShare;
        headerPhoto.setViewport(new Rectangle2D(
                x * image.getWidth(), PHOTO_Y * image.getHeight(),
                visibleShare * image.getWidth(), PHOTO_H * image.getHeight()));
        headerPhoto.setPreserveRatio(false);
        headerPhoto.setFitWidth(photoWidth);
        headerPhoto.setFitHeight(180);
        photoPane.setPrefWidth(photoWidth);
        photoPane.setMaxWidth(photoWidth);
        photoFade.setPrefWidth(Math.min(280, photoWidth * 0.45));
    }

    // ------------------------------------------------------------------
    // Shelters
    // ------------------------------------------------------------------

    /** Replaces the design-time sample cards with live shelter data. */
    private void loadShelters() {
        List<Shelter> shelters;
        try {
            shelters = new ArrayList<>(HurricaneReliefApplication.getInstance().findShelters());
        } catch (RuntimeException e) {
            e.printStackTrace();
            shelters = new ArrayList<>();
        }
        shelters.removeIf(s -> s.getStatus() == ShelterStatus.CLOSED || s.getLocation() == null);

        boolean hasData = !shelters.isEmpty();
        shelterGrid.setVisible(hasData);
        shelterGrid.setManaged(hasData);
        shelterEmpty.setVisible(!hasData);
        shelterEmpty.setManaged(!hasData);
        shelterMeta.setVisible(hasData);
        if (!hasData) {
            shelterGrid.getChildren().clear();
            shelterSubtitle.setText("Shelters in your area, with live space updates.");
            return;
        }

        // Most free beds first; full shelters last.
        shelters.sort(Comparator.comparingInt(ResourcesController::bedsOpen).reversed());
        List<Node> cards = new ArrayList<>();
        for (Shelter shelter : shelters) {
            cards.add(buildShelterCard(shelter));
        }
        shelterGrid.getChildren().setAll(cards);

        shelterSubtitle.setText("Shelters near " + mostCommonArea(shelters) + ", with live space updates.");
        shelterMeta.setText("Updated " + LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
    }

    private static int bedsOpen(Shelter shelter) {
        if (shelter.getStatus() == ShelterStatus.FULL) {
            return 0;
        }
        return Math.max(0, Math.min(shelter.getCapacity(), shelter.getAvailableSpace()));
    }

    private static String mostCommonArea(List<Shelter> shelters) {
        Map<String, Integer> counts = new HashMap<>();
        String best = null;
        for (Shelter shelter : shelters) {
            Location l = shelter.getLocation();
            String area = l.getCity() + ", " + l.getState();
            int count = counts.merge(area, 1, Integer::sum);
            if (best == null || count > counts.get(best)) {
                best = area;
            }
        }
        return best;
    }

    private static String formatAddress(Location l) {
        StringBuilder address = new StringBuilder(l.getStreetAddress());
        if (l.getApartmentNum() > 0) {
            address.append(" #").append(l.getApartmentNum());
        }
        return address.append(", ").append(l.getCity()).append(", ")
                .append(l.getState()).append(' ').append(l.getZipcode()).toString();
    }

    /** Builds a shelter card with the same structure and style classes as the samples in resources.fxml. */
    private Node buildShelterCard(Shelter shelter) {
        int capacity = Math.max(0, shelter.getCapacity());
        int open = bedsOpen(shelter);
        String address = formatAddress(shelter.getLocation());

        String status;
        String statusText;
        if (open == 0) {
            status = "status-full";
            statusText = "Full";
        } else if (capacity > 0 && (double) open / capacity <= ALMOST_FULL_SHARE) {
            status = "status-low";
            statusText = "Almost full";
        } else {
            status = "status-ok";
            statusText = "Space available";
        }

        Label name = label(shelter.getName(), "h3");
        name.setWrapText(true);
        Label addressLabel = label(address, "body-small", "muted");
        addressLabel.setWrapText(true);
        VBox heading = new VBox(4, name, addressLabel);

        FlowPane features = new FlowPane(8, 8);
        if (shelter.isPetFriendly()) {
            features.getChildren().add(chip("Pets allowed"));
        }
        if (shelter.getAccessibilityInfo() != null && shelter.getAccessibilityInfo().stream()
                .anyMatch(info -> info.toLowerCase().contains("wheelchair"))) {
            features.getChildren().add(chip("Wheelchair accessible"));
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox availabilityRow = new HBox(label(statusText, "label-text", status), spacer,
                label(open + " of " + capacity + " beds open", "body-small", "muted"));

        double filled = capacity == 0 ? 100 : 100.0 * (capacity - open) / capacity;
        GridPane track = new GridPane();
        track.getStyleClass().add("capacity-track");
        track.setMinHeight(8);
        track.setPrefHeight(8);
        track.setMaxHeight(8);
        ColumnConstraints fillColumn = new ColumnConstraints();
        fillColumn.setPercentWidth(filled);
        ColumnConstraints restColumn = new ColumnConstraints();
        restColumn.setPercentWidth(100 - filled);
        track.getColumnConstraints().setAll(fillColumn, restColumn);
        Region fill = new Region();
        fill.getStyleClass().addAll("capacity-fill", status);
        fill.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        track.add(fill, 0, 0);

        Button directions = textLink("Get directions");
        directions.setOnAction(e -> App.openWebPage("https://www.google.com/maps/dir/?api=1&destination="
                + URLEncoder.encode(address, StandardCharsets.UTF_8)));

        VBox card = new VBox(14, heading);
        if (!features.getChildren().isEmpty()) {
            card.getChildren().add(features);
        }
        card.getChildren().addAll(new VBox(8, availabilityRow, track), directions);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return card;
    }

    private static Label label(String text, String... styleClasses) {
        Label label = new Label(text);
        label.getStyleClass().addAll(styleClasses);
        return label;
    }

    private static Node chip(String text) {
        StackPane chip = new StackPane(label(text, "body-small"));
        chip.getStyleClass().add("chip");
        return chip;
    }

    private static Button textLink(String text) {
        ImageView chevron = new ImageView(image("chevron-right.png"));
        chevron.setFitWidth(20);
        chevron.setFitHeight(20);
        Button link = new Button(text, chevron);
        link.getStyleClass().setAll("button", "text-link");
        link.setContentDisplay(ContentDisplay.RIGHT);
        link.setGraphicTextGap(4);
        link.setMnemonicParsing(false);
        return link;
    }

    private static Image image(String file) {
        return new Image(App.class.getResource(IMAGES + file).toExternalForm());
    }

    // ------------------------------------------------------------------
    // Preparedness guides
    // ------------------------------------------------------------------

    private void showGuides(int stage) {
        visibleGuides = GUIDES[Math.max(0, Math.min(GUIDES.length - 1, stage))];
        ImageView[] icons = {guideIcon0, guideIcon1, guideIcon2};
        Label[] titles = {guideTitle0, guideTitle1, guideTitle2};
        Label[] bodies = {guideBody0, guideBody1, guideBody2};
        Label[] times = {guideTime0, guideTime1, guideTime2};
        for (int i = 0; i < titles.length; i++) {
            Guide guide = visibleGuides[i];
            icons[i].setImage(image(guide.icon));
            titles[i].setText(guide.title);
            bodies[i].setText(guide.summary);
            times[i].setText(guide.minutes + " min read");
        }
    }

    @FXML
    private void readGuide(ActionEvent event) {
        int index = Integer.parseInt((String) ((Node) event.getSource()).getUserData());
        Guide guide = visibleGuides[index];

        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(scroll.getScene().getWindow());
        dialog.setTitle("Preparedness Guide");
        dialog.setHeaderText(guide.title);
        dialog.setContentText("• " + String.join("\n\n• ", guide.steps));
        dialog.getDialogPane().setPrefWidth(520);
        dialog.showAndWait();
    }

    // ------------------------------------------------------------------
    // FAQ
    // ------------------------------------------------------------------

    @FXML
    private void toggleFaq(MouseEvent event) {
        VBox item = (VBox) event.getSource();
        Node answer = item.lookup(".faq-answer");
        ImageView chevron = (ImageView) item.lookup(".faq-chevron");
        boolean expand = !answer.isVisible();
        answer.setVisible(expand);
        answer.setManaged(expand);
        item.setSpacing(expand ? 10 : 0);
        chevron.setImage(image(expand ? "chevron-up.png" : "chevron-down.png"));
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------

    @FXML
    private void goHome() throws IOException {
        App.setRoot("home");
    }

    /** Submitting and tracking requests needs an account, so these go to sign-in for now. */
    @FXML
    private void goRequestHelp() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void goMyRequests() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void goLogin() throws IOException {
        App.setRoot("login");
    }

    // ------------------------------------------------------------------
    // Guide content
    // ------------------------------------------------------------------

    private static final class Guide {
        final String icon;
        final String title;
        final String summary;
        final int minutes;
        final String[] steps;

        Guide(String icon, String title, String summary, int minutes, String... steps) {
            this.icon = icon;
            this.title = title;
            this.summary = summary;
            this.minutes = minutes;
            this.steps = steps;
        }
    }

    /** Guides per tab: [0] before, [1] during, [2] after the storm. */
    private static final Guide[][] GUIDES = {
        {
            new Guide("package.png", "Build an Emergency Kit",
                "Water, food, medicine, flashlights, and batteries to last your household several days.", 5,
                "Store at least 1 gallon of water per person per day, for several days.",
                "Pack non-perishable food and a manual can opener, plus baby food or pet food if you need it.",
                "Add a flashlight, a battery-powered or hand-crank radio, and extra batteries.",
                "Include a first aid kit, a week's supply of prescription medicines, and copies of prescriptions.",
                "Keep phone chargers, a backup battery, and some cash with the kit."),
            new Guide("map.png", "Make an Evacuation Plan",
                "Know your evacuation zone, your route out, and where you will stay.", 4,
                "Look up your evacuation zone with your county emergency management office.",
                "Plan at least two routes out. Keep your gas tank at least half full during hurricane season.",
                "Decide where you will stay: friends or family outside the zone, a hotel, or a public shelter.",
                "Plan for your pets. Not every shelter accepts them, so check before you go.",
                "Leave right away when local officials tell you to evacuate."),
            new Guide("file-text.png", "Protect Important Documents",
                "Keep IDs, insurance papers, and medical records in a waterproof bag.", 3,
                "Gather IDs, insurance policies, deeds or leases, medical records, and bank information.",
                "Seal them in a waterproof bag or container, and take it with you if you evacuate.",
                "Take photos or scans and keep copies somewhere secure online.",
                "Photograph your home and belongings now to help with insurance claims later."),
        },
        {
            new Guide("life-buoy.png", "Shelter in Place Safely",
                "Stay in an interior room away from windows until the storm has fully passed.", 3,
                "Go to a small interior room, closet, or hallway on the lowest floor that will not flood.",
                "Stay away from windows, skylights, and glass doors.",
                "If water rises, move to a higher floor, but not into a closed attic where you could be trapped.",
                "If the eye passes over, stay inside. The wind will return quickly from the other direction."),
            new Guide("alert.png", "Stay Informed",
                "Keep a radio on and follow instructions from local officials.", 2,
                "Keep a battery-powered or hand-crank radio tuned to NOAA Weather Radio or local news.",
                "Turn on emergency alerts on your phone, and save battery by limiting other use.",
                "Follow instructions from local officials, including evacuation orders."),
            new Guide("map.png", "Avoid Floodwater",
                "Never walk, swim, or drive through floodwater. Turn around, don't drown.", 3,
                "Never walk, swim, or drive through floodwater. Turn around, don't drown.",
                "Just 6 inches of moving water can knock you down, and 1 foot can sweep a car away.",
                "Floodwater can hide debris, sharp objects, downed power lines, and sewage."),
        },
        {
            new Guide("map.png", "Return Home Safely",
                "Wait for the all-clear, then watch for power lines, damaged roads, and gas leaks.", 3,
                "Wait until local officials say it is safe to return.",
                "Stay away from downed power lines and report them to your utility company.",
                "Watch for weakened roads, bridges, and trees, and avoid flooded areas.",
                "If you smell gas, leave right away and call 911 from a safe place."),
            new Guide("alert.png", "Prevent Carbon Monoxide Poisoning",
                "Run generators and grills outdoors only, far from windows and doors.", 2,
                "Only run generators, pressure washers, and grills outdoors, at least 20 feet from windows and doors.",
                "Never run them in a garage, even with the door open.",
                "If you feel dizzy, weak, or sick to your stomach, get to fresh air and call 911."),
            new Guide("file-text.png", "Document Damage and Get Aid",
                "Photograph damage, start your insurance claim, and apply for assistance.", 4,
                "Take photos and video of the damage before you start cleaning up.",
                "Contact your insurance company to start a claim.",
                "Apply for FEMA assistance at DisasterAssistance.gov or 1-800-621-3362.",
                "Submit a relief request here if you need help with supplies, repairs, or transport."),
        },
    };
}
