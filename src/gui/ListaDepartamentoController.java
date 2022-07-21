package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utilitario;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class ListaDepartamentoController implements Initializable, DataChangeListener {

	private DepartamentoService service;

	@FXML
	private TableView<Departamento> tableViewDepartamento;

	@FXML
	private TableColumn<Departamento, Integer> tableColumnId;

	@FXML
	private TableColumn<Departamento, String> tableColumnNome;

	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEditar;

	@FXML
	private TableColumn<Departamento, Departamento> tableColumnDeletar;

	@FXML
	private Button btNovo;

	private ObservableList<Departamento> obsLista;

	@FXML
	public void onBTNovoAction(ActionEvent event) {
		Stage parentStage = Utilitario.stageAtual(event);
		Departamento obj = new Departamento();
		criarFormDialogo(obj, "/gui/FormularioDep.fxml", parentStage);
	}

	public void setDepartamentoService(DepartamentoService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

		iniciarNodes();

	}

	private void iniciarNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void atualizaTableView() {
		if (service == null) {
			throw new IllegalStateException("Service esta nulo!!!");
		}

		List<Departamento> lista = service.findAll();
		obsLista = FXCollections.observableArrayList(lista);

		tableViewDepartamento.setItems(obsLista);
		iniciaBotaoEdicao();
		iniciaBotaoDeletar();
	}

	private void criarFormDialogo(Departamento obj, String nomeAbsoluto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane pane = loader.load();

			DepartamentoFormController controlador = loader.getController();
			controlador.setDepartamento(obj);
			controlador.setDepartamentoService(new DepartamentoService());
			controlador.inscreveDCL(this);
			controlador.atualizaFormData();

			Stage dialogoStage = new Stage();
			dialogoStage.setTitle("Nome do Departamento");
			dialogoStage.setScene(new Scene(pane));
			dialogoStage.setResizable(false);
			dialogoStage.initOwner(parentStage);
			dialogoStage.initModality(Modality.WINDOW_MODAL);
			dialogoStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro carregar View", e.getMessage(), AlertType.ERROR);
		}
	}

	private void iniciaBotaoEdicao() {
		tableColumnEditar.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tableColumnEditar.setCellFactory(p -> new TableCell<Departamento, Departamento>() {
			private final Button b = new Button("editar");

			@Override
			protected void updateItem(Departamento obj, boolean vazio) {
				super.updateItem(obj, vazio);

				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(b);
				b.setOnAction(
						evento -> criarFormDialogo(obj, "/gui/FormularioDep.fxml", Utilitario.stageAtual(evento)));

			}

		});
	}

	private void iniciaBotaoDeletar() {
		tableColumnDeletar.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tableColumnDeletar.setCellFactory(p -> new TableCell<Departamento, Departamento>() {
			private final Button b = new Button("deletar");

			@Override
			protected void updateItem(Departamento obj, boolean vazio) {
				super.updateItem(obj, vazio);

				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(b);
				b.setOnAction(evento -> removeEntidade(obj));

			}

		});
	}

	private void removeEntidade(Departamento obj) {
		Optional<ButtonType> resultado = Alerts.showConfirmation("Confirmação", "Tem certeza que quer deletar?");
		if (resultado.get() == ButtonType.OK) {

			if (service == null) {
				throw new IllegalStateException("Service esta nulo");
			}

			try {
				service.deletar(obj);
				atualizaTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@Override
	public void onMudancaDados() {
		atualizaTableView();

	}

}
