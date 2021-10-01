package com.singingbush.ukcrime.controllers;

import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.model.SeniorOfficer;
import com.singingbush.ukcrime.services.UkPoliceApi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

    @FXML
    public ComboBox<PoliceForce> forceSelect;

//    @FXML
//    public Button primaryButton;

    @FXML
    public Hyperlink forceUrl;

    public PrimaryController() {
        _api = new UkPoliceApi();
        _policeForces = FXCollections.observableArrayList();
        _seniorOfficers = FXCollections.observableArrayList();
    }

    public ObservableList<PoliceForce> getPoliceForces() {
        return _policeForces;
    }

    public ObservableList<SeniorOfficer> getSeniorOfficers() {
        return _seniorOfficers;
    }

    public PoliceForceListCell getPoliceForceButtonCell() {
        return new PoliceForceListCell();
    }

    @FXML
    protected void initialize() {
        new Thread(() -> {
            Platform.runLater(() -> {
                final List<PoliceForce> forces = _api.getAllPoliceForces();

                _policeForces.addAll(forces);

                forceSelect.setDisable(false);
            });
        }).start();

        log.info("Initialising PrimaryController");
    }

    @FXML
    public void onForceChosen(final ActionEvent actionEvent) {
        final PoliceForce force = this.forceSelect.getValue();

        log.debug("chosen force {}", force);

        if(force != null) {
            final PoliceForce forceDetails = _api.getPoliceForceById(force.getId());
            forceUrl.setText(forceDetails.getUrl()); // todo: phone, description etc?
            //forceUrl.setText(String.format("tel:%s", forceDetails.getTelephone()));

            final List<SeniorOfficer> officers = _api.getPoliceForceSeniorOfficers(force);
            _seniorOfficers.clear();
            _seniorOfficers.addAll(officers);
        } else {
            forceUrl.setText(null);
            _seniorOfficers.clear();
        }
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
