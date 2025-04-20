package com.singingbush.ukcrime.ui;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.singingbush.ukcrime.model.Crime;
import com.singingbush.ukcrime.model.Location;
import com.singingbush.ukcrime.model.Neighbourhood;
import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.services.UkPoliceApi;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class NeighbourhoodComponent extends VBox implements ChangeListener<Neighbourhood> {

    private final UkPoliceApi _api;

    @FXML
    private Label crimeCount;
    @FXML
    private WebView neighbourDescriptionHtml;

    @FXML
    private MapView neighbourMap;
    private final CrimeLayer crimeLayer;

    private PoliceForce policeForce = null;

    public NeighbourhoodComponent() {
        this._api = new UkPoliceApi();

        final URL resource = this.getClass().getResource("/components/neighbourhood.fxml");
        final FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // initialise the map view
        neighbourMap.setZoom(5.4);
        neighbourMap.setCenter(new MapPoint(53.3957166,-1.4994561));
        this.crimeLayer = new CrimeLayer();
        neighbourMap.addLayer(this.crimeLayer);
    }

    public void setPoliceForce(final PoliceForce policeForce) {
        this.policeForce = policeForce;
    }

    private void loadNeighbourhood(final PoliceForce policeForce, final String id) {
        @Nullable final Neighbourhood neighbourhood = _api.getPoliceForceNeighbourhood(policeForce, id);

        if (neighbourhood != null) {
            neighbourDescriptionHtml.getEngine().loadContent(
                Optional.ofNullable(neighbourhood.getDescription()).orElseGet(neighbourhood::getName)
            );

            final Location centre = neighbourhood.getCentre();
            final MapPoint mapPoint = new MapPoint(
                Double.parseDouble(centre.getLatitude()),
                Double.parseDouble(centre.getLongitude())
            );
            neighbourMap.flyTo(0.2, mapPoint, 0.8);
            neighbourMap.setZoom(12.0);

            @Nullable final List<Crime> crimes = _api.streetCrimeByLocation(centre.getLatitude(), centre.getLongitude());
            if (crimes != null) {
                this.crimeCount.setText(String.valueOf(crimes.size()));
                this.crimeLayer.addCrimes(crimes);
            }
        }
    }

    @Override
    public void changed(ObservableValue<? extends Neighbourhood> observable, Neighbourhood oldValue, Neighbourhood newNeighbourhood) {
        Optional.ofNullable(this.policeForce).ifPresent(force -> Optional.ofNullable(newNeighbourhood)
            .ifPresent(n -> this.loadNeighbourhood(force, n.getId())));
    }

    static class CrimeLayer extends MapLayer {
        private final ObservableList<Pair<MapPoint, Node>> points = FXCollections.observableArrayList();
        private final Color colour = Color.RED.deriveColor(0, 0.7, 1, 0.6);

        public void addCrimes(@NotNull final List<Crime> crimes) {
            crimes
                .stream().map(Crime::getLocation)
                .map(l -> new MapPoint(
                    Double.parseDouble(l.getLatitude()),
                    Double.parseDouble(l.getLongitude())
                ))
                .forEach(p -> this.addPoint(p, new Circle(6, this.colour)));
            this.markDirty();
        }

        private void addPoint(MapPoint p, Node icon) {
            points.add(new Pair<>(p, icon));
            this.getChildren().add(icon);
        }

        @Override
        protected void layoutLayer() {
            points.forEach(candidate -> {
                final MapPoint point = candidate.getKey();
                final Node icon = candidate.getValue();
                final Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
                icon.setVisible(true);
                icon.setTranslateX(mapPoint.getX());
                icon.setTranslateY(mapPoint.getY());
            });
        }
    }
}
