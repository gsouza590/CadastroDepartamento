package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utilitario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Departamento;
import model.services.DepartamentoService;
import models.exceptions.ValidacaoExcecao;

public class DepartamentoFormController implements Initializable{
	
	private Departamento dep;
	
	private DepartamentoService service;
	
	private List<DataChangeListener> dataChangeListeners= new ArrayList<>();
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;
	
	@FXML
	private Label labelErrorNome;

	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setDepartamento(Departamento dep) {
		this.dep= dep;
	}
	public void setDepartamentoService(DepartamentoService service) {
		this.service= service;
	}
	
	public void inscreveDCL(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if(dep == null) {
			throw new IllegalStateException("Entidade nula");
		}
		if(service==null) {
			throw new IllegalStateException("Service esta nula");

		}
		try {
		dep = getDataFormulario();
		service.salvarOuAtualizar(dep);
		notificaDCL();
		Utilitario.stageAtual(event).close();
		}
		catch(ValidacaoExcecao e) {
			setMensagemErro(e.getErros());
		}
		catch(DbException e) {
			Alerts.showAlert("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notificaDCL() {
		for(DataChangeListener l : dataChangeListeners) {
			l.onMudancaDados();
		}
	}
	
	private Departamento getDataFormulario() {
		ValidacaoExcecao exc = new ValidacaoExcecao("Validacao erro");
		
		Departamento obj = new Departamento();
		
		obj.setId(Utilitario.tryParseToInt(txtId.getText()));
		
		if(txtNome.getText()==null || txtNome.getText().trim().equals("") ) {
			exc.addErros("nome", " Campo não pode ser vazio");
		}
		obj.setNome(txtNome.getText());
		
		if(exc.getErros().size()>0) {
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
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	
	public void atualizaFormData() {
		if(dep==null) {
			throw new IllegalStateException("Entidade esta nula!!");
		}
		txtId.setText(String.valueOf(dep.getId()));
		txtNome.setText(dep.getNome());
	}
	
	
	public void setMensagemErro(Map<String,String>erros) {
		Set<String> campos =erros.keySet();
		if(campos.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
			
		}
	}
}
