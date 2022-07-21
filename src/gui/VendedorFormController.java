package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utilitario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.services.DepartamentoService;
import model.services.VendedorService;
import models.exceptions.ValidacaoExcecao;

public class VendedorFormController implements Initializable {

	private Vendedor dep;

	private VendedorService service;

	private DepartamentoService dpservice;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpDataNasc;

	@FXML
	private TextField txtSalarioBase;

	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;

	@FXML
	private Label labelErrorNome;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorDataNasc;

	@FXML
	private Label labelErrorSalarioBase;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	private ObservableList<Departamento> obsList;

	public void setVendedor(Vendedor dep) {
		this.dep = dep;
	}

	public void setServices(VendedorService service, DepartamentoService dpservice) {
		this.service = service;
		this.dpservice = dpservice;
	}

	public void inscreveDCL(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (dep == null) {
			throw new IllegalStateException("Entidade nula");
		}
		if (service == null) {
			throw new IllegalStateException("Service esta nula");

		}
		try {
			dep = getDataFormulario();
			service.salvarOuAtualizar(dep);
			notificaDCL();
			Utilitario.stageAtual(event).close();
		} catch (ValidacaoExcecao e) {
			setMensagemErro(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notificaDCL() {
		for (DataChangeListener l : dataChangeListeners) {
			l.onMudancaDados();
		}
	}

	private Vendedor getDataFormulario() {
		ValidacaoExcecao exc = new ValidacaoExcecao("Validacao erro");

		Vendedor obj = new Vendedor();

		obj.setId(Utilitario.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exc.addErros("nome", " Campo não pode ser vazio");
		}
		obj.setNome(txtNome.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exc.addErros("email", " Campo não pode ser vazio");
		}
		obj.setEmail(txtEmail.getText());

		if (dpDataNasc.getValue() == null) {
			exc.addErros("dataNasc", "Campo não pode ser nulo");
		} else {
			Instant instant = Instant.from(dpDataNasc.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataNasc(Date.from(instant));
		}

		if (txtSalarioBase.getText() == null || txtSalarioBase.getText().trim().equals("")) {
			exc.addErros("salarioBase", "Campo não pode ser nulo");
		}
		obj.setSalarioBase(Utilitario.tryParseToDouble(txtSalarioBase.getText()));

		obj.setDepartamento(comboBoxDepartamento.getValue());
		
		if (exc.getErros().size() > 0) {
			throw exc;
		}
		return obj;

	}

	@FXML
	public void onBtCancelaAction(ActionEvent event) {
		Utilitario.stageAtual(event).close();
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		iniciaNodes();

	}

	private void iniciaNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 60);
		Constraints.setTextFieldDouble(txtSalarioBase);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utilitario.formatDatePicker(dpDataNasc, "dd/MM/yyyy");
		inicializaComboBoxDepartamento();
	}

	public void atualizaFormData() {
		if (dep == null) {
			throw new IllegalStateException("Entidade esta nula!!");
		}
		txtId.setText(String.valueOf(dep.getId()));
		txtNome.setText(dep.getNome());
		txtEmail.setText(dep.getEmail());
		Locale.setDefault(Locale.US);
		txtSalarioBase.setText(String.format("%.2f", dep.getSalarioBase()));

		if (dep.getDataNasc() != null) {
			dpDataNasc.setValue(LocalDate.ofInstant(dep.getDataNasc().toInstant(), ZoneId.systemDefault()));
		}
		if (dep.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartamento.setValue(dep.getDepartamento());
		}
	}

	public void associaObjetos() {

		if (dpservice == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Departamento> list = dpservice.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
	}

	public void setMensagemErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		if (campos.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
		}
		else {
			labelErrorNome.setText("");
		}
		
		if (campos.contains("email")) {
			labelErrorEmail.setText(erros.get("email"));
		}
		if (campos.contains("salarioBase")) {
			labelErrorSalarioBase.setText(erros.get("salarioBase"));
		}
		
	}

	private void inicializaComboBoxDepartamento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}
}
