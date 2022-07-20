package gui;

import java.net.URL;
import java.util.Date;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Vendedor;
import model.services.VendedorService;

public class ListaVendedorController implements Initializable, DataChangeListener {

	private VendedorService service;

	@FXML
	private TableView<Vendedor> tableViewVendedor;

	@FXML
	private TableColumn<Vendedor, Integer> tableColumnId;

	@FXML
	private TableColumn<Vendedor, String> tableColumnNome;
	
	@FXML
	private TableColumn<Vendedor, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Vendedor, Date> tableColumnDataNasc;
	
	@FXML
	private TableColumn<Vendedor, Double> tableColumnSalarioBase;
	

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEditar;

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnDeletar;

	@FXML
	private Button btNovo;

	private ObservableList<Vendedor> obsLista;

	@FXML
	public void onBTNovoAction(ActionEvent event) {
		Stage parentStage = Utilitario.stageAtual(event);
		Vendedor obj = new Vendedor();
		criarFormDialogo(obj, "/gui/FormularioDep.fxml", parentStage);
	}

	public void setVendedorService(VendedorService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

		iniciarNodes();

	}

	private void iniciarNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		tableColumnDataNasc.setCellValueFactory(new PropertyValueFactory<>("dataNasc"));
		Utilitario.formatTableColumnDate(tableColumnDataNasc, "dd/MM/yyyy");
		
		tableColumnSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utilitario.formatTableColumnDouble(tableColumnSalarioBase, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void atualizaTableView() {
		if (service == null) {
			throw new IllegalStateException("Service esta nulo!!!");
		}

		List<Vendedor> lista = service.findAll();
		obsLista = FXCollections.observableArrayList(lista);

		tableViewVendedor.setItems(obsLista);
		iniciaBotaoEdicao();
		iniciaBotaoDeletar();
	}

	private void criarFormDialogo(Vendedor obj, String nomeAbsoluto, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
//			Pane pane = loader.load();
//
//			VendedorFormController controlador = loader.getController();
//			controlador.setVendedor(obj);
//			controlador.setVendedorService(new VendedorService());
//			controlador.inscreveDCL(this);
//			controlador.atualizaFormData();
//
//			Stage dialogoStage = new Stage();
//			dialogoStage.setTitle("Nome do Vendedor");
//			dialogoStage.setScene(new Scene(pane));
//			dialogoStage.setResizable(false);
//			dialogoStage.initOwner(parentStage);
//			dialogoStage.initModality(Modality.WINDOW_MODAL);
//			dialogoStage.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IOException", "Erro carregar View", e.getMessage(), AlertType.ERROR);
//		}
	}

	private void iniciaBotaoEdicao() {
		tableColumnEditar.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tableColumnEditar.setCellFactory(p -> new TableCell<Vendedor, Vendedor>() {
			private final Button b = new Button("editar");

			@Override
			protected void updateItem(Vendedor obj, boolean vazio) {
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
		tableColumnDeletar.setCellFactory(p -> new TableCell<Vendedor, Vendedor>() {
			private final Button b = new Button("deletar");

			@Override
			protected void updateItem(Vendedor obj, boolean vazio) {
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

	private void removeEntidade(Vendedor obj) {
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
