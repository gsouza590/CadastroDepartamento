package gui;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class TelaPrincipalController implements Initializable{

	@FXML
	private MenuItem menuItemVendedor;
	
	@FXML
	private MenuItem menuItemDepartamento;
	
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void onMenuItemVendedorAction() {
		carregarView("/gui/ListaVendedor.fxml", (ListaVendedorController controller)-> {
			controller.setVendedorService(new VendedorService());
			controller.atualizaTableView();
		}  );
	}
	
	@FXML
	public void onMenuItemDepartamentoAction() {
		carregarView("/gui/ListaDepartamento.fxml", (ListaDepartamentoController controller)-> {
			controller.setDepartamentoService(new DepartamentoService());
			controller.atualizaTableView();
		}  );
	}
	
	@FXML
	public void onMenuItemSobreAction() {
		carregarView("/gui/TelaSobre.fxml", x->{});
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
	}

	private synchronized <T> void carregarView(String nomeAbsoluto, Consumer<T> iniciaAcao) {
			try {
			FXMLLoader loader = new FXMLLoader (getClass().getResource(nomeAbsoluto));
			VBox novoVbox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox principalVBox= (VBox)((ScrollPane) mainScene.getRoot()).getContent();
					
			Node mainMenu = principalVBox.getChildren().get(0);
			
			principalVBox.getChildren().clear();
			
			principalVBox.getChildren().add(mainMenu);
			principalVBox.getChildren().addAll(novoVbox.getChildren());
			
			T controller= loader.getController();
			iniciaAcao.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Erro carregamento da pagina", e.getMessage(), AlertType.ERROR);
			}
			
	}
}
