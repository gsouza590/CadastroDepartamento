package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.VendedorDao;
import model.entities.Vendedor;


public class VendedorService {

	private VendedorDao dao = DaoFactory.createVendedorDao();
	
	public List<Vendedor> findAll(){
			return dao.findAll();
	}
	
	public void salvarOuAtualizar(Vendedor obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deletar(Vendedor obj) {
		dao.deleteById(obj.getId());
	}
}
