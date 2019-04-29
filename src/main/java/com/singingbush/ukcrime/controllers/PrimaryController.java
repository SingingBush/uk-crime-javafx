package com.singingbush.ukcrime.controllers;

import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.model.SeniorOfficer;
import com.singingbush.ukcrime.services.UkPoliceApi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

    @FXML
    public Button primaryButton;

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
    public void onForceChosen(ActionEvent actionEvent) {
        final PoliceForce force = this.forceSelect.getValue();

        log.debug("chosen force {}", force);

        if(force != null) {
            final List<SeniorOfficer> officers = _api.getPoliceForceSeniorOfficers(force);
            _seniorOfficers.clear();
            _seniorOfficers.addAll(officers);
        } else {
            _seniorOfficers.clear();
        }
    }

    public Callback<ListView<PoliceForce>, ListCell<PoliceForce>> getPoliceForceCellFactory() {
        return new Callback<>() {
            @Override
            public ListCell<PoliceForce> call(ListView<PoliceForce> policeForceListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(PoliceForce policeForce, boolean b) {
                        super.updateItem(policeForce, b);
                        setText(policeForce != null ? policeForce.getName() : null);
                    }
                };
            }
        };
    }

    public Callback<ListView<SeniorOfficer>, ListCell<SeniorOfficer>> getSeniorOfficerCellFactory() {
        return new Callback<>() {
            @Override
            public ListCell<SeniorOfficer> call(ListView<SeniorOfficer> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(SeniorOfficer officer, boolean b) {
                        super.updateItem(officer, b);
                        setText(officer != null ? String.format("%s (%s)", officer.getName(), officer.getRank()) : null);
                    }
                };
            }
        };
    }
}
