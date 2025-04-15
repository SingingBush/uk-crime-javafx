package com.singingbush.ukcrime.controllers;

import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.model.SeniorOfficer;
import com.singingbush.ukcrime.services.UkPoliceApi;
import com.singingbush.ukcrime.ui.PoliceForceComponent;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PrimaryController {

    private static final Logger log = LoggerFactory.getLogger(PrimaryController.class);

    private final UkPoliceApi _api;
    private final ObservableList<PoliceForce> _policeForces;
    private final ObservableList<SeniorOfficer> _seniorOfficers;
    private final SimpleBooleanProperty _hasOfficers;

    @FXML
    private PoliceForceComponent currentForce;

    @FXML
    private ListView<PoliceForce> forceListView;

    @FXML
    private ListView<SeniorOfficer> officersListView;

    public PrimaryController() {
        _api = new UkPoliceApi();
        _policeForces = FXCollections.observableArrayList();
        _seniorOfficers = FXCollections.observableArrayList();
        _hasOfficers = new SimpleBooleanProperty(false);
    }

    public ObservableList<PoliceForce> getPoliceForces() {
        return _policeForces;
    }

    public ObservableList<SeniorOfficer> getSeniorOfficers() {
        return _seniorOfficers;
    }

    @FXML
    protected void initialize() {
        officersListView.visibleProperty().bind(_hasOfficers);

        new Thread(() -> {
            Platform.runLater(() -> {
                final List<PoliceForce> forces = _api.getAllPoliceForces(); // todo: get this from a service that can call API and cache response

                _policeForces.addAll(forces);

                forceListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPoliceForce, newPoliceForce) -> {
                    final PoliceForce forceDetails = _api.getPoliceForceById(newPoliceForce.getId());

                    currentForce.setPoliceForce(forceDetails);

                    final List<SeniorOfficer> officers = _api.getPoliceForceSeniorOfficers(newPoliceForce);
                    _seniorOfficers.clear();
                    _seniorOfficers.addAll(officers);

                    _hasOfficers.setValue(!officers.isEmpty());
                });
            });
        }).start();

        log.info("Initialising PrimaryController");
    }

    public Callback<ListView<PoliceForce>, ListCell<PoliceForce>> getPoliceForceCellFactory() {
        return policeForceListView -> new PoliceForceListCell();
    }

    public Callback<ListView<SeniorOfficer>, ListCell<SeniorOfficer>> getSeniorOfficerCellFactory() {
        return listView -> new SeniorOfficerListCell();
    }

    private static class PoliceForceListCell extends ListCell<PoliceForce> {
        @Override
        protected void updateItem(PoliceForce policeForce, boolean empty) {
            super.updateItem(policeForce, empty);
            if (empty) {
                setText(null);
            } else {
                setText(policeForce != null ? policeForce.getName() : "null");
            }
        }
    }

    private static class SeniorOfficerListCell extends ListCell<SeniorOfficer> {
        @Override
        protected void updateItem(SeniorOfficer officer, boolean empty) {
            super.updateItem(officer, empty);
            if (empty) {
                setText(null);
            } else {
                setText(officer != null ? String.format("%s (%s)", officer.getName(), officer.getRank()) : "null");
            }
        }
    }
}
