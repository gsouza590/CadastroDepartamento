package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartamentoService {

	public List<Departamento> findAll(){
		
		List<Departamento> l= new ArrayList<Departamento>();
		l.add(new Departamento(1,"Livros"));
		l.add(new Departamento(2,"Eletronicos"));
		l.add(new Departamento(3,"Casa&Banho"));
		return l;
	}
}
